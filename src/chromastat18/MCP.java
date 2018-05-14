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

import java.io.IOException;

import com.pi4j.gpio.extension.mcp.MCP23017GpioProvider;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;


/**
 * MCP allows the user to gain control over pins of an MCP23017 chip
 * @author WilliamKwok
 */
public class MCP {
    private final GpioController gpio;
    private final MCP23017GpioProvider provider;
    
    /**
     * Constructor for MCP
     * @param address Physical memory address of the MCP chip, found by using `i2cdetect -y 1` in bash
     * @throws com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException
     * @throws IOException 
     */
    public MCP(int address) throws  UnsupportedBusNumberException, IOException {
        this.gpio = GpioFactory.getInstance();
        this.provider = new MCP23017GpioProvider(I2CBus.BUS_1, address);
    }
    
    /**
     * Sets a pin to be an input pin and also returns the pin
     * @param pin Pin to set as an input
     * @return GpioPinDigitalInput of requested pin
     */
    public GpioPinDigitalInput input(Pin pin) {
        return gpio.provisionDigitalInputPin(provider, pin, "pin", PinPullResistance.PULL_UP);
    }
    
    /**
     * Sets requested pin to specified pinState
     * @param pin Pin to set output of
     * @param pinState LOW or HIGH
     * @return GpioPinDigitalOutput of requested pin
     */
    public GpioPinDigitalOutput output(Pin pin, PinState pinState){
        return gpio.provisionDigitalOutputPin(provider, pin, "pin", pinState);
    }
}
