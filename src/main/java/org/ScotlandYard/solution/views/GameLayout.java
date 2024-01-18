package solution.views;

import com.sun.deploy.util.StringUtils;
import scotlandyard.Colour;
import solution.Models.MapData;
import solution.helpers.ColourHelper;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;
import solution.views.map.MapView;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rory on 10/03/15.
 */
public class GameLayout extends JPanel {
    private MapView mapView;
    private SideBarView sbView;
    private GameControllerInterface mControllerInterface;
    GridBagConstraints mGridLayout;

    public GameLayout(GameControllerInterface controllerInterface, PlayerInfoBar.PlayerInfoBarListener listener) {
        // Set Opaque
        setOpaque(false);

        // Set to a percentage layout
        setLayout(new GridBagLayout());

        // Set the listeners
        GameAdapter gameAdapter = new GameAdapter();
        controllerInterface.addUpdateListener(gameAdapter);
        mControllerInterface = controllerInterface;

        // Create a new bar
        PlayerInfoBar playerInfoBar = new PlayerInfoBar(controllerInterface);
        playerInfoBar.setListener(listener);

        // Setup the global grid
        setupGridLayout();

        // Set up map Panel
        JPanel mapViewContainer = new JPanel();
        mapViewContainer.setLayout(new BorderLayout());
        mapViewContainer.setOpaque(false);
        mapViewContainer.setBorder(new EmptyBorder(20,20,20,20));

        // Load in the map view
        mapView = new MapView(controllerInterface, "pirate_map.png", new MapData("custom_data", MapData.DataFormat.CUSTOM));
        mapView.setBorder(new EmptyBorder(20,20,20,20));

        // Set Dimensions
        final Dimension dimension = new Dimension(1000, 800);
        mapView.setPreferredSize(dimension);
        mapView.setMinimumSize(dimension);


        // Load in the map
        mapViewContainer.add(mapView);

        // Setup top view container
        JPanel subLayout = new JPanel();
        subLayout.setLayout(new GridBagLayout());
        subLayout.setOpaque(false);

        // Set up the sidebar
        sbView = new SideBarView(controllerInterface);

        // Setup Layout
        GridBagConstraints gbcInside = setupInsideGrid(mapViewContainer, subLayout);

        // Add in the map
        subLayout.add(mapViewContainer, gbcInside);

        // change current grid position
        gbcInside.gridx = 1;
        gbcInside.weightx = 30;


        // Add in the sidebar
        subLayout.add(sbView, gbcInside);

        // Add in the sublayout
        add(subLayout, mGridLayout);

        // Add in the player bar
        mGridLayout.gridy = 1;
        mGridLayout.weighty = 10;
        add(playerInfoBar, mGridLayout);

        gameAdapter.onGameModelUpdated(controllerInterface);
    }

    private GridBagConstraints setupInsideGrid(JPanel mapViewContainer, JPanel subLayout) {
        GridBagConstraints gbcInside = new GridBagConstraints();
        gbcInside.gridy = gbcInside.gridx = 0;
        gbcInside.weightx = 70;
        gbcInside.weighty = 100;

        return gbcInside;
    }

    private void setupGridLayout() {
        mGridLayout = new GridBagConstraints();
        mGridLayout.gridy = mGridLayout.gridx = 0;
        mGridLayout.gridwidth = mGridLayout.gridheight = 1;
        mGridLayout.fill = GridBagConstraints.BOTH;
        mGridLayout.anchor = GridBagConstraints.NORTHWEST;
        mGridLayout.weightx = 100;
        mGridLayout.weighty = 90;
    }

    class GameAdapter extends GameUIAdapter {
        @Override
        public void onGameModelUpdated(GameControllerInterface controllerInterface) {

            // If the game is over then show the gameover menu
            if(!controllerInterface.isGameOver()) {
                System.out.println("It is " + ColourHelper.toString(controllerInterface.getCurrentPlayer()) + "'s turn");
            }else{
                // Print out the winning players in the log
                List<String> winningPlayers = new ArrayList<String>();
                for(Colour winningColour : controllerInterface.getWinningPlayers()){
                    winningPlayers.add(ColourHelper.toString(winningColour));
                }
                System.out.println("Gameover! " + StringUtils.join(winningPlayers, ", ") + " won!");

                // Show the game over view
                mapView.showGameOverView();

            }
        }
    }

}
