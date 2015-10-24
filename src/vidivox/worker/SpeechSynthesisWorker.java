package vidivox.worker;
import javax.swing.*;

import vidivox.audio.CommentaryOverlay;
import vidivox.audio.CommentaryOverlay.Emotion;
import vidivox.audio.CommentaryOverlay.Voice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
    public SpeechSynthesisWorker(String text, String fileName, float timeScale, CommentaryOverlay.Voice voice, CommentaryOverlay.Emotion emotion) {
        this.text = text;
        // Creating file path to the wav file
        filePath = "/tmp/" + fileName;
        int startPitch, endPitch;
        // Create a scheme file to set voice emotion and timeScale
        if(voice==Voice.FEMALE){
        	startPitch=200;
        	endPitch=165;
        }else{
        	startPitch=120;
        	endPitch=105;
        }
        
        if(emotion==Emotion.HAPPY){
        	startPitch+=15;
        	endPitch+=15;
        	timeScale*=0.8;
        }else if(emotion==Emotion.SAD){
        	startPitch-=25;
        	endPitch-=15;
        	timeScale/=0.8;
        }
        
        StringBuilder scm=new StringBuilder();
        scm.append("(Parameter.set 'Duration_Stretch "+timeScale+")");
        scm.append("(set! duffint_params '((start "+startPitch+") (end "+endPitch+")))");
        scm.append("(Parameter.set 'Int_Method 'DuffInt)");
        scm.append("(Parameter.set 'Int_Target_Method Int_Targets_Default)");
        
        try {
			BufferedWriter writer=new BufferedWriter(new FileWriter(new File("/tmp/festival.scm")));
			writer.write(scm.toString());
			writer.close();
		} catch (IOException e) {
			
		}
        
        
        // Creating process to convert text file into the audio file
        try {
            synthesisProcess = new ProcessBuilder("/bin/bash", "-c", 
            									  "echo \"" + text + "\" | text2wave -o \"" + filePath+"\" -eval "+"/tmp/festival.scm").start();
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
