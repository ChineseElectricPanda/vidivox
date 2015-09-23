package vidivox.worker;
import java.util.List;

import javax.swing.SwingWorker;

import vidivox.ui.ControlsPanel;
import vidivox.ui.VideoPlayerComponent;

public class SkipVideoWorker extends SwingWorker<Void, Integer> {

	private int skipValue = 0;
	private VideoPlayerComponent videoPlayer = null;
	private boolean isSkipping = false;
	private ControlsPanel controlsPanel;
	private float pos;
	private int time;
	private String timeString; 
	
	public SkipVideoWorker (VideoPlayerComponent videoPlayer, ControlsPanel controlsPanel) {
		this.videoPlayer = videoPlayer;
		this.controlsPanel = controlsPanel;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		while (isSkipping == true) {
			Thread.sleep(100);
			
			pos = videoPlayer.getMediaPlayer().getPosition()*100;
			time = (Math.round(videoPlayer.getMediaPlayer().getTime()));
			long second = (long) ((time / 1000) % 60);
			long minute = (long) ((time/ 60000) % 60);
			long hour = (long) ((time/ 3600000) % 24);
			timeString = String.format("%02d:%02d:%02d", hour, minute, second);
			
			publish(skipValue);
			
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