package memory.threads;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import memory.NRU;
import memory.OS;

public class Reader extends Thread {
    // Constants for actions
    private static final String READ_ACTION = "R";
    private static final String WRITE_ACTION = "W";

    // Constants for file operations
    private static final String DATA_FILE_PATH = "output/data.txt";
    private static final String FILE_ERROR_MESSAGE = "Error writing to file: ";

    // Constants for output formatting
    private static final String REPORT_SEPARATOR = "\n===============================";
    private static final String PAGE_SIZE_MESSAGE = "Page size: %d";
    private static final String FRAMES_MESSAGE = "Frames: %d";
    private static final String REFERENCES_MESSAGE = "References: %d";
    private static final String HITS_MESSAGE = "Hits: %d (%%%.2f)";
    private static final String FAULTS_MESSAGE = "Faults: %d (%%%.2f)";

    // Constants for page fault handling
    private static final String MODIFIED_PAGE_ERROR = "The modified page is not found in SWAP memory.";
    private static final String UNMODIFIED_PAGE_ERROR = "The unmodified page is not found on the hard drive.";

    // Constants for performance simulation
    private static final int REFERENCES_PER_SLEEP = 10000;
    private static final int SLEEP_MILLISECONDS = 1;
    private static final int SLEEP_NANOSECONDS = 0;

    // Instance variables
    private OS os;
    private ArrayList<String[]> references;
    private NRU nru;
    public static boolean working; // tells the inspector that the reader has finished reading references
    private int hits;
    private int faults;

    public Reader(OS os, ArrayList<String[]> references, NRU nru) {
        initializeFields(os, references, nru);
    }

    private void initializeFields(OS os, ArrayList<String[]> references, NRU nru) {
        this.os = os;
        this.references = references;
        this.nru = nru;
        this.hits = 0;
        this.faults = 0;
        working = true;
    }

    @Override
    public void run() {
        processAllReferences();
        finishExecution();
        displayReport();
        saveResultsToFile();
    }

    private void processAllReferences() {
        int referenceCount = 0;

        for (String[] reference : references) {
            processReference(reference);

            referenceCount++;
            checkForPeriodicSleep(referenceCount);
        }
    }

    private void processReference(String[] reference) {
        Integer page = extractPageNumber(reference);
        Integer offset = extractOffset(reference);
        String action = extractAction(reference);

        handlePageAccess(page, action);
    }

    private Integer extractPageNumber(String[] reference) {
        return Integer.parseInt(reference[1]);
    }

    private Integer extractOffset(String[] reference) {
        return Integer.parseInt(reference[2]);
    }

    private String extractAction(String[] reference) {
        return reference[3];
    }

    private void handlePageAccess(Integer page, String action) {
        boolean pageLoaded = checkIfPageLoaded(page);

        if (!pageLoaded) {
            handlePageFault(page);
        } else {
            handlePageHit();
        }

        updatePageBits(page, action);
    }

    private boolean checkIfPageLoaded(Integer page) {
        return os.pageTable.getFrame(page) != null;
    }

    private void handlePageFault(Integer page) {
        faults++;

        verifyPageLocation(page);
        loadPageIntoMemory(page);
    }

    private void verifyPageLocation(Integer page) {
        boolean modified = os.pageTable.getModifiedBit(page);
        boolean inSwap = os.swap.pageInSWAP(page);
        boolean inHardDrive = os.hardDrive.pageInHardDrive(page);

        if (modified) {
            verifyModifiedPageLocation(inSwap);
        } else {
            verifyUnmodifiedPageLocation(inHardDrive);
        }
    }

    private void verifyModifiedPageLocation(boolean inSwap) {
        if (!inSwap) {
            throw new IllegalStateException(MODIFIED_PAGE_ERROR);
        }
    }

    private void verifyUnmodifiedPageLocation(boolean inHardDrive) {
        if (!inHardDrive) {
            throw new IllegalStateException(UNMODIFIED_PAGE_ERROR);
        }
    }

    private void loadPageIntoMemory(Integer page) {
        Integer availableFrame = findAvailableFrame();

        if (availableFrame == null) {
            handleFullMemory(page);
        } else {
            assignFrameToPage(page, availableFrame);
        }
    }

    private Integer findAvailableFrame() {
        return os.ram.findAvailableFrame();
    }

    private void handleFullMemory(Integer page) {
        Integer freedFrame = nru.execute(page);
        assignFrameToPage(page, freedFrame);
    }

    private void assignFrameToPage(Integer page, Integer frame) {
        os.pageTable.setFrame(page, frame);
    }

    private void handlePageHit() {
        hits++;
    }

    private void updatePageBits(Integer page, String action) {
        if (READ_ACTION.equals(action)) {
            updateReadBits(page);
        } else if (WRITE_ACTION.equals(action)) {
            updateWriteBits(page);
        }
    }

    private void updateReadBits(Integer page) {
        os.pageTable.setReferenceBit(page, true);
    }

    private void updateWriteBits(Integer page) {
        os.pageTable.setReferenceBit(page, true);
        os.pageTable.setModifiedBit(page, true);
    }

    private void checkForPeriodicSleep(int referenceCount) {
        if (referenceCount % REFERENCES_PER_SLEEP == 0) {
            sleepForInterval();
        }
    }

    private void sleepForInterval() {
        sleep(SLEEP_MILLISECONDS, SLEEP_NANOSECONDS);
    }

    private void finishExecution() {
        working = false;
    }

    private void displayReport() {
        printReportHeader();
        printSimulationParameters();
        printPerformanceMetrics();
        printReportFooter();
    }

    private void printReportHeader() {
        System.out.println(REPORT_SEPARATOR);
    }

    private void printSimulationParameters() {
        System.out.println(String.format(PAGE_SIZE_MESSAGE, os.pageSize));
        System.out.println(String.format(FRAMES_MESSAGE, os.frameCount));
        System.out.println(String.format(REFERENCES_MESSAGE, references.size()));
    }

    private void printPerformanceMetrics() {
        double totalReferences = references.size();
        double hitPercentage = (double) hits * 100 / totalReferences;
        double faultPercentage = (double) faults * 100 / totalReferences;

        System.out.printf(HITS_MESSAGE + "%n", hits, hitPercentage);
        System.out.printf(FAULTS_MESSAGE, faults, faultPercentage);
    }

    private void printReportFooter() {
        System.out.println(REPORT_SEPARATOR);
    }

    private void saveResultsToFile() {
        try (BufferedWriter writer = createFileWriter()) {
            writeResultsToFile(writer);
        } catch (IOException e) {
            handleFileWriteError(e);
        }
    }

    private BufferedWriter createFileWriter() throws IOException {
        return new BufferedWriter(new FileWriter(DATA_FILE_PATH, true));
    }

    private void writeResultsToFile(BufferedWriter writer) throws IOException {
        writer.write(os.pageSize + " " + os.frameCount + " " + hits + " " + faults);
        writer.newLine();
    }

    private void handleFileWriteError(IOException e) {
        System.err.println(FILE_ERROR_MESSAGE + e.getMessage());
    }

    public void sleep(int millis, int nanos) {
        try {
            Thread.sleep(millis, nanos);
        } catch (InterruptedException e) {
            handleSleepInterruption(e);
        }
    }

    private void handleSleepInterruption(InterruptedException e) {
        e.printStackTrace();
    }

    public void saveInfo() {
        saveResultsToFile();
    }
}