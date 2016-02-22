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

import java.awt.image.BufferedImage;
import java.awt.Color;

public class Matrix {

    int precision = 10;
    double tolerance;
    private int sizex, sizey;
    private int[] pole;

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getPrecision() {
        return precision;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    /** Creates a new instance of Matrix */
    public Matrix(int sizex, int sizey) {
        this.sizex = sizex;
        this.sizey = sizey;
        pole = new int[sizex * sizey];

    }

    public void writeData(int x, int y, BufferedImage im) {
        im.setRGB(x, y, sizex, sizey, pole, 0, sizex);

    }

    public void readData(int x, int y, BufferedImage im) {
        im.getRGB(x, y, sizex, sizey, pole, 0, sizex);
        /*     set(0,1,-16777216);
        set(0,2,-16777216);
        set(0,3,-16777216);
        set(0,4,-16777216);
        set(0,5,-16777216);
        im.setRGB(x,y,sizex,sizey,pole,0,sizex);*/
    }

    public int get(int x, int y) {

        return pole[y * sizex + x];
    }

    public void set(int x, int y, int value) {
        pole[y * sizex + x] = value;
    }

    public int getUpperTop() {
        int y = 0;
        while (isNothing(new Color(get(0, y)))) {

            y++;
            if (y >= sizey) {
                y = -1;
                break;
            }
        }
        return y;
    }

    public int getLowerTop() {
        int y = sizey - 1;
        while (isNothing(new Color(get(0, y)))) {

            y--;
            if (y < 0) {
                y = -1;
                break;
            }
        }
        return y;
    }

    private boolean isNothing(Color c) {
        if (c.getRed() >= 255 - precision && c.getGreen() >= 255 - precision && c.getBlue() >= 255 - precision) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isSomething(Color c) {
        if (c.getRed() < 255 - precision && c.getGreen() < 255 - precision && c.getBlue() < 255 - precision) {
            return true;
        } else {
            return false;
        }
    }
}
