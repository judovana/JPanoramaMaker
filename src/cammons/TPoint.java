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
package cammons;

import java.util.Comparator;


public class TPoint {

    public int x, y, z;

    /** Creates a new instance of TPoint */
    public TPoint() {
        x = 0;
        y = 0;
        z = 0;
    }

    public TPoint(int x, int y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    public TPoint(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "x=" + x + " y= " + y + " z=" + z;
    }

    public double distance2D(TPoint p) {
        int dx = p.x - this.x;
        int dy = p.y - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public int getLeft() {
        return x;
    }

    public int getTop() {
        return y;
    }
    public static Comparator byX = new Comparator() {

        public int compare(Object o1, Object o2) {
            TPoint p1 = (TPoint) o1;
            TPoint p2 = (TPoint) o2;
            return p1.x - p2.x;
        }
    };
    public static Comparator byY = new Comparator() {

        public int compare(Object o1, Object o2) {
            TPoint p1 = (TPoint) o1;
            TPoint p2 = (TPoint) o2;
            return p1.y - p2.y;
        }
    };

    @Override
    public Object clone() {
        return new TPoint(x, y, z);
    }
}
