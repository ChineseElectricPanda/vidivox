package vidivox.video;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

/**
 * This class serves as an extension of the media player component from vlcj
 * 
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 *
 */
public class EmbeddedVideoPlayer extends EmbeddedMediaPlayerComponent implements Player{

	/**
	 * Constructor called whenever video player is instantiated
	 */
	public EmbeddedVideoPlayer() {
		// Setting the volume to the maximum possible
		getMediaPlayer().setVolume(100);
	}

	// Method to play video
	public void playVideo(String path) {
		getMediaPlayer().playMedia(path);
	}
	
	@Override
	public void dispose(){
		getMediaPlayer().release();
	}
}
