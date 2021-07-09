import java.util.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class IO {

     static void outputTree(BinaryLearnTree TheBestTreeCreated, String outputFileName) {
        try {
            File file = new File(outputFileName);
            if (file.exists())
                if (!file.delete()) {
                    throw new Exception();
                }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream obj = new ObjectOutputStream(fileOutputStream);
            obj.writeObject(TheBestTreeCreated);
            obj.close();
            fileOutputStream.close();
        } catch (Exception e) {
            System.err.println("Unable to create the file");
            System.exit(1);
        }
    }

    static BinaryLearnTree ImportTree(String treeFileName) {
        BinaryLearnTree tree = new BinaryLearnTree(LearnNode.InitMap(), new HashSet<>(),(byte)1, null);
        try {
            File file = new File(treeFileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream obj = new ObjectInputStream(fileInputStream);
            tree = (BinaryLearnTree) obj.readObject();
            obj.close();
            fileInputStream.close();
        } catch (Exception e) {
            System.err.println("Unable to open the file");
            System.exit(1);
        }
        return tree;
    }
}
