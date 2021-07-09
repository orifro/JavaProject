import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class LearnTree {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Invalid input");
        } else {
        byte version = 0;
        byte MaxPowerOfTwo = 0;
        float validationInPercent = 0;
        List<Example> examplesList;
        Map<Byte, Set<Example>> MapOfExamples;
        Map<Byte, Set<Example>> TrainingExamples;
        Map<Byte, Set<Example>> ValidationExamples;
        Set<Condition> filteredExamples;
        ThreadPoolExecutor threadPool;
        try {
            version = Byte.parseByte(args[0]);
            if (version != 1 && version != 2) {
                throw new Exception("version need to be 1 or 2");
            }
            validationInPercent = Float.parseFloat(args[1]);
            if (validationInPercent < 0 || validationInPercent > 100) {
                throw new Exception("validationInPercent need to be between 0 to 100");
            }
            MaxPowerOfTwo = Byte.parseByte(args[2]);
            if (MaxPowerOfTwo < 0) {
                throw new Exception("need to be positive number");
            }
        } catch (Exception e) {
            System.err.println("parameters are not correct");
            System.exit(1);
        }
        String trainingFileName = args[3];
        String outputFileName = args[4];
            
        threadPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
        MapOfExamples = LearnNode.InitMap(); // יוצר מפה לפי המבנה הנתון
        ValidationExamples = LearnNode.InitMap();
        TrainingExamples = LearnNode.InitMap();
        examplesList = Creation.CreateTheExampleList(trainingFileName); // יוצר רשימה של דוגמאות מקובץ מדגם האימון
        ValidationCreate(validationInPercent, examplesList, MapOfExamples, ValidationExamples,TrainingExamples); // יוצרת את מדגם האימון, מדגם הולידציה
        filteredExamples = applyVersion(version); // יוצר רשימת תמונות לפי התנאי המתאים
        BestTreeCreated(examplesList.size(), MaxPowerOfTwo,filteredExamples,MapOfExamples, ValidationExamples, TrainingExamples,outputFileName, threadPool); // מחזיר את המערך של כל אחוזי הטעייה
        threadPool.shutdownNow();
    }
}

    // יוצרת את מדגם האימון, מדגם הולידציה

    private static void ValidationCreate(float validationInPercent, List<Example> exampleList, Map<Byte, Set<Example>> MapOfExamples,
                                            Map<Byte, Set<Example>> ValidationExamples, Map<Byte, Set<Example>> TrainingExamples) {
        Iterator it = exampleList.iterator();
        int numOfValidationExamples = (int)(Math.ceil(exampleList.size() * (validationInPercent / 100))); // מספר תמונות הוליזציה לפי האחוז שנבחר
        Example example;
        for (int i = numOfValidationExamples; i < exampleList.size(); i++) { // יוצר את מדגם האימון
            example = (Example)it.next();
            MapOfExamples.get(example.label).add(example);
            TrainingExamples.get(example.label).add(example);
        }
        for (int i = 0; i < numOfValidationExamples; i++) {  // יוצר את מדגם הולידציה
            example = (Example)it.next();
            MapOfExamples.get(example.label).add(example);
            ValidationExamples.get(example.label).add(example);
        }
    }

    // יוצר רשימת תמונות לפי התנאי המתאים

    private static Set<Condition> applyVersion(byte version) {
        Set<Condition> filteredExamples = new HashSet<>();
        if (version == 1) {   //  יוצרים רשימת תמונות בהתאם לתנאי הראשון
            for (short i = 0; i < (short)784; i++) {
                Condition condition = new SimpleCondition(i, (short)128, (short)255);
                filteredExamples.add(condition);
            }
        } else {  // יוצרים רשימת תמונות בהתאם לתנאי השני
            for (short i = 1; i < 27; i = (short)(i+1)) {
                for (short j = 1; j < 27; j = (short)(j + 1)) {
                    Condition condition = new AdvancedCondition(i, j, (short)25, (short)255);
                    filteredExamples.add(condition);
                }
            }
        }
        return filteredExamples;
    }

    // מחשב לכל עלה את הלייבל הכי נפוץ שלו ומוסיף אותו לתור עדיפויות

    private static void CreateNodes(BinaryLearnTree tree, int numOfNodes, Set<Condition> filteredExamples, boolean lastIteration,
                                 ThreadPoolExecutor threadPool) {
        int numOfSplits = (int)Math.ceil(((float)numOfNodes)/2);
        Pair<LearnNode> Leaves;
        for (int i = 0; i < numOfSplits; i++) {
            Leaves = tree.EntropyHeap.poll().Split(); // מחזיר את האיבר הראשון בתור
            if (!lastIteration || (i < (numOfSplits-1))) {
                (Leaves.getFirst()).InformationGainCalculation(filteredExamples, threadPool);
                (Leaves.getSecond()).InformationGainCalculation(filteredExamples, threadPool);
                tree.EntropyHeap.add(Leaves.getFirst());
                tree.EntropyHeap.add(Leaves.getSecond());
            }
        }
    }

    // מחזיר את המערך של כל אחוזי הטעייה

    private static void BestTreeCreated(int numOfAllExamples, byte MaxPowerOfTwo,Set<Condition> filteredExamples,Map<Byte, Set<Example>> MapOfExamples, Map<Byte, Set<Example>> ValidationExamples, Map<Byte, Set<Example>> TrainingExamples,  String outputFileName,
                                          ThreadPoolExecutor threadPool) {
        byte IndexWithSmallestErrors = 0;
        float[] treesVector = new float[MaxPowerOfTwo+1];
        BinaryLearnTree tree = new BinaryLearnTree(TrainingExamples, filteredExamples, MaxPowerOfTwo, threadPool);
        treesVector[0] = tree.CorrectAnswersPercent(ValidationExamples);
        for (byte power = 1; power <= MaxPowerOfTwo; power++) {
            CreateNodes(tree, (int)(Math.pow(2,power-1)), filteredExamples, (power == MaxPowerOfTwo), threadPool);
            treesVector[power] = tree.CorrectAnswersPercent(ValidationExamples);
        }
        int length = treesVector.length;
        for (byte i = 0; i < length; i++) {
            if (treesVector[i] < treesVector[IndexWithSmallestErrors]) {
                IndexWithSmallestErrors = i;
            }
        }
        BinaryLearnTree TheBestTreeCreated = new BinaryLearnTree(MapOfExamples, filteredExamples, IndexWithSmallestErrors, threadPool);
        CreateNodes(TheBestTreeCreated, (int)(Math.pow(2,IndexWithSmallestErrors)), filteredExamples, true, threadPool);

        System.out.println("num: " + numOfAllExamples);
        System.out.println("error: " + ((int)Math.ceil(TheBestTreeCreated.CorrectAnswersPercent(MapOfExamples))));
        System.out.println("size: " + (int)Math.pow(2, IndexWithSmallestErrors));

        IO.outputTree(TheBestTreeCreated, outputFileName); // כותב לקובץ פלט שהוא הייצוג של העץ הטוב ביותר

    }

    static byte FindMostCommonLabel(Map<Byte, Set<Example>> examples) {
        byte mostCommonLabel = 0;
        for (byte label = 0; label <= 9; label++) {
            if (examples.get(label).size() > examples.get(mostCommonLabel).size()) {
                mostCommonLabel = label;
            }
        }
        return mostCommonLabel;
    }
}
