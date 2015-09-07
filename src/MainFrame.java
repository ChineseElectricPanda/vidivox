import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MainFrame extends JFrame{
    public static VideoPlayerComponent videoPlayer;
    private ControlsPanel controlsPanel;
    private JMenuBar menuBar;
    private JMenuItem openVideoButton;
    private JMenuItem openProjectButton;
    private JMenuItem saveProjectButton;
    private JMenuItem exportButton;
    private JMenuItem quitButton;

    public MainFrame(){
        super("VIDIVOX");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setupMenuBar();
        setupLayout();
        setMinimumSize(new Dimension(600,300));
        pack();
    }

    /**
     * Sets up the frame layout
     */
    private void setupLayout(){
        Container contentPane=getContentPane();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();

        //set up the video player frame
        videoPlayer=new VideoPlayerComponent();
        videoPlayer.setPreferredSize(new Dimension(600, 480));
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=1.0f;
        gbc.weighty=1.0f;
        gbc.anchor= GridBagConstraints.NORTH;
        gbc.fill= GridBagConstraints.BOTH;
        videoPlayer.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("Testing - mouse clicked inside player");
				// if paused then play else pause 
				if (videoPlayer.getMediaPlayer().isPlaying()) {
					 videoPlayer.getMediaPlayer().pause();
				} else {
					 videoPlayer.getMediaPlayer().play();
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				// Maybe could use this to show buttons later on in design
				System.out.println("Testing - mouse entered player");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				// Maybe could use this to hide buttons later on in design
				System.out.println("Testing - mouse exited player");
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub	
			}
        });
        contentPane.add(videoPlayer, gbc);

        //set up the controls panel
        controlsPanel=new ControlsPanel();
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.weightx=1.0f;
        gbc.weighty=0.0f;
        gbc.anchor= GridBagConstraints.SOUTH;
        gbc.fill= GridBagConstraints.HORIZONTAL;
        contentPane.add(controlsPanel,gbc);
    }

    /**
     * Sets up the menu bar layout
     */
    private void setupMenuBar(){
        menuBar=new JMenuBar();

        //the file menu
        JMenu fileMenu=new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        // Menu item choice to choose a video and play in the player
        openVideoButton=new JMenuItem("Open Video");
        openVideoButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		// File chooser to allow user to select a video to open
        		JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                  File selectedFile = fileChooser.getSelectedFile();	
                  String path = selectedFile.getPath(); // Getting file path
                                   
                  // Playing the video
                  videoPlayer.playVideo(videoPlayer, path);
                  
                // Sleeping the thread to allow for the time to be gotten
                try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					System.out.println("Error while sleeping open video button thread");
					e1.printStackTrace();
				}
                  // Getting total length of video in milliseconds
                  long time = videoPlayer.getMediaPlayer().getLength();
                  
                  // Converting millisecond time to preferred format
                  long second = (time / 1000) % 60;
                  long minute = (time / 60000) % 60;
                  long hour = (time / 3600000) % 24;
                  String totalTime = String.format("%02d:%02d:%02d", hour, minute, second);

                  // Setting total time variable
                  controlsPanel.setTotalTime(totalTime);
                }
        	}
        });
        fileMenu.add(openVideoButton);
        
        openProjectButton=new JMenuItem("Open Project");
        fileMenu.add(openProjectButton);
        saveProjectButton=new JMenuItem("Save Project");
        fileMenu.add(saveProjectButton);
        fileMenu.addSeparator();
        exportButton=new JMenuItem("Export");
        fileMenu.add(exportButton);
        fileMenu.addSeparator();
        
        quitButton=new JMenuItem("Quit");
        quitButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		// Cleaning up - closing eveything
        		dispose();
        		System.exit(0);
        	}
        });
        fileMenu.add(quitButton);
        
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }
    
}
