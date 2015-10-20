package vidivox.ui;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import vidivox.UpdateRunnable;
import vidivox.audio.AudioOverlay;
import vidivox.exception.FileFormatException;
import vidivox.ui.dialog.AudioOverlaysDialog;
import vidivox.ui.dialog.ProgressDialog;
import vidivox.ui.timeline.AudioTimelineDisplay;
import vidivox.ui.timeline.AudioTimelinesPanel;
import vidivox.video.AsciiVideoPlayer;
import vidivox.video.DirectVideoPlayer;
import vidivox.video.EmbeddedVideoPlayer;
import vidivox.video.VideoPlayerComponent;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This is the main frame of the vidivox application
 * Contains the components to allow for: viewing a video
 * 										 opening a video
 * 										 opening a project
 * 										 saving a project
 * 										 opening the commentary frame
 * 										 quitting the application
 * 
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 *
 */

public class MainFrame extends JFrame{
	
	/**
	 * Fields instantiated globally to be used by all methods in this class
	 */
    private static MainFrame instance;          // Singleton instance of the MainFrame

	private VideoPlayerComponent videoPlayer;	// Video player object
    private ControlsPanel controlsPanel;		// Reference to ControlsPanel class
    private AudioTimelinesPanel audioTimelinesPanel;
    private String videoPath;					// String containing the path to the video
    private JMenuBar menuBar;					// Menu bar at the top of the application
    private JMenuItem openVideoButton;			// Menu bar option for opening a video
    private JMenuItem openProjectButton;		// Menu bar option for opening a project
    private JMenuItem saveProjectButton;		// Menu bar option for saving a project
    private JMenuItem exportButton;				// Menu bar option for exporting the project containing video + commentary
    private JMenuItem quitButton;				// Menu bar option for closing the application
    private JMenuItem commentaryButton;			// Menu bar option for adding commentary
    private JMenuItem standardOutputButton;
    private JMenuItem canvasOutputButton;
    private JMenuItem asciiOutputButton;

    /**
     * Gets the singleton instance of this class
     * @return the MainFrame
     */
    public static MainFrame getInstance(){
        if(instance==null){
            instance=new MainFrame();
        }
        return instance;
    }

    /**
     * Constructor used to call methods to set up the layout and listeners for 
     * the menu bar options when this object is instantiated
     */
    private MainFrame() {
    	
    	// Naming the application VIDIVOX at the top of the frame
        super("VIDIVOX");
        // Setting the default close operation
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Setting up the menu bar
        setupMenuBar();
        // Setting up the layout of the application
        setupLayout(); 
        // Setting up the listeners called when a mouse is clicked on specific components
        setupListeners();
        // Setting up the minimum size to disallow distortion of the frame
        setMinimumSize(new Dimension(750,400));
        pack();

        // Instantiating a UpdateRunnable class to update specific components every 500 milliseconds
        // Source: https://github.com/caprica/vlcj/blob/master/src/test/java/uk/co/caprica/vlcj/test/basic/PlayerControlsPanel.java
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new UpdateRunnable(controlsPanel, audioTimelinesPanel), 0L, 500L, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Getter for the instance of controlsPanel of type ControlsPanel
     * 
     * @return controlsPanel - returns type ControlsPanel
     */
	public ControlsPanel getControlsPanel() {
		return controlsPanel;
	}

	/**
	 * Setter for the instance of controlsPanel of type ControlsPanel
	 * 
	 * @param controlsPanel
	 */
	public void setControlsPanel(ControlsPanel controlsPanel) {
		this.controlsPanel = controlsPanel;
	}

    /**
     * Method called when main frame is instantiated to set up the action listeners
     */
    private void setupListeners() {
        openVideoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // File chooser to allow user to select a video to open
                JFileChooser fileChooser = new JFileChooser();

                // Allowing user to only select mp4 and avi video files to open
                fileChooser.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter mp4Filter = new FileNameExtensionFilter("mp4 videos", "mp4");
                FileNameExtensionFilter aviFilter = new FileNameExtensionFilter("avi videos", "avi");
                fileChooser.addChoosableFileFilter(mp4Filter);
                fileChooser.addChoosableFileFilter(aviFilter);

                // Showing the dialog to allow a user to select a video to open and getting the return value
                int returnValue = fileChooser.showOpenDialog(null);

                // Checking if the return value is equal to the approve option
                if (returnValue == JFileChooser.APPROVE_OPTION) {

                    // Getting the file chosen and the location of where it is
                    File selectedFile = fileChooser.getSelectedFile();
                    videoPath = selectedFile.getPath();

                    // Playing the video
                    videoPlayer.playVideo(videoPath);

                    int totalTime = 0;
                    try {
                        Process ffProbeProcess = new ProcessBuilder("/bin/bash", "-c", "ffprobe -i \"" + videoPath + "\" -show_entries format=duration 2>&1 | grep \"duration=\"").start();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(ffProbeProcess.getInputStream()));
                        ffProbeProcess.waitFor();
                        String durationLine = reader.readLine();
                        if (durationLine != null) {
                            totalTime = (int) Float.parseFloat(durationLine.split("=")[1]) * 1000;
                        }
                    } catch (InterruptedException | IOException e1) {
                        e1.printStackTrace();
                        System.err.println("Failed to get video duration");
                    }
                    getControlsPanel().setTotalTime(totalTime);
                    audioTimelinesPanel.setVideoLength(((double) totalTime) / 1000);
                }
            }
        });

        openProjectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                // Creating a JFileChooser to allow the user to choose a project to open
                JFileChooser fileChooser = new JFileChooser();

                // Opening a dialog to allow the user to choose a project to open up
                if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        // Adding warning messages to inform user whether changes to current project (if any) are to be deleted
                        String[] options = new String[]{"Yes, Discard changes", "No"};
                        String message = "Opening another project will cause usaved changes to be discarded\n" +
                                "Are you sure you want to do this?";

                        // Storing result of the user's choice on whether to discard changes and open new project or not
                        int result = JOptionPane.showOptionDialog
                                (MainFrame.this, message, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

                        // If the user does want to open a new project
                        if (result == JOptionPane.YES_OPTION) {
                            // Opening new project and getting the selected file
                            openProject(fileChooser.getSelectedFile());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (FileFormatException e) {
                        // Informing user that an invalid project was chosen
                        JOptionPane.showMessageDialog(MainFrame.this, "Invalid or corrupted VIDIVOX project file", "Invalid File", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        saveProjectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                // Allowing user to specify where to save the project
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    // Allows the user to save the project by clicking on an existing file
                    // or by typing it in and passing the file to the saveProject() method
                    try {
                        saveProject(fileChooser.getSelectedFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // Allowing user to select destination of exported file
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        exportProject(fileChooser.getSelectedFile());
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Cleaning up by disposing of the main frame and then exiting the application
                dispose();
                System.exit(0);
            }
        });

        // Adding listener to the add commentary button
        commentaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // Creating an AudioOverlaysDialog that contains the options for adding commentary
                // to the video
                AudioOverlaysDialog.getInstance().setVisible(true);
            }
        });

        standardOutputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                videoPlayer.setVideoPlayer(videoPlayer.getEmbeddedPlayer());
            }
        });

        canvasOutputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    videoPlayer.setVideoPlayer(new DirectVideoPlayer(videoPlayer.getWidth(),videoPlayer.getHeight()));
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        asciiOutputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                videoPlayer.setVideoPlayer(new AsciiVideoPlayer());
            }
        });
        // Stop all audio previews when closing the main window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                for (AudioOverlay overlay : AudioOverlaysDialog.getOverlays()) {
                    overlay.stopPreviewAudio();
                    overlay.stopAudioPlayer();
                }
            }
        });
    }

    /**
     * Sets up the frame layout
     */

    private void setupLayout(){
    	   	
    	// Getting the content pane to add components to it
        Container contentPane = getContentPane();
        // Setting the layout to that of GridBagLayout
        contentPane.setLayout(new GridBagLayout());
        // Adding constraints to the grid bag layout to shape the panels in a neat format
        GridBagConstraints gbc = new GridBagConstraints();

        // Setting up the video player frame by adding a video player component to it
        videoPlayer=new VideoPlayerComponent(new EmbeddedVideoPlayer());
    	// Setting up size of the player
        videoPlayer.setPreferredSize(new Dimension(600, 480));
        // Formatting the grid bag layout for the video player and adding it to the top of the frame
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0f;
        gbc.weighty = 1.0f;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.BOTH;       
        // Adding the video player to the content pane
        contentPane.add(videoPlayer, gbc);

        // Setting up the controls panel which will contain all the buttons for playing the video, pausing etc
        setControlsPanel(new ControlsPanel(videoPlayer));
        // Formatting the grid bag layout for the content pane and adding it to the bottom of the frame
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.weightx=1.0f;
        gbc.weighty=0.0f;
        gbc.anchor= GridBagConstraints.SOUTH;
        gbc.fill= GridBagConstraints.HORIZONTAL;
        // Adding the controls panel to the content pane
        contentPane.add(getControlsPanel(), gbc);

        // Setting up the display which will show all the audio tracks
        audioTimelinesPanel = AudioTimelinesPanel.getInstance();
        gbc.fill= GridBagConstraints.BOTH;
        gbc.gridy++;
        gbc.weighty=0;
        contentPane.add(audioTimelinesPanel,gbc);

    }

    /**
     * Method called when the main frame is instantiated to set up the menu bar 
     * which contains options for opening a video, saving a project, adding commentary etc
     */
    private void setupMenuBar(){
        // Set the JMenuBar to use heavyweight so that it shows over the videoplayer
    	JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        menuBar = new JMenuBar();
        // Setting up the menu options for the menu bar item "File"
        JMenu fileMenu = new JMenu("File");
        // Adding a keyboard shortcut for opening the file menu by pressing "Alt + F"
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        // Menu item choice to choose a video and play in the video player component
        openVideoButton = new JMenuItem("Open Video");
        // Adding the open video button to the file menu
        fileMenu.add(openVideoButton);
        
        // Creating a button to allow the user to open a saved project
        openProjectButton=new JMenuItem("Open Project");
        // Adding the open project button to the file menu
        fileMenu.add(openProjectButton);
        
        // Button to allow projects to be saved so that the user can come back and reopen it
        saveProjectButton = new JMenuItem("Save Project");
        fileMenu.add(saveProjectButton);
        // Separator to increase neatness of GUI
        fileMenu.addSeparator();
        
        // Export button to allow user to merge video and audio generated from comments into
        // a single video file which can then be played and will contain both video and audio
        exportButton = new JMenuItem("Export");
        fileMenu.add(exportButton);
        fileMenu.addSeparator();
        
        // Adding a quit button to allow the user to quit the application cleanly
        quitButton = new JMenuItem("Quit");
        fileMenu.add(quitButton);
        
        // Adding the fileMenu to the menu bar
        menuBar.add(fileMenu);
        
        // Adding another tab "Audio" to allow user to select the option of adding audio
        // through the use of comments by opening the designated frame for those options
        JMenu audioMenu = new JMenu("Audio");
        // Setting shortcut key to "Alt + E"
        audioMenu.setMnemonic(KeyEvent.VK_E);
        
        // Creating button to allow user to bring up the commentary option frame
        commentaryButton = new JMenuItem("Audio Overlays...");
        audioMenu.add(commentaryButton);
        menuBar.add(audioMenu);

        JMenu outputMenu=new JMenu("Output");

        standardOutputButton=new JMenuItem("Standard");
        outputMenu.add(standardOutputButton);

        canvasOutputButton=new JMenuItem("Fancy");
        outputMenu.add(canvasOutputButton);

        asciiOutputButton=new JMenuItem("ASCII");
        outputMenu.add(asciiOutputButton);

        menuBar.add(outputMenu);

        setJMenuBar(menuBar);
    }

    public void updateAudioDisplays(){
        audioTimelinesPanel.updateComponents();
    }

    /**
     * This method attempts to save the project by specifying a file to write to
     * and then writing to it the video path as well as all the comments that
     * were being edited in that project
     * 
     * @param file - File specified by the user to store the project information in
     * @throws IOException - Throws an exception if there was an error when 
     * 						 dealing with the input/output
     */
    private void saveProject(File file) throws IOException {
        
    	// Creating file to write to
    	BufferedWriter saveFile = new BufferedWriter(new FileWriter(file));
        
    	// Allowing option to save the project with no video playing
    	// in other words, only audio being saved in the project
    	if (videoPath == null) {
    		// Writing new line so that it complies with the predetermined file structure
    		// and does not crash or give any undesired exceptions
    		saveFile.write("\n");
        } else {
        	// If there is a video, then saving the location of that video in the file
        	saveFile.write(videoPath + "\n");
        }
    	
    	// Looking through all the comments being edited in the AudioOverlayDialog frame and storing
    	// them into the file after the video path in order to bring them up in the future
        for(AudioOverlay overlay : AudioOverlaysDialog.getOverlays()) {
        	saveFile.write(overlay.toString()+"\n");
        }
        
        // Closing the file in order to prevent errors
        saveFile.close();
    }

    /**
     * This method is called when the user wants to open an existing project
     * It does so by opening the file where the video file path is saved, reading the
     * file path and the comments saved in the file and opening them into the application
     * 
     * @param file - File specified by the user on project to open up
     * @throws IOException
     * @throws FileFormatException
     */
    private void openProject(File file) throws IOException, FileFormatException {
    	
    	// Creating file to read from
        BufferedReader fileToOpen = new BufferedReader(new FileReader(file));
        String line = fileToOpen.readLine();
        String videoPath = null;
        
        // For when there was no video specified and only comments were saved
        if (!line.equals("")) {
            videoPath = line;
        }
        
        // Adding the comments from the file into a new array list to store for further editing
        List<AudioOverlay> overlays = new ArrayList<>();
        // Adding lines from the file where each line specifies a certain comment
        while((line = fileToOpen.readLine()) != null){
            overlays.add(AudioOverlay.fromString(line));
        }
        
        // Setting the global variable for video path after all other variables have been assigned to
        // prevent errors from occurring
        this.videoPath = videoPath;
        
        // Checking if there was a video saved or not, and if there was then this causes the video to
        // play immediately after opening the project
        if(videoPath!=null){
            videoPlayer.playVideo(videoPath);
        }
        
        // Setting the commentary overlays in the class AudioOverlaysDialog which handles them
        AudioOverlaysDialog.setOverlays(overlays);

    }

    /**
     * Merges the video and audio tracks into one file using ffmpeg by overlaying them
     * 
     * @param file the file to output
     * @throws InterruptedException
     * @throws IOException
     */
    private void exportProject(final File file) throws InterruptedException, IOException {
       
    	// Creating progress dialog to show the user the progress of the export as it takes
    	// some time
    	final ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.setVisible(true);
        
        // Creating a list of the audio overlays generated from the comments
        final List<AudioOverlay> overlays = AudioOverlaysDialog.getOverlays();
        
        // Setting the maximum length of the progress bar to the number of comments + 1
        // so that after each comment is processed the progress bar can be incremented
        progressDialog.setOverallTotal(overlays.size()+1);

        // Creating SwingWorker class to allow for multi-threading
        new SwingWorker<Void,AudioOverlay>() {
            
        	// Initial progress
        	int progress = 0;

        	/**
        	 * The doInBackground method handles time consuming tasks in the background
        	 * so as to not make the GUI freeze
        	 * This method is used to merge the audio and video files together into one video file
        	 * where the audio specified by the user overlaps the existing audio
        	 */
        	@Override
        	protected Void doInBackground() throws Exception {
        		
        		// Processing all of the audio files
        		List<String> audioPaths = new ArrayList<>();
        		for (AudioOverlay overlay : overlays) {
        			// Calling the process method to interact with the GUI
        			publish(overlay);
        			// Adding the file path of the audio files to the arraylist containing 
        			// the paths of all audio files
        			audioPaths.add(overlay.getProcessedFilePath());
        		}

        		// Combining all the audio files with the video files using an ffmpeg process
        		publish((AudioOverlay) null);
        		
        		// Building the command to pass into the process builder
        		StringBuilder cmd = new StringBuilder("ffmpeg -y -i \"" + videoPath+"\"");
        		// Appending the audio path of each audio file to the command
        		// Keep count of the number of audio files added
        		int audioTracksAdded=0;
        		for (String audioPath : audioPaths) {
        			// Only add audio files which aren't empty
        			if(audioPath!=null && !audioPath.isEmpty()){
        				cmd.append(" -i \"" + audioPath+"\"");
        				audioTracksAdded++;
        			}
        		}
        		
        		// If audio tracks have been added then append the command to mix them
        		if(audioTracksAdded>0){
        			cmd.append(" -filter_complex amix="+(audioTracksAdded+1));
        		}
        		
        		// Force output format to avi
        		cmd.append(" -f avi");

                // End the output when the video ends
                cmd.append(" -t "+((double)controlsPanel.getTotalTime())/1000);
        		
        		// Append the option to allow ffmpeg to use support more formats, then append the output file path
        		cmd.append(" -strict -2 \"" + file.getAbsolutePath()+"\"");
        		
        		// Building process and process builder to run the command then starting it
        		Process process = new ProcessBuilder("/bin/bash", "-c", cmd.toString()).start();

                //print the command that was executed (for debug)
        		System.out.println(cmd.toString());
        		
        		// Causes the current thread to wait if necessary
        		process.waitFor();
        		
        		// Closing the progress dialog indicating how long merging video and audio will take
        		progressDialog.close();
        		return null;
        	}
        	
        	/**
        	 * Process method called when the GUI needs to be updated at stages of the doInBackground
        	 * method
        	 * This method is called by the publish() method
        	 */
            @Override
            protected void process(List<AudioOverlay> chunks) {
            	
            	// Checking if the size of the list is 0 and returning if so
                if (chunks.size() == 0) {
                    return;
                }
                
                // Adding the size of the audio list to the progress variable in order
                // to inform user how much time is left
                progress += chunks.size();
                progressDialog.setOverallProgress(progress-1);
                
                // Informing the user as to which audio overlay is being processed at a given time
                AudioOverlay currentlyProcessedOverlay = chunks.get(chunks.size()-1);
                if (currentlyProcessedOverlay != null) {
                    // If there is an audio file being processed at a certain time then informing the user
                	// as to which file is being processed
                	progressDialog.setTaskProgressNote("Processing " + currentlyProcessedOverlay.getFileName() + "...");
                } else {
                	// If there is no audio being processed then this message shows and informs the user that
                	// they are in the final stages of exporting where the video and audio is being merged
                    progressDialog.setTaskProgressNote("Merging video with audio tracks...");
                }
            }

            /**
             * done() method which informs the user when the export is completed successfully
             */
            @Override
            protected void done() {
            	// Dialog popup to inform user of successful export
                JOptionPane.showMessageDialog(MainFrame.this,"Sucessfully exported project to "
                					+ file.getAbsolutePath(), "Success!", JOptionPane.INFORMATION_MESSAGE);
            }
        }.execute();
    }

    public VideoPlayerComponent getVideoPlayer() {
        return videoPlayer;
    }
}
