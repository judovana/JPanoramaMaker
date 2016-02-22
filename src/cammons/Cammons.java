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


package cammons;

import java.io.File;

public class Cammons {

    /** Creates a new instance of Cammons */
    public static final int OS_WIN = 2;
    public static final int OS_LINUX = 1;
    public static final int OS_UNDEFINED = -1;

    public Cammons() {
    }

    static final public String extractFilePath(String s) {

        int i = s.lastIndexOf(File.separator);
        return s.substring(0, i + 1);

    }

    static final public String extractFileName(String s) {

        int i = s.lastIndexOf(File.separator);
        return s.substring(i + 1, s.length());

    }

    public static final String deleteSuffix(String file) {
        int x = file.length();
        while (x >= 0) {
            x--;
            if (file.charAt(x) == '.') {
                return file.substring(0, x);

            }
            if (file.charAt(x) == File.separatorChar) {
                return file;
            }
        }
        return null;
    }

    public static final String getSuffix(String file) {
        if (file == null) {
            return null;
        }
        int x = file.length();
        x--;
        while (x >= 0) {

            if (file.charAt(x) == '.') {
                return file.substring(x + 1, file.length());

            }
            if (file.charAt(x) == File.separatorChar) {
                return file;
            }
            x--;
        }
        return null;
    }

    public static double vzdal(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public static int getOs() {
        String s = System.getProperty("os.name").toUpperCase();
        if (s.indexOf("WIN") >= 0) {
            return OS_WIN;
        } else if (s.indexOf("LINUX") >= 0 || s.indexOf("UNIX") >= 0 || s.indexOf("SOLARIS") >= 0) {
            return OS_LINUX;
        } else {
            return OS_UNDEFINED;
        }
    }
}

    

    

