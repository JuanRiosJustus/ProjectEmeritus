package main.ui.panels.carousel;

import java.awt.EventQueue;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TestCarousel extends JFrame {

    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    TestCarousel frame = new TestCarousel();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public TestCarousel() {
        setTitle("Test Carousel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 714, 533);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        final Carousel carousel1 = new Carousel();

        JButton firstButton = new JButton("First");
        firstButton.addActionListener(e -> {
            ((CarouselLayout) carousel1.getLayout()).first(carousel1);
            CarouselLayout cl = (CarouselLayout) (carousel1.getLayout());
            cl.show(carousel1, "name_748459848877");
        });
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING).addGroup(
                gl_contentPane
                        .createSequentialGroup()
                        .addGroup(
                                gl_contentPane.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_contentPane.createSequentialGroup().addContainerGap().addComponent(carousel1, GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE))
                                        .addGroup(gl_contentPane.createSequentialGroup().addGap(78).addComponent(firstButton))).addContainerGap()));
        gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(
                gl_contentPane.createSequentialGroup().addContainerGap().addComponent(carousel1, GroupLayout.PREFERRED_SIZE, 436, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.UNRELATED).addComponent(firstButton).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        JPanel panel = new JPanel();

        carousel1.add(panel, "name_713420434161");

        JButton button = new JButton("New button");
        panel.add(button);

        JButton button_1 = new JButton("New button");
        panel.add(button_1);

        JPanel panel_1 = new JPanel();
        carousel1.add(panel_1, "name_748459848877");

        JCheckBox checkBox = new JCheckBox("New check box");
        panel_1.add(checkBox);

        JCheckBox checkBox_1 = new JCheckBox("New check box");
        panel_1.add(checkBox_1);
        contentPane.setLayout(gl_contentPane);
    }
}