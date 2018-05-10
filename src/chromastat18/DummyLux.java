/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromastat18;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;

/**
 * Dummy Lux returns random numbers for testing
 */
public class DummyLux {

    public DummyLux() throws 
            IOException, I2CFactory.UnsupportedBusNumberException, InterruptedException{
        
    }

    /**
     * 
     * @return Reading as a double (unitless)
     * @throws IOException if device isn't ready for I/O
     * @throws InterruptedException Can happen during thread sleep
     */
    public double getReading()
            throws IOException, InterruptedException {

        return rawToLux();
    }

    /**
     * Calculates lux measurement based on both diodes
     *
     * @return Lux value for the sensor
     */
    private double rawToLux() {

        return Math.random();
    }

}