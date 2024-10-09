package filters;

import net.coobird.thumbnailator.filters.ImageFilter;

import java.awt.image.BufferedImage;

public class InvertFilter implements ImageFilter {
    @Override
    public BufferedImage apply(BufferedImage img) {
        // Creating a new BufferedImage with the same dimensions and type
        BufferedImage invertedImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                // getting rgb values
                int originalRgb = img.getRGB(x, y);

                // Extracting r g and b value
                int alpha = (originalRgb >> 24) & 0xff;
                int red = 255 - ((originalRgb >> 16) & 0xff);
                int green = 255 - ((originalRgb >> 8) & 0xff);
                int blue = 255 - (originalRgb & 0xff);

                // Adding inverted value to rgb value
                int invertedRgb = (alpha << 24) | (red << 16) | (green << 8) | blue;

                // Setting inverted RGB value
                invertedImg.setRGB(x, y, invertedRgb);
            }
        }
        return invertedImg;
    }
}