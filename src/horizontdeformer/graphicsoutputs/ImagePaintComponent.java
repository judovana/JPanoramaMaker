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
package horizontdeformer.graphicsoutputs;

import cammons.Cammons;
import horizontdeformer.analyzer.Analyzer;
import cammons.TPoint;
import horizontdeformer.deformer.DeformerData;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JComponent;

public class ImagePaintComponent extends JComponent {

    /** Creates a new instance of PaintComponent */
    private BufferedImage im;
    private Double zoom = 1d;
    private JComboBox interpolation;
    private ArrayList<TPoint> tpoint;
    private TPoint selected = null;
    private TPoint selcoords;
    private DeformerData dd;
    private double savedX;
    private double savedY;

    public void addDEformatorsData(int x, int y, int z) {
        tpoint.add(new TPoint(x, y, z));
        int sm = dd.getMethod();
        dd = new DeformerData(tpoint, im);
        try {
            dd.setMethod(sm);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setInterpolation(JComboBox interpolation) {
        this.interpolation = interpolation;
    }

    public ArrayList<TPoint> getPrimaryData() {
        return tpoint;
    }

    public DeformerData getDd() {
        return dd;
    }

    public Double getZoom() {
        return zoom;
    }

    public void setZoom(Double zoom) {
        this.zoom = zoom;
    }

    public ImagePaintComponent() {
    }

    public void setIamge(BufferedImage i) {
        im = i;
    }

    public void loadImage(File f) throws IOException {
        im = ImageIO.read(f);

    }

    public BufferedImage getImage() {
        return im;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (im != null) {
            if (zoom != 1) {
                g2d.drawImage(im, 0, 0, (int) (zoom * im.getWidth()), (int) (zoom * im.getHeight()), this); //:((
            } else {
                g2d.drawImage(im, null, 0, 0);

            }
            if (tpoint != null) {
                for (int i = 1; i < tpoint.size(); i++) {


                    TPoint elem = (TPoint) tpoint.get(i);
                    g2d.setColor(Color.RED);
                    GraphicsAdds.Cross(g2d, (int) (elem.x * zoom), (int) (elem.y * zoom), 20);
                }
            }
            if (dd != null) {
                dd.draw(g2d, zoom, interpolation.getSelectedIndex());
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        paint(g);
    }

    public void loadTxt(File file) throws IOException {
        tpoint = new ArrayList();
        BufferedReader br =  new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
        TPoint tp = new TPoint();
        String s = br.readLine();
        tp.x = Integer.valueOf(s);
        s = br.readLine();
        tp.y = Integer.valueOf(s);
        s = br.readLine();
        tp.z = Integer.valueOf(s);
        tpoint.add(tp);
        s = br.readLine();
        while (s != null) {
            tp = new TPoint();
            tpoint.add(tp);

            tp.x = Integer.valueOf(s);
            s = br.readLine();
            tp.y = Integer.valueOf(s);
            s = br.readLine();
            tp.z = Integer.valueOf(s);
            s = br.readLine();
        }

        dd = new DeformerData(tpoint, im);

    }

    public void loadTxt(Analyzer a, int resultset) {
        tpoint = new ArrayList();

        // tpoint.add(new TPoint(a.getFotosIndividualHeight(),0,0));
        tpoint.addAll(a.getResult(resultset));


        dd = new DeformerData(tpoint, im);

    }

    public void CreateNewPrimaryData() {
        tpoint = new ArrayList();
    }

    public void setPrimaryData(ArrayList l) {

        tpoint = l;


        dd = new DeformerData(tpoint, im);
    }

    public boolean mouseDown(MouseEvent evt, int x, int y) {
        //TPoint oldSelected=selected;
        selected = null;
        double X = (double) x / zoom;
        double Y = (double) y / zoom;
        double mindist = 99999999d;
        if (evt.getModifiers() == 18 || evt.getModifiers() == 6) {
            for (Iterator<TPoint> it = tpoint.iterator(); it.hasNext();) {
                TPoint tPoint = it.next();
                double dist = Cammons.vzdal(X, Y, (double) tPoint.x, (double) tPoint.y);
                if (dist < mindist) {
                    mindist = dist;
                    selected = tPoint;
                }

            }

        }
        /*if (oldSelected==selected){
        selected=null;dd=new DeformerData(tpoint, im);return true;
        }*/
        if (mindist * zoom.doubleValue() > 40) {
            selected = null;
        }
        if (selected != null) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                savedX = selected.x;
                savedY = selected.y;
                return true;
            }
            if (evt.getButton() == MouseEvent.BUTTON3) {
                tpoint.remove(selected);
                selected = null;
                int sm = dd.getMethod();
                dd = new DeformerData(tpoint, im);
                try {
                    dd.setMethod(sm);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    public void mouseUp() {

        selected = null;
    }

    public boolean mouseMove(MouseEvent evt, int x, int y) {

        if (selected != null) {
            double X = (double) x / zoom;
            double Y = (double) y / zoom;
            selected.x = (int) X;
            selected.y = (int) Y;
            int sm = dd.getMethod();
            dd = new DeformerData(tpoint, im);
            try {
                dd.setMethod(sm);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }
};
    

