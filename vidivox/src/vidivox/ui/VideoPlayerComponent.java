package vidivox.ui;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

public class VideoPlayerComponent extends EmbeddedMediaPlayerComponent {

	public VideoPlayerComponent(){
		getMediaPlayer().setVolume(100);
	}

	// Method to play video
	public void playVideo(String path) {
		
		getMediaPlayer().playMedia(path);
		
	}

	
}
