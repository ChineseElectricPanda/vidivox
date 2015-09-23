package vidivox;
import java.awt.Canvas;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import vidivox.ui.MainFrame;

/**
 * This is main class containing the main method to run the vidivox application
 * It is also used to call the UpdateRunnable class which updates the JSlider used
 * to inform the user the time at which the video is currently playing
 * 
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 *
 */
public class Main {
	
	/** Creating a ScheduledExecutorService to call a specific update class at given intervals
	*/
	private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	
	/**
	 * Main method for the vidivox application
	 * @param args
	 */
    public static void main(String[] args){
 
    	// Creating the main frame which contains functionality for opening a video, playing
    	// the video etc.
    	MainFrame mainFrame = new MainFrame();
    	
    	// Setting the visibility of the frame to true
		mainFrame.setVisible(true);
		
		// Instantiating a UpdateRunnable class to update specific components every 500 milliseconds
		executorService.scheduleAtFixedRate(new UpdateRunnable(mainFrame.getControlsPanel()), 0L, 500L, TimeUnit.MILLISECONDS);
    }
}
