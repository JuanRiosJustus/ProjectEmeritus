package main.ui.presets.loadout;

import main.constants.Constants;
import main.engine.EngineScene;
import main.game.entity.Entity;
import main.graphics.temporary.JImage;
import main.ui.components.OutlineLabel;
import main.ui.custom.SwingUiUtils;
import main.ui.presets.RoundCornerTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class UnitSelectionListScene extends EngineScene {
    private final List<Entity> mEntities = new ArrayList<>();
    private final List<SummaryCard> mSummaryCards = new ArrayList<>();
    private final OutlineLabel mTitleLabel = new OutlineLabel();
    private JTextField mSearchFiled = new JTextField();
    private Entity mSelectedEntity = null;
    private Map<Entity, SummaryCard> mEntityToCardMap = new HashMap<>();

    public void setup(List<Entity> entityList, int width, int height) {

        setPreferredSize(new Dimension(width, height));

        int spriteSizes = Constants.CURRENT_SPRITE_SIZE;
        removeAll();

        JPanel newPanel = new JPanel();
        newPanel.setOpaque(true);
        newPanel.setBackground(Color.darkGray);
        GridBagConstraints gridBagConstraints = SwingUiUtils.createGbc(0, 0);
        newPanel.removeAll();
        newPanel.setLayout(new GridBagLayout());
        newPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // TODO:
        // if there are more entities loaded than empty default slots, add more rows equal to the amount needed

        BufferedImage blank = new BufferedImage(spriteSizes, spriteSizes, BufferedImage.TYPE_INT_ARGB);
        int rowHeight = (int) (spriteSizes * 2);
        int rowWidth = width;

        for (int index = 0; index < entityList.size() + (entityList.size() * .1); index++) {

            Entity entity = (index < entityList.size() ? entityList.get(index) : null);
            // .95 just so that edges dont touch
            SummaryCard summaryCard = new SummaryCard((int) (rowWidth * .95), (int) (rowHeight * .95), entity, spriteSizes);
            summaryCard.getImage().getImageContainer().addActionListener(e2 -> mSelectedEntity = entity);
            mSummaryCards.add(summaryCard);
            mEntityToCardMap.put(entity, summaryCard);

            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = index;
            gridBagConstraints.ipady = 5;
            gridBagConstraints.ipadx = 5;
            gridBagConstraints.anchor = GridBagConstraints.CENTER;

            newPanel.add(summaryCard, gridBagConstraints);
        }

        mEntities.addAll(entityList);
        JPanel boxLayoutPanel = new JPanel();
        boxLayoutPanel.setLayout(new BoxLayout(boxLayoutPanel, BoxLayout.Y_AXIS));
        boxLayoutPanel.add(newPanel);

        mTitleLabel.setText("Collection");
        mTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mTitleLabel.setPreferredSize(new Dimension(width, (int) mTitleLabel.getPreferredSize().getHeight()));
        mTitleLabel.setBackground(Color.DARK_GRAY);
        add(mTitleLabel);

        mSearchFiled = getjTextField();
        mSearchFiled.setPreferredSize(new Dimension(width, (int) mSearchFiled.getPreferredSize().getHeight()));
        add(mSearchFiled);

        add(SwingUiUtils.createTranslucentScrollbar(width, (int)
                        (height - mSearchFiled.getPreferredSize().getHeight() - mTitleLabel.getPreferredSize().getHeight()),
                boxLayoutPanel));
        setBackground(Color.DARK_GRAY);
    }

    public SummaryCard getSummaryCard(Entity entity) {
        return mEntityToCardMap.get(entity);
    }
    private JTextField getjTextField() {
        JTextField searchField = new RoundCornerTextField();
        searchField.addActionListener(e -> {
            if (searchField.getText().isBlank()) {
                int index = 0;
                for (Entity entity : mEntities) {
                    SummaryCard summaryCard = mSummaryCards.get(index++);
                    summaryCard.update(entity, Constants.CURRENT_SPRITE_SIZE);
                    summaryCard.setVisible(true);
                    SwingUiUtils.removeAllActionListeners(summaryCard.getImage().getImageContainer());
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
                    summaryCard.update(polled, Constants.CURRENT_SPRITE_SIZE);
                    summaryCard.setVisible(true);
                    SwingUiUtils.removeAllActionListeners(summaryCard.getImage().getImageContainer());
                    summaryCard.getImage().getImageContainer().addActionListener(e2 -> mSelectedEntity = polled );

                    index++;
                }

                current = nonMatching;
                while (!current.isEmpty()) {
                    Entity polled = current.poll();
                    SummaryCard summaryCard = mSummaryCards.get(index);
                    summaryCard.update(polled, Constants.CURRENT_SPRITE_SIZE);
                    summaryCard.setVisible(false);
                    SwingUiUtils.removeAllActionListeners(summaryCard.getImage().getImageContainer());
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
}
