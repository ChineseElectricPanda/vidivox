import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MainFrame extends JFrame{
    private VideoPlayerComponent videoPlayer;
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
     * Seys up the frame layout
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
        openVideoButton=new JMenuItem("Open Video");
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
        fileMenu.add(quitButton);
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);

    }
}
