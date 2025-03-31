package memory;
import java.util.HashSet;

/**
 * Represents a hard drive storage for pages in the virtual memory system
 */
public class HardDrive {
    // Constants
    private static final int INITIAL_CAPACITY = 16; // Default HashSet initial capacity

    // Instance variables
    private final HashSet<Integer> memory;

    /**
     * Creates a new hard drive with the specified number of total pages
     */
    public HardDrive(int totalPages) {
        memory = initializeMemory(totalPages);
    }

    /**
     * Initializes the memory with all available page numbers
     */
    private HashSet<Integer> initializeMemory(int totalPages) {
        HashSet<Integer> initialMemory = new HashSet<>(INITIAL_CAPACITY);
        loadInitialPages(initialMemory, totalPages);
        return initialMemory;
    }

    /**
     * Loads all initial pages into the hard drive
     */
    private void loadInitialPages(HashSet<Integer> memory, int totalPages) {
        for (int i = 0; i < totalPages; i++) {
            memory.add(i);
        }
    }

    /**
     * Copies a page to the hard drive
     */
    public void copyPage(int page) {
        memory.add(page);
    }

    /**
     * Removes a page from the hard drive
     */
    public void freePage(int page) {
        memory.remove(page);
    }

    /**
     * Checks if a page is present on the hard drive
     */
    public boolean pageInHardDrive(int page) {
        return memory.contains(page);
    }
}