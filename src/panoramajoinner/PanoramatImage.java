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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PanoramatImage {

    /** Creates a new instance of PpanoramtImage */
    private BufferedImage im;
    private File src;
    private int leftBrightnes = 0, rightBrightnes;
    private int topBrightnes = 0, botomBrightnes;
    private int leftAlpha = 80, rightAlpha = 80;
    //private int leftJitter=0;
    //private int rightJitter=0;
    private int left = 0;
    private int top = 0;
    //verzion 4
    double rotate;
    double scalex = 1;
    double scaley = 1;
    double sharey;
    double sharex;
    int afw;
    int afh;
    int afx;
    int afy;
    //verzion 5
    private int topAlpha = 80, bottomAlpha = 80;
    //private int topJitter=0;
    //private int bottomJitter=0;

    public int getRightAlpha() {
        return rightAlpha;
    }

    public int getRightBrightnes() {
        return rightBrightnes;
    }

    public void setTopBrightnes(int topBrightnes) {
        this.topBrightnes = topBrightnes;
    }

    public int getTopBrightnes() {
        return topBrightnes;
    }

    public void setBotomBrightnes(int botomBrightnes) {
        this.botomBrightnes = botomBrightnes;
    }

    public int getBotomBrightnes() {
        return botomBrightnes;
    }

    public File getSrc() {
        return src;
    }

    public void setLeftAlpha(int leftAlpha) {
        this.leftAlpha = leftAlpha;
    }

    public int setLeftBrightnes(int leftBrightnes) {
        int old = this.leftBrightnes;
        this.leftBrightnes = leftBrightnes;
        return old;
    }

    public void setRightAlpha(int rightAlpha) {
        this.rightAlpha = rightAlpha;
    }

    public int setRightBrightnes(int rightBrightnes) {
        int old = this.rightBrightnes;
        this.rightBrightnes = rightBrightnes;
        return old;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public int getLeft() {
        return left;
    }

    public int getLeftBrightnes() {
        return leftBrightnes;
    }

    public void setBottomAlpha(int bottomAlpha) {
        this.bottomAlpha = bottomAlpha;
    }

    public void setTopAlpha(int topAlpha) {
        this.topAlpha = topAlpha;
    }

    public int getBottomAlpha() {
        return bottomAlpha;
    }

    public int getTopAlpha() {
        return topAlpha;
    }

    public int getLeftAlpha() {
        return leftAlpha;
    }

    public BufferedImage getImage() {
        return im;
    }

    public PanoramatImage() {
        src = null;
    }

    public PanoramatImage(File f) {
        try {
            loadImage(f);

            this.src = f;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void reload() {
        try {
            loadImage(src);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public int getWidth() {
        return im.getWidth();
    }

    public int getHeight() {
        return im.getHeight();
    }

    void write(BufferedWriter f) throws IOException {
        f.write(src.toString());
        f.newLine();
        f.write(String.valueOf(leftBrightnes));
        f.newLine();
        f.write(String.valueOf(rightBrightnes));
        f.newLine();
        f.write(String.valueOf(leftAlpha));
        f.newLine();
        f.write(String.valueOf(rightAlpha));
        f.newLine();
        f.write(String.valueOf(botomBrightnes));
        f.newLine();
        f.write(String.valueOf(topBrightnes));
        f.newLine();

        f.write(String.valueOf(topAlpha));
        f.newLine();
        f.write(String.valueOf(bottomAlpha));
        f.newLine();
        f.write(String.valueOf(0) + "tempraly disabled");
        f.newLine();
        f.write(String.valueOf(0) + "tempraly disabled");
        f.newLine();

        f.write(String.valueOf(left));
        f.newLine();
        f.write(String.valueOf(top));
        f.newLine();
        f.write("***affine transformations***");
        f.newLine();

        f.write(String.valueOf(rotate));
        f.newLine();
        f.write(String.valueOf(scalex));
        f.newLine();
        f.write(String.valueOf(scaley));
        f.newLine();
        f.write(String.valueOf(sharey));
        f.newLine();
        f.write(String.valueOf(sharex));
        f.newLine();
        f.write(String.valueOf(afw));
        f.newLine();
        f.write(String.valueOf(afh));
        f.newLine();
        f.write(String.valueOf(afx));
        f.newLine();
        f.write(String.valueOf(afy));
        f.newLine();


        f.write("***end of affine transformations and of one record***");
        f.newLine();
    }

    private void loadImage(File f) throws IOException {
        BufferedImage i = ImageIO.read(f);
        im = new BufferedImage(i.getWidth(), i.getHeight(), BufferedImage.TYPE_INT_RGB);
        im.getGraphics().drawImage(i, 0, 0, null);

    }

    void setImage(Image img) {
        im.getGraphics().drawImage(img, 0, 0, null);
    }

    void setImage(BufferedImage img) {
        im = img;
    }

    public boolean containsGlobalPixel(int x, int y) {
        if (x >= getLeft() && x < getLeft() + getWidth() && y >= getTop() && y < getTop() + getHeight()) {
            return true;
        }
        return false;
    }
}

