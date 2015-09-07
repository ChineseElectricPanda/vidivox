import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;

public class ControlsPanel extends JPanel {
    private JLabel currentTimeLabel;
    private JLabel totalTimeLabel;
    private JSlider seekSlider;
    private JButton playButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton rewindButton;
    private JButton fastForwardButton;
    private JSlider volumeSlider;
    private JLabel volumeLevelLabel;
    private int volume = 50;
    private String totalTime = "00:00";
    
    public ControlsPanel(){
        setupLayout();
    }

    /**
     * Sets up the seek slider and buttons layout
     */
    private void setupLayout(){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();

        //set up the time slider layout
        JPanel sliderPanel=new JPanel();
        sliderPanel.setLayout(new GridBagLayout());
        currentTimeLabel=new JLabel("00:00");
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
        sliderPanel.add(seekSlider,gbc);
        totalTimeLabel=new JLabel(totalTime);
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
        		// Playing the video
        		MainFrame.videoPlayer.getMediaPlayer().play();
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
        		// Pausing the video player
        		MainFrame.videoPlayer.getMediaPlayer().pause();
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
        		MainFrame.videoPlayer.getMediaPlayer().stop();
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
        		// Rewinding the player
        		MainFrame.videoPlayer.getMediaPlayer().skip(-10000);
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
        		// Forwarding the video
        		MainFrame.videoPlayer.getMediaPlayer().skip(10000);
        	}
        });
        buttonsPanel.add(fastForwardButton,gbc);
        gbc.gridx=6;
        gbc.gridy=0;
        gbc.weightx=0.5f;
        gbc.weighty=1.0f;
        buttonsPanel.add(new JPanel(),gbc);
        gbc.gridx=7;
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
				MainFrame.videoPlayer.getMediaPlayer().setVolume(volume);
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
    
    public void setTotalTime(String time) {
    	totalTimeLabel.setText(time);
    }
    
}
