package memory.threads;

import memory.OS;

/**
 * Thread responsible for periodically resetting reference bits in the page table
 */
public class Inspector extends Thread {
    // Constants
    private static final int INSPECTION_INTERVAL_MS = 1;
    private static final String THREAD_INTERRUPT_ERROR = "Inspector thread was interrupted: ";

    // Instance variables
    private final OS os;

    /**
     * Creates a new Inspector for the specified operating system
     */
    public Inspector(OS os) {
        this.os = os;
    }

    /**
     * Main execution method for the Inspector thread
     */
    @Override
    public void run() {
        monitorMemoryUsage();
    }

    /**
     * Continuously monitors memory usage and resets reference bits
     * until the Reader thread finishes its work
     */
    private void monitorMemoryUsage() {
        boolean readerWorking = true;

        while (readerWorking) {
            waitForNextInspection();
            resetAllReferenceBits();
            readerWorking = checkIfReaderIsWorking();
        }
    }

    /**
     * Waits for the specified inspection interval before proceeding
     */
    private void waitForNextInspection() {
        try {
            Thread.sleep(INSPECTION_INTERVAL_MS);
        } catch (InterruptedException e) {
            handleInterruptedException(e);
        }
    }

    /**
     * Handles an InterruptedException that may occur during sleep
     */
    private void handleInterruptedException(InterruptedException e) {
        System.err.println(THREAD_INTERRUPT_ERROR + e.getMessage());
        e.printStackTrace();
    }

    /**
     * Resets the reference bit for all pages in the page table
     */
    private void resetAllReferenceBits() {
        os.pageTable.resetReferenceBits();
    }

    /**
     * Checks if the Reader thread is still working
     *
     * @return true if the Reader is still working, false otherwise
     */
    private boolean checkIfReaderIsWorking() {
        return Reader.working;
    }
}