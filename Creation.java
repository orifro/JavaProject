import java.io.File;
import java.util.*;

public class Creation {

    static List<Example> CreateTheExampleList(String examplesFileName) {
        List<Example> examplesList = new LinkedList<>();
        try (Scanner scanner = new Scanner(new File(examplesFileName))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                    Example example = new Example(line);
                        examplesList.add(example);
            }
        } catch (Exception e) {
            System.err.println("Unable to open or read the examples file");
            System.exit(1);
        }
        return examplesList;
    }
}
