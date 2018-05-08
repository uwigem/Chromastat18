/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromastat18;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import java.util.Map;

/**
 * SyringePump allows control of a single syringe pump and its limit switches
 * @author WilliamKwok
 */
public class SyringePump {
    private final GpioPinDigitalOutput dirPin;
    private final GpioPinDigitalOutput stepPin;
    private final GpioPinDigitalOutput enablePin;
    private final GpioPinDigitalInput minStop;
    private final GpioPinDigitalInput maxStop;
    int delay = 1;
    
    
    /**
     * Constructor for a single syringe pump
     * @pre inArgs must have ONLY these keys: dirPin, stepPin, enablePin, minPin, maxPin
     * @param inArgs Map of pins
     * @param mcp 
     */
    public SyringePump(Map<String, Pin> inArgs, MCP mcp) {
        //Pin dirPin = inArgs.get("test");
        this.dirPin = mcp.output(inArgs.get("dirPin"), PinState.LOW);
        this.stepPin = mcp.output(inArgs.get("stepPin"), PinState.LOW);
        this.enablePin = mcp.output(inArgs.get("enablePin"), PinState.LOW);
        this.minStop = mcp.input(inArgs.get("minPin"));
        this.maxStop = mcp.input(inArgs.get("maxPin"));
    }
    
    /**
     * @return returns if minimum switch is pressed (empty syringe)
     */
    public boolean minPressed() {
        return this.minStop.isHigh();
    }
    
    /**
     * @return returns if the maximum switch is pressed (full syringe)
     */
    public boolean maxPressed() {
        return this.maxStop.isHigh();
    }
    
    /**
     * Moves the syringe a specified amount of steps and direction
     * @param steps steps to move
     * @param dispense direction. If dispensing, this will be true.
     */
    public void takeSteps(int steps, boolean dispense) throws InterruptedException {
        if(dispense) {
            this.dirPin.low();
        } else {
            this.dirPin.high();
        }
        for(int i = 0; i < steps; i++) {
            Thread.sleep(this.delay);
            this.stepPin.high();
            Thread.sleep(this.delay);
            this.stepPin.low();
        }
    }
}
