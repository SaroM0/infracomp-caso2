package image;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Image {

    byte[] header = new byte[54];
    public byte[][][] image;
    public int height, width; // in pixels
    int padding;

    /***
     * Method to create an image matrix from a file.
     *
     * @param input: file name. The format must be BMP with 24 bits of bit depth
     * @pos the image matrix has the values corresponding to the image
     *      stored in the file.
     */
    public Image(String name) {

        try {
            FileInputStream fis = new FileInputStream(name);
            fis.read(header);

            // Extract the width and height of the image from the header
            // Stored in little endian
            width = ((header[21] & 0xFF) << 24) | ((header[20] & 0xFF) << 16) |
                    ((header[19] & 0xFF) << 8) | (header[18] & 0xFF);
            height = ((header[25] & 0xFF) << 24) | ((header[24] & 0xFF) << 16) |
                    ((header[23] & 0xFF) << 8) | (header[22] & 0xFF);

            // System.out.println("Width: " + width + " px, Height: " + height + " px");
            image = new byte[height][width][3];

            int rowSizeWithoutPadding = width * 3;
            // The row size must be a multiple of 4 bytes
            padding = (4 - (rowSizeWithoutPadding % 4)) % 4;

            // Read and modify the pixel data
            // (in RGB format, but stored in BGR order)
            byte[] pixel = new byte[3];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    // Read the 3 bytes of the pixel (B, G, R)
                    fis.read(pixel);
                    image[i][j][0] = pixel[0];
                    image[i][j][1] = pixel[1];
                    image[i][j][2] = pixel[2];
                }
                fis.skip(padding);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to write an image to a file in BMP format
     *
     * @param output: name of the file where the image will be stored.
     *                It is expected to be invoked to store the modified image.
     * @pre the image matrix must have been initialized with an image
     * @pos the file was created in bmp format with the information from the image matrix
     */
    public void writeImage(String output) {
        byte pad = 0;
        try {
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(header);
            byte[] pixel = new byte[3];

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    // Read the 3 bytes of the pixel (B, G, R)
                    pixel[0] = image[i][j][0];
                    pixel[1] = image[i][j][1];
                    pixel[2] = image[i][j][2];
                    fos.write(pixel);
                }
                for (int k = 0; k < padding; k++)
                    fos.write(pad);
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}