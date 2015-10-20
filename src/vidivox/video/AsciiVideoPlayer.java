package vidivox.video;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

public class AsciiVideoPlayer implements Playable{
	private MediaPlayerFactory factory;
	private MediaPlayer mediaPlayer;
	public AsciiVideoPlayer(){
		factory=new MediaPlayerFactory("--vout=caca");
		mediaPlayer=factory.newEmbeddedMediaPlayer();
	}
	
	@Override
	public void playVideo(String path) {
		mediaPlayer.playMedia(path);
	}

	@Override
	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	@Override
	public void dispose() {
		mediaPlayer.stop();
		mediaPlayer.release();
		factory.release();
	}
	

}
