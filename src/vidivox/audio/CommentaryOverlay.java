package vidivox.audio;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import vidivox.ui.JTextFieldWithCharacterLimit;
import vidivox.worker.SpeechSynthesisWorker;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * This class is a subclass of the AudioOverlay class and is instantiated whenever 
 * the user selects to add a comment to the video. It contains the text the user 
 * entered and executes the synthesis worker class to create a wav file from the text
 * 
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 */
public class CommentaryOverlay extends AudioOverlay {

	/**
	 * Fields initialized to be used by methods in this class and other packages
	 */
    private final int CHARACTER_LIMIT=75;              // Character limit for the commentary
	private String text;								// String storing the user's comment
    private String filePath;							// File path of created wav file
    private int position;								// Position of the comment in the AudioOverlaysDialog
    private SpeechSynthesisWorker synthesisWorker;		// Reference to the class that creates the wav file
    private JTextFieldWithCharacterLimit textField;						// Text field where user enters comment
    private JButton saveToFileButton;					// Button used to save commentary as mp3

    /**
     * Constructor which takes in only the position of the audio and initializes other fields
     * in this class to a default value
     * 
     * @param position
     */
    public CommentaryOverlay(int position) {
        this(position, "", 0, 100);
    }
    
    /**
     * Constructor called when this class is instantiated 
     * 
     * @param position - position of the commentary in the dialog
     * @param text - text entered by the user
     * @param startTime - start time of audio specified by the user
     * @param volume - volume of audio
     */
    public CommentaryOverlay(int position, String text, float startTime,int volume){
        // Initializing fields
    	this.position = position;
        this.text = text;
        this.startTime = startTime;
        this.volume = volume;
        
        // Checking if the text is empty and if not, allowing the commentary to be
        // converted into a wav file
        if (text != null && !text.isEmpty()) {
        	// Instantiating class to convert from text to speech
        	new SpeechSynthesisWorker(text, "commentary" + position) {
        		@Override
        		protected void done() {
	        		super.done();
	        		// Setting the file path of the wav file
	        		CommentaryOverlay.this.filePath = filePath;
        		}
        	}.execute(); // Executing worker class
        }
    }
    
    /**
     * Getter for the position of the comment in the dialog frame
     * @return the position of the comment
     */
    public int getPosition() {
    	return position;
    }
    
    /**
     * Getter for the text the user typed in
     * @return the text
     */
    public String getText() {
    	return text;
    }

    /**
     * Getter for the file path variable
     * @return the string specifying the path to the file
     */
    @Override
	public String getFilePath() {
        return filePath;
    }
    
    /**
     * Method to set up the layout of the commentary overlay component using the grid 
     * bag layout
     * 
     * @return the content pane which is a JPanel
     */
    public JPanel getComponentView() {
    	
    	// Setting up the content pane by calling AudioOverlay's method
        final JPanel contentPane = super.getComponentView();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        // Adding text field where user will enter their text to turn into audio
        textField = new JTextFieldWithCharacterLimit(CHARACTER_LIMIT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0f;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        contentPane.add(textField, gbc);
        // Add button to save the commentary to an mp3 file
        saveToFileButton=new JButton("Save to mp3");
        gbc.gridx++;
        contentPane.add(saveToFileButton);

        // Adding document listener to the text field to determine when the user has
        // typed something in and getting the text from the text field to store
        textField.getDocument().addDocumentListener(new DocumentListener() {
            
        	// Method called whenever text is added
        	@Override
            public void insertUpdate(DocumentEvent documentEvent) {
                changedUpdate(documentEvent);
            }

            // Method called whenever text is removed
            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                changedUpdate(documentEvent);
            }

            // Method called whenever change occurs
            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
            	//Storing text
            	text = textField.getText();
                // Checking if the synthesis worker thread needs to be killed since
                // text is being updated
                if (synthesisWorker != null && !synthesisWorker.isDone()) {
                    synthesisWorker.kill();
                }

                // Creating new synthesis worker to convert the text file into a wav file
                synthesisWorker = new SpeechSynthesisWorker(text, "commentary" + position) {
                    @Override
                    protected void done() {

                        super.done();
                        // Allowing the play button to be clicked
                        playButton.setEnabled(true);
                        // Getting the file path of the wav file
                        CommentaryOverlay.this.filePath = synthesisWorker.filePath;

                        // Getting the duration of the audio and storing it
                        try {
                            duration = getDuration(filePath);
                            int minutes = (int)(duration / 60);
                            int seconds = (int)(duration % 60);
                            int milliseconds = (int)((duration - seconds - minutes*60) * 1000);
                            String durationText=String.format("%02d", minutes)+":"+String.format("%02d", seconds)+":"+String.format("%03d", milliseconds);
                            // Setting the text in the label to the duration value
                            durationLabel.setText(durationText);
                        } catch (IOException | InterruptedException e) {
                            // Error handling if there was a problem while getting duration
                            durationLabel.setText("???");
                        }
                        // Update the display on the timeline
                        displayPanel.revalidate();
                    }
                };
                synthesisWorker.execute(); // Executing the process
            }
        });
        
        // Adding action listener to button used to save a comment as a mp3 file
        saveToFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                
            	// Making sure the commentary is not empty
                if (getFilePath() == null) {
                	// Error message disallowing user to save empty comment
                    JOptionPane.showMessageDialog(contentPane,"Error: Cannot save empty commentary",
                    		"Error saving file",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Allowing user to choose a destination to save to
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
                	
                    String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                    
                    // Adding .mp3 to the end of the filename if the user didn't
                    if (!fileName.endsWith(".mp3")) {
                        fileName = fileName + ".mp3";
                    }
                    
                    // Calling method to save the comment as an mp3 file
                    try {
                        saveToMp3(fileName);
                        JOptionPane.showMessageDialog(contentPane,"Commentary sucessfully saved as " + fileName);
                    } catch (IOException | InterruptedException e) {
                        JOptionPane.showMessageDialog(contentPane,"Error saving commentary","Error saving file", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        // Checking if the text is not empty and setting the text in the text field
        if (text != null) {
            textField.setText(text);
        }
        
        return contentPane;
    }

    /**
     * Saves this commentary to a mp3 file
     * @param outputFilePath the file to save to
     * @throws IOException
     * @throws InterruptedException
     */
    private void saveToMp3(final String outputFilePath) throws IOException, InterruptedException {
        //create a  ffmpeg process to process the commentary file to an mp3
        //(note: this is fast enough to run on the main thread without noticible effect on UI)
        String cmd="ffmpeg -y -i \""+getFilePath()+"\" \""+outputFilePath+"\"";
        new ProcessBuilder("/bin/bash","-c",cmd).start().waitFor();
    }

    /**
     * This method is called whenever this class is needed to be printed out
     * It allows useful information such as position, text, start time, and volume
     * to be obtained
     * 
     * @return a string 's'
     */
    @Override
    public String toString() {
        String s = "C\t" + position + "\t" + text + "\t" + startTime + "\t" + volume;
        return s;
    }

    /**
     * This method takes in a string 's' and splits it to get the fields
     * position, text, start time and volume and creates a class of type
     * CommentaryOverlay with those fields
     * 
     * @param s
     * @return
     */
    public static CommentaryOverlay fromString(String s) {
    	
    	// Getting position
        int position = Integer.parseInt(s.split("\\t")[1]);
        
        // Getting text
        String text = s.split("\\t")[2];
        
        // Getting start time
        float startTime = Float.parseFloat(s.split("\\t")[3]);
        
        // Getting volume 
        int volume=Integer.parseInt(s.split("\\t")[4]);

        // Creating new object of this class
        return new CommentaryOverlay(position, text, startTime, volume);
    }
}