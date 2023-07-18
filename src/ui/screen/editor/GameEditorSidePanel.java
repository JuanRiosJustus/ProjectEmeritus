package ui.screen.editor;

import constants.ColorPalette;
import constants.Constants;
import game.stores.pools.AssetPool;
import graphics.Spritemap;
import graphics.Spritesheet;
import graphics.temporary.JImageLabel;
import ui.presets.SceneManager;
import utils.ComponentUtils;
import utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameEditorSidePanel extends JPanel {

    private JComboBox<String> mapSizeComboBox = new JComboBox<>();
    private JComboBox<String> brushSizeComboBox = new JComboBox<>();
    private JComboBox<String> brushModeComboBox = new JComboBox<>();
    private JComboBox<Integer> layerNumberComboBox = new JComboBox<>();
    private JPanel m_selectedWrapper = new JPanel();
    private GridBagConstraints constraints;

    private boolean preventListener = false;
    private JScrollPane m_scrollPane;
    private final JPanel containerPane = new JPanel();
//    private final JToggleButton m_fillButton;
//    private final JToggleButton m_clearButton;
    private GameEditorSidePanelItem selected = null;
    private final int rowHeight = 35;

    private JTextField nameField = new JTextField();
    private JTextField mapSizeWidthField = new JTextField();
    private JTextField mapSizeHeightField = new JTextField();
    private JToggleButton isPathButton = new JToggleButton("No");
    private JTextField mapHeightField = new JTextField();
    private JComboBox<String> terrainComboBox = new JComboBox<String>();
    private JComboBox<String> liquidComboBox = new JComboBox<String>();
    private JComboBox<String> structureComboBox = new JComboBox<String>();
    private Map<String, Integer> terrainToInteger = new HashMap<>();
// getLiquidLayer

    private JPanel getStructureLayer(int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton button = new JButton("Structure: ");
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension((int) (width * .35), (int)button.getPreferredSize().getHeight()));
        panel.add(button);

        structureComboBox.setPreferredSize(new Dimension((int)(width * .6), (int)structureComboBox.getPreferredSize().getHeight()));
        Spritemap terrains = AssetPool.instance().getSpriteMap(Constants.STRUCTURES_SPRITESHEET_FILEPATH);
        Set<String> spritesheets = terrains.getSheetNameKeys();
        for (String name : spritesheets) {
            Spritesheet sheet = terrains.getSpritesheetByName(name);
            structureComboBox.addItem(name);
        }        
        JImageLabel jimage = new JImageLabel(100, 100);
        jimage.setOpaque(true);
        jimage.setBackground(ColorPalette.TRANSPARENT);
        structureComboBox.addActionListener(e ->{
            String name = (String) structureComboBox.getSelectedItem();
            Spritesheet sheet = terrains.getSpritesheetByName(name);
            var r = sheet.getSprite(0, 0);
            jimage.image.setIcon(new ImageIcon(r));
            jimage.setPreferredSize(new Dimension(r.getWidth(), r.getHeight()));
        });
        panel.add(structureComboBox);
        
        panel.add(jimage);
        panel.setBackground(ColorPalette.PURPLE);
        return panel;
    }

    private JPanel getLiquidLayer(int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton button = new JButton("Liquid: ");
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension((int) (width * .35), (int)button.getPreferredSize().getHeight()));
        panel.add(button);

        liquidComboBox.setPreferredSize(new Dimension((int)(width * .6), (int)liquidComboBox.getPreferredSize().getHeight()));
        Spritemap terrains = AssetPool.instance().getSpriteMap(Constants.LIQUIDS_SPRITESHEET_FILEPATH);
        Set<String> spritesheets = terrains.getSheetNameKeys();
        for (String name : spritesheets) {
            Spritesheet sheet = terrains.getSpritesheetByName(name);
            liquidComboBox.addItem(name);
        }        
        JImageLabel jimage = new JImageLabel(100, 100);
        jimage.setOpaque(true);
        jimage.setBackground(ColorPalette.TRANSPARENT);
        liquidComboBox.addActionListener(e ->{
            String name = (String) liquidComboBox.getSelectedItem();
            Spritesheet sheet = terrains.getSpritesheetByName(name);
            var r = sheet.getSprite(0, 0);
            jimage.image.setIcon(new ImageIcon(r));
            jimage.setPreferredSize(new Dimension(r.getWidth(), r.getHeight()));
        });
        panel.add(liquidComboBox);
        
        panel.add(jimage);
        panel.setBackground(ColorPalette.PURPLE);
        return panel;
    }

    private JPanel getTerrainLayer(int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton button = new JButton("Terrain: ");
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension((int) (width * .35), (int)button.getPreferredSize().getHeight()));
        panel.add(button);

        terrainComboBox.setPreferredSize(new Dimension((int)(width * .6), (int)terrainComboBox.getPreferredSize().getHeight()));
        Spritemap terrains = AssetPool.instance().getSpriteMap(Constants.FLOORS_SPRITESHEET_FILEPATH);
        Set<String> spritesheets = terrains.getSheetNameKeys();
        for (String name : spritesheets) {
            Spritesheet sheet = terrains.getSpritesheetByName(name);
            terrainComboBox.addItem(name);
        }        
        JImageLabel jimage = new JImageLabel(100, 100);
        jimage.setOpaque(true);
        jimage.setBackground(ColorPalette.TRANSPARENT);
        terrainComboBox.addActionListener(e ->{
            String name = (String) terrainComboBox.getSelectedItem();
            Spritesheet sheet = terrains.getSpritesheetByName(name);
            var r = sheet.getSprite(0, 0);
            jimage.image.setIcon(new ImageIcon(r));
            jimage.setPreferredSize(new Dimension(r.getWidth(), r.getHeight()));
        });
        panel.add(terrainComboBox);
        
        panel.add(jimage);
        panel.setBackground(ColorPalette.PURPLE);
        return panel;
    }

    private JPanel getHeightLayer(int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton button = new JButton("Height: ");
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension((int) (width * .35), (int)button.getPreferredSize().getHeight()));
        panel.add(button);

        mapHeightField.setPreferredSize(new Dimension((int)(width * .6), (int)mapHeightField.getPreferredSize().getHeight()));
        panel.add(mapHeightField);
        panel.setBackground(ColorPalette.GREEN);
        return panel;
    }

    private JPanel getPathingLayer(int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton button = new JButton("Path: ");
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension((int) (width * .45), (int)button.getPreferredSize().getHeight()));
        panel.add(button);

        isPathButton.setPreferredSize(new Dimension((int)(width * .5), (int)isPathButton.getPreferredSize().getHeight()));
        isPathButton.addActionListener(e -> {
            AbstractButton abstractButton = (AbstractButton) e.getSource();
            boolean selected = abstractButton.getModel().isSelected();
            isPathButton.setText(selected ? "Yes" : "No");
        });
        panel.add(isPathButton);
        panel.setBackground(ColorPalette.BEIGE);
        return panel;
    }


    private JPanel getMapNamePanel(int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton button = new JButton("Name:");
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension((int) (width * .25), (int)button.getPreferredSize().getHeight()));
        panel.add(button);

        nameField.setPreferredSize(new Dimension((int)(width * .7), (int)nameField.getPreferredSize().getHeight()));
        panel.add(nameField);
        panel.setBackground(ColorPalette.BLUE);
        return panel;
    }

    private JPanel getMapSizePanel(int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
                
        JButton button = new JButton("Width:");
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension((int) (width * .25), (int)button.getPreferredSize().getHeight()));
        panel.add(button);

        mapSizeWidthField.setPreferredSize(new Dimension((int)(width * .2), (int)mapSizeWidthField.getPreferredSize().getHeight()));
        mapSizeWidthField.setColumns(3);
        mapSizeWidthField.setOpaque(true);
        panel.add(mapSizeWidthField);

        button = new JButton("Height:");
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension((int) (width * .25), (int)button.getPreferredSize().getHeight()));
        panel.add(button);

        mapSizeHeightField.setPreferredSize(new Dimension((int)(width * .2), (int)mapSizeHeightField.getPreferredSize().getHeight()));
        mapSizeHeightField.setColumns(3);
        mapSizeHeightField.setOpaque(true);
        panel.add(mapSizeHeightField);

        return panel;
    }

    public JPanel mapSizePanel(int width, int height) {
        // init dropdown texts
        mapSizeComboBox = new JComboBox<>();
        // for (Size size : Size.values()) {
        //     mapSizeComboBox.addItem(size.name());
        // }

        // add label to dropdown
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(width, height));
        JTextField tfw = new JTextField(3);
        p.add(tfw);
        JTextField tfh = new JTextField(3);
        p.add(tfh);
        return p;
    }

    public JComboBox<String> placementTypeCombobox = null;

    public JPanel placementTypeDropdown() {
        // available sizes for the brush
        placementTypeCombobox = new JComboBox<>();
        placementTypeCombobox.addItem("Terrain");
        placementTypeCombobox.addItem("Structure");

        JPanel p = new JPanel();
        p.add(new JLabel("Placement Type:"));
        p.add(placementTypeCombobox);
        return p;
    }

    public JPanel brushSizeDropdown() {
        // available sizes for the brush
        brushSizeComboBox = new JComboBox<>();
        for (Size size : Size.values()) {
            brushSizeComboBox.addItem(size.name());
        }
        brushSizeComboBox.setSelectedIndex(2);

        JPanel p = new JPanel();
        p.add(new JLabel("Brush size:"));
        p.add(brushSizeComboBox);
        return p;
    }

    public JPanel brushModeDropdown() {
        // available sizes for the brush
        brushModeComboBox = new JComboBox<>();

        brushModeComboBox.addItem("Standard");
        brushModeComboBox.addItem("Fill");
        brushModeComboBox.addItem("Erase");
        brushModeComboBox.addItem("None");
        brushModeComboBox.setSelectedIndex(0);

        JPanel p = new JPanel();
        p.add(new JLabel("Brush Mode:"));
        p.add(brushModeComboBox);
        return p;
    }

//    public JPanel initSelectedPanel() {
//        JPanel p = new JPanel();
//
//        layerNumberComboBox = new JComboBox<>();
//        for (int i = 0; i < 10; i++) {
//            layerNumberComboBox.addItem(i);
//        }
//        p.add(m_selectedWrapper);
//        p.add(layerNumberComboBox);
////        p.setBackground(Color.LIGHT_GRAY);
//
//        // this is giving us old value, not the new one we just set it to
//        layerNumberComboBox.addActionListener(e -> {
//            if (selected == null) { return; }
//            if (layerNumberComboBox.getSelectedItem() == null) { return; }
//            if (preventListener) { preventListener = false; return; }
//            String val = layerNumberComboBox.getSelectedItem().toString();
//            selected.label.setText(val);
//        });
//        return p;
//    }

    public GameEditorSidePanel(int width, int height) {
        constraints = new GridBagConstraints();
        setPreferredSize(new Dimension(width, height));

        containerPane.setLayout(new BoxLayout(containerPane, BoxLayout.Y_AXIS));

        ComponentUtils.setSize(containerPane, width, height);

        m_scrollPane = new JScrollPane(containerPane,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        setLayout(new BorderLayout());
        add(m_scrollPane);


        containerPane.add(getMapNamePanel(width, height));
        containerPane.add(getMapSizePanel(width, height));
        containerPane.add(brushModeDropdown());
        containerPane.add(brushSizeDropdown());
        containerPane.add(getPathingLayer(width, height));
        containerPane.add(getHeightLayer(width, height));
        containerPane.add(getTerrainLayer(width, height));
        containerPane.add(getLiquidLayer(width, height));
        containerPane.add(getStructureLayer(width, height));

        containerPane.add(brushModeComboBox);

        JPanel p = mapSizePanel(width, height);
        p.setBackground(Color.RED);
        ComponentUtils.setSize(p, getWidth(), rowHeight);
        // containerPane.add(p);

        p = brushSizeDropdown();
        p.setBackground(Color.GREEN);
        ComponentUtils.setSize(p, getWidth(), rowHeight);
        // containerPane.add(p);


        p = placementTypeDropdown();
        p.setBackground(ColorPalette.BLUE);
        ComponentUtils.setSize(p, getWidth(), rowHeight);
        // containerPane.add(p);


//        containerPane.add(createTileViews(getWidth(), AssetPool.instance().getSpriteSheet(Constants.TERRAIN_SPRITESHEET_FILEPATH)));


//        p = brushModeDropdown();
//        p.setBackground(Color.BLUE);
//        ComponentUtils.setSize(p, getWidth(), rowHeight);
//        containerPane.add(p);






//        m_fillButton = new JToggleButton("Fill");
//        m_clearButton = new JToggleButton("Erase");

//        JPanel panel = new JPanel(new FlowLayout());
//        panel.add(m_fillButton);
//        panel.add(m_clearButton);

//        containerPane.add(panel);

        JButton filler = new JButton("Open File");
        filler.addActionListener(e -> {
            JFileChooser jf = new JFileChooser(new File("."));
            int returnVal = jf.showOpenDialog(this);
//            if () Get File and parse into spreadsheet
            if (returnVal == JFileChooser.APPROVE_OPTION) {
//                createTileViews();
            }
        });
//        containerPane.add(createTileViews(getWidth(), AssetPool.instance().getAllSpriteSheetImages(Constants.TERRAIN_SPRITESHEET_FILEPATH)));
//        containerPane.add(createTileView(getWidth(), AssetPool.instance().getSpriteSheetImages(Constants.TERRAIN_SPRITESHEET_FILEPATH)));
        
        // TODO needs to be fixed, was changed when making all spritesheet references from maps
        //containerPane.add(createTileView(getWidth(), AssetPool.instance().getSpriteSheetImages(Constants.STRUCTURE_SPRITESHEET_FILEPATH)));
        
        containerPane.add(filler);

        JButton returnButton = new JButton(Constants.MAIN_MENU);
        returnButton.addActionListener(e -> SceneManager.instance().set(Constants.MAIN_MENU_SCENE));

        containerPane.add(returnButton);


    }

    private BufferedImage getAllImagesFrom() { return null; }

    private JPanel createTileView(int rowWidth, BufferedImage[] toShow) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        // Create the row for the selected item
        JPanel selectedItemRow = new JPanel();
        JLabel selectedLabel = new JLabel("Selected: ");
        GameEditorSidePanelItem selectedItem = new GameEditorSidePanelItem();
//        System.out.println(toShow.length + " images");
        selectedItemRow.add(selectedLabel);
        selectedItemRow.add(selectedItem);
        ComponentUtils.setSize(selectedItemRow, rowWidth, rowHeight);
        ComponentUtils.setTransparent(selectedItemRow);
        container.add(selectedItemRow);

        JPanel row = new JPanel();
        // go through all tile types
        for (int i = 0; i < toShow.length; i++) {

            // Only show a certain amount of tiles per row
            if (row.getComponents().length * toShow[0].getWidth() > rowWidth) {
                ComponentUtils.setSize(row, rowWidth, rowHeight);
                ComponentUtils.setTransparent(row);
                container.add(row);

                row = new JPanel();
            }

            BufferedImage raw = toShow[i];
            int newWidth = raw.getWidth() / 2;
            int newHeight = raw.getHeight() / 2;
            BufferedImage smallImg = ImageUtils.getResizedImage(raw, newWidth, newHeight);
            GameEditorSidePanelItem imagePanel = new GameEditorSidePanelItem(smallImg, i);
            imagePanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    selectedItem.link = imagePanel;
                    selectedItem.setIcon(smallImg);
                }
            });
            row.add(imagePanel);
        }
        ComponentUtils.setSize(row, rowWidth, rowHeight);
        ComponentUtils.setTransparent(row);
        container.add(row);
//        selected = list.get(0); // default selection

        container.setBackground(ColorPalette.BLUE);
        container.revalidate();
        container.repaint();
        return container;
    }

//    private JPanel createTileViews(int width) {
//        JPanel container = new JPanel();
//        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
//
//        // Create the row for the selected item
//        JPanel selectedItemRow = new JPanel();
//        JLabel selectedLabel = new JLabel("Terrain:");
//        GameEditorSidePanelItem selectedItem = new GameEditorSidePanelItem();
//        selectedItemRow.add(selectedLabel);
//        selectedItemRow.add(selectedItem);
//        ComponentUtils.setSize(selectedItemRow, width, rowHeight);
//        ComponentUtils.setTransparent(selectedItemRow);
//        container.add(selectedItemRow);
//
//        JPanel row = new JPanel();
//        // go through all tile types
//        for (int i = 0; i < AssetPool.instance().tileSprites(); i++) {
//            BufferedImage img = AssetPool.instance().getImage(Constants.TERRAIN_SPRITESHEET_FILEPATH, i);
//            BufferedImage smallImg = ImageUtils.getResizedImage(img, img.getWidth() / 2, img.getHeight() / 2);
//            GameEditorSidePanelItem p = new GameEditorSidePanelItem(smallImg, i);
//            p.addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseClicked(MouseEvent e) {
//                    super.mouseClicked(e);
//                    selectedItem.setIcon(smallImg);
//                }
//            });
//            row.add(p);
////            setActionListeners(p, list);
//
//            // Only show a certain amount of tiles per row
//            if (row.getComponents().length > 4) {
//                ComponentUtils.setSize(row, width, rowHeight);
//                ComponentUtils.setTransparent(row);
//                container.add(row);
//
//                row = new JPanel();
//            }
////            containerPane.add(row);
////            m_holderPane.add(p, m_constraints);
//        }
////        selected = list.get(0); // default selection
//        revalidate();
//        repaint();
//
//        container.setBackground(ColorPalette.PURPLE);
//        container.add(new JButton("TESTING"));
//
//        container.revalidate();
//        container.repaint();
//        return container;
//    }

//    private void createTileViews() {
////        AssetMap map = AssetPool.get().getSheet();
//        ArrayList<GameEditorSidePanelItem> list = new ArrayList<>();
//        JPanel container = new JPanel();
//        JPanel row = new JPanel();
//        // go through all tile types
//        for (int i = 0; i < AssetPool.instance().tileSprites(); i++) {
//            BufferedImage img = AssetPool.instance().getTileImage(i);
//            BufferedImage smallImg = ImageUtils.getResizedImage(img, img.getWidth() / 2, img.getHeight() / 2);
//            GameEditorSidePanelItem p = new GameEditorSidePanelItem(smallImg, i);
////            setActionListeners(p, list);
//            list.add(p);
//
//            if (row.getComponents().length < 4) {
//                row.add(p);
//            } else {
//                row = new JPanel();
//            }
//            containerPane.add(row);
////            m_holderPane.add(p, m_constraints);
//        }
////        selected = list.get(0); // default selection
//        revalidate();
//        repaint();
//    }

    private void setActionListeners(GameEditorSidePanelItem p, ArrayList<GameEditorSidePanelItem> panels) {
        Color originalColor = p.getBackground();
        p.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // the panel item has a number in its label
                if (p.label.getText().isEmpty()) {
                    // if the label is empty, there was no layer set
                    preventListener = true;
                    layerNumberComboBox.setSelectedIndex(0);
                } else {
                    // find the value equal to the number label
                    for (int i = 0; i < layerNumberComboBox.getItemCount(); i++) {
                        int val = layerNumberComboBox.getItemAt(i);
                        int itemVal = Integer.parseInt(p.label.getText());
                        if (itemVal == val) {
                            preventListener = true;
                            layerNumberComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                }

                m_selectedWrapper.removeAll();
                m_selectedWrapper.add(new JLabel(new ImageIcon(p.panelImage)));
                m_selectedWrapper.revalidate();
                m_selectedWrapper.repaint();
                selected = p;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                p.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                p.setBackground(originalColor);
            }
        });
//        p.label.addActionListener(e -> {
//            p.setBackground(ColorPalette.WHITE);
//            for (GameEditorPanelItem panelItem: panels) {
//                if (panelItem == p) { continue; }
//                panelItem.setBackground(getBackground());
//            }
//            selected = p;
//            m_fillButton.setSelected(false);
//            m_clearButton.setSelected(false);
//        });
//        m_clearButton.addActionListener(e ->{
//            for (GameEditorSidePanelItem panelItem: panels) {
//                panelItem.setBackground(getBackground());
//            }
//            m_fillButton.setSelected(false);
//        });
    }


//    public boolean shouldFill() { return m_fillButton.isSelected(); }
    public GameEditorSidePanelItem getSelectedPanelItem() { return selected; }
    public Size getSelectedBrushSize() {
        Object selected = brushSizeComboBox.getSelectedItem();
        return (selected != null ? Size.valueOf(selected.toString()) : null);
    }
    public Size getSelectedMapSize() {
        Object selected = mapSizeComboBox.getSelectedItem();
        return (selected != null ? Size.valueOf(selected.toString()) : null);
    }
    public Integer getSelectedLayer() {
        Object selected = layerNumberComboBox.getSelectedItem();
        return (selected != null ? Integer.parseInt(selected.toString()) : null);
    }
    public String getBrushMode() {
        Object selected = brushModeComboBox.getSelectedItem();
        return (selected != null ? (String) selected : "Standard");
    }
//    public boolean isClearing() { return m_clearButton.isSelected(); }
}
