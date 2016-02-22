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
package horizontdeformer.cammons;

import cammons.TPoint;

public class OrientedCross implements Comparable {

    /** Creates a new instance of OrientedCross */
    private int x, y, orientation, diff;
    private int compareBy;
    public static final int UP = -1;
    public static final int DOWN = +1;
    public static final int NONE = 0;
    public static final int COMPARE_BY_X = 1;
    public static final int COMPARE_BY_Y = 2;

    public int getDiff() {
        return diff;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setDiff(int diff) {
        this.diff = diff;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setCompareBy(int compareBy) {
        this.compareBy = compareBy;
    }

    public int getCompareBy() {
        return compareBy;
    }

    public int compareTo(Object oo) {
        OrientedCross o = (OrientedCross) oo;
        switch (compareBy) {
            case (OrientedCross.COMPARE_BY_Y):
                return this.getY() - o.getY();
            default:
                return this.getX() - o.getX();


        }
    }

    public OrientedCross(TPoint p) {
        x = p.x;
        y = p.y;
        orientation = (int) Math.signum(p.z);
        diff = Math.abs(p.z);
        compareBy = COMPARE_BY_X;

    }
}
