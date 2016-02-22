/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cammons.providers;

import cammons.PreviewProvider;
import java.awt.Color;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JTextArea;

/**
 *
 * @author jvanek
 */
public class SrcPreviewProvider implements PreviewProvider {
 File f;
 BufferedImage screenShot;
 JTextArea content;
 private static final String intro="jpanoramaamker saved file v5";

    public SrcPreviewProvider() {
    }

    private SrcPreviewProvider(File f){
        if (f==null) throw new NullPointerException("file cant be null");
        this.f = f;
        content=new JTextArea("unreadable content: \n"+f.getAbsolutePath());
        List<String> l;
        l=load(f);
        if (l!=null){
         String s=intro+"\n";
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
        return new SrcPreviewProvider(f);
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
        if (!file.exists())return false;
        if (file.isDirectory()) return false;
        return (load(file)!=null);
    }
    private List<String> load(File file) {
        if (file==null)return null;
        try{
            BufferedReader f=new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
            String s=f.readLine();
            if (s==null) return null;
            if (!"v5".equals(s.trim())) return null;

        s = f.readLine();
        Double.valueOf(s);
        s = f.readLine();
        Integer.valueOf(s);
        s = f.readLine();
        Integer.valueOf(s);
        s = f.readLine();
        int ims = Integer.valueOf(s);
        List<String> im = new ArrayList<String>(ims);
        int x = 0;
        while (true) {
           String ss=readRecord(f);
           if (ss==null)break;
            im.add(ss);
            x++;
        }
         return im;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
       
    }

    private String readRecord(BufferedReader f) throws IOException {
         String s = f.readLine();
         String NAME="illegal record??";
        if (s == null) {
            return null;
        }
        File file = new File(s);
        if (file.exists()) {
            NAME=file.getAbsolutePath();
        }else{
            NAME="Warning - can not find: "+file.getAbsolutePath();
        }
            try{
            s = f.readLine();
            Integer.valueOf(s);
            s = f.readLine();
            Integer.valueOf(s);
            s = f.readLine();
            Integer.valueOf(s);
            s = f.readLine();
            Integer.valueOf(s);
            s = f.readLine();
            Integer.valueOf(s);
            s = f.readLine();
            Integer.valueOf(s);
            ///
            s = f.readLine();
            Integer.valueOf(s);
            s = f.readLine();
            Integer.valueOf(s);
            s = f.readLine();
            //p.setTopJitter(Integer.valueOf(s));//temporaly disapbled
            s = f.readLine();
            //p.setBottomJitter(Integer.valueOf(s));//temporaly disabled
            //
            s = f.readLine();
            Integer.valueOf(s);
            s = f.readLine();
            Integer.valueOf(s);

            s = f.readLine();//***affine transforms***

            s = f.readLine();
            Double.valueOf(s);
            s = f.readLine();
            Double.valueOf(s);
            s = f.readLine();
            Double.valueOf(s);
            s = f.readLine();
            Double.valueOf(s);
            s = f.readLine();
            Double.valueOf(s);
            s = f.readLine();
            Integer.valueOf(s);
            s = f.readLine();
            Integer.valueOf(s);
            s = f.readLine();
            Integer.valueOf(s);
            s = f.readLine();
            Integer.valueOf(s);

            s = f.readLine();//***end of...***
        }catch (Exception wx){
            wx.printStackTrace();
            return "more errors in saved file, "+NAME;
        }

            return NAME;

    }


    public static void main(String[] as) throws IOException{
        PreviewProvider spp=new SrcPreviewProvider();
        File f1=new File("/home/jvanek/Desktop/source.src");
        File f2=new File("/home/jvanek/Desktop/index.png");
        File f3=new File("/home/jvanek/Desktop/source.src");
        File f4=new File("/home/jvanek/Desktop/images.jpg");
        File f5=new File("/home/jvanek/notexisitng/kumulaumula.jpg");
        System.out.println(f1.getName()+": "+spp.canAccept(f1));
        System.out.println(f2.getName()+": "+spp.canAccept(f2));
        System.out.println(f3.getName()+": "+spp.canAccept(f3));
        System.out.println(f4.getName()+": "+spp.canAccept(f4));
        System.out.println("null: "+spp.canAccept(null));
        System.out.println(f5.getName()+": "+spp.canAccept(f5));

        PreviewProvider cc=spp.getInstance(f1);
        System.out.println(f1.getName()+": "+cc);
       // ImageIO.write(cc.getImage(), "png", new File("/home/jvanek/Desktop/source.png"));
        try{
        System.out.println(f2.getName()+": "+spp.getInstance(f2));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        System.out.println(f3.getName()+": "+spp.getInstance(f3));
        try{
        System.out.println(f4.getName()+": "+spp.getInstance(f4));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        try{
        System.out.println("null: "+spp.getInstance(null));
        }catch (Exception ex){
            ex.printStackTrace();
        }
         try{
        System.out.println(f5.getName()+": "+spp.getInstance(f5));
                }catch (Exception ex){
            ex.printStackTrace();
        }

    }

}
