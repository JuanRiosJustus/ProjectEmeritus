package ui.panels;

import constants.ColorPalette;
import constants.Constants;
import graphics.JScene;

import javax.swing.*;
import java.awt.*;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;


public class LogPanel extends JScene {

    private int logCount;
    private int labels = 0;
    private int m_maxLogLineLength = 25;
    private int m_maxLogs = 8;

    private JTextArea logArea = new JTextArea();
    private Deque<String> lines = new ConcurrentLinkedDeque<>();
//    public final JPanel turnOrderView = new JPanel();
//    private List<JImageLabel> turnOrderList = new ArrayList<>();
//    private Map<Animation, ImageIcon> cache = new HashMap<>();

    public LogPanel() {
        super(Constants.SIDE_BAR_WIDTH, Constants.SIDE_BAR_LOGS_HEIGHT, "Logs");

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.VERTICAL;

        logArea.setColumns(Constants.SIDE_BAR_WIDTH / logArea.getFont().getSize());
        logArea.setLineWrap(true);
        logArea.setRows(Constants.SIDE_BAR_MAIN_PANEL_HEIGHT / 2 / logArea.getFont().getSize());
        logArea.setBackground(ColorPalette.TRANSPARENT);
        logArea.setEnabled(false);
        logArea.setDisabledTextColor(Color.BLACK);
        logArea.setFocusable(false);
        logArea.setEditable(false);
        logArea.setDragEnabled(false);

        add(logArea, gbc);
        add(getExitButton(), gbc);
    }

    public void log(String text) {

        lines.add(text);
        if (lines.size() > 15) {
            lines.removeFirst();
        }
        
        logArea.setText(null);
        for (String line : lines) {
            logArea.append(line + System.lineSeparator());
        }
    }
}
