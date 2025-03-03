package main.ui.presets.loadout;

import main.constants.Constants;
import main.engine.EngineScene;
import main.game.entity.Entity;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
// import main.ui.outline.OutlineLabel;
import main.ui.custom.SwingUiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SummaryCardsPanel extends EngineScene {
    private final List<Entity> mEntities = new ArrayList<>();
    private final List<SummaryCard> mSummaryCards = new ArrayList<>();
    private OutlineLabel mTitleLabel = new OutlineLabel(1);
    private JTextField mSearchField = new JTextField();
    private Entity mSelectedEntity = null;
    private final Map<Entity, SummaryCard> mEntityToSummaryCard = new LinkedHashMap<>();
//    private Color mColor = ColorPalette.getRandomColor();
    private int mSummaryCardWidth;
    private int mSummaryCardHeight;

    public void setup(List<Entity> entityList, int width, int height, Color color) {

        mColor = color;
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());

        int spriteSizes = Constants.CURRENT_SPRITE_SIZE;
        removeAll();

        JPanel newPanel = new JPanel();
        newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));

//        BufferedImage blank = new BufferedImage(spriteSizes, spriteSizes, BufferedImage.TYPE_INT_ARGB);
        mSummaryCardWidth = width;
        mSummaryCardHeight = (int) (height * .2);

        for (Entity entity : entityList) {
            SummaryCard summaryCard = new SummaryCard(mSummaryCardWidth, mSummaryCardHeight, entity, color);
//            summaryCard.setColors(color);
            summaryCard.getImage().getImageContainer().addActionListener(e2 -> mSelectedEntity = entity);
            summaryCard.setBorder(BorderFactory.createEtchedBorder(color, color.darker()));

            mSummaryCards.add(summaryCard);
            mEntityToSummaryCard.put(entity, summaryCard);
            newPanel.add(summaryCard);
        }

        // buffer for the bottom panel
        JPanel jPanel = new JPanel();
        jPanel.setPreferredSize(new Dimension(mSummaryCardWidth, mSummaryCardHeight));
        jPanel.setMinimumSize(jPanel.getPreferredSize());
        jPanel.setMaximumSize(jPanel.getPreferredSize());
        jPanel.setBackground(color);
        newPanel.add(jPanel);


        mEntities.addAll(entityList);

        int titleLabelHeight = (int) (height * .05);
        int titleLabelWidth = width;
        mTitleLabel = new OutlineLabel(1);
        mTitleLabel.setFont(FontPool.getInstance().getFontForHeight(titleLabelHeight));
        mTitleLabel.setText("Units");
        mTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        SwingUiUtils.setSize(mTitleLabel, titleLabelWidth, titleLabelHeight);
        mTitleLabel.setBackground(color);
        mTitleLabel.setOpaque(true);


        mSearchField = getjTextField();
        int searchFieldHeight = (int) (height * .025);
        int searchFieldWidth = width;
        mSearchField.setPreferredSize(new Dimension(searchFieldWidth, searchFieldHeight));
        mSearchField.setForeground(color);
        mSearchField.setBorder(BorderFactory.createEtchedBorder(color, color));

        int scrollPanelWidth = width;
        int scrollPanelHeight = height - titleLabelHeight - searchFieldHeight;
        JComponent pane = SwingUiUtils.createTranslucentScrollbar(scrollPanelWidth, scrollPanelHeight, newPanel);


        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(mTitleLabel);
        add(mSearchField);
        add(pane);
        setBackground(color);
        setOpaque(true);
    }

    public SummaryCard getSummaryCard(Entity entity) {
        return mEntityToSummaryCard.get(entity);
    }
    private JTextField getjTextField() {
        JTextField searchField = new JTextField(); //new RoundCornerTextField();
        searchField.addActionListener(e -> {
            if (searchField.getText().isBlank()) {
                int index = 0;
                for (Entity entity : mEntities) {
                    SummaryCard summaryCard = mSummaryCards.get(index++);
                    summaryCard.update(entity, mColor);
                    summaryCard.setVisible(true);
                    SwingUiUtils.removeAllListeners(summaryCard.getImage().getImageContainer());
                    summaryCard.getImage().getImageContainer().addActionListener(e2 -> mSelectedEntity = entity );
                }
            } else {

                Queue<Entity> matching = new LinkedList<>(mEntities.stream()
                        .filter(e2 -> e2.toString().contains(searchField.getText()))
                        .toList());

                Queue<Entity> nonMatching = new LinkedList<>(mEntities.stream()
                        .filter(e2 -> !matching.contains(e2))
                        .toList());

                int index = 0;

                Queue<Entity> current = matching;
                while (!current.isEmpty()) {
                    Entity polled = current.poll();
                    SummaryCard summaryCard = mSummaryCards.get(index);
                    summaryCard.update(polled, mColor);
                    summaryCard.setVisible(true);
                    SwingUiUtils.removeAllListeners(summaryCard.getImage().getImageContainer());
                    summaryCard.getImage().getImageContainer().addActionListener(e2 -> mSelectedEntity = polled );

                    index++;
                }

                current = nonMatching;
                while (!current.isEmpty()) {
                    Entity polled = current.poll();
                    SummaryCard summaryCard = mSummaryCards.get(index);
                    summaryCard.update(polled, mColor);
                    summaryCard.setVisible(false);
                    SwingUiUtils.removeAllListeners(summaryCard.getImage().getImageContainer());
                    summaryCard.getImage().getImageContainer().addActionListener(e2 -> mSelectedEntity = null);

                    index++;
                }
            }
        });
        return searchField;
    }

    public Entity getSelectedEntity() { return mSelectedEntity; }
    @Override
    public void update() {

    }

    @Override
    public void input() {

    }

    @Override
    public JPanel render() {
        return this;
    }
//    public void setBackground(Color color) {
//        super.setBackground(color);
//
//    }
}
