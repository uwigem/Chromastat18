/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromastat18;

import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;

/**
 * Dummy Lux returns random numbers for testing, simulates a Lux sensor
 * @author WilliamKwok
 */
public class DummyLux {

    /**
     * Constructor. Does nothing.
     * @throws IOException
     * @throws com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException
     * @throws InterruptedException 
     */
    public DummyLux() throws 
            IOException, I2CFactory.UnsupportedBusNumberException, 
            InterruptedException{
    }

    /**
     * returns a faux reading 
     * @return Reading as a double (unitless)
     * @throws IOException if device isn't ready for I/O
     * @throws InterruptedException Can happen during thread sleep
     */
    public double getReading()
            throws IOException, InterruptedException {
        return rawToLux();
    }

    /**
     * Creates a random double from 0 to 1
     * @return Fake double that simulates a random double from 0 to 1
     */
    private double rawToLux() {

        return Math.random();
    }

}