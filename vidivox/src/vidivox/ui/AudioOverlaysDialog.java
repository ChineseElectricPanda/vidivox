package vidivox.ui;
import javax.swing.*;

import vidivox.audio.AudioOverlay;
import vidivox.audio.CommentaryOverlay;
import vidivox.audio.FileOverlay;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the class for the Dialog which allows users to add and edit the audio tracks they wish to overlay onto their
 * video. It consists of container components generated from AudioOverlay objects.
 *
 * @author Hanzhi Wang
 * @author Ammar Bagasrawala
 *
 */
public class AudioOverlaysDialog extends JDialog {
    private static List<AudioOverlay> overlays = new ArrayList<>();                   // List of audio overlays added
    private List<JButton> deleteButtons;        // List of button for deleting each audio overlay track
    private JButton addCommentaryButton;        // A button for adding a commentary overlay
    private JButton addAudioButton;             // A button for adding an audio file (eg mp3) overlay

    /**
     * Initializes the Dialog with layout and listeners
     */
    public AudioOverlaysDialog() {
        super((Dialog)null);
        setupLayout();
        setupListeners();
        setMinimumSize(new Dimension(900, 400));
        pack();
    }

    /**
     * Gets the list of audio overlays currently being added to the video
     * @return the list of overlays
     */
    public static List<AudioOverlay> getOverlays() {
        return overlays;
    }

    /**
     * Sets the list of audio overlays currently being added to the video
     * @param overlays the list to set to
     */
    public static void setOverlays(List<AudioOverlay> overlays){
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
                setupLayout();
                setupListeners();
            }
        });
        // Add audio button creates a new audio track and adds it to the dialog
        addAudioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                FileOverlay overlay=new FileOverlay();
                overlays.add(overlay);
                setupLayout();
                setupListeners();
            }
        });

        // Set the delete button next to each track to delete that track when clicked
        for (int i = 0; i < deleteButtons.size(); i++) {
            final int position = i;
            deleteButtons.get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    overlays.remove(position);
                    setupLayout();
                    setupListeners();
                }
            });
        }
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
            JButton deleteButton=new JButton("X");
            deleteButtons.add(deleteButton);
            contentPane.add(deleteButton,gbc);
        }
        
        gbc.gridy++;
        gbc.weighty=1.0f;
        contentPane.add(new JPanel(),gbc);
        
        doLayout();
        revalidate();
    }
}
