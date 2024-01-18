package solution.views;

import solution.interfaces.GameControllerInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;

/**
 * Created by rory on 09/03/15.
 */
public class MainFrame extends JFrame implements ActionListener {

    private static final String COMMAND_LOAD = "load";
    private static final String COMMAND_SAVE = "save";
    private final GameControllerInterface mControllerInterface;
    ScreenView mainScreen;
    private GridBagConstraints mGridLayout = null;

    public MainFrame(final GameControllerInterface controllerInterface) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Set controllers and interfaces
        mControllerInterface = controllerInterface;
        mainScreen = new ScreenView(controllerInterface);

        // Setup screen
        setupScreen();
        add(mainScreen, mGridLayout);
        endSetupScreen();

    }

    private void setupScreen(){
        // Set layout
        setLayout(new GridBagLayout());
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        setPreferredSize(new Dimension(width, height));

        // Form the layout
        GridBagConstraints gbc = new GridBagConstraints();

        // Setup the grid
        gbc.gridy = gbc.gridx = 0;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weighty = gbc.weightx = 100;
        mGridLayout = gbc;
    }
    private void endSetupScreen(){
        // Set up background
        JLabel background = new JLabel();
        URL resource = getClass().getClassLoader().getResource("ui" + File.separator + "woodenbg.jpg");
        ImageIcon bgIcon = new ImageIcon(resource);
        background.setIcon(bgIcon);
        add(background,mGridLayout);
        setVisible(false);
        pack();
        repaint();
        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JFileChooser fc = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir"));
        fc.setCurrentDirectory(workingDirectory);
        if(COMMAND_LOAD.equals(e.getActionCommand())){
            int returnVal = fc.showOpenDialog(MainFrame.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                System.out.println("Opening: " + file.getName() + ".");


                //Custom button text
                Object[] options = {"Yes, please",
                        "Nope"};
                int response = JOptionPane.showOptionDialog(MainFrame.this,
                        "Would you like to replay the saved game?",
                        "Replay",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                mControllerInterface.loadGame(file, response == JOptionPane.YES_OPTION);

            } else {
                System.out.println("Open command cancelled by user.");
            }
        }else if(COMMAND_SAVE.equals(e.getActionCommand())){
            int returnVal = fc.showSaveDialog(MainFrame.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                System.out.println("Saving: " + file.getName() + ".");
                mControllerInterface.saveGame(file);
            } else {
                System.out.println("Save command cancelled by user.");
            }
        }
    }



}
