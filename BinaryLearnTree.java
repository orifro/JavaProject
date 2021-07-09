import java.io.Serializable;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

class BinaryLearnTree implements Serializable {
    LearnNode root;
    PriorityQueue<LearnNode> EntropyHeap;

    BinaryLearnTree(Map<Byte, Set<Example>> examples, Set<Condition> condition, byte power, ThreadPoolExecutor threadPool) {
        this.EntropyHeap = new PriorityQueue<>((int)Math.pow(2,power), new NodeComparator());
        this.root = new LearnNode(LearnTree.FindMostCommonLabel(examples));
        this.root.examples = examples;
        this.root.InformationGainCalculation(condition, threadPool);
        EntropyHeap.add(root);
    }


    float CorrectAnswersPercent(Map<Byte, Set<Example>> validationSet) {
        Set<Example> Examples;
        int size = 0;
        int errors = 0;
        for (byte label = 0; label <= 9; label++) {
            Examples = validationSet.get(label);
            size = size + Examples.size();
            for (Example example : Examples) {
                if (root.ThePredictedLabel(example) != label) {
                    errors++;
                }
            }
        }
        if (size != 0){
        return (((float)errors / size) * 100);
        } else {
        return 0;
        }
    }
}
    

