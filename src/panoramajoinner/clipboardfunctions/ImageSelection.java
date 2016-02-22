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
package panoramajoinner.clipboardfunctions;

import panoramajoinner.PanoramaMaker;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.TransferHandler;

public class ImageSelection extends TransferHandler
        implements Transferable {

    private static final DataFlavor flavors[] = {DataFlavor.imageFlavor};
    private Image image;
    private PanoramaMaker frame;

    public ImageSelection(PanoramaMaker aThis) {
        super();
        this.frame = aThis;
    }

    ImageSelection() {
        super();
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor flavor[]) {
        if (!(comp instanceof JLabel)
                || (comp instanceof AbstractButton)) {
            return false;
        }
        for (int i = 0, n = flavor.length; i < n; i++) {
            if (flavor[i].equals(flavors[0])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Transferable createTransferable(JComponent comp) {
// Clear
        image = null;
        Icon icon = null;

        if (comp instanceof JLabel) {
            JLabel label = (JLabel) comp;
            icon = label.getIcon();
        } else if (comp instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) comp;
            icon = button.getIcon();
        }
        if (icon instanceof ImageIcon) {
            image = ((ImageIcon) icon).getImage();
            return this;
        }
        return null;
    }

    public boolean importData(JComponent comp, Transferable t) {
        ImageIcon icon = null;
        try {
            if (t.isDataFlavorSupported(flavors[0])) {
                image = (Image) t.getTransferData(flavors[0]);
                icon = new ImageIcon(image);
            }
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                label.setIcon(icon);
                return true;
            } else if (comp instanceof AbstractButton) {
//AbstractButton button = (AbstractButton)comp;
//button.setIcon(icon);
                frame.processFromCLipboard((BufferedImage) image);
                return true;
            }

        } catch (UnsupportedFlavorException ignored) {
        } catch (IOException ignored) {
        }
        return false;
    }

// Transferable
    public Object getTransferData(DataFlavor flavor) {
        if (isDataFlavorSupported(flavor)) {
            return image;
        }
        return null;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(flavors[0]);
    }
}
