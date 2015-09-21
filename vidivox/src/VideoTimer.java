

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
		seekTimePercentage = controlsPanel.seekSlider.getValue();
		newTime = seekTimePercentage * (controlsPanel.totalTime/100);
		
		// Formatting time to look better
		long second = (long) ((newTime / 1000) % 60);
       	long minute = (long) ((newTime/ 60000) % 60);
       	long hour = (long) ((newTime/ 3600000) % 24);
       	timeString = String.format("%02d:%02d:%02d", hour, minute, second);
       	
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
