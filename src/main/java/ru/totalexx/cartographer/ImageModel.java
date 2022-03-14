package ru.totalexx.cartographer;

import com.pulispace.mc.ui.panorama.util.BigBufferedImage;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ImageModel {

    private static final String path = System.getProperty("user.dir") + "\\images\\";

    public static String createImage(int width, int height) {

        BufferedImage image = BigBufferedImage.create(width, height, BufferedImage.TYPE_INT_RGB);

        String imageID = UUID.randomUUID().toString();

        try {
            saveBigBufferedImage(image, imageID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageID;
    }

    public static void saveImageFragment(byte[] bmp, String imageID, int x, int y, int width, int height) {
        try {
            BufferedImage image = BigBufferedImage.create(new File(path + imageID + ".png"), BufferedImage.TYPE_INT_RGB);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bmp);
            BufferedImage fragment = ImageIO.read(inputStream);

            Graphics graphics = image.getGraphics();
            graphics.drawImage(fragment, x, y, width, height, null);

            saveBigBufferedImage(image, imageID);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getImageFragment(String id, int x, int y, int width, int height) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            BufferedImage image = BigBufferedImage.create(new File(path + id + ".png"), BufferedImage.TYPE_INT_RGB);
            BufferedImage subImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            subImage.setData(image.getData());
            ImageIO.write(subImage, "bmp", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public static boolean deleteImage(String imageID) {
        File file = new File(path + imageID + ".png");
        return file.delete();
    }

    private static void saveBigBufferedImage(BufferedImage image, String imageID) throws IOException {
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
    }

}
