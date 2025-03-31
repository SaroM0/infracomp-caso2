import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    // Constants
    private static final String DEFAULT_IMAGE_PATH = "images/caso2-parrotspeq.bmp";
    private static final String REFERENCES_FILE_PATH = "references.txt";
    private static final String DATA_OUTPUT_PATH = "output/data.txt";
    private static final int DEFAULT_PAGE_SIZE = 512;
    private static final int DEFAULT_FRAME_COUNT = 4;
    private static final boolean GENERATE_GRAPH_DATA = true;
    private static final int[] AVAILABLE_PAGE_SIZES = { 512, 1024, 2048 };
    private static final int[] AVAILABLE_FRAME_COUNTS = { 4, 6 };
    private static final String DATA_HEADER = "page_size frames_assigned number_hits number_faults";

    public static void main(String[] args) throws Exception {
        runSingleScenario();

        if (GENERATE_GRAPH_DATA) {
            runMultipleScenarios();
        }

        displayCompletionMessage();
    }

    private static void runSingleScenario() throws Exception {
        Option1 option1 = generateReferences(DEFAULT_PAGE_SIZE);
        simulateMemoryManagement(DEFAULT_FRAME_COUNT);
    }

    private static Option1 generateReferences(int pageSize) {
        Option1 option1 = new Option1(pageSize, Main.DEFAULT_IMAGE_PATH);
        option1.calculateNumberOfPages();
        option1.simulateReferences();
        option1.writeReferences();
        return option1;
    }

    private static void simulateMemoryManagement(int frameCount) {
        Option2 option2 = new Option2(frameCount, REFERENCES_FILE_PATH);
        option2.startAndWait();
    }

    private static void runMultipleScenarios() throws Exception {
        clearDataFile();

        for (int pageSize : AVAILABLE_PAGE_SIZES) {
            generateReferences(pageSize);

            for (int frameCount : AVAILABLE_FRAME_COUNTS) {
                simulateMemoryManagement(frameCount);
            }
        }
    }

    private static void clearDataFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_OUTPUT_PATH, false))) {
            bw.write(DATA_HEADER);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static void displayCompletionMessage() {
        System.out.println("\n|-----------------------------------------------------------|");
        System.out.println("    Simulation completed successfully!");
        System.out.println("    Run the Plotter.py script to view the graphs");
        System.out.println("|-----------------------------------------------------------|");
    }
}
