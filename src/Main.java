import java.awt.Canvas;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class Main {
	
    public static void main(String[] args){
 
    	// Creating main frame
    	MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);

    }

}
