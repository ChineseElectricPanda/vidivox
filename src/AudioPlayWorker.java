import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Field;

public class AudioPlayWorker extends SwingWorker<Void,Void>{
    private Process playerProcess;
    private String filePath;
    private int volume;
    public AudioPlayWorker(String filePath, int volume){
        this.filePath=filePath;
        this.volume=volume;
    }
    @Override
    protected Void doInBackground() throws Exception {
        playerProcess=new ProcessBuilder("/bin/bash","-c","ffplay -nodisp -autoexit -af volume="+(((float)volume)/100)+" "+filePath).start();
        playerProcess.waitFor();
        return null;
    }
    public void kill(){
        playerProcess.destroy();
        cancel(true);
    }
}
