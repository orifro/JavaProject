import java.io.Serializable;

interface Condition extends Serializable {
    boolean ApplyCondition(Example example);
}
