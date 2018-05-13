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
 * PumpController controls three Syringe Pumps. This is ran on a separate thread
 * than the GUI calculations, as Thread.sleep is needed to run the pumps. 
 * We only HAVE to move one pump at a time, but more testing must occur to 
 * determine if the Pi has more than 4 threads we can run java in. If it does,
 * then we can have each pump have its own thread.
 * @author WilliamKwok
 */
public class PumpController extends Thread {
    private ArrayList<SyringePump> pumps = new ArrayList<>();
    private int pumpMoving = -1;
    private boolean calibrated = false;
    
    /**
     * PumpController's constructor creates the three syringe pumps.
     * The puns are constants that must be set if anything is wrong.
     * @throws com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException
     * @throws IOException 
     */
    public PumpController(MCP mcpProviderOne, MCP mcpProviderTwo) throws I2CFactory.UnsupportedBusNumberException, IOException {
        Map<String, Pin> inarg1 = new HashMap<>();
        Map<String, Pin> inarg2 = new HashMap<>();
        Map<String, Pin> inarg3 = new HashMap<>();
        String[] keys = {"dirPin", "stepPin", "enablePin", "minPin", "maxPin"};
        Pin[] pins1 = {MCP23017Pin.GPIO_A2, MCP23017Pin.GPIO_A1, MCP23017Pin.GPIO_A0, MCP23017Pin.GPIO_A6, MCP23017Pin.GPIO_A2};
        Pin[] pins2 = {MCP23017Pin.GPIO_A5, MCP23017Pin.GPIO_A4, MCP23017Pin.GPIO_A3, MCP23017Pin.GPIO_A5, MCP23017Pin.GPIO_A3};
        Pin[] pins3 = {MCP23017Pin.GPIO_B0, MCP23017Pin.GPIO_A7, MCP23017Pin.GPIO_A6, MCP23017Pin.GPIO_A4, MCP23017Pin.GPIO_A7};
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
        pumps.add(pump1);
        pumps.add(pump2);
        pumps.add(pump3);
    }
    
    public SyringePump getPump(int pumpNumber) {
        return this.pumps.get(pumpNumber);
    }
    
    /**
     * @return whether a pump is moving or not (includes rest time)
     */
    public int pumpMoving() {
        return this.pumpMoving;
    }
    
    /**
     * Get the position of a pump
     * @param pumpNumber 0, 1, or 2.
     * @return position as a percentage (double) from 0 to 1
     */
    public double getPumpPos(int pumpNumber) {
       return this.pumps.get(pumpNumber).position();
    }
    
    /**
     * Sets the goal of a specified pump
     * @param pumpNumber 0, 1, or 2.
     * @param newGoal any negative or positive number. Don't go overboard with this!
     */
    public void setNewGoal(int pumpNumber, int newGoal) {
        if(this.pumpMoving == -1) {
            pumps.get(pumpNumber).setNewGoal(newGoal);
        }
    }
    
    /**
     * @return if the calibration procedure has been run or not
     */
    public boolean isCalibrated() {
        return this.calibrated;
    }
    
    /**
     * Calibrate will run each pump through a calibration method, then set
     * its calibrated state to true.
     * @throws InterruptedException 
     */
    public void calibrate() throws InterruptedException {
        for(int i = 0; i < 3; i++) {
            this.pumpMoving = i;
            this.pumps.get(i).calibrate();
        }
        this.pumpMoving = -1;
        this.calibrated = true;
    }
    
    /**
     * recalibrates the pumps
     */
    public void recalibrate() {
        this.calibrated = false;
    }
    
    /**
     * Run will run the controller. Only one pump will be allowed to move at a 
     * time. 
     */
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
