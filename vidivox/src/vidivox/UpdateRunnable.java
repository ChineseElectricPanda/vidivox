package vidivox;
import javax.swing.SwingUtilities;

import vidivox.ui.ControlsPanel;
import vidivox.ui.VideoPlayerComponent;


public class UpdateRunnable implements Runnable {
	
	private ControlsPanel controlsPanel;

	
	public UpdateRunnable(ControlsPanel controlsPanel) {
		this.controlsPanel = controlsPanel;
	}
	
	
	@Override
	public void run() {
		
		final VideoPlayerComponent videoPlayer = controlsPanel.getVideoPlayer();
		final int position = (int) (videoPlayer.getMediaPlayer().getPosition() * 100);
		
		 SwingUtilities.invokeLater(new Runnable() {
             @Override
             public void run() {
                 if(videoPlayer.getMediaPlayer().isPlaying()) {
                    
                	updatePosition(position);
                     
                    int time = (Math.round(videoPlayer.getMediaPlayer().getTime()));
             		
                   	String timeString = controlsPanel.calculateTime(time);
                   	controlsPanel.setCurrentTime(timeString, time);
                 }
             }

			private void updatePosition(int position) {
				controlsPanel.getSlider().setValue(position);				
			}
         });
	}

}
