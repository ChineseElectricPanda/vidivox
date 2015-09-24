package vidivox.ui;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vidivox.audio.AudioOverlay;
import vidivox.audio.CommentaryOverlay;
import vidivox.worker.AudioPlayWorker;
import vidivox.worker.SkipVideoWorker;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
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
    private JSlider seekSlider;						// Slider showing progress of video and allowing user to set
    private JButton playButton;						// Play button the play the video
    private JButton pauseButton;					// Button to pause the video
    private JButton stopButton;						// Button to stop the video
    private JButton rewindButton;					// Button to rewind the video
    private JButton fastForwardButton;				// Button to fast forward the video
    private JButton skipBackButton;					// Button to skip backwards
    private JButton skipForwardButton;				// Button to skip forwards
    private JSlider volumeSlider;					// Slider used to control volume
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
        setupLayout();
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
        seekSlider.addChangeListener(new ChangeListener()  {
            // For when user drags the seek bar
            @Override
            public void stateChanged(ChangeEvent e) {
                
            	// Updating the video to play from where the user dragged the 
            	// slider to and updating the current time label accordingly
            	if (sliderCanMove) {
            		// Updating current time
                    long currentTime = videoPlayer.getMediaPlayer().getTime();
                    setCurrentTime(calculateTime(currentTime), currentTime);
                    
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
                if(videoPlayer.getMediaPlayer().isPlaying()){
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
    			
                // Check through the commentaries to make sure they are all under 80 characters
    			List<CommentaryOverlay> commentaryOverlays = AudioOverlaysDialog.commentaryOverlays;
    			for(CommentaryOverlay overlay: commentaryOverlays){
    				// Looking through the commentary overlays to check if the text is less than 80 characters
					if (overlay.getText().length() >= 80) {
						
						// Showing error message to user informing them character limit is 80
						JOptionPane.showMessageDialog(null,
								"Must specify comment less than or equal 80 characters",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					} 
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
                setCurrentTime("00:00:00", 0);
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
        
        // Adding action listener for the skip ahead one large frame at a time button aka skip forward
        skipForwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	// Making sure the video is not muted
                videoPlayer.getMediaPlayer().mute(false);

                // Checking if the video is being forwarded or is rewinding and stopping that process if it is
                if (skipVid != null) {
                    skipVid.setIsSkipping(false);
                    skipVid.cancel(true);
                    skipVid = null;
                }

                // Skipping the video ahead and updating the audio
                videoPlayer.getMediaPlayer().skip(10000);
                seekSlider.setValue(seekSlider.getValue()+10000);
                updateAudioPlayers();
            }
        });
        
        // Adding action listener for the skip backward one large frame at a time button aka skip back
        skipBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// Making sure the video is not mute
                videoPlayer.getMediaPlayer().mute(false);

                // Checking if the video is currently forwarding/rewinding and stopping the process if it is
                if (skipVid != null) {
                    skipVid.setIsSkipping(false);
                    skipVid.cancel(true);
                    skipVid = null;
                }
                // RSkipping backwards by a set amount and updating the audio players
                videoPlayer.getMediaPlayer().skip(-10000);
                seekSlider.setValue(seekSlider.getValue()-10000);
                updateAudioPlayers();
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
    private void setupLayout(){
    	
    	// Setting the layout to that of a grid bag layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        // Set up the slider panel which contains the slider and labels for the time
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new GridBagLayout());
        
        // Creating and adding current time label to the slider panel
        currentTimeLabel = new JLabel("00:00:00");
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;       
        sliderPanel.add(currentTimeLabel,gbc);
        
        // Creating and adding the seek slider to the slider panel
        seekSlider = new JSlider();
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
        totalTimeLabel=new JLabel("00:00:00");
        gbc.gridx=2;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        sliderPanel.add(totalTimeLabel,gbc);
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=1.0;
        gbc.weighty=0.0;
        add(sliderPanel,gbc);
        
        // Creating a buttons panel to contain all the controls for the video
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        
        // Instantiating and adding the play button to the buttons panel
        playButton = new JButton("Play");
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(playButton);
        
        // Creating pause button and adding it to the layout
        pauseButton = new JButton("Pause");
        gbc.gridx=1;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(pauseButton,gbc);
        
        // Creating stop button and adding it to the layout
        stopButton=new JButton("Stop");
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
        rewindButton=new JButton("<<");
        gbc.gridx=4;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(rewindButton,gbc);
        
        // Creating forward button and adding it to the layout
        fastForwardButton=new JButton(">>");
        gbc.gridx=5;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(fastForwardButton,gbc);
        
        // Creating skip forward button and adding it to the layout
        skipForwardButton = new JButton(">");
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.weightx=0.0f;
        gbc.weighty=3.0f;
        buttonsPanel.add(skipForwardButton, gbc);
        
        // Creating skip back button and adding it to the layout
        skipBackButton = new JButton("<");
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.weightx=0.0f;
        gbc.weighty=3.0f;
        buttonsPanel.add(skipBackButton, gbc);
        
        // Adding space between GUI components
        gbc.gridx=8;
        gbc.gridy=0;
        gbc.weightx=0.5f;
        gbc.weighty=1.0f;
        buttonsPanel.add(new JPanel(),gbc);
        
        // Adding volume label to the layout
        gbc.gridx=8;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(new JLabel("Volume"));
        
        // Adding the volume slider to the layout
        volumeSlider=new JSlider();
        volumeSlider.setValue(100);
        volumeSlider.setPreferredSize(new Dimension(100,25));
        gbc.gridx=8;
        gbc.gridy=0;
        gbc.weightx=1.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(volumeSlider);
        
        // Adding the volume level label to the layout
        volumeLevelLabel=new JLabel(volume + "%");
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

    /**
     * Method which sets the total time variable as well as the label in the GUI
     * 
     * @param timeString -  the formatted string containing the total length of the video
     * @param time - the total length of the video
     */
    public void setTotalTime(String timeString, long time) {
    	totalTimeLabel.setText(timeString);
    	totalTime = time;
    	// Setting the maximum value of the progress slider to the total time of the video
        seekSlider.setMaximum((int)time);
    }
    
    /**
     * Method which sets the current time variable as well as the label in the GUI
     * 
     * @param timeString - formatted string containing the current time of the video
     * @param time - the current time of the video
     */
    public void setCurrentTime(String timeString, long time) {
    	currentTimeLabel.setText(timeString);
    	currentTime = time;
    }
    
    /**
     * This method calculates the hours, minutes and seconds of a given time and formats
     * it into a string to be shown on the GUI
     * 
     * @param newTime - the time to convert into seconds, minutes and hours
     * @return timeString - returns the formatted string
     */
    public String calculateTime(float newTime) {
    	
    	long second = (long) ((newTime / 1000) % 60);
		long minute = (long) ((newTime/ 60000) % 60);
		long hour = (long) ((newTime/ 3600000) % 24);
		
		// Storing the formatted string
		String timeString = String.format("%02d:%02d:%02d", hour, minute, second);
		return timeString;
    }
    
    /**
     * Getter for the totalTime variable
     * @return
     */
    public float getTotalTime() {
    	return totalTime;
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
    		// Only play the overlay tracks with "preview" ticked
    		if(overlay.isShowingPreview()) {
    			long currentPositionMillis = videoPlayer.getMediaPlayer().getTime();
    			double currentPositionSeconds = ((double) currentPositionMillis) / 1000;
    			AudioPlayWorker player = new AudioPlayWorker(overlay, currentPositionSeconds);
    			player.execute();
    			audioPlayWorkers.add(player);
    		}
    	}
    }

    /**
     * Stops all the overlaid audio tracks currently playing
     */
    public void stopAudioPlayers(){
        for(AudioPlayWorker player : audioPlayWorkers){
            player.kill();
        }
        audioPlayWorkers = new ArrayList<>();
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
