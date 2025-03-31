package memory;

/**
 * Manages the page table for virtual memory mapping
 */
public class PageTable {
    // Constants
    private static final boolean BIT_UNSET = false;
    private static final boolean REFERENCE_BIT_DEFAULT = false;
    private static final boolean MODIFIED_BIT_DEFAULT = false;

    // The page table entries
    private Entry[] entries;

    /**
     * Creates a new page table with the specified number of pages and frames
     *
     * @param pageCount The number of pages in the virtual address space
     * @param frameCount The number of frames in physical memory
     * @param preload Whether to preload the first frameCount pages
     */
    public PageTable(int pageCount, int frameCount, boolean preload) {
        initializeEntries(pageCount);

        if (preload) {
            preloadInitialPages(frameCount);
        }
    }

    /**
     * Initializes all page table entries
     */
    private void initializeEntries(int pageCount) {
        entries = new Entry[pageCount];

        for (int i = 0; i < pageCount; i++) {
            entries[i] = new Entry();
        }
    }

    /**
     * Preloads initial pages into frames when requested
     */
    private void preloadInitialPages(int frameCount) {
        for (int i = 0; i < frameCount; i++) {
            entries[i].frame = i;
        }
    }

    /**
     * Gets the reference bit for a page
     */
    public synchronized boolean getReferenceBit(int n) {
        return entries[n].R;
    }

    /**
     * Sets the reference bit for a page
     */
    public synchronized void setReferenceBit(int n, boolean R) {
        entries[n].R = R;
    }

    /**
     * Resets reference bits for all pages
     */
    public synchronized void resetReferenceBits() {
        for (Entry entry : entries) {
            entry.R = BIT_UNSET;
        }
    }

    /**
     * Gets the modified bit for a page
     */
    public synchronized boolean getModifiedBit(int n) {
        return entries[n].M;
    }

    /**
     * Sets the modified bit for a page
     */
    public synchronized void setModifiedBit(int n, boolean M) {
        entries[n].M = M;
    }

    /**
     * Gets the frame assigned to a page
     */
    public synchronized Integer getFrame(int n) {
        return entries[n].frame;
    }

    /**
     * Returns the number of pages in the page table
     */
    public int size() {
        return entries.length;
    }

    /**
     * Frees the frame assigned to a page
     */
    public void freeFrame(int page) {
        entries[page].frame = null;
    }

    /**
     * Assigns a frame to a page
     */
    public void setFrame(int page, int frame) {
        entries[page].frame = frame;
    }

    /**
     * Represents a single entry in the page table
     */
    public class Entry {
        Integer frame = null; // Address in RAM (to a frame)
        boolean R = REFERENCE_BIT_DEFAULT; // Reference bit
        boolean M = MODIFIED_BIT_DEFAULT; // Modified bit
    }
}