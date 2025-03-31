package image;
public class SobelFilter {
    Image imageIn;
    Image imageOut;

    SobelFilter(Image inputImage, Image outputImage) {
        imageIn = inputImage;
        imageOut = outputImage;
    }

    // Sobel Kernels for edge detection
    static final int[][] SOBEL_X = {
            { -1, 0, 1 },
            { -2, 0, 2 },
            { -1, 0, 1 }
    };

    static final int[][] SOBEL_Y = {
            { -1, -2, -1 },
            { 0, 0, 0 },
            { 1, 2, 1 }

    };

    /**
     * Method to apply the Sobel filter to a BMP image
     *
     * @pre the imageIn matrix must have been initialized with an image
     * @pos the imageOut matrix was modified by applying the Sobel filter
     */
    public void applySobel() {
        // Traverse the image applying the two Sobel filters
        for (int i = 1; i < imageIn.height - 1; i++) {
            for (int j = 1; j < imageIn.width - 1; j++) {
                int gradXRed = 0, gradXGreen = 0, gradXBlue = 0;
                int gradYRed = 0, gradYGreen = 0, gradYBlue = 0;

                // Apply the Sobel X and Y masks
                for (int ki = -1; ki <= 1; ki++) {
                    for (int kj = -1; kj <= 1; kj++) {
                        int red = imageIn.image[i + ki][j + kj][0];
                        int green = imageIn.image[i + ki][j + kj][1];
                        int blue = imageIn.image[i + ki][j + kj][2];

                        gradXRed += red * SOBEL_X[ki + 1][kj + 1];
                        gradXGreen += green * SOBEL_X[ki + 1][kj + 1];
                        gradXBlue += blue * SOBEL_X[ki + 1][kj + 1];

                        gradYRed += red * SOBEL_Y[ki + 1][kj + 1];
                        gradYGreen += green * SOBEL_Y[ki + 1][kj + 1];
                        gradYBlue += blue * SOBEL_Y[ki + 1][kj + 1];
                    }
                }

                // Calculate the gradient magnitude
                int red = Math.min(Math.max((int) Math.sqrt(gradXRed * gradXRed +
                        gradYRed * gradYRed), 0), 255);
                int green = Math.min(Math.max((int) Math.sqrt(gradXGreen * gradXGreen +
                        gradYGreen * gradYGreen), 0), 255);
                int blue = Math.min(Math.max((int) Math.sqrt(gradXBlue * gradXBlue +
                        gradYBlue * gradYBlue), 0), 255);

                // Create the new RGB value
                imageOut.image[i][j][0] = (byte) red;
                imageOut.image[i][j][1] = (byte) green;
                imageOut.image[i][j][2] = (byte) blue;
            }
        }
    }
}