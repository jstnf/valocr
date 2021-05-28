package dev.justinf.valocr;

import javax.swing.*;
import java.awt.*;

public class InfoFrame {

    private final ValOCR app;

    /* Components */
    private JPanel mainPanel;
    private JPanel imageInputPanel;
    private JPanel infoPanel;
    private JPanel healthPanel;
    private JPanel deathPanel;
    private JPanel healthImagePanel;
    private JPanel deathImagePanel;
    private JTextField healthImageInput;
    private JTextField deathImageInput;
    private JTextField currentHealthTextField;
    private JTextField lastHealthTextField;
    private JTextField sameHealthTicksTextField;
    private JTextField invalidTicksTextField;
    private JLabel currentHealthLabel;
    private JLabel lastHealthLabel;
    private JLabel sameHealthTicksLabel;
    private JLabel invalidTicksLabel;
    private JTextField validTicksTextField;
    private JLabel validTicksLabel;

    public InfoFrame(ValOCR app) {
        this.app = app;
    }

    public void update() {
        healthImageInput.setText(app.getLastHealthInput());
        if (app.processResult(app.getLastHealthInput()) != -1) {
            healthImageInput.setBackground(new Color(98, 222, 131));
        } else {
            healthImageInput.setBackground(new Color(247, 144, 121));
        }

        deathImageInput.setText(app.getLastDeathInput());
        if (app.getLastDeathInput().toLowerCase().contains("switch") || app.getLastDeathInput().toLowerCase().contains("player")) {
            deathImageInput.setBackground(new Color(98, 222, 131));
        } else {
            deathImageInput.setBackground(new Color(247, 144, 121));
        }

        currentHealthTextField.setText(app.getCurrentHealth() + "");
        lastHealthTextField.setText(app.getLastHealth() + "");
        sameHealthTicksTextField.setText(app.getSameHealthTicks() + "");
        invalidTicksTextField.setText(app.getInvalidTicks() + "");
        if (app.getInvalidTicks() >= 8) {
            invalidTicksTextField.setBackground(new Color(98, 222, 131));
        } else {
            invalidTicksTextField.setBackground(new Color(247, 144, 121));
        }
        validTicksTextField.setText(app.getValidTicks() + "");
        if (app.getValidTicks() >= 3) {
            validTicksTextField.setBackground(new Color(98, 222, 131));
        } else {
            validTicksTextField.setBackground(new Color(247, 144, 121));
        }
    }

    /* getset */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        healthImagePanel = new ImagePaintPanel(app::getLastHealthImage, ValOCR.RIGHT_X - ValOCR.LEFT_X, ValOCR.LOWER_Y - ValOCR.UPPER_Y);
        deathImagePanel = new ImagePaintPanel(app::getLastDeathImage, ValOCR.RIGHT_X2 - ValOCR.LEFT_X2, ValOCR.LOWER_Y2 - ValOCR.UPPER_Y2);
    }
}
