import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;

public abstract class AudioOverlay {
    protected float startTime=0;
    protected int volume=100;

    protected JTextField startTimeField;
    protected JLabel durationLabel;
    protected JButton playButton;
    protected JSlider volumeSlider;
    protected JLabel volumeLevelLabel;

    private ActionListener playActionListener;
    private ActionListener stopActionListener;
    private AudioPlayWorker audioPlayWorker;

    public JPanel getComponentView(){
        JPanel contentPane=new JPanel();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(5,5,5,5);
        gbc.fill= GridBagConstraints.HORIZONTAL;

        //set up the properties controls layout
        JPanel propertiesPanel=new JPanel();
        propertiesPanel.setLayout(new GridBagLayout());
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=1;
        gbc.weightx=0.0f;
        propertiesPanel.add(new JLabel("Start Time"),gbc);
        startTimeField=new JTextField();
        startTimeField.setPreferredSize(new Dimension(50,19));
        gbc.gridx++;
        gbc.weightx=1.0f;
        propertiesPanel.add(startTimeField,gbc);
        gbc.gridx++;
        gbc.weightx=0;
        propertiesPanel.add(new JLabel("Duration"),gbc);
        durationLabel=new JLabel("00:00");
        durationLabel.setPreferredSize(new Dimension(100,24));
        gbc.gridx++;
        gbc.weightx=0;
        propertiesPanel.add(durationLabel,gbc);
        playButton=new JButton("Play");
        playButton.setEnabled(false);
        gbc.gridx++;
        propertiesPanel.add(playButton,gbc);
        gbc.gridx++;
        gbc.weightx=1.0f;
        propertiesPanel.add(new JPanel(),gbc);
        gbc.gridx++;
        gbc.weightx=0;
        propertiesPanel.add(new JLabel("Volume"),gbc);
        volumeSlider=new JSlider();
        volumeSlider.setPreferredSize(new Dimension(100, 25));
        volumeSlider.setMinimum(0);
        volumeSlider.setMaximum(100);
        volumeSlider.setValue(100);
        gbc.gridx++;
        propertiesPanel.add(volumeSlider,gbc);
        volumeLevelLabel=new JLabel("100%");
        volumeLevelLabel.setPreferredSize(new Dimension(50,24));
        gbc.gridx++;
        propertiesPanel.add(volumeLevelLabel, gbc);
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.weightx=1.0f;
        gbc.fill= GridBagConstraints.HORIZONTAL;
        contentPane.add(propertiesPanel, gbc);

        //set up the listeners
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                volume = slider.getValue();
                volumeLevelLabel.setText(volume + "%");
            }
        });
        startTimeField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {

            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {

            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {

            }
        });
        startTimeField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {

            }

            @Override
            public void focusLost(FocusEvent focusEvent) {

            }
        });

        playActionListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //turn the button into a start button
                playButton.setText("Stop");
                playButton.removeActionListener(this);
                playButton.addActionListener(stopActionListener);

                //start the process
                audioPlayWorker=new AudioPlayWorker(getFilePath(),volume){
                    @Override
                    protected void done() {
                        super.done();
                        //turn the button back into a play button when it finishes playing
                        playButton.setText("Play");
                        playButton.removeActionListener(stopActionListener);
                        playButton.addActionListener(playActionListener);
                        audioPlayWorker=null;
                    }
                };
                audioPlayWorker.execute();
            }
        };
        stopActionListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //kill the playing process
                audioPlayWorker.kill();
            }
        };

        //set the values of the displays
        startTimeField.setText(startTime+"");
        volumeSlider.setValue(volume);
        if(getFilePath()!=null) {
            try {
                float duration = getDuration(getFilePath());
                String durationText = (int) (duration / 60) + ":" + (int) (duration % 60) + "." + (int) ((duration - (int) duration)*1000);
                durationLabel.setText(durationText);
            } catch (InterruptedException | IOException e) {
                durationLabel.setText("????");
            }
        }
        if(getFilePath()!=null){
        	playButton.setEnabled(true);
        }
        if(audioPlayWorker!=null){
        	playButton.setText("Stop");
            playButton.addActionListener(stopActionListener);
        }else{
        	playButton.addActionListener(playActionListener);
        }

        return contentPane;
    }
    
    public void stop(){
    	if(audioPlayWorker!=null){
    		audioPlayWorker.kill();
    	}
    }

    abstract protected String getFilePath();

    protected float getDuration(String filePath) throws IOException, InterruptedException {
        Process ffProbeProcess=new ProcessBuilder("/bin/bash","-c","ffprobe -i "+filePath+" -show_entries format=duration 2>&1 | grep \"duration=\"").start();
        BufferedReader reader=new BufferedReader(new InputStreamReader(ffProbeProcess.getInputStream()));
        ffProbeProcess.waitFor();
        String durationLine=reader.readLine();
        if(durationLine==null){
            return 0;
        }else {
            return Float.parseFloat(durationLine.split("=")[1]);
        }
    }

    protected float getDuration(File file) throws IOException, InterruptedException {
        return getDuration(file.getAbsolutePath());
    }
}
