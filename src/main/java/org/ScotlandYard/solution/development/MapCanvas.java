package org.ScotlandYard.solution.development;

import org.ScotlandYard.objects.Edge;
import org.ScotlandYard.objects.Graph;
import org.ScotlandYard.objects.Route;
import org.ScotlandYard.objects.Ticket;
import org.ScotlandYard.solution.development.models.*;
import org.ScotlandYard.solution.helpers.ColorHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class MapCanvas extends JPanel implements MouseListener, MouseMotionListener {

    public static final int EDIT_POINT_CIRC_SIZE = 8;
    private static final int POS_CIRC_SIZE = 20;
    private int mouseX;
    private int mouseY;
    private ArrayList<ViewPosition> mViewPositionList;
    private ArrayList<ViewPath> mViewPathList;
    private ArrayList<ViewRoute> mViewRouteList;
    private ArrayList<DataPosition> mPositionList;
    private ArrayList<DataPath> mPathList;
    private ArrayList<DataRoute> mRouteList;
    private ToolType currentTool = ToolType.ADD;
    private ViewType currentView = ViewType.NODES;
    private final CanvasInterface mInterface;
    private ViewRoute hoveringRoute;
    private ViewRoute selectedRoute;
    private DataPath hoveringpath;
    private DataPath selectedPath;
    private DataPosition selectedPosition;
    private DataPosition connectingPosition;

    public MapCanvas(CanvasInterface canvasInterface) {
        mInterface = canvasInterface;
        setOpaque(false);
        mPositionList = new ArrayList<DataPosition>();
        mPathList = new ArrayList<DataPath>();
        mRouteList = new ArrayList<DataRoute>();

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setFont(new Font(null, Font.PLAIN, getSize().width / 100));

        FontMetrics fm = g2d.getFontMetrics();

        g2d.setColor(Color.DARK_GRAY);

        //if the view is PREVIEW then we show what the map will look like in reality
        if (currentView == ViewType.PREVIEW) {

            for (ViewPath viewPath : mViewPathList) {
                viewPath.path.draw(g2d);
            }

            g2d.setColor(Color.DARK_GRAY);

            for (ViewPosition viewPosition : mViewPositionList) {

                g2d.setColor(Color.DARK_GRAY);

                int outerRadius = (int) (POS_CIRC_SIZE * 1.3f);
                g2d.fillOval(viewPosition.x - outerRadius / 2, viewPosition.y - outerRadius / 2, outerRadius, outerRadius);

                int segmentAngleSize = (int) (360 / (float) (viewPosition.types.size()));
                for (int i = 0; i < viewPosition.types.size(); i++) {
                    g2d.setColor(ColorHelper.ticketColor(viewPosition.types.get(i)));
                    g2d.fillArc(viewPosition.x - POS_CIRC_SIZE / 2, viewPosition.y - POS_CIRC_SIZE / 2, POS_CIRC_SIZE, POS_CIRC_SIZE, i * segmentAngleSize, segmentAngleSize);
                }

            }

            for (ViewPosition viewPosition : mViewPositionList) {
                final String nodeName = String.valueOf(viewPosition.id);
                Rectangle2D r = fm.getStringBounds(nodeName, g2d);
                int x = viewPosition.x - ((int) r.getWidth() / 2);
                int y = viewPosition.y - ((int) r.getHeight() / 2) + fm.getAscent();
                g2d.setColor(Color.BLUE);
                g.drawString(nodeName, x, y);
            }

            //if the view is ROUTES then we show all the routes of length longer than two nodes
            //so that the user can look for issues and fix them appropriately
        } else if (currentView == ViewType.ROUTES) {

            g2d.setStroke(new BasicStroke(3f));


            Random randomColour = new Random();
            for (ViewRoute viewRoute : mViewRouteList) {
                //we don't care about single path routes
//                if (viewRoute.positionList.size() > 2) {
                g2d.setColor(new Color(randomColour.nextFloat(), randomColour.nextFloat(), randomColour.nextFloat()));

                g2d.draw(viewRoute.path);
//                }
            }

            for (ViewRoute viewRoute : mViewRouteList) {
                //we don't care about single path routes
                if (viewRoute.positionList.size() > 2) {
                    if (selectedRoute != null && selectedRoute.id1 == viewRoute.id1 && selectedRoute.id2 == viewRoute.id2) {

                        g2d.setColor(Color.BLACK);

                        g2d.setStroke(new BasicStroke(5f));

                        g2d.draw(viewRoute.path);
                    } else if (hoveringRoute != null && hoveringRoute.id1 == viewRoute.id1 && hoveringRoute.id2 == viewRoute.id2) {

                        g2d.setColor(Color.darkGray);

                        g2d.setStroke(new BasicStroke(5f));

                        g2d.draw(viewRoute.path);
                    }
                }


            }


            for (ViewPosition viewPosition : mViewPositionList) {
                if (selectedRoute != null && selectedRoute.positionList.contains(new DataPosition(viewPosition.x, viewPosition.y, viewPosition.id))) {
                    g2d.setColor(Color.MAGENTA);
                } else {
                    g2d.setColor(Color.DARK_GRAY);
                }
                g2d.fillOval(viewPosition.x - POS_CIRC_SIZE / 2, viewPosition.y - POS_CIRC_SIZE / 2, POS_CIRC_SIZE, POS_CIRC_SIZE);
            }

            for (ViewPosition viewPosition : mViewPositionList) {
                final String nodeName = String.valueOf(viewPosition.id);
                Rectangle2D r = fm.getStringBounds(nodeName, g2d);
                int x = viewPosition.x - ((int) r.getWidth() / 2);
                int y = viewPosition.y - ((int) r.getHeight() / 2) + fm.getAscent();
                g2d.setColor(Color.WHITE);
                g.drawString(nodeName, x, y);
            }

            //the last view type, NODES is shown when the user wants to view and possibly edit
            //the position of nodes and the paths between them
        } else if (currentView == ViewType.NODES) {

            g2d.setStroke(new BasicStroke(3f));

            if (connectingPosition != null) {
                Path2D.Double connectingPath = new Path2D.Double(Path2D.WIND_EVEN_ODD, 2);
                connectingPath.moveTo(connectingPosition.x, connectingPosition.y);
                connectingPath.lineTo(mouseX, mouseY);
                g2d.draw(connectingPath);
            }

            for (DataPath dataPath : mPathList) {

                if (hoveringpath != null && hoveringpath.id1 == dataPath.id1 && hoveringpath.id2 == dataPath.id2) {
                    g2d.setStroke(new BasicStroke(4f));
                    g2d.setColor(Color.CYAN);
                } else if (selectedPath != null && selectedPath.id1 == dataPath.id1 && selectedPath.id2 == dataPath.id2) {
                    g2d.setStroke(new BasicStroke(4f));
                    g2d.setColor(Color.CYAN);
                } else {
                    g2d.setColor(Color.darkGray);
                    g2d.setStroke(new BasicStroke(3f));
                }

                for (int i = 0; i < dataPath.pathXCoords.length; i++) {
                    g2d.fillOval(dataPath.pathXCoords[i] - EDIT_POINT_CIRC_SIZE / 2, dataPath.pathYCoords[i] - EDIT_POINT_CIRC_SIZE / 2, EDIT_POINT_CIRC_SIZE, EDIT_POINT_CIRC_SIZE);
                }


                g2d.draw(dataPath.getPath());
            }

            for (DataPosition dataPosition : mPositionList) {
                g2d.fillOval(dataPosition.x - POS_CIRC_SIZE / 2, dataPosition.y - POS_CIRC_SIZE / 2, POS_CIRC_SIZE, POS_CIRC_SIZE);
            }

            for (DataPosition dataPosition : mPositionList) {
                final String nodeName = String.valueOf(dataPosition.id);
                Rectangle2D r = fm.getStringBounds(nodeName, g2d);
                int x = dataPosition.x - ((int) r.getWidth() / 2);
                int y = dataPosition.y - ((int) r.getHeight() / 2) + fm.getAscent();
                g2d.setColor(Color.WHITE);
                g.drawString(nodeName, x, y);
            }

        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    private DataPosition addPosition(MouseEvent e) {
        DataPosition dataPosition = new DataPosition(e.getX(), e.getY());
        mPositionList.add(dataPosition);
        return dataPosition;
    }

    private DataPosition selectPosition(MouseEvent e) {
        Rectangle2D.Double rect = new Rectangle2D.Double();
        for (DataPosition position : mPositionList) {
            rect.setRect(position.x - POS_CIRC_SIZE / 2, position.y - POS_CIRC_SIZE / 2, POS_CIRC_SIZE, POS_CIRC_SIZE);
            if (rect.contains(e.getX(), e.getY())) {
                return position;
            }
        }
        return null;
    }

    private DataPath selectPath(MouseEvent e) {
        for (DataPath dataPath : mPathList) {
            BasicStroke stroke = new BasicStroke(3f);
            if (stroke.createStrokedShape(dataPath.getPath()).contains(e.getX(), e.getY())) {
                return dataPath;
            }
        }
        return null;
    }

    private ViewRoute selectRoute(MouseEvent e) {
        for (ViewRoute viewRoute : mViewRouteList) {
            if (viewRoute.positionList.size() > 2) {
                BasicStroke stroke = new BasicStroke(3f);
                if (stroke.createStrokedShape(viewRoute.path).contains(e.getX(), e.getY())) {
                    return viewRoute;
                }
            }
        }
        return null;
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (currentView == ViewType.NODES) {
            unselectCurrentSelectedPosition();
            DataPosition clickedPosition = selectPosition(e);
            switch (currentTool) {
                case ADD:
                    selectedPosition = clickedPosition;
                    if (selectedPosition == null) {
                        selectedPosition = addPosition(e);
                    }
                    mInterface.onPositionSelected(selectedPosition);
                    repaint();
                    break;
                case CONNECT:
                    if (clickedPosition != null) {
                        if (connectingPosition == null) {
                            connectingPosition = clickedPosition;
                        } else {
                            if (connectingPosition.id == clickedPosition.id) {
                                System.err.println("Cannot link a position to itself");
                                connectingPosition = null;
                                break;
                            }

                            for (DataPath dataPath : mPathList) {
                                if ((dataPath.id1 == connectingPosition.id && dataPath.id2 == clickedPosition.id) || (dataPath.id2 == connectingPosition.id && dataPath.id1 == clickedPosition.id)) {
                                    System.err.println("Path already exists with " + dataPath.id1 + " & " + dataPath.id2);
                                    connectingPosition = null;
                                    break;
                                }
                            }
                            DataPath dataPath = new DataPath(connectingPosition.id, clickedPosition.id);
                            dataPath.pathXCoords = new int[]{connectingPosition.x, clickedPosition.x};
                            dataPath.pathYCoords = new int[]{connectingPosition.y, clickedPosition.y};
                            mPathList.add(dataPath);
                            connectingPosition = null;
                        }
                        repaint();
                    }
                    break;
                case EDIT:
                    selectedPath = selectPath(e);
                    if (selectedPath != null) {
                        selectedPath.onSelected(e.getX(), e.getY());
                    }
                    repaint();

            }
        } else if (currentView == ViewType.ROUTES) {
            if (currentTool == ToolType.EDIT) {
                ViewRoute route = selectRoute(e);
                selectedRoute = route;
                repaint();
            } else if (currentTool == ToolType.ADD) {
                if (selectedRoute != null) {
                    DataPosition position = selectPosition(e);
                    for (DataRoute dataRoute : mRouteList) {
                        if (dataRoute.waypointIdList.get(0) == selectedRoute.id1 && dataRoute.waypointIdList.get(dataRoute.waypointIdList.size() - 1) == selectedRoute.id2
                                || dataRoute.waypointIdList.get(0) == selectedRoute.id2 && dataRoute.waypointIdList.get(dataRoute.waypointIdList.size() - 1) == selectedRoute.id1) {
                            PathIterator iterator = selectedRoute.path.getPathIterator(null);
                            dataRoute.waypointIdList.add(1, position.id);
                            System.out.println("new waypoint list is " + dataRoute.waypointIdList);
                        }
                    }
                    calculateViewObjects();
                    for (ViewRoute viewRoute : mViewRouteList) {
                        if (viewRoute.id1 == selectedRoute.id1 && viewRoute.id2 == selectedRoute.id2
                                || viewRoute.id1 == selectedRoute.id2 && viewRoute.id2 == selectedRoute.id1) {
                            selectedRoute = viewRoute;
                        }
                    }
                }
            }

        }
    }

    private void unselectCurrentSelectedPosition() {
        if (selectedPosition != null) {
            if (selectedPosition.id <= 0) {
                mPositionList.remove(selectedPosition);
            } else {
                selectedPosition = null;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (currentView == ViewType.NODES) {
            if (currentTool == ToolType.EDIT) {
                if (selectedPath != null) {
                    selectedPath.onDragStop();
                    selectedPath = null;
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (currentView == ViewType.NODES) {
            switch (currentTool) {
                case MOVE:
                    selectedPosition = selectPosition(e);
                    if (selectedPosition != null) {
                        Rectangle2D.Double rect = new Rectangle2D.Double();
                        rect.setRect(selectedPosition.x, selectedPosition.y, POS_CIRC_SIZE, POS_CIRC_SIZE);

                        if (rect.contains(e.getX(), e.getY())) {

                            selectedPosition.x = e.getX();
                            selectedPosition.y = e.getY();

                            for (DataPath dataPath : mPathList) {
                                if (dataPath.id1 == selectedPosition.id || dataPath.id2 == selectedPosition.id) {

                                }
                            }

                        }

                    }
                    break;
                case EDIT:
                    if (selectedPath != null) {
                        selectedPath.onPointDrag(e.getX(), e.getY());
                    }
                    repaint();
                    break;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        if (currentView == ViewType.NODES) {
            if (currentTool == ToolType.EDIT) {
                hoveringpath = selectPath(e);
                repaint();
            } else {
                if (connectingPosition != null) {
                    repaint();
                }
            }


        } else if (currentView == ViewType.ROUTES) {
            ViewRoute route = selectRoute(e);
            hoveringRoute = route;
            repaint();
        }
    }

    public boolean setSelectedPositionId(int id) {

        if (selectedPosition == null || id <= 0) {
            return false;
        }

        for (DataPosition dataPosition : mPositionList) {
            if (dataPosition.x == selectedPosition.x && dataPosition.y == selectedPosition.y) {
                dataPosition.id = id;
                selectedPosition.id = id;
                repaint();
                return true;
            } else if (dataPosition.id == id) {
                return false;
            }
        }

        return false;
    }

    public DataSave getData() {
        return new DataSave(mPositionList, mPathList, mRouteList);
    }

    public void setData(DataSave savedData) {
        mPositionList = savedData.positionList;
        mPathList = savedData.pathList;
        mRouteList = savedData.routeList;

        if (mPositionList == null) {
            mPositionList = new ArrayList<DataPosition>();
        }

        if (mPathList == null) {
            mPathList = new ArrayList<DataPath>();
        }

        if (mRouteList == null) {
            mRouteList = new ArrayList<DataRoute>();
        }

        repaint();
    }

    public void setViewMode(ViewType preview) {
        currentView = preview;
        selectedRoute = null;

        if (currentView == ViewType.PREVIEW || currentView == ViewType.ROUTES) {
            calculateViewObjects();
        }
        repaint();
    }


    /**
     * this uses the data model objects to construct view objects to represent the map
     * more realistically
     */
    private void calculateViewObjects() {

        mViewPositionList = new ArrayList<ViewPosition>();
        mViewPathList = new ArrayList<ViewPath>();
        mViewRouteList = new ArrayList<ViewRoute>();

        for (DataRoute dataRoute : mRouteList) {
            mViewRouteList.add(new ViewRoute(dataRoute, mPositionList, mPathList));
        }

        for (DataPosition dataPosition : mPositionList) {
            ViewPosition viewPosition = new ViewPosition(dataPosition);
            mViewPositionList.add(viewPosition);

            ArrayList<Ticket> ticketTypes = new ArrayList<Ticket>();
            for (ViewRoute viewRoute : mViewRouteList) {
                if (viewRoute.positionList.get(0).id == dataPosition.id || viewRoute.positionList.get(viewRoute.positionList.size() - 1).id == dataPosition.id) {
                    ticketTypes.add(viewRoute.type);
                }
            }

            viewPosition.setTypes(ticketTypes);

        }

        for (DataPath dataPath : mPathList) {
            ViewPath viewPath = new ViewPath(dataPath);
            mViewPathList.add(viewPath);

            ArrayList<Ticket> ticketTypes = new ArrayList<Ticket>();

            for (ViewRoute viewRoute : mViewRouteList) {
                for (int i = 0; i < viewRoute.positionList.size() - 1; i++) {
                    if (viewRoute.positionList.get(i).id == dataPath.id1 && viewRoute.positionList.get(i + 1).id == dataPath.id2
                            || viewRoute.positionList.get(i).id == dataPath.id2 && viewRoute.positionList.get(i + 1).id == dataPath.id1) {
                        ticketTypes.add(viewRoute.type);
                    }

                }


            }

            viewPath.setTypes(ticketTypes);


        }


        repaint();
    }

    public void setGraph(Graph<Integer, Route> graph) {

        mRouteList.clear();

        for (Edge<Integer, Route> edge : graph.getEdges()) {
            mRouteList.add(new DataRoute(edge.source(), edge.target(), edge.data()));
        }

        System.out.println("routes calculated!");

    }

    public void setTool(ToolType tool) {
        this.currentTool = tool;
        unselectCurrentSelectedPosition();
        connectingPosition = selectedPosition = null;
    }

    public enum ViewType {PREVIEW, NODES, ROUTES}

    public enum ToolType {ADD, MOVE, CONNECT, EDIT}

    public interface CanvasInterface {
        void onPositionSelected(DataPosition pos);


    }
}
