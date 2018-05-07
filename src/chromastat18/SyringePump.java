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
 *
 * @author WilliamKwok
 */
public class SyringePump {
    
    public SyringePump(Map<String, Pin> inArgs, MCP mcp) {
        //Pin dirPin = inArgs.get("test");
        GpioPinDigitalOutput dirPin = mcp.output(inArgs.get("dirPin"), PinState.LOW);
        GpioPinDigitalOutput stepPin = mcp.output(inArgs.get("stepPin"), PinState.LOW);
        GpioPinDigitalOutput enablePin = mcp.output(inArgs.get("enablePin"), PinState.LOW);
        GpioPinDigitalInput minStop = mcp.input(inArgs.get("minPin"));
        GpioPinDigitalInput maxStop = mcp.input(inArgs.get("maxPin"));
    }
}
