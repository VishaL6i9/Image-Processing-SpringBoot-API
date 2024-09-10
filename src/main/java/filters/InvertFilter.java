package filters;

import net.coobird.thumbnailator.filters.ImageFilter;

import java.awt.image.BufferedImage;

public class InvertFilter implements ImageFilter {
    @Override
    public BufferedImage apply(BufferedImage img) {
        BufferedImage invertedImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;
                int red = 255 - (rgb >> 16 & 0xff);
                int green = 255 - (rgb >> 8 & 0xff);
                int blue = 255 - (rgb & 0xff);
                int invertedRgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                invertedImage.setRGB(x, y, invertedRgb);
            }
        }

        return invertedImage;
    }
}