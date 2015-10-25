package vidivox;
import javax.swing.UIManager;

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
	/**
	 * Main method for the vidivox application
	 * @param args command line arguments (not used)
	 */
    public static void main(String[] args){
    	// Set the look and feel to the Linux theme
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Set the sliders to not display the percent values
            UIManager.put("Slider.paintValue", false);
        }
        catch(Exception e) {
        	System.err.println("Failed to set look and feel");
        }
 
    	// Creating the main frame which contains functionality for opening a video, playing
    	// the video etc.
    	MainFrame mainFrame = MainFrame.getInstance();
    	
    	// Setting the visibility of the frame to true
		mainFrame.setVisible(true);
    }
}
