package memory;
import java.util.ArrayList;

import memory.threads.Inspector;
import memory.threads.Reader;

/**
 * Represents the operating system that manages the virtual memory simulation
 */
public class OS {
    // Constants
    private static final boolean PRELOAD_PAGES = false;
    private static final String THREAD_INTERRUPTED_ERROR = "Thread was interrupted: ";

    // System parameters
    public int frameCount;
    public int pageCount;
    public int pageSize;

    // System components
    public PageTable pageTable;
    public RAM ram;
    public SWAP swap;
    public HardDrive hardDrive;

    // Thread components
    private Inspector inspector;
    private Reader reader;

    /**
     * Creates a new operating system with the specified configuration
     */
    public OS(int frameCount, int pageCount, int pageSize, ArrayList<String[]> references) {
        initializeSystemParameters(frameCount, pageCount, pageSize);
        initializeMemoryComponents();
        initializeThreadComponents(references);
    }

    /**
     * Initializes the system parameters
     */
    private void initializeSystemParameters(int frameCount, int pageCount, int pageSize) {
        this.frameCount = frameCount;
        this.pageCount = pageCount;
        this.pageSize = pageSize;
    }

    /**
     * Initializes the memory components of the system
     */
    private void initializeMemoryComponents() {
        ram = new RAM(frameCount);
        swap = new SWAP();
        hardDrive = new HardDrive(pageCount);
        pageTable = new PageTable(pageCount, frameCount, PRELOAD_PAGES);
    }

    /**
     * Initializes the thread components of the system
     */
    private void initializeThreadComponents(ArrayList<String[]> references) {
        NRU nru = new NRU(this);
        reader = new Reader(this, references, nru);
        inspector = new Inspector(this);
    }

    /**
     * Starts the simulation and waits for completion
     */
    public void start() {
        startThreads();
        waitForThreadCompletion();
    }

    /**
     * Starts the reader and inspector threads
     */
    private void startThreads() {
        reader.start();
        inspector.start();
    }

    /**
     * Waits for both threads to complete
     */
    private void waitForThreadCompletion() {
        try {
            reader.join();
            inspector.join();
        } catch (InterruptedException e) {
            handleInterruptedException(e);
        }
    }

    /**
     * Handles thread interruption exceptions
     */
    private void handleInterruptedException(InterruptedException e) {
        System.err.println(THREAD_INTERRUPTED_ERROR + e.getMessage());
        e.printStackTrace();
    }
}