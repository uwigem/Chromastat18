/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromastat18;

import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;

/**
 * Main class for Chromastat 2018
 * @author WilliamKwok
 */
public class Chromastat18 {

    /**
     * Invokes the UI. This is literally all it does.
     * All the methods and such are called in the UI.
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, I2CFactory.UnsupportedBusNumberException, InterruptedException {
        Chromastat18UI form = new Chromastat18UI();
        form.setVisible(true);
    }
    
    
    
}
