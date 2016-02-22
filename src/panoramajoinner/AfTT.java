/*
Copyright (c) 2008 Jiri Vanek <judovana@email.cz>

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
 * Neither the name of the <ORGANIZATION> nor the names of its contributors
may be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package panoramajoinner;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class AfTT {

    private static TransformingCanvas canvas;

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        canvas = new TransformingCanvas();
        TranslateHandler translater = new TranslateHandler();
        canvas.addMouseListener(translater);
        canvas.addMouseMotionListener(translater);
        canvas.addMouseWheelListener(new ScaleHandler());
        frame.setLayout(new BorderLayout());
        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private static class TransformingCanvas extends JComponent {

        private double translateX;
        private double translateY;
        private double rotateX;
        private double scale;
        BufferedImage b;

        TransformingCanvas() {
            translateX = 0;
            translateY = 0;
            scale = 1;
            setOpaque(true);
            setDoubleBuffered(true);
            try {
                b = ImageIO.read(new File("D:\\CD\\Context\\Data\\Fotky\\BlankRosaMatterhorn2007\\0012_IMG_0439.jpg"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void paint(Graphics g) {

            AffineTransform tx = new AffineTransform();
            tx.translate(translateX, translateY);
            tx.scale(scale, scale);
            tx.rotate(Math.toRadians(rotateX));
            Graphics2D ourGraphics = (Graphics2D) g;
            ourGraphics.setColor(Color.WHITE);
            ourGraphics.fillRect(0, 0, getWidth(), getHeight());
            ourGraphics.setTransform(tx);
            ourGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            ourGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            ourGraphics.setColor(Color.BLACK);
            ourGraphics.drawRect(50, 50, 50, 50);
            ourGraphics.fillOval(100, 100, 100, 100);
            ourGraphics.drawString("Test Affine Transform", 50, 30);
            AffineTransform af = new AffineTransform();
            af.rotate(Math.PI / 4d);
            ourGraphics.drawImage(b, af, null);
            // super.paint(g);
        }
    }

    private static class TranslateHandler implements MouseListener,
            MouseMotionListener {

        private int lastOffsetX;
        private int lastOffsetY;
        private int button;

        public void mousePressed(MouseEvent e) {
            // capture starting point
            lastOffsetX = e.getX();
            lastOffsetY = e.getY();
            button = e.getButton();
        }

        public void mouseDragged(MouseEvent e) {

            // new x and y are defined by current mouse location subtracted
            // by previously processed mouse location
            if (button == e.BUTTON1) {
                int newX = e.getX() - lastOffsetX;
                int newY = e.getY() - lastOffsetY;

                // increment last offset to last processed by drag event.
                lastOffsetX += newX;
                lastOffsetY += newY;

                // update the canvas locations
                canvas.translateX += newX;
                canvas.translateY += newY;

                // schedule a repaint.
            }
            if (button == e.BUTTON3) {
                int newX = e.getX() - lastOffsetX;
                int newY = e.getY() - lastOffsetY;

                // increment last offset to last processed by drag event.
                lastOffsetX += newX;
                lastOffsetY += newY;

                // update the canvas locations
                canvas.rotateX += newX;


                // schedule a repaint.
            }
            canvas.repaint();
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }
    }

    private static class ScaleHandler implements MouseWheelListener {

        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {

                // make it a reasonable amount of zoom
                // .1 gives a nice slow transition
                canvas.scale += (.1 * e.getWheelRotation());
                // don't cross negative threshold.
                // also, setting scale to 0 has bad effects
                canvas.scale = Math.max(0.00001, canvas.scale);
                canvas.repaint();
            }
        }
    }
}
