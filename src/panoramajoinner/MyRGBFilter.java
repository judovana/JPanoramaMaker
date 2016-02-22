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

public class MyRGBFilter extends java.awt.image.RGBImageFilter {

    /** Creates a new instance of MyRGBFilter */
    public MyRGBFilter() {
    }
    private int leftB, rightB;
    private int topC, bottomC;
    private int imageHeight;

    public void setS1(int s1) {
        this.leftB = s1;
    }

    public void setS2(int s2) {
        this.rightB = s2;
    }

    public void setImwid(int imwid) {
        this.imwid = imwid;
    }
    private int imwid;

    public void setBottomC(int bottomC) {
        this.bottomC = bottomC;
    }

    public void setTopC(int topC) {
        this.topC = topC;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int filterRGB(int x, int y, int pixel) {

        int r = (pixel >> 16) & 0xff;
        int g = (pixel >> 8) & 0xff;
        int b = pixel & 0xff;
        //int avg = (r+g+b)/3;
        int verticalCorrection = makeVerticalCorrection(imageHeight, y, topC, bottomC);
        int correctedS1 = addCarefully(leftB, verticalCorrection);
        int correctedS2 = addCarefully(rightB, verticalCorrection);
        int d = ImagePaintComponent.divers(correctedS1, correctedS2, imwid, x);

        r = ImagePaintComponent.pricti(d, r);
        g = ImagePaintComponent.pricti(d, g);
        b = ImagePaintComponent.pricti(d, b);

        return (0xff << 24) | (r << 16) | (g << 8) | b;
    }

    private int addCarefully(int a, int b) {
        int mezi = a + b;
        if (mezi < -255) {
            return -255;
        }
        if (mezi > 255) {
            return 255;
        }
        return mezi;
    }

    private int makeVerticalCorrection(int height, int yPosition, int top, int bottom) {
        //yposition =0 => top value
        //yposition =height => bottom value

        /*
         * Ax+b=c
         * ...
         *
         * 0*a+b=top
         * height*a+b=bottom
         *
         * height*a=bottom-top
         */


        double b = top;
        double a = (double) (bottom - top) / (double) (height - 1);
        return (int) (a * (double) yPosition + b);

    }

    public static void main(String[] args) {
        MyRGBFilter m = new MyRGBFilter();
        int height = 156;
        int odv = 0;
        int dov = 0;
        for (int y = 0; y < 156; y++) {
            System.out.println(m.makeVerticalCorrection(height, y, odv, dov));
        }
    }
}
