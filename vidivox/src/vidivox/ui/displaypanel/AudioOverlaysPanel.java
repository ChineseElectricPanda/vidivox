package vidivox.ui.displaypanel;

import vidivox.audio.AudioOverlay;
import vidivox.audio.FileOverlay;
import vidivox.ui.dialog.AudioOverlaysDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public class AudioOverlaysPanel extends JPanel {
    private JPanel contentPane=new JPanel();
    private JFrame container;
    private List<AudioDisplayPanel> displays=new ArrayList<>();

    public static double scale=10;
    private static int playPosition=0;

    public AudioOverlaysPanel(JFrame container){
        this.container=container;
        contentPane.setLayout(new GridBagLayout());
        setLayout(new GridBagLayout());

        GridBagConstraints gbc=new GridBagConstraints();
        gbc.fill=GridBagConstraints.BOTH;
        gbc.weightx=1;
        JScrollPane scrollPane=new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setViewportView(contentPane);
        setMinimumSize(new Dimension(500, 35));
        add(scrollPane, gbc);
    }

    public static double getPosition() {
        return ((double)playPosition)/1000;
    }

    public void updateComponents(){
        // Remove all existing children
        contentPane.removeAll();
        displays=new ArrayList<>();

        GridBagConstraints gbc=new GridBagConstraints();
        gbc.weightx=1;
        gbc.weighty=1;
        gbc.gridy=0;
        gbc.fill=GridBagConstraints.BOTH;

        for(AudioOverlay overlay: AudioOverlaysDialog.getOverlays()){
            contentPane.add(new JSeparator(JSeparator.HORIZONTAL), gbc);
            gbc.gridy++;
            AudioDisplayPanel display=overlay.getDisplayPanel();
            displays.add(display);
            contentPane.add(display, gbc);
            gbc.gridy++;
        }

        revalidate();
        doLayout();

        container.revalidate();
        container.doLayout();

    }

    public void updatePosition(int position) {
        playPosition=position;
        revalidate();
        for(AudioDisplayPanel display:displays){
            display.revalidate();
        }
    }
}
