import java.util.List;

import javax.swing.SwingWorker;

public class SkipVideo extends SwingWorker<Void, Integer> {

	private int skipValue = 0;
	private VideoPlayerComponent videoPlayer = null;
	private boolean isSkipping = false;
	private ControlsPanel controlsPanel;
	private float pos;
	private int time;
	private String timeString; 
	
	public SkipVideo (VideoPlayerComponent videoPlayer, ControlsPanel controlsPanel) {
		this.videoPlayer = videoPlayer;
		this.controlsPanel = controlsPanel;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		while (isSkipping == true) {
			Thread.sleep(100);
						
			pos = videoPlayer.getMediaPlayer().getPosition()*100;
			time = (Math.round(videoPlayer.getMediaPlayer().getTime()));
			timeString = controlsPanel.calculateTime(time);
			
			if (time > 0 && time < controlsPanel.totalTime) {
				publish(skipValue);
			} else {
				// Stopping video when it reaches the end or beginning when the user was forwarding/rewinding
				videoPlayer.getMediaPlayer().stop();
			}
			
		}
		
		return null;
	}
	
	public void process (List<Integer> chunks) {
		videoPlayer.getMediaPlayer().skip(skipValue);
		controlsPanel.seekSlider.setValue((int)pos);
		controlsPanel.setCurrentTime(timeString, time);
	}
	
	public void setIsSkipping(boolean bool) {
		isSkipping = bool;
	}
	
	public void setSkipValue(int skipValue) {
		this.skipValue = skipValue;
	}
	
}