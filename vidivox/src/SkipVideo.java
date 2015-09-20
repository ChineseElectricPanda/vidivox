import java.util.List;

import javax.swing.SwingWorker;

public class SkipVideo extends SwingWorker<Void, Integer> {

	private int skipValue = 0;
	private VideoPlayerComponent videoPlayer = null;
	private boolean isSkipping = false;
	
	public SkipVideo (VideoPlayerComponent videoPlayer) {
		this.videoPlayer = videoPlayer;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		while (isSkipping = true) {
			Thread.sleep(100);
			publish(skipValue);
		}
		
		return null;
	}
	
	public void process (List<Integer> chunks) {
		videoPlayer.getMediaPlayer().skip(skipValue);
	}
	
	public void setIsSkipping(boolean bool) {
		isSkipping = bool;
	}
	
	public void setSkipValue(int skipValue) {
		this.skipValue = skipValue;
	}
	
}