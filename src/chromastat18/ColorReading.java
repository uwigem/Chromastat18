/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromastat18;

/**
 *
 * @author WilliamKwok
 */
/**
 * Simple subclass containing numerical color reading information
 */
public class ColorReading {

    public int red, blue, green, clear;

    public ColorReading(int r, int b, int g, int c) {
        this.red = r;
        this.blue = b;
        this.green = g;
        this.clear = c;
    }

    public int getRed() {
        return this.red;
    }

    public int getBlue() {
        return this.blue;
    }

    public int getGreen() {
        return this.green;
    }

    public int getClear() {
        return this.clear;
    }

    @Override
    public String toString() {
        return "Red:" + Integer.toString(red)
                + "\nBlue:" + Integer.toString(blue)
                + "\nGreen:" + Integer.toString(green)
                + "\nClear:" + Integer.toString(clear);
    }
}