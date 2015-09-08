import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class FileOverlay extends AudioOverlay{
    private File selectedFile;
    private JLabel fileNameLabel;
    private JButton fileSelectorButton;

    public FileOverlay(){
        this(null,0,100);
    }

    public FileOverlay(String filePath,float startTime,int volume){
        if(filePath!=null) {
            this.selectedFile = new File(filePath);
        }
        this.startTime=startTime;
        this.volume=volume;
    }

    @Override
    public JPanel getComponentView() {
        final JPanel contentPane=super.getComponentView();
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(5,5,5,5);

        JPanel fileSelectorPanel=new JPanel();
        fileSelectorPanel.setLayout(new GridBagLayout());

        fileNameLabel=new JLabel("[no file selected]");
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=1.0f;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.anchor= GridBagConstraints.NORTH;
        fileSelectorPanel.add(fileNameLabel, gbc);
        fileSelectorButton=new JButton("...");
        gbc.gridx=1;
        gbc.weightx=0.0f;
        fileSelectorPanel.add(fileSelectorButton,gbc);

        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=1.0f;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.anchor= GridBagConstraints.NORTH;
        contentPane.add(fileSelectorPanel,gbc);

        fileSelectorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                if (chooser.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
                    selectedFile = chooser.getSelectedFile();
                    //TODO check if file is acutally audio (or maybe video). Use file -i [filename] | grep "audio"
                    if (selectedFile != null) {
                        playButton.setEnabled(true);
                        fileNameLabel.setText(selectedFile.getName());
                        try {
                            float duration = getDuration(selectedFile);
                            String durationText = (int) (duration / 60) + ":" + (int) (duration % 60) + "." + (int) ((duration - (int) duration)*1000);
                            durationLabel.setText(durationText);
                        } catch (InterruptedException | IOException e) {
                            durationLabel.setText("????");
                        }
                    }else{
                        playButton.setEnabled(false);
                    }
                }
            }
        });
        if(selectedFile!=null){
            fileNameLabel.setText(selectedFile.getName());
            playButton.setEnabled(true);
        }
        return contentPane;
    }

    @Override
    public String toString(){
        String s="F\t"+getFilePath()+"\t"+startTime+"\t"+volume;
        return s;
    }

    public static FileOverlay fromString(String s){
        String filePath=s.split("\\t")[1];
        float startTime=Float.parseFloat(s.split("\\t")[2]);
        int volume=Integer.parseInt(s.split("\\t")[3]);
        return new FileOverlay(filePath,startTime,volume);
    }

    @Override
    protected String getFilePath() {
        if(selectedFile==null){
            return "";
        }else {
            return selectedFile.getAbsolutePath();
        }
    }
}
