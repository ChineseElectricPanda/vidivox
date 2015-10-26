package vidivox.ui.dialog;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;


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
    private JButton cancelButton;				// Button to allow user to cancel the export

    /**
     * Constructor to set up the layout of the progress bar
     */
    public ProgressDialog () {
    	setTitle("Exporting...");
        setupLayout();
        setMinimumSize(new Dimension(300,70));
        pack();
        overallProgresBar.setIndeterminate(true);
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
     * Add an ActionListener to the cancel button
     * @param l the ActionListener to add
     */
    public void addCancelListener(ActionListener l){
    	cancelButton.addActionListener(l);
    }

    /**
     * Method called to close the progress dialog instance
     */
    public void close(){
        dispose();
    }

}
