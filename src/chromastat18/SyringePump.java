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

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import java.util.Map;

/**
 * DummyPump simulates a syringe pump
 * @author WilliamKwok, RehaanBhimani
 */
public class SyringePump {
    private final GpioPinDigitalOutput dirPin;
    private final GpioPinDigitalOutput stepPin;
    private final GpioPinDigitalOutput enablePin;
    private final GpioPinDigitalInput minStop;
    private final GpioPinDigitalInput maxStop;
    private static final int FORCE_REST_TIME = 1000;
    private int maxPosition;
    private int currPosition;
    private int goal = 0;
    private boolean calibrating; //default is false
    int delay = 1;
    
    
    /**
     * Constructor for a single syringe pump
     * @pre inArgs must have ONLY these keys: dirPin, stepPin, enablePin, minPin, maxPin
     * @param inArgs Map of pins
     * @param mcp 
     */
    public SyringePump(Map<String, Pin> inArgs, MCP mcp1, MCP mcp2) {
        this.dirPin = mcp1.output(inArgs.get("dirPin"), PinState.LOW);
        this.stepPin = mcp1.output(inArgs.get("stepPin"), PinState.LOW);
        this.enablePin = mcp1.output(inArgs.get("enablePin"), PinState.LOW);
        this.minStop = mcp2.input(inArgs.get("minPin"));
        this.maxStop = mcp2.input(inArgs.get("maxPin"));
        this.maxPosition = 0;
        this.currPosition = 0;
    }
    
    /**
     * @return returns if minimum switch is pressed (empty syringe)
     */
    public boolean minPressed() {
        return this.minStop.isLow();
    }
    
    /**
     * @return returns if the maximum switch is pressed (full syringe)
     */
    public boolean maxPressed() {
        return this.maxStop.isLow();
    }
    
    /**
     * Moves the syringe a specified amount of steps and direction
     * @param steps steps to move
     * @param dispense direction. If dispensing, this will be true.
     */
    public void takeSteps(int steps, boolean dispense) throws InterruptedException {
        int toAdd = 1;
        if(dispense) {
            this.dirPin.low();
            toAdd = -1;
        } else {
            this.dirPin.high();
        }
        for(int i = 0; i < steps; i++) {
            if(!this.minPressed() && !this.maxPressed()) {
                Thread.sleep(this.delay);
                this.stepPin.high();
                Thread.sleep(this.delay);
                this.stepPin.low();
                this.currPosition = this.currPosition + toAdd;
            } else {
                if(this.minPressed()) {
                    this.currPosition = 0;
                    this.refill();
                } else {
                    this.maxPosition = this.currPosition;
                }
            }
        }
    }
    
    /**
     * Calibrate will move the syringe pump to the minimum position it detects,
     * then to the maximum, and stores the maximum position, and keeps track
     * of current position.
     * @throws InterruptedException 
     */
    public void calibrate() throws InterruptedException {
        calibrating = true;
        this.setNewGoal(-1 * Integer.MAX_VALUE);
    }
    
    /**
     * Refill refills the syringe and resets the maximum position of the syringe
     * @throws InterruptedException 
     */
    public void refill() throws InterruptedException {
        this.setNewGoal(Integer.MAX_VALUE);
    }
    
    /**
     * Position will give you the percentage of the position the syringe is at
     * @return Position in terms of percentage full
     */
    public double position() {
        return (double)this.currPosition/this.maxPosition;
    }
    
    /**
     * Sets a new goal.
     * @param newGoal amount to move the syringe
     */
    void setNewGoal(int newGoal) {
        this.goal = newGoal;
    }
    
    /**
     * @return if there is still some movement the pump must make
     */
    boolean goalMismatch() {
        return this.goal != 0;
    }
    
    /**
     * Move will move the syringe pump to the set goal, if there is one. 
     * Otherwise, it'll just do nothing. It refills automatically as well.
     * @throws InterruptedException 
     */
    void move() throws InterruptedException {
        int toAdd = 1;
        this.dirPin.high();
        if(goal < 0) {
            toAdd = -1;
            this.dirPin.low();
        }
        if(calibrating){
            System.out.println("getting here3");
        }
        if((toAdd == 1 && !this.maxPressed()) || (toAdd == -1 && !this.minPressed())) {
            Thread.sleep(this.delay);
            this.stepPin.high();
            Thread.sleep(this.delay);
            this.stepPin.low();
            this.currPosition = this.currPosition + toAdd;
            this.goal = this.goal - toAdd;
        } else {
            if(this.minPressed()) {
                if(calibrating){
                    this.goal = 0;
                    calibrating = false;
                    System.out.println("getting here");
                }
                this.currPosition = 0;
                this.refill();
            } else {
                this.maxPosition = this.currPosition;
                this.goal = 0;
            }
        }
    }

    
}
