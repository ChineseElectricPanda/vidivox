package vidivox.audio;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import vidivox.worker.SpeechSynthesisWorker;

import java.awt.*;
import java.io.IOException;

public class CommentaryOverlay extends AudioOverlay{
    public String text;
    private String filePath;
    private int position;
    private SpeechSynthesisWorker synthesisWorker;
    private JTextField textField;

    public CommentaryOverlay(int position){
        this(position,"",0,100);
    }

    public CommentaryOverlay(int position, String text, float startTime,int volume){
        this.position=position;
        this.text=text;
        this.startTime=startTime;
        this.volume=volume;
        if(text!=null && !text.isEmpty()){
        	new SpeechSynthesisWorker(text,"commentary"+position){
        		@Override
        		protected void done() {
	        		super.done();
	        		CommentaryOverlay.this.filePath=filePath;
        		}
        	}.execute();
        }
    }
    
    public int getPosition() {
    	return position;
    }
    
    public String getText() {
    	return text;
    }

    public JPanel getComponentView() {
        JPanel contentPane=super.getComponentView();
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(5,5,5,5);

        textField=new JTextField();
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=1.0f;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.anchor= GridBagConstraints.NORTH;
        contentPane.add(textField, gbc);

        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                changedUpdate(documentEvent);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                changedUpdate(documentEvent);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                text = textField.getText();

                if (text.length() > 20) {
                	JOptionPane.showMessageDialog(null,
                		    "Must specify comment less than or equal 20 characters",
                		    "Error",
                		    JOptionPane.ERROR_MESSAGE);
                } else {
                
                	if (synthesisWorker != null && !synthesisWorker.isDone()) {
                		synthesisWorker.kill();
                	}
                	synthesisWorker = new SpeechSynthesisWorker(text,"commentary"+position) {
                		@Override
                		protected void done() {
                			super.done();
                			playButton.setEnabled(true);
                			CommentaryOverlay.this.filePath = synthesisWorker.filePath;
                			try {
                				float duration = getDuration(filePath);
                				String durationText = (int) (duration / 60) + ":" + (int) (duration % 60) + "." + (int) ((duration - (int) duration)*1000);
                				durationLabel.setText(durationText);
                			} catch (IOException | InterruptedException e) {
                				durationLabel.setText("???");
                			}
                		}
                	};
                	synthesisWorker.execute();
                }
            }
        });
        if(text!=null){
            textField.setText(text);
        }
        return contentPane;
    }

    @Override
    public String toString(){
        String s="C\t"+position+"\t"+text+"\t"+startTime+"\t"+volume;
        return s;
    }

    public static CommentaryOverlay fromString(String s){
        int position=Integer.parseInt(s.split("\\t")[1]);
        String text=s.split("\\t")[2];
        float startTime=Float.parseFloat(s.split("\\t")[3]);
        int volume=Integer.parseInt(s.split("\\t")[4]);

        return new CommentaryOverlay(position,text,startTime,volume);
    }

    @Override
	public String getFilePath() {
        return filePath;
    }

}