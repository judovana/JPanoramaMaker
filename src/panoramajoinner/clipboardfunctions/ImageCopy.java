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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;

public class ImageCopy {

    public static void main(String args[]) {

        JFrame frame = new JFrame("Copy Image");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = frame.getContentPane();

        Toolkit kit = Toolkit.getDefaultToolkit();
        final Clipboard clipboard =
                kit.getSystemClipboard();

        Icon icon = new ImageIcon("scott.gif");
        final JLabel label = new JLabel(icon);
        label.setTransferHandler(new ImageSelection());

        JScrollPane pane = new JScrollPane(label);
        contentPane.add(pane, BorderLayout.CENTER);

        JButton copy = new JButton("Label Copy");
        copy.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                TransferHandler handler =
                        label.getTransferHandler();
                handler.exportToClipboard(label, clipboard,
                        TransferHandler.COPY);
            }
        });

        JButton clear = new JButton("Label Clear");
        clear.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                label.setIcon(null);
            }
        });

        JButton paste = new JButton("Label Paste");
        paste.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                Transferable clipData =
                        clipboard.getContents(clipboard);
                if (clipData != null) {
                    if (clipData.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                        TransferHandler handler =
                                label.getTransferHandler();
                        handler.importData(label, clipData);
                    }
                }
            }
        });

        JPanel p = new JPanel();
        p.add(copy);
        p.add(clear);
        p.add(paste);
        contentPane.add(p, BorderLayout.NORTH);

        JPanel pasteP = new JPanel();
        JButton pasteB = new JButton("Paste");

        pasteB.setTransferHandler(new ImageSelection());

        pasteB.addActionListener(TransferHandler.getPasteAction());

        pasteP.add(pasteB);
        contentPane.add(pasteB, BorderLayout.SOUTH);

        frame.setSize(400, 400);
        frame.show();
    }
}
