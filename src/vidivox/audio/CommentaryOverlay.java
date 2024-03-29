package vidivox.audio;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import vidivox.ui.JTextFieldWithCharacterLimit;
import vidivox.ui.dialog.AudioOverlaysDialog;
import vidivox.worker.SpeechSynthesisWorker;

/**
 * This class is a subclass of the AudioOverlay class and is instantiated whenever 
 * the user selects to add a comment to the video. It contains the text the user 
 * entered and executes the synthesis worker class to create a wav file from the text
 * 
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 */
public class CommentaryOverlay extends AudioOverlay {
	public enum Voice{
		MALE,
		FEMALE
	}
	
	public enum Emotion{
		NEUTRAL,
		HAPPY,
		SAD
	}
	/**
	 * Fields initialized to be used by methods in this class and other packages
	 */
    private final int CHARACTER_LIMIT=75;              	// Character limit for the commentary
	private String text;								// String storing the user's comment
    private String filePath;							// File path of created wav file
    private int position;								// Position of the comment in the AudioOverlaysDialog
    private SpeechSynthesisWorker synthesisWorker;		// Reference to the class that creates the wav file
    private JTextFieldWithCharacterLimit textField;		// Text field where user enters comment
    private JButton saveToFileButton;					// Button used to save commentary as mp3
    
    private JToggleButton maleVoiceButton;
    private JToggleButton femaleVoiceButton;
    
    private JToggleButton neutralVoiceButton;
    private JToggleButton happyVoiceButton;
    private JToggleButton sadVoiceButton;
    
    private JSlider timeScaleSlider;
    
    private Voice voice;
    private Emotion emotion;
    private int timeScale;

    /**
     * Constructor which takes in only the position of the audio and initializes other fields
     * in this class to a default value
     * 
     * @param position the position of this commentary in the list
     */
    public CommentaryOverlay(int position) {
        this(position, "", 0, 100, Voice.MALE,Emotion.NEUTRAL,100);
    }
    
    /**
     * Constructor called when this class is instantiated 
     * 
     * @param position - position of the commentary in the dialog
     * @param text - text entered by the user
     * @param startTime - start time of audio specified by the user
     * @param volume - volume of audio
     */
    public CommentaryOverlay(int position, String text, float startTime,int volume,Voice voice, Emotion emotion, int timeScale){
        // Initializing fields
    	this.position = position;
        this.text = text;
        this.startTime = startTime;
        this.volume = volume;
        this.voice=voice;
        this.emotion=emotion;
        this.timeScale=timeScale;
        
        // Checking if the text is empty and if not, allowing the commentary to be
        // converted into a wav file
        if (text != null && !text.isEmpty()) {
        	// Instantiating class to convert from text to speech
        	new SpeechSynthesisWorker(text, "commentary" + position,100/((float)timeScale),voice,emotion) {
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
        
        JPanel voiceOptionsPanel=new JPanel();
        voiceOptionsPanel.setLayout(new GridBagLayout());
        
        ButtonGroup voiceButtonGroup=new ButtonGroup();
        gbc.insets=new Insets(0, 0, 0, 0);
        
        maleVoiceButton=new JToggleButton("Male");
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.fill=GridBagConstraints.NONE;
        gbc.anchor=GridBagConstraints.WEST;
        voiceOptionsPanel.add(maleVoiceButton,gbc);
        voiceButtonGroup.add(maleVoiceButton);
        
        femaleVoiceButton=new JToggleButton("Female");
        gbc.gridx++;
        voiceOptionsPanel.add(femaleVoiceButton,gbc);
        voiceButtonGroup.add(femaleVoiceButton);
        
        JPanel spacer=new JPanel();
        spacer.setPreferredSize(new Dimension(50,1));
        gbc.gridx++;
        voiceOptionsPanel.add(spacer,gbc);
        
        ButtonGroup emotionButtonGroup=new ButtonGroup();
        
        neutralVoiceButton=new JToggleButton("Neutral");
        gbc.gridx++;
        voiceOptionsPanel.add(neutralVoiceButton,gbc);
        emotionButtonGroup.add(neutralVoiceButton);
        
        happyVoiceButton=new JToggleButton("Happy");
        gbc.gridx++;
        voiceOptionsPanel.add(happyVoiceButton,gbc);
        emotionButtonGroup.add(happyVoiceButton);
        
        sadVoiceButton=new JToggleButton("Sad");
        gbc.gridx++;
        voiceOptionsPanel.add(sadVoiceButton,gbc);
        emotionButtonGroup.add(sadVoiceButton);
        
        spacer=new JPanel();
        spacer.setPreferredSize(new Dimension(50,1));
        gbc.gridx++;
        voiceOptionsPanel.add(spacer,gbc);
        
        gbc.gridx++;
        voiceOptionsPanel.add(new JLabel("Speed (%)"), gbc);
        
        timeScaleSlider=new JSlider();
        timeScaleSlider.setMinimum(60);
        timeScaleSlider.setMaximum(140);
        timeScaleSlider.setValue(timeScale);
        timeScaleSlider.setMajorTickSpacing(20);
        timeScaleSlider.setMinorTickSpacing(5);
        timeScaleSlider.setSnapToTicks(true);
        timeScaleSlider.setPaintTicks(true);
        timeScaleSlider.setPaintLabels(true);
        gbc.gridx++;
        voiceOptionsPanel.add(timeScaleSlider,gbc);
        
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.gridwidth=3;
        gbc.insets=new Insets(5, 5, 5, 5);
        contentPane.add(voiceOptionsPanel,gbc);
        
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
            	synthesizeText();
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
                    
                    // Check if the file already exists
                    File outFile=new File(fileName);
                    if(outFile.exists()){
                    	// Show a confirmation dialog before overwriting a file 
                    	if(JOptionPane.showConfirmDialog(AudioOverlaysDialog.getInstance(),
                    			outFile.getName()+" already exists!\nWould you like to overwrite it?","File already exists",
                    			JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)!=JOptionPane.YES_OPTION){
                    		return;
                    	}
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
        
        //Resynthesise the text whenever a voice setting is changed
        ActionListener voiceChangeListener=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synthesizeText();
			}
		};
		maleVoiceButton.addActionListener(voiceChangeListener);
		femaleVoiceButton.addActionListener(voiceChangeListener);
		neutralVoiceButton.addActionListener(voiceChangeListener);
		happyVoiceButton.addActionListener(voiceChangeListener);
		sadVoiceButton.addActionListener(voiceChangeListener);
		timeScaleSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				synthesizeText();
			}
		});
        
        // Checking if the text is not empty and setting the text in the text field
        if (text != null) {
            textField.setText(text);
        }
        
        // Set the voice options
        if(voice==Voice.MALE){
        	maleVoiceButton.setSelected(true);
        }else{
        	femaleVoiceButton.setSelected(true);
        }
        
        if(emotion==Emotion.NEUTRAL){
        	neutralVoiceButton.setSelected(true);
        }else if(emotion==Emotion.HAPPY){
        	happyVoiceButton.setSelected(true);
        }else if(emotion==Emotion.SAD){
        	sadVoiceButton.setSelected(true);
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
        return "C\t" + position + "\t" + text + "\t" + startTime + "\t" + volume + "\t" + voice.toString() + "\t" + emotion.toString() + "\t" + timeScale;
    }

    private void synthesizeText() {
		//Storing text
		text = textField.getText();
		// Checking if the synthesis worker thread needs to be killed since
		// text is being updated
		if (synthesisWorker != null && !synthesisWorker.isDone()) {
		    synthesisWorker.kill();
		}
		
		// Determine the selected voice and emotion
    	if(maleVoiceButton.isSelected()){
    		voice=Voice.MALE;
    	}else{
    		voice=Voice.FEMALE;
    	}
    	
    	if(neutralVoiceButton.isSelected()){
    		emotion=Emotion.NEUTRAL;
    	}else if(happyVoiceButton.isSelected()){
    		emotion=Emotion.HAPPY;
    	}else{
    		emotion=Emotion.SAD;
    	}
    	
    	timeScale=timeScaleSlider.getValue();

		// Creating new synthesis worker to convert the text file into a wav file
		synthesisWorker = new SpeechSynthesisWorker(text, "commentary" + position, 100/((float)timeScale) ,voice, emotion) {
		    @Override
		    protected void done() {

		        super.done();
		        // Allowing the play button to be clicked is there is text
		        playButton.setEnabled(text.trim().length()>0);
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

	/**
     * This method takes in a string 's' and splits it to get the fields
     * position, text, start time and volume and creates a class of type
     * CommentaryOverlay with those fields
     * 
     * @param s the string to construct this object from
     * @return the CommentaryOverlay object created from the string
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
        
        // Getting voice
        Voice voice=Voice.valueOf(s.split("\\t")[5]);
        
        // Getting voice
        Emotion emotion=Emotion.valueOf(s.split("\\t")[6]);
        
        // Getting timescale
        int timeScale=Integer.parseInt(s.split("\\t")[7]);
        
        // Creating new object of this class
        return new CommentaryOverlay(position, text, startTime, volume, voice, emotion, timeScale);
    }
}