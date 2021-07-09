import java.io.Serializable;

class Example implements Serializable {

    short[] pixels;
    byte label;

    Example(String line) {
        String[] splitLine = line.split(",");
        label = Byte.parseByte(splitLine[0]);
        pixels = new short[(short)784];
        for(short pixel = 1; pixel <= (short)784; pixel++) {
            pixels[pixel-1] = Short.parseShort(splitLine[pixel]);
        }
    }
}



// מפצל את הייצוג של התמונה ללייבל ולמערך של מספרים (שורטים)