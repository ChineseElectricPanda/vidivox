package vidivox.ui;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

public class VideoPlayerComponent extends EmbeddedMediaPlayerComponent {
		
	// Method to play video
	public void playVideo(String path) {
		
		getMediaPlayer().playMedia(path);
		
	}

	
}
