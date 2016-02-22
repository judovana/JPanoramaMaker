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
import java.util.ArrayList;

public class GausianMatrix {

    /** Creates a new instance of Matrix */
    int size;

    ;
    double[][] prvky;
    /*
    [0,0][1,0][2,0] | [3,0]
    [0,1][1,1][2,1] | [3,1]
    [0,2][1,2][2,2] | [3,2]
    [0,3][1,3][2,3] | [3,3]
    
     */

    public double[][] getPrvky() {
        return prvky;
    }

    public int getSize() {
        return size;
    }

    public GausianMatrix(int size) {
        prvky = new double[size][size];
        this.size = size;
    }

    /*a.size bodu
     *y=ax^2+ax+c ma JEDEN  vrchol
     *y=dx^3+ax^2+ax+c ma DVA vrchol
     *y=anx^n+a(n-1)x^(n-1)+...+a2x^2+a1x+a0=y ma N-1 vrcholu
     */
    /*
     *pislo a.size bodu (x,y)
     *z nich udelam n=a.size rovnic
     *o neznamich an ... a1
     *a.get(0) je x1^(1)=y(1)
     *a.get(1) je x(2)^(2)=y(2)
     *a.get(n-2) je x(n-1)^(n-1)=y(n-1)
     *a.get(n-1) je x(n)^n=y(n)
     *=>N-1 vrcholu
     *
     *anxn^(n)+a(n-1)a(n-1)^n...a1x1=yn
     *anxn^(n)+a(n-1)a(n-1)^n...a1x1=y(n-1)
     *...........
     *anxn^(n)+a(n-1)a(n-1)^n...a1x1=y1
     *
     *get 0  ;  1   ;   2
     *size=3
     *x=5 y=4;x=3 y=2; x=7 y=8
     *
     * a1*5+a2*5^2+a5*7^3=4
     * a1*3+a2*3^2+a3*3^3=2
     * a1*7+a2*7^2+a3*7^3=8
     */
    public GausianMatrix(ArrayList<TPoint> a) {
        prvky = new double[size][size];
        this.size = size;
    }

    public void setPrvek(int x, int y, double prvek) {
        if (x >= size) {
            return;
        }
        if (y >= size) {
            return;
        }
        prvky[x][y] = prvek;
    }
}
