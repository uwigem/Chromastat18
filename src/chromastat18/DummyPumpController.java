/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chromastat18;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author WilliamKwok
 */
public class DummyPumpController extends Thread {
    private ArrayList<DummyPump> pumps = new ArrayList<>();
    private int pumpMoving = -1;
    private boolean calibrated = false;
    public DummyPumpController() {
        for(int i = 0; i < 3; i++) {
            pumps.add(new DummyPump());
        }
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
    
    public void calibrate() throws InterruptedException {
        for(int i = 0; i < 3; i++) {
            this.pumpMoving = i;
            this.pumps.get(i).calibrate();
        }
        this.pumpMoving = -1;
        this.calibrated = true;
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
