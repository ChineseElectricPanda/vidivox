package vidivox.audio;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import vidivox.exception.FileFormatException;
import vidivox.ui.AudioOverlaysDialog;
import vidivox.worker.AudioPlayWorker;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AudioOverlay {
    protected double startTime=0;
    protected int volume=100;
    protected CommentaryOverlay commentary;
    protected JTextField startTimeMinutesField;
    protected JTextField startTimeSecondsField;
    protected JTextField startTimeMillisecondsField;
    protected JLabel durationLabel;
    protected JButton playButton;
    protected JSlider volumeSlider;
    protected JLabel volumeLevelLabel;
    protected JCheckBox previewCheckBox;
    public boolean showPreview = false;
    
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
        gbc.insets=new Insets(0, 0, 0, 0);
        startTimeMinutesField=new JTextField();
        startTimeMinutesField.setHorizontalAlignment(JTextField.RIGHT);
        startTimeMinutesField.setPreferredSize(new Dimension(50,19));
        gbc.gridx++;
        propertiesPanel.add(startTimeMinutesField,gbc);
        gbc.gridx++;
        propertiesPanel.add(new JLabel(":"),gbc);
        startTimeSecondsField=new JTextField();
        startTimeSecondsField.setHorizontalAlignment(JTextField.RIGHT);
        startTimeSecondsField.setPreferredSize(new Dimension(25,19));
        gbc.gridx++;
        propertiesPanel.add(startTimeSecondsField,gbc);
        gbc.gridx++;
        propertiesPanel.add(new JLabel("."),gbc);
        startTimeMillisecondsField=new JTextField();
        startTimeMillisecondsField.setHorizontalAlignment(JTextField.RIGHT);
        startTimeMillisecondsField.setPreferredSize(new Dimension(35,19));
        gbc.gridx++;
        propertiesPanel.add(startTimeMillisecondsField,gbc);
        gbc.insets=new Insets(5, 5, 5, 5);
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
        previewCheckBox = new JCheckBox("Preview");
        gbc.gridx++;
        gbc.weightx=1.0f;
        propertiesPanel.add(previewCheckBox, gbc);
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

        final DocumentListener startTimeDocumentListener=new DocumentListener() {
        		@Override
        		public void removeUpdate(DocumentEvent e) {
        			changedUpdate(e);
        		}
        		
        		@Override
        		public void insertUpdate(DocumentEvent e) {
        			changedUpdate(e);
        		}
        		
        		@Override
        		public void changedUpdate(DocumentEvent e) {
        			int minutes=0,seconds=0,milliseconds=0;
        			try{
        				minutes=Integer.parseInt(startTimeMinutesField.getText());
        			}catch(NumberFormatException|NullPointerException ex){}
        			try{
        				seconds=Integer.parseInt(startTimeSecondsField.getText());
        			}catch(NumberFormatException|NullPointerException ex){}
        			try{
        				milliseconds=Integer.parseInt(startTimeMillisecondsField.getText());
        			}catch(NumberFormatException|NullPointerException ex){}
        			startTime=minutes*60+seconds+((float)milliseconds)/1000;
        			System.out.println(startTime);
        		}
        	};
        	
        	final FocusListener startTimeFocusListener=new FocusListener() {
        		@Override
        		public void focusLost(FocusEvent e) {
        			updateStartTimeFields();
        		}
        		
        		@Override
        		public void focusGained(FocusEvent e) {
        	
        		}
        	};
        	startTimeMinutesField.addFocusListener(startTimeFocusListener);
        	startTimeSecondsField.addFocusListener(startTimeFocusListener);
        	startTimeMillisecondsField.addFocusListener(startTimeFocusListener);
        	
        	startTimeMinutesField.getDocument().addDocumentListener(startTimeDocumentListener);
        	startTimeSecondsField.getDocument().addDocumentListener(startTimeDocumentListener);
        	startTimeMillisecondsField.getDocument().addDocumentListener(startTimeDocumentListener);
        	
        
        //set up the listeners
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                volume = slider.getValue();
                volumeLevelLabel.setText(volume + "%");
            }
        });
        
        previewCheckBox.addItemListener(new ItemListener () {
        	@Override
        	public void itemStateChanged(ItemEvent e) {
        		if (e.getStateChange() == ItemEvent.SELECTED) {
        			showPreview = true;
        		} else {
        			showPreview = false;
        		}
        	}
        });

        playActionListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
            	           	
            	ArrayList<CommentaryOverlay> overlays = (ArrayList<CommentaryOverlay>) AudioOverlaysDialog.commentaryOverlays;
            	for (int i = 0; i < overlays.size(); i++) {
            		if (overlays.get(i).text.length() >= 80) {
            			JOptionPane.showMessageDialog(null,
                    		    "Must specify comment less than or equal 80 characters",
                    		    "Error",
                    		    JOptionPane.ERROR_MESSAGE);
            			return;
            		} 
            	}
            	
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
        playButton.addActionListener(playActionListener);

        //set the values of the displays
        updateStartTimeFields();
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

        return contentPane;
    }

    public static AudioOverlay fromString(String s) throws FileFormatException {
        try {
            if (s.startsWith("F")) {
                return FileOverlay.fromString(s);
            } else if (s.startsWith("C")) {
                return CommentaryOverlay.fromString(s);
            }
        }catch(ArrayIndexOutOfBoundsException|NumberFormatException e){
            throw new FileFormatException();
        }
        throw new FileFormatException();
    }

    public String getProcessedFilePath() throws IOException, InterruptedException {
        System.out.println(Thread.currentThread().getName());
        if(!Thread.currentThread().getName().startsWith("SwingWorker")){
            System.err.println("BIG RED LETTERS!");
            throw new RuntimeException("This method must be called on a swingworker thread!");
        }
        if(startTime==0){
            return getFilePath();
        }
        //create a silent audio file equal to the length of the start time offset
        //(reference: http://superuser.com/questions/579008/add-1-second-of-silence-to-audio-through-ffmpeg)
        String silenceFilePath="/tmp/silence"+startTime+".mp3";
        Process process=new ProcessBuilder("/bin/bash","-c","ffmpeg -y -f lavfi -i aevalsrc=0:0:0:0:0:0::duration="+startTime+" "+silenceFilePath).start();
        System.out.println("ffmpeg -y -f lavfi -i aevalsrc=0:0:0:0:0:0::duration="+startTime+" "+silenceFilePath);
        process.waitFor();

        //concatenate the silence file before the actual file and adjust the volume
        //(reference: http://superuser.com/questions/587511/concatenate-multiple-wav-files-using-single-command-without-extra-file)
        String outFilePath="/tmp/"+getFileName()+".mp3";
        process=new ProcessBuilder("/bin/bash","-c","ffmpeg -y -i "+silenceFilePath+" -i "+getFilePath()+" -filter_complex '[0:0][1:0]concat=n=2:v=0:a=1[combined];[combined]volume="+(((float)volume)/100)+"[out]' -map '[out]' "+outFilePath).start();
        System.out.println("ffmpeg -y -i "+silenceFilePath+" -i "+getFilePath()+" -filter_complex '[0:0][1:0]concat=n=2:v=0:a=1[combined];[combined]volume="+(((float)volume)/100)+"[out]' -map '[out]' "+outFilePath);
        process.waitFor();
        return outFilePath;
    }

    abstract public String getFilePath();

    public String getFileName(){
        return getFilePath().split("/")[getFilePath().split("/").length-1];
    }

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
    
    private void updateStartTimeFields() {
    	int minutes=(int)(startTime/60);
    	int seconds=(int)(startTime%60);
    	int milliseconds=(int)((startTime-seconds-minutes*60)*1000);
    	startTimeMinutesField.setText(String.format("%02d", minutes));
    	startTimeSecondsField.setText(String.format("%02d", seconds));
    	startTimeMillisecondsField.setText(String.format("%03d", milliseconds));
    }
}
