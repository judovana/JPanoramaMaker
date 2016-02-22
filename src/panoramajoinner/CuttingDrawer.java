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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;


public class CuttingDrawer extends JComponent {
    private BufferedImage i;
    private AffineTransform primary;
    private AffineTransform at;
    private double zoom;
    private int w,h,xx,yy;
    
    

    public int getH() {
        return h;
    }

    public int getXX() {
        return xx;
    }

    public int getW() {
        return w;
    }

    public int getYY() {
        return yy;
    }





    
    public void setXX(int x) {
        this.xx = x;
    }

    public void setH(int h) {
        this.h = h;
    }

    public void setYY(int y) {
        this.yy = y;
    }

    public void setW(int h) {
        this.w = h;
        
        
    }
    

    

    




    public void setZoom(double zoom) {
        this.zoom = zoom;
    }


    /** Creates a new instance of CuttingDrawer */
    public CuttingDrawer(BufferedImage bi,AffineTransform af) {
        i=bi;
        primary=af;
        
    }
    
    
    public void paint(Graphics g){
        Graphics2D g2d=(Graphics2D)g;
        AffineTransform aff=new AffineTransform();
       aff.translate((double)(this.getParent().getWidth()-(int)(((double)w*zoom)))/2d,
                     (double)(this.getParent().getHeight()-(int)(((double)h*zoom)))/2d);
        
        AffineTransform af=setAfineTransform(primary);
        
        g2d.transform(aff);        
        
        g2d.drawImage(i,af,null);
        g2d.setColor(Color.red);
        g2d.drawRect(0,0,(int)((double)w*zoom),(int)((double)h*zoom));
        
    }
    
    public void paintComponent(Graphics g){
        paint(g);
    }
    
    private AffineTransform setAfineTransform(AffineTransform axf) {
        AffineTransform vysledek=null;
        if (axf==null) return null;else{
        vysledek=new AffineTransform();
                        
            vysledek.scale(zoom,zoom);
            vysledek.translate((double)(xx),(double)(yy));
                          
        vysledek.concatenate(axf);
        return vysledek;
            
        }
    }

    public double getZoom() {
        return zoom;
        
    }
}
