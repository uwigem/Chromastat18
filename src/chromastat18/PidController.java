/*
 * Copyright (C) 2018 Sea Eun Lee
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

import chromastat18.RgbSensor.ColorReading;

public class PidController {
    private final RgbSensor sensor;
    private final float kP, kI, kD;

    // this.running set to true will make the program start running
    private boolean running;
    private final PumpController pumps;

    /**
     * Constructor
     * @param pumps pump controller so you can control pumps from the file
     * @throws java.lang.Exception
     */
    public PidController (PumpController pumps) throws Exception{
        // instantiate color sensor or something
        this.pumps = pumps;
        this.sensor = new RgbSensor();
        this.kP = 50;
        this.kI = 50;
        this.kD = 50;
        this.running = false;
    }
    /**
     * gets the RGB and returns it as an int array
     *
     * This can be an arraylist too, doesn't matter much
     *
     * int array uses less memory but it's trivial
     * @return 
     * @throws java.lang.Exception
     */
    public int[] getRGB() throws Exception {
        // does memory calculations that should already exist
        int[] rgbArray = new int[3];
        chromastat18.RgbSensor.ColorReading temp;
        temp = sensor.getReading();
        rgbArray[0] = temp.getRed();
        rgbArray[1] = temp.getGreen();
        rgbArray[2] = temp.getBlue();
        return rgbArray;
    }

    /**
     * The purpose of this function is the get the RGB and turn it
     * into a 1 dimensional number to work off of for the PID.
     *
     * Note that in the video I linked, there was 1 dimension of movement
     * the dude had to account for, overshoot or undershoot. I really believe
     * that while it'd be ideal to use PID for all 3 values of the RGB
     * at once, it is smarter to conjoin them to determine quantitatively
     * what the concentration of X or Y is, in this case pH, and then
     * from there, determine if you need more acid or base
     *
     * @return {int} calculation of the pH based on the rgb
     *              this can be determined by functions, linear scaling
     *              modeling, machine learning, however you want. This
     *              is one of the harder parts
     */
    public static int rgbToPH(int[] rgb) {
        // TODO
    }

    /**
     * This may be used later on somewhere?
     * @param error
     * @param integral
     * @param derivative
     * @return {int} expected pH or error based on the reading
     */
    public int getExpectedPH(int error, int integral, int derivative) {
        integral = (int) (kI * integral);
        derivative = error - derivative;
        return (int) ((kP * error) + integral + (kD* derivative));
    }

    /**
     * pidLoop will run the code. We wrap it in this.running because
     * we don't want to constantly be running the code. We only want
     * to run this code when the user specifies a goal value.
     *
     * @param goalpH goal pH to hit
     */
    public void pidLoop(int goalpH) {
        int errorSum = 0;
        int lastError = 0;
        if(this.running) {
            int reading = this.rgbToPH(this.getRGB());
            int error = goalpH-reading;
            errorSum += error;
            lastError = error;
            // the idea is if the error positive, we want to add base (7 -> 5 for example)
            // if the error is negative, we want to add acid (5 -> 7 for example)

            // pid loop stuff
            getExpectedPH(error, errorSum, lastError);
            // gives you an output of how much to move and which to move, you might want
            // to calculate the possible values to move for either acid or base, and
            // only CHOOSE which one to use here at this next segment I'm about to type
            // or rethink your own algorithm. What I type might not even make much sense
            // to use
            int stepsToMoveMotorForAcidic = 1;
            int stepsToMoveMotorForBasic = 1;
            if(error > 0) {
                moveMotor(acidMotor, stepsToMoveMotorForAcidic);
            } else {
                moveMotor(basicMotor, stepsToMoveMotorForBasic);
            }
        }
    }

    /**
     * Sets this.running to variable set, runs the code or stops it
     * @param set true/false
     */
    public void setRunState(boolean set) {
        this.running = set;
    }
}
