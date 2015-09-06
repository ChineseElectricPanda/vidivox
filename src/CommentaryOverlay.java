import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;

public class CommentaryOverlay extends AudioOverlay{
    private String text;
    private String filePath;
    private SpeechSynthesisWorker synthesisWorker;
    private JTextField textField;

    public CommentaryOverlay(){
        this("",0);
    }

    public CommentaryOverlay(String text, float startTime){
        this.text=text;
        this.startTime=startTime;
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
                if (synthesisWorker != null && !synthesisWorker.isDone()) {
                    synthesisWorker.kill();
                }
                synthesisWorker = new SpeechSynthesisWorker(text) {
                    @Override
                    protected void done() {
                        super.done();
                        playButton.setEnabled(true);
                        CommentaryOverlay.this.filePath=filePath;
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
        });
        return contentPane;
    }

    @Override
    protected String getFilePath() {
        return filePath;
    }

}