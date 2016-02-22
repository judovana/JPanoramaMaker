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
package horizontdeformer.deformer;

import horizontdeformer.cammons.OrientedCross;
import cammons.TPoint;
import horizontdeformer.graphicsoutputs.GraphicsAdds;
import horizontdeformer.interpolators.InterpolationWithCoords;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class DeformerData {

    ArrayList<OrientedCross> originalCross = new ArrayList();
    ArrayList<OrientedCross> XsortedCross;
    ArrayList<OrientedCross> YsortedCross;
    ArrayList<TPoint> guidingLine;
    private int lefttop, righttop;
    private BufferedImage im;
    private int individualHeight;
    private int avaragey;
    private int method;
    private static final int METHOD_UPPER = 1;
    private static final int METHOD_LOWER = 2;

    public int getApopsuv() {
        return apopsuv;
    }

    public void setApopsuv(int apopsuv) {
        this.apopsuv = apopsuv;
    }
    private int apopsuv = 0;
    private static final int METHOD_ALL = 3;

    public int getAvaragey() {
        return avaragey;
    }

    public BufferedImage getIm() {
        return im;
    }

    public int getIndividualHeight() {
        return individualHeight;
    }

    public ArrayList<TPoint> getGuidingLine() {
        return guidingLine;
    }

    public int getLefttop() {
        return lefttop;
    }

    public int getMethod() {
        return method;
    }

    public ArrayList<OrientedCross> getOriginalCross() {
        return originalCross;
    }

    public int getRighttop() {
        return righttop;
    }

    public ArrayList<OrientedCross> getXsortedCross() {
        return XsortedCross;
    }

    public ArrayList<OrientedCross> getYsortedCross() {
        return YsortedCross;
    }
    private static final int METHOD_MIDDLE = 4;

    /** Creates a new instance of DeformerData */
    public void setMethod(int method) throws Exception {
        this.method = method;
        guidingLine = null;
        if (im == null) {
            return;
        }
        switch (method) {
            case METHOD_MIDDLE:
                guidingLine = new ArrayList();
                guidingLine.add(new TPoint(0, lefttop + individualHeight / 2));
                for (int x = 0; x < originalCross.size(); x += 2) {
                    OrientedCross elem1 = (OrientedCross) XsortedCross.get(x);

                    OrientedCross elem2;
                    if (x + 1 < XsortedCross.size()) {
                        elem2 = (OrientedCross) XsortedCross.get(x + 1);
                    } else {
                        elem2 = (OrientedCross) XsortedCross.get(x - 1);
                    }
                    int SGN1 = 0;
                    if (YsortedCross.indexOf(elem1) < originalCross.size() / 2) {
                        SGN1 = 1;
                    } else {
                        SGN1 = -1;
                    }
                    int SGN2 = 0;
                    if (YsortedCross.indexOf(elem2) < originalCross.size() / 2) {
                        SGN2 = 1;
                    } else {
                        SGN2 = -1;
                    }
                    guidingLine.add(new TPoint(
                            (elem1.getX() + elem2.getX()) / 2,
                            ((elem1.getY() + SGN1 * individualHeight / 2) + (elem2.getY() + SGN2 * individualHeight / 2)) / 2));
                }
                guidingLine.add(new TPoint(im.getWidth(), righttop + individualHeight / 2));
                break;

            case METHOD_ALL:
                guidingLine = new ArrayList();
                guidingLine.add(new TPoint(0, lefttop + individualHeight / 2));
                for (Iterator it = XsortedCross.iterator(); it.hasNext();) {
                    OrientedCross elem = (OrientedCross) it.next();
                    int SGN = 0;
                    if (YsortedCross.indexOf(elem) < originalCross.size() / 2) {
                        SGN = 1;
                    } else {
                        SGN = -1;
                    }
                    guidingLine.add(new TPoint(elem.getX(), elem.getY() + SGN * individualHeight / 2));
                }
                guidingLine.add(new TPoint(im.getWidth(), righttop + individualHeight / 2));
                break;

            case METHOD_UPPER:
                guidingLine = new ArrayList();
                guidingLine.add(new TPoint(0, lefttop + individualHeight / 2));
                for (Iterator it = XsortedCross.iterator(); it.hasNext();) {
                    OrientedCross elem = (OrientedCross) it.next();

                    if (YsortedCross.indexOf(elem) < originalCross.size() / 2) {
                        guidingLine.add(new TPoint(elem.getX(), elem.getY() + individualHeight / 2));
                    }
                }
                guidingLine.add(new TPoint(im.getWidth(), righttop + individualHeight / 2));
                break;

            case METHOD_LOWER:
                guidingLine = new ArrayList();
                guidingLine.add(new TPoint(0, lefttop + individualHeight / 2));
                for (Iterator it = XsortedCross.iterator(); it.hasNext();) {
                    OrientedCross elem = (OrientedCross) it.next();

                    if (YsortedCross.indexOf(elem) < originalCross.size() / 2) {
                    } else {
                        guidingLine.add(new TPoint(elem.getX(), elem.getY() - individualHeight / 2));
                    }
                }
                guidingLine.add(new TPoint(im.getWidth(), righttop + individualHeight / 2));
                break;

        }

        if (guidingLine != null) {
            //prumerna hodnota
            avaragey = 0;
            for (Iterator it = guidingLine.iterator(); it.hasNext();) {
                TPoint elem = (TPoint) it.next();
                avaragey += elem.y;
            }
            avaragey /= guidingLine.size();

        }
    }

    public DeformerData(ArrayList<TPoint> list, BufferedImage image) {
        if (list.size() > 1) {
            im = image;
            for (int i = 1; i < list.size(); i++) {


                TPoint elem = (TPoint) list.get(i);
                originalCross.add(new OrientedCross(elem));

            }
            individualHeight = ((TPoint) list.get(0)).x;
            XsortedCross = xSort(originalCross);
            YsortedCross = ySort(originalCross);
            if (YsortedCross.indexOf(XsortedCross.get(0)) < originalCross.size() / 2) {
                lefttop = ((OrientedCross) XsortedCross.get(0)).getY();
            } else {
                lefttop = ((OrientedCross) XsortedCross.get(0)).getY() - individualHeight;
            }
            if (YsortedCross.indexOf(XsortedCross.get(originalCross.size() - 1)) < originalCross.size() / 2) {
                righttop = ((OrientedCross) XsortedCross.get(originalCross.size() - 1)).getY();
            } else {
                righttop = ((OrientedCross) XsortedCross.get(originalCross.size() - 1)).getY() - individualHeight;
            }
            try {


                setMethod(METHOD_ALL);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void draw(Graphics g, double zoom, int interpolation) {
        g.setColor(Color.ORANGE);
        for (int i = 0; i < originalCross.size(); i++) {
            OrientedCross o = (OrientedCross) originalCross.get(i);
            GraphicsAdds.Cross(g, (int) (o.getX() * zoom), (int) (o.getY() * zoom), 10);
            GraphicsAdds.Arrow(g, (int) (o.getX() * zoom), (int) (o.getY() * zoom), o.getOrientation(), 15);

        }
        g.setColor(Color.ORANGE);
        GraphicsAdds.Cross(g, (int) (0 * zoom), (int) ((lefttop + individualHeight) * zoom), 10);
        GraphicsAdds.Cross(g, (int) (im.getWidth() * zoom), (int) ((righttop + individualHeight) * zoom), 10);
        GraphicsAdds.Cross(g, (int) (0 * zoom), (int) (lefttop * zoom), 10);
        GraphicsAdds.Cross(g, (int) (im.getWidth() * zoom), (int) (righttop * zoom), 10);

        if (guidingLine != null) {
            g.setColor(Color.yellow);
            if (interpolation == Deformer.Linear_Interpolation) {
                for (int i = 1; i < guidingLine.size(); i++) {
                    TPoint p1 = guidingLine.get(i - 1);
                    TPoint p2 = guidingLine.get(i);
                    g.drawLine((int) (zoom * p1.x), (int) (zoom * p1.y), (int) (zoom * p2.x), (int) (zoom * p2.y));
                }

            }
            if (interpolation == Deformer.Kvadratic_Interpolation) {
                Deformer d = new Deformer(this);
                d.setUsedInterpolation(interpolation);
                for (int i = 0; i < guidingLine.size() - 1; i++) {
                    //TPoint p1=guidingLine.get(i-1);
                    //TPoint p2=guidingLine.get(i);
                    //g.drawLine((int)(zoom*p1.x),(int)(zoom*p1.y),(int)(zoom*p2.x),(int)(zoom*p2.y));
                    InterpolationWithCoords iws = d.createKvadraticInerpolation(i, guidingLine);
                    for (int x = iws.getB1().x; x <= iws.getB2().x; x++) {
                        int y = iws.getInterpolation().getY(x);
                        g.drawLine((int) (zoom * x), (int) (zoom * y), (int) (zoom * x), (int) (zoom * y));
                    }
                }

            }

            if (interpolation == Deformer.NPolynomial_Interpolation) {
                Deformer d = new Deformer(this);
                d.setUsedInterpolation(interpolation);

                InterpolationWithCoords iws = d.createNpolynomialInterpolation(0, guidingLine);
                int lastx = iws.getB1().x;
                int lasty = iws.getInterpolation().getY(lastx);

                for (int x = iws.getB1().x + 1; x <= iws.getB2().x; x++) {
                    int y = iws.getInterpolation().getY(x);
                    g.drawLine((int) (zoom * lastx), (int) (zoom * lasty), (int) (zoom * x), (int) (zoom * y));
                    lastx = x;
                    lasty = y;
                }
            }


            g.setColor(Color.MAGENTA);
            g.drawLine(0, (int) ((avaragey + apopsuv) * zoom), (int) (im.getWidth() * zoom), (int) ((avaragey + apopsuv) * zoom));
        }


    }

    private ArrayList<OrientedCross> xSort(ArrayList<OrientedCross> a) {

        for (Iterator it = a.iterator(); it.hasNext();) {
            OrientedCross elem = (OrientedCross) it.next();
            elem.setCompareBy(OrientedCross.COMPARE_BY_X);

        }
        ArrayList<OrientedCross> vysledek = (ArrayList) a.clone();
        Collections.sort(vysledek);
        return vysledek;
    }

    private ArrayList<OrientedCross> ySort(ArrayList<OrientedCross> a) {

        for (Iterator it = a.iterator(); it.hasNext();) {
            OrientedCross elem = (OrientedCross) it.next();
            elem.setCompareBy(OrientedCross.COMPARE_BY_Y);

        }
        ArrayList<OrientedCross> vysledek = (ArrayList) a.clone();
        Collections.sort(vysledek);
        return vysledek;
    }
}
