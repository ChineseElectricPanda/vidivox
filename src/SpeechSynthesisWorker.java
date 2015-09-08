import javax.swing.*;
import java.io.IOException;

public class SpeechSynthesisWorker extends SwingWorker<Void,Void>{
    protected String text;
    protected Process synthesisProcess;
    protected String filePath;

    public SpeechSynthesisWorker(String text,String fileName){
        this.text=text;
        filePath="/tmp/"+fileName;
        try {
            synthesisProcess=new ProcessBuilder("/bin/bash","-c","echo \""+text+"\" | text2wave -o "+filePath).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected Void doInBackground() throws Exception {
        synthesisProcess.waitFor();
        return null;
    }
    public void kill(){
        synthesisProcess.destroy();
        cancel(true);
    }
}
