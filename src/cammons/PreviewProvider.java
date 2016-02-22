/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cammons;

import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JComponent;

/**
 *
 * @author jvanek
 */
public interface PreviewProvider {

    public PreviewProvider getInstance(File f);
    public BufferedImage getImage();
    public JComponent placeYourself(Container c);
    public boolean canAccept(File f);
}
