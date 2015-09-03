import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class VideoPlayerComponent extends EmbeddedMediaPlayerComponent {
    EmbeddedMediaPlayer player;
    public VideoPlayerComponent(){
        this.player=getMediaPlayer();
    }

    public void play(){
        player.playMedia("/home/hamcake/aiuraop.mkv");
    }
}