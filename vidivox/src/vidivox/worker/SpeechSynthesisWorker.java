package vidivox.worker;
import javax.swing.*;
import java.io.IOException;

/**
 * This class extends swing worker in order to perform a time consuming task of converting 
 * text into a wav file by running the conversion process in the background
 * 
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 *
 */
public class SpeechSynthesisWorker extends SwingWorker<Void,Void> {
	
	/**
	 * Fields declared to be used within class and package
	 */
    protected String text;					// Text that is to be converted to audio
    protected Process synthesisProcess;		// Process which will convert text file to wav file
    public String filePath;					// Path to the file 

    /**
     * Constructor called when this class is instantiated, allowing the fields declared to be set
     * and also creating the process and starting the conversion of the text into the wav file
     * 
     * @param text - Text to be created into audio
     * @param fileName - Name of the text file
     */
    public SpeechSynthesisWorker(String text, String fileName) {
        this.text = text;
        // Creating file path to the wav file
        filePath = "/tmp/" + fileName;
        
        // Creating process to convert text file into the audio file
        try {
            synthesisProcess = new ProcessBuilder("/bin/bash", "-c", 
            									  "echo \"" + text + "\" | text2wave -o " + filePath).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Method which carries out time consuming task, in this case waiting for the another command
     * to the process
     */
    @Override
    protected Void doInBackground() throws Exception {
        synthesisProcess.waitFor();
        return null;
    }
    
    /**
     * Method to stop the text to audio conversion process and destroy it
     */
    public void kill() {
        synthesisProcess.destroy();
        cancel(true);
    }
}
