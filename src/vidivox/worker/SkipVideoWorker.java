package vidivox.worker;
import java.util.List;

import javax.swing.SwingWorker;

import vidivox.ui.ControlsPanel;
import vidivox.video.EmbeddedVideoPlayer;
import vidivox.video.Player;

/**
 * This class extends swing worker and inherits its methods to perform a time consuming
 * task in the background. This class deals with the fast forwarding and rewinding of the 
 * video by running the skip function in a while loop and publishing the results to the GUI
 * 
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 *
 */
public class SkipVideoWorker extends SwingWorker<Void, Integer> {

	/**
	 * Declaring fields to be used within class and project
	 */
	private ControlsPanel controlsPanel;				// Reference to controls panel class
	private Player videoPlayer = null;	// Reference to the video player
	private boolean isSkipping = false;					// Boolean to allow skipping
	private int skipValue = 0;							// Value to skip by
	private int time;									// Current time of the video
	
	/**
	 * Constructor called to set references for fields declared
	 * 
	 * @param videoPlayer -  video player reference
	 * @param controlsPanel - controls panel reference
	 */
	public SkipVideoWorker (Player videoPlayer, ControlsPanel controlsPanel) {
		this.videoPlayer = videoPlayer;
		this.controlsPanel = controlsPanel;
	}
	
	/**
	 * Method called when this thread is executed and performs the skipping of the video
	 */
	@Override
	protected Void doInBackground() throws Exception {
		
		// While the user still wants the video to fast forward
		while (isSkipping == true) {
			// Sleeping the thread for 100 milliseconds to avoid errors
			Thread.sleep(100);

			// Obtaining the current time of the video 
			time = Math.round(videoPlayer.getMediaPlayer().getTime());
						
			// Disabling the users ability to rewind at the 0th second of the video and 
			// to forward at the end of the video
			if (time > 0 && time < controlsPanel.getTotalTime()) {
				// Calling process method to skip the video
				publish(skipValue);
			} else {
				// Stopping video when it reaches the end or beginning when the user was forwarding/rewinding
				videoPlayer.getMediaPlayer().stop();
			}
		}
		return null;
	}
	
	/**
	 * This method is called to skip the video, set the slider to the appropriate position 
	 * according to where the video is currently playing and updating the time label showing
	 * the time at which the video is playing
	 */
	public void process (List<Integer> chunks) {
		// Skipping video
		videoPlayer.getMediaPlayer().skip(skipValue);
		
		// Getting position of video and setting the slider to that position
		long pos = videoPlayer.getMediaPlayer().getTime();
		controlsPanel.getSlider().setValue((int)pos);
		
		// Setting the time label in the GUI
		controlsPanel.setCurrentTime(time);
	}
	
	/**
	 * Method used to set the boolean isSkipping
	 * @param bool - true/false depending on what is inputed
	 */
	public void setIsSkipping(boolean bool) {
		isSkipping = bool;
	}
	
	/**
	 * Method used to set the value at which to skip the video
	 * @param skipValue - value at which to skip
	 */
	public void setSkipValue(int skipValue) {
		this.skipValue = skipValue;
	}
	
}