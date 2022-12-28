package ui.screen.editor;

import constants.Constants;
import game.stores.pools.AssetPool;
import utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class GameEditorPanel extends JPanel {

    private JComboBox<String> mapSizeComboBox = new JComboBox<>();
    private JComboBox<String> brushSizeComboBox = new JComboBox<>();
    private JComboBox<Integer> layerNumberComboBox = new JComboBox<>();
    private JPanel m_selectedWrapper = new JPanel();
    private GridBagConstraints constraints;

    private boolean preventListener = false;
    private JScrollPane m_scrollPane;
    private final JPanel m_holderPane = new JPanel();
    private final JToggleButton m_fillButton;
    private final JToggleButton m_clearButton;
    private GameEditorPanelItem selected = null;

    public JPanel mapSizeDropdown() {
        // init dropdown texts
        mapSizeComboBox = new JComboBox<>();
        for (Size size : Size.values()) {
            mapSizeComboBox.addItem(size.name());
        }
//        brushSizeComboBox.setSelectedIndex(2);

        // add label to dropdown
        JPanel p = new JPanel();
        p.add(new JLabel("Map size:"));
        p.add(mapSizeComboBox);
        return p;
    }

    public JPanel initBrushSizeDropdownPanel() {

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

    public JPanel initSelectedPanel() {
        JPanel p = new JPanel();

        layerNumberComboBox = new JComboBox<>();
        for (int i = 0; i < 10; i++) {
            layerNumberComboBox.addItem(i);
        }
        p.add(m_selectedWrapper);
        p.add(layerNumberComboBox);
//        p.setBackground(Color.LIGHT_GRAY);

        // this is giving us old value, not the new one we just set it to
        layerNumberComboBox.addActionListener(e -> {
            if (selected == null) { return; }
            if (layerNumberComboBox.getSelectedItem() == null) { return; }
            if (preventListener) { preventListener = false; return; }
            String val = layerNumberComboBox.getSelectedItem().toString();
            selected.label.setText(val);
        });
        return p;
    }

    public GameEditorPanel() {
//        super(width, height, "Game Editor Panel");
        constraints = new GridBagConstraints();

        constraints.insets = new Insets(1, 1, 1, 1);
        m_holderPane.setLayout(new GridBagLayout());
//        m_holderPane.setSize(100, Constants.APPLICATION_HEIGHT);
//        m_holderPane.setPreferredSize(new Dimension(100, Constants.APPLICATION_HEIGHT));


        m_scrollPane = new JScrollPane(m_holderPane,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        setLayout(new BorderLayout());
        add(m_scrollPane);

        JPanel p = mapSizeDropdown();
        m_holderPane.add(p, constraints);

        p = initBrushSizeDropdownPanel();
        m_holderPane.add(p, constraints);

        p = initSelectedPanel();
        m_holderPane.add(p, constraints);

        m_fillButton = new JToggleButton("Fill");
        m_clearButton = new JToggleButton("Erase");

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(m_fillButton);
        panel.add(m_clearButton);



        m_holderPane.add(panel, constraints);

        JButton filler = new JButton("Open File");
        filler.addActionListener(e -> {
            JFileChooser jf = new JFileChooser(new File("."));
            int returnVal = jf.showOpenDialog(this);
//            if () Get File and parse into spreadsheet
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                //createTileViews();
            }
        });
        createTileViews();
        m_holderPane.add(filler, constraints);

//        m_holderPane.add(escapeButton, constraints);
    }
    private void createTileViews() {
//        AssetMap map = AssetPool.get().getSheet();
        ArrayList<GameEditorPanelItem> list = new ArrayList<>();
        JPanel row = new JPanel();
        // go through all tile types
        for (int i = 0; i < AssetPool.instance().tileSprites(); i++) {
            BufferedImage img = AssetPool.instance().getTileImage(i);
            BufferedImage smallImg = ImageUtils.getResizedImage(img, img.getWidth() / 2, img.getHeight() / 2);
            GameEditorPanelItem p = new GameEditorPanelItem(smallImg, i);
            setActionListeners(p, list);
            list.add(p);

            if (row.getComponents().length < 4) {
                row.add(p);
            } else {
                row = new JPanel();
            }
            m_holderPane.add(row, constraints);
//            m_holderPane.add(p, m_constraints);
        }
        selected = list.get(0); // default selection
        revalidate();
        repaint();
    }

    private void setActionListeners(GameEditorPanelItem p, ArrayList<GameEditorPanelItem> panels) {
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
        m_clearButton.addActionListener(e ->{
            for (GameEditorPanelItem panelItem: panels) {
                panelItem.setBackground(getBackground());
            }
            m_fillButton.setSelected(false);
        });
    }


    public boolean shouldFill() { return m_fillButton.isSelected(); }
    public GameEditorPanelItem getSelectedPanelItem() { return selected; }
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
    public boolean isClearing() { return m_clearButton.isSelected(); }
}
