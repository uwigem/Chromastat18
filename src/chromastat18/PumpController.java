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
 * @author WilliamKwok, RehaanBhimani
 */
public class PumpController extends Thread {
    private final ArrayList<SyringePump> pumps = new ArrayList<>();
    private ArrayList<Integer> pumpMoving = new ArrayList<>();
    private boolean calibrated = false;
    
    /**
     * PumpController's constructor creates the three syringe pumps.
     * The puns are constants that must be set if anything is wrong.
     * @param mcpProviderOne MCP provider in bus 0x20
     * @param mcpProviderTwo MCP provider in bus 0x21
     * @throws com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException
     * @throws IOException 
     */
    public PumpController(MCP mcpProviderOne, MCP mcpProviderTwo) throws I2CFactory.UnsupportedBusNumberException, IOException {
        // Set up pump parameters
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
        
        // Create the argument maps
        for(int i = 0; i < keys.length; i++) {
            inarg1.put(keys[i], pins1[i]);
            inarg2.put(keys[i], pins2[i]);
            inarg3.put(keys[i], pins3[i]);
        }
        
        // Create the pumps and add them to this.pumps
        pump1 = new SyringePump(inarg1, mcpProviderOne, mcpProviderTwo);
        pump2 = new SyringePump(inarg2, mcpProviderOne, mcpProviderTwo);
        pump3 = new SyringePump(inarg3, mcpProviderOne, mcpProviderTwo);
        pumps.add(pump1);
        pumps.add(pump2);
        pumps.add(pump3);
    }
    
    /**
     * Return a specified SyringePump. Use only for debugging!
     * @param pumpNumber Specify the pump. 0, 1, or 2.
     * @return The pump in question.
     */
    public SyringePump getPump(int pumpNumber) {
        return this.pumps.get(pumpNumber);
    }
    
    /**
     * @return whether a pump is moving or not (includes rest time)
     */
    public ArrayList<Integer> pumpMoving() {
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
        if(!this.pumpMoving.contains(pumpNumber)) {
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
            this.pumpMoving.add(i);
            this.pumps.get(i).calibrate();
            this.pumpMoving.clear();
        }
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
            // Check if the pumps have been calibrated or not
            if(this.calibrated) {
                //  Test all pumps for goalMismatch
                pumpMoving.clear();
                for(int i = 0; i < pumps.size(); i++) {
                    if(pumps.get(i).goalMismatch()){
                        pumpMoving.add(i);
                    }
                }
                // step all pumps with goal mismatch (in pumpMoving)                
                if(pumpMoving.size() > 0) {
                    try {
                        for(int i = 0; i < pumpMoving.size(); i++){
                            pumps.get(pumpMoving.get(i)).move();
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DummyPumpController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                //If the pump has not been calibrated, calibrate it.
                try {
                    this.calibrate();
                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(DummyPumpController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
