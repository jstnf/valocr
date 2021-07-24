package dev.justinf.valocr;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

public class ImagePaintPanel extends JPanel {

    private final int dimensionX, dimensionY;
    private BufferedImage currentFrame;

    public ImagePaintPanel(Supplier<BufferedImage> imageSupplier, int dimensionX, int dimensionY) {
        this.dimensionX = dimensionX;
        this.dimensionY = dimensionY;
        new Timer(17, e -> {
            currentFrame = imageSupplier.get();
            repaint();
        }).start();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(dimensionX, dimensionY);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentFrame != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            int x = (getWidth() - currentFrame.getWidth()) / 2;
            int y = (getHeight() - currentFrame.getHeight()) / 2;
            g2d.drawImage(currentFrame, x, y, this);
            g2d.dispose();
        }
    }
}
