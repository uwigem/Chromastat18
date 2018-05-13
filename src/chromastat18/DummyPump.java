/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromastat18;

/**
 * DummyPump allows control of a fake syringe pump and its fake switches
 * @author WilliamKwok
 */
public class DummyPump {
    private int maxPosition;
    private int currPosition;
    private int goal = 0;
    int delay = 1;
    
    /**
     * Constructor for a single fake syringe pump
     * @pre inArgs must have ONLY these keys: dirPin, stepPin, enablePin, minPin, maxPin
     * @param inArgs Map of pins
     * @param mcp 
     */
    public DummyPump() {
        this.maxPosition = 1000;
        this.currPosition = (int)(Math.random() * this.maxPosition);
    }
    
    /**
     * minPressed() has the position 0 be the minimum limit
     * @return returns if minimum switch is pressed (empty syringe)
     */
    public boolean minPressed() {
        return this.currPosition <= 0;
    }
    
    /**
     * maxPressed() has the position 1000 be the max limit.
     * @return returns if the maximum switch is pressed (full syringe)
     */
    public boolean maxPressed() {
        return this.currPosition >= 1000;
    }
    
    /**
     * Calibrate will move the syringe pump to the minimum position it detects,
     * then to the maximum, and stores the maximum position, and keeps track
     * of current position.
     * @throws InterruptedException 
     */
    public void calibrate() throws InterruptedException {
        while(!this.minPressed()) {
            Thread.sleep(this.delay);
            Thread.sleep(this.delay);
            this.currPosition = this.currPosition - 1;
        }
        this.currPosition = 0;
        this.refill();
        this.goal = 0;
    }
    
    /**
     * Refill refills the syringe and resets the maximum position of the syringe
     * @throws InterruptedException 
     */
    public void refill() throws InterruptedException {
        while(!this.maxPressed()) {
            Thread.sleep(this.delay);
            Thread.sleep(this.delay);
            this.currPosition = this.currPosition + 1;
        }
        this.maxPosition = this.currPosition;
    }
    
    /**
     * Position will give you the percentage of the position the syringe is at
     * @return Position in terms of percentage full
     */
    public double position() {
        return (double)this.currPosition/this.maxPosition;
    }

    /**
     * Set a new goal for the pump
     * @param newGoal negative or positive number (dispense or fill)
     */
    public void setNewGoal(int newGoal) {
        this.goal = newGoal;
    }

    /**
     * Checks if goal is met or not
     * @return boolean for if a goal is met or not
     */
    public boolean goalMismatch() {
        return this.goal != 0;
    }

    /**
     * Move will move the pump if there is not a mismatch.
     * @throws InterruptedException 
     */
    public void move() throws InterruptedException {
        int toAdd = 1;
        if(goal < 0) {
            toAdd = -1;
        }
        while(this.goalMismatch()) {
            if((toAdd == 1 && !this.maxPressed()) || (toAdd == -1 && !this.minPressed())) {
                Thread.sleep(this.delay);
                Thread.sleep(this.delay);
                this.currPosition = this.currPosition + toAdd;
                this.goal = this.goal - toAdd;
            } else {
                if(this.minPressed()) {
                    this.currPosition = 0;
                    this.refill();
                } else {
                    this.maxPosition = this.currPosition;
                    this.goal = 0;
                }
            }
        }
        Thread.sleep(2000); // force a pause after movement
    }
}
