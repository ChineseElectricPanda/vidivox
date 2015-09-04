package vidivox;

import javax.swing.*;
import java.awt.*;

public class CommentaryDialog extends JDialog {
    private static String text;
    private static float startTime;
    private static float volume;

    private JTextField textField;
    private JTextField startTimeField;
    private JSlider volumeSlider;
    private JLabel volumeLevelLabel;

    private JButton saveButton;
    private JButton cancelButton;


    public static void main(String[] args){
        new CommentaryDialog().setVisible(true);
    }
    public CommentaryDialog(){

        setMinimumSize(new Dimension(400,170));
        setupLayout();
        setupListeners();
        pack();
    }

    private void setupListeners() {

    }

    private void setupLayout(){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(5,5,5,5);

        textField=new JTextField();
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=1.0f;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.anchor= GridBagConstraints.NORTH;
        add(textField,gbc);

        //set up the properties controls layout
        JPanel propertiesPanel=new JPanel();
        propertiesPanel.setLayout(new GridBagLayout());
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=1;
        gbc.weightx=0.0f;
        propertiesPanel.add(new JLabel("Start Time"),gbc);
        startTimeField=new JTextField();
        startTimeField.setPreferredSize(new Dimension(50,19));
        gbc.gridx++;
        gbc.weightx=1.0f;
        propertiesPanel.add(startTimeField,gbc);
        gbc.gridx++;
        gbc.weightx=1.0f;
        propertiesPanel.add(new JPanel(),gbc);
        gbc.gridx++;
        gbc.weightx=0;
        propertiesPanel.add(new JLabel("Volume"),gbc);
        volumeSlider=new JSlider();
        volumeSlider.setPreferredSize(new Dimension(100, 25));
        volumeSlider.setValue(100);
        gbc.gridx++;
        propertiesPanel.add(volumeSlider,gbc);
        volumeLevelLabel=new JLabel("100%");
        gbc.gridx++;
        propertiesPanel.add(volumeLevelLabel, gbc);
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.weightx=1.0f;
        gbc.fill= GridBagConstraints.HORIZONTAL;
        add(propertiesPanel,gbc);

        gbc.gridx=0;
        gbc.gridy=2;
        gbc.weighty=1.0f;
        add(new JPanel(), gbc);

        //set up the ok and cancel buttons
        JPanel buttonsPanel=new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=1.0f;
        buttonsPanel.add(new JPanel(),gbc);
        saveButton=new JButton("Save");
        gbc.gridx++;
        gbc.weightx=0.0f;
        buttonsPanel.add(saveButton,gbc);
        cancelButton=new JButton("Cancel");
        gbc.gridx++;
        buttonsPanel.add(cancelButton,gbc);
        gbc.gridy=3;
        gbc.gridx=0;
        gbc.gridwidth=10;
        gbc.anchor= GridBagConstraints.SOUTH;
        add(buttonsPanel,gbc);
    }


}