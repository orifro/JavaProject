public class AdvancedCondition implements Condition{

    private short row;
    private short column;
    private short minBrightness;
    private short maxBrightness;

    AdvancedCondition(short row,short column, short minBrightness, short maxBrightness) {
        this.row = row;
        this.column = column;
        this.minBrightness = minBrightness;
        this.maxBrightness = maxBrightness;
    }
 
    @Override
    public boolean ApplyCondition(Example example) {
        int sumOfPixels = 0;
        sumOfPixels = sumOfPixels + 5*example.pixels[28*row + column] 
                                  + example.pixels[28*(row-1) + column] 
                                  + example.pixels[28*(row+1) + column]
                                  + example.pixels[28*row + (column-1)] 
                                  + example.pixels[28*row + (column+1)];
        sumOfPixels = sumOfPixels / 9;
        boolean min = (sumOfPixels >= minBrightness);
        boolean max = (sumOfPixels <= maxBrightness);
        return (min && max);
    }
}

// מנרמל את כל הפיקסלים ובודק אם הם בטווח
