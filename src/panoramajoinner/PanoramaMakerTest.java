package panoramajoinner;
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

import cammons.Cammons;
import cammons.ImgFileFilter;
import cammons.TPoint;
import horizontdeformer.HorizontDeformerWindow;
import panoramajoinner.ImagePaintComponent;
import panoramajoinner.clipboardfunctions.ImageSelection;
import java.awt.Graphics2D;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import javax.swing.JFileChooser;

import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.TransferHandler;

/**
 *
 * @author  Jirka
 */
public class PanoramaMakerTest {

   

    /** Creates new form NewJFrame */
    public PanoramaMakerTest() {
    }



  
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

     
               java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

try{               HorizontDeformerWindow     hd = new HorizontDeformerWindow();
                    PanoramaMaker pa = new PanoramaMaker(new LinkedList<File>(),null);
                    hd.setPaWindow(pa);
                    //jDialog1.setVisible(true);
                    hd.setVisible(true);
                    pa.setVisible(true);
}catch(Throwable ex){
ex.printStackTrace();
System.exit(1);
}
System.exit(0);

            }
        });
    }

  
}
