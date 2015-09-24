package vidivox.worker;
import vidivox.audio.AudioOverlay;

import javax.swing.*;

public class AudioPlayWorker extends SwingWorker<Void,Void>{
    private Process playerProcess;
    private String filePath;
    private int volume;
    private double startTimeOffset;
//    int pid;

    public AudioPlayWorker(String filePath, int volume){
        this(filePath,volume,0);
    }

    public AudioPlayWorker(AudioOverlay overlay, double currentTime){
        this(overlay.getFilePath(),overlay.getVolume(),overlay.getStartTime()-currentTime);
    }

    public AudioPlayWorker(String filePath, int volume,double startTimeOffset){
        this.filePath=filePath;
        this.volume=volume;
        this.startTimeOffset = startTimeOffset;
    }
    @Override
    protected Void doInBackground() throws Exception {
        if(startTimeOffset >0) {
            Thread.sleep((long) (startTimeOffset *1000));
            playerProcess=new ProcessBuilder("/bin/bash","-c","ffplay -nodisp -autoexit -af volume="+(((float)volume)/100)+" "+filePath).start();
        }else{
            playerProcess=new ProcessBuilder("/bin/bash","-c","ffplay -nodisp -autoexit -af volume="+(((float)volume)/100)+" -ss "+(-startTimeOffset)+" "+filePath).start();
        }
        playerProcess.waitFor();
        return null;
    }
    public void kill(){
        if(playerProcess!=null) {
            playerProcess.destroy();
        }
        cancel(true);
    }
}
