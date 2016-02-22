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
package horizontdeformer.graphicsoutputs;

import horizontdeformer.cammons.OrientedCross;
import java.awt.Graphics;

public class GraphicsAdds {

    /** Creates a new instance of Arrow */
    public GraphicsAdds() {
    }

    public static void Arrow(Graphics g, int x, int y, int orientation, int w) {

        int ww = w / 3;
        switch (orientation) {
            case (OrientedCross.DOWN):
                g.drawLine(x, y, x, y + w);
                g.drawLine(x + ww, y + w - ww, x, y + w);
                g.drawLine(x - ww, y + w - ww, x, y + w);
                break;
            case (OrientedCross.UP):
                g.drawLine(x, y, x, y - w);
                g.drawLine(x - ww, y - w + ww, x, y - w);
                g.drawLine(x + ww, y - w + ww, x, y - w);

                break;
        }

    }

    public static void Cross(Graphics g, int x, int y, int w) {


        g.drawLine(x - w, y, x + w, y);
        g.drawLine(x, y - w, x, y + w);
    }
}
