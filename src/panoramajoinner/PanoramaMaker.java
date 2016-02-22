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

import cammons.AllFileFilter;
import cammons.Cammons;
import cammons.ImgFileFilter;
import cammons.MyFileChooser;
import cammons.TPoint;
import horizontdeformer.HorizontDeformerWindow;
import java.util.List;
import java.util.Observable;
import panoramajoinner.clipboardfunctions.ImageSelection;
import java.awt.Graphics2D;
import java.awt.Point;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;

import javax.swing.JFileChooser;

import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

/**
 *
 * @author  Jirka
 */
public class PanoramaMaker extends javax.swing.JFrame implements Observer {

    private static HorizontDeformerWindow hd;

    private static void setDirAndFiles(File f, List<File> files, String string, File[] lastDir) {
        if (string.trim().length() > 0) {
            if (f.exists()) {
                if (f.isDirectory()) {
                    lastDir[0] = f;
                    System.out.println("Settign working dir to " + f.getAbsolutePath());
                } else {
                    files.add(f.getAbsoluteFile());
                    System.out.println("adding " + f.getAbsolutePath() + " to processed files");
                }
            } else {
                System.out.println(f.getAbsolutePath() + " declared as " + string + " do not exists");
            }
        }
    }
    private MyFileChooser jFch = new MyFileChooser();
    private ImagePaintComponent p = new ImagePaintComponent();
    private boolean tahne = false;
    private List<TPoint> save = new ArrayList<TPoint>();
    private TPoint old = new TPoint();
    private SpinnerNumberModel zoommodel = new SpinnerNumberModel(1d, 0d, 1000d, 0.05d);
    private Timer timer;
    private int xadd = 0, yadd = 0;
    private javax.swing.ButtonGroup buttonGroupOneAll, buttonGroupLEftRightBoth;
    private BufferedImage kam;
    private boolean dontchange = false;
    private javax.swing.ButtonGroup buttonGroupOverUnder, buttonGroupAvgMedMost;
    private boolean edgeLinesFollow=false;

    /** Creates new form NewJFrame */
    public PanoramaMaker(List<File> files, File wDir) {
        p.observableMe.addObserver(this);
        initComponents();
        if (Cammons.getOs() == Cammons.OS_WIN) {
            jMenu1.setEnabled(true);
        } else {
            jMenu1.setEnabled(false);
        }
        Toolkit kit = Toolkit.getDefaultToolkit();
        final Clipboard clipboard = kit.getSystemClipboard();
        jButton18.setTransferHandler(new ImageSelection(this));
        jButton18.addActionListener(TransferHandler.getPasteAction());


        if (p != null && hd != null) {
            p.setProgress(hd.jProgressBar1);
        }
        jPanel6.add(p);
        jDialog1.pack();
        jDialog1.validate();
        jDialog2.setSize(250, 300);
        jDialog8.setSize(250, 300);
        jDialog3.setSize(100, 100);
        jDialog4.setSize(500, 400);
        jDialog6.setSize(300, 300);
        jDialog7.setSize(800, 600);
        jDialog9.setSize(200, 200);
        jDialog9.setLocation(300, 300);

        this.setSize(800, 600);
        jDialog1.setVisible(true);
        jSpinner6.setModel(zoommodel);
        buttonGroup1.add(jRadioButton1);
        buttonGroup1.add(jRadioButton2);

        ButtonGroup buttonGroupForcing = new ButtonGroup();
        buttonGroupForcing.add(forceV);
        buttonGroupForcing.add(forceH);
        buttonGroupForcing.add(forceNone);
        
        buttonGroupOneAll = new javax.swing.ButtonGroup();
        buttonGroupLEftRightBoth = new javax.swing.ButtonGroup();
        buttonGroupOverUnder = new javax.swing.ButtonGroup();
        buttonGroupAvgMedMost = new javax.swing.ButtonGroup();

        buttonGroupOneAll.add(jRadioButton3);
        buttonGroupOneAll.add(jRadioButton4);

        buttonGroupLEftRightBoth.add(jRadioButton7);
        buttonGroupLEftRightBoth.add(jRadioButton8);
        buttonGroupLEftRightBoth.add(jRadioButton9);

        buttonGroupOverUnder.add(jRadioButton5);
        buttonGroupOverUnder.add(jRadioButton6);

        buttonGroupAvgMedMost.add(jRadioButton10);
        buttonGroupAvgMedMost.add(jRadioButton11);
        buttonGroupAvgMedMost.add(jRadioButton12);

        jRadioButton1.setSelected(true);


        int delay = 100; //milliseconds
        ActionListener taskPerformer = new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                p.moveRightLeft(xadd);
                p.moveTopDown(yadd);
            }
        };
        timer = new javax.swing.Timer(delay, taskPerformer);
        timer.start();
        jTextArea2.setText(
                "JPanoramaMaker\n homepage: JPanoramaMaker.wz.cz \n  by Jiri Vanek -  judovana@email.cz \n This program is freeware. If you wnat source codes, write me why a nd I will send them back to you \n version 5.0"
                + "\n \n This program is part of project TripShare  - tripshare.cz"
                + "\n for which it serves also like tool for creating maps");
        jDialog5.setSize(600, 600);
        AboutIamge logo = new AboutIamge();
        logo.setParent(jPanel8);
        jPanel8.add(logo);

        /*

        try {
        p.loadImage(new File("D:\\fotky\\BlankAmatterhorn2007\\ja\\1\\panoramas\\122-4\\IMG_0122.jpg"),10000);
        p.loadImage(new File("D:\\fotky\\BlankAmatterhorn2007\\ja\\1\\panoramas\\122-4\\IMG_0123.jpg"),10000);
        p.loadImage(new File("D:\\fotky\\BlankAmatterhorn2007\\ja\\1\\panoramas\\122-4\\IMG_0124.jpg"),10000);
        } catch (IOException ex) {
        ex.printStackTrace();
        }
        //   p.loadFromFile(new File("D:\\xxx.txt"));
         */
        if (files.size() == 1) {
            try {
                p.loadFromFile(files.get(0));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            for (File file : files) {
                try {
                    p.loadImage(file, 10000);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (wDir != null) {
            jFch.setCurrentDirectory(wDir);
        }
    }

    public void processFromCLipboard(BufferedImage image) {
        try {
            File f = File.createTempFile("jpm", "fromClip.jpg");
            ImageIO.write(image, "jpg", f);
            p.loadImage(f, 1000000);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "error pasting image (no image in clipboard?)");
        }
        repaint();

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jButton6 = new javax.swing.JButton();
        forceNone = new javax.swing.JRadioButton();
        forceV = new javax.swing.JRadioButton();
        forceH = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jButton7 = new javax.swing.JButton();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        jButton21 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jSpinner21 = new javax.swing.JSpinner();
        jCheckBox9 = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jSpinner6 = new javax.swing.JSpinner();
        jButton12 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jCheckBox3 = new javax.swing.JCheckBox();
        jButton18 = new javax.swing.JButton();
        jLabel40 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton20 = new javax.swing.JButton();
        jDialog2 = new javax.swing.JDialog();
        jLabel2 = new javax.swing.JLabel();
        jScrollBar1 = new javax.swing.JScrollBar();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollBar2 = new javax.swing.JScrollBar();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jScrollBar3 = new javax.swing.JScrollBar();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jScrollBar4 = new javax.swing.JScrollBar();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jDialog8 = new javax.swing.JDialog();
        jLabel9 = new javax.swing.JLabel();
        jSpinner3 = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jSpinner4 = new javax.swing.JSpinner();
        jSpinner5 = new javax.swing.JSpinner();
        jLabel29 = new javax.swing.JLabel();
        jSpinner17 = new javax.swing.JSpinner();
        jSpinner18 = new javax.swing.JSpinner();
        jLabel30 = new javax.swing.JLabel();
        jSpinner19 = new javax.swing.JSpinner();
        jLabel31 = new javax.swing.JLabel();
        jSpinner20 = new javax.swing.JSpinner();
        jLabel32 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem20 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem19 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem25 = new javax.swing.JMenuItem();
        jMenuItem26 = new javax.swing.JMenuItem();
        jMenuItem27 = new javax.swing.JMenuItem();
        jMenuItem28 = new javax.swing.JMenuItem();
        jDialog3 = new javax.swing.JDialog();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jDialog4 = new javax.swing.JDialog();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jButton13 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jRadioButton9 = new javax.swing.JRadioButton();
        jRadioButton10 = new javax.swing.JRadioButton();
        jRadioButton11 = new javax.swing.JRadioButton();
        jRadioButton12 = new javax.swing.JRadioButton();
        jDialog5 = new javax.swing.JDialog();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jDialog6 = new javax.swing.JDialog();
        jLabel14 = new javax.swing.JLabel();
        jSpinner7 = new javax.swing.JSpinner();
        jLabel15 = new javax.swing.JLabel();
        jSpinner8 = new javax.swing.JSpinner();
        jLabel16 = new javax.swing.JLabel();
        jSpinner9 = new javax.swing.JSpinner();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jSpinner10 = new javax.swing.JSpinner();
        jSpinner11 = new javax.swing.JSpinner();
        jButton16 = new javax.swing.JButton();
        jCheckBox4 = new javax.swing.JCheckBox();
        jDialog7 = new javax.swing.JDialog();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jSpinner12 = new javax.swing.JSpinner();
        jLabel21 = new javax.swing.JLabel();
        jSpinner13 = new javax.swing.JSpinner();
        jLabel22 = new javax.swing.JLabel();
        jSpinner14 = new javax.swing.JSpinner();
        jLabel23 = new javax.swing.JLabel();
        jSpinner15 = new javax.swing.JSpinner();
        jLabel24 = new javax.swing.JLabel();
        jSpinner16 = new javax.swing.JSpinner();
        jPanel10 = new javax.swing.JPanel();
        jButton17 = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jDialog9 = new javax.swing.JDialog();
        jButton19 = new javax.swing.JButton();
        jSpinner22 = new javax.swing.JSpinner();
        jDialog10 = new javax.swing.JDialog();
        jDialog10.setSize(500, 300);
        sobeldirection = new javax.swing.JComboBox();
        jButton23 = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        tresholSpin = new javax.swing.JSpinner();
        enableSobel = new javax.swing.JCheckBox();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton5.setText("Clear all");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton4.setText("Load");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton3.setText("Save ");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton2.setText("Export");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setText("Add fotos");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jCheckBox1.setText("Edit perspective");
        jCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButton3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .add(jButton2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .add(jCheckBox1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .add(jButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .add(jButton4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCheckBox1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton5)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jRadioButton1.setText("Bigest");
        jRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        jRadioButton2.setText("Smallest");
        jRadioButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        jButton6.setText("Show rectangle");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        forceNone.setSelected(true);
        forceNone.setText("Do NOT force");
        forceNone.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        forceNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forceNonejRadioButton1ActionPerformed(evt);
            }
        });

        forceV.setText("force H");
        forceV.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        forceV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forceVActionPerformed(evt);
            }
        });

        forceH.setText("Force V");
        forceH.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        forceH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forceHjRadioButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jRadioButton1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRadioButton2)
                        .add(36, 36, 36)
                        .add(forceV)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(forceH)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(forceNone))
                    .add(jButton6))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButton1)
                    .add(jRadioButton2)
                    .add(forceV)
                    .add(forceH)
                    .add(forceNone))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton6)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setText("Max alpha");

        jSpinner1.setValue(new Integer(80));

        jButton7.setText("Set to all H");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jCheckBox2.setSelected(true);
        jCheckBox2.setText("blur edges");
        jCheckBox2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jCheckBox5.setSelected(true);
        jCheckBox5.setText("Vertical?");
        jCheckBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox5ActionPerformed(evt);
            }
        });

        jCheckBox6.setSelected(true);
        jCheckBox6.setText("Horizontal?");
        jCheckBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox6ActionPerformed(evt);
            }
        });

        jCheckBox7.setSelected(true);
        jCheckBox7.setText("vertical corners");

        jCheckBox8.setText("horizontal corners");

        jButton21.setText("Set to all V");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jButton24.setText("Edge lines follow");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSpinner1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                    .add(jLabel1)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jButton7)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jButton21)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 196, Short.MAX_VALUE)
                        .add(jButton24))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jCheckBox2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jCheckBox5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jCheckBox6))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jCheckBox7)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jCheckBox8)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSpinner1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton7)
                    .add(jButton21)
                    .add(jButton24))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jCheckBox2)
                    .add(jCheckBox5)
                    .add(jCheckBox6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jCheckBox7)
                    .add(jCheckBox8)))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton8.setText("Show edges");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Get sizes");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText("Save deformation coords");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("Switch to perspective window");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton15.setText("AutoSort fotos");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jCheckBox9.setSelected(true);
        jCheckBox9.setText("advanced edge detection");
        jCheckBox9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox9ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jCheckBox9)
                    .add(jButton8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .add(jButton9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .add(jButton10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .add(jButton11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .add(jButton15, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .add(jSpinner21, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jButton8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jCheckBox9)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 62, Short.MAX_VALUE)
                .add(jButton9)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton10)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton11)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton15)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSpinner21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel13.setText("Zoom");

        jSpinner6.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner6StateChanged(evt);
            }
        });

        jButton12.setText("reset movement");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton14.setFont(new java.awt.Font("Tahoma", 1, 18));
        jButton14.setText("?");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jCheckBox3.setText("advanced information");
        jCheckBox3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        jButton18.setText("Paste");

        jLabel40.setText("Quick Save (empty=disabled)");

        jButton20.setText("L&F");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jButton14))
                    .add(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel13)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jButton12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jSpinner6)
                            .add(jCheckBox3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .add(229, 229, 229))
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jComboBox1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel40, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton18, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                            .add(jButton20, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(198, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel13)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSpinner6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton12)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCheckBox3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton14)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel40)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 19, Short.MAX_VALUE)
                .add(jButton20)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton18)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jDialog1Layout = new org.jdesktop.layout.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jDialog1Layout.createSequentialGroup()
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel5, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jDialog1Layout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jDialog1Layout.createSequentialGroup()
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jDialog2.setAlwaysOnTop(true);
        jDialog2.setModal(true);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Horizontal Brightnes");

        jScrollBar1.setMaximum(255);
        jScrollBar1.setMinimum(-255);
        jScrollBar1.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
        jScrollBar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                scrollBArCLick(evt);
            }
        });
        jScrollBar1.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                jScrollBar1AdjustmentValueChanged(evt);
            }
        });

        jLabel3.setText("+255");

        jLabel4.setText("-255");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("left=0");

        jLabel6.setText("-255");

        jScrollBar2.setMaximum(255);
        jScrollBar2.setMinimum(-255);
        jScrollBar2.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
        jScrollBar2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                scrollBArCLick(evt);
            }
        });
        jScrollBar2.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                jScrollBar2AdjustmentValueChanged(evt);
            }
        });

        jLabel7.setText("+255");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("right=0");

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("Vertical Brightnes");

        jLabel34.setText("-255");

        jScrollBar3.setMaximum(255);
        jScrollBar3.setMinimum(-255);
        jScrollBar3.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
        jScrollBar3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                scrollBArCLick(evt);
            }
        });
        jScrollBar3.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                jScrollBar3AdjustmentValueChanged(evt);
            }
        });

        jLabel35.setText("+255");

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("top=0");
        jLabel36.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jLabel36FocusLost(evt);
            }
        });

        jLabel37.setText("-255");

        jScrollBar4.setMaximum(255);
        jScrollBar4.setMinimum(-255);
        jScrollBar4.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
        jScrollBar4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                scrollBArCLick(evt);
            }
        });
        jScrollBar4.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                jScrollBar4AdjustmentValueChanged(evt);
            }
        });

        jLabel38.setText("+255");

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("bottom=0");

        org.jdesktop.layout.GroupLayout jDialog2Layout = new org.jdesktop.layout.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog2Layout.createSequentialGroup()
                .add(jDialog2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jDialog2Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jDialog2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialog2Layout.createSequentialGroup()
                                .add(jLabel4)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel3))))
                    .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialog2Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel6)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollBar2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel7))
                    .add(jDialog2Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialog2Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jDialog2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel33, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialog2Layout.createSequentialGroup()
                                .add(jLabel34)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollBar3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel35))
                            .add(jLabel36, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialog2Layout.createSequentialGroup()
                                .add(jLabel37)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollBar4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel38))
                            .add(jLabel39, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3)
                    .add(jLabel4)
                    .add(jScrollBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel7)
                    .add(jLabel6)
                    .add(jScrollBar2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel33)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel35)
                    .add(jLabel34)
                    .add(jScrollBar3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel36)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel38)
                    .add(jLabel37)
                    .add(jScrollBar4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel39)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jDialog8.setAlwaysOnTop(true);
        jDialog8.setModal(true);

        jLabel9.setText("Max Alpha left");

        jSpinner3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner3StateChanged(evt);
            }
        });

        jLabel10.setText("Max Alpha right");

        jSpinner2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner2StateChanged(evt);
            }
        });

        jLabel11.setText("Jittert max value");
        jLabel11.setOpaque(true);

        jLabel12.setText("Jittert max value");
        jLabel12.setOpaque(true);

        jSpinner4.setEnabled(false);

        jSpinner5.setEnabled(false);

        jLabel29.setText("Max Alpha bottom");

        jSpinner17.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner17StateChanged(evt);
            }
        });

        jSpinner18.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                vertikalSpinners(evt);
            }
        });

        jLabel30.setText("Max Alpha top");

        jSpinner19.setEnabled(false);

        jLabel31.setText("Jittert max value");
        jLabel31.setOpaque(true);

        jSpinner20.setEnabled(false);

        jLabel32.setText("Jittert max value");
        jLabel32.setOpaque(true);

        org.jdesktop.layout.GroupLayout jDialog8Layout = new org.jdesktop.layout.GroupLayout(jDialog8.getContentPane());
        jDialog8.getContentPane().setLayout(jDialog8Layout);
        jDialog8Layout.setHorizontalGroup(
            jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jDialog8Layout.createSequentialGroup()
                        .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jSpinner2)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel9))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 24, Short.MAX_VALUE)
                        .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(jSpinner3)
                            .add(jLabel10)))
                    .add(jDialog8Layout.createSequentialGroup()
                        .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jSpinner4)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel12))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(jSpinner5)
                            .add(jLabel11)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialog8Layout.createSequentialGroup()
                        .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jSpinner17)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel29))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
                        .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(jSpinner18)
                            .add(jLabel30)))
                    .add(jDialog8Layout.createSequentialGroup()
                        .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel31)
                            .add(jSpinner19, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(jSpinner20)
                            .add(jLabel32))))
                .addContainerGap())
        );
        jDialog8Layout.setVerticalGroup(
            jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jSpinner2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSpinner3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel11)
                    .add(jLabel12))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jSpinner4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSpinner5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel29)
                    .add(jLabel30))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jSpinner17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSpinner18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel32)
                    .add(jLabel31))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jSpinner20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSpinner19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(54, Short.MAX_VALUE))
        );

        jMenuItem20.setText("jMenuItem20");
        jMenuItem20.setEnabled(false);
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem20);
        jPopupMenu1.add(jSeparator4);

        jMenuItem1.setText("Brightnes");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        jMenuItem8.setLabel("Alpa");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem8);

        jMenuItem2.setText("Preview alpha");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem2);

        jMenuItem9.setText("preview complete alpha");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem9);

        jMenuItem19.setText("top");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem19);

        jMenuItem3.setText("Move up");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem3);

        jMenuItem4.setText("Move down");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem4);

        jMenuItem18.setText("bottom");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem18);
        jPopupMenu1.add(jSeparator2);

        jMenuItem5.setText("Remove");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem5);
        jPopupMenu1.add(jSeparator3);

        jMenuItem6.setText("Auto brightnes computing");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem6);

        jMenuItem7.setText("Rotate/scale/share");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem7);

        jMenu1.setText("connect with neighbours");
        jMenu1.setEnabled(false);

        jMenuItem10.setText("left");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem10);

        jMenuItem11.setText("right");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem11);

        jMenuItem12.setText("all left");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem12);

        jMenuItem13.setText("all right");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem13);
        jMenu1.add(jSeparator1);

        jMenuItem14.setText("up");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem14);

        jMenuItem15.setText("down");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem15);

        jMenuItem16.setText("all up");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem16);

        jMenuItem17.setText("all down");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem17);

        jPopupMenu1.add(jMenu1);

        jMenu2.setText("sobel");

        jMenuItem21.setText("left");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem21);

        jMenuItem22.setText("right");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem22);

        jMenuItem23.setText("all left");
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem23);

        jMenuItem24.setText("all right");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem24);
        jMenu2.add(jSeparator5);

        jMenuItem25.setText("up");
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem25);

        jMenuItem26.setText("down");
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem26);

        jMenuItem27.setText("all up");
        jMenuItem27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem27ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem27);

        jMenuItem28.setText("all down");
        jMenuItem28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem28ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem28);

        jPopupMenu1.add(jMenu2);

        jDialog3.setModal(true);

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        org.jdesktop.layout.GroupLayout jDialog3Layout = new org.jdesktop.layout.GroupLayout(jDialog3.getContentPane());
        jDialog3.getContentPane().setLayout(jDialog3Layout);
        jDialog3Layout.setHorizontalGroup(
            jDialog3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        jDialog3Layout.setVerticalGroup(
            jDialog3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );

        jDialog4.setModal(true);

        jRadioButton3.setSelected(true);
        jRadioButton3.setText("Only this one and his nighbrs");
        jRadioButton3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jRadioButton4.setText("all");
        jRadioButton4.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jRadioButton5.setSelected(true);
        jRadioButton5.setText("Put all the difference to one image (some cna be over/under Brighted)");
        jRadioButton5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jRadioButton6.setText("divide the difference between nghbr (can be unprecis)");
        jRadioButton6.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jButton13.setText("Set");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 46, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 58, Short.MAX_VALUE)
        );

        jRadioButton7.setText("only left");
        jRadioButton7.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jRadioButton8.setText("only right");
        jRadioButton8.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jRadioButton9.setSelected(true);
        jRadioButton9.setText("both");
        jRadioButton9.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jRadioButton10.setText("avg");
        jRadioButton10.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jRadioButton11.setSelected(true);
        jRadioButton11.setText("median");
        jRadioButton11.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jRadioButton12.setText("most often");
        jRadioButton12.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout jDialog4Layout = new org.jdesktop.layout.GroupLayout(jDialog4.getContentPane());
        jDialog4.getContentPane().setLayout(jDialog4Layout);
        jDialog4Layout.setHorizontalGroup(
            jDialog4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog4Layout.createSequentialGroup()
                .add(jDialog4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jDialog4Layout.createSequentialGroup()
                        .add(153, 153, 153)
                        .add(jButton13))
                    .add(jDialog4Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jRadioButton3))
                    .add(jDialog4Layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jDialog4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jRadioButton9)
                            .add(jRadioButton8)
                            .add(jRadioButton7)))
                    .add(jDialog4Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jRadioButton4))
                    .add(jDialog4Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jDialog4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jRadioButton5)
                            .add(jRadioButton6)))
                    .add(jDialog4Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jRadioButton10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRadioButton11)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRadioButton12)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDialog4Layout.setVerticalGroup(
            jDialog4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialog4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jRadioButton3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jDialog4Layout.createSequentialGroup()
                        .add(jRadioButton7)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRadioButton8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jRadioButton9))
                    .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRadioButton4)
                .add(21, 21, 21)
                .add(jRadioButton5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRadioButton6)
                .add(17, 17, 17)
                .add(jDialog4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButton10)
                    .add(jRadioButton11)
                    .add(jRadioButton12))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 42, Short.MAX_VALUE)
                .add(jButton13)
                .addContainerGap())
        );

        jPanel8.setLayout(new java.awt.BorderLayout());

        jTextArea2.setColumns(20);
        jTextArea2.setEditable(false);
        jTextArea2.setFont(new java.awt.Font("Courier", 1, 14));
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jDialog5Layout = new org.jdesktop.layout.GroupLayout(jDialog5.getContentPane());
        jDialog5.getContentPane().setLayout(jDialog5Layout);
        jDialog5Layout.setHorizontalGroup(
            jDialog5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialog5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jDialog5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jDialog5Layout.setVerticalGroup(
            jDialog5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialog5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jDialog6.setAlwaysOnTop(true);
        jDialog6.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                jDialog6WindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                jDialog6WindowClosing(evt);
            }
        });

        jLabel14.setText("Rotate");

        jSpinner7.setModel(new SpinnerNumberModel(0d,-100000d,100000d,0.01d));
        jSpinner7.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                AfineTransformSetters(evt);
            }
        });

        jLabel15.setText("Scale x");

        jSpinner8.setModel(new SpinnerNumberModel(1d,0.00001d,100000d,0.01d));
        jSpinner8.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                AfineTransformSetters(evt);
            }
        });

        jLabel16.setText("Share x");

        jSpinner9.setModel(new SpinnerNumberModel(1d,0.00001d,100000d,0.01d));
        jSpinner9.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                AfineTransformSetters(evt);
            }
        });

        jLabel17.setText("Scale y");

        jLabel18.setText("Share y");

        jSpinner10.setModel(new SpinnerNumberModel(0d,-10000d,100000d,0.01d));
        jSpinner10.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                AfineTransformSetters(evt);
            }
        });

        jSpinner11.setModel(new SpinnerNumberModel(0d,-10000d,100000d,0.01d));
        jSpinner11.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                AfineTransformSetters(evt);
            }
        });

        jButton16.setText("OK");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jCheckBox4.setText("apply to all");
        jCheckBox4.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout jDialog6Layout = new org.jdesktop.layout.GroupLayout(jDialog6.getContentPane());
        jDialog6.getContentPane().setLayout(jDialog6Layout);
        jDialog6Layout.setHorizontalGroup(
            jDialog6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jDialog6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButton16, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                    .add(jDialog6Layout.createSequentialGroup()
                        .add(jDialog6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jSpinner10)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jSpinner7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel14)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel15)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jSpinner8)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel16))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jDialog6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jSpinner9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                            .add(jLabel17)
                            .add(jLabel18)
                            .add(jSpinner11)))
                    .add(jCheckBox4))
                .addContainerGap())
        );
        jDialog6Layout.setVerticalGroup(
            jDialog6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel14)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSpinner7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel15)
                    .add(jLabel17))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jSpinner8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSpinner9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel16)
                    .add(jLabel18))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jSpinner10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSpinner11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCheckBox4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 62, Short.MAX_VALUE)
                .add(jButton16)
                .addContainerGap())
        );

        jDialog7.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        jDialog7.setModal(true);

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Clip the image");
        jLabel19.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel20.setText("dest width:");

        jSpinner12.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner12StateChanged(evt);
            }
        });

        jLabel21.setText("dest height");

        jSpinner13.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner13StateChanged(evt);
            }
        });

        jLabel22.setText("source x");

        jSpinner14.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner14StateChanged(evt);
            }
        });

        jLabel23.setText("source y");

        jSpinner15.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner15StateChanged(evt);
            }
        });

        jLabel24.setText("zoom");

        jSpinner16.setModel(new SpinnerNumberModel(1d,0.01d,1000d,0.05d));
        jSpinner16.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner16StateChanged(evt);
            }
        });

        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel10.setLayout(new java.awt.BorderLayout());

        jButton17.setText("Finished");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jLabel25.setText("(0)");

        jLabel26.setText("(0)");

        jLabel27.setText("(0)");

        jLabel28.setText("(0)");

        org.jdesktop.layout.GroupLayout jDialog7Layout = new org.jdesktop.layout.GroupLayout(jDialog7.getContentPane());
        jDialog7.getContentPane().setLayout(jDialog7Layout);
        jDialog7Layout.setHorizontalGroup(
            jDialog7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialog7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jDialog7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel19, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jDialog7Layout.createSequentialGroup()
                        .add(jLabel20)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSpinner12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel25))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jDialog7Layout.createSequentialGroup()
                        .add(jDialog7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel21)
                            .add(jLabel22)
                            .add(jLabel23))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jDialog7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSpinner15, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSpinner14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSpinner13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jDialog7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel26)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel27)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel28)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jDialog7Layout.createSequentialGroup()
                        .add(jLabel24)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSpinner16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton17)))
                .addContainerGap())
        );
        jDialog7Layout.setVerticalGroup(
            jDialog7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel20)
                    .add(jLabel25)
                    .add(jSpinner12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jDialog7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel21)
                        .add(jSpinner13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel26))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel22)
                    .add(jLabel27)
                    .add(jSpinner14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialog7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jDialog7Layout.createSequentialGroup()
                        .add(jDialog7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel23)
                            .add(jSpinner15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jDialog7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel24)
                            .add(jSpinner16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jButton17)))
                    .add(jLabel28))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                .addContainerGap())
        );

        jDialog9.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        jDialog9.setModal(true);

        jButton19.setText("ok");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jDialog9Layout = new org.jdesktop.layout.GroupLayout(jDialog9.getContentPane());
        jDialog9.getContentPane().setLayout(jDialog9Layout);
        jDialog9Layout.setHorizontalGroup(
            jDialog9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jDialog9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton19, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(jSpinner22, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addContainerGap())
        );
        jDialog9Layout.setVerticalGroup(
            jDialog9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialog9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jSpinner22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jButton19)
                .addContainerGap())
        );

        jDialog10.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        jDialog10.setModal(true);

        sobeldirection.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "both", "horizontal", "vertical" }));
        SpinnerNumberModel sn=new SpinnerNumberModel(50, 0, 255, 1);
        tresholSpin.setModel(sn);

        jButton23.setText("done");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jLabel41.setText("treshold");

        enableSobel.setText("enabled");

        jButton25.setText("set to all vertical");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jButton26.setText("set to all horizontal");
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        jButton22.setText("preview cofigure");

        org.jdesktop.layout.GroupLayout jDialog10Layout = new org.jdesktop.layout.GroupLayout(jDialog10.getContentPane());
        jDialog10.getContentPane().setLayout(jDialog10Layout);
        jDialog10Layout.setHorizontalGroup(
            jDialog10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog10Layout.createSequentialGroup()
                .addContainerGap()
                .add(jDialog10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButton23)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, sobeldirection, 0, 529, Short.MAX_VALUE)
                    .add(jDialog10Layout.createSequentialGroup()
                        .add(jLabel41)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tresholSpin, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE))
                    .add(enableSobel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton25)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton26)
                    .add(jButton22))
                .addContainerGap())
        );
        jDialog10Layout.setVerticalGroup(
            jDialog10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialog10Layout.createSequentialGroup()
                .addContainerGap()
                .add(sobeldirection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jDialog10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel41)
                    .add(tresholSpin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(enableSobel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton22)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 18, Short.MAX_VALUE)
                .add(jButton26)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton25)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton23)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Press F1 to show toolbar");
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jPanel6.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jPanel6MouseWheelMoved(evt);
            }
        });
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        jPanel6.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        jPanel6.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jPanel6FocusLost(evt);
            }
        });
        jPanel6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });
        jPanel6.setLayout(new java.awt.BorderLayout());

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jSpinner15StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner15StateChanged
// TODO add your handling code here:
        ((CuttingDrawer) jPanel10.getComponent(0)).setYY(((Integer) jSpinner15.getValue()).intValue());
        jPanel10.repaint();
    }//GEN-LAST:event_jSpinner15StateChanged

    private void jSpinner14StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner14StateChanged
// TODO add your handling code here:
        ((CuttingDrawer) jPanel10.getComponent(0)).setXX(((Integer) jSpinner14.getValue()).intValue());
        jPanel10.repaint();
    }//GEN-LAST:event_jSpinner14StateChanged

    private void jSpinner13StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner13StateChanged
// TODO add your handling code here:
        ((CuttingDrawer) jPanel10.getComponent(0)).setH(((Integer) jSpinner13.getValue()).intValue());
        jPanel10.repaint();
    }//GEN-LAST:event_jSpinner13StateChanged

    private void jSpinner16StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner16StateChanged
// TODO add your handling code here:
        CuttingDrawer lcd = ((CuttingDrawer) jPanel10.getComponent(0));
        lcd.setZoom(((Double) jSpinner16.getValue()).doubleValue());
        int js14 = ((Integer) jSpinner14.getValue()).intValue();
        int js15 = ((Integer) jSpinner15.getValue()).intValue();
        int js12 = ((Integer) jSpinner12.getValue()).intValue();
        int js13 = ((Integer) jSpinner13.getValue()).intValue();
        jSpinner14.setModel(new SpinnerNumberModel(js14, -9999999, 9999999, Math.max((int) (1d / lcd.getZoom()), 1)));
        jSpinner15.setModel(new SpinnerNumberModel(js15, -9999999, 9999999, Math.max((int) (1d / lcd.getZoom()), 1)));
        jSpinner12.setModel(new SpinnerNumberModel(js12, 1, 9999999, Math.max((int) (1d / lcd.getZoom()), 1)));
        jSpinner13.setModel(new SpinnerNumberModel(js13, 1, 9999999, Math.max((int) (1d / lcd.getZoom()), 1)));
        jPanel10.repaint();
    }//GEN-LAST:event_jSpinner16StateChanged

    private void jSpinner12StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner12StateChanged
// TODO add your handling code here:
        ((CuttingDrawer) jPanel10.getComponent(0)).setW(((Integer) jSpinner12.getValue()).intValue());
        jPanel10.repaint();
    }//GEN-LAST:event_jSpinner12StateChanged

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        jDialog7.setVisible(false);
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jDialog6WindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jDialog6WindowClosing
// TODO add your handling code here:
        p.setAfineTransform(null);
    }//GEN-LAST:event_jDialog6WindowClosing

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost
// TODO add your handling code here:
        xadd = 0;
        yadd = 0;
    }//GEN-LAST:event_formFocusLost

    private void jDialog6WindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jDialog6WindowClosed
// TODO add your handling code here:
    }//GEN-LAST:event_jDialog6WindowClosed

    private void AfineTransformSetters(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_AfineTransformSetters
// TODO add your handling code here:
        if (dontchange) {
            return;
        }
        if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        AffineTransform af = new AffineTransform();
        af.rotate(((Double) jSpinner7.getValue()).doubleValue(), p.getTheSelected().getWidth() / 2, p.getTheSelected().getHeight() / 2);
        af.scale(((Double) jSpinner8.getValue()).doubleValue(), ((Double) jSpinner9.getValue()).doubleValue());
        af.shear(((Double) jSpinner10.getValue()).doubleValue(), ((Double) jSpinner11.getValue()).doubleValue());
        p.setAfineTransform(af);
        p.repaint();
    }//GEN-LAST:event_AfineTransformSetters

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
// TODO add your handling code here:
        jDialog6.setVisible(false);
        if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        AffineTransform af = new AffineTransform();
        double rotate = ((Double) jSpinner7.getValue()).doubleValue();
        double sheerx = ((Double) jSpinner10.getValue()).doubleValue();
        double sheery = ((Double) jSpinner11.getValue()).doubleValue();
        af.rotate(rotate, p.getTheSelected().getWidth() / 2, p.getTheSelected().getHeight() / 2);
        double scalex = ((Double) jSpinner8.getValue()).doubleValue();
        double scaley = ((Double) jSpinner9.getValue()).doubleValue();
        af.scale(scalex, scaley);
        af.shear(sheerx, sheery);
        //if (((int)Math.abs(Math.round(Math.toDegrees(rotate))))%90!=0 || sheerx!=0d || sheery!=0d)
        {
            CuttingDrawer cd = new CuttingDrawer(p.getTheSelected().getImage(), af);
            jPanel10.add(cd);
            jLabel25.setText("(" + p.getTheSelected().afw + ")");
            jLabel26.setText("(" + p.getTheSelected().afh + ")");
            jLabel27.setText("(" + p.getTheSelected().afx + ")");
            jLabel28.setText("(" + p.getTheSelected().afy + ")");
            jSpinner12.setValue(new Integer((int) ((double) p.getTheSelected().getWidth() * scalex)));
            jSpinner13.setValue(new Integer((int) ((double) p.getTheSelected().getHeight() * scaley)));
            jSpinner14.setValue(new Integer(0));
            jSpinner15.setValue(new Integer(0));
            jSpinner16.setValue(new Double(p.getZoom()));
            cd.setYY(((Integer) jSpinner15.getValue()).intValue());
            cd.setXX(((Integer) jSpinner14.getValue()).intValue());
            cd.setH(((Integer) jSpinner13.getValue()).intValue());
            cd.setW(((Integer) jSpinner12.getValue()).intValue());
            cd.repaint();
            jDialog7.setVisible(true);

            if (jCheckBox4.isSelected()) {
                p.affineAll(
                        ((Double) jSpinner7.getValue()).doubleValue(),
                        ((Double) jSpinner8.getValue()).doubleValue(),
                        ((Double) jSpinner9.getValue()).doubleValue(),
                        ((Double) jSpinner10.getValue()).doubleValue(),
                        ((Double) jSpinner11.getValue()).doubleValue(),
                        af,
                        cd.getW(), cd.getH(), cd.getXX(), cd.getYY());

            } else {
                p.afinuj(
                        ((Double) jSpinner7.getValue()).doubleValue(),
                        ((Double) jSpinner8.getValue()).doubleValue(),
                        ((Double) jSpinner9.getValue()).doubleValue(),
                        ((Double) jSpinner10.getValue()).doubleValue(),
                        ((Double) jSpinner11.getValue()).doubleValue(),
                        af,
                        cd.getW(), cd.getH(), cd.getXX(), cd.getYY());
            }
            jPanel10.remove(0);
        }
        /*else{
        p.afinuj(
        ((Double)jSpinner7.getValue()).doubleValue(),
        ((Double)jSpinner8.getValue()).doubleValue(),
        ((Double)jSpinner9.getValue()).doubleValue(),
        ((Double)jSpinner10.getValue()).doubleValue(),
        ((Double)jSpinner11.getValue()).doubleValue(),
        af
        );
        }*/
        p.setAfineTransform(null);
        p.repaint();

    }//GEN-LAST:event_jButton16ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
// TODO add your handling code here:
        jDialog1.setVisible(false);
if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        if (p.getTheSelected().getLeftBrightnes() != 0 || p.getTheSelected().getRightBrightnes() != 0) {
            p.brightuj(p.getTheOneIndex(), p.getTheSelected().getLeftBrightnes(), p.getTheSelected().getRightBrightnes(), p.getTheSelected().getTopBrightnes(), p.getTheSelected().getBotomBrightnes(), false);
        } else {
            p.getTheSelected().reload();
        }
        dontchange = true;
        try {
            jSpinner7.setValue(new Double(p.getTheSelected().rotate));
            jSpinner8.setValue(new Double(p.getTheSelected().scalex));
            jSpinner9.setValue(new Double(p.getTheSelected().scaley));
            jSpinner10.setValue(new Double(p.getTheSelected().sharex));
            jSpinner11.setValue(new Double(p.getTheSelected().sharey));
        } finally {
            dontchange = false;
        }
        AfineTransformSetters(null);
        jDialog6.setVisible(true);


    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jPanel6FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPanel6FocusLost
// TODO add your handling code here:
        xadd = 0;
        yadd = 0;
    }//GEN-LAST:event_jPanel6FocusLost

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
// TODO add your handling code here:
        p.setAdvancedDraw(jCheckBox3.isSelected());
        p.repaint();
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
// TODO add your handling code here:
        p.autosortPhotos(((Integer) jSpinner21.getValue()).intValue());
        p.repaint();
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
// TODO add your handling code here:
        jDialog5.setVisible(true);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
// TODO add your handling code here:
        int i = 0;
        if (jRadioButton7.isSelected()) {
            i = ImagePaintComponent.ONLY_LEFT;
        }
        if (jRadioButton8.isSelected()) {
            i = ImagePaintComponent.ONLY_RIGHT;
        }
        if (jRadioButton9.isSelected()) {
            i = ImagePaintComponent.ONLY_BOTH;
        }

        int t = 0;
        if (jRadioButton10.isSelected()) {
            t = ImagePaintComponent.METHOD_AVG;
        }
        if (jRadioButton11.isSelected()) {
            t = ImagePaintComponent.METHOD_MEDIAN;
        }
        if (jRadioButton12.isSelected()) {
            t = ImagePaintComponent.METHOD_MOSTOFTEN;
        }
        if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.setBrightness(p.getTheOneIndex(), jRadioButton3.isSelected(), jRadioButton5.isSelected(), i, t);
        p.repaint();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
// TODO add your handling code here:
        jDialog1.setVisible(false);
        jDialog4.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jSpinner3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner3StateChanged
// TODO add your handling code here:
        spinnerBody();
    }//GEN-LAST:event_jSpinner3StateChanged

    private void jSpinner2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner2StateChanged
// TODO add your handling code here:
        spinnerBody();
    }//GEN-LAST:event_jSpinner2StateChanged

    void spinnerBody() {
        if (!jDialog8.isVisible()) {
            return;
        }

        if (dontchange) {
            return;
        }
        if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        ((PanoramatImage) p.getTheSelected()).setLeftAlpha(((Integer) jSpinner2.getValue()).intValue());
        ((PanoramatImage) p.getTheSelected()).setRightAlpha(((Integer) jSpinner3.getValue()).intValue());

        ((PanoramatImage) p.getTheSelected()).setTopAlpha(((Integer) jSpinner18.getValue()).intValue());
        ((PanoramatImage) p.getTheSelected()).setBottomAlpha(((Integer) jSpinner17.getValue()).intValue());

        Neighbourhood o = p.getHorizontalNeighbours(p.getTheOneIndex());
        ((PanoramatImage) p.getImage(o.y)).setLeftAlpha(((PanoramatImage) p.getTheSelected()).getRightAlpha());
        ((PanoramatImage) p.getImage(o.x)).setRightAlpha(((PanoramatImage) p.getTheSelected()).getLeftAlpha());

        o = p.getVerticalNeighbours(p.getTheOneIndex());
        ((PanoramatImage) p.getImage(o.x)).setTopAlpha(((PanoramatImage) p.getTheSelected()).getBottomAlpha());
        ((PanoramatImage) p.getImage(o.y)).setBottomAlpha(((PanoramatImage) p.getTheSelected()).getTopAlpha());

        p.previewAlpha(p.getTheOneIndex(), (Graphics2D) p.getGraphics());
    }

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
// TODO add your handling code here:
        xadd = 0;
        yadd = 0;
        p.zeroMovement();
        p.repaint();
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        p.getGraphics().drawRect(-1, -1, this.getWidth() + 1, this.getHeight() + 1);
        p.paint((Graphics2D) p.getGraphics());
        p.drawEdges((Graphics2D) p.getGraphics());
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
// TODO add your handling code here:
        if (jRadioButton1.isSelected()) {
            jCheckBox1.setEnabled(true);
        } else {
            jCheckBox1.setSelected(false);
            jCheckBox1.setEnabled(false);

        }
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
// TODO add your handling code here:
        hd.setVisible(true);
        this.setVisible(false);
        jDialog1.setVisible(false);

    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
// TODO add your handling code here:
        jFch.unregisterAllContentProviders();
        jFch.setMultiSelectionEnabled(false);
        jFch.disableCurretnPhotosVisible(false);
        jFch.setFileFilter(new AllFileFilter());
        int returnValue = jFch.showSaveDialog(this);
        if (returnValue != JFileChooser.APPROVE_OPTION) {
            return;
        }
        //if (jFch.getSelectedFiles()==null &&  jFch.getSelectedFiles().length==0) return;
        try {
            p.saveDeformationPoints(jFch.getSelectedFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
// TODO add your handling code here:
        TPoint s = p.drawRect((Graphics2D) p.getGraphics(), jRadioButton1.isSelected());
        jTextArea1.setText("width: " + s.x + "\nheight: " + s.y);
        jDialog3.setVisible(true);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
// TODO add your handling code here:
        p.drawRect((Graphics2D) p.getGraphics(), jRadioButton1.isSelected());
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
// TODO add your handling code here:
        jFch.unregisterAllContentProviders();
        jFch.regisdterContentProvider(MyFileChooser.imagesPreview);
        jFch.regisdterContentProvider(MyFileChooser.srcsPreview);
        jFch.setMultiSelectionEnabled(false);
        jFch.disableCurretnPhotosVisible(false);
        jFch.setFileFilter(new AllFileFilter());
        int returnValue = jFch.showSaveDialog(this);
        if (returnValue != JFileChooser.APPROVE_OPTION) {
            return;
        }
        //if (jFch.getSelectedFiles()==null &&  jFch.getSelectedFiles().length==0) return;
        try {
            p.saveToFile(jFch.getSelectedFile());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
// TODO add your handling code here:
        jFch.unregisterAllContentProviders();
        jFch.regisdterContentProvider(MyFileChooser.imagesPreview);
        jFch.regisdterContentProvider(MyFileChooser.srcsPreview);
        jFch.setMultiSelectionEnabled(false);
        jFch.disableCurretnPhotosVisible(false);
        jFch.setFileFilter(new AllFileFilter());
        int returnValue = jFch.showOpenDialog(this);
        if (returnValue != JFileChooser.APPROVE_OPTION) {
            return;
        }
        //if (jFch.getSelectedFiles()==null &&  jFch.getSelectedFiles().length==0) return;
        Thread thread = new Thread(new Runnable() {

            public void run() {
                try {
                    try {
                        p.loadFromFile(jFch.getSelectedFile());
                        jSpinner6.setValue(new Double(p.getZoom()));

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } finally {
                    hd.jDialog1.setVisible(false);
                }

            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);

        thread.start();
        hd.jDialog1.setVisible(true);
        p.repaint();

    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
// TODO add your handling code here:
        for (int i = 0; i < p.getImagesCount(); i++) {
            p.getImage(i).setLeftAlpha(((Integer) jSpinner1.getValue()).intValue());
            p.getImage(i).setRightAlpha(((Integer) jSpinner1.getValue()).intValue());

        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
// TODO add your handling code here:
        if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.removeImage(p.getTheOneIndex());
        p.repaint();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
// TODO add your handling code here:
         if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.moveSelectedDown();
        p.repaint();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jScrollBar2AdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_jScrollBar2AdjustmentValueChanged
// TODO add your handling code here:
        jLabel8.setText("right: " + String.valueOf(jScrollBar2.getValue()));
        if (dontchange) {
            return;
        }
        if (p.getTheOneIndex() == null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.brightuj(p.getTheOneIndex(), jScrollBar1.getValue(), jScrollBar2.getValue(), jScrollBar3.getValue(), jScrollBar4.getValue());
        p.getTheSelected().setRightBrightnes(jScrollBar2.getValue());

        this.setVisible(true);
        p.repaint();
    }//GEN-LAST:event_jScrollBar2AdjustmentValueChanged

    private void jScrollBar1AdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_jScrollBar1AdjustmentValueChanged
// TODO add your handling code here:
        jLabel5.setText("left: " + String.valueOf(jScrollBar1.getValue()));
        if (dontchange) {
            return;
        }
        if (p.getTheOneIndex() == null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.brightuj(p.getTheOneIndex(), jScrollBar1.getValue(), jScrollBar2.getValue(), jScrollBar3.getValue(), jScrollBar4.getValue());
        p.getTheSelected().setLeftBrightnes(jScrollBar1.getValue());
        this.setVisible(true);
        p.repaint();
    }//GEN-LAST:event_jScrollBar1AdjustmentValueChanged

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
// TODO add your handling code here:
        if (p.getTheOneIndex() == null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }

        p.previewAlpha(p.getTheOneIndex(), (Graphics2D) p.getGraphics());
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
// TODO add your handling code here:
         if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.moveSelectedUp();
        p.repaint();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
// TODO add your handling code here:
        jDialog1.setVisible(false);
        dontchange = true;
        try {
            if (p.getTheOneIndex() == null) {
                JOptionPane.showMessageDialog(this, "exacly one image must be selected");
                return;
            }
            jScrollBar1.setValue(p.getTheSelected().getLeftBrightnes());
            jScrollBar2.setValue(p.getTheSelected().getRightBrightnes());
            jScrollBar3.setValue(p.getTheSelected().getTopBrightnes());
            jScrollBar4.setValue(p.getTheSelected().getBotomBrightnes());

            p.brightujSelected(true);

        } finally {
            dontchange = false;
        }
        this.setVisible(true);
        jDialog2.setVisible(true);


    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
// TODO add your handling code here:
        createExport(true, jRadioButton1.isSelected(), (String) jComboBox1.getSelectedItem());
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
// TODO add your handling code here:
        p.clearImages();
        repaint();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
// TODO add your handling code here:
        formMouseMoved(evt);
        if (tahne) {
            double z = p.getZoom();
            for (int i = 0; i < p.getIndexes().size(); i++) {
                PanoramatImage pp = p.getImageByIndex(i);
                pp.setLeft(save.get(i).x - (int) Math.round((double) (old.x - evt.getX()) / z));
                pp.setTop(save.get(i).y - (int) Math.round((double) (old.y - evt.getY()) / z));
            }
            this.repaint();
        }
    }//GEN-LAST:event_formMouseDragged

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
// TODO add your handling code here:
        tahne = false;
        if (p.getIndexes().size() <= 0) {
            return;
        }
        if (evt.getButton() == MouseEvent.BUTTON1) {
            for (int x = p.getImagesCount() - 1; x >= 0; x--) {
                Neighbourhood n;
                n = p.getHorizontalNeighbours(x);
                if ((n.x > -1) && (n.x < p.getImagesCount())) {
                    p.getImage(n.x).setRightAlpha(p.getImage(x).getLeftAlpha());
                }
                if ((n.y > -1) && (n.y < p.getImagesCount())) {
                    p.getImage(n.y).setLeftAlpha(p.getImage(x).getRightAlpha());
                }

                n = p.getVerticalNeighbours(x);
                if ((n.y > -1) && (n.y < p.getImagesCount())) {
                    p.getImage(n.y).setBottomAlpha(p.getImage(x).getTopAlpha());
                }
                if ((n.x > -1) && (n.x < p.getImagesCount())) {
                    p.getImage(n.x).setTopAlpha(p.getImage(x).getBottomAlpha());
                }

            }
        }
    }//GEN-LAST:event_formMouseReleased

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        xadd = 0;
        yadd = 0;
        if (this.isFocused()) {
            if (evt.getX() < 10) {
                xadd = 10;
            }
            if (evt.getX() > jPanel6.getWidth() - 10) {
                xadd = -10;
            }
            if (evt.getY() < 30) {
                yadd = 10;
            }
            if (evt.getY() > jPanel6.getHeight() - 20) {
                yadd = -10;
            }
        }
    }//GEN-LAST:event_formMouseMoved

    private void jSpinner6StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner6StateChanged
// TODO add your handling code here:
        p.setZoom(((Double) zoommodel.getValue()).doubleValue());
        this.repaint();
    }//GEN-LAST:event_jSpinner6StateChanged

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        boolean add = (evt.getModifiers() & InputEvent.CTRL_MASK) != 0;
        p.findIndex(evt.getX(), evt.getY(), add);
        repaint();
        jMenuItem20.setText("---none selected---");
        if (evt.getButton() == MouseEvent.BUTTON3) {
            jPopupMenu1.show(jPanel6, evt.getX(), evt.getY());
            if (p.getTheOneIndex() != null) {
                jMenuItem20.setText(p.getTheOneIndex()+" - "+p.getTheSelected().getSrc().getName());
                Neighbourhood nv = p.getVerticalNeighbours(p.getTheOneIndex());
                Neighbourhood nh = p.getHorizontalNeighbours(p.getTheOneIndex());
                jMenuItem1.setEnabled(true);
                jMenuItem2.setEnabled(true);
                jMenuItem3.setEnabled(true);
                jMenuItem4.setEnabled(true);
                jMenuItem19.setEnabled(true);
                jMenuItem18.setEnabled(true);
                jMenuItem5.setEnabled(true);
                jMenuItem6.setEnabled(true);
                jMenuItem7.setEnabled(true);
                jMenuItem8.setEnabled(true);
                jMenu2.setEnabled(true);
                if (nh.x > -1) {
                    jMenuItem10.setEnabled(true);
                    jMenuItem21.setEnabled(true);
                } else {
                    jMenuItem10.setEnabled(false);
                    jMenuItem21.setEnabled(false);
                }
                if (nh.y > -1) {
                    jMenuItem11.setEnabled(true);
                    jMenuItem22.setEnabled(true);
                } else {
                    jMenuItem11.setEnabled(false);
                    jMenuItem22.setEnabled(false);
                }
                if (nh.x > -1) {
                    jMenuItem12.setEnabled(true);
                    jMenuItem23.setEnabled(true);
                } else {
                    jMenuItem12.setEnabled(false);
                    jMenuItem23.setEnabled(false);
                }
                if (nh.y > -1) {
                    jMenuItem13.setEnabled(true);
                    jMenuItem24.setEnabled(true);
                } else {
                    jMenuItem13.setEnabled(false);
                    jMenuItem24.setEnabled(false);
                }
                if (nv.y > -1) {
                    jMenuItem14.setEnabled(true);
                    jMenuItem25.setEnabled(true);
                } else {
                    jMenuItem14.setEnabled(false);
                    jMenuItem25.setEnabled(false);
                }
                if (nv.x > -1) {
                    jMenuItem15.setEnabled(true);
                    jMenuItem26.setEnabled(true);
                } else {
                    jMenuItem15.setEnabled(false);
                    jMenuItem26.setEnabled(false);
                }
                if (nv.y > -1) {
                    jMenuItem16.setEnabled(true);
                    jMenuItem27.setEnabled(true);
                } else {
                    jMenuItem16.setEnabled(false);
                    jMenuItem27.setEnabled(false);
                }
                if (nv.x > -1) {
                    jMenuItem17.setEnabled(true);
                    jMenuItem28.setEnabled(true);
                } else {
                    jMenuItem17.setEnabled(false);
                    jMenuItem28.setEnabled(false);
                }
            } else {
                jMenuItem1.setEnabled(false);
                jMenuItem2.setEnabled(false);
                jMenuItem3.setEnabled(false);
                jMenuItem4.setEnabled(false);
                jMenuItem19.setEnabled(false);
                jMenuItem18.setEnabled(false);
                jMenuItem5.setEnabled(false);
                jMenuItem6.setEnabled(false);
                jMenuItem7.setEnabled(false);
                jMenuItem8.setEnabled(false);
                jMenuItem10.setEnabled(false);
                jMenuItem11.setEnabled(false);
                jMenuItem12.setEnabled(false);
                jMenuItem13.setEnabled(false);
                jMenuItem14.setEnabled(false);
                jMenuItem15.setEnabled(false);
                jMenuItem16.setEnabled(false);
                jMenuItem17.setEnabled(false);
                jMenu2.setEnabled(false);

            }

        }
        if (p.getIndexes().size() == 0) {
            return;
        }
        if (evt.getButton() == MouseEvent.BUTTON1) {
            tahne = true;
            save = new ArrayList<TPoint>();
            for (int i = 0; i < p.getIndexes().size(); i++) {
                save.add(new TPoint(p.getImageByIndex(i).getLeft(), p.getImageByIndex(i).getTop()));
            }
            old.x = evt.getX();
            old.y = evt.getY();
        }


    }//GEN-LAST:event_formMousePressed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
// TODO add your handling code here:
        if (KeyEvent.getKeyText(evt.getKeyCode()).equalsIgnoreCase("F1")) {
            jDialog1.setVisible(true);
        }
    }//GEN-LAST:event_formKeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// TODO add your handling code here:
        try {
            jFch.unregisterAllContentProviders();
            jFch.regisdterContentProvider(MyFileChooser.imagesPreview);
            jFch.setClearCurretnPhotosVisible(true);
            jFch.setMultiSelectionEnabled(true);
            jFch.setFileFilter(new ImgFileFilter());
            int returnValue = jFch.showOpenDialog(this);
            if (returnValue != JFileChooser.APPROVE_OPTION) {
                return;
            }
            if (jFch.isClearCurretnPhotosSelected()) {
                p.clearImages();
            }
            //if (jFch.getSelectedFiles()==null &&  jFch.getSelectedFiles().length==0) return;
            for (int i = 0; i < jFch.getSelectedFiles().length; i++) {
                try {
                    p.loadImage(jFch.getSelectedFiles()[i], 1000000);
                    repaint();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            //jFch.setFileFilter(null);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // TODO add your handling code here:
        jDialog1.setVisible(false);
        dontchange = true;
        try {
            if (p.getTheOneIndex() == null) {
                JOptionPane.showMessageDialog(this, "exacly one image must be selected");
                return;
            }
            jSpinner2.setValue(new Integer(p.getTheSelected().getLeftAlpha()));
            jSpinner3.setValue(new Integer(p.getTheSelected().getRightAlpha()));
            //jSpinner4.setValue(new Integer(p.getSelected().getRightJitter()));
            //jSpinner5.setValue(new Integer(p.getSelected().getLeftJitter()));

            jSpinner17.setValue(new Integer(p.getTheSelected().getBottomAlpha()));
            jSpinner18.setValue(new Integer(p.getTheSelected().getTopAlpha()));
            //jSpinner19.setValue(new Integer(p.getSelected().getBottomJitter()));
            //jSpinner20.setValue(new Integer(p.getSelected().getTopJitter()));

        } finally {
            dontchange = false;
        }
        this.setVisible(true);
        jDialog8.setVisible(true);


    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jSpinner17StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner17StateChanged
        // TODO add your handling code here:
        spinnerBody();
    }//GEN-LAST:event_jSpinner17StateChanged

    private void vertikalSpinners(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_vertikalSpinners
        // TODO add your handling code here:
        spinnerBody();
}//GEN-LAST:event_vertikalSpinners

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        // TODO add your handling code here:
        if (jCheckBox2.isSelected()) {
            jCheckBox5.setSelected(true);
            jCheckBox6.setSelected(true);
            jCheckBox5.setEnabled(true);
            jCheckBox6.setEnabled(true);
        } else {
            jCheckBox5.setSelected(false);
            jCheckBox6.setSelected(false);
            jCheckBox5.setEnabled(false);
            jCheckBox6.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jCheckBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox5ActionPerformed

    private void jCheckBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox6ActionPerformed

    private void jCheckBox9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox9ActionPerformed
        // TODO add your handling code here:
        Neighbourhood.enchantedEdges = jCheckBox9.isSelected();
    }//GEN-LAST:event_jCheckBox9ActionPerformed

    private void jScrollBar3AdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_jScrollBar3AdjustmentValueChanged
        jLabel36.setText("top: " + String.valueOf(jScrollBar3.getValue()));
        if (dontchange) {
            return;
        }
        if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.brightuj(p.getTheOneIndex(), jScrollBar1.getValue(), jScrollBar2.getValue(), jScrollBar3.getValue(), jScrollBar4.getValue());
        p.getTheSelected().setTopBrightnes(jScrollBar3.getValue());

        this.setVisible(true);
        p.repaint();
    }//GEN-LAST:event_jScrollBar3AdjustmentValueChanged

    private void jScrollBar4AdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_jScrollBar4AdjustmentValueChanged
        jLabel39.setText("bottom: " + String.valueOf(jScrollBar4.getValue()));
        if (dontchange) {
            return;
        }
        if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.brightuj(p.getTheOneIndex(), jScrollBar1.getValue(), jScrollBar2.getValue(), jScrollBar3.getValue(), jScrollBar4.getValue());
        p.getTheSelected().setBotomBrightnes(jScrollBar4.getValue());

        this.setVisible(true);
        p.repaint();
    }//GEN-LAST:event_jScrollBar4AdjustmentValueChanged

    private void jLabel36FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jLabel36FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel36FocusLost

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        // TODO add your handling code here:
        jDialog9.setVisible(false);
    }//GEN-LAST:event_jButton19ActionPerformed

    private void scrollBArCLick(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scrollBArCLick
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON3) {
            JScrollBar jsb = (JScrollBar) evt.getSource();
            jSpinner22.setValue(jsb.getValue());
            jDialog9.setVisible(true);
            jsb.setValue(((Integer) jSpinner22.getValue()).intValue());
        }
    }//GEN-LAST:event_scrollBArCLick

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // TODO add your handling code here:
        createExport(false, true, null);
        p.drawExport(kam);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // TODO add your handling code here:
         if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.connection(ImagePaintComponent.JUST_LEFT);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
 if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.connection(ImagePaintComponent.JUST_RIGHT);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        // TODO add your handling code here:
         if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.connection(ImagePaintComponent.ALL_LEFT);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        // TODO add your handling code here:
         if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.connection(ImagePaintComponent.ALL_RIGHT);
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        // TODO add your handling code here:
         if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.connection(ImagePaintComponent.JUST_UP);
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        // TODO add your handling code here:
         if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.connection(ImagePaintComponent.JUST_DOWN);
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        // TODO add your handling code here:
         if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.connection(ImagePaintComponent.ALL_UP);
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        // TODO add your handling code here:
         if (p.getTheOneIndex()==null) {
            JOptionPane.showMessageDialog(this, "exacly one image must be selected");
            return;
        }
        p.connection(ImagePaintComponent.ALL_DOWN);
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jPanel6MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jPanel6MouseWheelMoved
        // TODO add your handling code here:
        try {
            double z1 = ((Double) zoommodel.getValue()).doubleValue();
            Double nwValue = null;
            if (evt.getWheelRotation() < 0) {
                nwValue = (new Double((Double) zoommodel.getValue() - 0.05));
            }
            if (evt.getWheelRotation() > 0) {
                nwValue = (new Double((Double) zoommodel.getValue() + 0.05));
            }
            if (nwValue != null) {
                if (nwValue.doubleValue() > 0) {
                    zoommodel.setValue(nwValue);
                }
            }
            double z2 = ((Double) zoommodel.getValue()).doubleValue();
            if (z1 == z2 || z1 == 0) {
                return;
            }
            TPoint posun = (TPoint) p.getPosun().clone();
            double top = ((double) (evt.getY() - posun.y)) / z1;
            double left = ((double) (evt.getX() - posun.x)) / z1;
            TPoint nwPosun = new TPoint();
            nwPosun.x = evt.getX() - (int) (z2 * left);
            nwPosun.y = evt.getY() - (int) (z2 * top);
            p.setPosun(nwPosun);
            repaint();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }//GEN-LAST:event_jPanel6MouseWheelMoved

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        // TODO add your handling code here:
        int k = JOptionPane.showConfirmDialog(jDialog1, "do you really want for this hyper-simple gui swap to system one?\n  press ok  to keep  beautifull java one or no to swap to system one");
        try {
            if (k == JOptionPane.OK_OPTION) {

                UIManager.setLookAndFeel(
                        UIManager.getCrossPlatformLookAndFeelClassName());


            } else if (k == JOptionPane.NO_OPTION) {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());

            }


            SwingUtilities.updateComponentTreeUI(this);
            this.pack();
            jDialog1.repaint();

            SwingUtilities.updateComponentTreeUI(hd);
            hd.pack();

            SwingUtilities.updateComponentTreeUI(jFch);
            jFch.pack();


        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(jDialog1, ex.getMessage());
        }
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        // TODO add your handling code here:
        for (int i = 0; i < p.getImagesCount(); i++) {
            p.getImage(i).setTopAlpha(((Integer) jSpinner1.getValue()).intValue());
            p.getImage(i).setBottomAlpha(((Integer) jSpinner1.getValue()).intValue());

        }
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
        // TODO add your handling code here:
        p.moveSelectedToTop();
        p.repaint();
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        // TODO add your handling code here:
        p.moveSelectedToBottom();
        p.repaint();
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        // TODO add your handling code here:
        if (jdialog10Settings!=null){
            //apply for all attendants
        }
        jDialog10.setVisible(false);
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        // TODO add your handling code here:
        prepareJD10For(null, 'X');
        jDialog10.setVisible(true);
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        // TODO add your handling code here:
        prepareJD10For(p.getTheOneIndex(),'l');
        jDialog10.setVisible(true);
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
        // TODO add your handling code here:
                prepareJD10For(p.getTheOneIndex(),'L');
        jDialog10.setVisible(true);
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
        // TODO add your handling code here:
                prepareJD10For(p.getTheOneIndex(),'r');
        jDialog10.setVisible(true);
    }//GEN-LAST:event_jMenuItem23ActionPerformed

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        // TODO add your handling code here:
                prepareJD10For(p.getTheOneIndex(),'R');
        jDialog10.setVisible(true);
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    private void jMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem25ActionPerformed
        // TODO add your handling code here:
                prepareJD10For(p.getTheOneIndex(),'u');
        jDialog10.setVisible(true);
    }//GEN-LAST:event_jMenuItem25ActionPerformed

    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed
        // TODO add your handling code here:
                prepareJD10For(p.getTheOneIndex(),'U');
        jDialog10.setVisible(true);
    }//GEN-LAST:event_jMenuItem26ActionPerformed

    private void jMenuItem27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem27ActionPerformed
        // TODO add your handling code here:
                prepareJD10For(p.getTheOneIndex(),'b');
        jDialog10.setVisible(true);
    }//GEN-LAST:event_jMenuItem27ActionPerformed

    private void jMenuItem28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem28ActionPerformed
        // TODO add your handling code here:
                prepareJD10For(p.getTheOneIndex(),'B');
        jDialog10.setVisible(true);
    }//GEN-LAST:event_jMenuItem28ActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton26ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void forceNonejRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forceNonejRadioButton1ActionPerformed
        // TODO add your handling code here:
        p.setForcing(0);
    }//GEN-LAST:event_forceNonejRadioButton1ActionPerformed

    private void forceVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forceVActionPerformed
        // TODO add your handling code here:
        p.setForcing(1);
    }//GEN-LAST:event_forceVActionPerformed

    private void forceHjRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forceHjRadioButton1ActionPerformed
        // TODO add your handling code here:
        p.setForcing(2);
    }//GEN-LAST:event_forceHjRadioButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        String arg1 = "-panoramat";
        final List<File> files = new LinkedList<File>();
        File lastDir[] = new File[1];
        for (int i = 0; i < args.length; i++) {
            String string = args[i];
            if (string.equalsIgnoreCase("-deformer")) {
                arg1 = string;
            } else if (string.equalsIgnoreCase("-panormat")) {
                arg1 = string;
            } else if (string.equals(".")) {
                File f = new File(System.getProperty("user.dir"));
                setDirAndFiles(f, files, string, lastDir);
            } else if (string.equals("~")) {
                File f = new File(System.getProperty("user.home"));
                setDirAndFiles(f, files, string, lastDir);
            } else {
                File f = new File(string);
                setDirAndFiles(f, files, string, lastDir);
            }

        }
        System.out.println("[-deformer]/[-panormat] [direcory] [file1] [file2] ... [filen]");
        final String mainArg = arg1;
        final File wDir = lastDir[0];
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                if (mainArg.equalsIgnoreCase("-deformer")) {
                    hd = new HorizontDeformerWindow();
                    PanoramaMaker pa = new PanoramaMaker(files, wDir);
                    hd.setPaWindow(pa);
                    jDialog1.setVisible(false);
                    hd.setVisible(true);
                } else {
                    hd = new HorizontDeformerWindow();
                    PanoramaMaker pa = new PanoramaMaker(files, wDir);
                    hd.setPaWindow(pa);
                    pa.setVisible(true);

                }

            }
        });
    }

    private String getCleverSuffix(File file) {
        String s = file.getName();
        String ss = Cammons.getSuffix(s);
        if (ss == null) {
            return "bmp";
        }
        if (s.equalsIgnoreCase(ss)) {
            return "bmp";
        }
        return ss;

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox enableSobel;
    private javax.swing.JRadioButton forceH;
    private javax.swing.JRadioButton forceNone;
    private javax.swing.JRadioButton forceV;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JComboBox jComboBox1;
    private static javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog10;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JDialog jDialog3;
    private javax.swing.JDialog jDialog4;
    private javax.swing.JDialog jDialog5;
    private javax.swing.JDialog jDialog6;
    private javax.swing.JDialog jDialog7;
    private javax.swing.JDialog jDialog8;
    private javax.swing.JDialog jDialog9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton11;
    private javax.swing.JRadioButton jRadioButton12;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollBar jScrollBar2;
    private javax.swing.JScrollBar jScrollBar3;
    private javax.swing.JScrollBar jScrollBar4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner10;
    private javax.swing.JSpinner jSpinner11;
    private javax.swing.JSpinner jSpinner12;
    private javax.swing.JSpinner jSpinner13;
    private javax.swing.JSpinner jSpinner14;
    private javax.swing.JSpinner jSpinner15;
    private javax.swing.JSpinner jSpinner16;
    private javax.swing.JSpinner jSpinner17;
    private javax.swing.JSpinner jSpinner18;
    private javax.swing.JSpinner jSpinner19;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JSpinner jSpinner20;
    private javax.swing.JSpinner jSpinner21;
    private javax.swing.JSpinner jSpinner22;
    private javax.swing.JSpinner jSpinner3;
    private javax.swing.JSpinner jSpinner4;
    private javax.swing.JSpinner jSpinner5;
    private javax.swing.JSpinner jSpinner6;
    private javax.swing.JSpinner jSpinner7;
    private javax.swing.JSpinner jSpinner8;
    private javax.swing.JSpinner jSpinner9;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JComboBox sobeldirection;
    private javax.swing.JSpinner tresholSpin;
    // End of variables declaration//GEN-END:variables

    private void createExport(final boolean save, final boolean biggest, final String suggested) {
        p.setVCorner(jCheckBox7.isSelected());
        p.setHCorner(jCheckBox8.isSelected());
        final boolean wasJCHB1selected = jCheckBox1.isSelected();
        final int priority;
        if (save == false) {
            jCheckBox1.setSelected(false);
            priority = Thread.MIN_PRIORITY;
        } else {
            priority = Thread.MAX_PRIORITY;
        }

        if (jCheckBox1.isSelected()) {
            {
                Thread thread = new Thread(new Runnable() {

                    public void run() {
                        try {
                            hd.createData(
                                    p.exportAll(biggest, jCheckBox2.isSelected(), jCheckBox5.isSelected(), jCheckBox6.isSelected()),
                                    p.getDeformationsPoints());
                        } finally {
                            hd.jDialog1.setVisible(false);
                        }

                    }
                });
                thread.setPriority(priority);

                thread.start();
                hd.jDialog1.setVisible(true);
                this.setVisible(false);
                jDialog1.setVisible(false);
                if (suggested == null || suggested.trim().equals("")) {
                } else {
                    hd.jTextField1.setText(suggested);
                }
                hd.setVisible(true);
            }
        } else {
            if (save) {
                if (suggested == null || suggested.trim().equals("")) {
                    jFch.unregisterAllContentProviders();
                    jFch.regisdterContentProvider(MyFileChooser.imagesPreview);
                    jFch.setMultiSelectionEnabled(false);
                    jFch.disableCurretnPhotosVisible(false);
                    jFch.setFileFilter(new ImgFileFilter());
                    int returnValue = jFch.showSaveDialog(this);
                    if (returnValue != JFileChooser.APPROVE_OPTION) {
                        return;
                    }
                }
            }
            //if (jFch.getSelectedFiles()==null &&  jFch.getSelectedFiles().length==0) return;
            Thread thread = new Thread(new Runnable() {

                public void run() {
                    try {
                        try {

                            //p.exportAllToFile(jFch.getSelectedFile(),jRadioButton1.isSelected(),jCheckBox2.isSelected(),jCheckBox5.isSelected(), jCheckBox6.isSelected(),getCleverSuffix(jFch.getSelectedFile()));
                            kam = p.exportAll(biggest, jCheckBox2.isSelected(), jCheckBox5.isSelected(), jCheckBox6.isSelected());
                            if (save) {
                                File f = null;
                                if (suggested == null || suggested.trim().equals("")) {
                                    f = jFch.getSelectedFile();
                                } else {
                                    f = new File(suggested);
                                }
                                ImageIO.write(kam, getCleverSuffix(f), f);

                            }

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } finally {
                        jCheckBox1.setSelected(wasJCHB1selected);
                        hd.jDialog1.setVisible(false);
                    }

                }
            });
            thread.setPriority(priority);

            thread.start();
            hd.jDialog1.setVisible(true);
        }
    }

    public void update(Observable o, Object arg) {
        List<String> l = p.getSugestedSaves();
        DefaultComboBoxModel m = new DefaultComboBoxModel();
        m.addElement("");
        for (String string : l) {
            m.addElement(string);

        }
        jComboBox1.setModel(m);
        jComboBox1.setSelectedIndex(0);
        jComboBox1.repaint();
    }


    Point jdialog10Settings;
    private void prepareJD10For(Integer theOneIndex, char c) {
        jdialog10Settings=null;
       if (theOneIndex==null){
           jButton22.setEnabled(false);
           jButton25.setEnabled(true);
           jButton26.setEnabled(true);
       }else{
           jButton22.setEnabled(true);
           jButton25.setEnabled(false);
           jButton26.setEnabled(false);
           jdialog10Settings=new Point(theOneIndex, (int)c);
       }
    }
}
