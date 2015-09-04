import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

public class VideoPlayerComponent extends EmbeddedMediaPlayerComponent {
		
	// Method to play video
	public void playVideo(VideoPlayerComponent videoPlayer, String path) {
		
		videoPlayer.getMediaPlayer().playMedia(path);
		
	}

	
}
