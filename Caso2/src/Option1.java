import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import image.Image;

public class Option1 {
    // Constants for file fields
    private static final String PAGE_SIZE_FIELD = "TP=";
    private static final String ROW_COUNT_FIELD = "NF=";
    private static final String COL_COUNT_FIELD = "NC=";
    private static final String REF_COUNT_FIELD = "NR=";
    private static final String PAGE_COUNT_FIELD = "NP=";

    // Constants for image operations
    private static final int BYTES_PER_PIXEL = 3; // RGB components
    private static final int SOBEL_FILTER_SIZE = 36; // 9 elements of 4 bytes (4*9 = 36)
    private static final int FILTER_SIZE = 3; // 3x3 kernel
    private static final int SOBEL_ROW_SIZE = 3; // Each row in Sobel matrix has 3 integers
    private static final int BYTES_PER_INT = 4; // Size of an integer in bytes

    // Constants for reference operations
    private static final String IMAGE_RED_FORMAT = "Image[%d][%d].r,%d,%d,R";
    private static final String IMAGE_GREEN_FORMAT = "Image[%d][%d].g,%d,%d,R";
    private static final String IMAGE_BLUE_FORMAT = "Image[%d][%d].b,%d,%d,R";
    private static final String SOBEL_X_FORMAT = "SOBEL_X[%d][%d],%d,%d,R";
    private static final String SOBEL_Y_FORMAT = "SOBEL_Y[%d][%d],%d,%d,R";
    private static final String OUTPUT_RED_FORMAT = "Ans[%d][%d].r,%d,%d,W";
    private static final String OUTPUT_GREEN_FORMAT = "Ans[%d][%d].g,%d,%d,W";
    private static final String OUTPUT_BLUE_FORMAT = "Ans[%d][%d].b,%d,%d,W";

    private static final String REFERENCES_FILE_PATH = "references.txt";
    private static final String FILE_ERROR_MESSAGE = "Error writing to file: ";

    // Instance variables
    private int pageSize;
    private int rowCount;
    private int colCount;
    private int refCount;
    private int pageCount;
    private Image image = null;
    private ArrayList<String> references = new ArrayList<>();
    private int[] start_imageIn, start_imageOut, start_sobelX, start_sobelY;

    public Option1(int pageSize, String fileName) {
        this.pageSize = pageSize;
        loadImage(fileName);
    }

    private void loadImage(String fileName) {
        image = new Image(fileName);
        rowCount = image.height;
        colCount = image.width;
    }

    public void calculateNumberOfPages() {
        int totalBytes = calculateTotalMemoryRequirements();
        calculatePageCount(totalBytes);
        calculateMatrixStartPositions();
    }

    private int calculateTotalMemoryRequirements() {
        int bytesImageIn = calculateImageBytes();
        int bytesImageOut = bytesImageIn;
        int bytesSobelX = SOBEL_FILTER_SIZE;
        int bytesSobelY = SOBEL_FILTER_SIZE;

        return bytesImageIn + bytesImageOut + bytesSobelX + bytesSobelY;
    }

    private int calculateImageBytes() {
        return rowCount * colCount * BYTES_PER_PIXEL;
    }

    private void calculatePageCount(int totalBytes) {
        pageCount = (int) Math.ceil((double) totalBytes / pageSize);
    }

    private void calculateMatrixStartPositions() {
        int byteOffset = 0;

        // Set start position for input image (always at beginning)
        start_imageIn = new int[]{0, 0};

        // Calculate start positions for other matrices
        byteOffset += calculateImageBytes();
        start_sobelX = calculateMatrixPosition(byteOffset);

        byteOffset += SOBEL_FILTER_SIZE;
        start_sobelY = calculateMatrixPosition(byteOffset);

        byteOffset += SOBEL_FILTER_SIZE;
        start_imageOut = calculateMatrixPosition(byteOffset);
    }

    private int[] calculateMatrixPosition(int byteOffset) {
        return new int[]{byteOffset / pageSize, byteOffset % pageSize};
    }

    public void simulateReferences() {
        simulateSobelFilterExecution();
        updateReferenceCount();
    }

    private void simulateSobelFilterExecution() {
        // Iterate through the image pixels (excluding border)
        for (int i = 1; i < image.height - 1; i++) {
            for (int j = 1; j < image.width - 1; j++) {
                processPixelNeighborhood(i, j);
                generateOutputReferences(i, j);
            }
        }
    }

    private void processPixelNeighborhood(int centerRow, int centerCol) {
        // Apply the Sobel X and Y masks to the 3x3 neighborhood
        for (int ki = -1; ki <= 1; ki++) {
            for (int kj = -1; kj <= 1; kj++) {
                int row = centerRow + ki;
                int col = centerCol + kj;

                // Generate input image references (RGB)
                generateImageInputReferences(row, col);

                // Generate filter references
                int filterRow = ki + 1;
                int filterCol = kj + 1;
                generateSobelXReferences(filterRow, filterCol);
                generateSobelYReferences(filterRow, filterCol);
            }
        }
    }

    private void generateImageInputReferences(int row, int col) {
        int byteOffset = calculatePixelByteOffset(row, col);

        // Add references for red, green, and blue components
        references.add(formatImageRedReference(row, col, byteOffset));
        references.add(formatImageGreenReference(row, col, byteOffset));
        references.add(formatImageBlueReference(row, col, byteOffset));
    }

    private int calculatePixelByteOffset(int row, int col) {
        return BYTES_PER_PIXEL * (row * image.width + col);
    }

    private String formatImageRedReference(int row, int col, int byteOffset) {
        return String.format(IMAGE_RED_FORMAT,
                row, col,
                calculatePageNumber(start_imageIn[0], start_imageIn[1], byteOffset, 0),
                calculateOffset(start_imageIn[1], byteOffset, 0));
    }

    private String formatImageGreenReference(int row, int col, int byteOffset) {
        return String.format(IMAGE_GREEN_FORMAT,
                row, col,
                calculatePageNumber(start_imageIn[0], start_imageIn[1], byteOffset, 1),
                calculateOffset(start_imageIn[1], byteOffset, 1));
    }

    private String formatImageBlueReference(int row, int col, int byteOffset) {
        return String.format(IMAGE_BLUE_FORMAT,
                row, col,
                calculatePageNumber(start_imageIn[0], start_imageIn[1], byteOffset, 2),
                calculateOffset(start_imageIn[1], byteOffset, 2));
    }

    private int calculatePageNumber(int startPage, int startOffset, int byteOffset, int componentOffset) {
        return startPage + (startOffset + componentOffset + byteOffset) / pageSize;
    }

    private int calculateOffset(int startOffset, int byteOffset, int componentOffset) {
        return (startOffset + componentOffset + byteOffset) % pageSize;
    }

    private void generateSobelXReferences(int row, int col) {
        int byteOffset = calculateFilterByteOffset(row, col);
        String reference = formatSobelXReference(row, col, byteOffset);

        // The same position is used for all three color components
        references.add(reference);
        references.add(reference);
        references.add(reference);
    }

    private int calculateFilterByteOffset(int row, int col) {
        return BYTES_PER_INT * (row * SOBEL_ROW_SIZE + col);
    }

    private String formatSobelXReference(int row, int col, int byteOffset) {
        return String.format(SOBEL_X_FORMAT,
                row, col,
                calculatePageNumber(start_sobelX[0], start_sobelX[1], byteOffset, 0),
                calculateOffset(start_sobelX[1], byteOffset, 0));
    }

    private void generateSobelYReferences(int row, int col) {
        int byteOffset = calculateFilterByteOffset(row, col);
        String reference = formatSobelYReference(row, col, byteOffset);

        // The same position is used for all three color components
        references.add(reference);
        references.add(reference);
        references.add(reference);
    }

    private String formatSobelYReference(int row, int col, int byteOffset) {
        return String.format(SOBEL_Y_FORMAT,
                row, col,
                calculatePageNumber(start_sobelY[0], start_sobelY[1], byteOffset, 0),
                calculateOffset(start_sobelY[1], byteOffset, 0));
    }

    private void generateOutputReferences(int row, int col) {
        int byteOffset = calculatePixelByteOffset(row, col);

        // Generate write references for output image
        references.add(formatOutputRedReference(row, col, byteOffset));
        references.add(formatOutputGreenReference(row, col, byteOffset));
        references.add(formatOutputBlueReference(row, col, byteOffset));
    }

    private String formatOutputRedReference(int row, int col, int byteOffset) {
        return String.format(OUTPUT_RED_FORMAT,
                row, col,
                calculatePageNumber(start_imageOut[0], start_imageOut[1], byteOffset, 0),
                calculateOffset(start_imageOut[1], byteOffset, 0));
    }

    private String formatOutputGreenReference(int row, int col, int byteOffset) {
        return String.format(OUTPUT_GREEN_FORMAT,
                row, col,
                calculatePageNumber(start_imageOut[0], start_imageOut[1], byteOffset, 1),
                calculateOffset(start_imageOut[1], byteOffset, 1));
    }

    private String formatOutputBlueReference(int row, int col, int byteOffset) {
        return String.format(OUTPUT_BLUE_FORMAT,
                row, col,
                calculatePageNumber(start_imageOut[0], start_imageOut[1], byteOffset, 2),
                calculateOffset(start_imageOut[1], byteOffset, 2));
    }

    private void updateReferenceCount() {
        refCount = references.size();
    }

    public void writeReferences() {
        try (PrintWriter writer = new PrintWriter(REFERENCES_FILE_PATH)) {
            writeMetadata(writer);
            writeReferencesList(writer);
        } catch (FileNotFoundException e) {
            System.err.println(FILE_ERROR_MESSAGE + e.getMessage());
        }
    }

    private void writeMetadata(PrintWriter writer) {
        writer.println(PAGE_SIZE_FIELD + pageSize);
        writer.println(ROW_COUNT_FIELD + rowCount);
        writer.println(COL_COUNT_FIELD + colCount);
        writer.println(REF_COUNT_FIELD + refCount);
        writer.println(PAGE_COUNT_FIELD + pageCount);
    }

    private void writeReferencesList(PrintWriter writer) {
        for (String reference : references) {
            writer.println(reference);
        }
    }
}