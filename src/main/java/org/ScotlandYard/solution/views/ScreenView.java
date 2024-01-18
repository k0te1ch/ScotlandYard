package solution.views;

import solution.Constants;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by benallen on 18/03/15.
 */
public class ScreenView extends JPanel {

    private GameLayout gameLayout;
    private IntroView introView;
    private PlayerAddView playerAddView;
    private LoadingView loadingView;
    enum Screen {INTRO, ADD_PLAYER, GAME_PLAY, LOADING}
    private final GameControllerInterface mControllerInterface;

    private GridBagConstraints mGridLayout = null;

    class GameAdapter extends GameUIAdapter {
        @Override
        public void showGameInterface() {
            callScreen(Screen.GAME_PLAY);
        }
    }
    public ScreenView(final GameControllerInterface controllerInterface) {
        setOpaque(false);

        // Set listeners
        mControllerInterface = controllerInterface;
        controllerInterface.addUpdateListener(new GameAdapter());

        // When game starts call the Intro Screen
        callScreen(Screen.INTRO);

    }
    public void callScreen(final Screen screenToDisplay){
        // First call the loading screen and then when the new screen is ready call that
        manageScreens(Screen.LOADING);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                manageScreens(screenToDisplay);
            }
        });
    }
    private void manageScreens(Screen screenToDisplay){
        // Clear and setup screens
        clearScreen();
        setupScreen();

        // Call the screen depending on preference
        switch (screenToDisplay){
            case INTRO:
                showIntroView();
                break;
            case ADD_PLAYER:
                showPlayerAddView();
                break;
            case GAME_PLAY:
                showGameView();
                break;
            case LOADING:
                showLoadingView();
                break;
        }

        // End setup
        endSetupScreen();
    }

    private void showGameView() {
        System.out.println("Showing Game View");

        // Reset the game layout
        if(gameLayout != null){
            remove(gameLayout);
            gameLayout = null;
        }

        // Create a new layout and pass the listener in through the constructor
        gameLayout = new GameLayout(mControllerInterface, new PlayerInfoBar.PlayerInfoBarListener(){
            @Override
            public void onMenuBtnPress() {
                callScreen(Screen.INTRO);
            }
            @Override
            public void onSaveBtnPress() {
                showSaveOptions();
            }
        });

        // Add it the screen
        add(gameLayout, mGridLayout);
    }

    public void clearScreen(){
        revalidate();
        removeAll();
    }
    public void showIntroView(){
        System.out.println("Showing Intro Screen");

        // Reset the intro view
        if(introView != null){
            remove(introView);
            introView = null;
        }
        // Create a new intro view and set its listener
        introView = new IntroView();
        introView.setListener(new IntroView.IntroViewListener(){
            @Override
            public void onPlayBtnPress() {
                callScreen(Screen.ADD_PLAYER);
            }
            @Override
            public void onLoadBtnPress() { showLoadOptions(); }
        });

        // Add it to the screen
        add(introView, mGridLayout);

    }
    public void showLoadingView(){
        System.out.println("Showing Loading View");

        // Reset the loading view
        if(loadingView != null){
            remove(loadingView);
            loadingView = null;
        }

        // Create a new loading view
        loadingView = new LoadingView();

        // Add it to the screen
        add(loadingView, mGridLayout);
    }
    public void showPlayerAddView(){
        System.out.println("Showing Add Player View");

        // Set up player add view but reset it
        if(playerAddView != null){
            remove(playerAddView);
            playerAddView = null;
        }

        // Create the new Player view
        playerAddView = new PlayerAddView(Constants.MIN_PLAYERS, Constants.MAX_PLAYERS);

        // Set in the listener
        playerAddView.setListener(new PlayerAddView.PlayerCountListener() {
            @Override
            public void onPlayerCountDecided(int count) {
                mControllerInterface.notifyAllPlayersAdded(count);
            }
        });

        // Add it to the screen
        add(playerAddView, mGridLayout);
    }
    private void setupScreen(){

        // Set layout
        setLayout(new GridBagLayout());
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        // Set the preferred Size
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
        setVisible(false);
        repaint();
        setVisible(true);

    }
    private void showSaveOptions(){

        // Create the chooser
        final JFileChooser fc = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir"));
        fc.setCurrentDirectory(workingDirectory);
        int returnVal = fc.showSaveDialog(getParent());

        // If approval then get the file selected
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
            System.out.println("Saving: " + file.getName() + ".");
            mControllerInterface.saveGame(file);
        } else {
            System.out.println("Save command cancelled by user.");
        }
    }
    private void showLoadOptions(){

        // Create the file chooser
        final JFileChooser fc = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir"));
        fc.setCurrentDirectory(workingDirectory);

        int returnVal = fc.showOpenDialog(getParent());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
            System.out.println("Opening: " + file.getName() + ".");


            //Custom button text
            Object[] options = {"Yes, please",
                    "Nope"};
            int response = JOptionPane.showOptionDialog(getParent(),
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
    }
}
