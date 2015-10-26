package vidivox.worker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import vidivox.audio.AudioOverlay;
import vidivox.audio.CommentaryOverlay.Voice;
import vidivox.ui.MainFrame;
import vidivox.ui.dialog.AudioOverlaysDialog;
import vidivox.ui.dialog.ProgressDialog;

public class VideoExportWorker extends SwingWorker<Void, Integer>{

	private String videoPath;
	private String outputPath;
	private float videoDuration;
	private ProgressDialog progressDialog;
	private Process process; 
	
	public VideoExportWorker(String videoPath, String outputPath, float videoDuration){
		this.videoPath=videoPath;
		this.outputPath=outputPath;
		this.videoDuration=videoDuration;
		
		// Creating progress dialog to show the user the progress of the export as it takes
    	// some time
		progressDialog = new ProgressDialog();
        progressDialog.setVisible(true);
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		List<AudioOverlay> overlays=AudioOverlaysDialog.getInstance().getOverlays();
		// Processing all of the audio files
		List<String> audioPaths = new ArrayList<>();
		for (AudioOverlay overlay : overlays) {
			// Adding the file path of the audio files to the arraylist containing 
			// the paths of all audio files
			audioPaths.add(overlay.getProcessedFilePath());
		}
		
		// Building the command to pass into the process builder
		StringBuilder cmd = new StringBuilder("ffmpeg -y -i \"" + videoPath+"\"");
		// Appending the audio path of each audio file to the command
		// Keep count of the number of audio files added
		int audioTracksAdded=0;
		for (int i=0;i<audioPaths.size();i++) {
			// Only add audio files which aren't empty
			if(audioPaths.get(i)!=null && !audioPaths.get(i).isEmpty()){
				cmd.append(" -itsoffset "+overlays.get(i).getStartTime()+" -i \"" + audioPaths.get(i)+"\"");
				audioTracksAdded++;
			}
		}
		
		// If audio tracks have been added then append the command to mix them
		if(audioTracksAdded>0){
			for(int i=0;i<audioTracksAdded+1;i++){
				cmd.append(" -map "+i+":0");
			}
			cmd.append(" -async 1 -filter_complex amix=inputs="+(audioTracksAdded+1));
		}

        // End the output when the video ends
        cmd.append(" -t "+videoDuration);
		
        // Append the option to fix volume levels
        cmd.append(" -ac 2");
        
        // Keep the video codec
        cmd.append(" -c:v copy");
        
		// Append the option to allow ffmpeg to use support more formats, then append the output file path
		cmd.append(" -strict -2 \"" + outputPath + "\"");
		
		// Building process and process builder to run the command then starting it
		process = new ProcessBuilder("/bin/bash", "-c", cmd.toString()).start();

        //print the command that was executed (for debug)
		System.out.println(cmd.toString());
		
		// Causes the current thread to wait if necessary
		process.waitFor();
		
		return null;
	}
	
	@Override
	protected void done(){
		// Closing the progress dialog indicating how long merging video and audio will take
		progressDialog.close();
		//Show a success or failure message for the export process
		if(process.exitValue()==0){
			JOptionPane.showMessageDialog(MainFrame.getInstance(), "Video sucessfully exported as "+videoPath,"Success!",JOptionPane.INFORMATION_MESSAGE);
		}else{
			JOptionPane.showMessageDialog(MainFrame.getInstance(), "Failed to export video. Error code "+process.exitValue(),"Failure",JOptionPane.ERROR_MESSAGE);
		}
	}
}
