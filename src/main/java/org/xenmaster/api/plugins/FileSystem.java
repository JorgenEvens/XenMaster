/*
 * FileSystem.java
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
package org.xenmaster.api.plugins;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xenmaster.api.entities.Host;
import org.xenmaster.controller.BadAPICallException;

/**
 * 
 * @created Dec 12, 2011
 * @author double-u
 */
public class FileSystem {

    protected final static String FILESYTEM_PLUGIN = "xm-filesystem";
    protected static Gson gson = new Gson();
    protected final static Pattern PARTITION_PATTERN = Pattern.compile("([A-z]{3})(\\d+)");

    public static String[] getDiskDevices(Host host) throws BadAPICallException {
        String result = host.callPlugin(FILESYTEM_PLUGIN, "disks", new HashMap<String, String>());
        String[] str = gson.fromJson(result, String[].class);
        ArrayList<String> disks = new ArrayList<>();

        for (String device : str) {
            if (!PARTITION_PATTERN.matcher(device).matches()) {
                disks.add(device);
            }
        }
        return disks.toArray(new String[0]);
    }

    public static Map<String, ArrayList<String>> getDiskStructure(Host host) throws BadAPICallException {
        HashMap<String, ArrayList<String>> struct = new HashMap<>();
        String result = host.callPlugin(FILESYTEM_PLUGIN, "disks", new HashMap<String, String>());
        String[] str = gson.fromJson(result, String[].class);

        for (String device : str) {
            Matcher m = PARTITION_PATTERN.matcher(device);
            if (m.matches()) {
                if (!struct.containsKey(m.group(1))) {
                    struct.put(m.group(1), new ArrayList<String>());
                }
                struct.get(m.group(1)).add(device);
            } else if (!device.startsWith("sd")) {
                struct.put(device, null);
            }
        }

        return struct;
    }

    public static String[] getKernels(Host host) throws BadAPICallException {
        String result = host.callPlugin(FILESYTEM_PLUGIN, "kernels", new HashMap<String, String>());
        return gson.fromJson(result, String[].class);
    }

    public static String[] getRamdisks(Host host) throws BadAPICallException {
        String result = host.callPlugin(FILESYTEM_PLUGIN, "ramdisks", new HashMap<String, String>());
        return gson.fromJson(result, String[].class);
    }
}
