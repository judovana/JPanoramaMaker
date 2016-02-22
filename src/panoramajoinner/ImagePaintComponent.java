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

import cammons.StupidInteger;
import cammons.TPoint;
import cammons.TRect;
import horizontdeformer.analyzer.Analyzer;
import panoramajoinner.autopanoconnection.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.FilteredImageSource;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class ImagePaintComponent extends JComponent {
    private int forcing = 0;

    private String normalize1(String name1, String name2) {
        return name1 + "-" + name2;
    }

    private String normalize2(String name1, String name2) {
        return cut(name1) + "-" + name2;
    }

    private static String cut(String s) {
        return s.substring(0, s.lastIndexOf("."));
    }

    public static void main(String[] args) {
        System.out.println(cut("123.abcd.pngm"));
    }

    void moveSelectedToTop() {
       if (getTheOneIndex()==null) return;
        int index = getTheOneIndex();
        if (index < 0 || index >= im.size()) {
            return;
        }
        //PanoramatImage p0 = im.get(im.size()-1);
        PanoramatImage ps = im.get(index);
        im.remove(index);
        im.add(ps);
        //index=im.size()-1;//?
    }

    void moveSelectedToBottom() {
       if (getTheOneIndex()==null) return;
        int index = getTheOneIndex();
        if (index < 0 || index >= im.size()) {
            return;
        }
        //PanoramatImage p0 = im.get(0);
        PanoramatImage ps = im.get(index);
        im.remove(index);
        im.add(0, ps);
        //index=0;//?


    }

    void setForcing(int i) {
        forcing = i;
    }

    public class ObservableMe extends Observable {

        @Override
        public synchronized void setChanged() {
            super.setChanged();
        }
    }
    public final ObservableMe observableMe = new ObservableMe();
    private List<Integer> indexes=new ArrayList<Integer>();
    private ArrayList<PanoramatImage> im = new ArrayList();
    private Double zoom = 1d;
    private TPoint posun = new TPoint();
    private boolean advancedDraw = false;
    public static final int LEFT = 11;
    public static final int RIGHT = 12;
    public static final int UP = 22;
    public static final int DOWN = 21;
    public static final int JUST_RIGHT = 1;
    public static final int JUST_LEFT = 2;
    public static final int ALL_RIGHT = 11;
    public static final int ALL_LEFT = 22;
    public static final int JUST_UP = 3;
    public static final int JUST_DOWN = 4;
    public static final int ALL_UP = 33;
    public static final int ALL_DOWN = 44;
    private ArrayList<String> sugestedSaves;

    public List<String> getSugestedSaves() {
        return Collections.unmodifiableList(sugestedSaves);
    }

    private boolean isJust(int i) {
        return (i < 10);
    }

    private boolean isRight(int i) {
        return (i % 10 == 1);
    }

    private boolean isLeft(int i) {
        return (i % 10 == 2);
    }

    private boolean isUp(int i) {
        return (i % 10 == 3);
    }

    private boolean isDown(int i) {
        return (i % 10 == 4);
    }
    private boolean hCorner;
    private boolean vCorner;
    private JProgressBar progress;
    private AffineTransform af;

    public void setVCorner(boolean vCorner) {
        this.vCorner = vCorner;
    }

    public void setHCorner(boolean hCorner) {
        this.hCorner = hCorner;
    }

    public boolean isAdvancedDraw() {
        return advancedDraw;
    }

    public void setAdvancedDraw(boolean advancedDraw) {
        this.advancedDraw = advancedDraw;
    }

    public List<Integer> getIndexes() {
        if (indexes==null) indexes = new ArrayList<Integer>();
        return indexes;
    }

    public Integer getTheOneIndex() {
        if (indexes == null || indexes.size() != 1) {
            return null;
        }
        return indexes.get(0);
    }

    public void deselectAll() {
        this.indexes = new ArrayList<Integer>();
    }

    public void setIndexes(List<Integer> index) {
        this.indexes = index;
    }
    public void addIndex(int index) {
        if (indexes==null) indexes=new ArrayList();
        for (int i = 0; i < indexes.size(); i++) {
            Integer integer = indexes.get(i);
            if (integer.intValue()==index) return;

        }
        if (index>=0 && index< im.size())indexes.add(index);
        
    }
    
    public void removeIndex(int index) {
        if (indexes==null) return;
        for (int i = 0; i < indexes.size(); i++) {
            Integer integer = indexes.get(i);
            if (integer.intValue()==index) {
                indexes.remove(i);
                return;
              }
        }
        
    }

    public void resetIndex(int index) {
        indexes=new ArrayList();
        if (index>=0 && index< im.size())indexes.add(index);
    }

    public void moveRightLeft(int x) {
        posun.x += x;
        if (x != 0) {
            repaint();
        }
    }

    public void moveTopDown(int y) {
        posun.y += y;
        if (y != 0) {
            repaint();
        }
    }

    public void zeroMovement() {
        posun.x = 0;
        posun.y = 0;
    }

    public TPoint getPosun() {
        return posun;
    }

    public void setPosun(TPoint p) {
        final int k = 1000000;
        this.posun = (TPoint) p.clone();
        if (posun.x < -k) {
            posun.x = 0;
        }
        if (posun.x > k) {
            posun.x = 0;
        }
        if (posun.y < -k) {
            posun.y = 0;
        }
        if (posun.y > k) {
            posun.y = 0;
        }

    }

    public PanoramatImage getTheSelected() {
        if (getTheOneIndex()==null) return null;
        return im.get(getTheOneIndex());
    }
    public PanoramatImage getImage(int i) {
        return im.get(i);
    }
    public PanoramatImage getImageByIndex(int i) {
        Integer index= getIndexes().get(i);
        return (getImage(index));
    }

    public int getImagesCount() {
        return im.size();
    }

    public void findIndex(int xxx, int yyy,boolean  add) {
        int index = -1;
        double z = zoom;
        for (int xx = 0; xx < im.size(); xx++) {
            PanoramatImage a = im.get(xx);
            int rLeft = (int) Math.round((double) a.getLeft() * z) + posun.x;
            int rTop = (int) Math.round((double) a.getTop() * z) + posun.y;
            int rRight = (int) Math.round((double) rLeft + (double) a.getImage().getWidth() * z);
            int rBottom = (int) Math.round((double) rTop + (double) a.getImage().getHeight() * z);
            int x = xxx;
            int y = yyy;
            if ((x > rLeft) && (x < rRight) && (y > rTop) && (y < rBottom)) {
                index = xx;
               // break;
            }
        }
        if (add) {
            int l=indexes.size();
            addIndex(index);
            if (indexes.size()==l){
                removeIndex(index);
            }
        } else {
            resetIndex(index);
        }
    }

    public Double getZoom() {
        return zoom;
    }

    public void setZoom(Double zoom) {
        this.zoom = zoom;
    }

    public ImagePaintComponent() {
    }

    public void setImage(PanoramatImage image, int index) {
        if (im == null) {
            return;
        }
        if (index > im.size() || index < 0) {
            im.add(image);
        } else {
            im.remove(index);
            im.add(index, image);
        }
        actualiseQuickList();
    }

    public void loadImage(File f, int index) throws IOException {
        PanoramatImage i = new PanoramatImage(f);
        setImage(i, index);

    }



    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (im != null) {
            for (Iterator it = im.iterator(); it.hasNext();) {
                PanoramatImage elem = (PanoramatImage) it.next();
              //  if (zoom != 1) {
                    if (af != null && getTheSelected() == elem) {
                        g2d.drawImage(elem.getImage(), af, null);
                    } else {
                        g2d.drawImage(elem.getImage(), posun.x + (int) Math.round((double) elem.getLeft() * zoom), posun.y + (int) Math.round((double) elem.getTop() * zoom), (int) Math.round(zoom * (double) elem.getImage().getWidth()), (int) Math.round(zoom * (double) elem.getImage().getHeight()), this); //:((
                    }
              //  } else {
//                    if (af != null && getTheSelected() == elem) {
//                        g2d.drawImage(elem.getImage(), af, null);
//                    } else {
//                        g2d.drawImage(elem.getImage(), null, posun.x + (int) (elem.getLeft()), posun.y + (elem.getTop()));
//                    }
//
//                }
            }

            if (advancedDraw) {
                for (Iterator it = im.iterator(); it.hasNext();) {
                    PanoramatImage elem = (PanoramatImage) it.next();
                    g2d.drawLine(this.getWidth() / 2, this.getHeight() / 2,
                            posun.x + (int) Math.round(zoom * (double) (elem.getWidth() / 2 + elem.getLeft())),
                            posun.y + (int) Math.round(zoom * (double) (elem.getHeight() / 2 + elem.getTop())));

                }
                g2d.setColor(Color.RED);
                for (int i=0; i<getIndexes().size();i++){
                    PanoramatImage elem = getImageByIndex(i);
                    g2d.drawRect(posun.x + (int) Math.round(zoom * (double) elem.getLeft()),
                            posun.y + (int) Math.round(zoom * (double) elem.getTop()),
                            (int) Math.round(zoom * (double) (elem.getWidth())),
                            (int) Math.round(zoom * (double) (elem.getHeight())));
                }
                g2d.setColor(Color.BLACK);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        paint(g);
    }

    public void loadTxt(File file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

        String s = br.readLine();
        while (s != null) {

            s = br.readLine();
        }



    }

    void clearImages() {
        im = new ArrayList();
    }

    void connection(int whichType) {
        int wasWhich = whichType;
        Connector c = null;
        ConnectionData d = null;
        Neighbourhood nghbr;
        int index = getTheOneIndex();
        if (isUp(whichType) || isDown(whichType)) {
            nghbr = getVerticalNeighbours(index);
        } else {
            nghbr = getHorizontalNeighbours(index);
        }
        ArrayList<PanoramatImage> dest = new ArrayList<PanoramatImage>();
        if (isUp(wasWhich)) {
            if (isJust(wasWhich)) {
                whichType = JUST_RIGHT;
            } else {
                whichType = ALL_RIGHT;
            }
        }
        if (isDown(wasWhich)) {
            if (isJust(wasWhich)) {
                whichType = JUST_LEFT;
            } else {
                whichType = ALL_LEFT;
            }
        }

        switch (whichType) {
            case JUST_LEFT:
                dest.add(im.get(nghbr.x));
                break;
            case JUST_RIGHT:
                dest.add(im.get(nghbr.y));
                break;
            case ALL_LEFT:
                for (Integer i : nghbr.getAllX()) {
                    dest.add(im.get(i.intValue()));
                }
                break;
            case ALL_RIGHT:
                for (Integer i : nghbr.getAllY()) {
                    dest.add(im.get(i.intValue()));
                }
                break;

        }

        if (isUp(wasWhich)) {
            if (isJust(wasWhich)) {
                whichType = JUST_LEFT;
            } else {
                whichType = ALL_LEFT;
            }
        }
        if (isDown(wasWhich)) {
            if (isJust(wasWhich)) {
                whichType = JUST_RIGHT;
            } else {
                whichType = ALL_RIGHT;
            }
        }

        JDialog dg = new JDialog((JFrame) (this.getParent().getParent().getParent().getParent().getParent()), "please wait", false);
        dg.setSize(300, 100);
        dg.setLocationRelativeTo(null);
        JProgressBar pb = new JProgressBar();
        dg.add(pb);
        pb.setMaximum(dest.size() + 1);
        pb.setValue(0);
        dg.setAlwaysOnTop(true);
        dg.setVisible(true);
        try {
            for (PanoramatImage pi : dest) {
                pb.setValue(pb.getValue() + 1);
                try {

                    if (isRight(whichType)) {
                        c = new Connector(pi, getTheSelected(), wasWhich != whichType);
                    } else {
                        c = new Connector(getTheSelected(), pi, wasWhich != whichType);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (c != null) {
                    try {
                        d = c.generate();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } finally {
                        c.clean();
                    }
                }
                if (d != null && d.getData().size() > 0) {
                    ArrayList<MatchPoint> a = d.getData();
                    TPoint vektor = new TPoint();
                    for (Iterator<MatchPoint> it = a.iterator(); it.hasNext();) {
                        MatchPoint mp = it.next();
                        if (wasWhich != whichType) {
                            //rotate -90 leftdown
                            c.rotatePoint(mp, isLeft(whichType));
                        }
                        if (isRight(whichType)) {
                            vektor.x += getTheSelected().getLeft() + mp.x2 - mp.x1;
                            vektor.y += getTheSelected().getTop() + mp.y2 - mp.y1;
                        } else {
                            vektor.x += getTheSelected().getLeft() + mp.x1 - mp.x2;
                            vektor.y += getTheSelected().getTop() + mp.y1 - mp.y2;
                        }
                        System.out.println(mp);

                    }
                    vektor.x /= a.size();
                    vektor.y /= a.size();
                    pi.setLeft(vektor.x);
                    pi.setTop(vektor.y);
                } else {
                    JOptionPane.showMessageDialog(this, "No  match points found for selected " + getTheSelected().getSrc().toString() + "\n and its neighjbour " + pi.getSrc().toString());
                }
            }
        } finally {
            dg.setVisible(false);
            this.repaint();

        }
    }

    void drawExport(BufferedImage kam) {
        if (im == null || im.size() < 1 || kam == null) {
            return;
        }
        PanoramatImage elemL = getLeftestIamge();
        PanoramatImage elemT = getTopestIamge();
        StupidInteger e = new StupidInteger(0), w = new StupidInteger(0), n = new StupidInteger(0), s = new StupidInteger(0);//nej polohy e a w jsou asi obracene:)
        int hposun, vposun;
        getPosun(e, w, n, s, true);
        hposun = -n.getValue();
        vposun = -e.getValue();
        double z = this.zoom.doubleValue();
        int l = posun.x + (int) Math.round((double) elemL.getLeft() * z) + (int) ((double) hposun * 0);
        int t = posun.y + (int) Math.round((double) elemT.getTop() * z) + (int) ((double) vposun * 0);
        this.getGraphics().drawImage(kam, l, t, (int) ((double) kam.getWidth() * z), (int) ((double) kam.getHeight() * z), null);
// z paintu g2d.drawImage(elem.getImage(),posun.x+(int)Math.round((double)elem.getLeft()*zoom),posun.y+(int)Math.round((double)elem.getTop()*zoom),(int)Math.round(zoom*(double)elem.getImage().getWidth()),(int)Math.round(zoom*(double)elem.getImage().getHeight()),this); //:((     
    }

    Neighbourhood getHorizontalNeighbours(int i) {
        Neighbourhood vysledek = new Neighbourhood(-1, -1);
        /*int rdif =9999999;
        int ldif =9999999;

        PanoramatImage ai=im.get(i);
        for (int x=0 ; x<im.size() ; x++)  if  (x!=i)  {
        PanoramatImage ax=im.get(x);
        /*   if ( ((ai.getLeft()>ax.getLeft())&&(ai.getLeft()-(ax.getLeft()+ax.getImage().getWidth())<ldif ())  {
        ldif (=ai.getLeft()-(ax.getLeft()+ax.getImage().getWidth());
        result.x=x;
        }
        if ( ((ai.getLeft()+ai.getImage().getWidth()>ax.getLeft()+ax.getImage().getWidth())&&(ax.getLeft()-(ai.getLeft()+ai.getImage().getWidth())<rdif ())  {
        rdif (=(ax.getLeft())-(ai.getLeft()+ai.getImage().getWidth());
        result.y=x;
        }
        ee:((*/
        /*  if ((ax.getLeft()<ai.getLeft())&&(ax.getLeft()+ax.getImage().getWidth()>ai.getLeft()) )
        vysledek.x=x;
        if ((ax.getLeft()<ai.getLeft()+ai.getImage().getWidth())&&(ax.getLeft()+ax.getImage().getWidth()>ai.getLeft()+ai.getImage().getWidth()))
        vysledek.y=x;
        }
        //pouziti v edges
        //prochazim vsecky a kdyz jsou oba nightbours mensi nez umistenej
        //tak edguj
        //pozor na anomalii pri neighbours  kdy je levej bliz prqvumy nez p[ravej a vice versa
         */


        vysledek.setAllX(getMostUpImageContainsThisOnesEDGE(LEFT, i));


        vysledek.setAllY(getMostUpImageContainsThisOnesEDGE(RIGHT, i));
        return vysledek;
    }

    Neighbourhood getVerticalNeighbours(int i) {
        Neighbourhood vysledek = new Neighbourhood(-1, -1);
        /*int rdif =9999999;
        int ldif =9999999;

        PanoramatImage ai=im.get(i);
        for (int x=0 ; x<im.size() ; x++)  if  (x!=i)  {
        PanoramatImage ax=im.get(x);

        if ((ax.getTop()<ai.getTop())&&(ax.getTop()+ax.getImage().getHeight()>ai.getTop()) )
        vysledek.y=x;//horni
        if ((ax.getTop()<ai.getTop()+ai.getImage().getHeight())&&(ax.getTop()+ax.getImage().getHeight()>ai.getTop()+ai.getImage().getHeight()))
        vysledek.x=x;//spodni
        }*/

        vysledek.setAllX(getMostUpImageContainsThisOnesEDGE(DOWN, i));

        vysledek.setAllY(getMostUpImageContainsThisOnesEDGE(UP, i));
        return vysledek;
    }

    void exportAllToFile(File f, boolean biggest, boolean bluredges, boolean blurV, boolean blurH, String output_type) throws IOException {
        BufferedImage kam = exportAll(biggest, bluredges, blurV, blurH);
        ImageIO.write(kam, output_type, f);
    }

    public BufferedImage exportAll(boolean biggest, boolean bluredges, boolean blurV, boolean blurH) {



        StupidInteger e = new StupidInteger(0), w = new StupidInteger(0), n = new StupidInteger(0), s = new StupidInteger(0);//nej polohy e a w jsou asi obracene:)
        int hposun, vposun;
        BufferedImage kam;
        TPoint nghbr;
        TRect r = new TRect();
        int xx, odkud, p1, p2;
        {

            getPosun(e, w, n, s, biggest);

            kam = new BufferedImage((w.getValue() - e.getValue()), (s.getValue() - n.getValue()), BufferedImage.TYPE_INT_RGB);
            kam.getGraphics().setColor(Color.WHITE);
            kam.getGraphics().fillRect(0, 0, kam.getWidth() - 1, kam.getHeight() - 1);
            hposun = -n.getValue();
            vposun = -e.getValue();
            for (int x = 0; x < im.size(); x++) {
                Graphics2D g2d = (Graphics2D) kam.getGraphics();
                g2d.drawImage(im.get(x).getImage(), null, getImage(x).getLeft() + vposun, im.get(x).getTop() + hposun);
                progress.setValue(((x + 1) * 100) / (im.size() * 2));
            }

            if (bluredges) {
                if (blurH) {
                    blurHorizontalEdges(hposun, r, vposun, e, kam, hCorner);
                }
                if (blurV) {
                    blurVerticalEdges(hposun, r, vposun, n, kam, vCorner); //pouze vertical si hraje s rozkama, nebot je dela az druhej (prekresli to to horizontal)
                }
            }
            return kam;
//form1.Canvas.Draw(0,0,kam);
        }

    }

    private void blurVerticalEdges(int hposun, TRect r, int vposun, StupidInteger n, BufferedImage kam, boolean cornerCorrection) {
        int odkud;
        int p1;
        Neighbourhood nghbr;
        int p2;
//kam.canvas.Pen.Color=clred;
//kam.canvas.Pen.Width=3;
        for (int x = 1; x < im.size(); x++) {
            progress.setValue(((x + im.size()) * 100) / (im.size() * 2));


            r.left = Math.round(im.get(x).getLeft()) + vposun;
            r.top = Math.round(im.get(x).getTop()) + hposun;
            r.right = Math.round(r.left + im.get(x).getImage().getWidth());
            r.bottom = Math.round(r.top + im.get(x).getImage().getHeight());

            nghbr = getHorizontalNeighbours(x);
            Neighbourhood nghbrV = getVerticalNeighbours(x);
            for (Integer i : nghbr.getAllX()) {
                nghbr.x = i.intValue();


                if ((nghbr.x < x) && (nghbr.x > -1)) {

//kam.canvas.moveto(r.left,r.top);
//kam.canvas.lineto(r.left,r.bottom);
//rozmaz alfou(?) o sirce n
                    odkud = getLeftOdkud(x, nghbr);
                    int odkudU = getUpperOdkud(x, nghbrV);
                    int odkudL = getLowerOdkud(x, nghbrV);
                    AnalytycLine alU = null;
                    if (odkud > -1 && odkudU > -1) {
                        alU = new AnalytycLine(0, 0, odkud, odkudU);
                    }
                    AnalytycLine alL = null;
                    if (odkud > -1 && odkudL > -1) {
                        alL = new AnalytycLine(0, im.get(x).getHeight(), odkud, im.get(x).getHeight() - odkudL);
                    }

                    for (int y = 0; y < kam.getHeight(); y++) {


                        for (int xx = 0; xx < odkud; xx++) {
                            try {

                                int pixel1x = xx;
                                if (pixel1x < 0 || pixel1x >= im.get(x).getImage().getWidth()) {
                                    continue;
                                }
                                int pixel1y = n.getValue() - im.get(x).getTop() + y;
                                if (pixel1y < 0 || pixel1y >= im.get(x).getImage().getHeight()) {
                                    continue;
                                }
                                int pixel2x = im.get(nghbr.x).getImage().getWidth() - Math.abs(im.get(nghbr.x).getLeft() - im.get(x).getLeft() + im.get(nghbr.x).getImage().getWidth()) + xx;
                                if (pixel2x < 0 || pixel2x >= im.get(nghbr.x).getImage().getWidth()) {
                                    continue;
                                }
                                int pixel2y = n.getValue() - im.get(nghbr.x).getTop() + y;
                                if (pixel2y < 0 || pixel2y >= im.get(nghbr.x).getImage().getHeight()) {
                                    continue;
                                }
                                p1 = im.get(x).getImage().getRGB(pixel1x, pixel1y);
                                p2 = im.get(nghbr.x).getImage().getRGB(pixel2x, pixel2y);
//if ( p1<0 ) p1=clwhite;
//if ( p2<0 ) p2=clwhite;
//if ( (p1<0) continue;
//if ( (p2<0) continue;
                                //left edge of upper
                                if (isPixelVisible(Math.max(nghbr.x, x) + 1, pixel1x + im.get(x).getLeft(), pixel1y + im.get(x).getTop())) {
                                    if (cornerCorrection && i.equals(nghbr.getAllX().get(0))) {
                                        boolean kresli = false;
                                        if (alU != null && alL != null && alU.isInHalfPlane1(pixel1x, pixel1y) && alL.isInHalfPlane2(pixel1x, pixel1y)) {
                                            kresli = true;
                                        }
                                        if (alU != null && alL == null && alU.isInHalfPlane1(pixel1x, pixel1y)) {
                                            kresli = true;
                                        }
                                        if (alU == null && alL != null && alL.isInHalfPlane2(pixel1x, pixel1y)) {
                                            kresli = true;
                                        }
                                        if (alU == null && alL == null) {
                                            kresli = true;
                                        }
                                        if (kresli) {
                                            kam.setRGB(im.get(x).getLeft() + vposun + xx, y, alphuj(p2, p1, xx, odkud));
                                        }
                                        //kam.setRGB(im.get(x).getLeft() + vposun + xx, y, Color.red.getRGB());
                                    } else {
                                        kam.setRGB(im.get(x).getLeft() + vposun + xx, y, alphuj(p2, p1, xx, odkud));
                                    }
                                }
                                //kam.setRGB(im.get(x).getLeft() + vposun + xx, y, Color.red.getRGB());
                            } catch (Exception ex) {
                                System.out.println("necessary pixel read/write exception " + x + ":" + y);
                            }
                        }
                    }
                }
            }

            for (Integer i : nghbr.getAllY()) {
                nghbr.y = i.intValue();

                if ((nghbr.y < x) && (nghbr.y > -1)) {
//kam.canvas.moveto(r.Right,r.top);
//kam.canvas.lineto(r.right,r.bottom);
//rozmaz...
                    odkud = getRightOdkud(x, nghbr);
                    int odkudU = getUpperOdkud(x, nghbrV);
                    int odkudL = getLowerOdkud(x, nghbrV);
                    AnalytycLine alU = null;
                    if (odkud > -1 && odkudU > -1) {
                        alU = new AnalytycLine(im.get(x).getWidth(), 0, im.get(x).getWidth() - odkud, odkudU);
                    }
                    AnalytycLine alL = null;
                    if (odkud > -1 && odkudL > -1) {
                        alL = new AnalytycLine(im.get(x).getWidth(), im.get(x).getHeight(), im.get(x).getWidth() - odkud, im.get(x).getHeight() - odkudL);
                    }

                    for (int y = 0; y < kam.getHeight(); y++) {


                        for (int xx = -odkud; xx <= -1; xx++) {
                            try {
                                int pixel1x = im.get(x).getImage().getWidth() + xx;
                                if (pixel1x < 0 || pixel1x >= im.get(x).getImage().getWidth()) {
                                    continue;
                                }
                                int pixel1y = n.getValue() - im.get(x).getTop() + y;
                                if (pixel1y < 0 || pixel1y >= im.get(x).getImage().getHeight()) {
                                    continue;
                                }
                                int pixel2x = Math.abs(im.get(nghbr.y).getLeft() - im.get(x).getLeft() - im.get(x).getImage().getWidth()) + xx;
                                if (pixel2x < 0 || pixel2x >= im.get(nghbr.y).getImage().getWidth()) {
                                    continue;
                                }
                                int pixel2y = n.getValue() - im.get(nghbr.y).getTop() + y;
                                if (pixel2y < 0 || pixel2y >= im.get(nghbr.y).getImage().getHeight()) {
                                    continue;
                                }
                                p1 = im.get(x).getImage().getRGB(pixel1x, pixel1y);
                                p2 = im.get(nghbr.y).getImage().getRGB(pixel2x, pixel2y);
//if ( p1<0 ) p1=clwhite;
//if ( p2<0 ) p2=clwhite;
                                if (isPixelVisible(Math.max(nghbr.y, x) + 1, pixel1x + im.get(x).getLeft(), pixel1y + im.get(x).getTop())) {
                                    if (cornerCorrection && i.equals(nghbr.getAllY().get(0))) {
                                        boolean kresli = false;
                                        if (alU != null && alL != null && alU.isInHalfPlane1(pixel1x, pixel1y) && alL.isInHalfPlane2(pixel1x, pixel1y)) {
                                            kresli = true;
                                        }
                                        if (alU != null && alL == null && alU.isInHalfPlane1(pixel1x, pixel1y)) {
                                            kresli = true;
                                        }
                                        if (alU == null && alL != null && alL.isInHalfPlane2(pixel1x, pixel1y)) {
                                            kresli = true;
                                        }
                                        if (alU == null && alL == null) {
                                            kresli = true;
                                        }
                                        if (kresli) {
                                            kam.setRGB(im.get(x).getLeft() + im.get(x).getImage().getWidth() + vposun + xx, y, alphuj(p2, p1, xx, odkud));
                                        }
                                        //  kam.setRGB(im.get(x).getLeft() + im.get(x).getImage().getWidth() + vposun + xx, y, Color.red.getRGB()   );
                                    } else {
                                        kam.setRGB(im.get(x).getLeft() + im.get(x).getImage().getWidth() + vposun + xx, y, alphuj(p2, p1, xx, odkud));
                                    }
                                }
                                //    kam.setRGB(im.get(x).getLeft() + im.get(x).getImage().getWidth() + vposun + xx, y, Color.red.getRGB()   );
                            } catch (Exception ex) {
                                System.out.println("necessary pixel read/write exception " + x + ":" + y);
                            }
                        }
                    }
                }
            }
        }
    }

    private void blurHorizontalEdges(int hposun, TRect r, int vposun, StupidInteger n, BufferedImage kam, boolean cornerCorrection) {
        int odkud;
        int p1;
        Neighbourhood nghbr;
        int p2;
//kam.canvas.Pen.Color=clred;
//kam.canvas.Pen.Width=3;
        for (int x = 1; x < im.size(); x++) {
            progress.setValue(((x + im.size()) * 100) / (im.size() * 2));
            nghbr = getVerticalNeighbours(x);
            Neighbourhood nghbrH = getHorizontalNeighbours(x);

            r.left = Math.round(im.get(x).getLeft()) + vposun;
            r.top = Math.round(im.get(x).getTop()) + hposun;
            r.right = Math.round(r.left + im.get(x).getImage().getWidth());
            r.bottom = Math.round(r.top + im.get(x).getImage().getHeight());


            for (Integer i : nghbr.getAllY()) {
                nghbr.y = i.intValue();

                if ((nghbr.y < x) && (nghbr.y > -1)) { //horni soused
//g2d.drawLine(r.left,r.top,r.left,r.bottom);
//rozmaz alfou(?) o sirce n
                    odkud = getUpperOdkud(x, nghbr);
                    int odkudLE = getLeftOdkud(x, nghbrH);
                    int odkudRI = getRightOdkud(x, nghbrH);
                    AnalytycLine alLE = null;
                    if (odkud > -1 && odkudLE > -1) {
                        alLE = new AnalytycLine(0, 0, odkud, odkudLE);
                    }
                    AnalytycLine alRI = null;
                    if (odkud > -1 && odkudRI > -1) {
                        alRI = new AnalytycLine(im.get(x).getWidth(), 0, im.get(x).getWidth() - odkud, odkudRI);
                    }

                    for (int y = 0; y < kam.getWidth(); y++) {


                        for (int xx = 0; xx < odkud; xx++) {
                            try {
                                int pixel1y = xx;
                                if (pixel1y < 0 || pixel1y >= im.get(x).getImage().getHeight()) {
                                    continue;
                                }
                                int pixel1x = n.getValue() - im.get(x).getLeft() + y;
                                if (pixel1x < 0 || pixel1x >= im.get(x).getImage().getWidth()) {
                                    continue;
                                }
                                int pixel2y = im.get(nghbr.y).getImage().getHeight() - Math.abs(im.get(nghbr.y).getTop() - im.get(x).getTop() + im.get(nghbr.y).getImage().getHeight()) + xx;
                                if (pixel2y < 0 || pixel2y >= im.get(nghbr.y).getImage().getHeight()) {
                                    continue;
                                }
                                int pixel2x = n.getValue() - im.get(nghbr.y).getLeft() + y;
                                if (pixel2x < 0 || pixel2x >= im.get(nghbr.y).getImage().getWidth()) {
                                    continue;
                                }
                                p1 = im.get(x).getImage().getRGB(pixel1x, pixel1y);
                                p2 = im.get(nghbr.y).getImage().getRGB(pixel2x, pixel2y);
//if ( p1<0 ) p1=clwhite;
//if ( p2<0 ) p2=clwhite;
//if ( (p1<0) continue;
//if ( (p2<0) continue;
                                //upper of upper image
                                if (isPixelVisible(Math.max(nghbr.y, x) + 1, pixel1x + im.get(x).getLeft(), pixel1y + im.get(x).getTop())) {
                                    if (cornerCorrection && i.equals(nghbr.getAllY().get(0))) {
                                        boolean kresli = false;
                                        if (alLE != null && alRI != null && alLE.isInHalfPlane2(pixel1x, pixel1y) && alRI.isInHalfPlane2(pixel1x, pixel1y)) {
                                            kresli = true;
                                        }
                                        if (alLE != null && alRI == null && alLE.isInHalfPlane2(pixel1x, pixel1y)) {
                                            kresli = true;
                                        }
                                        if (alLE == null && alRI != null && alRI.isInHalfPlane2(pixel1x, pixel1y)) {
                                            kresli = true;
                                        }
                                        if (alLE == null && alRI == null) {
                                            kresli = true;
                                        }
                                        if (kresli) {
                                            kam.setRGB(y, im.get(x).getTop() + hposun + xx, alphuj(p2, p1, xx, odkud));
                                        }
                                        // kam.setRGB(y,im.get(x).getTop() + hposun + xx,Color.red.getRGB());
                                    } else {
                                        kam.setRGB(y, im.get(x).getTop() + hposun + xx, alphuj(p2, p1, xx, odkud));
                                    }
                                }
                                //kam.setRGB(y,im.get(x).getTop() + hposun + xx,Color.red.getRGB());
                            } catch (Exception ex) {
                                System.out.println("necessary pixel read/write exception " + x + ":" + y);
                            }
                        }
                    }
                }
            }

            for (Integer i : nghbr.getAllX()) {
                nghbr.x = i.intValue();
                if ((nghbr.x < x) && (nghbr.x > -1)) { //spodni soused
//g2d.drawLine(r.left,r.bottom,r.right,r.bottom);
//rozmaz...

                    odkud = getLowerOdkud(x, nghbr);
                    int odkudLE = getLeftOdkud(x, nghbrH);
                    int odkudRI = getRightOdkud(x, nghbrH);
                    AnalytycLine alLE = null;
                    if (odkud > -1 && odkudLE > -1) {
                        alLE = new AnalytycLine(0, im.get(x).getHeight(), odkud, im.get(x).getHeight() - odkudLE);
                    }
                    AnalytycLine alRI = null;
                    if (odkud > -1 && odkudRI > -1) {
                        alRI = new AnalytycLine(im.get(x).getWidth(), im.get(x).getHeight(), im.get(x).getWidth() - odkud, im.get(x).getHeight() - odkudRI);
                    }


                    for (int y = 0; y < kam.getWidth(); y++) {

                        for (int xx = -odkud; xx <= -1; xx++) {
                            try {
                                int pixel1y = im.get(x).getImage().getHeight() + xx;
                                if (pixel1y < 0 || pixel1y >= im.get(x).getImage().getHeight()) {
                                    continue;
                                }
                                int pixel1x = n.getValue() - im.get(x).getLeft() + y;
                                if (pixel1x < 0 || pixel1x >= im.get(x).getImage().getWidth()) {
                                    continue;
                                }
                                int pixel2y = Math.abs(im.get(nghbr.x).getTop() - im.get(x).getTop() - im.get(x).getImage().getHeight()) + xx;
                                if (pixel2y < 0 || pixel2y >= im.get(nghbr.x).getImage().getHeight()) {
                                    continue;
                                }
                                int pixel2x = n.getValue() - im.get(nghbr.x).getLeft() + y;
                                if (pixel2x < 0 || pixel2x >= im.get(nghbr.x).getImage().getWidth()) {
                                    continue;
                                }
                                p1 = im.get(x).getImage().getRGB(pixel1x, pixel1y);
                                p2 = im.get(nghbr.x).getImage().getRGB(pixel2x, pixel2y);
//if ( p1<0 ) p1=clwhite;
//if ( p2<0 ) p2=clwhite;

                                if (isPixelVisible(Math.max(nghbr.x, x) + 1, pixel1x + im.get(x).getLeft(), pixel1y + im.get(x).getTop())) {
                                    if (cornerCorrection && i.equals(nghbr.getAllX().get(0))) {
                                        boolean kresli = false;
                                        if (alLE != null && alRI != null && alLE.isInHalfPlane1(pixel1x, pixel1y) && alRI.isInHalfPlane1(pixel1x, pixel1y)) {
                                            kresli = true;
                                        }
                                        if (alLE != null && alRI == null && alLE.isInHalfPlane1(pixel1x, pixel1y)) {
                                            kresli = true;
                                        }
                                        if (alLE == null && alRI != null && alRI.isInHalfPlane1(pixel1x, pixel1y)) {
                                            kresli = true;
                                        }
                                        if (alLE == null && alRI == null) {
                                            kresli = true;
                                        }
                                        if (kresli) {
                                            kam.setRGB(y, im.get(x).getTop() + im.get(x).getImage().getHeight() + hposun + xx, alphuj(p2, p1, xx, odkud));
                                        }
                                        //  kam.setRGB(y,im.get(x).getTop() + im.get(x).getImage().getHeight() + hposun + xx,Color.red.getRGB()      );
                                    } else {
                                        kam.setRGB(y, im.get(x).getTop() + im.get(x).getImage().getHeight() + hposun + xx, alphuj(p2, p1, xx, odkud));
                                    }
                                }
                                //    kam.setRGB(y,im.get(x).getTop() + im.get(x).getImage().getHeight() + hposun + xx,Color.red.getRGB()      );
                            } catch (Exception ex) {
                                System.out.println("necessary pixel read/write exception " + x + ":" + y);
                            }
                        }
                    }
                }
            }
        }
    }

    private PanoramatImage getLeftestIamge() {
        PanoramatImage p = null;
        int rekord = 999999999;
        for (PanoramatImage panoramatImage : im) {
            if (panoramatImage.getLeft() < rekord) {
                p = panoramatImage;
                rekord = p.getLeft();
            }
        }

        return p;
    }

    private PanoramatImage getTopestIamge() {
        PanoramatImage p = null;
        int rekord = 999999999;
        for (PanoramatImage panoramatImage : im) {
            if (panoramatImage.getTop() < rekord) {
                p = panoramatImage;
                rekord = p.getTop();
            }
        }

        return p;
    }

    private boolean isPixelVisible(int start, int x, int y) {
        //return false;
        for (int i = start; i < im.size(); i++) {
            if (im.get(i).containsGlobalPixel(x, y)) {
                return false;
            }
        }
        return true;
    }

    private int getLeftOdkud(int x, Neighbourhood nghbr) {
        try {
            int odkud = im.get(x).getLeftAlpha();
            if (Math.abs(im.get(x).getLeft() - (im.get(nghbr.x).getLeft() + im.get(nghbr.x).getImage().getWidth())) < odkud) {
                odkud = Math.abs(im.get(x).getLeft() - (im.get(nghbr.x).getLeft() + im.get(nghbr.x).getImage().getWidth()));
            }

            return odkud;
        } catch (Exception ex) {
            return -1;
        }
    }

    private int getLowerOdkud(int x, Neighbourhood nghbr) {
        try {
            int odkud = im.get(x).getBottomAlpha();
            if (Math.abs(im.get(x).getTop() + im.get(x).getImage().getHeight() - (im.get(nghbr.x).getTop())) < odkud) {
                odkud = Math.abs(im.get(x).getTop() + im.get(x).getImage().getHeight() - (im.get(nghbr.x).getTop()));
            }
            return odkud;
        } catch (Exception ex) {
            return -1;
        }
    }

    private int getRightOdkud(int x, Neighbourhood nghbr) {
        try {
            int odkud = im.get(x).getRightAlpha();
            if (Math.abs(im.get(x).getLeft() + im.get(x).getImage().getWidth() - (im.get(nghbr.y).getLeft())) < odkud) {
                odkud = Math.abs(im.get(x).getLeft() + im.get(x).getImage().getWidth() - (im.get(nghbr.y).getLeft()));
            }
            return odkud;
        } catch (Exception ex) {
            return -1;
        }
    }

    private int getUpperOdkud(int x, Neighbourhood nghbr) {
        try {
            int odkud = im.get(x).getTopAlpha();
            if (Math.abs(im.get(x).getTop() - (im.get(nghbr.y).getTop() + im.get(nghbr.y).getImage().getHeight())) < odkud) {
                odkud = Math.abs(im.get(x).getTop() - (im.get(nghbr.y).getTop() + im.get(nghbr.y).getImage().getHeight()));
            }
            return odkud;
        } catch (Exception ex) {
            return -1;
        }
    }

    private void getPosun(StupidInteger e, StupidInteger w, StupidInteger n, StupidInteger s, boolean biggest) {
        RealCoords rc = getRealCoords(biggest);
        e.setValue(rc.e);
        w.setValue(rc.w);
        n.setValue(rc.n);
        s.setValue(rc.s);
    }

    private int alphuj(int p2i, int p1i, int pozice, int zkolika) {
        int r1, r2, g1, g2, b1, b2;
        int r, b, g;
        double o;

        //*

        Color p1 = new Color(p2i);
        Color p2 = new Color(p1i);
        r1 = p1.getRed();
        g1 = p1.getGreen();
        b1 = p1.getBlue();
        r2 = p2.getRed();
        g2 = p2.getGreen();
        b2 = p2.getBlue();
        o = Math.abs((double) pozice / (double) zkolika);
        r = (int) Math.round((1d - o) * (double) r1 + o * (double) r2);
        g = (int) Math.round((1d - o) * (double) g1 + o * (double) g2);
        b = (int) Math.round((1d - o) * (double) b1 + o * (double) b2);
        return new Color(r, g, b).getRGB();


    }

    public void brightujSelected(boolean affined) {
        brightuj(getTheOneIndex(), getTheSelected().getLeftBrightnes(), getTheSelected().getRightBrightnes(), getTheSelected().getTopBrightnes(), getTheSelected().getBotomBrightnes(), affined);
    }

    public void brightuj(int index, int ss1, int ss2, int topCorrection, int bottomCorrection) {
        brightuj(index, ss1, ss2, topCorrection, bottomCorrection, true);
    }

    public void brightuj(int index, int ss1, int ss2, int topCorrection, int bottomCorrection, boolean affined) {
        //PanoramatImage img;

        im.get(index).reload();
        if (affined) {
            afineIndexed(index);
        }


        MyRGBFilter a = new MyRGBFilter();


        a.setS1(ss1);
        a.setS2(ss2);
        a.setBottomC(bottomCorrection);
        a.setTopC(topCorrection);
        a.setImageHeight(im.get(index).getHeight());
        a.setImwid(im.get(index).getWidth());


        Image img = im.get(index).getImage();
        FilteredImageSource filteredSrc = new FilteredImageSource(img.getSource(), a);
        img = createImage(filteredSrc);
        im.get(index).setImage(img);





    }


  
    static int pricti(int co, int kcemu) {
        int vysledek = co + kcemu;
        if (vysledek > 255) {
            vysledek = 255;
        }
        if (vysledek < 0) {
            vysledek = 0;
        }
        return vysledek;
    }

    static int divers(int hodnotaod, int hodnotakam, int rozsah, int pozice) {
        return hodnotaod + (int) Math.round(((double) pozice / (double) rozsah) * ((double) hodnotakam - (double) hodnotaod));
    }

    void moveSelectedDown() {
       
        PanoramatImage p = im.get(getTheOneIndex() - 1);
        im.set(getTheOneIndex() - 1, im.get(getTheOneIndex()));
        im.set(getTheOneIndex(), p);
    }

    void moveSelectedUp() {
       
        PanoramatImage p = im.get(getTheOneIndex() + 1);
        im.set(getTheOneIndex() + 1, im.get(getTheOneIndex()));
        im.set(getTheOneIndex(), p);
    }

    void removeImage(int i) {
        im.remove(i);
        actualiseQuickList();
    }

    private void swap(StupidInteger a, StupidInteger b) {
        int temp;
        temp = a.getValue();
        a.setValue(b.getValue());
        b.setValue(temp);
    }

    public void previewAlpha(int index, Graphics2D g2d) {
        int q, x, y, xx, nn, n, s, e, w;
        Neighbourhood nghbrs;
        double z;
        int vposun, hposun;
        StupidInteger[] poradi = new StupidInteger[4];
        int oldindex, p1, p2, odkud;
        BufferedImage[] previewed = new BufferedImage[4];
        TPoint[] lefttop = new TPoint[4];
        BufferedImage alphapreview;

        {
            z = zoom;
            oldindex = index;
            try {
                nghbrs = getHorizontalNeighbours(index);
                if ((nghbrs.x < 0) && (nghbrs.y > -1)) {
                    index = nghbrs.y;
                    nghbrs = getHorizontalNeighbours(index);
                } else if ((nghbrs.y < 0) && (nghbrs.x > -1)) {
                    index = nghbrs.x;
                    nghbrs = getHorizontalNeighbours(index);
                } else if ((nghbrs.y < 0) && (nghbrs.x < 0)) {
                    System.out.println("no edges!");
                }//if ( ( (nghbrs.y<0)&&(nghbrs.x<0)) { showmessage('no edges');exit;}

                poradi[1] = new StupidInteger(index);
                poradi[2] = new StupidInteger(nghbrs.x);
                poradi[3] = new StupidInteger(nghbrs.y);
                w = 99999999;
                e = -99999999;
                for (x = 1; x <= 3; x++) {
                    if (poradi[x].getValue() > -1) {
                        if (im.get(poradi[x].getValue()).getLeft() < w) {
                            w = im.get(poradi[x].getValue()).getLeft();
                        }
                        if (im.get(poradi[x].getValue()).getLeft() + im.get(poradi[x].getValue()).getWidth() > e) {
                            e = im.get(poradi[x].getValue()).getLeft() + im.get(poradi[x].getValue()).getWidth();
                        }
                    }
                }
                int alphapreviewWidth = (int) Math.round(z * (double) (e - w));
                vposun = -(int) Math.round(z * (double) w);
                n = 99999999;
                s = -99999999;
                for (x = 1; x <= 3; x++) {
                    if (poradi[x].getValue() > -1) {
                        if (im.get(poradi[x].getValue()).getTop() < n) {
                            n = im.get(poradi[x].getValue()).getTop();
                        }
                        if (im.get(poradi[x].getValue()).getTop() + im.get(poradi[x].getValue()).getHeight() > s) {
                            s = im.get(poradi[x].getValue()).getTop() + im.get(poradi[x].getValue()).getHeight();
                        }
                    }
                }

                /*alphapreview.Width()=round(z*((im.get(nghbrs.y).getLeft()+im.get(nghbrs.y).getWidth())-im.get(nghbrs.x).getLeft()));
                vposun=-round(im.get(nghbrs.x).getLeft()*z);
                if ( ( im.get(index).getTop()<im.get(nghbrs.x).getTop() ) n=im.get(index).getTop() ;else n=im.get(nghbrs.x).getTop();
                if ( ( n>im.get(nghbrs.y).getTop() ) n=im.get(nghbrs.y).getTop();
                if ( ( im.get(index).getTop()+im.get(index).getHeight()>im.get(nghbrs.x).getTop()+im.get(nghbrs.x).getHeight() ) s=im.get(index).getTop()+im.get(index).getHeight() ;else s=im.get(nghbrs.x).getTop()+im.get(nghbrs.x).getHeight();
                if ( ( s<im.get(nghbrs.y).getTop()+im.get(nghbrs.y).getHeight() ) s=im.get(nghbrs.y).getTop()+im.get(nghbrs.y).getHeight();*/

                hposun = -(int) Math.round((double) n * z);
                int alphapreviewHeight = (int) Math.round(z * (double) (s - n));
                alphapreview = new BufferedImage(alphapreviewWidth, alphapreviewHeight, BufferedImage.TYPE_INT_RGB);
                nn = n;
                n = (int) Math.round((double) n * z);
                s = (int) Math.round((double) s * z);

//mazani alphaprewiew:DD
                alphapreview.getGraphics().setColor(Color.WHITE);
                alphapreview.getGraphics().fillRect(0, 0, alphapreview.getWidth(), alphapreview.getHeight());

                for (x = 1; x <= 4; x++) {
                    if (poradi[1].getValue() > poradi[2].getValue()) {
                        swap(poradi[1], poradi[2]);
                    }
                    if (poradi[2].getValue() > poradi[3].getValue()) {
                        swap(poradi[3], poradi[2]);
                    }
                    if (poradi[1].getValue() > poradi[3].getValue()) {
                        swap(poradi[1], poradi[3]);
                    }
                }
                for (x = 1; x <= 3; x++) {
                    if (poradi[x].getValue() > -1) {
                        int previewedWidth = (int) Math.round((double) im.get(poradi[x].getValue()).getWidth() * z);
                        int previewedHeight = (int) Math.round((double) im.get(poradi[x].getValue()).getHeight() * z);
                        int previewedLeft = (int) Math.round((double) im.get(poradi[x].getValue()).getLeft() * z);
                        int previewedTop = (int) Math.round((double) im.get(poradi[x].getValue()).getTop() * z);
                        previewed[x] = new BufferedImage(previewedWidth, previewedHeight, BufferedImage.TYPE_INT_RGB);
                        lefttop[x] = new TPoint(previewedLeft, previewedTop);
                        ((Graphics2D) previewed[x].getGraphics()).drawImage(
                                im.get(poradi[x].getValue()).getImage(),
                                0, 0, (int) Math.round((double) im.get(poradi[x].getValue()).getWidth() * z), (int) Math.round((double) im.get(poradi[x].getValue()).getHeight() * z),
                                null);
//canvas.draw(200*x,0,previewed[x).getpicture.graphic);
                    }
                }



                for (x = 1; x <= 3; x++) {
                    if (previewed[x] != null) {
                        ((Graphics2D) alphapreview.getGraphics()).drawImage(
                                previewed[x], lefttop[x].x + vposun, lefttop[x].y + hposun, null);
                    }
                }

                for (x = 1; x <= 3; x++) {
                    if (poradi[x].getValue() == -1) {
                        continue;
                    }
                    nghbrs = getHorizontalNeighbours(poradi[x].getValue());


                    if ((nghbrs.x < poradi[x].getValue()) && (nghbrs.x > -1) && ((nghbrs.x == poradi[1].getValue())
                            || (nghbrs.x == poradi[2].getValue())
                            || (nghbrs.x == poradi[3].getValue()))) {
                        q = -1;
                        for (y = 1; y <= 3; y++) {
                            if (poradi[y].getValue() == nghbrs.x) {
                                q = y;
                            }
                        }
                        for (y = 0; y < alphapreview.getHeight(); y++) {
                            odkud = (int) Math.round((double) im.get(poradi[x].getValue()).getLeftAlpha() * z);
                            if (Math.abs(lefttop[x].getLeft() - (lefttop[q].getLeft() + previewed[q].getWidth())) < odkud) {
                                odkud = Math.abs(lefttop[x].getLeft() - (lefttop[q].getLeft() + previewed[q].getWidth()));
                            }
                            for (xx = 0; xx < odkud; xx++) {
                                int xxx = xx;
                                int yyy = n - lefttop[x].getTop() + y;
                                if (xxx < 0 || xxx >= previewed[x].getWidth() || yyy < 0 || yyy >= previewed[x].getHeight()) {
                                    continue;
                                }
                                p1 = previewed[x].getRGB(xxx, yyy);
                                xxx = previewed[q].getWidth() - Math.abs(lefttop[q].getLeft() - lefttop[x].getLeft() + previewed[q].getWidth()) + xx;
                                yyy = n - lefttop[q].getTop() + y;
                                if (xxx < 0 || xxx >= previewed[q].getWidth() || yyy < 0 || yyy >= previewed[q].getHeight()) {
                                    continue;
                                }
                                p2 = previewed[q].getRGB(xxx, yyy);
//if ( ( p1<0 ) p1=clwhite;
//if ( ( p2<0 ) p2=clwhite;
//if ( ( p1<0 ) continue;
//if ( ( p2<0 ) continue;
                                alphapreview.setRGB(lefttop[x].getLeft() + vposun + xx, y, alphuj(p2, p1, xx, odkud));
                            }
                        }
                    }

                    if ((nghbrs.y < poradi[x].getValue()) && (nghbrs.y > -1) && ((nghbrs.y == poradi[1].getValue())
                            || (nghbrs.y == poradi[2].getValue())
                            || (nghbrs.y == poradi[3].getValue()))) {
//kam.canvas.moveto(r.Right,r.Top());
//kam.canvas.lineto(r.right,r.bottom);
//rozmaz...
                        q = -1;
                        for (y = 1; y <= 3; y++) {
                            if (poradi[y].getValue() == nghbrs.y) {
                                q = y;
                            }
                        }
                        for (y = 0; y < alphapreview.getHeight(); y++) {
                            odkud = (int) Math.round((double) im.get(poradi[x].getValue()).getRightAlpha() * z);
                            if (Math.abs(lefttop[x].getLeft() + previewed[x].getWidth() - (lefttop[q].getLeft())) < odkud) {
                                odkud = Math.abs(lefttop[x].getLeft() + previewed[x].getWidth() - (lefttop[q].getLeft()));
                            }
                            for (xx = -odkud; xx <= -1; xx++) {
                                int xxx = previewed[x].getWidth() + xx;
                                int yyy = n - lefttop[x].getTop() + y;
                                if (xxx < 0 || xxx >= previewed[x].getWidth() || yyy < 0 || yyy >= previewed[x].getHeight()) {
                                    continue;
                                }
                                p1 = previewed[x].getRGB(xxx, yyy);
                                xxx = Math.abs(lefttop[q].getLeft() - lefttop[x].getLeft() - previewed[x].getWidth()) + xx;
                                yyy = n - lefttop[q].getTop() + y;
                                if (xxx < 0 || xxx >= previewed[q].getWidth() || yyy < 0 || yyy >= previewed[q].getHeight()) {
                                    continue;
                                }
                                p2 = previewed[q].getRGB(xxx, yyy);
//if ( ( p1<0 ) p1=clwhite;
//if ( ( p2<0 ) p2=clwhite;
//if ( ( p1<0 ) continue;
//if ( ( p2<0 ) continue;
                                alphapreview.setRGB(lefttop[x].getLeft() + previewed[x].getWidth() + vposun + xx, y, alphuj(p2, p1, xx, odkud));
                            }
                        }
                    }

                }


                nghbrs = getHorizontalNeighbours(index);
                g2d.drawImage(alphapreview,
                        (int) Math.round(((double) w * z)) + posun.x,
                        (int) Math.round((double) nn * z) + posun.y,
                        null);
            } finally {
                index = oldindex;
            }
        }



    }

    void saveToFile(File file) throws IOException {
        BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
        f.write("v5");
        f.newLine();
        f.write(String.valueOf(zoom));
        f.newLine();
        f.write(String.valueOf(posun.x));
        f.newLine();
        f.write(String.valueOf(posun.y));
        f.newLine();
        f.write(String.valueOf(im.size()));
        f.newLine();

        for (Iterator it = im.iterator(); it.hasNext();) {
            PanoramatImage elem = (PanoramatImage) it.next();
            elem.write(f);

        }

        f.close();
    }

    void loadFromFile(File file) throws FileNotFoundException, IOException {
        BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
        String s = f.readLine();
        if (s.equalsIgnoreCase("v4")) {
            loadVersion4(f, s);
        }
        if (s.equalsIgnoreCase("v5")) {
            loadVersion5(f, s);
        } else {
            loadOlder(f, s);
        }



        f.close();
    }

    private void loadVersion5(BufferedReader f, String s) throws IOException {
        s = f.readLine();
        zoom = Double.valueOf(s);
        s = f.readLine();
        posun.x = Integer.valueOf(s);
        s = f.readLine();
        posun.y = Integer.valueOf(s);
        s = f.readLine();
        int ims = Integer.valueOf(s);
        im = new ArrayList();
        PanoramatImage p = PanoramatImageFactory.createFromStream5(f);
        int x = 0;
        while (p != null) {
            x++;
            if (p.getSrc() != null) {
                progress.setValue(((x * 100) / ims));
                setImage(p, 999999);
                resetIndex(im.size() - 1);
                if (!(p.getRightBrightnes() == 0 && p.getLeftBrightnes() == 0)) {
                    brightuj(im.size() - 1, p.getLeftBrightnes(), p.getRightBrightnes(), p.getTopBrightnes(), p.getBotomBrightnes());
                } else {
                    afineIndexed(im.size() - 1);
                }
            }
            p = PanoramatImageFactory.createFromStream5(f);
        }
    }

    private void loadVersion4(BufferedReader f, String s) throws IOException {
        s = f.readLine();
        zoom = Double.valueOf(s);
        s = f.readLine();
        posun.x = Integer.valueOf(s);
        s = f.readLine();
        posun.y = Integer.valueOf(s);
        s = f.readLine();
        int ims = Integer.valueOf(s);
        im = new ArrayList();
        PanoramatImage p = PanoramatImageFactory.createFromStream4(f);
        int x = 0;
        while (p != null) {
            x++;
            if (p.getSrc() != null) {
                progress.setValue(((x * 100) / ims));
                setImage(p, 999999);
                resetIndex(im.size() - 1);
                if (!(p.getRightBrightnes() == 0 && p.getLeftBrightnes() == 0)) {
                    brightuj(im.size() - 1, p.getLeftBrightnes(), p.getRightBrightnes(), p.getTopBrightnes(), p.getBotomBrightnes());
                } else {
                    afineIndexed(im.size() - 1);
                }
            }
            p = PanoramatImageFactory.createFromStream4(f);
        }
    }

    private void loadOlder(BufferedReader f, String s) throws IOException {
        zoom = Double.valueOf(s);
        s = f.readLine();
        posun.x = Integer.valueOf(s);
        s = f.readLine();
        posun.y = Integer.valueOf(s);
        s = f.readLine();
        int ims = Integer.valueOf(s);
        im = new ArrayList();
        PanoramatImage p = PanoramatImageFactory.createFromStreamOlder(f);
        int x = 0;
        while (p != null) {
            x++;
            if (p.getSrc() != null) {
                progress.setValue(((x * 100) / ims));
                setImage(p, 999999);
                resetIndex(im.size() - 1);
                if (!(p.getRightBrightnes() == 0 && p.getLeftBrightnes() == 0)) {
                    brightuj(im.size() - 1, p.getLeftBrightnes(), p.getRightBrightnes(), p.getTopBrightnes(), p.getBotomBrightnes());
                }
            }
            p = PanoramatImageFactory.createFromStreamOlder(f);
        }
    }

    public boolean getOrientation(){
        if (forcing == 2){
            return false;
        }
        if (forcing == 1){
            return true;
        }
        Rectangle r = getBiggestSize();
        int w = Math.abs(r.width - r.x);
        int h = Math.abs(r.height - r.y);
        boolean b = (w<h);
        //true "na vysku"
        //false  kalsicke na sirku
        //System.out.println(b);
        return b;
    }
    public Rectangle getBiggestSize(){
       int minX = Integer.MAX_VALUE;
       int maxX = Integer.MIN_VALUE;
       int minY = Integer.MAX_VALUE;
       int maxY = Integer.MIN_VALUE;
     for (int x = 0; x < im.size(); x++) {
                    if (im.get(x).getLeft() < minX) {
                        minX = im.get(x).getLeft();
                    }
                    if (im.get(x).getLeft() + im.get(x).getWidth() > maxX) {
                        maxX = im.get(x).getLeft() + im.get(x).getWidth();
                    }
                    if (im.get(x).getTop() < minY) {
                        minY = im.get(x).getTop();
                    }
                    if (im.get(x).getTop() + im.get(x).getHeight() > maxY) {
                        maxY = im.get(x).getTop() + im.get(x).getHeight();
                    }
                }
       return new Rectangle(minX, minY, maxX, maxY);
   }

    public RealCoords getRealCoords(boolean biggest) {
                boolean b = getOrientation();
     RealCoords rc = new RealCoords();
            if (biggest) {
                rc.w = -9990000;
                rc.e = 99999999;
                rc.s = -9990000;
                rc.n = 99999999;
                rc.ie = -1;
                rc.iw = -1;
                rc.iin = -1;
                rc.iis = -1;
                for (int x = 0; x < im.size(); x++) {
                    if (im.get(x).getLeft() < rc.e) {
                        rc.e = im.get(x).getLeft();
                        rc.ie = x;
                    }
                    if (im.get(x).getLeft() + im.get(x).getWidth() > rc.w) {
                        rc.w = im.get(x).getLeft() + im.get(x).getWidth();
                        rc.iw = x;
                    }
                    if (im.get(x).getTop() < rc.n) {
                        rc.n = im.get(x).getTop();
                        rc.iin = x;
                    }
                    if (im.get(x).getTop() + im.get(x).getHeight() > rc.s) {
                        rc.s = im.get(x).getTop() + im.get(x).getHeight();
                        rc.iis = x;
                    }
                }

                if (rc.ie == -1) {
                    System.out.println("unexpected edge detection (ie=-1)");
                }
                if (rc.iw == -1) {
                    System.out.println("unexpected edge detection (iw=-1)");
                }
                if (rc.iin == -1) {
                    System.out.println("unexpected edge detection (iin=-1)");
                }
                if (rc.iis == -1) {
                    System.out.println("unexpected edge detection (iis=-1)");
                }

            }

            if (!biggest) {
                if (!b) {//klasicke na sirku
                    rc.w = -9990000;
                    rc.e = 99999999;
                    rc.s = 9990000;
                    rc.n = -99999999;
                } else {//na vysku
                    rc.w = 9990000;
                    rc.e = -99999999;
                    rc.s = -9990000;
                    rc.n = 99999999;
                }
                rc.ie = -1;
                rc.iw = -1;
                rc.iin = -1;
                rc.iis = -1;
                for (int x = 0; x < im.size(); x++) {
                    if (!b) {//klasicke na sirku
                         if (im.get(x).getLeft() < rc.e) {
                            rc.e = im.get(x).getLeft();
                            rc.ie = x;
                        }
                        if (im.get(x).getLeft() + im.get(x).getWidth() > rc.w) {
                            rc.w = im.get(x).getLeft() + im.get(x).getWidth();
                            rc.iw = x;
                        }
                        if (im.get(x).getTop() > rc.n) {
                            rc.n = im.get(x).getTop();
                            rc.iin = x;
                        }
                        if (im.get(x).getTop() + im.get(x).getHeight() < rc.s) {
                            rc.s = im.get(x).getTop() + im.get(x).getHeight();
                            rc.iis = x;
                        }
                    }else{//na vyslu
                         if (im.get(x).getLeft() > rc.e) {
                            rc.e = im.get(x).getLeft();
                            rc.ie = x;
                        }
                        if (im.get(x).getLeft() + im.get(x).getWidth() < rc.w) {
                            rc.w = im.get(x).getLeft() + im.get(x).getWidth();
                            rc.iw = x;
                        }
                        if (im.get(x).getTop() < rc.n) {
                            rc.n = im.get(x).getTop();
                            rc.iin = x;
                        }
                        if (im.get(x).getTop() + im.get(x).getHeight() > rc.s) {
                            rc.s = im.get(x).getTop() + im.get(x).getHeight();
                            rc.iis = x;
                        }
                    }
                }

                if (rc.ie == -1) {
                    System.out.println("unexpected edge detection (ie=-1)");
                }
                if (rc.iw == -1) {
                    System.out.println("unexpected edge detection (iw=-1)");
                }
                if (rc.iin == -1) {
                    System.out.println("unexpected edge detection (iin=-1)");
                }
                if (rc.iis == -1) {
                    System.out.println("unexpected edge detection (iis=-1)");
                }

            }
        return rc;
    }
    public TPoint drawRect(Graphics2D g2d, boolean biggest) {
        double z = zoom;
        RealCoords rc = getRealCoords(biggest);
        //draw it
//draw it

            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(3.0f));
            g2d.drawRect(posun.x + (int) Math.round((double) rc.e * z),
                    posun.y + (int) Math.round((double) rc.n * z),
                    (int) Math.round((double) Math.abs(rc.w - rc.e) * z),
                    (int) Math.round((double) Math.abs(rc.n - rc.s) * z));
            g2d.setStroke(new BasicStroke());

            TPoint vysledek = new TPoint((int) Math.round((double) Math.abs(rc.w - rc.e) * z),
                    (int) Math.round((double) Math.abs(rc.n - rc.s) * z));
            return vysledek;



    }

    public void saveDeformationPoints(File file) throws IOException {
        ArrayList<TPoint> p = getDeformationsPoints();
        BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
        for (Iterator it = p.iterator(); it.hasNext();) {
            TPoint elem = (TPoint) it.next();
            f.write(String.valueOf(elem.x));
            f.newLine();
            f.write(String.valueOf(elem.y));
            f.newLine();
            f.write(String.valueOf(elem.z));
            f.newLine();

        }
        f.close();

    }

    public ArrayList getDeformationsPoints() {
        Neighbourhood nghbr;
        int dif, x;
        TRect r = new TRect();
        double z;
        StupidInteger e = new StupidInteger(), w = new StupidInteger(), n = new StupidInteger(), s = new StupidInteger();//nej polohy e a w jsou asi obracene:);
        int hhposun, vvposun, hposun, vposun;
        int nghbrtop;
        ArrayList<TPoint> list;
        TPoint p;
        {
            list = new ArrayList();

            getPosun(e, w, n, s, true);
            hposun = -n.getValue();
            vposun = -e.getValue();
            z = 1;
//z=strtofloat(edit1.text);
            hhposun = posun.x;
            vvposun = posun.y;


            p = new TPoint((int) Math.round((double) im.get(0).getHeight() * z), 0, 0);
            list.add(p);

            for (x = 0; x < im.size(); x++) {
                nghbr = getHorizontalNeighbours(x);

                r.left = (int) Math.round(im.get(x).getLeft() * z) + vposun;
                r.top = (int) Math.round(im.get(x).getTop() * z) + hposun;
                r.right = (int) Math.round(r.left + im.get(x).getWidth() * z);
                r.bottom = (int) Math.round(r.top + im.get(x).getHeight() * z);

                if ((nghbr.x > -1)) {
                    nghbrtop = (int) Math.round(im.get(nghbr.x).getTop() * z) + hposun;
                    dif = nghbrtop - r.top;
                    if ((dif > 0)) {
//neaka!
//canvas.moveto((int)Math.round(r.getLeft()+hhposun),(int)Math.round(r.getTop()+vvposun));
//canvas.lineto((int)Math.round(r.getLeft()+hhposun),(int)Math.round(r.getTop()+vvposun+dif (*z));
                        p = new TPoint();
                        p.x = r.left;
                        p.y = r.top + dif;
                        p.z = dif;
                        list.add(p);
                    } else {
//nefaka
//canvas.moveto((int)Math.round(r.Left()+hhposun),(int)Math.round(vvposun+r.Top()*z+im.get(x).getHeight()*z+dif (*z));
//canvas.lineto((int)Math.round(r.Left()+hhposun),(int)Math.round(vvposun+r.Top()*z+im.get(x).getHeight()*z));
                        p = new TPoint();
                        p.x = r.left;
                        p.y = r.top + (int) Math.round(im.get(x).getHeight() * z) + dif;
                        p.z = dif;
                        list.add(p);
                    }
                    /*new(p);
                    p^.x=r.Left();
                    p^.y=r.Top();
                    list.add(p);
                    new(p);
                    p^.x=dif (;
                    p^.y=im.get(x).getHeight();
                    list.add(p);*/

                }

                if ((nghbr.y > -1)) {
                    nghbrtop = (int) Math.round(im.get(nghbr.y).getTop() * z) + hposun;
                    dif = nghbrtop - r.top;
                    if ((dif > 0)) {
//canvas.moveto(r.Right,r.Top());
//canvas.lineto(r.Right,r.Top()+dif);
                        p = new TPoint();
                        p.x = r.right;
                        p.y = r.top + dif;
                        p.z = dif;
                        list.add(p);
                    } else {
//canvas.moveto(r.Right,r.Top()+(int)Math.round(im.get(x).getHeight()*z)+dif );
//canvas.lineto(r.Right,r.Top()+(int)Math.round(im.get(x).getHeight()*z));
                        p = new TPoint();
                        p.x = r.right;
                        p.y = r.top + (int) Math.round(im.get(x).getHeight() * z) + dif;
                        p.z = dif;
                        list.add(p);
                    }
                    /*new(p);
                    p^.x=r.right;
                    p^.y=r.Top();
                    list.add(p);
                    new(p);
                    p^.x=dif (;
                    p^.y=im.get(x).getHeight();
                    list.add(p);*/
                }
            }
            /*if ( sd1.Execute ) {
            assignfile(f,sd1.FileName);
            rewrite(f);
            for (x=0 ; x< list.size();x++){
            p=list.items[x];
            writeln(f,inttostr(p.x));
            writeln(f,inttostr(p.y));
            writeln(f,inttostr(p.z));
            }
            closefile(f);*/
        }


        return list;
    }

    public void drawEdges(Graphics2D g2d) {
        Neighbourhood nghbr;
        int x;
        TRect r = new TRect();
        double z;
        {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(3.0f));


            z = zoom;

            for (x = 1; x < im.size(); x++) {
                nghbr = getHorizontalNeighbours(x);

                r.left = (int) Math.round((double) im.get(x).getLeft() * z) + posun.x;
                r.top = (int) Math.round((double) im.get(x).getTop() * z) + posun.y;
                r.right = (int) Math.round((double) r.left + (double) im.get(x).getWidth() * z);
                r.bottom = (int) Math.round((double) r.top + (double) im.get(x).getHeight() * z);

                for (Integer X : nghbr.getAllX()) {
                    nghbr.x = X.intValue();
                    if ((nghbr.x < x) && (nghbr.x > -1)) {
                        g2d.drawLine(r.left, r.top, r.left, r.bottom);
                    }
                }
                for (Integer Y : nghbr.getAllY()) {
                    nghbr.y = Y.intValue();
                    if ((nghbr.y < x) && (nghbr.y > -1)) {
                        g2d.drawLine(r.right, r.top, r.right, r.bottom);
                    }
                }
//vodorovny(pridat podminku na kreslit?-casem polmoci radii
                nghbr = getVerticalNeighbours(x);
                r.left = (int) Math.round((double) im.get(x).getLeft() * z) + posun.x;
                r.top = (int) Math.round((double) im.get(x).getTop() * z) + posun.y;
                r.right = (int) Math.round((double) r.left + (double) im.get(x).getWidth() * z);
                r.bottom = (int) Math.round((double) r.top + (double) im.get(x).getHeight() * z);

                for (Integer Y : nghbr.getAllY()) {
                    nghbr.y = Y.intValue();
                    if ((nghbr.y < x) && (nghbr.y > -1)) {
                        g2d.drawLine(r.left, r.top, r.right, r.top);
                    }
                }

                for (Integer X : nghbr.getAllX()) {
                    nghbr.x = X.intValue();
                    if ((nghbr.x < x) && (nghbr.x > -1)) {
                        g2d.drawLine(r.left, r.bottom, r.right, r.bottom);
                    }
                }

// 
            }
        }

        /*g2d.drawRect(posun.x+(int)Math.round((double)e*z),
        posun.y+(int)Math.round((double)n*z),
        (int)Math.round((double)Math.abs(w-e)*z),
        (int)Math.round((double)Math.abs(n-s)*z));*/
        g2d.setStroke(new BasicStroke());

    }

    void setProgress(JProgressBar jProgressBar1) {
        this.progress = jProgressBar1;
    }
    public static final int ONLY_LEFT = 1;
    public static final int ONLY_RIGHT = 2;
    public static final int ONLY_BOTH = 3;
    public static final int METHOD_AVG = 1;
    public static final int METHOD_MEDIAN = 2;
    public static final int METHOD_MOSTOFTEN = 3;

    void setBrightness(int index, boolean onlyOne, boolean overBrigtened, int lrb, int method) {

        if (onlyOne) {
            int oldSR = 0;
            int oldSL = 0;
            int oldLN = 0;
            int oldRN = 0;

            Neighbourhood nghbr = getHorizontalNeighbours(index);

            if (index != -1) {
                oldSR = im.get(index).getRightBrightnes();
            }
            if (index != -1) {
                oldSL = im.get(index).getLeftBrightnes();
            }
            if (nghbr.x != -1) {
                oldLN = im.get(nghbr.x).getLeftBrightnes();
            }
            if (nghbr.y != -1) {
                oldRN = im.get(nghbr.y).getRightBrightnes();
            }

            proceedOneImageBrightness(index, overBrigtened, lrb, method);

            if (oldSR != im.get(index).getRightBrightnes() || oldSL != im.get(index).getLeftBrightnes()) {
                brightuj(index, im.get(index).getLeftBrightnes(), im.get(index).getRightBrightnes(), im.get(index).getTopBrightnes(), im.get(index).getBotomBrightnes());
            }
            if (oldLN != 0 && oldLN != im.get(nghbr.x).getLeftBrightnes()) {
                brightuj(nghbr.x, im.get(nghbr.x).getLeftBrightnes(), im.get(nghbr.x).getRightBrightnes(), im.get(nghbr.x).getTopBrightnes(), im.get(nghbr.x).getBotomBrightnes());
            }
            if (oldRN != 0 && oldRN != im.get(nghbr.y).getRightBrightnes()) {
                brightuj(nghbr.y, im.get(nghbr.y).getLeftBrightnes(), im.get(nghbr.y).getRightBrightnes(), im.get(nghbr.y).getTopBrightnes(), im.get(nghbr.y).getBotomBrightnes());
            }

        } else {
            ArrayList<PanoramatImage> ims = new ArrayList();
            ims.addAll(im);
            for (int x = ims.size() - 1; x >= 0; x--) {
                if (ims.get(x) != null) {
                    proceedOneImageBrightness(x, overBrigtened, lrb, method);
                    ims.set(x, null);
                    Neighbourhood nghbr = getHorizontalNeighbours(x);
                    if (lrb == ONLY_LEFT || lrb == ONLY_BOTH) {
                        if (nghbr.x >= 0) {
                            ims.set(nghbr.x, null);
                        }
                    }
                    if (lrb == ONLY_RIGHT || lrb == ONLY_BOTH) {
                        if (nghbr.y >= 0) {
                            ims.set(nghbr.y, null);
                        }
                    }
                }
            }
            for (int x = 0; x < im.size(); x++) {
                brightuj(x, im.get(x).getLeftBrightnes(), im.get(x).getRightBrightnes(), im.get(x).getTopBrightnes(), im.get(x).getBotomBrightnes());
            }
        }
    }

    private int zpracujVysledek(ArrayList<Integer> list, int method) {
        switch (method) {
            case (METHOD_AVG):
                return avg(list);
            case (METHOD_MEDIAN):
                return median(list);
            case (METHOD_MOSTOFTEN):
                return mostOften(list);
            default:
                return 0;
        }

    }

    private ArrayList<Integer> computeRigtBrigthnesDiff(int index) {
        StupidInteger opravan = new StupidInteger();
        StupidInteger opravas = new StupidInteger();
        StupidInteger max = new StupidInteger();


        int ng = getHorizontalNeighbours(index).y;
        if (ng < 0) {
            return null;//non eighbr
        }
        PanoramatImage selected = im.get(index);
        PanoramatImage nghbr = im.get(ng);
        int selRight = 0;
        int ngLeft = 0;

        setAutoBrightnesCoords(selected, nghbr, max, opravas, opravan);

        if (index < ng) {//pocitany je vys nez soused
            /*
             *|-----|
            selected  |
             *|  |------|
             *|--|      |
             *   |      |
             *   |------|
             */

            ngLeft = 0;
            selRight = -selected.getLeft() + nghbr.getLeft();



        }

        if (index > ng) {//pocitany je vys nez soused
            /*
             *|-----|
            selected  |
             *|     |--|
             *|-----|  |
             *   |     |
             *   |-----|
             */

            ngLeft = nghbr.getWidth() - (-selected.getLeft() + nghbr.getLeft()) - 1;
            selRight = selected.getWidth() - 1;

        }


        ArrayList<Integer> vysledek = new ArrayList();

        for (int y = 0; y < max.getValue(); y++) {
            //nghbr.getImage().setRGB(ngLeft,y+opravan.getValue(),Color.RED.getRGB());
            //selected.getImage().setRGB(selRight,y+opravas.getValue(),Color.RED.getRGB());
            Color nghbrCol = new Color(nghbr.getImage().getRGB(ngLeft, y + opravan.getValue()));
            Color selCol = new Color(selected.getImage().getRGB(selRight, y + opravas.getValue()));

            int nghbrVal = (nghbrCol.getRed() + nghbrCol.getGreen() + nghbrCol.getBlue()) / 3;
            int selVal = (selCol.getRed() + selCol.getGreen() + selCol.getBlue()) / 3;
            vysledek.add(new Integer((-selVal + nghbrVal)));
        }
        vysledek.set(0, new Integer(selRight));
        return vysledek;

    }

    private void setAutoBrightnesCoords(PanoramatImage selected, PanoramatImage nghbr, StupidInteger max, StupidInteger opravas, StupidInteger opravan) {

        int starty = Math.max(nghbr.getTop(), selected.getTop());
        int opravay = Math.min(nghbr.getTop(), selected.getTop());
        int endy = Math.min(nghbr.getTop() + nghbr.getHeight(), selected.getTop() + selected.getHeight());

        max.setValue(-starty + endy);


        if (nghbr.getTop() < selected.getTop()) {
            opravas.setValue(0);//Math.abs(starty-opravay);
            opravan.setValue(Math.abs(starty - opravay));
        } else {
            opravan.setValue(0);//Math.abs(starty-opravay);
            opravas.setValue(Math.abs(starty - opravay));
        }
    }

    private ArrayList<Integer> computeLeftBrigthnesDiff(int index) {
        StupidInteger opravan = new StupidInteger();
        StupidInteger opravas = new StupidInteger();
        StupidInteger max = new StupidInteger();


        int ng = getHorizontalNeighbours(index).x;
        if (ng < 0) {
            return null;//no neighbr
        }
        PanoramatImage selected = im.get(index);
        PanoramatImage nghbr = im.get(ng);
        int selLeft = 0;
        int ngRight = 0;

        setAutoBrightnesCoords(selected, nghbr, max, opravas, opravan);

        if (index > ng) {//pocitany je vys nez soused
            /*
             *|-----|
             *|     |
             *|  |------|
             *|--|      |
             *   |  selected
             *   |-----|
             */

            selLeft = 0;
            ngRight = selected.getLeft() - nghbr.getLeft();



        }

        if (index < ng) {//pocitany je vys nez soused
            /*
             *|-----|
             *|     |
             *|     |--|
             *|-----|  |
             *   |  selected
             *   |-----|
             */

            selLeft = selected.getWidth() - (selected.getLeft() - nghbr.getLeft()) - 1;
            ngRight = nghbr.getWidth() - 1;

        }


        ArrayList<Integer> vysledek = new ArrayList();

        for (int y = 0; y < max.getValue(); y++) {
            //nghbr.getImage().setRGB(ngRight,y+opravan.getValue(),Color.RED.getRGB());
            //selected.getImage().setRGB(selLeft,y+opravas.getValue(),Color.RED.getRGB());

            Color nghbrCol = new Color(nghbr.getImage().getRGB(ngRight, y + opravan.getValue()));
            Color selCol = new Color(selected.getImage().getRGB(selLeft, y + opravas.getValue()));

            int nghbrVal = (nghbrCol.getRed() + nghbrCol.getGreen() + nghbrCol.getBlue()) / 3;
            int selVal = (selCol.getRed() + selCol.getGreen() + selCol.getBlue()) / 3;

            vysledek.add(new Integer((-selVal + nghbrVal)));
        }
        vysledek.set(0, new Integer(selLeft));
        return vysledek;

    }

    private int avg(ArrayList<Integer> list) {
        long acumulator = 0;

        for (Iterator it = list.iterator(); it.hasNext();) {
            Integer elem = (Integer) it.next();
            acumulator += elem.longValue();
        }
        return (int) (acumulator / (long) list.size());
    }

    private int median(ArrayList<Integer> list) {
        Collections.sort(list);
        return list.get(list.size() / 2).intValue();
    }

    private int mostOften(ArrayList<Integer> list) {
        Collections.sort(list);
        ArrayList<TPoint> countedHeights = Analyzer.countHeights(list, 0);
        Collections.sort(countedHeights, TPoint.byX);
        int x = countedHeights.size() - 1;
        return countedHeights.get(countedHeights.size() - 1).x;
    }

    private int enpoweredLeft(int leftBrighntes, int rightBrightnes, int imageWidth, int leftPosition) {
        double ono = (double) leftBrighntes;
        double x = (double) leftPosition;
        double w = (double) imageWidth;
        double rb = (double) rightBrightnes;
        return (int) Math.round((w * ono - rb * x) / (w - x));
    }

    private int enpoweredRight(int leftBrighntes, int rightBrightnes, int imageWidth, int rightPositionFromLeft) {
        double ono = (double) rightBrightnes;
        double x = (double) rightPositionFromLeft;
        double w = (double) imageWidth;
        double lb = (double) leftBrighntes;
        return (int) Math.round((w * ono + lb * x + w * lb) / x);
    }

    private void createRightNighbrhood(int index, int rdif) {
        try {
            Neighbourhood nghbr = getHorizontalNeighbours(index);
            /*
             *|-----|
            selected  |
             *|     |--|
             *|-----|  |
             *   |     |
             *   |-----|
             */
            if (index > nghbr.y) {
                int newLB = enpoweredLeft(im.get(nghbr.y).getLeftBrightnes(), im.get(nghbr.y).getRightBrightnes(), im.get(nghbr.y).getWidth(), im.get(index).getWidth() - rdif);
                im.get(nghbr.y).setLeftBrightnes(newLB);
            }

            /*
             *|-----|
            selected  |
             *|  |------|
             *|--|      |
             *   |      |
             *   |------|
             */
            if (index < nghbr.y) { //<je mozna obracene
                int newRB = enpoweredRight(im.get(index).getLeftBrightnes(), im.get(index).getRightBrightnes(), im.get(index).getWidth(), rdif);
                im.get(index).setRightBrightnes(newRB);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createLeftNighbrhood(int index, int ldif) {
        try {
            Neighbourhood nghbr = getHorizontalNeighbours(index);
            /*
             *|-----|
             *|     |
             *|  |------|
             *|--|      |
             *   |  selected
             *   |-----|
             */
            if (index > nghbr.x) {
                int newRB = enpoweredRight(im.get(nghbr.x).getLeftBrightnes(), im.get(nghbr.x).getRightBrightnes(), im.get(nghbr.x).getWidth(), im.get(index).getWidth() - ldif);
                im.get(nghbr.x).setRightBrightnes(newRB);
            }
            /*
             *|-----|
             *|     |
             *|     |--|
             *|-----|  |
             *   |  selected
             *   |-----|
             */
            if (index < nghbr.x) {
                int newLB = enpoweredLeft(im.get(index).getLeftBrightnes(), im.get(index).getRightBrightnes(), im.get(index).getWidth(), ldif);
                im.get(index).setLeftBrightnes(newLB);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void proceedOneImageBrightness(int index, boolean overBrigtened, int lrb, int method) {
        ArrayList<Integer> ll = null;
        ArrayList<Integer> rl = null;
        int vl = 0;
        int vr = 0;
        int oldSL = 0, oldSR = 0, oldLN = 0, oldRN = 0;
        int ldif = 0, rdif = 0;

        Neighbourhood nghbr = getHorizontalNeighbours(index);
        if (index < 0) {
            return;
        }
        getTheSelected().reload();
        afineIndexed(index);
        if ((lrb == ONLY_LEFT || lrb == ONLY_BOTH) && (nghbr.x >= 0)) {
            im.get(nghbr.x).reload();
            afineIndexed(nghbr.x);
            ll = computeLeftBrigthnesDiff(index);
        }
        if ((lrb == ONLY_RIGHT || lrb == ONLY_BOTH) && (nghbr.y >= 0)) {
            im.get(nghbr.y).reload();
            afineIndexed(nghbr.x);
            rl = computeRigtBrigthnesDiff(index);
        }
        if (ll != null) {
            ldif = ll.get(0).intValue();
            ll.remove(0);
            vl = zpracujVysledek(ll, method);

            if (overBrigtened) {
                oldSL = im.get(index).setLeftBrightnes(vl);
                oldLN = im.get(nghbr.x).setRightBrightnes(0);
            } else {
                oldSL = im.get(index).setLeftBrightnes(vl / 2);
                oldLN = im.get(nghbr.x).setRightBrightnes(-/*?*/vl / 2);
            }

        }
        if (rl != null) {
            rdif = rl.get(0).intValue();
            rl.remove(0);
            vr = zpracujVysledek(rl, method);

            if (overBrigtened) {
                oldSR = im.get(index).setRightBrightnes(vr);
                oldRN = im.get(nghbr.y).setLeftBrightnes(0);
            } else {
                oldSR = im.get(index).setRightBrightnes(vr / 2);
                oldRN = im.get(nghbr.y).setLeftBrightnes(-/*?*/vr / 2);
            }

        }

        //enpowered ten spodni
        //kazdou variantu zvlast??
        if (lrb == ONLY_LEFT) {
            /*if (index<nghbr.x){
            int newLB=enpoweredLeft(im.get(index).getLeftBrightnes(),im.get(index).getRightBrightnes(),im.get(index).getWidth(),ldif);
            im.get(index).setLeftBrightnes(newLB); //tyhle 4 veci by se meli zaprocedurovat, neb je bude viuzivat both
            }
            if (index>nghbr.x){
            int newRB=enpoweredRight(im.get(nghbr.x).getLeftBrightnes(),im.get(nghbr.x).getRightBrightnes(),im.get(nghbr.x).getWidth(),im.get(index).getWidth()-ldif);
            im.get(nghbr.x).setRightBrightnes(newRB);
            }*/
            createLeftNighbrhood(index, ldif);
        }

        if (lrb == ONLY_RIGHT) {
            /*  if (index>nghbr.y){
            int newLB=enpoweredLeft(im.get(nghbr.y).getLeftBrightnes(),im.get(nghbr.y).getRightBrightnes(),im.get(nghbr.y).getWidth(),im.get(index).getWidth()-rdif);
            im.get(nghbr.y).setLeftBrightnes(newLB);
            }
            if (index<nghbr.y){
            int newRB=enpoweredRight(im.get(index).getLeftBrightnes(),im.get(index).getRightBrightnes(),im.get(index).getWidth(),rdif);
            im.get(index).setRightBrightnes(newRB);
            }*/
            createRightNighbrhood(index, rdif);
        }

        if (lrb == ONLY_BOTH) {
            if (index > nghbr.x && index > nghbr.y) {
                //v prdeli..cos stim
                for (int x = 0; x <= 10; x++) {
                    if (nghbr.x >= 0) {
                        createLeftNighbrhood(index, ldif);
                    }
                    if (nghbr.y >= 0) {
                        createRightNighbrhood(index, rdif);
                    }
                }

            } else {
                if (nghbr.x >= 0) {
                    createLeftNighbrhood(index, ldif);
                }
                if (nghbr.y >= 0) {
                    createRightNighbrhood(index, rdif);
                }
            }

        }

    }

    void autosortPhotos(int a) {
        for (int x = 0; x < im.size(); x++) {
            if (x == 0) {
                im.get(x).setLeft(x * im.get(x).getWidth());
            } else {
                im.get(x).setLeft(x * im.get(x - 1).getWidth() + x * a);
            }
        }
    }

    void setAfineTransform(AffineTransform af) {
        if (af == null) {
            this.af = null;
        } else {
            this.af = new AffineTransform();


            this.af.translate((double) (posun.x), (double) (posun.y));
            this.af.scale(zoom, zoom);
            this.af.translate((double) (getTheSelected().getLeft()), (double) (+getTheSelected().getTop()));

            this.af.concatenate(af);


        }
    }

    void afinuj(double rotate, double scalex, double scaley, double sharex, double sharey, AffineTransform af) {

        getTheSelected().rotate = rotate;
        getTheSelected().scalex = scalex;
        getTheSelected().scaley = scaley;
        getTheSelected().sharex = sharex;
        getTheSelected().sharey = sharey;

        AffineTransformOp afo = new AffineTransformOp(af, AffineTransformOp.TYPE_BICUBIC);
        BufferedImage ii = afo.createCompatibleDestImage(getTheSelected().getImage(), null);
        getTheSelected().setImage(afo.filter(getTheSelected().getImage(), ii));
    }

    void afineIndexed(int index) {
        PanoramatImage pi = im.get(index);
        if (pi.getImage().getWidth() <= 0 || pi.getImage().getHeight() <= 0 || pi.afw <= 0 || pi.afh <= 0) {
            //System.out.println("Pokud se tohle pise jindy nez pri nacitani neexistujici fotky tak je to divny....");
            return;
        }
//if (((int)Math.abs(Math.round(Math.toDegrees(pi.rotate))))%90!=0 || pi.sharex!=0d || pi.sharey!=0d)
        {
            BufferedImage i = new BufferedImage(pi.afw, pi.afh, pi.getImage().getType());
            Graphics2D g2d = (Graphics2D) i.getGraphics();

            AffineTransform taf = new AffineTransform();
            taf.rotate(pi.rotate, pi.getWidth() / 2, pi.getHeight() / 2);
            taf.scale(pi.scalex, pi.scaley);
            taf.shear(pi.sharex, pi.sharey);

            AffineTransform aaff = new AffineTransform();
            aaff.translate((double) (pi.afx), (double) (pi.afy));
            aaff.concatenate(taf);

            g2d.drawImage(pi.getImage(), aaff, null);

            pi.setImage(i);
        }/*else{
        AffineTransform taf=new AffineTransform();
        taf.rotate(pi.rotate,pi.getWidth()/2,pi.getHeight()/2);
        taf.scale(pi.scalex,pi.scaley);
        taf.shear(pi.sharex,pi.sharey);
        AffineTransformOp afo= new AffineTransformOp(taf,AffineTransformOp.TYPE_BICUBIC);
        pi.setImage(afo.filter(pi.getImage(),null));
        }*/
    }

    void afinuj(double rotate, double scalex, double scaley, double sharex, double sharey, AffineTransform af, int w, int h, int xx, int yy) {

        //if (w<=0)w=1;
        //if (h<=0)h=1;
        getTheSelected().rotate = rotate;
        getTheSelected().scalex = scalex;
        getTheSelected().scaley = scaley;
        getTheSelected().sharex = sharex;
        getTheSelected().sharey = sharey;
        getTheSelected().afw = w;
        getTheSelected().afh = h;
        getTheSelected().afx = xx;
        getTheSelected().afy = yy;

        BufferedImage i = new BufferedImage(w, h, getTheSelected().getImage().getType());
        Graphics2D g2d = (Graphics2D) i.getGraphics();

        AffineTransform aaff = new AffineTransform();
        aaff.translate((double) (xx), (double) (yy));
        aaff.concatenate(af);

        g2d.drawImage(getTheSelected().getImage(), aaff, null);

        getTheSelected().setImage(i);
    }

    public void affineAll(double rotate, double scalex, double scaley, double sharex, double sharey, AffineTransform af, int w, int h, int xx, int yy) {
        for (Iterator it = im.iterator(); it.hasNext();) {
            PanoramatImage elem = (PanoramatImage) it.next();

            elem.rotate = rotate;
            elem.scalex = scalex;
            elem.scaley = scaley;
            elem.sharex = sharex;
            elem.sharey = sharey;
            elem.afw = w;
            elem.afh = h;
            elem.afx = xx;
            elem.afy = yy;

            BufferedImage i = new BufferedImage(w, h, elem.getImage().getType());
            Graphics2D g2d = (Graphics2D) i.getGraphics();

            AffineTransform aaff = new AffineTransform();
            aaff.translate((double) (xx), (double) (yy));
            aaff.concatenate(af);

            g2d.drawImage(elem.getImage(), aaff, null);

            elem.setImage(i);


        }
    }

    private ArrayList<Integer> getMostUpImageContainsThisOnesEDGE(int corner, int thisOneIndex) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        TPoint from = null;
        TPoint to = null;
        PanoramatImage pi = getImage(thisOneIndex);
        if (pi == null) {
            result.add(new Integer(-1));
            return result;
        }
        switch (corner) {
            case (LEFT):
                from = new TPoint(pi.getLeft(), pi.getTop());
                to = new TPoint(pi.getLeft(), pi.getTop() + pi.getHeight());
                break;
            case (RIGHT):
                from = new TPoint(pi.getLeft() + pi.getWidth(), pi.getTop());
                to = new TPoint(pi.getLeft() + pi.getWidth(), pi.getTop() + pi.getHeight());
                break;
            case (UP):
                from = new TPoint(pi.getLeft(), pi.getTop());
                to = new TPoint(pi.getLeft() + pi.getWidth(), pi.getTop());
                break;
            case (DOWN):
                from = new TPoint(pi.getLeft(), pi.getTop() + pi.getHeight());
                to = new TPoint(pi.getLeft() + pi.getWidth(), pi.getTop() + pi.getHeight());
                break;
        }
        if (from == null || to == null) {
            result.add(new Integer(-1));
            return result;
        }
        //int result=-1;

        for (int i = im.size() - 1; i >= 0; i--) {
            if (i != thisOneIndex) {
                PanoramatImage pp = im.get(i);
                if (corner == LEFT && pp.getLeft() + pp.getWidth() < pi.getLeft()) {
                    continue;
                }
                if (corner == RIGHT && pp.getLeft() > pi.getLeft() + pi.getWidth()) {
                    continue;
                }
                if (corner == UP && pp.getTop() + pp.getHeight() < pi.getTop()) {
                    continue;
                }
                if (corner == DOWN && pp.getTop() > pi.getTop() + pi.getHeight()) {
                    continue;
                }
                if (stepEdge(from, to, corner, pp)) {
                    //result=Math.max(result,i);
                    result.add(new Integer(i));
                }

            }
        }
        if (result.size() == 0) {
            result.add(new Integer(-1));
        }
        return result;
    }

    private boolean stepEdge(TPoint from, TPoint to, int corner, PanoramatImage pp) {

        for (int x = from.x; x <= to.x; x++) {
            for (int y = from.y; y <= to.y; y++) {
                TPoint p = new TPoint(x, y);
                if (isPointInImage(p, pp)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPointInImage(TPoint p, PanoramatImage pp) {
        if (p.x >= pp.getLeft()
                && p.x <= pp.getLeft() + pp.getWidth()
                && p.y >= pp.getTop()
                && p.y <= pp.getTop() + pp.getHeight()) {
            return true;
        }
        return false;
    }

    private void actualiseQuickList() {

        List<File> names = new ArrayList(im.size());
        boolean samePath = true;

        for (int i = 0; i < im.size(); i++) {
            PanoramatImage panoramatImage = im.get(i);
            names.add(panoramatImage.getSrc());
            if (!panoramatImage.getSrc().getAbsoluteFile().getParentFile().getAbsolutePath().equals(
                    names.get(0).getAbsoluteFile().getParentFile().getAbsolutePath())) {
                samePath = false;
            }

        }
        Collections.sort(names, new Comparator<File>() {

            public int compare(File o1, File o2) {
                return (o1.getName().compareTo(o2.getName()));
            }
        });
        this.sugestedSaves = new ArrayList<String>(0);
        if (names.size() > 0) {
            String nameSugestion2 = normalize1(names.get(0).getName(), names.get(names.size() - 1).getName());
            String nameSugestion1 = normalize2(names.get(0).getName(), names.get(names.size() - 1).getName());

            sugestedSaves.add(nameSugestion1);
            sugestedSaves.add(nameSugestion2);
            sugestedSaves.add("panoramas" + File.separator + nameSugestion1);
            sugestedSaves.add("panoramas" + File.separator + nameSugestion2);
            if (samePath) {
                File parent = names.get(0).getParentFile();
                while (parent != null) {
                    String suggestedPath1 = parent.getAbsolutePath() + File.separator;
                    String suggestedPath2 = parent.getAbsolutePath() + File.separator + "panoramas" + File.separator;

                    sugestedSaves.add(suggestedPath1 + nameSugestion1);
                    sugestedSaves.add(suggestedPath2 + nameSugestion1);
                    sugestedSaves.add(suggestedPath1 + nameSugestion2);
                    sugestedSaves.add(suggestedPath2 + nameSugestion2);
                    parent = parent.getParentFile();
                }
            }
        }
//        for (File file : names) {
//            sugestedSaves.add(file.getAbsolutePath());
//        }
        observableMe.setChanged();
        observableMe.notifyObservers();
    }
}
