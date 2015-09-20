import javax.swing.*;
import java.awt.*;


// Progress bar to 
public class ProgressDialog extends JDialog {
    private JLabel overallProgressLabel;
    private JProgressBar overallProgresBar;
    private JLabel taskProgressLabel;
    private JProgressBar taskProgressBar;
    private JButton cancelButton;

    public ProgressDialog(){
        setupLayout();
        setMinimumSize(new Dimension(300,130));
        pack();
        taskProgressBar.setIndeterminate(true);
    }

    private void setupLayout(){
        Container contentPane=getContentPane();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(5,5,5,5);
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=2;
        gbc.fill= GridBagConstraints.HORIZONTAL;
        gbc.anchor= GridBagConstraints.NORTH;
        overallProgressLabel=new JLabel("Overall Progress...");
        contentPane.add(overallProgressLabel,gbc);
        overallProgresBar=new JProgressBar();
        gbc.gridy++;
        contentPane.add(overallProgresBar,gbc);
        taskProgressLabel=new JLabel("Task Progress...");
        gbc.gridy++;
        contentPane.add(taskProgressLabel,gbc);
        taskProgressBar=new JProgressBar();
        gbc.gridy++;
        contentPane.add(taskProgressBar,gbc);
        gbc.gridy++;
        gbc.weighty=1.0f;
        contentPane.add(new JPanel(),gbc);
        gbc.gridy++;
        gbc.gridwidth=1;
        gbc.weighty=0.0f;
        gbc.weightx=1.0f;
        contentPane.add(new JPanel(),gbc);
        cancelButton=new JButton("Cancel");
        gbc.gridx++;
        gbc.weightx=0.0f;
        contentPane.add(cancelButton,gbc);
    }

    public void setOverallTotal(int total){
        overallProgresBar.setMaximum(total);
    }
    public void setOverallProgress(int progress){
        overallProgresBar.setValue(progress);
    }
    public void setTaskTotal(int total){
        taskProgressBar.setMaximum(total);
    }
    public void setTaskProgress(int progress){
        taskProgressBar.setValue(progress);
    }
    public void setOverallProgressNote(String note){
        overallProgressLabel.setText(note);
    }
    public void setTaskProgressNote(String note){
        taskProgressLabel.setText(note);
    }

    public void close(){
        dispose();
    }

}
