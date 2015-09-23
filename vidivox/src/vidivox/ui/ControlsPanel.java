package vidivox.ui;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vidivox.audio.AudioOverlay;
import vidivox.audio.CommentaryOverlay;
import vidivox.worker.AudioPlayWorker;
import vidivox.worker.SkipVideoWorker;
import vidivox.worker.VideoTimerWorker;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class ControlsPanel extends JPanel {
	private ControlsPanel controlsPanel = this;
    private VideoPlayerComponent videoPlayer;
    protected JLabel currentTimeLabel;
    protected JLabel totalTimeLabel;
    public JSlider seekSlider;
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
    public float totalTime = 0;
    protected float currentTime = 0;
    public boolean sliderCanMove = false;
    private SkipVideoWorker skipVid = null;
    private ArrayList<AudioPlayWorker> audioPlayWorkerList = new ArrayList<AudioPlayWorker>();
    
    public ControlsPanel(VideoPlayerComponent videoPlayer){
        this.videoPlayer=videoPlayer;
        setupLayout();
    }
    
    public VideoPlayerComponent getVideoPlayer() {
    	return videoPlayer;
    }

    /**
     * Sets up the seek slider and buttons layoutideo skipV
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
        // Seek slider change event to make video player to play at set time
        seekSlider.addChangeListener(new ChangeListener()  {
			// For when user drags the seek bar
        	@Override
			public void stateChanged(ChangeEvent e) {
        		if (sliderCanMove) {
        			VideoTimerWorker videoTimeControl = new VideoTimerWorker(controlsPanel);
        			videoTimeControl.execute();
        		}
        	}
        });
        seekSlider.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				sliderCanMove = true;
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				sliderCanMove = false;
			}
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}     	
        });
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
        		if (skipVid != null) {
        			skipVid.setIsSkipping(false);
        			skipVid.cancel(true);
        			skipVid = null;
        			videoPlayer.getMediaPlayer().mute(false);
        		}      		
            	
				if (Math.round(videoPlayer.getMediaPlayer().getTime()) < 100) {
        		ArrayList<AudioOverlay> overlays = (ArrayList<AudioOverlay>) AudioOverlaysDialog.getOverlays();
        		for (int i = 0; i < overlays.size(); i ++) {
        			if (overlays.get(i).showPreview == true) {
        				ArrayList<CommentaryOverlay> commentaryOverlays = (ArrayList<CommentaryOverlay>) AudioOverlaysDialog.commentaryOverlays;
        				if (commentaryOverlays.get(i).text.length() > 20) {
        					JOptionPane.showMessageDialog(null,
        							"Must specify comment less than or equal 20 characters",
        							"Error",
        							JOptionPane.ERROR_MESSAGE);
        					return;
        				} 

        				String filePath = overlays.get(i).getFilePath();
        				AudioPlayWorker audioPlayer = new AudioPlayWorker(filePath, 100);
        				audioPlayWorkerList.add(audioPlayer);
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
        pauseButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if (skipVid != null) {
        			skipVid.setIsSkipping(false);
        			skipVid.cancel(true);
        			skipVid = null;
        			videoPlayer.getMediaPlayer().mute(false);
        		}
        		for (int i = 0; i < audioPlayWorkerList.size(); i++) {
        			try {
						audioPlayWorkerList.get(i).pause();
					} catch (IOException | InterruptedException e1) {
						e1.printStackTrace();
					}
        		}
        		// Pausing the video player
        		videoPlayer.getMediaPlayer().pause();
        	}
        });
        buttonsPanel.add(pauseButton,gbc);
        stopButton=new JButton("Stop");
        gbc.gridx=2;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        stopButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if (skipVid != null) {
        			skipVid.setIsSkipping(false);
        			skipVid.cancel(true);
        			skipVid = null;
        			videoPlayer.getMediaPlayer().mute(false);
        		}
        		for (int i = 0; i < audioPlayWorkerList.size(); i++) {
        			audioPlayWorkerList.get(i).kill();
        		}
        		videoPlayer.getMediaPlayer().stop();
        		seekSlider.setValue(0);
        		setCurrentTime("00:00:00", 0);
        	}
        });
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
        rewindButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if (skipVid == null) {
        			videoPlayer.getMediaPlayer().mute(true);
        			skipVid =  new SkipVideoWorker(videoPlayer, controlsPanel);
        			skipVid.setSkipValue(-1000);
        			skipVid.setIsSkipping(true);
        			skipVid.execute();
        			
        		} else if (skipVid != null) {
        			videoPlayer.getMediaPlayer().mute(false);
        			skipVid.setIsSkipping(false);
        			skipVid.cancel(true);
        			skipVid = null;
        		}
        	}
        });
        buttonsPanel.add(rewindButton,gbc);
        fastForwardButton=new JButton(">>");
        gbc.gridx=5;
        gbc.gridy=0;
        gbc.weightx=0.0f;
        gbc.weighty=1.0f;
        fastForwardButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if (skipVid == null) {
        			videoPlayer.getMediaPlayer().mute(true);
        			skipVid =  new SkipVideoWorker(videoPlayer, controlsPanel);
        			skipVid.setSkipValue(1000);
        			skipVid.setIsSkipping(true);
        			skipVid.execute();
        			
        		} else if (skipVid != null) {
        			videoPlayer.getMediaPlayer().mute(false);
        			skipVid.setIsSkipping(false);
        			skipVid.cancel(true);
        			skipVid = null;
        		}
        	}
        });
        buttonsPanel.add(fastForwardButton,gbc);
        skipForwardButton = new JButton(">");
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.weightx=0.0f;videoPlayer.getMediaPlayer().mute(false);
        gbc.weighty=3.0f;
        skipForwardButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {        		
        		if (skipVid != null) {
        			skipVid.setIsSkipping(false);
        			skipVid.cancel(true);
        			skipVid = null;
        			videoPlayer.getMediaPlayer().mute(false);
        		}
        		// Forwarding the video
        		videoPlayer.getMediaPlayer().skip(10000);  
        	}
        });
        buttonsPanel.add(skipForwardButton, gbc);
        
        skipBackButton = new JButton("<");
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.weightx=0.0f;
        gbc.weighty=3.0f;
        skipBackButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {        		
        		if (skipVid != null) {
        			skipVid.setIsSkipping(false);
        			skipVid.cancel(true);
        			skipVid = null;
        			videoPlayer.getMediaPlayer().mute(false);
        		}
        		// Rewinding the player
        		videoPlayer.getMediaPlayer().skip(-10000);
        	}
        });
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
        volumeSlider.addChangeListener(new ChangeListener()  {
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
}
