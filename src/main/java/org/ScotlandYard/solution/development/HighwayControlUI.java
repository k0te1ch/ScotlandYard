package org.ScotlandYard.solution.development;

import org.ScotlandYard.objects.ScotlandYardGraphReader;
import org.ScotlandYard.solution.development.models.DataPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class HighwayControlUI extends JFrame implements MapCanvas.CanvasInterface {
	public static final String ADD_EDIT_NODE_TOOL = "add_node_tool";
	public static final String MOVE_ELEMENT_TOOL = "edit_node_tool";
	public static final String CONNECT_NODE_TOOL = "connect_node_tool";
	public static final String EDIT_PATH_TOOL = "edit_path_tool";
	private static final String MENU_LOAD = "menu_load";
    private static final String MENU_LOAD_GRAPH = "menu_load_graph";
    private static final String MENU_SAVE_RAW = "menu_save";
    public static final String PREVIEW = "preview";
    public static final String EDIT_NODES = "edit_nodes";
    public static final String VIEW_UNDERGROUND = null;
    private static final String EDIT_ROUTES = "edit_routes";

    private final JPanel mImagePanel;
	private final MapCanvas mCanvas;
	private final DataParser mDataParser;
	private String mCurrentTool = ADD_EDIT_NODE_TOOL;
    private String mCurrentView = PREVIEW;
    private JFileChooser fileChooser;
    private JTextField selectedNodeTextField;
    private JButton deleteSelectedNodeButton;
    private final ActionListener mMenuListener = new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if(MENU_LOAD.equals(e.getActionCommand()) || MENU_LOAD_GRAPH.equals(e.getActionCommand())){
				int returnVal = fileChooser.showOpenDialog(HighwayControlUI.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
                        if(MENU_LOAD.equals(e.getActionCommand())) {
                            mCanvas.setData(mDataParser.loadV3Data(file));
                        }else{
                            mCanvas.setGraph(new ScotlandYardGraphReader().readGraph(file.getAbsolutePath()));
                        }
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.out.println("Opening: " + file.getName() + ".");
				} else {
					System.out.println("Load command cancelled by user.");
				}
			}else if(MENU_SAVE_RAW.equals(e.getActionCommand())){
				int returnVal = fileChooser.showSaveDialog(HighwayControlUI.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
                        if(MENU_SAVE_RAW.equals(e.getActionCommand())) {
                            mDataParser.saveV3Data(mCanvas.getData(), file);
                        }
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.out.println("Saving: " + file.getName() + ".");
				} else {
					System.out.println("Save command cancelled by user.");
				}
			}
		}
	};

    public HighwayControlUI(final File mapImage)
	{

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Highway Control");


		mImagePanel = new JPanel();
		mImagePanel.setLayout(null);

		ImageIcon image = new ImageIcon(mapImage.getAbsolutePath());
		JLabel imageLabel = new JLabel(image);
		imageLabel.setBounds(0, 0, image.getIconWidth(), image.getIconHeight());
		imageLabel.setVisible(true);





		mCanvas = new MapCanvas(this);
		mCanvas.setSize(image.getIconWidth(), image.getIconHeight());

		mImagePanel.add(mCanvas);
		mImagePanel.add(imageLabel);

		JPanel toolbarPanel = createToolbar();


		add(mImagePanel);
		add(toolbarPanel);

		setSize(image.getIconWidth() + 200, image.getIconHeight()+100);

		createMenu();

		updateImageCursor();

		mDataParser = new DataParser();


	}
	private JPanel createToolbar() {
		JPanel toolbarPanel = new JPanel();
		toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.Y_AXIS));


		JPanel idPanel = new JPanel();
		idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS));

		JLabel idTitle = new JLabel();
		idTitle.setText("Name:");


		selectedNodeTextField = new JTextField();
		selectedNodeTextField.setMaximumSize(new Dimension(100, 40));
		selectedNodeTextField.setEnabled(false);
		selectedNodeTextField.addActionListener(e -> {
			try {
				if (!mCanvas.setSelectedPositionId(Integer.parseInt(selectedNodeTextField.getText()))) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException ex){
				System.err.println("could not save with specified id");
				selectedNodeTextField.setText(null);
			}
		});

		idPanel.add(idTitle);
		idPanel.add(selectedNodeTextField);

		deleteSelectedNodeButton = new JButton();
		deleteSelectedNodeButton.setText("Delete");
		deleteSelectedNodeButton.setEnabled(false);
		deleteSelectedNodeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
//				mCanvas.deleteNode(mCanvas.getSelectedNode());
			}
		});

		JRadioButton addNodeButton = new JRadioButton();
		addNodeButton.setText("Add/Edit node");
		addNodeButton.setActionCommand(ADD_EDIT_NODE_TOOL);
		addNodeButton.setSelected(true);

		JRadioButton moveButton = new JRadioButton();
		moveButton.setText("Move");
		moveButton.setActionCommand(MOVE_ELEMENT_TOOL);

		JRadioButton connectNodeButton = new JRadioButton();
		connectNodeButton.setText("Connect nodes");
		connectNodeButton.setActionCommand(CONNECT_NODE_TOOL);

		JRadioButton pathEditTool = new JRadioButton();
		pathEditTool.setText("Edit path");
		pathEditTool.setActionCommand(EDIT_PATH_TOOL);

		ButtonGroup nodeActionGroup = new ButtonGroup();
		nodeActionGroup.add(addNodeButton);
		nodeActionGroup.add(connectNodeButton);
		nodeActionGroup.add(moveButton);
		nodeActionGroup.add(pathEditTool);

        JRadioButton previewButton = new JRadioButton();
        previewButton.setText("Preview");
        previewButton.setActionCommand(PREVIEW);

        JRadioButton editNodeButton = new JRadioButton();
        editNodeButton.setText("Edit Nodes");
        editNodeButton.setActionCommand(EDIT_NODES);
        editNodeButton.setSelected(true);

        JRadioButton editRouteButton = new JRadioButton();
        editRouteButton.setText("Edit Routes");
        editRouteButton.setActionCommand(EDIT_ROUTES);


        ButtonGroup viewGroup = new ButtonGroup();
        viewGroup.add(previewButton);
        viewGroup.add(editNodeButton);
        viewGroup.add(editRouteButton);

		ActionListener nodeToolListener = new ActionListener(){
			@Override
			public void actionPerformed(final ActionEvent e) {
				mCurrentTool = e.getActionCommand();

				updateImageCursor();
			}
		};

        ActionListener viewToolListener = new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                mCurrentView = e.getActionCommand();
                if(PREVIEW.equals(mCurrentView)){
                    mCanvas.setViewMode(MapCanvas.ViewType.PREVIEW);
                }else if(EDIT_NODES.equals(mCurrentView)){
                    mCanvas.setViewMode(MapCanvas.ViewType.NODES);
                }else if(EDIT_ROUTES.equals(mCurrentView)){
                    mCanvas.setViewMode(MapCanvas.ViewType.ROUTES);
                }
            }
        };

		addNodeButton.addActionListener(nodeToolListener);
		connectNodeButton.addActionListener(nodeToolListener);
		moveButton.addActionListener(nodeToolListener);
		pathEditTool.addActionListener(nodeToolListener);


        previewButton.addActionListener(viewToolListener);
        editNodeButton.addActionListener(viewToolListener);
        editRouteButton.addActionListener(viewToolListener);

        toolbarPanel.add(previewButton);
        toolbarPanel.add(editNodeButton);
        toolbarPanel.add(editRouteButton);

		toolbarPanel.add(idPanel);
		toolbarPanel.add(deleteSelectedNodeButton);

		toolbarPanel.add(addNodeButton);
		toolbarPanel.add(connectNodeButton);
		toolbarPanel.add(moveButton);
		toolbarPanel.add(pathEditTool);

		return toolbarPanel;
	}

    private void createMenu() {

		fileChooser = new JFileChooser();
		File workingDirectory = new File(System.getProperty("user.dir"));
		fileChooser.setCurrentDirectory(workingDirectory);

		//Where the GUI is created:
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;

		menuBar = new JMenuBar();

		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		menuItem = new JMenuItem("Load Map Data",
				KeyEvent.VK_L);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Load existing map data");
		menuItem.setActionCommand(MENU_LOAD);
		menuItem.addActionListener(mMenuListener);
		menu.add(menuItem);

        menuItem = new JMenuItem("Load Graph Data",
                KeyEvent.VK_L);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Load graph route data");
        menuItem.setActionCommand(MENU_LOAD_GRAPH);
        menuItem.addActionListener(mMenuListener);
        menu.add(menuItem);

		menuItem = new JMenuItem("Save Raw Map Data",
				KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Save raw map data");
		menuItem.setActionCommand(MENU_SAVE_RAW);
		menuItem.addActionListener(mMenuListener);
		menu.add(menuItem);

		setJMenuBar(menuBar);
	}

    private void updateImageCursor() {
		if(ADD_EDIT_NODE_TOOL.equals(mCurrentTool)){
			mImagePanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            mCanvas.setTool(MapCanvas.ToolType.ADD);
		}else if(EDIT_PATH_TOOL.equals(mCurrentTool)){
            mImagePanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            mCanvas.setTool(MapCanvas.ToolType.EDIT);
        }else if(MOVE_ELEMENT_TOOL.equals(mCurrentTool)){
			mImagePanel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            mCanvas.setTool(MapCanvas.ToolType.MOVE);
		}else if(CONNECT_NODE_TOOL.equals(mCurrentTool)){
			mImagePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            mCanvas.setTool(MapCanvas.ToolType.CONNECT);
		}

	}

    @Override
    public void onPositionSelected(DataPosition pos) {
        if(pos != null){
            selectedNodeTextField.setText(pos.id > 0 ? String.valueOf(pos.id) : "");
            selectedNodeTextField.grabFocus();
        }else{
            selectedNodeTextField.setText("");
        }

        selectedNodeTextField.setEnabled(pos != null);
        deleteSelectedNodeButton.setEnabled(pos != null);
    }
}
