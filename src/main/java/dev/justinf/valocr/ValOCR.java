package dev.justinf.valocr;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ValOCR {

    // Health Area
    public static final int LEFT_X = 576;
    public static final int UPPER_Y = 1006;
    public static final int RIGHT_X = 645;
    public static final int LOWER_Y = 1045;

    // Detect Death Area ("SWITCH PLAYER")
    public static final int LEFT_X2 = 164;
    public static final int UPPER_Y2 = 856;
    public static final int RIGHT_X2 = 275;
    public static final int LOWER_Y2 = 870;

    private static final boolean SUPER_HYPER_SENSITIVE_DANGER_MODE = false;

    private static final String TESSERACT_DATAPATH = "C:/TesseractDataset/tessdata/";

    private final Robot robot;
    private final ITesseract tesseract;
    private final InfoFrame window;

    private Rectangle healthRegion;
    private Rectangle deathRegion;

    private int currentHealth = 0;
    private int lastHealth = 0;
    private int sameHealthTicks = 0;
    private int invalidTicks = 8;
    private int validTicks = 0;

    private BufferedImage lastHealthImage = null;
    private BufferedImage lastDeathImage = null;

    private String lastHealthInput = "";
    private String lastDeathInput = "";

    public ValOCR() throws AWTException {
        robot = new Robot();
        tesseract = new Tesseract1();
        window = new InfoFrame(this);

        healthRegion = new Rectangle();
        healthRegion.setBounds(LEFT_X, UPPER_Y, RIGHT_X - LEFT_X, LOWER_Y - UPPER_Y);

        deathRegion = new Rectangle();
        deathRegion.setBounds(LEFT_X2, UPPER_Y2, RIGHT_X2 - LEFT_X2, LOWER_Y2 - UPPER_Y2);
    }

    public void start() {
        tesseract.setDatapath(TESSERACT_DATAPATH);

        Thread updateThread = new Thread(() -> {
            while (true) {
                lastHealthImage = robot.createScreenCapture(healthRegion);
                lastDeathImage = robot.createScreenCapture(deathRegion);

                try {
                    lastDeathInput = tesseract.doOCR(lastDeathImage);
                    lastHealthInput = tesseract.doOCR(lastHealthImage);

                    window.update();

                    if (lastDeathInput.toLowerCase().contains("switch") || lastDeathInput.toLowerCase().contains("player")) {
                        System.out.println("Death detected!");
                        System.out.println("Pausing OCR for 5 seconds!");
                        Thread.sleep(5000);
                        continue;
                    }

                    int processed = processResult(lastHealthInput);
                    if (processed != -1) {

                        // Murder
                        if (SUPER_HYPER_SENSITIVE_DANGER_MODE) {
                            lastHealth = processed;
                            if (lastHealth < currentHealth) {
                                System.out.println("SHOOT! You took damage! [" + currentHealth + " -> " + lastHealth + "]");
                            } else if (lastHealth == currentHealth) {
                                // System.out.println("Your health stayed the same! [" + currentHealth + "]");
                            } else {
                                System.out.println("You healed! [" + currentHealth + "->" + lastHealth + "]");
                            }

                            currentHealth = lastHealth;
                            continue;
                        }

                        // The safe code
                        validTicks++;
                        if (validTicks < 3) continue;
                        invalidTicks = 0;
                        if (processed == lastHealth) {
                            sameHealthTicks++;
                            if (sameHealthTicks > 1) {
                                if (lastHealth < currentHealth) {
                                    System.out.println("SHOOT! You took damage! [" + currentHealth + " -> " + lastHealth + "]");
                                } else if (lastHealth == currentHealth) {
                                    // System.out.println("Your health stayed the same! [" + currentHealth + "]");
                                } else {
                                    System.out.println("You healed! [" + currentHealth + "->" + lastHealth + "]");
                                }

                                currentHealth = lastHealth;
                                sameHealthTicks = 0;
                            }
                        } else {
                            lastHealth = processed;
                            sameHealthTicks = 0;
                        }
                    } else {
                        validTicks = 0;
                        invalidTicks++;
                        if (invalidTicks == 8) {
                            System.out.println("SHOOT!! You're probably dead.");
                        }
                    }
                } catch (TesseractException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        updateThread.start();

        JFrame frame = new JFrame("ValOCR");
        frame.setContentPane(window.getMainPanel());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public int processResult(String data) {
        if (data.length() > 0) {
            if (!Character.isDigit(data.charAt(0))) return -1;

            String result = data.charAt(0) + "";
            for (int i = 1; i < data.length(); i++) {
                if (Character.isDigit(data.charAt(i))) {
                    result += data.charAt(i);
                } else break;
            }

            try {
                return Integer.parseInt(result);
            } catch (Exception ignored) {
                return -1; // Incase of integer overflow
            }
        }

        return -1;
    }

    /* getset */
    public Rectangle getHealthRegion() {
        return healthRegion;
    }

    public Rectangle getDeathRegion() {
        return deathRegion;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getLastHealth() {
        return lastHealth;
    }

    public int getSameHealthTicks() {
        return sameHealthTicks;
    }

    public int getInvalidTicks() {
        return invalidTicks;
    }

    public int getValidTicks() {
        return validTicks;
    }

    public BufferedImage getLastHealthImage() {
        return lastHealthImage;
    }

    public BufferedImage getLastDeathImage() {
        return lastDeathImage;
    }

    public String getLastHealthInput() {
        return lastHealthInput;
    }

    public String getLastDeathInput() {
        return lastDeathInput;
    }
}