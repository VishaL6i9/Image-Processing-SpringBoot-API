package filters;

import net.coobird.thumbnailator.filters.ImageFilter;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

public class GrayscaleFilter implements ImageFilter {
    @Override
    public BufferedImage apply(BufferedImage img) {
        ColorConvertOp op = new ColorConvertOp(null);
        BufferedImage grayscaleImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        op.filter(img, grayscaleImage);
        return grayscaleImage;
    }
}