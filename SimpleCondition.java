public class SimpleCondition implements Condition {

    private short index;
    private short minBrightness;
    private short maxBrightness;

    SimpleCondition(short index, short minBrightness, short maxBrightness) {
        this.index = index;
        this.minBrightness = minBrightness;
        this.maxBrightness = maxBrightness;
    }

    @Override
    public boolean ApplyCondition(Example example) {
        boolean min = (example.pixels[index] >= minBrightness);
        boolean max = (example.pixels[index] <= maxBrightness);
        return (min && max);
    }
}

// בודק שהפיקסלים של הדוגמא בטווח הנכון