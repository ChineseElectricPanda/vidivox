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

public class Main {
	
	private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	
    public static void main(String[] args){
 
    	// Creating main frame
    	MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
		
		
		executorService.scheduleAtFixedRate(new UpdateRunnable(mainFrame.getControlsPanel()), 0L, 500L, TimeUnit.MILLISECONDS);
		
    }

}
