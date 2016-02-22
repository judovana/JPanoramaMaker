/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cammons.providers;

import cammons.PreviewProvider;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author jvanek
 */
public class BasicImagesPreviewProvider implements PreviewProvider {
 private File f;
 private BufferedImage i;
 private String[] supporting = {".jpg",".jpeg", ".png", ".tiff", ".tif", ".gif","bmp"};
 private PreviewComponent c;



    public BasicImagesPreviewProvider() {
    }

    private BasicImagesPreviewProvider(File f) {
        if (f==null) throw new NullPointerException("file cant be null");

        try {
            this.f = f;
            i = ImageIO.read(f);
        } catch (IOException ex) {
            throw new IllegalArgumentException("cant load image: "+f.getAbsolutePath(),ex);

        }
        if (i==null){
            /*
            i=new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = i.createGraphics();
            int fh = g.getFontMetrics().getHeight();
            g.drawString("error loading:",5,fh*2);
            g.drawString(f.getName(),5,fh*3);
            g.drawString("from:",5,fh*4);
            g.drawString(f.getParentFile().getAbsolutePath(),5,fh*5);
             *
             */
            throw new IllegalArgumentException("cant load image: "+f.getAbsolutePath());

        }
        c=new PreviewComponent(i);
    }




    public PreviewProvider getInstance(File f) {
        return new BasicImagesPreviewProvider(f);
    }

    public BufferedImage getImage() {
        return i;
    }

    public boolean canAccept(File f) {
        if (f==null)return false;
        if (!f.exists())return false;
        if (f.isDirectory()) return false;
        String suffix = f.getName();
        for (int i = 0; i < supporting.length; i++) {
            String string = supporting[i];
            if (suffix.toLowerCase().endsWith(string)) return true;

        }
        return false;

        
    }

      public JComponent placeYourself(Container cont) {
        if (cont!=null){
            c.removeAll();
            cont.add(c);
    }
        return c;
    }

     class PreviewComponent extends JComponent{
       BufferedImage image;

        private PreviewComponent(BufferedImage i) {
            if (i==null)throw  new NullPointerException("image cant be null");
            image = i;
        }

        @Override
        public void paint(Graphics g) {
            if (getWidth()==0 || getHeight()==0) return;
            double pW=(double)image.getWidth()/(double)getWidth();
            double pH=(double)image.getHeight()/(double)getHeight();
            double p=Math.max(pW, pH);
            int nw= (int)((double)image.getWidth()/p);
            int nh= (int)((double)image.getHeight()/p);
            g.drawImage(image, getWidth()/2-nw/2,getHeight()/2-nh/2, nw, nh, null);
            if (f!=null){
            int fh=g.getFontMetrics().getHeight();
            //int fw=g.getFontMetrics().stringWidth(f.getName());
            String date=timeToStr(f.lastModified());
            g.setColor(Color.white);
            g.drawString(f.getName(), 0-1, getHeight()-1);
            g.drawString(date, 0-1, getHeight()-fh-1);
            g.drawString(f.getName(), 0+1, getHeight()+1);
            g.drawString(date, 0+1, getHeight()-fh+1);
            g.setColor(Color.black);
            g.drawString(f.getName(), 0, getHeight());
            g.drawString(date, 0, getHeight()-fh);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
        paint(g);
        }



    }

private String timeToStr(long l){
 //return new SimpleDateFormat("dd-mm-yyyy").format(l);
    return new Date(l).toString();

}

        public static void main(String[] as) throws IOException{
        PreviewProvider spp=new BasicImagesPreviewProvider();
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

        try{
        PreviewProvider cc=spp.getInstance(f1);
        System.out.println(f1.getName()+": "+cc);
       // ImageIO.write(cc.getImage(), "png", new File("/home/jvanek/Desktop/source.png"));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        try{
        System.out.println(f2.getName()+": "+spp.getInstance(f2));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        try{
        System.out.println(f3.getName()+": "+spp.getInstance(f3));
        }catch (Exception ex){
            ex.printStackTrace();
        }
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
