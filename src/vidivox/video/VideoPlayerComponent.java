package vidivox.video;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import uk.co.caprica.vlcj.player.MediaPlayer;

public class VideoPlayerComponent extends JPanel implements Player{
	private Player mediaPlayer;
	private EmbeddedVideoPlayer embeddedPlayer;
	private GridBagConstraints gbc;
	private String mediaPath;
	/**
	 * Instantiates a container which holds a video player
	 * @param embeddedPlayer the player embedded in the MainFrame
	 */
	public VideoPlayerComponent(EmbeddedVideoPlayer embeddedPlayer){
		this.embeddedPlayer=embeddedPlayer;
		this.mediaPlayer=embeddedPlayer;
		setLayout(new GridBagLayout());
		gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.weighty=1;
		add(embeddedPlayer, gbc);
	}
	
	@Override
	public void setPreferredSize(Dimension dim){
		embeddedPlayer.setPreferredSize(dim);
		super.setPreferredSize(dim);
	}
	
	/**
	 * Set the video player which is currently active and controlled by the controls
	 * @param player the active player
	 */
	public void setVideoPlayer(Player player){
		mediaPlayer.getMediaPlayer().pause();
		mediaPlayer.dispose();
		
		mediaPlayer=player;

		if(player instanceof EmbeddedVideoPlayer){
			removeAll();
			add((EmbeddedVideoPlayer)player,gbc);
		}

		if(player instanceof  DirectVideoPlayer){
			removeAll();
			add((DirectVideoPlayer)player,gbc);
		}

		if(mediaPath!=null) {
			mediaPlayer.playVideo(mediaPath);
		}
	}
	
	/**
	 * Get the instance of the player embedded in the MainFrame
	 * @return the main (embedded) player
	 */
	public EmbeddedVideoPlayer getEmbeddedPlayer(){
		return embeddedPlayer;
	}
	
	@Override
	public void playVideo(String path) {
		mediaPlayer.playVideo(path);
		mediaPath=path;
	}
	@Override
	public MediaPlayer getMediaPlayer() {
		return mediaPlayer.getMediaPlayer();
	}
	@Override
	public void dispose() {
		throw new RuntimeException("Not disposable");
	}
	
	
}
