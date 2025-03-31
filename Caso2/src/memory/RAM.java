package memory;

/**
 * Represents the physical RAM memory with frames that can be allocated to pages
 */
public class RAM {
    // Constants
    private static final boolean FRAME_OCCUPIED = true;
    private static final boolean FRAME_FREE = false;

    // Frame status array: true = occupied, false = free
    private boolean[] frames;

    /**
     * Creates a new RAM with the specified number of frames
     *
     * @param size The number of frames in RAM
     */
    public RAM(int size) {
        initializeFrames(size);
    }

    /**
     * Initializes the frames array with all frames set to free
     *
     * @param frameCount The number of frames to initialize
     */
    private void initializeFrames(int frameCount) {
        frames = new boolean[frameCount];
        // All frames are initially free (false) by default in Java
    }

    /**
     * Finds the first available free frame and marks it as occupied
     *
     * @return The index of the allocated frame, or null if no frames are available
     */
    public Integer findAvailableFrame() {
        for (int i = 0; i < frames.length; i++) {
            if (!frames[i]) {
                markFrameAsOccupied(i);
                return i;
            }
        }
        return null;
    }

    /**
     * Marks the specified frame as occupied
     *
     * @param frameIndex The index of the frame to mark as occupied
     */
    private void markFrameAsOccupied(int frameIndex) {
        frames[frameIndex] = FRAME_OCCUPIED;
    }
}