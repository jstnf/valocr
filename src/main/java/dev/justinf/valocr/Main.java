package dev.justinf.valocr;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.AWTException;

public class Main {

    public static void main(String[] args) throws AWTException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException |
                ClassNotFoundException |
                InstantiationException |
                IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        ValOCR app = new ValOCR();
        app.start();
    }
}