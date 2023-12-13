package main.ui.panels;


//import java.awt.*;
//import java.awt.event.*;
//import java.awt.font.*;
//import java.awt.image.BufferedImage;
//import javax.swing.*;
//
//public class ExpandingPanels extends MouseAdapter
//{
//    ActionPanel[] aps;
//    JPanel[] panels;
//
//    public ExpandingPanels()
//    {
//        assembleActionPanels();
//        assemblePanels();
//    }
//
//    public void mousePressed(MouseEvent e)
//    {
//        ActionPanel ap = (ActionPanel)e.getSource();
//        if(ap.target.contains(e.getPoint()))
//        {
//            ap.toggleSelection();
//            togglePanelVisibility(ap);
//        }
//    }
//
//    private void togglePanelVisibility(ActionPanel ap)
//    {
//        int index = getPanelIndex(ap);
//        if(panels[index].isShowing())
//            panels[index].setVisible(false);
//        else
//            panels[index].setVisible(true);
//        ap.getParent().validate();
//    }
//
//    private int getPanelIndex(ActionPanel ap)
//    {
//        for(int j = 0; j < aps.length; j++)
//            if(ap == aps[j])
//                return j;
//        return -1;
//    }
//
//    private void assembleActionPanels()
//    {
//        String[] ids = { "level 1", "level 2", "level 3", "level 4" };
//        aps = new ActionPanel[ids.length];
//        for(int j = 0; j < aps.length; j++)
//            aps[j] = new ActionPanel(ids[j], this);
//    }
//
//    private void assemblePanels()
//    {
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(2,1,2,1);
//        gbc.weightx = 1.0;
//        gbc.weighty = 1.0;
//
//        JPanel p1 = new JPanel(new GridBagLayout());
//        gbc.gridwidth = gbc.RELATIVE;
//        p1.add(new JButton("button 1"), gbc);
//        gbc.gridwidth = gbc.REMAINDER;
//        p1.add(new JButton("button 2"), gbc);
//        gbc.gridwidth = gbc.RELATIVE;
//        p1.add(new JButton("button 3"), gbc);
//        gbc.gridwidth = gbc.REMAINDER;
//        p1.add(new JButton("button 4"), gbc);
//
//        JPanel p2 = new JPanel(new GridBagLayout());
//        gbc.gridwidth = 1;
//        gbc.anchor = gbc.EAST;
//        p2.add(new JLabel("enter"), gbc);
//        gbc.anchor = gbc.WEST;
//        p2.add(new JTextField(8), gbc);
//        gbc.anchor = gbc.CENTER;
//        p2.add(new JButton("button 1"), gbc);
//        gbc.gridwidth = gbc.REMAINDER;
//        p2.add(new JButton("button 2"), gbc);
//
//        JPanel p3 = new JPanel(new BorderLayout());
//        JTextArea textArea = new JTextArea(8,12);
//        textArea.setLineWrap(true);
//        p3.add(new JScrollPane(textArea));
//
//        JPanel p4 = new JPanel(new GridBagLayout());
//        addComponents(new JLabel("label 1"), new JTextField(12), p4, gbc);
//        addComponents(new JLabel("label 2"), new JTextField(16), p4, gbc);
//        gbc.gridwidth = 2;
//        gbc.gridy = 2;
//        p4.add(new JSlider(), gbc);
//        gbc.gridy++;
//        JPanel p5 = new JPanel(new GridBagLayout());
//        p5.add(new JButton("button 1"), gbc);
//        p5.add(new JButton("button 2"), gbc);
//        p5.add(new JButton("button 3"), gbc);
//        p5.add(new JButton("button 4"), gbc);
//        gbc.weighty = 1.0;
//        gbc.fill = gbc.BOTH;
//        p4.add(p5, gbc);
//        panels = new JPanel[] { p1, p2, p3, p4 };
//    }
//
//    private void addComponents(Component c1, Component c2, Container c,
//                               GridBagConstraints gbc)
//    {
//        gbc.anchor = gbc.EAST;
//        gbc.gridwidth = gbc.RELATIVE;
//        c.add(c1, gbc);
//        gbc.anchor = gbc.WEST;
//        gbc.gridwidth = gbc.REMAINDER;
//        c.add(c2, gbc);
//        gbc.anchor = gbc.CENTER;
//    }
//
//    private JPanel getComponent()
//    {
//        JPanel panel = new JPanel(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(1,3,0,3);
//        gbc.weightx = 1.0;
//        gbc.fill = gbc.HORIZONTAL;
//        gbc.gridwidth = gbc.REMAINDER;
//        for(int j = 0; j < aps.length; j++)
//        {
//            panel.add(aps[j], gbc);
//            panel.add(panels[j], gbc);
//            panels[j].setVisible(false);
//        }
//        JLabel padding = new JLabel();
//        gbc.weighty = 1.0;
//        panel.add(padding, gbc);
//        return panel;
//    }
//
//    public static void main(String[] args)
//    {
//        ExpandingPanels test = new ExpandingPanels();
//        JFrame f = new JFrame();
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f.getContentPane().add(new JScrollPane(test.getComponent()));
//        f.setSize(360,500);
//        f.setLocation(200,100);
//        f.setVisible(true);
//    }
//}
//
//class ActionPanel extends JPanel
//{
//    String text;
//    Font font;
//    private boolean selected;
//    BufferedImage open, closed;
//    Rectangle target;
//    final int
//            OFFSET = 30,
//            PAD    =  5;
//
//    public ActionPanel(String text, MouseListener ml)
//    {
//        this.text = text;
//        addMouseListener(ml);
//        font = new Font("sans-serif", Font.PLAIN, 12);
//        selected = false;
//        setBackground(new Color(200,200,220));
//        setPreferredSize(new Dimension(200,20));
//        setBorder(BorderFactory.createRaisedBevelBorder());
//        setPreferredSize(new Dimension(200,20));
//        createImages();
//        setRequestFocusEnabled(true);
//    }
//
//    public void toggleSelection()
//    {
//        selected = !selected;
//        repaint();
//    }
//
//    protected void paintComponent(Graphics g)
//    {
//        super.paintComponent(g);
//        Graphics2D g2 = (Graphics2D)g;
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
//        int w = getWidth();
//        int h = getHeight();
//        if(selected)
//            g2.drawImage(open, PAD, 0, this);
//        else
//            g2.drawImage(closed, PAD, 0, this);
//        g2.setFont(font);
//        FontRenderContext frc = g2.getFontRenderContext();
//        LineMetrics lm = font.getLineMetrics(text, frc);
//        float height = lm.getAscent() + lm.getDescent();
//        float x = OFFSET;
//        float y = (h + height)/2 - lm.getDescent();
//        g2.drawString(text, x, y);
//    }
//
//    private void createImages()
//    {
//        int w = 20;
//        int h = getPreferredSize().height;
//        target = new Rectangle(2, 0, 20, 18);
//        open = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//        Graphics2D g2 = open.createGraphics();
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
//        g2.setPaint(getBackground());
//        g2.fillRect(0,0,w,h);
//        int[] x = { 2, w/2, 18 };
//        int[] y = { 4, 15,   4 };
//        Polygon p = new Polygon(x, y, 3);
//        g2.setPaint(Color.green.brighter());
//        g2.fill(p);
//        g2.setPaint(Color.blue.brighter());
//        g2.draw(p);
//        g2.dispose();
//        closed = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//        g2 = closed.createGraphics();
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
//        g2.setPaint(getBackground());
//        g2.fillRect(0,0,w,h);
//        x = new int[] { 3, 13,   3 };
//        y = new int[] { 4, h/2, 16 };
//        p = new Polygon(x, y, 3);
//        g2.setPaint(Color.red);
//        g2.fill(p);
//        g2.setPaint(Color.blue.brighter());
//        g2.draw(p);
//        g2.dispose();
//    }
//}



import main.constants.ColorPalette;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;


public class ExpandingPanels extends JScrollPane implements ActionListener {
    private final JPanel mContainer = new JPanel();
    private final Map<String, BarInfo> mPanels = new LinkedHashMap<>();
    private final GridBagConstraints mConstraints = new GridBagConstraints();
    private static class BarInfo {
        public String name;
        public final JButton button;
        public final JComponent component;
        public boolean shouldShow = false;

        public BarInfo( String mName, Icon mIcon, JComponent mComponent) {
            name = mName;
            component = mComponent;
            button = new JButton( mName, mIcon );
            button.addActionListener(e -> {
                if (button.getText().endsWith("(OPEN)")) {
                    button.setText(button.getText().substring(0, button.getText().lastIndexOf(" ")));
                } else {
                    button.setText(button.getText() + " (OPEN)");
                }
            });
        }
    }
    public ExpandingPanels() {
        mContainer.setLayout(new GridBagLayout());
//        mContainer.setBackground(ColorPalette.getRandomColor());
        setBorder(BorderFactory.createEmptyBorder());
        setViewportView(mContainer);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Map.Entry<String, BarInfo> entry : mPanels.entrySet()) {
            if (entry.getValue().button != e.getSource()) { continue; }
            entry.getValue().shouldShow = !entry.getValue().shouldShow;
            if (entry.getValue().shouldShow && entry.getValue().name.endsWith("<")) {
                entry.getValue().name += "V";
            }
        }
        render();
    }
    public void addPanel(String key, JComponent component) {
        BarInfo barInfo = new BarInfo(key, null, component);
        barInfo.button.addActionListener(this);
        mPanels.put(key, barInfo);
        render();
    }

    public void horizontalSquish() {

    }

    public static JPanel getDummyPanel( String name ) {
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( new JLabel( name, JLabel.CENTER ) );
        panel.setBackground(ColorPalette.getRandomColor());
        return panel;
    }
    public static JPanel getDummyPanel(String... labels) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        for (String label : labels) {
            panel.add( new JLabel( label, JLabel.CENTER ), gbc);
            gbc.gridy = panel.getComponentCount() + 1;
        }
        panel.setBackground(ColorPalette.getRandomColor());
        return panel;
    }

    private void render() {
        int row = 0;

        mContainer.removeAll();
        for (Map.Entry<String, BarInfo> entry : mPanels.entrySet()) {
            JButton button = entry.getValue().button;
            mConstraints.weighty = 0;
            mConstraints.weightx = 1;
            mConstraints.gridwidth = GridBagConstraints.REMAINDER;
            mConstraints.fill = GridBagConstraints.HORIZONTAL;
            mConstraints.gridy = row++;
            mConstraints.gridx = 0;
//            button.setBackground(ColorPalette.getRandomColor());
            mContainer.add(button, mConstraints);

            JComponent panel = entry.getValue().component;
            if (!entry.getValue().shouldShow) { continue; }
            mConstraints.weighty = 1;
            mConstraints.gridy = row++;
//            panel.setBackground(ColorPalette.getRandomColor());
            mContainer.add(panel, mConstraints);
        }
        revalidate();
    }

    public void openAll() {
        for (Map.Entry<String, BarInfo> entry : mPanels.entrySet()) {
            entry.getValue().shouldShow = true;
        }
        render();
    }

    public static void main(String[] args)
    {
        ExpandingPanels test = new ExpandingPanels();
        test.addPanel("one",  getDummyPanel( "1111111", "1111.1111", "1111.1111.111" ));
        test.addPanel("two",  getDummyPanel( "2222222" ));
        test.addPanel("three",  getDummyPanel( "33333333" ));

        JPanel tester = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JPanel panel = getDummyPanel("f54m4oi4ofmioifo4m");
        panel.setPreferredSize(new Dimension(400, 800));
        tester.add(panel, constraints);
//        tester.setPreferredSize(new Dimension(200, 400));
//        tester.setMaximumSize(tester.getPreferredSize());
//        tester.setMinimumSize(tester.getPreferredSize());
//        tester.setBackground(ColorPalette.getRandomColor());
        test.addPanel("four", tester);

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f.getContentPane().add(new JScrollPane(test.getComponent()));
        f.getContentPane().add(new JScrollPane(test));
        f.setSize(360,500);
        f.setLocation(200,100);
        f.setVisible(true);
    }
}


//
//import main.constants.ColorPalette;
//
//import java.awt.*;
//import java.awt.event.*;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import javax.swing.*;
//
//
//public class ExpandingPanels extends JPanel implements ActionListener {
//    private final Map<String, BarInfo> mPanels = new LinkedHashMap<>();
//    private final GridBagConstraints mConstraints = new GridBagConstraints();
//    private static class BarInfo {
//        public final String name;
//        public final JButton button;
//        public final JComponent component;
//        public boolean shouldShow = false;
//
//        public BarInfo( String mName, Icon mIcon, JComponent mComponent) {
//            name = mName;
//            component = mComponent;
//            button = new JButton( mName, mIcon );
//        }
//    }
//    public ExpandingPanels() {
//
//        setLayout(new GridBagLayout());
//
////        JScrollPane scrollPane = new JScrollPane(panel,
////                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
////                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
////        scrollPane.getViewport().setPreferredSize(new Dimension(width, height));
////        scrollPane.setPreferredSize(new Dimension(width, height));
////
////        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
////        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
////        scrollPane.setBorder(BorderFactory.createEmptyBorder());
////
////        return scrollPane;
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        for (Map.Entry<String, BarInfo> entry : mPanels.entrySet()) {
//            if (entry.getValue().button != e.getSource()) { continue; }
//            entry.getValue().shouldShow = !entry.getValue().shouldShow;
//            System.out.println(entry.getKey() + ": " + entry.getValue().shouldShow);
//        }
//        render();
//    }
//    public void addPanel(String key, JComponent component) {
//        BarInfo barInfo = new BarInfo(key, null, component);
//        barInfo.button.addActionListener(this);
//        mPanels.put(key, barInfo);
//        render();
//    }
//
//    public static JPanel getDummyPanel( String name ) {
//        JPanel panel = new JPanel( new BorderLayout() );
//        panel.add( new JLabel( name, JLabel.CENTER ) );
//        panel.setBackground(ColorPalette.getRandomColor());
//        return panel;
//    }
//    public static JPanel getDummyPanel(String... labels) {
//        JPanel panel = new JPanel(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        for (String label : labels) {
//            panel.add( new JLabel( label, JLabel.CENTER ), gbc);
//            gbc.gridy = panel.getComponentCount() + 1;
//        }
//        panel.setBackground(ColorPalette.getRandomColor());
//        return panel;
//    }
//
//    private void render() {
//        int row = 0;
//
//        //        JPanel panel = new JPanel(new GridBagLayout());
////        GridBagConstraints gbc = new GridBagConstraints();
////        gbc.insets = new Insets(1,3,0,3);
////        gbc.weightx = 1.0;
////        gbc.fill = gbc.HORIZONTAL;
////        gbc.gridwidth = gbc.REMAINDER;
////        for(int j = 0; j < aps.length; j++)
////        {
////            panel.add(aps[j], gbc);
////            panel.add(panels[j], gbc);
////            panels[j].setVisible(false);
////        }
////        JLabel padding = new JLabel();
////        gbc.weighty = 1.0;
////        panel.add(padding, gbc);
//
//
//        removeAll();
//        for (Map.Entry<String, BarInfo> entry : mPanels.entrySet()) {
//            JButton button = entry.getValue().button;
//            mConstraints.weighty = 0;
//            mConstraints.weightx = 1;
//            mConstraints.fill = GridBagConstraints.HORIZONTAL;
//            mConstraints.gridy = row++;
//            mConstraints.gridx = 0;
//            add(button, mConstraints);
//
//            JComponent panel = entry.getValue().component;
//            if (!entry.getValue().shouldShow) { continue; }
//            mConstraints.weighty = 1;
//            mConstraints.gridy = row++;
//            add(panel, mConstraints);
//        }
//        revalidate();
//    }
//
//    public void openAll() {
//        for (Map.Entry<String, BarInfo> entry : mPanels.entrySet()) {
//            entry.getValue().shouldShow = true;
//        }
//        render();
//    }
//
//    public static void main(String[] args)
//    {
//        ExpandingPanels test = new ExpandingPanels();
//        test.addPanel("one",  getDummyPanel( "1111111", "1111.1111", "1111.1111.111" ));
//        test.addPanel("two",  getDummyPanel( "2222222" ));
//        test.addPanel("three",  getDummyPanel( "33333333" ));
//
//        JPanel tester = new JPanel(new GridBagLayout());
//        tester.setPreferredSize(new Dimension(200, 600));
//        tester.setBackground(ColorPalette.getRandomColor());
//        test.addPanel("four", tester);
//
//        JFrame f = new JFrame();
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////        f.getContentPane().add(new JScrollPane(test.getComponent()));
//        f.getContentPane().add(new JScrollPane(test));
//        f.setSize(360,500);
//        f.setLocation(200,100);
//        f.setVisible(true);
//    }
//}