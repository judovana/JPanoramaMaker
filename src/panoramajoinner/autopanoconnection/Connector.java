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
package panoramajoinner.autopanoconnection;

import panoramajoinner.PanoramatImage;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;

public class Connector {

    private File f1;
    private File f2;
    private File k1;
    private File k2;
    private File output;
    private ConnectionData result;
    private AffineTransform af1;
    private AffineTransformOp afop1;
    private AffineTransform af2;
    private AffineTransformOp afop2;

    public void rotatePoint(MatchPoint m, boolean revers) {
        try {
            Point2D src1 = new Point2D.Double(m.x1, m.y1);
            Point2D src2 = new Point2D.Double(m.x2, m.y2);
            Point2D dest1 = new Point2D.Double();
            Point2D dest2 = new Point2D.Double();
            if (!revers) {
                af1.inverseTransform(src1, dest1);
                af2.inverseTransform(src2, dest2);
            } else {
                af2.inverseTransform(src1, dest1);
                af1.inverseTransform(src2, dest2);
            }
            m.x1 = (int) dest1.getX();
            m.y1 = (int) dest1.getY();
            m.x2 = (int) dest2.getX();
            m.y2 = (int) dest2.getY();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Connector(PanoramatImage p1, PanoramatImage p2, boolean rotate90upright) throws IOException {
        try {
            double ang = -Math.PI / 2;

            af1 = AffineTransform.getTranslateInstance(0, p1.getWidth());
            af1.concatenate(AffineTransform.getRotateInstance(ang));
            afop1 = new AffineTransformOp(af1, AffineTransformOp.TYPE_BICUBIC);

            af2 = AffineTransform.getTranslateInstance(0, p2.getWidth());
            af2.concatenate(AffineTransform.getRotateInstance(ang));
            afop2 = new AffineTransformOp(af2, AffineTransformOp.TYPE_BICUBIC);



            f1 = File.createTempFile("jpm", p1.getSrc().getName());
            f1.deleteOnExit();
            f2 = File.createTempFile("jpm", p2.getSrc().getName());
            f2.deleteOnExit();
            BufferedImage image1OP = p1.getImage();
            BufferedImage image2OP = p2.getImage();
            if (rotate90upright) {
                image1OP = afop1.filter(image1OP, new BufferedImage(image1OP.getHeight(), image1OP.getWidth(), image1OP.getType()));

                image2OP = afop1.filter(image2OP, new BufferedImage(image2OP.getHeight(), image2OP.getWidth(), image2OP.getType()));
            }
            ImageIO.write(image1OP, cammons.Cammons.getSuffix(f1.getName()), f1);
            ImageIO.write(image2OP, cammons.Cammons.getSuffix(f2.getName()), f2);
            k1 = new File(f1.getAbsolutePath() + ".key");
            k2 = new File(f2.getAbsolutePath() + ".key");
            k1.deleteOnExit();
            k2.deleteOnExit();
            output = File.createTempFile("jpm", "output.txt");
            output.deleteOnExit();
        } catch (IOException ex) {
            clean();
            throw ex;
        }

    }

    public void clean() {
        f1.delete();
        f2.delete();
        // output.delete();

        k1.delete();
        k2.delete();

    }

    public ConnectionData generate() throws IOException {
        try {

            ConnecterExecuter ce = new ConnecterExecuter(output, f1, f2);

            Thread t = new Thread(ce);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
            Thread.sleep(500);
            System.out.println("started process:(" + ce.toString());
            readProcess(ce.getProcess());
            int i = 0;
            while (!ce.finito) {
                Thread.sleep(500);
                i++;
                if (i > 80) {
                    t.stop();
                    ce.clean();
                    System.out.println("killing process:(");
                }
            }
            Thread.sleep(500);
            result = new ConnectionData(output);
            ce.clean();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        Connector c = null;
        ConnectionData d = null;
        try {
            File f1 = new File("D:\\fotky\\norsko2007\\prepare\\panoramatas\\sources\\IMG_0198.jpg");
            File f2 = new File("D:\\fotky\\norsko2007\\prepare\\panoramatas\\sources\\IMG_0199.jpg");
            c = new Connector(new PanoramatImage(f1), new PanoramatImage(f2), false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (c != null) {
            try {
                d = c.generate();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                c.clean();
            }
        }
        if (d != null) {
            ArrayList<MatchPoint> a = d.getData();
            for (Iterator<MatchPoint> it = a.iterator(); it.hasNext();) {
                MatchPoint mp = it.next();
                System.out.println(mp);

            }

        }

    }

    private void readProcess(Process process) throws IOException {
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

    }
}
