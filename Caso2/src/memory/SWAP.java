package memory;
import java.util.HashSet;

/**
 * Represents a SWAP memory area for storing modified pages
 * that have been moved out of RAM
 */
public class SWAP {
    // Constants
    private static final int INITIAL_CAPACITY = 16; // Default HashSet initial capacity

    // Instance variables
    private final HashSet<Integer> memory;

    /**
     * Creates a new empty SWAP memory area
     */
    public SWAP() {
        memory = initializeMemory();
    }

    /**
     * Initializes an empty memory area for SWAP
     */
    private HashSet<Integer> initializeMemory() {
        return new HashSet<>(INITIAL_CAPACITY);
    }

    /**
     * Copies a page to SWAP memory
     */
    public void copyPage(int page) {
        memory.add(page);
    }

    /**
     * Removes a page from SWAP memory
     */
    public void freePage(int page) {
        memory.remove(page);
    }

    /**
     * Checks if a page is present in SWAP memory
     */
    public boolean pageInSWAP(int page) {
        return memory.contains(page);
    }
}