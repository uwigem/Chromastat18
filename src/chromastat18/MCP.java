/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromastat18;

import java.io.IOException;

import com.pi4j.gpio.extension.mcp.MCP23017GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP23017Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;


/**
 *
 * @author WilliamKwok
 */
public class MCP {
    private final GpioController gpio;
    private final MCP23017GpioProvider provider;
    public MCP(int address) throws  UnsupportedBusNumberException, IOException {
        this.gpio = GpioFactory.getInstance();
        this.provider = new MCP23017GpioProvider(I2CBus.BUS_1, address);
    }
    
    /**
     * @param pin Pin from MCP23017Pin.java
     * @return GpioPinDigitalInput of requested pin
     */
    public GpioPinDigitalInput input(Pin pin) {
        return gpio.provisionDigitalInputPin(provider, pin, PinPullResistance.PULL_UP);
    }
    
    /**
     * Sets requested pin to specified pinState
     * @param pin Pin from MCP23017Pin.java
     * @param pinState LOW or HIGH
     * @return GpioPinDigitalOutput of requested pin
     * TODO: Unsure if works as intended.
     */
    public GpioPinDigitalOutput output(Pin pin, PinState pinState){
        return gpio.provisionDigitalOutputPin(provider, pin, pinState);
    }
}
