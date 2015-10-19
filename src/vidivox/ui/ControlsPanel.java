package vidivox.ui;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vidivox.audio.AudioOverlay;
import vidivox.ui.dialog.AudioOverlaysDialog;
import vidivox.worker.AudioPlayWorker;
import vidivox.worker.SkipVideoWorker;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class contains the components which allow the user to perform the basic tasks
 * associated with video players such as play, pause, stop, skip forward/back, fast forward,
 * rewind and adjust the volume. The user can also drag the slider defined in this class
 * which shows the progress of the video to a specific time. 
 * 
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 *
 */
public class ControlsPanel extends JPanel {
	
	/**
	 * Declaring fields to be used within this class and the project
	 */
	private ArrayList<AudioPlayWorker> audioPlayWorkers = new ArrayList<AudioPlayWorker>(); // List of audio workers
	private ControlsPanel controlsPanel = this;		// Reference to the only instance of this class
    private VideoPlayerComponent videoPlayer;		// Reference to the video player component 
    private SkipVideoWorker skipVid = null;			// Reference to instance of a worker class to forward/rewind
    private JLabel currentTimeLabel;				// Label showing the time the video is currently at
    private JLabel totalTimeLabel;					// Label showing the total time of the video
    private EnhancedJSlider seekSlider;						// Slider showing progress of video and allowing user to set
    private JButton playButton;						// Play button the play the video
    private JButton pauseButton;					// Button to pause the video
    private JButton stopButton;						// Button to stop the video
    private JButton rewindButton;					// Button to rewind the video
    private JButton fastForwardButton;				// Button to fast forward the video
    private JButton audioOverlaysButton;            // Button to open the audio overlays dialog
    private EnhancedJSlider volumeSlider;			// Slider used to control volume
    private JLabel volumeLevelLabel;				// Label showing user current volume level
    private int volume = 100;						// Integer representing volume level
    private float totalTime = 0;					// Total time of the video
    private float currentTime = 0;					// Current time of the video
    private boolean sliderCanMove = false;			// Boolean indicating whether the slider is movable

    /**
     * Constructor called when this class is instantiated to set up the layout, listeners
     * and initialize the video player component
     * @param videoPlayer - the media play component which allows the video to played
     */
    public ControlsPanel(VideoPlayerComponent videoPlayer){
        this.videoPlayer = videoPlayer;
        try {
            setupLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setupListeners();
    }
    
    /**
     * Getter for the videoPlayer instance
     * @return the instance of the video player "videoPlayer"
     */
    public VideoPlayerComponent getVideoPlayer() {
    	return videoPlayer;
    }

    /**
     * Sets up the listeners for the controls
     */
    private void setupListeners(){
        // Seek slider change event to make video player to play at set time
        seekSlider.addChangeListener(new ChangeListener() {
            // For when user drags the seek bar
            @Override
            public void stateChanged(ChangeEvent e) {

                // Updating the video to play from where the user dragged the
                // slider to and updating the current time label accordingly
                if (sliderCanMove) {
                    // Updating current time
                    long currentTime = videoPlayer.getMediaPlayer().getTime();
                    setCurrentTime(currentTime);

                    // Setting the position of the video to that of the seek slider
                    videoPlayer.getMediaPlayer().setTime(seekSlider.getValue());

                }
            }
        });
        
        // Adding a mouse listener to the slider so that the boolean sliderCanMove
        // can be changed to true or false accordingly
        seekSlider.addMouseListener(new MouseAdapter() {

            // Setting the sliderCanMove field to true when the user clicks
            // on the slider so that they can drag the slider and update the video
            @Override
            public void mousePressed(MouseEvent arg0) {
                sliderCanMove = true;
                // Mute the audio when the slider is being moved
                videoPlayer.getMediaPlayer().mute(true);
                stopAudioPlayers();
            }

            // Setting the sliderCanMove field to false when the user releases
            // the click from the slider so that the state changed listener doesn't
            // cause frame lags
            @Override
            public void mouseReleased(MouseEvent arg0) {
                sliderCanMove = false;
                // Unmute the audio when the seek slider is released
                videoPlayer.getMediaPlayer().mute(false);
                if (videoPlayer.getMediaPlayer().isPlaying()) {
                    startAudioPlayers();
                }
            }
        });
        
        // Adding an action listener to the play button to allow the user to play the video
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // Un-muting the video if it is muted
            	videoPlayer.getMediaPlayer().mute(false);
            	
            	// Checking if the video is currently forwarding/rewinding and if it is
            	// then stopping it from skipping
                if (skipVid != null) {
                    skipVid.setIsSkipping(false);
                    skipVid.cancel(true);
                    skipVid = null;
                }
                
                // Starting the audio players
                startAudioPlayers();
                
                // Playing the video
                videoPlayer.getMediaPlayer().play();
            }
        });
        
        // Adding action listener for the pause button
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	// Un-muting the audio from the video
            	videoPlayer.getMediaPlayer().mute(false);
            	
            	// Checking if the video is currently forwarding/rewinding and
            	// stopping this process if it is
                if (skipVid != null) {
                    skipVid.setIsSkipping(false);
                    skipVid.cancel(true);
                    skipVid = null;
                }
                
                // Stopping the audio players
                stopAudioPlayers();
                
                // Pausing the video player
                videoPlayer.getMediaPlayer().pause();
            }
        });
        
        // Adding action listener to the stop button
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// Un-muting the video
                videoPlayer.getMediaPlayer().mute(false);

                // Checking if the video is currently forwarding/rewinding and
            	// stopping this process if it is
            	if (skipVid != null) {
                    skipVid.setIsSkipping(false);
                    skipVid.cancel(true);
                    skipVid = null;
                }
            	
            	// Stopping the audio players
                stopAudioPlayers();
                
                // Stopping the video
                videoPlayer.getMediaPlayer().stop();
  
                // Resetting the slider and the time label to their initial states
                seekSlider.setValue(0);
                setCurrentTime(0);
            }
        });
        
        // Adding action listener to the fast forward button to allow the user to
        // fast forward the video when the button is clicked on
        fastForwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	// Checking if there is no already running skipping function
                if (skipVid == null) {
                    stopAudioPlayers();
                    // Muting the video
                    videoPlayer.getMediaPlayer().mute(true);
                    
                    // Instantiating new SkipVideoWorker to forward the video
                    // and executing the thread to start the skipping
                    skipVid =  new SkipVideoWorker(videoPlayer, controlsPanel);
                    skipVid.setSkipValue(1000);
                    skipVid.setIsSkipping(true);
                    skipVid.execute();

                } else {
                	// If there is an already running skipping function then stopping it
                	if(videoPlayer.getMediaPlayer().isPlaying()){
                		startAudioPlayers();
                	}
                    videoPlayer.getMediaPlayer().mute(false);
                    skipVid.setIsSkipping(false);
                    skipVid.cancel(true);
                    skipVid = null;
                }
            }
        });
        
        // Adding listener to the rewind button to allow the user to rewind the video
        rewindButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	// Checking if the video is already forwarding or rewinding
                if (skipVid == null) {
                	// If the video is not currently forwarding/rewinding then muting
                	// the audio of the video and starting process to rewind the video
                    stopAudioPlayers();
                    videoPlayer.getMediaPlayer().mute(true);
                    skipVid = new SkipVideoWorker(videoPlayer, controlsPanel);
                    skipVid.setSkipValue(-1000);
                    skipVid.setIsSkipping(true);
                    skipVid.execute();

                } else {
                	// If the video is skipping then the following will un-mute the video
                	// and stop it from skipping
                	if(videoPlayer.getMediaPlayer().isPlaying()){
                		startAudioPlayers();
                	}
                    videoPlayer.getMediaPlayer().mute(false);
                    skipVid.setIsSkipping(false);
                    skipVid.cancel(true);
                    skipVid = null;
                }
            }
        });

        audioOverlaysButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // Open the audio overlays dialog
                AudioOverlaysDialog.getInstance().setVisible(true);
            }
        });
        
        // Adding a listener to the volume slider in order to change the volume of the video accordingly
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Getting current volume user has set
                volume = volumeSlider.getValue();
                // Updating the audio in the video player to the selected volume
                videoPlayer.getMediaPlayer().setVolume(volume);
                // Updating the label for the volume to show current volume level
                volumeLevelLabel.setText(volume + "%");
            }
        });
    }

    /**
     * Sets up the layout for the sliders, labels and other buttons through the use of the
     * grid bag layout
     */
    private void setupLayout() throws IOException {
    	
    	// Setting the layout to that of a grid bag layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        // Set up the slider panel which contains the slider and labels for the time
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new GridBagLayout());
        
        // Creating and adding current time label to the slider panel
        currentTimeLabel = new JLabel("00:00.000");
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;       
        sliderPanel.add(currentTimeLabel,gbc);
        
        // Creating and adding the seek slider to the slider panel
        seekSlider = new EnhancedJSlider();
        seekSlider.setMinorTickSpacing(1000);
        seekSlider.setMajorTickSpacing(1000);
        gbc.gridx=1;
        gbc.gridy=0;
        gbc.weightx=1.0f;
        gbc.weighty=1.0f;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        seekSlider.setMaximum(100);
        seekSlider.setMinimum(0);
        seekSlider.setValue(0);
        sliderPanel.add(seekSlider,gbc);    
        
        // Creating and adding the total video time label to the slider panel
        totalTimeLabel=new JLabel("00:00.000");
        gbc.gridx=2;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        sliderPanel.add(totalTimeLabel,gbc);
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=1.0;
        gbc.weighty=0.0;
        add(sliderPanel, gbc);
        
        // Creating a buttons panel to contain all the controls for the video
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        
        // Instantiating and adding the play button to the buttons panel
        playButton = new JButton(openImage("play.png"));
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(playButton);
        
        // Creating pause button and adding it to the layout
        pauseButton = new JButton(openImage("pause.png"));
        gbc.gridx=1;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(pauseButton,gbc);
        
        // Creating stop button and adding it to the layout
        stopButton=new JButton(openImage("stop.png"));
        gbc.gridx=2;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(stopButton,gbc);
        
        // Adding space between GUI components
        gbc.gridx=3;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(new JPanel());
        
        // Creating rewind button and adding it to the layout
        rewindButton=new JButton(openImage("rewind.png"));
        gbc.gridx=4;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(rewindButton,gbc);
        
        // Creating forward button and adding it to the layout
        fastForwardButton=new JButton(openImage("ff.png"));
        gbc.gridx=5;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(fastForwardButton,gbc);

        // Create button for opening AudioOverlays dislog
        audioOverlaysButton=new JButton("Add Audio");
        gbc.gridx=6;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(audioOverlaysButton,gbc);
        
        // Adding space between GUI components
        gbc.gridx=7;
        gbc.gridy=0;
        gbc.weightx=0.5f;
        gbc.weighty=1.0f;
        buttonsPanel.add(new JPanel(),gbc);
        
        // Adding volume label to the layout
        gbc.gridx=8;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(new JLabel(openImage("speaker.png")));
        
        // Adding the volume slider to the layout
        volumeSlider=new EnhancedJSlider();
        volumeSlider.setValue(100);
        volumeSlider.setPreferredSize(new Dimension(100,25));
        gbc.gridx=8;
        gbc.gridy=0;
        gbc.weightx=1.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(volumeSlider);
        
        // Adding the volume level label to the layout
        volumeLevelLabel=new JLabel(volume + "%");
        volumeLevelLabel.setPreferredSize(new Dimension(50,19));
        gbc.gridx=9;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(volumeLevelLabel);
        
        // Adding the button panels to the layout
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.weightx=1.0;
        gbc.weighty=0.0;
        add(buttonsPanel,gbc);
    }

    public static ImageIcon openImage(String path) throws IOException {
        return new ImageIcon(ImageIO.read(new File(path)));
    }

    /**
     * Method which sets the total time variable as well as the label in the GUI
     *
     * @param time - the total length of the video
     */
    public void setTotalTime(long time) {
    	totalTimeLabel.setText(formatTime(time));
    	totalTime = time;
    	// Setting the maximum value of the progress slider to the total time of the video
        seekSlider.setMaximum((int)time);
    }
    
    /**
     * Method which sets the current time variable as well as the label in the GUI
     *
     * @param time - the current time of the video
     */
    public void setCurrentTime(long time) {
    	currentTimeLabel.setText(formatTime(time));
    	currentTime = time;
    }
    
    /**
     * This method calculates the hours, minutes and seconds of a given time and formats
     * it into a string to be shown on the GUI
     * 
     * @param time - the time to convert into seconds, minutes and hours
     * @return timeString - returns the formatted string
     */
    private static String formatTime(long time) {
        // Ensure time is non-negative
        time=Math.max(time,0);

    	int millisecond= (int) (time % 1000);
    	int second = (int) ((time / 1000) % 60);
		int minute = (int) ((time/ 60000) % 60);
		int hour = (int) ((time/ 3600000) % 24);
		
		// Storing the formatted string
        if(hour>0) {
            return String.format("%d:%02d:%02d.%03d", hour, minute, second, millisecond);
        }else{
            return String.format("%02d:%02d.%03d", minute, second, millisecond);
        }
    }
    
    /**
     * Getter for the totalTime variable
     * @return
     */
    public float getTotalTime() {
    	return totalTime;
    }

    /**
     * Getter for the current play time
     * @return
     */
    public float getCurrentTime(){
        return currentTime;
    }
    
    /**
     * Getter for the slider field
     * @return
     */
    public JSlider getSlider() {
    	return seekSlider;
    }
    
    /**
     * Getter for the sliderCanMove variable
     * @return
     */
    public boolean getSliderStatus() {
    	return sliderCanMove;
    }

    /**
     * Plays all of the overlaid audio tracks
     */
    public void startAudioPlayers(){

    	stopAudioPlayers();
    	
    	for(AudioOverlay overlay: AudioOverlaysDialog.getOverlays()){
    		overlay.startAudioPlayer();
    	}
    }

    /**
     * Stops all the overlaid audio tracks currently playing
     */
    public void stopAudioPlayers(){
        for(AudioOverlay overlay: AudioOverlaysDialog.getOverlays()){
            overlay.stopAudioPlayer();
        }
    }

    /**
     * Updates the currently playing audio tracks to synchronize them with the video
     */
    public void updateAudioPlayers() {
        stopAudioPlayers();
        if(videoPlayer.getMediaPlayer().isPlaying()){
        	startAudioPlayers();
        }
    }
}
