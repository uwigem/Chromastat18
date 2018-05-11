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
    private boolean pumpMoving = false;
    public DummyPumpController() {
        for(int i = 0; i < 3; i++) {
            pumps.add(new DummyPump());
        }
    }
    
    public boolean pumpMoving() {
        return this.pumpMoving;
    }
    
    public void setNewGoal(int pumpNumber, int newGoal) {
        if(!this.pumpMoving) {
            pumps.get(pumpNumber).setNewGoal(newGoal);
        }
    }
    
    @Override
    public void run() {
        while(true) {
            ArrayList<Boolean> pumpsMoving = new ArrayList<>();
            for(int i = 0; i < pumps.size(); i++) {
                pumpsMoving.add(pumps.get(i).goalMismatch());
            }
            int pumpMovingIndex = pumpsMoving.indexOf(true);
            if(pumpMovingIndex >= 0) {
                pumps.get(pumpMovingIndex).move();
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DummyPumpController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
