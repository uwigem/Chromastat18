/*
 * Copyright (C) 2018 WilliamKwok
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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