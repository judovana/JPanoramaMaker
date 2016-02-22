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

public class AnalytycLine {

    double x, y, k, q;
    double a, b, c;
    double p;
    int type = 1;

    public AnalytycLine(int x1, int y1, int x2, int y2) throws IllegalArgumentException {
        if (x1 == x2 && y1 == y2) {
            throw new IllegalArgumentException("point1 equals point2");
        }
        if (x1 == x2) {
            type = 2;
            p = x1;
            x = x1;
            y = y1;
            return;
        }
        double dx1 = x1;
        double dx2 = x2;
        double dy1 = y1;
        double dy2 = y2;
        k = (dy2 - dy1) / (dx2 - dx1);
        q = dy1 - (k * dx1);
        x = dx1;
        y = dy1;
        a = k;
        b = -1;
        c = q;

    }

    public double getX(double y) {
        if (type == 2) {
            return p;
        }
        return (0 - b * y - c) / a;
    }

    public double getY(double x) {
        if (type == 2) {
            return 0;
        }
        return (0 - a * x - c) / b;
    }

    public boolean isInLine(double tolerance, double x, double y) {
        if (type == 2) {
            return Math.abs(x - p) < tolerance;
        }
        return (Math.abs(a * x + b * y + c) < tolerance);
    }

    public boolean isInLine(int x, int y) {
        if (type == 2) {
            return Math.round(x - p) == 0;
        }
        return (Math.round((a * (double) x + b * (double) y + c)) == 0);
    }

    public boolean isInHalfPlane1(double x, double y) {
        if (type == 2) {
            return x <= p;
        }
        return ((a * x + b * y + c) <= 0);
    }

    public boolean isInHalfPlane2(double x, double y) {
        if (type == 2) {
            return x >= p;
        }
        return ((a * x + b * y + c) >= 0);
    }

    public boolean isInHalfPlane1(int x, int y) {
        if (type == 2) {
            return x <= p;
        }
        return (Math.round(a * (double) x + b * (double) y + c) <= 0);
    }

    public boolean isInHalfPlane2(int x, int y) {
        if (type == 2) {
            return x >= p;
        }
        return (Math.round(a * (double) x + b * (double) y + c) >= 0);
    }

    public static void main(String[] args) {
        AnalytycLine al;
        al = new AnalytycLine(0, 0, 0, 10);
        System.out.println(al.getX(20));
        System.out.println(al.getY(30));


        System.out.println(al.isInHalfPlane1(-50, 0));
        System.out.println(al.isInHalfPlane2(-50, 0));
    }
}
