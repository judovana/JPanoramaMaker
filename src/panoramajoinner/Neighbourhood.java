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

import java.util.ArrayList;
import java.util.Collections;

public class Neighbourhood {

    public int x, y;
    private ArrayList<Integer> allX;
    private ArrayList<Integer> allY;
    public static boolean enchantedEdges = true;

    public Neighbourhood() {
        x = 0;
        y = 0;
        //   z=0;
    }

    public Neighbourhood(int x, int y) {
        this.x = x;
        this.y = y;
        //this.z=0;
    }

    public void setAllX(ArrayList<Integer> x) {
        allX = (ArrayList<Integer>) x.clone();
        Collections.sort(allX);
        if (!enchantedEdges) {
            Collections.reverse(allX);
        }

        if (!enchantedEdges) {
            while (allX.size() > 1) {
                allX.remove(1);
            }
        }
        //this.x=allX.get(0).intValue();
        this.x = allX.get(allX.size() - 1).intValue();
    }

    public void setAllY(ArrayList<Integer> y) {
        allY = (ArrayList<Integer>) y.clone();
        Collections.sort(allY);
        if (!enchantedEdges) {
            Collections.reverse(allY);
        }

        if (!enchantedEdges) {
            while (allY.size() > 1) {
                allY.remove(1);
            }
        }
        //this.y=allY.get(0).intValue();
        this.y = allY.get(allY.size() - 1).intValue();
    }

    public ArrayList<Integer> getAllX() {
        return allX;
    }

    public ArrayList<Integer> getAllY() {
        return allY;
    }
}
