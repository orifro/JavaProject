import java.util.List;

public class Predict {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Invalid input");
        } else {
        String treeFileName = args[0];
        String testFileName = args[1];
        BinaryLearnTree tree = IO.ImportTree(treeFileName);
        List<Example> examples = Creation.CreateTheExampleList(testFileName);
        PrintLabels(examples, tree);
        }
    }

    private static void PrintLabels(List<Example> examples, BinaryLearnTree tree) {
        for (Example example: examples) {
            System.out.println(tree.root.ThePredictedLabel(example));
        }
    }
}
