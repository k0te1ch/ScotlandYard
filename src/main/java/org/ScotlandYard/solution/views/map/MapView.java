package solution.views.map;

import scotlandyard.*;
import solution.Constants;
import solution.Models.MapData;
import solution.development.models.ViewRoute;
import solution.helpers.ColourHelper;
import solution.helpers.PathInterpolator;
import solution.helpers.RouteHelper;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;
import solution.views.FloatingDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapView extends JPanel implements MapNodePopup.PopupInterface {
    private final GameControllerInterface mControllerInterface;
    private final MapData mMapData;
    private final MapNodePopup mMapPopup;
    private BufferedImage mBgPathImage;
    private BufferedImage mBgPositionImage;
    private BufferedImage mGraphImage;
    private BufferedImage mMapImage;
    private MoveTicket firstMove;
    private List<MoveTicket> secondMoves;
    private AffineTransform transform;
    private AffineTransform inverseTransform;
    private Dimension mImageSize;
    private TransportSprite transportSprite;
    private AnimationWorker animationWorker;
    private List<MoveTicket> mCurrentMoveList;
    private FloatingDialog mFloatingDialog;
    private boolean hasPainted;
    private GameAdapter mListener;
    private int mBackgroundWidth;
    private int mBackgroundHeight;
    private boolean mShouldCheckDimensions;

    //we store some hardcoded data for the image
    static class MapImageInfo {
        public static int topMargin = 63;
        public static int leftMargin = 75;
        public static int bottomMargin = 71;
        public static int rightMargin = 72;
        public static float imageWidth = 819f;
        public static float imageHeight = 1058f;
    }
    
    public MapView(final GameControllerInterface controllerInterface, final String graphImageMapPath, final MapData mapData) {
        mControllerInterface = controllerInterface;
        mMapData = mapData;
        setOpaque(false);

        transform = new AffineTransform();
        inverseTransform = new AffineTransform();
        animationWorker = new AnimationWorker(this);
        addMouseListener(new GraphMouseListener());
        addMouseMotionListener(new GraphMouseListener());
        setupGraphImage(graphImageMapPath);
        mMapPopup = new MapNodePopup(this);

        createBgPathImage();
        createBgPositionImage();

        invalidateMapDimensions();

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {

                invalidateMapDimensions();

                setTransform();
                repaint();
            }

        });


    }

    /**
     * sets up the transformation for use later
     */
    private void setTransform() {

        double topMarginRatio = ((double)MapImageInfo.topMargin) / MapImageInfo.imageWidth;
        double leftMarginRatio = ((double)MapImageInfo.leftMargin) / MapImageInfo.imageHeight;

        int scaledLeftPos = (int)(leftMarginRatio * getWidth());
        int scaledTopPos =  (int)(topMarginRatio * getHeight());

        float scaleX = (float)getWidth() / mImageSize.width;
        float scaleY = (float)getHeight() / mImageSize.height;

        transform = new AffineTransform();
        transform.translate(scaledLeftPos, scaledTopPos);
        transform.scale(scaleX * 0.858, scaleY * 0.8);

        try {
            inverseTransform = transform.createInverse();
        } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * we remove the {@link solution.interfaces.GameControllerInterface} here
     */
    @Override
    public void removeNotify() {
        super.removeNotify();

        mControllerInterface.removeUpdateListener(mListener);
    }

    /**
     * we add the {@link solution.interfaces.GameControllerInterface} here
     */
    @Override
    public void addNotify() {
        super.addNotify();

        mListener = new GameAdapter();
        mControllerInterface.addUpdateListener(mListener);
        mListener.onGameModelUpdated(mControllerInterface);

    }

    private void setupGraphImage(final String graphImageMapPath) {
        try {
            URL resource = getClass().getClassLoader().getResource(graphImageMapPath);
            mGraphImage = ImageIO.read(new File(resource.toURI()));
            resource = getClass().getClassLoader().getResource("ui" + File.separator + "mapbg.png");
            mMapImage = ImageIO.read(new File(resource.toURI()));
            mImageSize = new Dimension(mGraphImage.getWidth(), mGraphImage.getHeight());
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        //antialiasing makes everything look nicer
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        //check to make sure the map border is correct
        ensureMapDimensions();

        //draw the map border
        g2d.drawImage(mMapImage, 0,0,mBackgroundWidth, mBackgroundHeight, this);


        //apply the image transformation to scale all of the following
        g2d.setTransform(transform);

        //draw the map image
        g2d.drawImage(mGraphImage, null, 0, 0);

        //draw the cached path image
        g2d.drawImage(mBgPathImage, null, 0, 0);

        //draw live path data
        for (MapPath mapPath : mMapData.getPathList()) {
            if (mapPath.isAvailable()) {
                mapPath.drawBackground(g2d);
            }
        }

        for (MapPath mapPath : mMapData.getPathList()) {
            if (mapPath.isAvailable()) {
                mapPath.draw(g2d);
            }
        }

        //draw the cached position image
        g2d.drawImage(mBgPositionImage, null, 0, 0);

        //draw the live position data
        for (MapPosition position : mMapData.getPositionList()) {
            if (position.isAvailable() || position.isHighlighted() || position.hasPlayerColor()) {
                position.draw(g2d);
            }
        }

        g2d.setColor(Color.GREEN);

        //draw any currently active sprites
        if (transportSprite != null) {
            transportSprite.draw(g2d);
        }

        //draw any popups currently active
        if (mMapPopup != null) {
            mMapPopup.draw(g2d);
        }

        //draw the floating text dialog if it exists
        if (mFloatingDialog != null) {
            mFloatingDialog.draw(g2d);
        }

        onPaintComplete();

    }

    /**
     * lets the map know it should recalculate the map dimensions
     */
    private void invalidateMapDimensions(){
        mShouldCheckDimensions = true;
    }
    /**
     * calculates dimensions for border image
     */
    private void ensureMapDimensions() {
        if(mShouldCheckDimensions) {
            mShouldCheckDimensions = false;
            double topMarginRatio = ((double) MapImageInfo.topMargin) / MapImageInfo.imageWidth;
            double bottomMarginRatio = ((double) (MapImageInfo.bottomMargin + MapImageInfo.topMargin)) / MapImageInfo.imageWidth;
            double leftMarginRatio = ((double) MapImageInfo.leftMargin) / MapImageInfo.imageHeight;
            double rightMarginRatio = ((double) (MapImageInfo.rightMargin + MapImageInfo.leftMargin)) / MapImageInfo.imageHeight;

            double bottomMarginRatioBE = ((double) (38)) / MapImageInfo.imageWidth;
            double rightMarginRatioBE = ((double) (65)) / MapImageInfo.imageHeight;

            int scaledLeftPos = (int) (leftMarginRatio * getWidth());
            int scaledTopPos = (int) (topMarginRatio * getHeight());
            int scaledRightPos = (int) (rightMarginRatioBE * getWidth());
            int scaledBottomPos = (int) (bottomMarginRatioBE * getHeight());
            int scaledWidth = (int) (getWidth() - (getWidth() * rightMarginRatio));
            int scaledHeight = (int) (getHeight() - (getHeight() * bottomMarginRatio));

            mBackgroundWidth = scaledWidth + scaledLeftPos + scaledRightPos;
            mBackgroundHeight = scaledHeight + scaledTopPos + scaledBottomPos;
        }
    }

    /**
     * This is run after the first paint to let the interface know
     * that we've loaded the map and all associated images
     */
    private void onPaintComplete() {
        if (!hasPainted) {
            hasPainted = true;
            mControllerInterface.notifyMapLoaded();
        }
    }

    /**
     * shows a dialog for the gameover state
     */
    public void showGameOverView() {
        int width = mGraphImage.getWidth();
        int height = mGraphImage.getHeight();
        mFloatingDialog = FloatingDialog.getGameoverInstance(mControllerInterface, width, height);
        repaint();
    }

    /**
     * shows a dialog for the case where a player cannot move
     */
    private void showMovePass() {
        int width = mGraphImage.getWidth();
        int height = mGraphImage.getHeight();
        final Colour currentPlayer = mControllerInterface.getCurrentPlayer();
        mFloatingDialog = FloatingDialog.getMovePassInstance(currentPlayer, width, height);

        new Thread(new Runnable() {
            @Override
            public void run() {

                //we wait 3 seconds before continuing
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mFloatingDialog = null;
                mControllerInterface.notifyMoveSelected(new MovePass(currentPlayer));
            }
        }).start();
        repaint();
    }

    /**
     * creates a cached image of all paths so that we don't need to redraw all paths on {@link #paintComponent(java.awt.Graphics)}
     */
    private void createBgPathImage() {

        mBgPathImage = new BufferedImage(mImageSize.width, mImageSize.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = mBgPathImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setStroke(new BasicStroke(5f));

        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));


        for (MapPath mapPath : mMapData.getPathList()) {
            mapPath.drawBackground(g2d);
        }

        for (MapPath mapPath : mMapData.getPathList()) {
            mapPath.draw(g2d);
        }

        g2d.dispose();
    }

    /**
     * creates a cached image of all positions so that we don't need to redraw all positions
     * on {@link #paintComponent(java.awt.Graphics)}
     */
    private void createBgPositionImage() {


        mBgPositionImage = new BufferedImage(mImageSize.width, mImageSize.height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = mBgPositionImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (MapPosition position : mMapData.getPositionList()) {
            position.draw(g2d);
        }

        g2d.dispose();


    }

    @Override
    public void onTicketSelected(final Ticket ticket, final int posId) {
        final Colour currentPlayer = mControllerInterface.getCurrentPlayer();
        final MoveTicket singleMove = new MoveTicket(currentPlayer, posId, ticket);

        if (secondMoves != null && firstMove != null) {
            mControllerInterface.notifyMoveSelected(new MoveDouble(mControllerInterface.getCurrentPlayer(), firstMove, singleMove));
            firstMove = null;
            secondMoves = null;
        } else {
            mControllerInterface.notifyMoveSelected(singleMove);
        }

        mMapPopup.reset();
        repaint();
    }

    /**
     * Animates the the current player through the two moves
     *
     * @param firstMove the first move to animate
     * @param secondMove the second move to animate, may be null
     */
    private void animateMove(final MoveTicket firstMove, final MoveTicket secondMove) {

        PathInterpolator firstMoveInterpolator = null;
        PathInterpolator secondMoveInterpolator = null;

        for (ViewRoute viewRoute : mMapData.getRouteList()) {
            if (RouteHelper.routeContains(viewRoute, mControllerInterface.getCurrentPlayerRealPosition(), firstMove.target)) {
                firstMoveInterpolator = new PathInterpolator(viewRoute.path);
                if (viewRoute.id1 == firstMove.target) {
                    firstMoveInterpolator.reverse();
                }
                if (secondMove == null || secondMoveInterpolator != null) {
                    break;
                }
            }

            if (secondMove != null) {
                if (RouteHelper.routeContains(viewRoute, firstMove.target, secondMove.target)) {

                    secondMoveInterpolator = new PathInterpolator(viewRoute.path);
                    if (viewRoute.id1 == secondMove.target) {
                        secondMoveInterpolator.reverse();
                    }
                    if (firstMoveInterpolator != null) {
                        break;
                    }
                }
            }
        }

        if (firstMoveInterpolator != null) {

            firstMoveInterpolator.interpolate(2f);

            transportSprite = new TransportSprite(firstMove.ticket);


            final PathInterpolator finalFirstMoveInterpolator = firstMoveInterpolator;
            final PathInterpolator finalSecondMoveInterpolator = secondMoveInterpolator;
            animationWorker.addWork(new AnimationWorker.AnimationInterface() {
                @Override
                public boolean onTick() {


                    transportSprite.setSegment(finalFirstMoveInterpolator.getCurrentSegment());

                    finalFirstMoveInterpolator.nextSegment();

                    return finalFirstMoveInterpolator.isDone();
                }

                @Override
                public void onFinished() {

                    if (finalSecondMoveInterpolator != null) {


                        finalSecondMoveInterpolator.interpolate(2f);

                        transportSprite = new TransportSprite(secondMove.ticket);


                        animationWorker.addWork(new AnimationWorker.AnimationInterface() {
                            @Override
                            public boolean onTick() {


                                transportSprite.setSegment(finalSecondMoveInterpolator.getCurrentSegment());

                                finalSecondMoveInterpolator.nextSegment();

                                return finalSecondMoveInterpolator.isDone();
                            }

                            @Override
                            public void onFinished() {

                                transportSprite = null;
                                secondMoves = null;
                                MapView.this.firstMove = null;
                                mControllerInterface.notifyMoveAnimationFinished();
                            }
                        });

                    } else {
                        transportSprite = null;
                        mControllerInterface.notifyMoveAnimationFinished();
                    }

                }
            });

        }

    }

    @Override
    public void onDoubleMoveSelected(Ticket ticket, int posId) {
        final Colour currentPlayer = mControllerInterface.getCurrentPlayer();
        final MoveTicket moveTicket = new MoveTicket(currentPlayer, posId, ticket);

        secondMoves = mControllerInterface.getValidSecondMovesAtLocation(currentPlayer, posId, ticket);
        firstMove = moveTicket;

        showValidMoves(secondMoves, posId);
        mMapPopup.reset();

        repaint();

    }

    @Override
    public void onDoubleMoveCancelled(Ticket mSelectedTicket, int posId) {

        secondMoves = null;
        firstMove = null;
        mMapPopup.reset();
        Colour currentPlayer = mControllerInterface.getCurrentPlayer();
        int currentPlayerLocation = mControllerInterface.getCurrentPlayerRealPosition();
        showValidMoves(mControllerInterface.getValidSingleMovesAtLocation(currentPlayer, currentPlayerLocation), currentPlayerLocation);

        repaint();

    }

    private void showValidMoves(List<MoveTicket> moves, int currentPosition) {

        mCurrentMoveList = moves;
        for (MapPosition mapPosition : mMapData.getPositionList()) {
            mapPosition.setAvailable(false);
            mapPosition.setHighlighted(mapPosition.getId() == currentPosition);
            for (MoveTicket moveTicket : moves) {
                if (mapPosition.getId() == moveTicket.target) {
                    mapPosition.setAvailable(true);
                }
            }
        }


        for (MapPath mapPath : mMapData.getPathList()) {

            mapPath.resetAvailableTickets();

            for (ViewRoute viewRoute : mMapData.getRouteList()) {
                Ticket availableTicket = null;
                for (MoveTicket moveTicket : moves) {
                    if (RouteHelper.routeContains(viewRoute, currentPosition, moveTicket.target)) {
                        availableTicket = viewRoute.type;
                        break;
                    }
                }
                if (availableTicket != null) {
                    for (int i = 0; i < viewRoute.positionList.size() - 1; i++) {
                        if (viewRoute.positionList.get(i).id == mapPath.getStartingNode() && viewRoute.positionList.get(i + 1).id == mapPath.getEndingNode()
                                || viewRoute.positionList.get(i).id == mapPath.getEndingNode() && viewRoute.positionList.get(i + 1).id == mapPath.getStartingNode()) {
                            mapPath.addAvailableTicket(availableTicket);
                            break;
                        }

                    }
                }


            }

        }

        repaint();
    }
    class GraphMouseListener extends MouseAdapter implements MouseMotionListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            Point2D transformedPoint = inverseTransform.transform(new Point2D.Double(e.getX(), e.getY()), null);

            if (transportSprite != null) {
                //return, because we're animating
                return;
            }

            final boolean popupClicked = mMapPopup != null && mMapPopup.onClick((int) transformedPoint.getX(), (int) transformedPoint.getY());
            if (!popupClicked) {
                for (MapPosition position : mMapData.getPositionList()) {
                    if (position.notifyMouseClick((int) transformedPoint.getX(), (int) transformedPoint.getY())) {
                        final boolean isMrX = mControllerInterface.getCurrentPlayer() == Constants.MR_X_COLOUR;
                        final boolean hasEnoughDoubleMoves = mControllerInterface.getPlayerTickets(Constants.MR_X_COLOUR).get(Ticket.DoubleMove) > 0;

                        MapNodePopup.DoubleMoveState doubleMoveState;

                        if (isMrX && hasEnoughDoubleMoves && secondMoves == null) {
                            doubleMoveState = MapNodePopup.DoubleMoveState.ALLOWED_NOT_STARTED;
                        } else if (isMrX && secondMoves != null) {
                            doubleMoveState = MapNodePopup.DoubleMoveState.ALLOWED_STARTED;
                        } else {
                            doubleMoveState = MapNodePopup.DoubleMoveState.NOT_ALLOWED;
                        }

                        ArrayList<Ticket> availableTickets = new ArrayList<Ticket>();
                        for (MoveTicket moveTicket : mCurrentMoveList) {
                            if (moveTicket.target == position.getId()) {
                                availableTickets.add(moveTicket.ticket);
                            }
                        }

                        mMapPopup.create(position, getSize(), doubleMoveState, availableTickets);
                    }
                }
            }
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Point2D transformedPoint = inverseTransform.transform(new Point2D.Double(e.getX(), e.getY()), null);

            boolean positionHovered = false;
            final boolean popupHovered = mMapPopup != null && mMapPopup.onMouseMoved((int) transformedPoint.getX(), (int) transformedPoint.getY());
            if (!popupHovered) {
                for (MapPosition position : mMapData.getPositionList()) {
                    if (position.notifyMouseMove((int) transformedPoint.getX(), (int) transformedPoint.getY())) {
                        positionHovered = true;
//                        for (MapPath path : mMapData.getPathList()) {
//                            path.notifyPositionHovered(position);
//                        }
                    }

                }
            }

            if (!positionHovered && !popupHovered) {

                if (mMapPopup.isShowing()) {
                    mMapPopup.reset();
                }
//                for (MapPath path : mMapData.getPathList()) {
//                    path.notifyPositionHovered(null);
//                }
            }

//            createBgPositionImage();
            repaint();

        }
        @Override
        public void mouseEntered(MouseEvent e) {
            Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            setCursor(cursor);
        }

    }
    class GameAdapter extends GameUIAdapter {

        @Override
        public void onGameModelUpdated(GameControllerInterface controllerInterface) {


            if (!mControllerInterface.isGameOver()) {

                for (MapPosition mapPosition : mMapData.getPositionList()) {
                    mapPosition.setPlayerColor(null);
                }
                int realPlayerLocation = mControllerInterface.getCurrentPlayerRealPosition();
                MapPosition currentPlayerPosition = null;
                MapPosition mrXPosition = null;
                for (Colour colour : mControllerInterface.getPlayerList()) {

                    int location = mControllerInterface.getPlayerVisiblePosition(colour);

                    if (mControllerInterface.getCurrentPlayer() == Constants.MR_X_COLOUR && colour == Constants.MR_X_COLOUR && (colour == mControllerInterface.getCurrentPlayer() || mControllerInterface.isMrXVisible())) {
                        location = realPlayerLocation;
                    }
                    System.out.println(ColourHelper.toString(colour) + " @ " + location);
                    for (MapPosition mapPosition : mMapData.getPositionList()) {

                        if(mapPosition.getId() == mControllerInterface.getPlayerVisiblePosition(Constants.MR_X_COLOUR)){
                            mrXPosition = mapPosition;
                        }

                        if (mapPosition.getId() == realPlayerLocation) {
                            currentPlayerPosition = mapPosition;
                        }

                        if (mapPosition.getId() == location) {
                            mapPosition.setPlayerColor(ColourHelper.toColor(colour));


                            break;
                        }
                    }
                }
                if (currentPlayerPosition != null) {
                    final MapPosition finalCurrentPlayerPosition = currentPlayerPosition;
                    final MapPosition finalMrXPosition = mrXPosition;
                    animationWorker.addWork(new AnimationWorker.AnimationInterface() {

                        private float TICK_LIMIT = 50;
                        private int tick;

                        @Override
                        public boolean onTick() {

                            if(finalMrXPosition != null) {
                                finalMrXPosition.setPlayerRingAlpha(1f - tick / TICK_LIMIT);
                                finalMrXPosition.setPlayerRingRadius((int) (MapPosition.CIRC_RADIUS * (tick * 4 / TICK_LIMIT)));
                            }

                            finalCurrentPlayerPosition.setPlayerRingAlpha(1f - tick / TICK_LIMIT);
                            finalCurrentPlayerPosition.setPlayerRingRadius((int) (MapPosition.CIRC_RADIUS * (tick * 4 / TICK_LIMIT)));

                            tick++;
                            if (tick > TICK_LIMIT) {
                                tick = 0;
                            }

                            return !ColourHelper.toColor(mControllerInterface.getCurrentPlayer()).equals(finalCurrentPlayerPosition.getPlayerColor());
                        }

                        @Override
                        public void onFinished() {

                            if(finalMrXPosition != null) {
                                finalMrXPosition.setPlayerRingAlpha(1f - tick / TICK_LIMIT);
                                finalMrXPosition.setPlayerRingRadius((int) (MapPosition.CIRC_RADIUS * (tick * 4 / TICK_LIMIT)));
                            }

                            finalCurrentPlayerPosition.setPlayerRingAlpha(1f);
                            finalCurrentPlayerPosition.setPlayerRingRadius(MapPosition.CIRC_RADIUS);
                        }
                    });
                }
                final Colour currentPlayer = mControllerInterface.getCurrentPlayer();

                List<MoveTicket> validMoves = mControllerInterface.getValidSingleMovesAtLocation(currentPlayer, realPlayerLocation);

                if(validMoves.size() > 0) {
                    showValidMoves(validMoves, realPlayerLocation);
                }else{
                    showMovePass();
                }
            }

            repaint();
        }
        @Override
        public void animateMove(MoveTicket firstMove, MoveTicket secondMove) {
            MapView.this.animateMove(firstMove, secondMove);
        }

    }


}
