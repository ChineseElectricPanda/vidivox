import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AudioOverlaysDialog extends JDialog {
    private static List<AudioOverlay> overlays=new ArrayList<>();
    private List<JButton> deleteButtons;
    private JButton addCommentaryButton;
    private JButton addAudioButton;

    public AudioOverlaysDialog() {
        setupLayout();
        setupListeners();
        setMinimumSize(new Dimension(800, 400));
        pack();
    }

    private void setupListeners() {
        addCommentaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CommentaryOverlay overlay=new CommentaryOverlay(overlays.size());
                overlays.add(overlay);
                setupLayout();
                setupListeners();
            }
        });
        addAudioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                FileOverlay overlay=new FileOverlay();
                overlays.add(overlay);
                setupLayout();
                setupListeners();
            }
        });

        for(int i=0;i<deleteButtons.size();i++){
            final int position=i;
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

    private void setupLayout(){
        getContentPane().removeAll();
        JScrollPane scrollPane=new JScrollPane();
        JPanel contentPane=new JPanel();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(contentPane);
        getContentPane().add(scrollPane);

        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();

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

        deleteButtons=new ArrayList<>();

        gbc.gridwidth=1;
        for(AudioOverlay overlay:overlays){
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
