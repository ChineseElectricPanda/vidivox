package vidivox.ui.timeline;

import vidivox.audio.AudioOverlay;
import vidivox.ui.dialog.AudioOverlaysDialog;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AudioTimelinesPanel extends JPanel {
    private JPanel contentPane=new JPanel();
    private JFrame container;
    private List<AudioTimelineDisplay> displays=new ArrayList<>();

    public static double scale=10;
    private static int playPosition=0;

    public AudioTimelinesPanel(JFrame container){
        this.container=container;
        contentPane.setLayout(new GridBagLayout());
        setLayout(new GridBagLayout());

        GridBagConstraints gbc=new GridBagConstraints();
        gbc.fill=GridBagConstraints.BOTH;
        gbc.weightx=1;
        gbc.weighty=1;
        JScrollPane scrollPane=new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setViewportView(contentPane);
        scrollPane.setMinimumSize(new Dimension(500,100));
        setMinimumSize(new Dimension(500, 100));
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
        gbc.weighty=0;
        gbc.gridy=0;
        gbc.anchor= GridBagConstraints.NORTH;
        gbc.fill=GridBagConstraints.BOTH;

        for(AudioOverlay overlay: AudioOverlaysDialog.getOverlays()){
            contentPane.add(new JSeparator(JSeparator.HORIZONTAL), gbc);
            gbc.gridy++;
            AudioTimelineDisplay display=overlay.getDisplayPanel();
            displays.add(display);
            contentPane.add(display, gbc);
            gbc.gridy++;
        }
        gbc.weighty=1;
        contentPane.add(new JPanel(),gbc);

        revalidate();
        doLayout();

        container.revalidate();
        container.doLayout();

    }

    public void updatePosition(int position) {
        playPosition=position;
        revalidate();
        for(AudioTimelineDisplay display:displays){
            display.revalidate();
        }
    }
}
