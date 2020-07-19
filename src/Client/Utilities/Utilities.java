package Client.Utilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

/**
 * Client.Utilities
 *
 * @Created by Long - StudentID : 18120455
 * @Date 03/07/2020 - 9:41 PM
 * @Description
 **/
public class Utilities {

    public static final String TRANSFER_ICON = "transfer_file";
    public static final String LOGO_ICON = "khtn_logo";
    public static final String EXTENSION = ".png";
    public static final int ICON_SIZE = 25;
    public static final int LOGO_SIZE = 100;
    public static final int SPACE_SIZE = 5;

    public static void setICon(JButton button, String imageName) {
        try {
            // load icon
            Image img = ImageIO.read(new FileInputStream("res/" + imageName + EXTENSION));
            img = img.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_DEFAULT);

            // disable icon
            Image disableImg = ImageIO.read(new FileInputStream("res/" + imageName + "_disable" + EXTENSION));
            disableImg = disableImg.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_DEFAULT);

            // set properties
            button.setIcon(new ImageIcon(img));
            button.setBorderPainted(false);
            button.setBorder(null);
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setContentAreaFilled(false);
            button.setPressedIcon(new ImageIcon(disableImg));

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public static JLabel getImage(String imageName, boolean scaleToLogoSize) {

        try {
            BufferedImage myPicture = ImageIO.read(new FileInputStream("res/" + imageName + EXTENSION));
            if (scaleToLogoSize)
                return new JLabel(new ImageIcon(myPicture.getScaledInstance(LOGO_SIZE, LOGO_SIZE, Image.SCALE_DEFAULT)));
            else
                return new JLabel(new ImageIcon(myPicture));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
