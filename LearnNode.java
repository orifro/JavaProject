import java.io.Serializable;
import java.util.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.CountDownLatch;

class LearnNode implements Serializable {
    
    private byte label;
    private boolean leaf;
    private LearnNode leftNode;
    private LearnNode rightNode;
    private float examplesSize;
    Map<Byte, Set<Example>> leftLeafExamples;
    Map<Byte, Set<Example>> rightLeafExamples;
    Map<Byte, Set<Example>> examples;
    Condition condition;
    float NewEntropy;
    float MaxInformationGain;


    LearnNode(byte label) {
        this.label = label;
        leftNode = null;
        rightNode = null;
        condition = null;
        leftLeafExamples = null;
        rightLeafExamples = null;
        examplesSize = 0;
        MaxInformationGain = -1;
        examples = InitMap();
        NewEntropy = Float.POSITIVE_INFINITY;
        leaf = true;
    }

    static Map<Byte, Set<Example>> InitMap() {
        Map<Byte, Set<Example>> examplesMap = new HashMap<>();
        for (byte label = 0; label <= 9; label++) {
            examplesMap.put(label, new HashSet<>());
        }
        return examplesMap;
    }

    @SuppressWarnings("unchecked")
    void InformationGainCalculation(Set<Condition> conditions, ThreadPoolExecutor threadPool) {
        examplesSize = SizeCalculation(examples);  // מחשב כמה תמונות יש לך לכל ספרה
        CountDownLatch countDownLatch = new CountDownLatch(conditions.size());
        float CalcEntropy = EntropyCalculation(examples, examplesSize);  // מחשב את האנתרופיה
        for (Condition condition : conditions) {
            threadPool.execute(() -> {
                Pair<Map<Byte, Set<Example>>> splittedExamples = SplitExamples(condition);     // בודק לכל לייבל האם כל הדוגמאות שלו בטווח. אם כן מכניס את הדוגמא למפה הימנית ואם לא מכניס אותה לשמאלית
                float conditionEntropy = ConditionEntropyCalculation(splittedExamples); // האנתרופיה המשוקללת
                synchronized (this) {
                    if (conditionEntropy < NewEntropy) {
                        NewEntropy = conditionEntropy;
                        this.condition = condition;
                        leftLeafExamples = splittedExamples.getFirst();
                        rightLeafExamples = splittedExamples.getSecond();
                    }
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        float IG = CalcEntropy - NewEntropy;
        MaxInformationGain = examplesSize * IG;
    }


    // מחשב את האנתרופיה

    private float EntropyCalculation(Map<Byte, Set<Example>> examples, float size) {
        if (size != 0) {
        float entropy = 0;
        Set<Example> EachLabal;
        for (byte i = 0; i <= 9; i++) {
            EachLabal = examples.get(i);
            if (!EachLabal.isEmpty()) {
                entropy += (EachLabal.size() / size) * Math.log10(size / EachLabal.size());
            }
        }
        return entropy;
        } else {
            return 0;
        }
    }

    // מחשב כמה תמונות יש לך לכל ספרה

    private int SizeCalculation(Map<Byte, Set<Example>> examples) {
        int size = 0;
        for (byte i = 0; i <= 9; i++) {
            size = size + examples.get(i).size();
        }
        return size;
    }
    // האנתרופיה המשוקללת

    float ConditionEntropyCalculation(Pair<Map<Byte, Set<Example>>> Examples) {
        int leftSize = SizeCalculation(Examples.getFirst());
        int rightSize = SizeCalculation(Examples.getSecond());
        float leftEntropy = EntropyCalculation(Examples.getFirst(), leftSize);
        float rightEntropy = EntropyCalculation(Examples.getSecond(), rightSize);
        if (examplesSize != 0) {
            return (((leftSize / examplesSize) * leftEntropy) + ((rightSize / examplesSize) * rightEntropy));
        } else {
            return 0;
        }
    }

    private void LeafToNode(Pair<LearnNode> leafPair) {
        label = -1;
        MaxInformationGain = -1;
        leaf = false;
        this.leftNode = leafPair.getFirst();
        this.rightNode = leafPair.getSecond();
    }
    // מחזיר את הדוגמאות של כל תת עץ ואת הלייבל הנפוץ ביותר עבור כל תת עץ

    Pair<LearnNode> Split() {
        LearnNode leftLeaf = new LearnNode((byte)10);
        LearnNode rightLeaf = new LearnNode((byte)10);
        leftLeaf.examples = leftLeafExamples;
        rightLeaf.examples = rightLeafExamples;
        leftLeaf.label = LearnTree.FindMostCommonLabel(leftLeaf.examples);
        rightLeaf.label = LearnTree.FindMostCommonLabel(rightLeaf.examples);
        Pair<LearnNode> leafPair = new Pair<LearnNode>(leftLeaf,rightLeaf);
        LeafToNode(leafPair);
        return leafPair;
    }

    // בודק לכל לייבל האם כל הדוגמאות שלו בטווח. אם כן מכניס את הדוגמא למפה הימנית ואם לא מכניס אותה לשמאלית

    Pair<Map<Byte, Set<Example>>> SplitExamples(Condition condition) {
        Map<Byte, Set<Example>> leftExamples = InitMap();
        Map<Byte, Set<Example>> rightExamples = InitMap();
        Set<Example> EachLabal;
        for (byte i = 0; i <= 9; i++) {
            EachLabal = examples.get(i);
            for (Example example : EachLabal) {
                if (condition.ApplyCondition(example)) {
                    rightExamples.get(i).add(example);
                } else {
                    leftExamples.get(i).add(example);
                }
            }
        }
        return new Pair<Map<Byte, Set<Example>>>(leftExamples, rightExamples);
    }


    byte ThePredictedLabel(Example example) {
        if (leaf) {
            return label;
        }
        if (condition.ApplyCondition(example)) {
            return rightNode.ThePredictedLabel(example);
        } else {
            return leftNode.ThePredictedLabel(example);
            }
    }
}

