package dev.justinf.valocr;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class InfoFrame {

    private static final Color _GREEN = new Color(98, 222, 131);
    private static final Color _RED = new Color(247, 144, 121);
    private static final Color _YELLOW = new Color(255, 212, 94);

    private final ValOCR app;
    private final AtomicBoolean connectionResult;

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
    private JLabel imageInputPanelTitleLabel;
    private JLabel infoPanelTitleLabel;
    private JPanel connectionInfoPanel;
    private JLabel connectionInfoPanelTitleLabel;
    private JPanel sinceLastHandshakePanel;
    private JLabel sinceLastHandshakeLabel;
    private JTextField sinceLastHandshakeTextField;
    private JTextField serialPortTextField;
    private JButton connectButton;
    private JPanel connectPanel;
    private JPanel currentSerialPortPanel;
    private JLabel currentSerialPortLabel;
    private JTextField currentSerialPortTextField;
    private JLabel consolePanelTitleLabel;
    private JPanel consolePanel;
    private JTextArea consoleTextArea;
    private JScrollPane consoleScrollPane;
    private JButton doSomethingButton;

    public InfoFrame(ValOCR app) {
        this.app = app;
        connectionResult = new AtomicBoolean(false);
        connectButton.addActionListener(e -> {
            app.getSerialTerminal().setPort(serialPortTextField.getText());
            app.testConnection();
        });
        doSomethingButton.addActionListener(e -> {
            byte random = (byte) ((int) (Math.random() * 3));
            app.getSerialTerminal().write(random);
            app.log("Sent " + random + " to " + app.getSerialTerminal().getPort() + "!");
        });

        // Auto-scroll console text area
        ((DefaultCaret) consoleTextArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    public void update() {
        healthImageInput.setText(app.getLastHealthInput());
        if (app.processResult(app.getLastHealthInput()) != -1) {
            healthImageInput.setBackground(_GREEN);
        } else {
            healthImageInput.setBackground(_RED);
        }

        deathImageInput.setText(app.getLastDeathInput());
        if (app.getLastDeathInput().toLowerCase().contains("switch") || app.getLastDeathInput().toLowerCase().contains("player")) {
            deathImageInput.setBackground(_GREEN);
        } else {
            deathImageInput.setBackground(_RED);
        }

        currentHealthTextField.setText(app.getCurrentHealth() + "");
        lastHealthTextField.setText(app.getLastHealth() + "");
        sameHealthTicksTextField.setText(app.getSameHealthTicks() + "");
        invalidTicksTextField.setText(app.getInvalidTicks() + "");
        if (app.getInvalidTicks() >= 8) {
            invalidTicksTextField.setBackground(_GREEN);
        } else {
            invalidTicksTextField.setBackground(_RED);
        }
        validTicksTextField.setText(app.getValidTicks() + "");
        if (app.getValidTicks() >= 3) {
            validTicksTextField.setBackground(_GREEN);
        } else {
            validTicksTextField.setBackground(_RED);
        }

        currentSerialPortTextField.setText(app.getSerialTerminal().getPort());
        if (app.getSerialTerminal().isConnecting()) {
            currentSerialPortTextField.setBackground(_YELLOW);
        } else {
            if (connectionResult.get()) {
                currentSerialPortTextField.setBackground(_GREEN);
            } else {
                currentSerialPortTextField.setBackground(_RED);
            }
        }

        if (app.getSerialTerminal().getLastHandshakeTimestamp() == -1) {
            sinceLastHandshakeTextField.setText("never");
        } else {
            sinceLastHandshakeTextField.setText("" + (System.currentTimeMillis() - app.getSerialTerminal().getLastHandshakeTimestamp()));
        }
    }

    public JTextArea getConsoleTextArea() {
        return consoleTextArea;
    }

    /* getset */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void atomicallySetConnectionResult(boolean result) {
        connectionResult.set(result);
    }

    private void createUIComponents() {
        healthImagePanel = new ImagePaintPanel(app::getLastHealthImage, ValOCR.RIGHT_X - ValOCR.LEFT_X, ValOCR.LOWER_Y - ValOCR.UPPER_Y);
        deathImagePanel = new ImagePaintPanel(app::getLastDeathImage, ValOCR.RIGHT_X2 - ValOCR.LEFT_X2, ValOCR.LOWER_Y2 - ValOCR.UPPER_Y2);
    }
}
