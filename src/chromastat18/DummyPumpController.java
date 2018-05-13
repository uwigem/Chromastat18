/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromastat18;

import com.pi4j.gpio.extension.mcp.MCP23017Pin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author WilliamKwok
 */
public class DummyPumpController extends Thread {
    private ArrayList<DummyPump> pumps = new ArrayList<>();
    private ArrayList<SyringePump> pumps2 = new ArrayList<>();
    private int pumpMoving = -1;
    private boolean calibrated = false;
    
    
    private final MCP mcpProviderOne;
    private final MCP mcpProviderTwo;
    
    
    
    
    public DummyPumpController() throws I2CFactory.UnsupportedBusNumberException, IOException {
        for(int i = 0; i < 3; i++) {
            pumps.add(new DummyPump());
        }
        mcpProviderOne = new MCP(0x20);
        mcpProviderTwo = new MCP(0x21);
        Map<String, Pin> inarg1 = new HashMap<>();
        Map<String, Pin> inarg2 = new HashMap<>();
        Map<String, Pin> inarg3 = new HashMap<>();
        String[] keys = {"dirPin", "stepPin", "enablePin", "minPin", "maxPin"};
        Pin[] pins1 = {MCP23017Pin.GPIO_A7, MCP23017Pin.GPIO_A6, MCP23017Pin.GPIO_A5, MCP23017Pin.GPIO_B5, MCP23017Pin.GPIO_B1};
        Pin[] pins2 = {MCP23017Pin.GPIO_B0, MCP23017Pin.GPIO_B1, MCP23017Pin.GPIO_B7, MCP23017Pin.GPIO_B4, MCP23017Pin.GPIO_B2};
        Pin[] pins3 = {MCP23017Pin.GPIO_A4, MCP23017Pin.GPIO_A3, MCP23017Pin.GPIO_A2, MCP23017Pin.GPIO_B6, MCP23017Pin.GPIO_B3};
        SyringePump pump1;
        SyringePump pump2;
        SyringePump pump3;
    
    
        for(int i = 0; i < keys.length; i++) {
            inarg1.put(keys[i], pins1[i]);
            inarg2.put(keys[i], pins2[i]);
            inarg3.put(keys[i], pins3[i]);
        }
        
        pump1 = new SyringePump(inarg1, mcpProviderOne, mcpProviderTwo);
        pump2 = new SyringePump(inarg2, mcpProviderOne, mcpProviderTwo);
        pump3 = new SyringePump(inarg3, mcpProviderOne, mcpProviderTwo);
        pumps2.add(pump1);
        pumps2.add(pump2);
        pumps2.add(pump3);
        System.out.println("got here");
    }
    
    public void testpump() throws InterruptedException {
        System.out.println("test");
        pumps2.get(0).calibrate();
    }
    
    public int pumpMoving() {
        return this.pumpMoving;
    }
    
    public double getPumpPos(int pumpNumber) {
       return this.pumps.get(pumpNumber).position();
    }
    
    public void setNewGoal(int pumpNumber, int newGoal) {
        if(this.pumpMoving == -1) {
            pumps.get(pumpNumber).setNewGoal(newGoal);
        }
    }
    
    public boolean isCalibrated() {
        return this.calibrated;
    }
    
    public void calibrate() throws InterruptedException {
        for(int i = 0; i < 3; i++) {
            this.pumpMoving = i;
            this.pumps.get(i).calibrate();
        }
        this.pumpMoving = -1;
        this.calibrated = true;
    }
    
    public void recalibrate() {
        this.calibrated = false;
    }
    
    @Override
    public void run() {
        while(true) {
            if(this.calibrated) {
                ArrayList<Boolean> pumpsMoving = new ArrayList<>();
                for(int i = 0; i < pumps.size(); i++) {
                    pumpsMoving.add(pumps.get(i).goalMismatch());
                }
                int pumpMovingIndex = pumpsMoving.indexOf(true);
                if(pumpMovingIndex >= 0) {
                    try {
                        this.pumpMoving = pumpMovingIndex;
                        pumps.get(pumpMovingIndex).move();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DummyPumpController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    this.pumpMoving = -1;
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DummyPumpController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                try {
                    this.calibrate();
                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(DummyPumpController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
