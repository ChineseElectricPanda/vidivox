package vidivox.video;

import uk.co.caprica.vlcj.player.MediaPlayer;

public interface Player {
	public void playVideo(String path);
	public MediaPlayer getMediaPlayer();
	public void dispose();
}
