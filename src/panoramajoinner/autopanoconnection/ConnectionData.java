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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ConnectionData {

    ArrayList<MatchPoint> data;

    public ArrayList<MatchPoint> getData() {
        return data;
    }

    ConnectionData(File output) throws IOException {
        data = loadFromFile(output);
    }

    private ArrayList<MatchPoint> loadFromFile(File output) throws IOException {
        return loadFromReader(new FileReader(output));
    }

    private ArrayList<MatchPoint> loadFromReader(FileReader r) throws IOException {
        ArrayList<MatchPoint> result = new ArrayList<MatchPoint>();
        BufferedReader br = new BufferedReader(r);
        try {
            String s = br.readLine();
            int line = 1;
            while (s != null) {
                if (line > 13 && line < 24) {
                    if (!s.trim().equals("") && s.indexOf("#") < 0) {
                        result.add(new MatchPoint(s.split(" ")));
                    }
                }
                s = br.readLine();
                line++;
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            br.close();
        }
        return result;
    }
}
