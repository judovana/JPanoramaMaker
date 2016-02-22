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

import cammons.TPoint;
import horizontdeformer.interpolators.Interpolation;
import horizontdeformer.interpolators.InterpolationWithCoords;
import horizontdeformer.interpolators.KvadraticInterpolation;
import horizontdeformer.interpolators.LinearInterpolation;
import horizontdeformer.interpolators.NpolynomialInterpolation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JProgressBar;

public class Deformer {

    DeformerData dd;
    public JProgressBar progress;
    private int usedInterpolation;
    public static final int Linear_Interpolation = 0;
    public static final int Kvadratic_Interpolation = 1;
    public static final int NPolynomial_Interpolation = 2;

    ///interpolacni body
    //TPoint b1;
    //TPoint b2;
    //TPoint b3;
    //Interpolation li;
    /** Creates a new instance of Deformer */
    public Deformer(DeformerData dd) {
        this.dd = dd;
        setUsedInterpolation(Linear_Interpolation);

    }

    public int getUsedInterpolation() {
        return usedInterpolation;
    }

    public void setUsedInterpolation(int usedInterpolation) {
        this.usedInterpolation = usedInterpolation;
    }

    public BufferedImage deformation() {
        if (progress != null) {
            progress.setValue(0);
        }
        if (dd == null) {
            return null;
        }
        BufferedImage vysledek;
        vysledek = new BufferedImage(dd.getIm().getWidth(), dd.getIm().getHeight(), dd.getIm().getType());
        System.out.println("please wait");
        int[] pole = new int[dd.getIm().getHeight()];
        int last = 0;
        InterpolationWithCoords li = null;
        if (usedInterpolation == Linear_Interpolation) {
            li = createLineraInterpolation(0);
        }
        if (usedInterpolation == Kvadratic_Interpolation) {
            li = createKvadraticInerpolation(0);
        }
        if (usedInterpolation == NPolynomial_Interpolation) {
            li = createNpolynomialInterpolation(0);
        }
        last++;


        for (int x = 0; x < dd.getIm().getWidth(); x++) {
            if (x > li.getB2().x) {
                if (usedInterpolation == Linear_Interpolation) {
                    li = createLineraInterpolation(last);
                }
                if (usedInterpolation == Kvadratic_Interpolation) {
                    li = createKvadraticInerpolation(last);
                }
                last++;

            }
            //System.out.println(x+":"+dd.getIm().getWidth());
            if (progress != null) {
                progress.setValue((x * 100) / dd.getIm().getWidth());
            }
            readcolumn(x, 0, dd.getIm(), pole);
            writecolumn(pole, x, compute(x, li.getInterpolation()), vysledek);
            //vysledek.setRGB(x,li.getY(x),Color.BLACK.getRGB());

        }
        System.out.println("finished");
        if (progress != null) {
            progress.setValue(100);
        }
        return vysledek;
    }

    private int compute(int x, Interpolation l) {
        return -(l.getY(x) - (dd.getAvaragey() + dd.getApopsuv()));
    }

    private int[] readcolumn(int x, int y, BufferedImage im, int[] pole) {
        return im.getRGB(x, y, 1, im.getHeight(), pole, 0, 1);
    }

    private void writecolumn(int[] pole, int x, int y, BufferedImage im) {
        if (y == 0) {
            im.setRGB(x, y, 1, im.getHeight(), pole, 0, 1);
        } else if (y > 0) {
            im.setRGB(x, y, 1, im.getHeight() - y, pole, 0, 1);
        } else if (y < 0) {
            im.setRGB(x, 0, 1, im.getHeight() + y, pole, -1 * y, 1);
        }

    }

    /*private Interpolation getInterpolation(int x, int y, int x0, int y0) {
    if (usedInterpolation==Linear_Interpolation || y==y0)
    return new LinearInterpolation(x, y, x0, y0);
    if (usedInterpolation==Kvadratic_Interpolation)
    return null;//new KvadraticInterpolation(x, y, x0, y0);
    return null;
    }*/
    private InterpolationWithCoords createLineraInterpolation(int i) {

        /*b1=dd.getGuidingLine().get(i);
        b2=dd.getGuidingLine().get(i+1);
        
        return new LinearInterpolation(b1.x,b1.y,b2.x,b2.y);
         */

        return createLineraInterpolation(i, dd.getGuidingLine());

    }

    private InterpolationWithCoords createNpolynomialInterpolation(int i) {



        return createNpolynomialInterpolation(i, dd.getGuidingLine());

    }

    private InterpolationWithCoords createKvadraticInerpolation(int i) {

        /*if (i+2>=dd.getGuidingLine().size()){
        b1=dd.getGuidingLine().get(i-1);
        b2=dd.getGuidingLine().get(i+1);
        b3=dd.getGuidingLine().get(i);
        if (b3.y==b2.y) return new LinearInterpolation(b3.x,b3.y,b2.x,b2.y);
        return new KvadraticInterpolation(b1.x,b1.y,b2.x,b2.y,b3.x,b3.y);
        }
        b1=dd.getGuidingLine().get(i);
        b2=dd.getGuidingLine().get(i+1);
        b3=dd.getGuidingLine().get(i+2);
        if (b1.y==b2.y) return new LinearInterpolation(b1.x,b1.y,b2.x,b2.y);
        return new KvadraticInterpolation(b1.x,b1.y,b2.x,b2.y,b3.x,b3.y);
         */
        return createKvadraticInerpolation(i, dd.getGuidingLine());


    }

    public InterpolationWithCoords createLineraInterpolation(int i, ArrayList<TPoint> gl) {

        TPoint b1 = gl.get(i);
        TPoint b2 = gl.get(i + 1);
        InterpolationWithCoords iws = new InterpolationWithCoords(new LinearInterpolation(b1.x, b1.y, b2.x, b2.y));
        iws.setB1(b1);
        iws.setB2(b2);
        return iws;
    }

    public InterpolationWithCoords createKvadraticInerpolation(int i, ArrayList<TPoint> gl) {

        if (i + 2 >= gl.size()) {
            TPoint b1 = gl.get(i - 1);
            TPoint b2 = gl.get(i + 1);
            TPoint b3 = gl.get(i);
            if (b3.y == b1.y || b3.y == b2.y) {
                InterpolationWithCoords iws = new InterpolationWithCoords(new LinearInterpolation(b3.x, b3.y, b2.x, b2.y));
                iws.setB1(b3);
                iws.setB2(b2);
                return iws;
            }

            InterpolationWithCoords iws = new InterpolationWithCoords(new KvadraticInterpolation(b1.x, b1.y, b3.x, b3.y, b2.x, b2.y));
            iws.setB1(b3);
            iws.setB2(b2);
            return iws;
        }
        TPoint b1 = gl.get(i);
        TPoint b2 = gl.get(i + 1);
        TPoint b3 = gl.get(i + 2);
        if (b1.y == b2.y) {

            InterpolationWithCoords iws = new InterpolationWithCoords(new LinearInterpolation(b1.x, b1.y, b2.x, b2.y));
            iws.setB1(b1);
            iws.setB2(b2);
            return iws;
        }
        InterpolationWithCoords iws = new InterpolationWithCoords(new KvadraticInterpolation(b1.x, b1.y, b2.x, b2.y, b3.x, b3.y));
        iws.setB1(b1);
        iws.setB2(b2);
        return iws;

    }

    public InterpolationWithCoords createNpolynomialInterpolation(int i, ArrayList<TPoint> gl) {
        TPoint b1 = gl.get(i);
        TPoint b2 = gl.get(gl.size() - 1);
        InterpolationWithCoords iws = new InterpolationWithCoords(new NpolynomialInterpolation(gl));
        iws.setB1(b1);
        iws.setB2(b2);
        return iws;
    }
}
