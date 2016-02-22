/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cammons.providers;

import cammons.PreviewProvider;
import java.awt.Color;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTextArea;

/**
 *
 * @author jvanek
 */
public class UnsuportedPreviewProvider implements PreviewProvider {
 File f;
 BufferedImage screenShot;
 JTextArea content;
 private static final String intro="preview is not supported";

    public UnsuportedPreviewProvider() {
    }

    private UnsuportedPreviewProvider(File f){
        if (f==null) throw new NullPointerException("file cant be null");
        this.f = f;
        content=new JTextArea(intro);
        content.setBackground(Color.yellow);
        List<String> l=new  ArrayList<String>(4);
        l.add(intro);
        l.add(f.getName());
        l.add(f.getAbsolutePath());
        if (f.exists()) l.add("exists"); else l. add("not exists");
        if (f.isDirectory()) l.add("directory"); else l. add("file");
        if (l!=null){
         String s="";
            for (int i = 0; i < l.size(); i++) {
                String string = l.get(i);
                s=s+string+"\n";

            }
         content.setText(s);
         screenShot=new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
         FontMetrics g=screenShot.createGraphics().getFontMetrics();
         int h=g.getHeight();
         int maxw=g.stringWidth(intro);
            for (String string : l) {
                int w=g.stringWidth(string);
                if (w>maxw)maxw=w;
            }
        screenShot=new BufferedImage( maxw+10,h*(l.size()+2), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d=screenShot.createGraphics();
        g2d.setColor(Color.black);
        g2d.drawString(intro, 5, h);
            for (int i = 0; i < l.size(); i++) {
                String string = l.get(i);
                g2d.drawString(string, 5,(i+2)*h);

            }

        }else {
            throw new IllegalStateException(f.getAbsoluteFile()+" cant be read");
        }



    }




    public PreviewProvider getInstance(File f) {
        return new UnsuportedPreviewProvider(f);
    }

    public BufferedImage getImage() {
        return screenShot;
    }

    public JComponent placeYourself(Container c) {
        if (c!=null){
            c.removeAll();
            c.add(content);
    }
        return content;
    }

    public boolean canAccept(File file) {
        if (file==null)return false;
        return true;
    }
  

  


    
}
