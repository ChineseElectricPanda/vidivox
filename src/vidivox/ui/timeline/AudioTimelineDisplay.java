package vidivox.ui.timeline;

import vidivox.audio.AudioOverlay;
import vidivox.audio.CommentaryOverlay;
import vidivox.audio.FileOverlay;
import vidivox.ui.MainFrame;
import vidivox.ui.dialog.AudioOverlaysDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

/**
 * This class is for the timeline display for a single audio track
 */
public class AudioTimelineDisplay extends JPanel{
    private JPanel canvas;

    /**
     * Create a timeline display for the given audio overlay
     * @param overlay the audio overlay to display
     */
    public AudioTimelineDisplay(final AudioOverlay overlay){
        setMinimumSize(new Dimension(100, 75));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();

        JPanel spacer=new JPanel();
        spacer.setPreferredSize(new Dimension(0,35));
        gbc.weighty=1;
        gbc.weightx=0;
        add(spacer,gbc);

        canvas=new JPanel(){
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                //get the start and end position of where to draw the block based on
                //the audio start position and duration
                int startPosition=(int)(overlay.getStartTime()* AudioTimelinesPanel.getScale());
                int length=(int)(overlay.getDuration()* AudioTimelinesPanel.getScale());

                //generate the block's display color based on the file's path
                Random r=new Random();
                String text="";
                if(overlay instanceof FileOverlay){
                    r=new Random(overlay.getFileName().hashCode());
                    text=overlay.getFileName();
                }else if(overlay instanceof CommentaryOverlay){
                    r=new Random(((CommentaryOverlay)overlay).getPosition());
                    text=((CommentaryOverlay)overlay).getText();
                }
                Color blockColor=new Color(100 + r.nextInt(150), 100 + r.nextInt(150), 100 + r.nextInt(150));
                g.setColor(blockColor);

                //draw the block to represent the timeline
                g.fillRoundRect(startPosition, 0, length, 35, 5, 5);

                //draw the name of the file
                g.setColor(new Color(0, 0, 0));
                g.drawString(text, Math.max(startPosition + 5, 5), 17);
                setMinimumSize(new Dimension(length, 35));

                //draw a line where the current play position is
                g.setColor(new Color(255, 0, 0));
                int x= (int) (AudioTimelinesPanel.getPosition()* AudioTimelinesPanel.getScale());
                g.drawLine(x, 0, x, 35);

                //draw a line where the end of the video is (only if video length is not 0)
                g.setColor(new Color(0,0,255));
                x= (int) (AudioTimelinesPanel.getVideoLength()* AudioTimelinesPanel.getScale());
                if(x>0) {
                    g.drawLine(x, 0, x, 35);
                }

                //grey out the timeline after the end of the video
                g.setColor(blockColor);
                g.setClip(new RoundRectangle2D.Double(startPosition, 0, length, 35, 5, 5));
                g.setXORMode(new Color(100, 100, 100));
                g.fillRect(x+1,0,999999,35);

            }
        };

        // Create a listener to listen for clicks and drags on the timeline
        MouseAdapter adapter=new MouseAdapter() {
            boolean mouseDown=false;
            int startPosition=0;
            double initialTime=overlay.getStartTime();
            @Override
            public void mousePressed(MouseEvent e) {
                //start tracking mouse movement when it is pressed down on the timeline
                mouseDown=true;
                startPosition=e.getX();
                initialTime=overlay.getStartTime();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseDown=false;

                //update the audio player to reflect changes made to timeline
                overlay.updateAudioPlayer();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                //when the audio track is dragged on the timeline, change the starting time accordingly
                int offset=e.getX()-startPosition;
                double newStartTime=initialTime+((double)offset)/ AudioTimelinesPanel.getScale();

                // Make the start position snap to 0 or the current play position
                if(Math.abs(newStartTime-AudioTimelinesPanel.getPosition())<0.5){
                    newStartTime=AudioTimelinesPanel.getPosition();
                }else if(Math.abs(newStartTime)<0.5){
                    newStartTime=0;
                }
                overlay.setStartTime(newStartTime);
                AudioOverlaysDialog.getInstance().updateStartTimeFields();
                canvas.revalidate();
            }
        };
        canvas.addMouseListener(adapter);
        canvas.addMouseMotionListener(adapter);
        canvas.setMinimumSize(new Dimension(500, 50));
        canvas.setVisible(true);
        gbc.fill= GridBagConstraints.BOTH;
        gbc.gridwidth=1;
        gbc.gridx=1;
        gbc.gridy=0;
        gbc.weightx=1;
        gbc.weighty=1;
        add(canvas,gbc);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        canvas.revalidate();
        canvas.repaint();
    }
}
