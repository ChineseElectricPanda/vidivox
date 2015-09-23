

import java.util.List;

import javax.swing.SwingWorker;


public class VideoTimer extends SwingWorker<Void, Void>{

	private ControlsPanel controlsPanel;
	private VideoPlayerComponent videoPlayer = null;
	private String timeString;
	private float newTime;
	private float seekTimePercentage; 
	
	public VideoTimer(ControlsPanel controlsPanel) {
		this.controlsPanel = controlsPanel;
		videoPlayer = controlsPanel.getVideoPlayer();
	}
	
	@Override
	protected Void doInBackground() throws Exception {

		// Setting the video to play according to the where the user wants the video
		// to play when they drag the seek slider
		seekTimePercentage = controlsPanel.getSlider().getValue();
		newTime = seekTimePercentage * (controlsPanel.getTotalTime()/100);
		
		timeString = controlsPanel.calculateTime(newTime);
		
       	publish();
		
		return null;
	}
	
	public void process (List<Void> chunks) {
		
		controlsPanel.setCurrentTime(timeString, (long)newTime);
		
        if (controlsPanel.sliderCanMove) {
        	videoPlayer.getMediaPlayer().setPosition(seekTimePercentage/100);
		}
	}
	
	
}
