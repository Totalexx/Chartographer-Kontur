package ru.totalexx.cartographer;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.InputStreamResource;
import ru.totalexx.cartographer.exceptions.InvalidRequestParams;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.util.Properties;
import java.util.UUID;

public class ImageModel {

    private static String imagesDir;

    public static String createImage(int width, int height) throws IOException, InvalidRequestParams {


        if (width > 20000 || height > 50000 || width < 0 || height < 0)
            throw new InvalidRequestParams("");

        String imageID = UUID.randomUUID().toString();
        File imageDir = new File(imagesDir + imageID);
        imageDir.mkdirs();

        File imageParams = new File(imageDir.getPath() + File.separator + "image.properties");
        imageParams.createNewFile();
        Properties properties = new Properties();
        FileOutputStream outputStream = new FileOutputStream(imageParams);

        properties.setProperty("width", String.valueOf(width));
        properties.setProperty("height", String.valueOf(height));
        properties.store(outputStream, null);
        outputStream.close();

        return imageID;
    }

    public static void saveFragment(InputStreamResource bodyStream,
                                    String imageID,
                                    int x,
                                    int y,
                                    int width,
                                    int height) throws IOException, InvalidRequestParams {
        hasImage(imageID);

        String imageDir = imagesDir + imageID + File.separator;

        Properties properties = new Properties();
        File imageParams = new File(imageDir + "image.properties");
        FileInputStream propertiesInput = new FileInputStream(imageParams);
        properties.load(propertiesInput);

        if (x > Integer.parseInt(properties.getProperty("width"))
                || x < 0
                || y > Integer.parseInt(properties.getProperty("height"))
                || y < 0
                || width > 20000
                || height > 50000
                || width < 0
                || height < 0)
            throw new InvalidRequestParams("");


        int filename = new File(imageDir).list().length;

        File fragment = new File(imageDir + filename + ".bmp");
        fragment.createNewFile();

        FileOutputStream imageOutput = new FileOutputStream(fragment);
        IOUtils.copy(bodyStream.getInputStream(), imageOutput);
        imageOutput.close();

        ImageInputStream imageInput = ImageIO.createImageInputStream(fragment);
        ImageReader reader = ImageIO.getImageReaders(imageInput).next();
        reader.setInput(imageInput, true, true);

        int widthFragment = reader.getWidth(reader.getMinIndex());
        int heightFragment = reader.getHeight(reader.getMinIndex());

        imageInput.close();
        reader.dispose();

        if (widthFragment != width || heightFragment != height) {
            fragment.delete();
            throw new InvalidRequestParams("");
        }

        properties.setProperty(filename + ".x", String.valueOf(x));
        properties.setProperty(filename + ".y", String.valueOf(y));
        properties.setProperty(filename + ".width", String.valueOf(widthFragment));
        properties.setProperty(filename + ".height", String.valueOf(heightFragment));

        FileOutputStream propertiesOutput = new FileOutputStream(imageParams);
        properties.store(propertiesOutput, null);
        propertiesOutput.close();

    }

    public static byte[] getFragment(String imageID, int x, int y, int width, int height) throws IOException, InvalidRequestParams {
        hasImage(imageID);

        String imageDir = imagesDir + imageID + File.separator;

        Properties properties = new Properties();
        File imageParams = new File(imageDir + "image.properties");
        FileInputStream propertiesInput = new FileInputStream(imageParams);
        properties.load(propertiesInput);

        if (x >= Integer.parseInt(properties.getProperty("width"))
                || x < 0
                || y >= Integer.parseInt(properties.getProperty("height"))
                || y < 0
                || width > 5000
                || height > 5000
                || width < 1
                || height < 1)
            throw new InvalidRequestParams("");

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Rectangle fragmentRect = new Rectangle(x, y, width, height);
        Rectangle imageRect = new Rectangle(0, 0,
                Integer.parseInt(properties.getProperty("width")), Integer.parseInt(properties.getProperty("height")));
        fragmentRect = fragmentRect.intersection(imageRect);

        for (int i = 1; properties.getProperty(i + ".x") != null; i++) {

            int imgX = Integer.parseInt(properties.getProperty(i + ".x"));
            int imgY = Integer.parseInt(properties.getProperty(i + ".y"));
            int imgWidth = Integer.parseInt(properties.getProperty(i + ".width"));
            int imgHeight = Integer.parseInt(properties.getProperty(i + ".height"));

            Rectangle imgRect = new Rectangle(imgX, imgY, imgWidth, imgHeight);

            if (fragmentRect.intersects(imgRect)) {
                imgRect = imgRect.intersection(fragmentRect);
                imgRect.setLocation((int)imgRect.getX() - imgX, (int)imgRect.getY() - imgY);

                File fragmentFile = new File(imageDir + i + ".bmp");

                ImageInputStream imageInput = ImageIO.createImageInputStream(fragmentFile);
                ImageReader reader = ImageIO.getImageReaders(imageInput).next();
                reader.setInput(imageInput, true, true);

                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceRegion(imgRect);

                Raster raster = reader.readRaster(0, param);

                imageInput.close();
                reader.dispose();

                image.getRaster().setRect(imgRect.x + imgX - x, imgRect.y + imgY - y, raster);
            }
        }

        propertiesInput.close();

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ImageIO.write(image, "bmp", byteOutput);

        return byteOutput.toByteArray();
    }

    public static void deleteImage(String imageID) throws InvalidRequestParams {
        File imageDir = new File(imagesDir + imageID);
        if (!imageDir.exists())
            throw new InvalidRequestParams("");
        for (File file : imageDir.listFiles()) {
            file.delete();
        }
        imageDir.delete();
    }

    public static void hasImage(String imageID) throws InvalidRequestParams {
        if (!new File(imagesDir + imageID).exists())
            throw new InvalidRequestParams("Image not found");
    }

    public static void setImagesDir(String dir) {
        imagesDir = dir + File.separator + "images" + File.separator;
    }
}
