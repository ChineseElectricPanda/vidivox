package vidivox;
import javax.swing.SwingUtilities;

import vidivox.ui.ControlsPanel;
import vidivox.ui.timeline.AudioTimelinesPanel;
import vidivox.video.VideoPlayerComponent;

/**
 * This class is instantiated every 500 milliseconds and is used to set the time label and the slider
 * which shows the progress of the video without causing the GUI to freeze
 * 
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 *
 */
public class UpdateRunnable implements Runnable {
	
	/**
	 * Field which stores the reference to the ControlsPanel class to enable the editing
	 * of the time label and slider contained within that class
	 */
	private ControlsPanel controlsPanel;
	private AudioTimelinesPanel audioTimelinesPanel;

	public UpdateRunnable(ControlsPanel controlsPanel, AudioTimelinesPanel audioTimelinesPanel) {
		this.controlsPanel = controlsPanel;
		this.audioTimelinesPanel = audioTimelinesPanel;
	}
	
	/**
	 * Overriding inherited run() method to set the position of the slider and time label according to the current
	 * position of the video
	 */
	@Override
	public void run() {
		
		// Getting the video player instance from the ControlsPanel class
		final VideoPlayerComponent videoPlayer = controlsPanel.getVideoPlayer();
		
		// Getting the position the video is currently at
		final int position = (int) videoPlayer.getMediaPlayer().getTime();
		
		// Using SwingUtilities to create a thread which is invoked when it is possible to do so
		// and thus enables the GUI to not freeze
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Checking if the video is playing
				if(videoPlayer.getMediaPlayer().isPlaying()) {
					
					// Updating the position of the slider showing the current position of the video
					updatePosition(position);

					// Getting the current time of the video
					int time = Math.round(videoPlayer.getMediaPlayer().getTime());

					// Stopping all audio playing when the video reaches the end
					if (time >= controlsPanel.getTotalTime()) {
						controlsPanel.stopAudioPlayers();
					}
					
					// Calling method in the ControlsPanel class to set the time label indicating the time-stamp
					// of the video
					controlsPanel.setCurrentTime(time);
				}
			}

			/**
			 * This method takes in an int specifying the position of the video and sets the position
			 * of the slider to that position thus enabling the user to see how much time has elapsed
			 * since the video has been started
			 * 
			 * @param position - position of the video
			 */
			private void updatePosition(int position) {
				controlsPanel.getSlider().setValue(position);
				audioTimelinesPanel.updatePosition(((double)position)/1000);
			}
		});
	}

}
