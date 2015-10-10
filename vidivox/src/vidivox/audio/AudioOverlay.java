package vidivox.audio;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import vidivox.exception.FileFormatException;
import vidivox.ui.displaypanel.AudioDisplayPanel;
import vidivox.worker.AudioPlayWorker;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;

/**
 * This abstract class is instantiated whenever the user wants to add a comment in the AudioOverlaysDialog
 * dialog and contains the container to enter the text, test the audio, enter a start duration,
 * and select the volume to play the audio at
 * 
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 *
 */
public abstract class AudioOverlay {
	
	/**
	 * Fields initialized to be used by other packages and methods in the project
	 */
    protected AudioDisplayPanel displayPanel;
    protected double startTime = 0;						// Setting the default start time of the audio to 0
    protected int volume = 100;							// Setting the default volume to 100
    protected CommentaryOverlay commentary;				// Reference to the CommentaryOverlay object
    protected JTextField startTimeMinutesField;			// Field where the start minute of the audio is specified
    protected JTextField startTimeSecondsField;			// Field where the start seconds of the audio is specified
    protected JTextField startTimeMillisecondsField;	// Field where the start milliseconds of the audio is specified
    protected JLabel durationLabel;						// Label which informs user of duration of audio
    protected JButton playButton;						// Button to play the voice without any video
    protected JSlider volumeSlider;						// Slider to allow user to increase/decrease volume
    protected JLabel volumeLevelLabel;					// Label informing user of current volume level
    protected JCheckBox previewCheckBox;				// Check box allowing user to select audio to play with video
    protected boolean showPreview = true;				// Field to determine whether audio should be played with video
    
    /**
     * Fields initialized to be used by methods within this class
     */
    private ActionListener playActionListener;			// Listener for the play button in this overlay	
    private ActionListener stopActionListener;			// Listener for the play button in this overlay
    private AudioPlayWorker audioPlayWorker;			// Reference to the AudioPlayWorker object which plays audio
    protected float duration;

    /**
     * Method which sets up the layout of the components in this class
     * through the use of the grid bag layout and returns the formatted
     * JPanel contentPane
     * 
     * @return 
     */
    public JPanel getComponentView() {
    	// Initializing the contentPane JPanel to be formatted and returned
        JPanel contentPane = new JPanel();
        
        //Adding a grid bag layout to the contentPane 
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Creating the propertiesPanel which will contain the options to control the commentary
        JPanel propertiesPanel = new JPanel();
        // Setting up the layout of this panel
        propertiesPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0f;
        
        // Adding the label informing the user on where to enter the time the audio should start
        propertiesPanel.add(new JLabel("Start Time"),gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        
        // Instantiating the text field where the user will enter the minute at which audio should start
        // and formatting and adding it to the properties panel
        startTimeMinutesField = new JTextField();
        startTimeMinutesField.setHorizontalAlignment(JTextField.RIGHT);
        startTimeMinutesField.setPreferredSize(new Dimension(50,19));
        gbc.gridx++;
        propertiesPanel.add(startTimeMinutesField,gbc);
        
        // Adding label to increase visual understanding of the GUI
        gbc.gridx++;
        propertiesPanel.add(new JLabel(":"),gbc);
        
        // Instantiating the text field where the user will enter the second at which audio should start
        // and formatting and adding it to the properties panel 
        startTimeSecondsField = new JTextField();
        startTimeSecondsField.setHorizontalAlignment(JTextField.RIGHT);
        startTimeSecondsField.setPreferredSize(new Dimension(25,19));
        gbc.gridx++;
        propertiesPanel.add(startTimeSecondsField,gbc);
        
        // Adding label to increase visual understanding of the GUI
        gbc.gridx++;
        propertiesPanel.add(new JLabel("."),gbc);
        
        // Instantiating the text field where the user will enter the millisecond at which audio should start
        // and formatting and adding it to the properties panel 
        startTimeMillisecondsField = new JTextField();
        startTimeMillisecondsField.setHorizontalAlignment(JTextField.RIGHT);
        startTimeMillisecondsField.setPreferredSize(new Dimension(35,19));
        gbc.gridx++;
        propertiesPanel.add(startTimeMillisecondsField,gbc);
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Adding the label informing user of the meaning of the numbers which specify the duration of the audio
        gbc.gridx++;
        gbc.weightx=0;
        propertiesPanel.add(new JLabel("Duration"),gbc);
        
        // Instantiating label which will inform the user of how long the commentary is when converted into audio
        // and adding it to the properties panel
        durationLabel = new JLabel("00:00.000");
        durationLabel.setPreferredSize(new Dimension(100,24));
        gbc.gridx++;
        gbc.weightx=0;
        propertiesPanel.add(durationLabel,gbc);
        
        // Instantiating button to allow user to play the commentary as audio and
        // adding it to the properties panel
        playButton=new JButton("Play");
        // Disabling the user from playing audio before there is any audio
        playButton.setEnabled(false);
        gbc.gridx++;
        propertiesPanel.add(playButton,gbc);
        
        // Adding space to increase visual appeal of the GUI
        gbc.gridx++;
        gbc.weightx=1.0f;
        
        // Instantiating check box user can click on to allow audio to be played when
        // the video plays and adds it to the properties panel
        previewCheckBox = new JCheckBox("Preview");
        previewCheckBox.setSelected(true);
        gbc.gridx++;
        gbc.weightx=1.0f;
        propertiesPanel.add(previewCheckBox, gbc);
        
        // Adding panel to create space between GUI elements
        propertiesPanel.add(new JPanel(),gbc);
        
        // Adding volume label to inform the purpose of the slider to the user
        gbc.gridx++;
        gbc.weightx = 0;
        propertiesPanel.add(new JLabel("Volume"),gbc);
        
        // Creating slider to allow user to increase or decrease volume of commentary audio
        // and adding it to the properties panel
        volumeSlider = new JSlider();
        volumeSlider.setPreferredSize(new Dimension(100, 25));
        volumeSlider.setMinimum(0);
        volumeSlider.setMaximum(100);
        volumeSlider.setValue(100);
        gbc.gridx++;
        propertiesPanel.add(volumeSlider,gbc);
        
        // Instantiating label to inform user of current volume level and adding it
        // to the properties panel
        volumeLevelLabel = new JLabel("100%");
        volumeLevelLabel.setPreferredSize(new Dimension(50,24));
        gbc.gridx++;
        propertiesPanel.add(volumeLevelLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth=10;
        gbc.weightx = 1.0f;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Adding the properties panel to the content pane panel
        contentPane.add(propertiesPanel, gbc);
        
        // Setting up listener for the start time the user wants the audio to be played
        final DocumentListener startTimeDocumentListener = new DocumentListener() {
        		
        	// Method called whenever text is removed
        	@Override
        	public void removeUpdate(DocumentEvent e) {
        		changedUpdate(e);
        	}
        		
        	// Method called whenever text is entered
        	@Override
        	public void insertUpdate(DocumentEvent e) {
        		changedUpdate(e);
        	}
        	
        	// Method called when a user makes a change to the text field this listener
        	// is added to
        	@Override
        	public void changedUpdate(DocumentEvent e) {
        		// Initializing time fields
        		int minutes = 0;
        		int	seconds = 0 ;
        		int	milliseconds = 0;
        		
        		// Getting the value of the minutes the user entered
        		try{
        			minutes = Integer.parseInt(startTimeMinutesField.getText());
        		} catch (NumberFormatException|NullPointerException ex) {}
        		
        		// Getting the value of the seconds the user entered
        		try{
        			seconds = Integer.parseInt(startTimeSecondsField.getText());
        		} catch (NumberFormatException|NullPointerException ex) {}
        	
        		// Getting the milliseconds the user entered
        		try{
        			milliseconds = Integer.parseInt(startTimeMillisecondsField.getText());
        		} catch (NumberFormatException|NullPointerException ex) {}
        		
        		// Specifying the start time
        		startTime = minutes*60 + seconds + ((float)milliseconds)/1000;

                updateDisplayPanel();
        	}
        };
        	
        // Setting up listener to update the time fields whenever any of the fields gain
        // or lose attention through keyboard or mouse
        final FocusListener startTimeFocusListener = new FocusListener() {
        	
        	// Method called when focus lost
        	@Override
        	public void focusLost(FocusEvent e) {
        		updateStartTimeFields();
        	}

        	@Override
        	public void focusGained(FocusEvent e) {}
        };
        
        // Adding the focus listener to the minutes, seconds and milliseconds fields
        startTimeMinutesField.addFocusListener(startTimeFocusListener);
        startTimeSecondsField.addFocusListener(startTimeFocusListener);
        startTimeMillisecondsField.addFocusListener(startTimeFocusListener);

        // Adding the document listener to the minutes, seconds and milliseconds fields
        startTimeMinutesField.getDocument().addDocumentListener(startTimeDocumentListener);
        startTimeSecondsField.getDocument().addDocumentListener(startTimeDocumentListener);
        startTimeMillisecondsField.getDocument().addDocumentListener(startTimeDocumentListener);


        // Setting up the listener for the volume slider to determine whether changes have 
        // been made to it and accordingly setting the volume label
        volumeSlider.addChangeListener(new ChangeListener() {
            // Method called whenever slider is moved
        	@Override
            public void stateChanged(ChangeEvent e) {
        		// Getting the value of the slider and setting the volume label according
        		// to the slider position
                JSlider slider = (JSlider) e.getSource();
                volume = slider.getValue();
                volumeLevelLabel.setText(volume + "%");
            }
        });
        
        // Adding item listener to the check box to determine when the box is ticked or not ticked
        previewCheckBox.addItemListener(new ItemListener () {
        	// Method called whenever user clicks on the check box
        	@Override
        	public void itemStateChanged(ItemEvent e) {
        		
        		// Determining whether the check box is ticked or not ticked
        		if (e.getStateChange() == ItemEvent.SELECTED) {
        			// If the box is ticked
        			showPreview = true;
        		} else {
        			// If the box isn't ticked
        			showPreview = false;
        		}
        	}
        });

        // Setting up listener for the play button
        playActionListener = new ActionListener() {
        	
        	// Method called whenever button is clicked
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
            	// Modifying the text "Play" to "Stop"
                playButton.setText("Stop");
                // Removing the action listener for the play functionality
                playButton.removeActionListener(this);
                // Adding the action listener for the stop functionality
                playButton.addActionListener(stopActionListener);
                               
                // Starting the process for playing the audio
                audioPlayWorker = new AudioPlayWorker(getFilePath(),volume){
                    
                	// Overriding done() function to set the text from "Stop" to "Play"
                	// also setting the listener for the play functionality
                	@Override
                    protected void done() {
                        super.done();
                        // Turning the button back into a play button when it finishes playing
                        playButton.setText("Play");
                        playButton.removeActionListener(stopActionListener);
                        playButton.addActionListener(playActionListener);
                    }
                };
                // Executing the worker to play the audio
                audioPlayWorker.execute();
            }
               
        };
        
        // Creating the action listener for when the audio needs to stop playing
        stopActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // Killing the process for playing the audio
                audioPlayWorker.kill();
            }
        };
        
        // Adding the listener to the play button enabling its play functionality
        playButton.addActionListener(playActionListener);

        // Setting initial start values of the time fields
        updateStartTimeFields();
        
        // Setting the initial volume
        volumeSlider.setValue(volume);
        
        // If there is a file already selected then get and display its duration
        if (getFilePath() != null) {
        	
        	// Getting the duration of the file and setting the label to show the duration
            try {
                duration = getDuration(getFilePath());

                int minutes = (int)(duration / 60);
                int seconds = (int)(duration % 60);
                int milliseconds = (int)((duration - seconds - minutes*60) * 1000);
                String durationText=String.format("%02d", minutes)+":"+String.format("%02d", seconds)+":"+String.format("%03d", milliseconds);
                
                // Setting the label to show the duration of the audio
                durationLabel.setText(durationText);
            } catch (InterruptedException | IOException e) {
                durationLabel.setText("????");
            }
        }

        return contentPane;
    }

    /**
     * Creates an AudioOverlay object from string arguments
     * @param s the string input
     * @return the created AudioOverlay object
     * @throws FileFormatException if the string is formatted incorrectly
     */
    public static AudioOverlay fromString(String s) throws FileFormatException {
        try {
            if (s.startsWith("F")) {
                return FileOverlay.fromString(s);
            } else if (s.startsWith("C")) {
                return CommentaryOverlay.fromString(s);
            }
        } catch(ArrayIndexOutOfBoundsException|NumberFormatException e){
            throw new FileFormatException();
        }
        throw new FileFormatException();
    }
       
    /**
     * This method returns the file path of the file with both the silent audio and actual audio
     * merged in order to offset the audio by the time the user specified
     * 
     * @return outFilePath 
     * @throws IOException
     * @throws InterruptedException
     */
    public String getProcessedFilePath() throws IOException, InterruptedException {
        // Make sure this method is called on a SwingWorker thread so that it doesn't freeze the UI (for debug)
    	System.out.println(Thread.currentThread().getName());
        if (!Thread.currentThread().getName().startsWith("SwingWorker")) {
            throw new RuntimeException("This method must be called on a swingworker thread!");
        }
        
        // Check that a file is actually selected before proceeding
        if(getFilePath()==null || getFilePath().isEmpty()){
        	return null;
        }
        
        // If the start time is 0 then no need to create an offset so returning the file path of the non
        // processed file
        if (startTime == 0) {
            return getFilePath();
        }
        
        // Creating a silent audio file equal to the length of the start time offset
        // (reference: http://superuser.com/questions/579008/add-1-second-of-silence-to-audio-through-ffmpeg)
        String silenceFilePath = "/tmp/silence" + startTime + ".mp3";
        
        // Building process to create the silent audio file
        String cmd = "ffmpeg -y -f lavfi -i aevalsrc=0:0:0:0:0:0::duration=" + startTime + " " + silenceFilePath;
        Process process=new ProcessBuilder("/bin/bash", "-c", cmd).start();
        process.waitFor();

        // Concatenating the silent file before the actual file and adjusting the volume
        //(reference: http://superuser.com/questions/587511/concatenate-multiple-wav-files-using-single-command-without-extra-file)
        String outFilePath = "/tmp/" + getFileName() + ".mp3";
        String concatCommand = "ffmpeg -y -i " + silenceFilePath + " -i \"" + getFilePath()
        						+ "\" -filter_complex '[0:0][1:0]concat=n=2:v=0:a=1[combined];[combined]volume=" + 
        						(((float)volume)/100) + "[out]' -map '[out]' \"" + outFilePath+"\"";
        
        // Starting process to concatenate the silent and audio files
        process = new ProcessBuilder("/bin/bash", "-c", concatCommand).start();
        process.waitFor();
        return outFilePath;
    }

    protected void updateDisplayPanel(){
        if(displayPanel!=null) {
            displayPanel.invalidate();
        }
    }

    /**
     * Abstract method defined so that sub classes must implement method to get the file path
     */
    abstract public String getFilePath();

    /**
     * Getter method for the file name
     * @return returns file path
     */
    public String getFileName() {
        return getFilePath().split("/")[getFilePath().split("/").length-1];
    }
    
    /**
     * This method is used to get the duration of a comment when it is converted to audio
     * through the use ffprobe.
     * It is not to be confused with getDuration(File file) as that method takes in a file
     * and calls this method
     * 
     * @param filePath - path to file
     * @return returns duration of audio
     * @throws IOException
     * @throws InterruptedException
     */
    protected float getDuration(String filePath) throws IOException, InterruptedException {

    	// Creating and starting process to obtain duration of file
    	Process ffProbeProcess = new ProcessBuilder("/bin/bash", "-c", "ffprobe -i \"" + filePath +
    					"\" -show_entries format=duration 2>&1 | grep \"duration=\"").start();
    	
    	// Creating buffered reader to read in the output from the bash process
    	BufferedReader reader = new BufferedReader(new InputStreamReader(ffProbeProcess.getInputStream()));
    	ffProbeProcess.waitFor();
    	String durationLine = reader.readLine();
    	
    	// Formatting the duration string so that it is returned correctly
    	if (durationLine == null) {
    		return 0;
    	} else {
    		return Float.parseFloat(durationLine.split("=")[1]);
    	}
    }

    /**
     * This method takes in a file and calls the getDuration() method which takes in the 
     * string of the file path and gets the actual duration of that audio file
     * 
     * @param file - input of which the path is to be found
     * @return	returns the file path
     * @throws IOException
     * @throws InterruptedException
     */
    protected float getDuration(File file) throws IOException, InterruptedException {
    	// Calling getDuration() method defined above
        return getDuration(file.getAbsolutePath());
    }
    
    /**
     * This method updates the start time fields which the user specifies in an
     * appropriate format
     */
    public void updateStartTimeFields() {
    	
    	int minutes = (int)(startTime / 60);
    	int seconds = (int)(startTime % 60);
    	int milliseconds = (int)((startTime - seconds - minutes*60) * 1000);
    	startTimeMinutesField.setText(String.format("%02d", minutes));
    	startTimeSecondsField.setText(String.format("%02d", seconds));
    	startTimeMillisecondsField.setText(String.format("%03d", milliseconds));
    }
    
    /**
     * This method serves as a getter for the boolean showPreview variable
     * @return returns showPreview
     */
    public boolean isShowingPreview() {
    	return showPreview;
    }
    
    /**
     * This method serves as a getter for the volume variable
     * @return an integer specifying the volume
     */
    public int getVolume() {
        return volume;
    }

    /**
     * This method serves as a getter for the start time
     * @return a double specifying the start time
     */
    public double getStartTime() {
        return startTime;
    }

    public float getDuration() {
        return duration;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }
}
