import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import memory.OS;

public class Option2 {
    // Constants for file processing
    private static final String FIELD_SEPARATOR = "=";
    private static final String REFERENCE_SEPARATOR = ",";
    private static final int PAGE_SIZE_LINE_NUMBER = 1;
    private static final int PAGE_COUNT_LINE_NUMBER = 5;
    private static final int METADATA_LINES_COUNT = 6;

    // Instance variables
    private ArrayList<String[]> references = new ArrayList<>();
    private OS os;
    private Integer pageCount;
    private Integer pageSize;

    /**
     * Constructor that initializes Option2 with frame count and references file
     */
    public Option2(int frameCount, String fileName) {
        loadReferences(fileName);
        initializeOS(frameCount);
    }

    /**
     * Starts the operating system simulation and waits for completion
     */
    public void startAndWait() {
        os.start();
    }

    /**
     * Loads memory references from the specified file
     */
    public void loadReferences(String fileName) {
        try (BufferedReader br = createFileReader(fileName)) {
            processReferenceFile(br);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    /**
     * Creates a buffered reader for the specified file
     */
    private BufferedReader createFileReader(String fileName) throws IOException {
        return new BufferedReader(new FileReader(fileName));
    }

    /**
     * Processes the reference file line by line
     */
    private void processReferenceFile(BufferedReader br) throws IOException {
        String line;
        int lineNumber = 0;

        while ((line = br.readLine()) != null) {
            lineNumber++;
            processLine(line, lineNumber);
        }
    }

    /**
     * Processes an individual line from the reference file
     */
    private void processLine(String line, int lineNumber) {
        // Skip empty lines
        if (line.trim().isEmpty()) {
            return;
        }

        if (lineNumber == PAGE_SIZE_LINE_NUMBER) {
            extractPageSize(line);
        } else if (lineNumber == PAGE_COUNT_LINE_NUMBER) {
            extractPageCount(line);
        } else if (lineNumber >= METADATA_LINES_COUNT) {
            addReference(line);
        }
    }

    /**
     * Extracts the page size from the appropriate metadata line
     */
    private void extractPageSize(String line) {
        String[] parts = line.split(FIELD_SEPARATOR);
        pageSize = Integer.parseInt(parts[1]);
    }

    /**
     * Extracts the page count from the appropriate metadata line
     */
    private void extractPageCount(String line) {
        String[] parts = line.split(FIELD_SEPARATOR);
        pageCount = Integer.parseInt(parts[1]);
    }

    /**
     * Adds a parsed reference to the references list
     */
    private void addReference(String line) {
        String[] referenceParts = line.split(REFERENCE_SEPARATOR);
        references.add(referenceParts);
    }

    /**
     * Initializes the operating system with loaded parameters
     */
    private void initializeOS(int frameCount) {
        os = new OS(frameCount, pageCount, pageSize, references);
    }

    /**
     * Handles IO exceptions during file processing
     */
    private void handleIOException(IOException e) {
        e.printStackTrace();
    }
}