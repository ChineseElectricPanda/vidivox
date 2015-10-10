package vidivox.ui.displaypanel;

import vidivox.audio.AudioOverlay;
import vidivox.audio.CommentaryOverlay;
import vidivox.audio.FileOverlay;
import vidivox.ui.dialog.AudioOverlaysDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

public class AudioDisplayPanel extends JPanel{
    private JPanel canvas;
    private static double videoLength=0;
    public AudioDisplayPanel(final AudioOverlay overlay){
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
                int startPosition=(int)(overlay.getStartTime()*AudioOverlaysPanel.scale);
                int length=(int)(overlay.getDuration()*AudioOverlaysPanel.scale);

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
                g.fillRoundRect(startPosition, 0, length, 35, 5, 5);

                //draw the name of the file
                g.setColor(new Color(0, 0, 0));
                g.drawString(text, Math.max(startPosition + 5, 5), 17);
                setMinimumSize(new Dimension(length, 35));

                //draw a line where the current play position is
                g.setColor(new Color(255, 0, 0));
                int x= (int) (AudioOverlaysPanel.getPosition()*AudioOverlaysPanel.scale);
                g.drawLine(x, 0, x, 35);

                //draw a line where the end of the video is (only if video length is not 0)
                g.setColor(new Color(0,0,255));
                x= (int) (videoLength*AudioOverlaysPanel.scale);
                if(x>0) {
                    g.drawLine(x, 0, x, 35);
                }

                g.setColor(blockColor);
                g.setClip(new RoundRectangle2D.Double(startPosition, 0, length, 35, 5, 5));
                g.setXORMode(new Color(100, 100, 100));
                g.fillRect(x+1,0,999999,35);

            }
        };
        MouseAdapter adapter=new MouseAdapter() {
            boolean mouseDown=false;
            int startPosition=0;
            double initialTime=overlay.getStartTime();
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDown=true;
                startPosition=e.getX();
                initialTime=overlay.getStartTime();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseDown=false;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int offset=e.getX()-startPosition;
                overlay.setStartTime(initialTime+((double)offset)/AudioOverlaysPanel.scale);
                AudioOverlaysDialog.updateStartTimeFields();
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

    public static void setVideoLength(double length){
        AudioDisplayPanel.videoLength=length;
    }
}
