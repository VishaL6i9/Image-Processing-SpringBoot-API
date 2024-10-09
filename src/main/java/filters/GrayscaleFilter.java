package filters;

import net.coobird.thumbnailator.filters.ImageFilter;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

public class GrayscaleFilter implements ImageFilter {
    @Override
    public BufferedImage apply(BufferedImage img) {
        ColorConvertOp colorConvertOp = new ColorConvertOp(null);BufferedImage grayScaleImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        colorConvertOp.filter(img, grayScaleImg);
        return grayScaleImg;
    }
}