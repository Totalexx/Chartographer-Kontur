package ru.totalexx.cartographer;

import com.pulispace.mc.ui.panorama.util.BigBufferedImage;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ImageModel {

    private static final String path = System.getProperty("user.dir") + "\\images\\";

    public static String createImage(int width, int height) throws IOException{

        BufferedImage image = BigBufferedImage.create(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, width, height);
        graphics.dispose();

        String imageID = UUID.randomUUID().toString();

        File file = new File(path + imageID + ".png");
        try (ImageOutputStream out = ImageIO.createImageOutputStream(file)) {
            ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(image);
            ImageWriter writer = ImageIO.getImageWriters(type, "png").next();

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.0f);
            }
            writer.setOutput(out);
            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();
        }

        return imageID;
    }

    public static void saveImageFragment(String imageID, int x, int y, int width, int height) {
        try {
            BufferedImage image = ImageIO.read(new File(path + imageID + ".png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getImageFragment(String id, int x, int y, int width, int height) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            BufferedImage image = BigBufferedImage.create(new File(path + id + ".png"), BufferedImage.TYPE_INT_RGB);
            ImageIO.write(image.getSubimage(x, y, width, height), "bmp", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public static boolean deleteImage(String imageID) {
        File file = new File(path + imageID + ".png");
        return file.delete();
    }

}
