package memory;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the Not Recently Used (NRU) page replacement algorithm
 */
public class NRU {
    // Constants
    private static final int NO_PAGE_FOUND = -1;
    private static final int FIRST_ELEMENT = 0;

    // Class priority classification constants
    private static final int CLASS_COUNT = 4;
    private static final int CLASS_NOT_REFERENCED_NOT_MODIFIED = 0;
    private static final int CLASS_NOT_REFERENCED_MODIFIED = 1;
    private static final int CLASS_REFERENCED_NOT_MODIFIED = 2;
    private static final int CLASS_REFERENCED_MODIFIED = 3;

    // Operating system reference
    private final OS os;

    /**
     * Creates a new NRU page replacement algorithm
     */
    public NRU(OS os) {
        this.os = os;
    }

    /**
     * Executes the NRU algorithm to free up a frame for a new page
     *
     * @param newPage The page that needs to be loaded
     * @return The frame that was freed for the new page
     */
    public Integer execute(int newPage) {
        int victimPage = selectCandidate();
        int freedFrame = freeVictimPage(victimPage);
        handleModifiedPage(victimPage);
        return freedFrame;
    }

    /**
     * Frees the frame associated with the victim page
     */
    private int freeVictimPage(int victimPage) {
        int frame = os.pageTable.getFrame(victimPage);
        os.pageTable.freeFrame(victimPage);
        return frame;
    }

    /**
     * Handles a modified page by copying it to swap if needed
     */
    private void handleModifiedPage(int victimPage) {
        if (os.pageTable.getModifiedBit(victimPage)) {
            os.swap.copyPage(victimPage);
        }
    }

    /**
     * Selects a victim page based on the NRU algorithm
     */
    public int selectCandidate() {
        List<List<Integer>> classes = classifyPages();
        return selectVictimFromClasses(classes);
    }

    /**
     * Classifies all pages in RAM into the four NRU classes
     */
    private List<List<Integer>> classifyPages() {
        List<List<Integer>> classes = initializeClasses();

        for (int page = 0; page < os.pageTable.size(); page++) {
            classifyPage(page, classes);
        }

        return classes;
    }

    /**
     * Initializes the four classes for page classification
     */
    private List<List<Integer>> initializeClasses() {
        List<List<Integer>> classes = new ArrayList<>(CLASS_COUNT);

        for (int i = 0; i < CLASS_COUNT; i++) {
            classes.add(new ArrayList<>());
        }

        return classes;
    }

    /**
     * Classifies a single page into one of the four NRU classes
     */
    private void classifyPage(int page, List<List<Integer>> classes) {
        Integer frame = os.pageTable.getFrame(page);

        if (frame == null) {
            return; // Skip pages not in RAM
        }

        boolean referenceBit = os.pageTable.getReferenceBit(page);
        boolean modifiedBit = os.pageTable.getModifiedBit(page);
        int classIndex = determineClassIndex(referenceBit, modifiedBit);

        classes.get(classIndex).add(page);
    }

    /**
     * Determines the class index based on reference and modified bits
     */
    private int determineClassIndex(boolean referenceBit, boolean modifiedBit) {
        if (!referenceBit && !modifiedBit) {
            return CLASS_NOT_REFERENCED_NOT_MODIFIED;
        } else if (!referenceBit && modifiedBit) {
            return CLASS_NOT_REFERENCED_MODIFIED;
        } else if (referenceBit && !modifiedBit) {
            return CLASS_REFERENCED_NOT_MODIFIED;
        } else {
            return CLASS_REFERENCED_MODIFIED;
        }
    }

    /**
     * Selects the first victim page from the lowest non-empty class
     */
    private int selectVictimFromClasses(List<List<Integer>> classes) {
        for (List<Integer> classPages : classes) {
            if (!classPages.isEmpty()) {
                return classPages.get(FIRST_ELEMENT);
            }
        }

        return NO_PAGE_FOUND;
    }
}