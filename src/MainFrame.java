import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.ArrayList;

public class MainFrame extends JFrame{
    private VideoPlayerComponent videoPlayer;
    private String videoPath;
    private ControlsPanel controlsPanel;
    private JMenuBar menuBar;
    private JMenuItem openVideoButton;
    private JMenuItem openProjectButton;
    private JMenuItem saveProjectButton;
    private JMenuItem exportButton;
    private JMenuItem quitButton;
    private JMenuItem commentaryButton;

    public MainFrame(){
        super("VIDIVOX");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setupMenuBar();
        setupLayout();
        setupListeners();
        setMinimumSize(new Dimension(600,300));
        pack();
    }

    private void setupListeners() {
        commentaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new AudioOverlaysDialog().setVisible(true);
            }
        });
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
        controlsPanel=new ControlsPanel(videoPlayer);
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.weightx=1.0f;
        gbc.weighty=0.0f;
        gbc.anchor= GridBagConstraints.SOUTH;
        gbc.fill= GridBagConstraints.HORIZONTAL;
        contentPane.add(controlsPanel, gbc);
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
                    videoPath = selectedFile.getPath(); // Getting file path

                    // Playing the video
                    videoPlayer.playVideo(videoPath);

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
        openProjectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        String[] options=new String[]{"Yes, Discard changes","No"};
                        String message="Opening another project will cause usaved changes to be discarded\n" +
                                "Are you sure you want to do this?";
                        int result=JOptionPane.showOptionDialog(MainFrame.this,message,"Warning",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,options,options[1]);
                        if(result==JOptionPane.YES_OPTION) {
                            openProject(fileChooser.getSelectedFile());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (FileFormatException e) {
                        JOptionPane.showMessageDialog(MainFrame.this,"Invalid or corrupted VIDIVOX project file","Invalid File",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        fileMenu.add(openProjectButton);
        saveProjectButton=new JMenuItem("Save Project");
        saveProjectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        saveProject(fileChooser.getSelectedFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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

        JMenu editMenu=new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        commentaryButton=new JMenuItem("Audio Overlays...");
        editMenu.add(commentaryButton);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);
    }

    private void saveProject(File file) throws IOException {
        BufferedWriter f=new BufferedWriter(new FileWriter(file));
        if(videoPath==null){
            f.write("\n");
        }else {
            f.write(videoPath + "\n");
        }
        for(AudioOverlay overlay:AudioOverlaysDialog.getOverlays()){
            f.write(overlay.toString()+"\n");
        }
        f.close();
    }

    private void openProject(File file) throws IOException, FileFormatException {
        BufferedReader f=new BufferedReader(new FileReader(file));
        String line=f.readLine();
        String videoPath=null;
        if(!line.equals("")) {
            videoPath = line;
        }
        List<AudioOverlay> overlays=new ArrayList<>();
        while((line=f.readLine())!=null){
            overlays.add(AudioOverlay.fromString(line));
        }
        //only set the new variables after ALL lines have been read
        this.videoPath=videoPath;
        if(videoPath!=null){
            videoPlayer.playVideo(videoPath);
        }
        AudioOverlaysDialog.setOverlays(overlays);

    }
    
}
