package vidivox.worker;
import vidivox.audio.AudioOverlay;

import javax.swing.*;

/**
 * This class extends swing worker and thus inherits its methods in order to create a background
 * task which doesn't freeze the GUI. The task this class carries out is that of playing the
 * wav file created by the user when they entered their comment. It plays the wav file and overlays
 * on top of other audio
 * 
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 *
 */
public class AudioPlayWorker extends SwingWorker<Void,Void>{
	
	/**
	 * Declaring fields to allow access within class
	 */
    private Process playerProcess;		// Process which plays the audio
    private String filePath;			// Path to the wav file
    private int volume;					// Volume of the audio
    private double startTimeOffset;		// Time offset at the start of the audio

    /**
     * Constructor to initialize fields when created
     * 
     * @param filePath - string specifying the path to the file
     * @param volume - the volume of the audio represented as an integer
     */
    public AudioPlayWorker(String filePath, int volume) {
        this(filePath, volume, 0);
    }

    /**
     * Constructor used to call another constructor while getting important info from fields
     * 
     * @param overlay - field of type AudioOverlay containing important audio formatting components
     * @param currentTime - double containing the current time
     */
    public AudioPlayWorker(AudioOverlay overlay, double currentTime) {
        this(overlay.getFilePath(), overlay.getVolume(), overlay.getStartTime()-currentTime);
    }

    /**
     * Constructor called to initialize fields in the class
     * 
     * @param filePath - the path to the wav file
     * @param volume - the volume of the audio
     * @param startTimeOffset  - the amount of time after which the audio should play
     */
    public AudioPlayWorker(String filePath, int volume, double startTimeOffset){
        this.filePath = filePath;
        this.volume = volume;
        this.startTimeOffset = startTimeOffset;
    }
    
    /**
     * This method does a time consuming task of playing the audio in the background so as to stop
     * the GUI from freezing
     */
    @Override
    protected Void doInBackground() throws Exception {
    	
    	// Checking if the user specified a time other than the start to play the audio
        if (startTimeOffset > 0) {
        	// Sleeping the thread for the duration as specified by the user
            Thread.sleep((long) (startTimeOffset *1000));
            // Building the process to play the audio and starting it
            playerProcess = new ProcessBuilder("/bin/bash","-c",
            		"ffplay -nodisp -autoexit -af volume=" + (((float)volume)/100) + " \"" + filePath+"\"").start();
        } else {
        	// Building the process to play the audio and starting it
            playerProcess=new ProcessBuilder("/bin/bash","-c","ffplay -nodisp -autoexit -af volume="+(((float)volume)/100)+" -ss "+(-startTimeOffset)+" \""+filePath+"\"").start();
        }
        
        playerProcess.waitFor();
        return null;
    }
    
    /**
     * This method is invoked when the user wants the audio to stop playing and it kills the process
     * that is running the audio
     */
    public void kill() {
    	// Checking if the process is existing or not to avoid exceptions being thrown
        if (playerProcess != null) {
        	// Destroying the process playing a audio file
            playerProcess.destroy();
        }
        cancel(true);
    }
}
