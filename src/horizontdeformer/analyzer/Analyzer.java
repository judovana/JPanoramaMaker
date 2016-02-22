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
package horizontdeformer.analyzer;

import cammons.TPoint;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import java.util.Iterator;
import javax.swing.JProgressBar;

public class Analyzer {

    /** Creates a new instance of Analyzer */
    private BufferedImage image;
    public JProgressBar progress;
    private Matrix matrix;
    private int computedHeight;
    private double dist = 20;
    private int peersTolerance = 30;
    private int precision = 5;
    private int vzorek = 50;
    private double tolerance = 0.7;
    private ArrayList<Integer> heights;
    private ArrayList<TPoint> lowerbeggins, lowerends;
    private ArrayList<TPoint> upperbeggins, upperends;
    private ArrayList<TPoint> peers;
    private ArrayList<TPoint> lowerline;
    private ArrayList<TPoint> upperline;

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public void setPeersTolerance(int peersTolerance) {
        this.peersTolerance = peersTolerance;
    }

    public void setSample(int vzorek) {
        this.vzorek = vzorek;
    }

    public void setMatrixPrecision(int precision) {
        matrix.setPrecision(precision);
    }

    public Analyzer(BufferedImage im) {

        this.image = im;
        this.matrix = new Matrix(1, im.getHeight());


    }

    public void Analyze() {
        upperline = new ArrayList();
        lowerline = new ArrayList();

        if (image == null) {
            return;
        }
        int pr = 0;
        //nenajde to rozky bliz k top,botton,left,right nez je velikost matice!!!

        for (int x = 0; x < image.getWidth(); x++) {
            pr++;
            progress.setValue((pr * 100) / (image.getWidth()));
            matrix.readData(x, 0, image);
            int u = matrix.getUpperTop();
            if (u != -1) {
                // image.setRGB(x,u,Color.RED.getRGB());
                upperline.add(new TPoint(x, u));
            }
            int l = matrix.getLowerTop();
            if (l != -1) {
                //image.setRGB(x,l,Color.RED.getRGB());
                lowerline.add(new TPoint(x, l));
            }

        }



        upperbeggins = findBeggins(upperline, -1);
        removeToNear(upperbeggins, dist);
        removeToNear2(upperbeggins, dist);
        lowerbeggins = findBeggins(lowerline, -1);
        removeToNear(lowerbeggins, dist);
        removeToNear2(lowerbeggins, dist);
        upperends = findEnds(upperline, 1);
        removeToNear(upperends, dist);
        removeToNear2(upperends, dist);
        lowerends = findEnds(lowerline, 1);
        removeToNear(lowerends, dist);
        removeToNear2(lowerends, dist);

        lowerline = new ArrayList();
        lowerline.addAll(lowerends);
        lowerline.addAll(lowerbeggins);
        removeToNear2(lowerline, dist);

        upperline = new ArrayList();
        upperline.addAll(upperends);
        upperline.addAll(upperbeggins);
        removeToNear2(upperline, dist);



        Collections.sort(lowerline, TPoint.byX);
        Collections.sort(upperline, TPoint.byX);
        peers = findPeers(upperline, lowerline, peersTolerance);
        removeToNear2(peers, dist);

        Collections.sort(heights);
        ArrayList<TPoint> countedHeights = countHeights(heights, 5);
        Collections.sort(countedHeights, TPoint.byX);
        int suma = 0;
        int count = 0;
        int x = countedHeights.size() - 1;
        int maxocc = countedHeights.get(countedHeights.size() - 1).x;

        while (x >= 0 && countedHeights.get(x).x == maxocc) {
            count++;
            suma += countedHeights.get(x).y;
            x--;
        }

        computedHeight = suma / count;


    }

    private ArrayList<TPoint> findBeggins(ArrayList<TPoint> l, int z) {

        ArrayList<TPoint> vysledek = new ArrayList();




        int futureOK = 0;

        for (int x = 0; x < l.size(); x++) {
            if (isFutureOk(DIRECTION_FF, l, x, vzorek, precision, tolerance)) {
                TPoint p = l.get(x);
                if (futureOK == 0) {
                    vysledek.add(new TPoint(p.x, p.y, z));
                }
                futureOK++;
            } else {
                futureOK = 0;
            }


        }
        return vysledek;
    }

    private ArrayList<TPoint> findEnds(ArrayList<TPoint> l, int z) {
        ArrayList<TPoint> vysledek = new ArrayList();




        int futureOK = 0;

        for (int x = l.size() - 1; x >= 0; x--) {
            if (isFutureOk(DIRECTION_REW, l, x, vzorek, precision, tolerance)) {
                TPoint p = l.get(x);
                if (futureOK == 0) {
                    vysledek.add(new TPoint(p.x, p.y, z));
                }
                futureOK++;
            } else {
                futureOK = 0;
            }


        }
        return vysledek;
    }
    private static final int DIRECTION_FF = 1;
    private static final int DIRECTION_REW = 2;

    private boolean isFutureOk(int direction, ArrayList<TPoint> l, int index, int delka, int precision, double tolerance) {
        if (tolerance > 1d) {
            tolerance = 1d;
        }
        if (tolerance < 0d) {
            tolerance = 0d;
        }
        TPoint last = l.get(index);
        int ok = 0;
        switch (direction) {
            case (DIRECTION_FF):
                for (int i = index + 1; i < Math.min(index + delka, l.size()); i++) {
                    TPoint p = l.get(i);
                    if (Math.abs(last.y - p.y) < precision) {
                        ok++;
                    }
                }
                break;

            case (DIRECTION_REW):
                for (int i = index - 1; i > Math.max(index - delka, 0); i--) {
                    TPoint p = l.get(i);
                    if (Math.abs(last.y - p.y) < precision) {
                        ok++;
                    }
                }
                break;

        }
        if (((double) delka * tolerance) <= ok) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<TPoint> getResult(int i) {
        switch (i + 1) {
            case 1:
                upperends.add(0, new TPoint(computedHeight, 0, 0));
                return upperends;
            case 2:
                lowerends.add(0, new TPoint(computedHeight, 0, 0));
                return lowerends;
            case 3:
                lowerbeggins.add(0, new TPoint(computedHeight, 0, 0));
                return lowerbeggins;
            case 4:
                upperbeggins.add(0, new TPoint(computedHeight, 0, 0));
                return upperbeggins;
            case 5:
                lowerends.addAll(upperends);
                removeToNear2(lowerends, dist);
                Collections.sort(lowerends, TPoint.byX);
                lowerends.add(0, new TPoint(computedHeight, 0, 0));
                return lowerends;
            case 6:
                lowerbeggins.addAll(upperbeggins);
                removeToNear2(lowerbeggins, dist);
                Collections.sort(lowerbeggins, TPoint.byX);
                lowerbeggins.add(0, new TPoint(computedHeight, 0, 0));
                return lowerbeggins;
            case 7:
                lowerends.addAll(upperbeggins);
                removeToNear2(lowerends, dist);
                Collections.sort(lowerends, TPoint.byX);
                lowerends.add(0, new TPoint(computedHeight, 0, 0));
                return lowerends;
            case 8:
                lowerbeggins.addAll(upperends);
                removeToNear2(lowerbeggins, dist);
                Collections.sort(lowerbeggins, TPoint.byX);
                lowerbeggins.add(0, new TPoint(computedHeight, 0, 0));
                return lowerbeggins;
            case 9:
                lowerline.addAll(upperline);
                removeToNear2(lowerline, dist);
                Collections.sort(lowerline, TPoint.byX);
                lowerline.add(0, new TPoint(computedHeight, 0, 0));
                return lowerline;
            case 10:
                peers.add(0, new TPoint(computedHeight, 0, 0));
                return peers;

            case 11:
                lowerline.add(0, new TPoint(computedHeight, 0, 0));
                return lowerline;
            case 12:
                upperline.add(0, new TPoint(computedHeight, 0, 0));
                return upperline;

        }

        return null;
    }

    public int getFotosIndividualHeight() {
        return 0;
    }

    private void removeToNear(ArrayList<TPoint> l, double i) {
        for (int x = 0; x < l.size();) {
            TPoint p = l.get(x);
            x++;
            while (x < l.size() && p.distance2D((TPoint) l.get(x)) < i) {
                p = (TPoint) l.get(x);
                l.remove(x);
            }
        }
    }

    private ArrayList<TPoint> findPeers(ArrayList<TPoint> l1, ArrayList<TPoint> l2, int tolerance) {
        ArrayList<TPoint> vysledek = new ArrayList();
        heights = new ArrayList();
        for (int i = 0; i < l1.size(); i++) {
            TPoint p1 = l1.get(i);
            TPoint p2 = findNeerestByX(p1, l2);
            // if (Math.abs(p1.x-p2.x)<tolerance)
            {
                vysledek.add(p1);
                vysledek.add(p2);
                heights.add(new Integer((int) Math.round(p1.distance2D(p2))));

            }
        }
        return vysledek;
    }

    private TPoint findNeerestByX(TPoint p1, ArrayList<TPoint> l2) {
        int dist = 99999999;
        TPoint vysledek = null;
        for (int i = 0; i < l2.size(); i++) {
            TPoint p2 = l2.get(i);
            if (Math.abs(p1.x - p2.x) < dist) {
                vysledek = p2;
                dist = Math.abs(p1.x - p2.x);
                if (dist == 0) {
                    return vysledek;
                }
            }
        }
        return vysledek;
    }

    private void removeToNear2(ArrayList<TPoint> l, double dist) {
        for (int x = 0; x < l.size(); x++) {
            TPoint p = l.get(x);
            for (int y = 0; y < l.size(); y++) {
                if (x != y) {
                    if (p.distance2D(l.get(y)) < dist) {
                        if (y < x) {
                            x--;
                        }
                        l.remove(y);
                        y--;
                    }

                }
            }

        }
    }

    public static ArrayList<TPoint> countHeights(ArrayList<Integer> heights, int i) {
        ArrayList<TPoint> vysledek = new ArrayList();
        for (Iterator it = heights.iterator(); it.hasNext();) {
            Integer elem = (Integer) it.next();
            int counter = 0;
            for (Iterator it2 = heights.iterator(); it2.hasNext();) {
                Integer elem2 = (Integer) it2.next();
                if (Math.abs(elem.intValue() - elem2.intValue()) < i) {
                    counter++;
                }

            }
            vysledek.add(new TPoint(counter, elem.intValue()));
        }
        return vysledek;
    }
}
