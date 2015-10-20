package vidivox.video;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurface;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapter;

import javax.print.attribute.standard.Media;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.lang.reflect.InvocationTargetException;

/**
 * This creates a JPanel containing a DirectVideoPlayer which can be drawn on as a canvas
 * Reference: https://github.com/caprica/vlcj/blob/master/src/test/java/uk/co/caprica/vlcj/test/direct/DirectTestPlayer.java
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 *
 */
public class DirectVideoPlayer extends JPanel implements Player {
	private int width=500, height=500;
	private DirectMediaPlayer mediaPlayer;
	private BufferedImage image;
	private MediaPlayerFactory factory;
	/**
	 * Constructor called whenever video player is instantiated
	 */
	public DirectVideoPlayer(int width, int height) throws InvocationTargetException, InterruptedException {
		this.width=width;
		this.height=height;
		
		image=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);
		image.setAccelerationPriority(1.0f);

		factory=new MediaPlayerFactory();

		mediaPlayer=factory.newDirectMediaPlayer(new BufferFormatCallback(), new RenderCallback());
	}

	// Method to play video
	public void playVideo(String path) {
		getMediaPlayer().playMedia(path);
	}

	public MediaPlayer getMediaPlayer(){
		return mediaPlayer;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.drawImage(image, null, 0, 0);
		// You could draw on top of the image here...
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g2.setColor(Color.red);
		g2.setComposite(AlphaComposite.SrcOver.derive(0.3f));
		g2.fillRoundRect(100, 100, 100, 80, 32, 32);
		g2.setComposite(AlphaComposite.Xor);
		g2.setColor(Color.white);
		g2.drawString("vlcj direct media player", 130, 150);
	}

	private class RenderCallback extends RenderCallbackAdapter{

		public RenderCallback() {
			super(((DataBufferInt) image.getRaster().getDataBuffer()).getData());
		}

		@Override
		protected void onDisplay(DirectMediaPlayer directMediaPlayer, int[] ints) {
			DirectVideoPlayer.this.repaint();
		}
	}

	private class BufferFormatCallback implements uk.co.caprica.vlcj.player.direct.BufferFormatCallback{

		@Override
		public BufferFormat getBufferFormat(int srcWidth, int srcHeight) {
			return new RV32BufferFormat(500,500);
		}
	}

	@Override
	public void dispose() {
		factory.release();
		mediaPlayer.release();
	}
}
