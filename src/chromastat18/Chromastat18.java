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

import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;

/**
 * Main class for Chromastat 2018
 * @author WilliamKwok
 */
public class Chromastat18 {

    /**
     * Invokes the UI. This is literally all it does.
     * All the methods and such are called in the UI.
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, 
            I2CFactory.UnsupportedBusNumberException, InterruptedException {
        Chromastat18UI form = new Chromastat18UI();
        form.setVisible(true);
    }
    
    
    
}
