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
import java.util.ArrayList;

public class ControlsPanel extends JPanel {
	private ControlsPanel controlsPanel = this;
    private VideoPlayerComponent videoPlayer;
    private JLabel currentTimeLabel;
    private JLabel totalTimeLabel;
    private JSlider seekSlider;
    private JButton playButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton rewindButton;
    private JButton fastForwardButton;
    private JButton skipBackButton;
    private JSlider volumeSlider;
    private JLabel volumeLevelLabel;
    private JButton skipForwardButton;
    private int volume = 50;
    private float totalTime = 0;
    private float currentTime = 0;
    private boolean sliderCanMove = false;
    private SkipVideoWorker skipVid = null;

    private ArrayList<AudioPlayWorker> audioPlayWorkers = new ArrayList<AudioPlayWorker>();
    
    public ControlsPanel(VideoPlayerComponent videoPlayer){
        this.videoPlayer=videoPlayer;
        setupLayout();
        setupListeners();
    }
    
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
                if (sliderCanMove) {
                    long currentTime=videoPlayer.getMediaPlayer().getTime();
                    setCurrentTime(calculateTime(currentTime),currentTime);
                    videoPlayer.getMediaPlayer().setTime(seekSlider.getValue());
                    updateAudioPlayers();
                }
            }
        });
        seekSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                sliderCanMove = true;
            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
                sliderCanMove = false;
            }
        });
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                videoPlayer.getMediaPlayer().mute(false);
                if (skipVid != null) {
                    skipVid.setIsSkipping(false);
                    skipVid.cancel(true);
                    skipVid = null;

                }
                startAudioPlayers();
                // Playing the video
                videoPlayer.getMediaPlayer().play();
            }
        });
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (skipVid != null) {
                    skipVid.setIsSkipping(false);
                    skipVid.cancel(true);
                    skipVid = null;
                    videoPlayer.getMediaPlayer().mute(false);
                }
                stopAudioPlayers();
                // Pausing the video player
                videoPlayer.getMediaPlayer().pause();
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (skipVid != null) {
                    skipVid.setIsSkipping(false);
                    skipVid.cancel(true);
                    skipVid = null;
                    videoPlayer.getMediaPlayer().mute(false);
                }
                stopAudioPlayers();
                videoPlayer.getMediaPlayer().stop();
                seekSlider.setValue(0);
                setCurrentTime("00:00:00", 0);
            }
        });
        fastForwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (skipVid == null) {
                    stopAudioPlayers();
                    videoPlayer.getMediaPlayer().mute(true);
                    skipVid =  new SkipVideoWorker(videoPlayer, controlsPanel);
                    skipVid.setSkipValue(1000);
                    skipVid.setIsSkipping(true);
                    skipVid.execute();

                } else {
                    startAudioPlayers();
                    videoPlayer.getMediaPlayer().mute(false);
                    skipVid.setIsSkipping(false);
                    skipVid.cancel(true);
                    skipVid = null;
                }
            }
        });
        rewindButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (skipVid == null) {
                    stopAudioPlayers();
                    videoPlayer.getMediaPlayer().mute(true);
                    skipVid = new SkipVideoWorker(videoPlayer, controlsPanel);
                    skipVid.setSkipValue(-1000);
                    skipVid.setIsSkipping(true);
                    skipVid.execute();

                } else {
                    startAudioPlayers();
                    videoPlayer.getMediaPlayer().mute(false);
                    skipVid.setIsSkipping(false);
                    skipVid.cancel(true);
                    skipVid = null;
                }
            }
        });
        skipForwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                videoPlayer.getMediaPlayer().mute(false);

                if (skipVid != null) {
                    skipVid.setIsSkipping(false);
                    skipVid.cancel(true);
                    skipVid = null;
                }

                // Forwarding the video
                videoPlayer.getMediaPlayer().skip(10000);
                updateAudioPlayers();
            }
        });
        skipBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                videoPlayer.getMediaPlayer().mute(false);
                System.out.println("heheheh");
                if (skipVid != null) {
                    skipVid.setIsSkipping(false);
                    skipVid.cancel(true);
                    skipVid = null;
                }
                // Rewinding the player
                videoPlayer.getMediaPlayer().skip(-10000);
                updateAudioPlayers();
            }
        });
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
     * Sets up the seek slider and buttons
     */
    private void setupLayout(){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(5,5,5,5);

        //set up the time slider layout
        JPanel sliderPanel=new JPanel();
        sliderPanel.setLayout(new GridBagLayout());
        currentTimeLabel=new JLabel("00:00:00");
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;       
        sliderPanel.add(currentTimeLabel,gbc);
        seekSlider=new JSlider();
        gbc.gridx=1;
        gbc.gridy=0;
        gbc.weightx=1.0f;
        gbc.weighty=1.0f;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        seekSlider.setMaximum(100);
        seekSlider.setMinimum(0);
        seekSlider.setValue(0);
        sliderPanel.add(seekSlider,gbc);        
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
        
        //set up the control buttons layout
        JPanel buttonsPanel=new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        
        playButton=new JButton("Play");
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;

        playButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		videoPlayer.getMediaPlayer().mute(false);
        		if (skipVid != null) {
        			skipVid.setIsSkipping(false);
        			skipVid.cancel(true);
        			skipVid = null;
        			
        		}      		
            	
				if (Math.round(videoPlayer.getMediaPlayer().getTime()) < 100) {
        		ArrayList<AudioOverlay> overlays = (ArrayList<AudioOverlay>) AudioOverlaysDialog.getOverlays();
        		for (int i = 0; i < overlays.size(); i ++) {
        			if (overlays.get(i).isShowingPreview() == true) {
        				ArrayList<CommentaryOverlay> commentaryOverlays = (ArrayList<CommentaryOverlay>) AudioOverlaysDialog.commentaryOverlays;
        				if (commentaryOverlays.get(i).getText().length() >= 80) {
        					JOptionPane.showMessageDialog(null,
        							"Must specify comment less than or equal 80 characters",
        							"Error",
        							JOptionPane.ERROR_MESSAGE);
        					return;
        				} 

        				String filePath = overlays.get(i).getFilePath();
        				AudioPlayWorker audioPlayer = new AudioPlayWorker(filePath, 100);
        				audioPlayWorkers.add(audioPlayer);
        				audioPlayer.execute();
        			}
        		}
				}
        		
        		// Playing the video
        		videoPlayer.getMediaPlayer().play();
        	}
        });
        
        buttonsPanel.add(playButton);
        pauseButton=new JButton("Pause");
        gbc.gridx=1;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(pauseButton,gbc);
        stopButton=new JButton("Stop");
        gbc.gridx=2;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(stopButton,gbc);
        gbc.gridx=3;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(new JPanel());
        rewindButton=new JButton("<<");
        gbc.gridx=4;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(rewindButton,gbc);
        fastForwardButton=new JButton(">>");
        gbc.gridx=5;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(fastForwardButton,gbc);
        skipForwardButton = new JButton(">");
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.weightx=0.0f;videoPlayer.getMediaPlayer().mute(false);
        gbc.weighty=3.0f;
        buttonsPanel.add(skipForwardButton, gbc);
        
        skipBackButton = new JButton("<");
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.weightx=0.0f;
        gbc.weighty=3.0f;
        buttonsPanel.add(skipBackButton, gbc);
        
        gbc.gridx=8;
        gbc.gridy=0;
        gbc.weightx=0.5f;
        gbc.weighty=1.0f;
        buttonsPanel.add(new JPanel(),gbc);
        gbc.gridx=8;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(new JLabel("Volume"));
        volumeSlider=new JSlider();
        volumeSlider.setPreferredSize(new Dimension(100,25));
        gbc.gridx=8;
        gbc.gridy=0;
        gbc.weightx=1.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(volumeSlider);
        volumeLevelLabel=new JLabel(volume + "%");
        gbc.gridx=9;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        buttonsPanel.add(volumeLevelLabel);
        
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.weightx=1.0;
        gbc.weighty=0.0;
        add(buttonsPanel,gbc);
    }

    public void setTotalTime(String timeString, long time) {
    	totalTimeLabel.setText(timeString);
    	totalTime = time;
        seekSlider.setMaximum((int)time);
    }
    
    public void setCurrentTime(String timeString, long time) {
    	currentTimeLabel.setText(timeString);
    	currentTime = time;
    }
    
    public String calculateTime(float newTime) {
    	
    	long second = (long) ((newTime / 1000) % 60);
		long minute = (long) ((newTime/ 60000) % 60);
		long hour = (long) ((newTime/ 3600000) % 24);
		String timeString = String.format("%02d:%02d:%02d", hour, minute, second);
		return timeString;
    }
    
    public float getTotalTime() {
    	return totalTime;
    }
    
    public JSlider getSlider() {
    	return seekSlider;
    }
    
    public boolean getSliderStatus() {
    	return sliderCanMove;
    }

    /**
     * Plays all of the overlaid audio tracks
     */
    private void startAudioPlayers(){
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
    private void stopAudioPlayers(){
        for(AudioPlayWorker player: audioPlayWorkers){
            player.kill();
        }
        audioPlayWorkers=new ArrayList<>();
    }

    /**
     * Updates the currently playing audio tracks to synchronise them with the video
     */
    private void updateAudioPlayers() {
        stopAudioPlayers();
        startAudioPlayers();
    }
}
