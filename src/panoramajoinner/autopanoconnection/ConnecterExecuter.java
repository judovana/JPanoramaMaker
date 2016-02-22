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

import java.io.File;
import java.io.IOException;

public class ConnecterExecuter implements Runnable {

    private File output;
    private File f1;
    private File f2;
    public boolean finito = false;
    private Process p = null;

    public ConnecterExecuter(File output, File f1, File f2) {
        this.output = output;
        this.f1 = f1;
        this.f2 = f2;
    }

    public void run() {

        try {

            String dir = System.getProperty("user.dir") + File.separator + "panoTools" + File.separator;
            ProcessBuilder pb = new ProcessBuilder(dir + "matchpoint-complete.exe", "--output", output.getAbsolutePath(), f1.getAbsolutePath(), f2.getAbsolutePath());
            pb = pb.redirectErrorStream(true);
            pb.directory(new File(dir));

            p = pb.start();
            //OutputReader or= new OutputReader(p.getErrorStream());
            // new Thread(or).start();
            p.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            finito = true;
            try {
                //  clean();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void clean() {
        try {
            p.getInputStream().close();
        } catch (Exception ex) {
        } finally {
            try {
                p.getOutputStream().close();
            } catch (Exception ex) {
            } finally {
                try {
                    p.getErrorStream().close();
                } catch (Exception ex) {
                } finally {
                }
            }
        }
    }

    @Override
    public String toString() {
        if (p != null) {
            return p.toString();
        } else {
            return "no p:(";
        }
    }

    public Process getProcess() {
        return p;
    }
}
