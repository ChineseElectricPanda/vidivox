package vidivox.worker;
import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Field;

public class AudioPlayWorker extends SwingWorker<Void,Void>{
    private Process playerProcess;
    private String filePath;
    private int volume;
    int pid;
    public AudioPlayWorker(String filePath, int volume){
        this.filePath=filePath;
        System.out.println(filePath);
        this.volume=volume;
    }
    @Override
    protected Void doInBackground() throws Exception {
        playerProcess=new ProcessBuilder("/bin/bash","-c","ffplay -nodisp -autoexit -af volume="+(((float)volume)/100)+" "+filePath).start();
       
        Field f = playerProcess.getClass().getDeclaredField("pid");
        f.setAccessible(true);
        pid = f.getInt(playerProcess);
        playerProcess.waitFor();
        return null;
    }
    public void kill(){
        playerProcess.destroy();
        cancel(true);
    }
    
    public void pause() throws IOException, InterruptedException {
    	Process pauseProcess = new ProcessBuilder("/bin/bash","-c","kill -STOP " + pid).start();
    	pauseProcess.waitFor();
    }
    
    public void continueProcess() throws IOException, InterruptedException {
    	Process contProcess = new ProcessBuilder("/bin/bash","-c","kill -CONT " + pid).start();
    	contProcess.waitFor();
    }
}
