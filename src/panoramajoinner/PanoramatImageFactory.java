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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;


public class PanoramatImageFactory {

    public static PanoramatImage createFromStreamOlder(BufferedReader f) throws IOException {
        String s = f.readLine();
        if (s == null) {
            return null;
        }
        File file = new File(s);
        if (file.exists()) {
            PanoramatImage p = new PanoramatImage(file);
            s = f.readLine();
            p.setLeftBrightnes(Integer.valueOf(s));
            s = f.readLine();
            p.setRightBrightnes(Integer.valueOf(s));
            s = f.readLine();
            p.setLeftAlpha(Integer.valueOf(s));
            s = f.readLine();
            p.setRightAlpha(Integer.valueOf(s));
            s = f.readLine();
            p.setBotomBrightnes(Integer.valueOf(s));
            s = f.readLine();
            p.setTopBrightnes(Integer.valueOf(s));
            s = f.readLine();
            p.setLeft(Integer.valueOf(s));
            s = f.readLine();
            p.setTop(Integer.valueOf(s));
            return p;
        } else {
            if (file != null) {
                System.out.println(file.toString() + " dont exists. check and repair the path in loaded file");
            }
            for (int i = 0; i < 8; i++) {
                f.readLine();
            }
            return new PanoramatImage();
        }
    }

    public static PanoramatImage createFromStream4(BufferedReader f) throws IOException {
        String s = f.readLine();
        if (s == null) {
            return null;
        }
        File file = new File(s);
        if (file.exists()) {
            PanoramatImage p = new PanoramatImage(file);
            s = f.readLine();
            p.setLeftBrightnes(Integer.valueOf(s));
            s = f.readLine();
            p.setRightBrightnes(Integer.valueOf(s));
            s = f.readLine();
            p.setLeftAlpha(Integer.valueOf(s));
            s = f.readLine();
            p.setRightAlpha(Integer.valueOf(s));
            s = f.readLine();
            p.setBotomBrightnes(Integer.valueOf(s));
            s = f.readLine();
            p.setTopBrightnes(Integer.valueOf(s));
            s = f.readLine();
            p.setLeft(Integer.valueOf(s));
            s = f.readLine();
            p.setTop(Integer.valueOf(s));

            s = f.readLine();//***affine transforms***

            s = f.readLine();
            p.rotate = Double.valueOf(s);
            s = f.readLine();
            p.scalex = Double.valueOf(s);
            s = f.readLine();
            p.scaley = Double.valueOf(s);
            s = f.readLine();
            p.sharey = Double.valueOf(s);
            s = f.readLine();
            p.sharex = Double.valueOf(s);
            s = f.readLine();
            p.afw = Integer.valueOf(s);
            s = f.readLine();
            p.afh = Integer.valueOf(s);
            s = f.readLine();
            p.afx = Integer.valueOf(s);
            s = f.readLine();
            p.afy = Integer.valueOf(s);

            s = f.readLine();//***end of...***

            return p;
        } else {
            if (file != null) {
                System.out.println(file.toString() + " dont exists. check and repair the path in loaded file");
            }
            for (int i = 0; i < 19/*?*/; i++) {
                f.readLine();
            }
            return new PanoramatImage();
        }
    }

    public static PanoramatImage createFromStream5(BufferedReader f) throws IOException {
        String s = f.readLine();
        if (s == null) {
            return null;
        }
        File file = new File(s);
        if (file.exists()) {
            PanoramatImage p = new PanoramatImage(file);
            s = f.readLine();
            p.setLeftBrightnes(Integer.valueOf(s));
            s = f.readLine();
            p.setRightBrightnes(Integer.valueOf(s));
            s = f.readLine();
            p.setLeftAlpha(Integer.valueOf(s));
            s = f.readLine();
            p.setRightAlpha(Integer.valueOf(s));
            s = f.readLine();
            p.setBotomBrightnes(Integer.valueOf(s));
            s = f.readLine();
            p.setTopBrightnes(Integer.valueOf(s));
            ///
            s = f.readLine();
            p.setTopAlpha(Integer.valueOf(s));
            s = f.readLine();
            p.setBottomAlpha(Integer.valueOf(s));
            s = f.readLine();
            //p.setTopJitter(Integer.valueOf(s));//temporaly disapbled
            s = f.readLine();
            //p.setBottomJitter(Integer.valueOf(s));//temporaly disabled
            //
            s = f.readLine();
            p.setLeft(Integer.valueOf(s));
            s = f.readLine();
            p.setTop(Integer.valueOf(s));

            s = f.readLine();//***affine transforms***

            s = f.readLine();
            p.rotate = Double.valueOf(s);
            s = f.readLine();
            p.scalex = Double.valueOf(s);
            s = f.readLine();
            p.scaley = Double.valueOf(s);
            s = f.readLine();
            p.sharey = Double.valueOf(s);
            s = f.readLine();
            p.sharex = Double.valueOf(s);
            s = f.readLine();
            p.afw = Integer.valueOf(s);
            s = f.readLine();
            p.afh = Integer.valueOf(s);
            s = f.readLine();
            p.afx = Integer.valueOf(s);
            s = f.readLine();
            p.afy = Integer.valueOf(s);

            s = f.readLine();//***end of...***

            return p;
        } else {
            if (file != null) {
                System.out.println(file.toString() + " dont exists. check and repair the path in loaded file");
            }
            for (int i = 0; i < 23/*?*/; i++) {
                f.readLine();
            }
            return new PanoramatImage();
        }
    }
}
