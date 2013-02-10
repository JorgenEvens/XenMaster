/*
 * Modeler.java
 * Copyright (C) 2011,2012 Wannes De Smet
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

package org.xenmaster.scheduler.analytics;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.ArgumentOutsideDomainException;
import org.apache.commons.math.analysis.PolynomialSplineFunction;
import org.apache.commons.math.analysis.SplineInterpolator;

public class Modeler {

    public static void main(String[] args) {
        try {
            ArrayList<Double> vals;
            try (BufferedReader br = new BufferedReader(new FileReader("data.csv"))) {
                vals = new ArrayList<>();
                String line = br.readLine();
                while (line != null) {
                    String[] split = StringUtils.split(line, ',');
                    vals.add(Double.parseDouble(split[0]));
                    line = br.readLine();
                }
            }
            
            double[] idx = new double[1440];
            for (int i = 0; i < idx.length; i++) {
                idx[i] = i;
            }
            double y[] = new double[1440];
            for (int i = 0; i < idx.length; i++) {
                y[i] = vals.get(i).doubleValue();
            }
            SplineInterpolator si = new SplineInterpolator();
            PolynomialSplineFunction psf = (PolynomialSplineFunction) si.interpolate(idx, y);
           
            for (int i = 0; i < 3; i++) {
                psf = psf.polynomialSplineDerivative();
            }
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("derp.csv", false))) {
                for (double i = psf.getKnots()[0]; i < psf.getKnots()[psf.getKnots().length-1]; i++) {
                    bw.append("" + psf.value(i) + "\n");
                }
            } catch (ArgumentOutsideDomainException | IOException ex) {
                
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Modeler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
