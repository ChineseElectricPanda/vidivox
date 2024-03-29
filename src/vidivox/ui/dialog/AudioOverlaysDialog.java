package vidivox.ui.dialog;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;

import sun.applet.Main;
import vidivox.audio.AudioOverlay;
import vidivox.audio.CommentaryOverlay;
import vidivox.audio.FileOverlay;
import vidivox.ui.MainFrame;

/**
 * This is the class for the Dialog which allows users to add and edit the audio tracks they wish to overlay onto their
 * video. It consists of container components generated from AudioOverlay objects.
 *
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 *
 */
public class AudioOverlaysDialog extends JDialog {
    private static AudioOverlaysDialog instance;    // Singleton instance of this class

    private MainFrame mainFrame;
    private static List<AudioOverlay> overlays = new ArrayList<>();                   // List of audio overlays added
    private List<JButton> deleteButtons;        // List of button for deleting each audio overlay track
    private JButton addCommentaryButton;        // A button for adding a commentary overlay
    private JButton addAudioButton;             // A button for adding an audio file (eg mp3) overlay

    /**
     * Gets the singleton instance of this class
     * @return the AudioOverlaysDialog singleton
     */
    public static AudioOverlaysDialog getInstance(){
        if(instance==null){
            instance=new AudioOverlaysDialog();
        }
        return instance;
    }

    /**
     * Initializes the Dialog with layout and listeners
     */
    private AudioOverlaysDialog() {
        super(MainFrame.getInstance());
        this.mainFrame=MainFrame.getInstance();
        setTitle("Audio Overlays");
        refreshLayout();
        setMinimumSize(new Dimension(900, 400));
        setLocation(mainFrame.getX()+mainFrame.getWidth(), mainFrame.getY());
        pack();
    }

    /**
     * Gets the list of audio overlays currently being added to the video
     * @return the list of overlays
     */
    public List<AudioOverlay> getOverlays() {
        return overlays;
    }

    /**
     * Sets the list of audio overlays currently being added to the video
     * @param overlays the list to set to
     */
    public void setOverlays(List<AudioOverlay> overlays){
        AudioOverlaysDialog.overlays=overlays;
    }

    /**
     * Sets up the listeners to respond to user input
     */
    private void setupListeners() {
        // Add commentary button creates a new commentary track and adds it to the dialog
        addCommentaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CommentaryOverlay overlay=new CommentaryOverlay(overlays.size());
                overlays.add(overlay);
                refreshLayout();
                mainFrame.updateAudioDisplays();
            }
        });
        // Add audio button creates a new audio track and adds it to the dialog
        addAudioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                FileOverlay overlay=new FileOverlay();
                overlays.add(overlay);
                refreshLayout();
                mainFrame.updateAudioDisplays();
            }
        });

        // Set the delete button next to each track to delete that track when clicked
        for (int i = 0; i < deleteButtons.size(); i++) {
            final int position = i;
            deleteButtons.get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                	//stop the audio preview when it is removed
                	overlays.get(position).stopAudioPlayer();
                	overlays.get(position).stopPreviewAudio();
                    overlays.remove(position);
                    refreshLayout();
                    mainFrame.updateAudioDisplays();
                }
            });
        }
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                // Stop all preview audio when the window is closed
                for(AudioOverlay overlay: overlays){
                    overlay.stopPreviewAudio();
                }
            }
        });
    }

    /**
     * Set up the layout of the dialog using a GridBagLayout
     */
    private void setupLayout(){
        getContentPane().removeAll();
        JScrollPane scrollPane = new JScrollPane();
        JPanel contentPane = new JPanel();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(contentPane);
        getContentPane().add(scrollPane);

        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(5,5,5,5);

        //create a panel for holding the buttons at the top of the dialog
        JPanel buttonsPanel=new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        addCommentaryButton=new JButton("Add Commentary");
        buttonsPanel.add(addCommentaryButton);
        buttonsPanel.add(new JPanel());
        addAudioButton=new JButton("Add Audio File");
        buttonsPanel.add(addAudioButton);

        gbc.gridwidth=2;
        gbc.gridy=0;
        gbc.fill= GridBagConstraints.HORIZONTAL;
        gbc.anchor=GridBagConstraints.NORTH;
        contentPane.add(buttonsPanel,gbc);

        deleteButtons = new ArrayList<>();

        gbc.gridwidth = 1;

        //create a panel for holding the components to display the audio overlay tracks
        for (AudioOverlay overlay:overlays) {
            Component overlayDisplay=overlay.getComponentView();

            //add a divider between each overlay
            gbc.gridy++;
            gbc.gridx=0;
            gbc.gridwidth=2;
            contentPane.add(new JSeparator(JSeparator.HORIZONTAL),gbc);

            //set up the layout for the overlay controls
            gbc.gridy++;
            gbc.gridx=0;
            gbc.gridwidth=1;
            gbc.weightx=1.0f;
            gbc.anchor=GridBagConstraints.CENTER;
            contentPane.add(overlayDisplay,gbc);

            //add a delete button for removing the track
            gbc.gridx=1;
            gbc.weightx=0.0f;
            gbc.anchor=GridBagConstraints.CENTER;
            JButton deleteButton=new JButton(new ImageIcon(Main.class.getResource("/vidivox/ui/image/delete.png")));
            deleteButtons.add(deleteButton);
            contentPane.add(deleteButton,gbc);
        }
        
        gbc.gridy++;
        gbc.weighty=1.0f;
        contentPane.add(new JPanel(),gbc);
        
        doLayout();
        revalidate();
    }

    public void updateStartTimeFields(){
        for(AudioOverlay overlay:overlays){
            overlay.updateStartTimeFields();
        }
    }

	public void refreshLayout() {
		setupLayout();
		setupListeners();
	}
}
