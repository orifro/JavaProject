import java.io.Serializable;
import java.util.Comparator;

public class NodeComparator implements Comparator<LearnNode>, Serializable {

    @Override
    public int compare(LearnNode node1, LearnNode node2) {
        return Float.compare(node2.MaxInformationGain, node1.MaxInformationGain);
    }
}

// משווה בין 2 נודים
