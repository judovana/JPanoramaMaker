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
package horizontdeformer.interpolators;

import cammons.TPoint;
import java.util.ArrayList;

public class NpolynomialInterpolation implements Interpolation {

    private double a, b;
    private ArrayList<TPoint> polynom;

    /** Creates a new instance of LinearInterpolation */
    public NpolynomialInterpolation(ArrayList<TPoint> a) {

        createInterpolation(a);
    }

    private void createInterpolation(ArrayList<TPoint> a) {
        polynom = new ArrayList();
        polynom.add(new TPoint(-200, ((TPoint) a.get(0)).y));
        polynom.add(new TPoint(-100, ((TPoint) a.get(0)).y));
        polynom.addAll(a);
        polynom.add(new TPoint(((TPoint) a.get(a.size() - 1)).x + 100, ((TPoint) a.get(a.size() - 1)).y));
        polynom.add(new TPoint(((TPoint) a.get(a.size() - 1)).x + 200, ((TPoint) a.get(a.size() - 1)).y));

    }

    public int getY(int x) {
        double pi = 0;
        for (int i = 0; i < polynom.size(); i++) {
            pi += getLi(i, (double) x) * ((TPoint) polynom.get(i)).y;
        }
        return (int) pi;
    }

    private double getLi(int i, double x) {
        double citatel = 1;
        for (int ii = 0; ii < polynom.size(); ii++) {
            if (ii != i) {
                citatel *= (x - (double) ((TPoint) polynom.get(ii)).x);
            }
        }
        double jmenovatel = 1;
        for (int ii = 0; ii < polynom.size(); ii++) {
            if (ii != i) {
                jmenovatel *= ((double) ((TPoint) polynom.get(i)).x - (double) ((TPoint) polynom.get(ii)).x);
            }
        }
        return citatel / jmenovatel;
    }

    public static void main(String args[]) {
        ArrayList<TPoint> a = new ArrayList();
        a.add(new TPoint(0, 2));
        a.add(new TPoint(1, 3));
        a.add(new TPoint(2, 12));
        a.add(new TPoint(5, 147));
        NpolynomialInterpolation np = new NpolynomialInterpolation(a);
        for (int x = 0; x <= 5; x++) {
            System.out.println(x + " " + np.getY(x));
        }
    }
}
