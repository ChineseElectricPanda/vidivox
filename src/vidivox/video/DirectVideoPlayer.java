package vidivox.video;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;
import vidivox.shape.Ellipse;
import vidivox.shape.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ArrayList;
import vidivox.shape.Shape;

/**
 * This creates a JPanel containing a DirectVideoPlayer which can be drawn on as a canvas
 * Reference: https://github.com/caprica/vlcj/blob/master/src/test/java/uk/co/caprica/vlcj/test/direct/DirectTestPlayer.java
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 *
 */
public class DirectVideoPlayer extends JPanel implements Playable {
	private int width, height;
	private DirectMediaPlayer mediaPlayer;
	private BufferedImage image;
	private MediaPlayerFactory factory;

	private List<Shape> shapes =new ArrayList<>();
	/**
	 * Constructor called whenever video player is instantiated
	 */
	public DirectVideoPlayer(int width, int height) throws InvocationTargetException, InterruptedException {
		this.width=width;
		this.height=height;
		
		image= GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);
		image.setAccelerationPriority(1.0f);

		factory=new MediaPlayerFactory();

		mediaPlayer=factory.newDirectMediaPlayer(new BufferFormatCallback(), new RenderCallback());

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(e.getButton()==MouseEvent.BUTTON1) {
					shapes.add(new Rectangle(e.getX(), e.getY()));
				}else if(e.getButton()==MouseEvent.BUTTON3){
					shapes.add(new Ellipse(e.getX(), e.getY()));
				}
			}
		});
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
		//draw the rendered image
		g2.drawImage(image, null, 0, 0);

		g2.setXORMode(new Color(100,100,100));
		for(Shape shape:shapes){
			shape.move(width,height);
			shape.draw(g2);
		}

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
			return new RV32BufferFormat(width,height);
		}
	}

	@Override
	public void dispose() {
		factory.release();
		mediaPlayer.release();
	}
}
