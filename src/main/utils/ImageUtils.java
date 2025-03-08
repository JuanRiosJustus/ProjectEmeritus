package main.utils;

import main.game.stores.pools.ColorPaletteV1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class ImageUtils {

    public static final int ALPHA_OPAQUE = 1;
    public static final int ALPHA_BIT_MASKED = 2;
    public static final int ALPHA_BLEND = 3;

    public static Image loadImage(String filePath) {
        try {
            URL resource = ImageUtils.class.getResource(filePath);
            if (resource == null) { throw new Exception(); }
            Image imageFromDisk = ImageIO.read(resource);
            BufferedImage compatibleImage = (BufferedImage) createCompatibleImage(
                    imageFromDisk.getWidth(null), imageFromDisk.getHeight(null),
                    ALPHA_BIT_MASKED
            );

            Graphics2D graphics = compatibleImage.createGraphics();
            graphics.drawImage(imageFromDisk, 0, 0, null);

            graphics.dispose();
            return compatibleImage;
        } catch (IOException e) {
//            ELoggerManager.get().log("Could not load image from path: " + filePath);
        } catch (Exception e) {
//            ELoggerManager.get().log(e.getMessage());
        }

        return null;
    }

//    public static BufferedImage createBlurredImage(BufferedImage img) {
//        return createBlurredImage(img, .25f, 2);
//    }

    public static BufferedImage createBlurredImage(BufferedImage img) {
        int radius = 15;
        int size = radius + 1;
        float weight = 1.0f / (size * size);
        float[] data = new float[size * size];
        Arrays.fill(data, weight);

        // Blur the given image
        Kernel kernel = new Kernel(size, size, data);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
        BufferedImage blurred = op.filter(img, null);

        // create new image to draw on
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, img.getType());
        Graphics2D newGraphics = newImage.createGraphics();
        newGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        newGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        newGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // place blurred image onto new image, ensuring its zoomed in to remove the black edges from blurring
        float zoom = 1.25f;
        int newWidth = (int) (width * zoom);
        int newHeight = (int) (height * zoom);
        BufferedImage largerImage = getResizedImage(blurred, newWidth, newHeight);

        // Center the blurred image
        int xOffset = (newWidth - width) / 2;
        int yOffset = (newHeight - height) / 2;
        int x = xOffset * -1;
        int y = (int) ((yOffset * 1.5) * -1);
        newGraphics.drawImage(largerImage, x, y, null);

        // Darken image a bit
        float percentage = .2f; // 50% bright - change this (or set dynamically) as you feel fit
        int brightness = (int)(256 - 256 * percentage);
        newGraphics.setColor(new Color(0,0,0, brightness));
        newGraphics.fillRect(0, 0, img.getWidth(), img.getHeight());

        return newImage;
    }

//    public static BufferedImage createBlurredImage(BufferedImage img, float zoom) {
//        int radius = 15;
//        int size = radius + 1;
//        float weight = 1.0f / (size * size);
//        float[] data = new float[size * size];
////
//        Arrays.fill(data, weight);
//
//        Kernel kernel = new Kernel(size, size, data);
//        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
//        //tbi is BufferedImage
//        BufferedImage blurred = op.filter(img, null);
//
//        int w = blurred.getWidth();
//        int h = blurred.getHeight();
//        BufferedImage stretchedAndCentered = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//        AffineTransform at = new AffineTransform();
//        at.scale(-2, -2);
//        at.translate(((double) blurred.getWidth() * zoom) * -1, (blurred.getHeight() * zoom) * -1);
//        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
//        stretchedAndCentered = scaleOp.filter(blurred, stretchedAndCentered);
//
//        return stretchedAndCentered;
//    }

    public static Image createCompatibleImage(int width, int height, int transparency) {
        GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        return graphicsConfiguration.createCompatibleImage(width, height, transparency);
    }
//    public boolean isNotCompletelyTransparent(BufferedImage img) {
//        for (int pixelRow = 0; pixelRow < img.getHeight(); pixelRow++) {
//            for (int pixelCol = 0; pixelCol < img.getWidth(); pixelCol++) {
//                int color = img.getRGB(pixelCol, pixelRow);
//                int alpha = (color>>24) & 0xff;
//                // if there is a non alpha color in this image, then it is valid
//                if (alpha != 0) { return true; }
//            }
//        }
//        return false;
//    }
    public static BufferedImage createMergedImage(BufferedImage[][] images, int width, int height) {
        GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        BufferedImage image = graphicsConfiguration.createCompatibleImage(width, height, ALPHA_BIT_MASKED);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

//        int spriteSize = Settings.getInstance().getSpriteSize();
        for (int row = 0; row < images.length; row++) {
            for (int column = 0; column < images[row].length; column++) {
                g.drawImage(images[row][column], column * width, row * height, null);
            }
        }
        g.dispose();
        return image;
    }
    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaMultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaMultiplied, null);
    }

    public static BufferedImage getResizedImage(BufferedImage src, int newWidth, int newHeight){
        BufferedImage resized = new BufferedImage(newWidth, newHeight, src.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(src.getScaledInstance(newWidth, newHeight, src.getType()), 0, 0, null);
        g.dispose();
        return resized;
    }

    public static void resizeImagesWithSubtraction(BufferedImage[] source, int subtractedWidth, int subtractedHeight) {
        for (int i = 0; i < source.length; i++) {
            BufferedImage currentImage = source[i];
            source[i] = getResizedImage(currentImage,
                    currentImage.getWidth() - subtractedWidth,
                    currentImage.getHeight() - subtractedHeight);
        }
    }
    public static void resizeImages(BufferedImage[] source, int newWidth, int newHeight) {
        for (int i = 0; i < source.length; i++) {
            source[i] = getResizedImage(source[i], newWidth, newHeight);
        }
    }

    public static void resizeImages(List<BufferedImage> source, int newWidth, int newHeight) {
        source.replaceAll(src -> getResizedImage(src, newWidth, newHeight));
    }

    public static BufferedImage[] createAnimationViaYStretchWithOscillation(BufferedImage image, int length, double maxYStretch) {
        BufferedImage[] animationFrames = new BufferedImage[length];

        // Loop through each frame to create the oscillating animation
        for (int index = 0; index < length; index++) {
            // Calculate the current oscillation factor using a sine wave
            double oscillationFactor = Math.sin(2 * Math.PI * index / length) * maxYStretch;

            // Calculate the new height based on the oscillation factor
            int newHeight = (int) (image.getHeight() + oscillationFactor);

            // Create a resized image with the new height
            BufferedImage newImage = getResizedImage(image, image.getWidth(), newHeight);

            // Apply a rescale operation for better rendering consistency
            RescaleOp op = new RescaleOp(1f, 0, null);
            animationFrames[index] = op.filter(newImage, null);
        }

        return animationFrames;
    }

    public static BufferedImage[] createBouncyAnimation(BufferedImage image, int length, double maxYStretch, double maxXStretchFactor) {
        BufferedImage[] animationFrames = new BufferedImage[length];

        for (int index = 0; index < length; index++) {
            // Compute oscillation size for height using a sine wave for smooth transition
            double heightOscillation = Math.sin(2 * Math.PI * index / length) * maxYStretch;

            // Calculate new height with oscillation
            int newHeight = Math.max(1, (int) (image.getHeight() + heightOscillation));

            // Compute width adjustment inversely related to height
            double widthOscillationFactor = 1.0 - (Math.abs(heightOscillation) / maxYStretch) * maxXStretchFactor;
            int newWidth = Math.max(1, (int) (image.getWidth() * widthOscillationFactor));

            // Create the resized image
            BufferedImage newImage = getResizedImage(image, newWidth, newHeight);

            // Optionally apply a rescale operation (if needed for brightness/contrast)
            RescaleOp op = new RescaleOp(1f, 0, null);
            animationFrames[index] = op.filter(newImage, null);
        }

        return animationFrames;
    }

    public static BufferedImage[] createAnimationViaXStretch(BufferedImage image, int length, double maxXStretch) {
        BufferedImage[] animationFrames = new BufferedImage[length];

        for (int index = 0; index < length; index++) {
            // Compute oscillation size using a sine wave for smooth transition
            double size = Math.sin(2 * Math.PI * index / length) * maxXStretch;

            // Compute the new width while clamping to avoid invalid values
            int newWidth = Math.max(1, (int) (image.getWidth() + size));

            // Maintain constant height
            int newHeight = image.getHeight();

            // Create a new blank image to center the horizontally stretched version
            BufferedImage newImage = new BufferedImage(newWidth, newHeight, image.getType());
            Graphics2D g2 = newImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Calculate the x-offset to keep the image centered
            int centerX = (newWidth - image.getWidth()) / 2;

            // Draw the original image centered horizontally within the stretched frame
            g2.drawImage(image, centerX, 0, image.getWidth(), image.getHeight(), null);
            g2.dispose();

            // Optionally apply a rescale operation for brightness/contrast
            RescaleOp op = new RescaleOp(1f, 0, null);
            animationFrames[index] = op.filter(newImage, null);
        }

        return animationFrames;
    }

    public static BufferedImage[] createAnimationViaYStretch(BufferedImage image, int length, double maxYStretch) {
        BufferedImage[] animationFrames = new BufferedImage[length];

        for (int index = 0; index < length; index++) {
            // Compute oscillation size using a sine wave for smooth transition
            double size = Math.sin(2 * Math.PI * index / length) * maxYStretch;

            // Compute the new height while clamping to avoid negative values
            int newHeight = Math.max(1, (int) (image.getHeight() + size));

            // Optionally adjust width proportionally if aspect ratio needs to be preserved
            int newWidth = image.getWidth(); // Keep width constant or adjust proportionally

            // Create the resized image
            BufferedImage newImage = getResizedImage(image, newWidth, newHeight);

            // Optionally apply a rescale operation (if needed for brightness/contrast)
            RescaleOp op = new RescaleOp(1f, 0, null);
            animationFrames[index] = op.filter(newImage, null);
        }

        return animationFrames;
    }

//    public static BufferedImage[] createAnimationViaYStretch(BufferedImage image, int length, double yStretch) {
//        BufferedImage[] animationFrames = new BufferedImage[length];
//        double size = 0;
//        for (int index = 0; index < length; index++) {
//            int newHeight = (int) (image.getHeight() + size);
//            BufferedImage newImage = getResizedImage(image, image.getWidth(), newHeight);
//            RescaleOp op = new RescaleOp(1f, 0, null);
//            animationFrames[index] = (op.filter(newImage, null));
//            if (index < animationFrames.length / 2) {
//                size += yStretch;
//            } else {
//                size -= yStretch;
//            }
//        }
//        return animationFrames;
//    }

    public static BufferedImage[] createAnimationViaStretch(BufferedImage image, int length, double increase) {
        BufferedImage[] animationFrames = new BufferedImage[length];
        double size = 0;
        for (int index = 0; index < length; index++) {
            int newHeight = (int) (image.getHeight() + size);
            int newWidth = (int) (image.getWidth() + size);
            BufferedImage newImage = getResizedImage(image, newWidth, newHeight);
            RescaleOp op = new RescaleOp(1f, 0, null);
            animationFrames[index] = (op.filter(newImage, null));
            if (index < animationFrames.length / 2) {
                size += increase;
            } else {
                size -= increase;
            }
        }
        return animationFrames;
    }

//    public static BufferedImage[] createTopSwayingAnimation(BufferedImage image, int length, double maxShear) {
//        BufferedImage[] animationFrames = new BufferedImage[length];
//
//        // Resize the image slightly smaller to give space for shearing
//        int height = (int) (image.getHeight() * 0.9);
//        int width = (int) (image.getWidth() * 0.9);
//        BufferedImage copy = ImageUtils.getResizedImage(image, width, height);
//
//        // Define the height at which the shearing starts
//        int shearStartY = (int) (image.getHeight() * 0.5); // Middle of the image
//
//        for (int index = 0; index < animationFrames.length; index++) {
//            // Create a new image for the current frame
//            BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
//            Graphics2D g2 = newImage.createGraphics();
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//            // Fill with transparent background
//            g2.setComposite(AlphaComposite.Clear);
//            g2.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
//            g2.setComposite(AlphaComposite.SrcOver);
//
//            // Draw the stationary bottom part
//            g2.drawImage(copy.getSubimage(0, shearStartY, copy.getWidth(), copy.getHeight() - shearStartY), 0, shearStartY, null);
//
//            // Shearing factor for the current frame (oscillates symmetrically around 0)
//            double shearFactor = Math.sin(2 * Math.PI * index / length) * maxShear;
//
//            // Create a gradient effect for the shear
//            for (int y = 0; y < shearStartY; y++) {
//                double shearAmount = shearFactor * ((double) y / shearStartY); // Linearly decrease shear toward the middle
//
//                AffineTransform at = new AffineTransform();
//                at.translate(0, y);
//                at.shear(shearAmount, 0); // Apply horizontal shearing
//                g2.setTransform(at);
//
//                // Draw the part of the image corresponding to this y-row
//                g2.drawImage(copy.getSubimage(0, y, copy.getWidth(), 1), 0, y, null);
//            }
//
//            // Save the current frame
//            animationFrames[index] = newImage;
//            g2.dispose();
//        }
//        return animationFrames;
//    }


//    public static BufferedImage[] createSwayingAnimation(BufferedImage image, int length, double maxShear) {
//        BufferedImage[] animationFrames = new BufferedImage[length];
//
//        // Resize the image slightly smaller for animation to avoid clipping
//        int height = image.getHeight();
//        int width = image.getWidth();
//        BufferedImage copy = ImageUtils.getResizedImage(image, width, height);
//
//        // Calculate the maximum horizontal offset for the shear
//        int maxShearOffset = (int) (maxShear * height);
//        int adjustedWidth = copy.getWidth() + Math.abs(maxShearOffset) * 2;
//
//        for (int index = 0; index < animationFrames.length; index++) {
//            // Create a new buffered image for the current frame
//            BufferedImage newImage = new BufferedImage(adjustedWidth, copy.getHeight(), image.getType());
//            Graphics2D g2 = newImage.createGraphics();
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//            // Clear the background to ensure transparency
//            g2.setComposite(AlphaComposite.Clear);
//            g2.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
//            g2.setComposite(AlphaComposite.SrcOver);
//
//            // Calculate the current shear factor for this frame
//            double oscillationFactor = Math.sin(2 * Math.PI * index / length) * maxShear;
//
//            // Draw the image row by row with increasing shear
//            for (int y = 0; y < copy.getHeight(); y++) {
//                double rowShear = (y / (double) height) * oscillationFactor; // Shear increases with height
//
//                // Apply shear transformation for the current row
//                AffineTransform rowTransform = new AffineTransform();
//                rowTransform.translate((adjustedWidth - copy.getWidth()) / 2f, 0); // Center the image
//                rowTransform.shear(rowShear, 0); // Apply horizontal shear
//                g2.setTransform(rowTransform);
//
//                // Draw the current row at its transformed position
//                g2.drawImage(copy.getSubimage(0, y, copy.getWidth(), 1), 0, y, null);
//            }
//
//            // Save the resulting frame
//            animationFrames[index] = newImage;
//            g2.dispose();
//        }
//
//        return animationFrames;
//    }

    public static BufferedImage[] createTopSwayingAnimation(BufferedImage image, int length, double maxShear) {
        BufferedImage[] animationFrames = new BufferedImage[length];

        // Resize the image slightly smaller for animation to avoid clipping
        int height = image.getHeight();
        int width = image.getWidth();
        BufferedImage copy = ImageUtils.getResizedImage(image, width, height);

        // Calculate max offset for centering
        int maxShearOffset = (int) (maxShear * height);
        int adjustedWidth = copy.getWidth() + Math.abs(maxShearOffset) * 2;

        for (int index = 0; index < animationFrames.length; index++) {
            // Create a new buffered image for the current frame
            BufferedImage newImage = new BufferedImage(adjustedWidth, copy.getHeight(), image.getType());
            Graphics2D g2 = newImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Clear the background to ensure transparency
            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
            g2.setComposite(AlphaComposite.SrcOver);

            // Calculate the current shear factor
            double shearFactor = Math.sin(2 * Math.PI * index / length) * maxShear;

            // Apply transformations: translate -> shear -> translate back
            AffineTransform transform = new AffineTransform();
            transform.translate((adjustedWidth - copy.getWidth()) / 2.0, height); // Move bottom to origin
            transform.shear(shearFactor, 0); // Apply horizontal shear
            transform.translate(0, -height); // Move back to original position
            g2.setTransform(transform);

            // Draw the image with the applied transformation
            g2.drawImage(copy, 0, 0, null);

            // Save the resulting frame
            animationFrames[index] = newImage;
            g2.dispose();
        }

        return animationFrames;
    }

//    public static BufferedImage[] createSwayingAnimation(BufferedImage image, int length, double maxShear) {
//        BufferedImage[] animationFrames = new BufferedImage[length];
//
//        // Resize the image slightly smaller for animation to avoid clipping
//        int height = image.getHeight();
//        int width = image.getWidth();
//        BufferedImage copy = ImageUtils.getResizedImage(image, width, height);
//
//        // Calculate max offset for centering
//        int maxShearOffset = (int) (maxShear * height);
//        int adjustedWidth = copy.getWidth() + Math.abs(maxShearOffset) * 2;
//
//        for (int index = 0; index < animationFrames.length; index++) {
//            // Create a new buffered image for the current frame
//            BufferedImage newImage = new BufferedImage(adjustedWidth, copy.getHeight(), image.getType());
//            Graphics2D g2 = newImage.createGraphics();
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//            // Clear the background to ensure transparency
//            g2.setComposite(AlphaComposite.Clear);
////            g2.setPaint(Color.RED);
//            g2.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
//            g2.setComposite(AlphaComposite.SrcOver);
//
//            // Calculate the current shear factor for the entire image
//            double shearFactor = Math.sin(2 * Math.PI * index / length) * maxShear;
//
//            // Apply shear transformation to the entire image
//            AffineTransform transform = new AffineTransform();
//            transform.translate((adjustedWidth - copy.getWidth()) / 2f, 0); // Center the image
//            transform.shear(shearFactor, 0); // Apply consistent horizontal shear
//            g2.setTransform(transform);
//
//            // Draw the image with the applied transformation
//            g2.drawImage(copy, 0, 0, null);
//
//            // Save the resulting frame
//            animationFrames[index] = newImage;
//            g2.dispose();
//        }
//
//        return animationFrames;
//    }

    public static BufferedImage[] createShearingAnimation(BufferedImage image, int length, double shear) {
        BufferedImage[] animationFrames = new BufferedImage[length];

        // Resize image slightly smaller
        int height = (int) (image.getHeight() * 0.9);
        int width = (int) (image.getWidth() * 0.9);
        BufferedImage copy = ImageUtils.getResizedImage(image, width, height);

        for (int index = 0; index < animationFrames.length; index++) {
            BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            Graphics2D g2 = newImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fill transparent background
            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
            g2.setComposite(AlphaComposite.SrcOver);

            // Calculate shear magnitude for current frame
            double shearFactor = Math.sin(2 * Math.PI * index / length) * shear;

            // Create a transform for symmetrical shearing
            AffineTransform at = new AffineTransform();
            at.translate((newImage.getWidth() - copy.getWidth()) / 2, 0); // Center horizontally
            at.shear(shearFactor, 0); // Apply horizontal shearing
            at.translate(-shearFactor * copy.getWidth() / 2, 0); // Counter translation to balance the shear

            g2.setTransform(at);

            // Draw centered copy of the image
            int centerX = (newImage.getWidth() - copy.getWidth()) / 2;
            int centerY = (newImage.getHeight() - copy.getHeight()) / 2;
            g2.drawImage(copy, centerX, centerY, null);

            animationFrames[index] = newImage;
            g2.dispose();
        }

        return animationFrames;
    }

//    public static BufferedImage[] createShearingAnimation(BufferedImage image, int length, double shear) {
//        BufferedImage[] animationFrames = new BufferedImage[length];
//        // Make sure image copy is weeee bit smaller so it can fit inside square when it moves
//        int height = (int) (image.getHeight() * .9);
//        int width = (int) (image.getWidth() * .9);
//        BufferedImage copy = ImageUtils.getResizedImage(image, width, height);
//
//        double sizeIncrease = 0;
//        double rate = shear / animationFrames.length;
//        for (int index = 0; index < animationFrames.length; index++) {
//            // Keeping image same height as the rest of the sprites
//            BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), copy.getType());
//            Graphics2D g2 = newImage.createGraphics();
//            AffineTransform at = new AffineTransform();
//            at.translate(sizeIncrease * -90, 0);
//            at.shear( sizeIncrease, 0);
//            g2.transform(at);
//            g2.setColor(ColorPalette.TRANSPARENT);
//            g2.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
//            // Try to put copy of the image smack dab in the middle of the sprite
//            g2.drawImage(copy, (int)(copy.getWidth() * .1), (int)(copy.getHeight() * .1), null);
//
//            animationFrames[index] = newImage;
//
//            if (index < animationFrames.length * .25) {
//                sizeIncrease += rate * 1;
//            } else if (index < animationFrames.length * .75) {
//                sizeIncrease -= rate * 1;
//            } else {
//                sizeIncrease += rate * 1;
//            }
//        }
//        return animationFrames;
//    }

//    public static BufferedImage[] stretch(BufferedImage image, int frames, double sFactor) {
//        BufferedImage[] animatedFrames = new BufferedImage[frames];
//        animatedFrames[0] = image;
//        double sizeIncrease = 0;
//        for (int index = 1; index < frames; index++) {
//            animatedFrames[index] = getResizedImage(image, image.getWidth(),image.getHeight() + (int)sizeIncrease);
//            if (index < animatedFrames.length / 2) {
//                sizeIncrease += sFactor;
//            } else {
//                sizeIncrease -= sFactor;
//            }
//        }
//        return animatedFrames;
//    }

//    private static boolean isNotCompletelyTransparent(BufferedImage img) {
//        for (int pixelRow = 0; pixelRow < img.getHeight(); pixelRow++) {
//            for (int pixelCol = 0; pixelCol < img.getWidth(); pixelCol++) {
//                int color = img.getRGB(pixelCol, pixelRow);
//                int alpha = (color>>24) & 0xff;
//                // if there is a non alpha color in this image, then it is valid
//                if (alpha != 0) { return true; }
//            }
//        }
//        return false;
//    }

    public static BufferedImage[] createFlickeringAnimation(BufferedImage image, int frames, float scaleFactor) {
        BufferedImage[] animatedFrames = new BufferedImage[frames];
        animatedFrames[0] = image;
        float brightness = 1f;
        RescaleOp op;
        for (int index = 1; index < frames; index++) {
            op = new RescaleOp(brightness, 0, null);
            animatedFrames[index] = (op.filter(image, null));
            if (index < frames / 2) {
                brightness += scaleFactor;
            } else {
                brightness -= scaleFactor;
            }
        }
        return animatedFrames;
    }


    public static boolean isNotCompletelyTransparent(BufferedImage img) {
        for (int pixelRow = 0; pixelRow < img.getHeight(); pixelRow++) {
            for (int pixelCol = 0; pixelCol < img.getWidth(); pixelCol++) {
                int color = img.getRGB(pixelCol, pixelRow);
                int alpha = (color>>24) & 0xff;
                // if there is a non alpha color in this image, then it is valid
                if (alpha != 0) { return true; }
            }
        }
        return false;
    }

    public static boolean isCompletelyTransparent(BufferedImage img) {
        for (int pixelRow = 0; pixelRow < img.getHeight(); pixelRow++) {
            for (int pixelCol = 0; pixelCol < img.getWidth(); pixelCol++) {
                int color = img.getRGB(pixelCol, pixelRow);
                int alpha = (color>>24) & 0xff;
                // if there is a non alpha color in this image, then it is not transparent
                if (alpha != 0) { return false; }
            }
        }
        return true;
    }

    public static BufferedImage empty(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(ColorPaletteV1.TRANSPARENT);
        g.dispose();
        return image;
    }

    public static BufferedImage[] spinify(BufferedImage image, float scaleFactor) {
        BufferedImage[] animatedFrames = new BufferedImage[180];
        animatedFrames[0] = image;
        float brightness = 1f;
        RescaleOp op;
        double angle = 0;
        double delta = 360f / animatedFrames.length;
        boolean enlighten = true;
        int count = 0;
        for (int index = 1; index < animatedFrames.length; index++) {
            BufferedImage newImage = rotateImageByDegrees(image, angle);
            op = new RescaleOp(brightness, 0, null);
            animatedFrames[index] = (op.filter(newImage, null));
            if (enlighten) {
                brightness += scaleFactor;
                count++;
                if (count >= 10) { enlighten = false; }
            } else {
                brightness -= scaleFactor;
                count--;
                if (count <= 0) { enlighten = true; }
            }
            angle += delta;
        }
        return animatedFrames;
    }

    public static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(angle), img.getWidth()/2f, img.getHeight()/2f);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.setColor(ColorPaletteV1.TRANSPARENT);
        g2d.drawRect(0, 0, newWidth - 1, newHeight - 1);
        g2d.dispose();

        return rotated;
    }

    /**
     * Merges a background image with an array of animation frames.
     *
     * @param background The background BufferedImage.
     * @param animationFrames The array of animation frames.
     * @return A new array of BufferedImage with the background merged.
     */
    public static BufferedImage[] mergeAnimationWithBackgroundBottomAlligned(BufferedImage background, BufferedImage[] animationFrames) {
        return mergeAnimationWithBackground(background, animationFrames, 0, .9);
    }

//    /**
//     * Merges a background image with an array of animation frames.
//     * The animation frames are positioned based on percentage-based offsets for X and Y.
//     *
//     * @param background The background BufferedImage.
//     * @param animationFrames The array of animation frames.
//     * @param xOffsetPercentage The X offset percentage (-1.0 to 1.0).
//     * @param yOffsetPercentage The Y offset percentage (-1.0 to 1.0).
//     * @return A new array of BufferedImage with the background merged.
//     */
//    public static BufferedImage[] mergeAnimationWithBackground(
//            BufferedImage background,
//            BufferedImage[] animationFrames,
//            double xOffsetPercentage,
//            double yOffsetPercentage) {
//        if (background == null || animationFrames == null || animationFrames.length == 0) {
//            throw new IllegalArgumentException("Background and animation frames must not be null or empty.");
//        }
//
//        int width = Math.max(background.getWidth(), animationFrames[0].getWidth());
//        int height = Math.max(background.getHeight(), animationFrames[0].getHeight());
//
//        BufferedImage[] mergedFrames = new BufferedImage[animationFrames.length];
//
//        for (int i = 0; i < animationFrames.length; i++) {
//            // Create a new BufferedImage for each merged frame
//            BufferedImage mergedFrame = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//
//            // Get Graphics2D to draw on the new image
//            Graphics2D g2d = mergedFrame.createGraphics();
//
//            // Draw the background
//            g2d.drawImage(background, 0, 0, null);
//
//            // Calculate position based on percentage offsets
//            int x = (int) ((width - animationFrames[i].getWidth()) * ((xOffsetPercentage + 1.0) / 2.0));
//            int y = (int) ((height - animationFrames[i].getHeight()) * ((yOffsetPercentage + 1.0) / 2.0));
//
//            // Ensure x and y are within bounds
//            x = Math.max(0, Math.min(x, width - animationFrames[i].getWidth()));
//            y = Math.max(0, Math.min(y, height - animationFrames[i].getHeight()));
//
//            // Draw the animation frame
//            g2d.drawImage(animationFrames[i], x, y, null);
//
//            g2d.dispose();
//
//            // Store the merged frame
//            mergedFrames[i] = mergedFrame;
//        }
//
//        return mergedFrames;
//    }

    public static BufferedImage mergeImages(int width, int height, BufferedImage[] imageLists) {
        if (imageLists == null || imageLists.length == 0) {
            throw new IllegalArgumentException("Image list cannot be null or empty.");
        }

//      BufferedImage newImage = graphicsConfiguration.createCompatibleImage(imageWidth, imageHeight, ALPHA_BIT_MASKED);
        BufferedImage newImage = new BufferedImage(width, height, ALPHA_BIT_MASKED);
        Graphics2D g2d = newImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw each image one below the other
        for (BufferedImage image : imageLists) { g2d.drawImage(image, 0, 0, null); }

        g2d.dispose();
        return newImage;
    }


    /**
     * Merges a background image with an array of animation frames.
     * The animation frames are positioned based on percentage-based offsets for X and Y.
     * The **bottom** of each animation frame is fixed at the calculated Y position.
     *
     * @param background The background BufferedImage.
     * @param animationFrames The array of animation frames.
     * @param xOffsetPercentage The X offset percentage (-1.0 to 1.0).
     * @param yOffsetPercentage The Y offset percentage (-1.0 to 1.0).
     * @return A new array of BufferedImage with the background merged.
     */
    public static BufferedImage[] mergeAnimationWithBackground(
            BufferedImage background,
            BufferedImage[] animationFrames,
            double xOffsetPercentage,
            double yOffsetPercentage) {
        if (background == null || animationFrames == null || animationFrames.length == 0) {
            throw new IllegalArgumentException("Background and animation frames must not be null or empty.");
        }

        int width = background.getWidth();
        int height = background.getHeight();

        BufferedImage[] mergedFrames = new BufferedImage[animationFrames.length];

        for (int i = 0; i < animationFrames.length; i++) {
            BufferedImage frame = animationFrames[i];

            // Create a new BufferedImage for each merged frame
            BufferedImage mergedFrame = new BufferedImage(width, height, TYPE_INT_ARGB);
            Graphics2D g2d = mergedFrame.createGraphics();

            // Draw the background
            g2d.drawImage(background, 0, 0, null);

            int frameWidth = frame.getWidth();
            int frameHeight = frame.getHeight();

            // Calculate X position based on percentage (horizontal alignment)
            int x = (int) ((width - frameWidth) * ((xOffsetPercentage + 1.0) / 2.0));

            // Calculate the Y position ensuring the **bottom** of the frame remains fixed
            int bottomY = (int) (height * ((yOffsetPercentage + 1.0) / 2.0));
            int y = bottomY - frameHeight; // Adjust Y so the **bottom stays fixed**

            // Ensure X and Y stay within valid bounds
            x = Math.max(0, Math.min(x, width - frameWidth));
            y = Math.max(0, Math.min(y, height - frameHeight));

            // Draw the animation frame
            g2d.drawImage(frame, x, y, null);
            g2d.dispose();

            // Store the merged frame
            mergedFrames[i] = mergedFrame;
        }

        return mergedFrames;
    }

    /**
     * Merges a background image with an array of animation frames.
     *
     * @param background The background BufferedImage.
     * @param animationFrames The array of animation frames.
     * @return A new array of BufferedImage with the background merged.
     */
    public static BufferedImage[] mergeAnimationWithBackground(BufferedImage background, BufferedImage[] animationFrames) {
        if (background == null || animationFrames == null || animationFrames.length == 0) {
            throw new IllegalArgumentException("Background and animation frames must not be null or empty.");
        }

        int width = Math.max(background.getWidth(), animationFrames[0].getWidth());
        int height = Math.max(background.getHeight(), animationFrames[0].getHeight());

        BufferedImage[] mergedFrames = new BufferedImage[animationFrames.length];

        for (int i = 0; i < animationFrames.length; i++) {
            // Create a new BufferedImage for each merged frame
            BufferedImage mergedFrame = new BufferedImage(width, height, TYPE_INT_ARGB);

            // Get Graphics2D to draw on the new image
            Graphics2D g2d = mergedFrame.createGraphics();

            // Draw the background
            g2d.drawImage(background, 0, 0, null);

            // Draw the animation frame centered on the background
            int x = (width - animationFrames[i].getWidth()) / 2;
            int y = (height - animationFrames[i].getHeight()) / 2;
            g2d.drawImage(animationFrames[i], x, y, null);

            g2d.dispose();

            // Store the merged frame
            mergedFrames[i] = mergedFrame;
        }

        return mergedFrames;
    }
}
