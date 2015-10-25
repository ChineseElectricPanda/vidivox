package vidivox.ui.dialog;
import javax.swing.*;
import java.awt.*;


/**
 * This class is used to show the progress when the user wants to export their project as
 * a single video file. The progress bar updates as the export process progresses
 * 
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 *
 */
public class ProgressDialog extends JDialog {
	
	/**
	 * Declaring fields to be used globally within class
	 */
    private JLabel overallProgressLabel; 		// Label indicating to user the overall progress
    private JProgressBar overallProgresBar;		// Progress bar showing overall progress of export
    private JLabel taskProgressLabel;			// Label indicating to user the progress of a specific task
    private JProgressBar taskProgressBar;		// Progress bar showing the progress of a specific task
    private JButton cancelButton;				// Button to allow user to cancel the export

    /**
     * Constructor to set up the layout of the progress bar
     */
    public ProgressDialog () {
        setupLayout();
        setMinimumSize(new Dimension(300,130));
        pack();
        taskProgressBar.setIndeterminate(true);
    }

    /**
     * Method called to set up the layout of the progress bar through the use
     * of the grid bag layout
     */
    private void setupLayout() {
    	
    	// Obtaining the content pane
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        // Adding the overall progress label and bar to the content pane
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        // Adding the progress label
        overallProgressLabel = new JLabel("Overall Progress...");
        contentPane.add(overallProgressLabel,gbc);
        // Adding the progress bar
        overallProgresBar = new JProgressBar();
        gbc.gridy++;
        contentPane.add(overallProgresBar,gbc);
        
        // Adding the specific task progress bar and label to the content pane
        taskProgressLabel = new JLabel("Task Progress...");
        gbc.gridy++;
        contentPane.add(taskProgressLabel,gbc);
        taskProgressBar = new JProgressBar();
        gbc.gridy++;
        contentPane.add(taskProgressBar,gbc);
        
        // Adding empty panels to allow for space between GUI elements
        gbc.gridy++;
        gbc.weighty = 1.0f;
        contentPane.add(new JPanel(),gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weighty = 0.0f;
        gbc.weightx = 1.0f;
        contentPane.add(new JPanel(),gbc);
        
        // Adding the cancel button to allow user to cancel the export
        cancelButton = new JButton("Cancel");
        gbc.gridx++;
        gbc.weightx=0.0f;
        contentPane.add(cancelButton,gbc);
    }

    /**
     * Method to set the maximum value of the progress bar
     * @param total - highest progress that can be achieved
     */
    public void setOverallTotal(int total) {
        overallProgresBar.setMaximum(total);
    }
    
    /**
     * Method to set the actual progress of the progress bar
     * @param progress - the current level of progress
     */
    public void setOverallProgress(int progress){
        overallProgresBar.setValue(progress);
    }
    
    /**
     * Method to set the maximum value of the task bar
     * @param total - highest progress that can be achieved
     */
    public void setTaskTotal(int total){
        taskProgressBar.setMaximum(total);
    }
    
    /**
     * Method to set the actual progress of the task specific bar
     * @param progress the progress of the task
     */
    public void setTaskProgress(int progress){
        taskProgressBar.setValue(progress);
    }
    
    /**
     * Method to set the text in the label indicating the overall progress
     * @param note - string describing progress
     */
    public void setOverallProgressNote(String note){
        overallProgressLabel.setText(note);
    }
    
    /**
     * Method to set the text in the label indicating the progress of a certain task
     * @param note - string describing progress
     */
    public void setTaskProgressNote(String note){
        taskProgressLabel.setText(note);
    }

    /**
     * Method called to close the progress dialog instance
     */
    public void close(){
        dispose();
    }

}
