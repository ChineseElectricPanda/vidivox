package vidivox.audio;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This class is instantiated when the user wants to add an audio file to the project.
 * The components required to format this audio file are initialized by this class
 * 
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 *
 */
public class FileOverlay extends AudioOverlay{
	
	/**
	 * Fields declared globally in order for whole class to use them
	 */
    private File selectedFile;				// Audio file user selected
    private JLabel fileNameLabel;			// The label containing information of the selected file
    private JButton fileSelectorButton;		// Button to add an audio file to the project

    /**
     * Constructor to allow class to be instantiated with no inputs and calls another constructor
     */
    public FileOverlay(){
        this(null, 0, 100);
    }

    /**
     * Constructor called to initialize global variables
     * 
     * @param filePath - the path to get to the audio file
     * @param startTime - the time at which the audio should start in the video
     * @param volume - the volume at which the audio should play
     */
    public FileOverlay(String filePath, float startTime, int volume){
        
    	// Making sure the file path isn't empty
    	if (filePath != null) {
            this.selectedFile = new File(filePath);
        }
        this.startTime = startTime;
        this.volume = volume;
    }

    /**
     * Setting up the layout of the components this class has e.g. Add file button
     * Using grid bag layout to make GUI look neat and tidy even when resized
     */
    @Override
    public JPanel getComponentView() {
        
    	// Calling the getComponentView of the AudioOverlay class to set up layout
    	final JPanel contentPane = super.getComponentView();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        // Creating JPanel to hold the button to open the file and for the label showing
        // information about the chosen audio file
        JPanel fileSelectorPanel = new JPanel();
        fileSelectorPanel.setLayout(new GridBagLayout());

        // Setting up label to contain information for the file the user selected
        fileNameLabel = new JLabel("[ no file selected ]");
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=1.0f;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.anchor= GridBagConstraints.NORTH;
        // Adding label to the fileSelectorPanel
        fileSelectorPanel.add(fileNameLabel, gbc);
        
        // Setting up button to allow user to choose an audio file
        fileSelectorButton = new JButton("...");
        gbc.gridx=1;
        gbc.weightx=0.0f;
        fileSelectorPanel.add(fileSelectorButton,gbc);

        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=1.0f;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.anchor= GridBagConstraints.NORTH;
        contentPane.add(fileSelectorPanel,gbc);

        // Adding action listener to the file selector button to allow user to actually open an audio file
        fileSelectorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            	// Allowing user to select the file in a visually appealing way through the
            	// use of a JFileChooser
            	JFileChooser chooser = new JFileChooser();
            	// Only allowing files to be chosen
            	chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            	// Only allowing one file to be chosen
            	chooser.setMultiSelectionEnabled(false);
            	
            	// Allowing user to only select mp3 and wav files to open
            	chooser.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter mp3Filter = new FileNameExtensionFilter("mp3 files", "mp3");
                FileNameExtensionFilter wavFilter = new FileNameExtensionFilter("wav files", "wav");
                chooser.addChoosableFileFilter(mp3Filter);
                chooser.addChoosableFileFilter(wavFilter);
            	
            	// Obtaining the file selected by the user
            	if (chooser.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
            		selectedFile = chooser.getSelectedFile();
            		
            		// Making sure file was selected
            		if (selectedFile != null) {
            			// Allowing the user to play the audio file
            			playButton.setEnabled(true);
            			// Setting the label text to the name of the file
            			fileNameLabel.setText(selectedFile.getName());
            			
            			// Getting the duration of the audio file and setting the label accordingly
            			try {
            				duration = getDuration(selectedFile);
            				// Formatting the duration string
                            int minutes = (int)(duration / 60);
                            int seconds = (int)(duration % 60);
                            int milliseconds = (int)((duration - seconds - minutes*60) * 1000);
                            String durationText=String.format("%02d", minutes)+":"+String.format("%02d", seconds)+":"+String.format("%03d", milliseconds);

            				durationLabel.setText(durationText);
            			} catch (InterruptedException | IOException e) {
            				durationLabel.setText("????");
            			}
            		} else {
            			// Disabling the user from playing the audio
            			playButton.setEnabled(false);
            		}
            	}
                updateDisplayPanel();
            }
        });
        
        // Setting the file name label to display the name of the file selected if any
        if (selectedFile != null) { 
        	fileNameLabel.setText(selectedFile.getName());
        	playButton.setEnabled(true);
        }
        
        return contentPane;
    }

    /**
     * This method returns the file part, start time and the volume of a specific file overlay
     * object in a string format
     * 
     * @return s - string containing information on the file path, start time and volume of an audio file
     */
    @Override
    public String toString(){
        String s = "F\t" + getFilePath() + "\t" + startTime + "\t" + volume;
        return s;
    }

    /**
     * This method takes in a string s and splits it to obtain the file path, start time and volume
     * of a specific audio file. It then returns a new instance of FileOverlay with those specified
     * inputs
     * 
     * @param s
     * @return instance of FileOverlay
     */
    public static FileOverlay fromString(String s){
        String filePath=s.split("\\t")[1];
        float startTime=Float.parseFloat(s.split("\\t")[2]);
        int volume=Integer.parseInt(s.split("\\t")[3]);
        return new FileOverlay(filePath,startTime,volume);
    }

    /**
     * Method which gets the path of a file
     * 
     * @return the path of the selected file
     */
    @Override
	public String getFilePath() {
    	
    	// Checking if the variable selectedFile actually contains a file or not
        if (selectedFile == null) {
            return "";
        } else {
        	// Returning path of selected file
            return selectedFile.getAbsolutePath();
        }
    }
}
