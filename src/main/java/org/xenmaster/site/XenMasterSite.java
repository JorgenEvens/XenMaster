/* XenMasterSite.java
 * Copyright (C) 2012 Wannes De Smet
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.xenmaster.site;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.io.IOUtils;

/**
 * 
 * @created Feb 10, 2012
 * @author double-u
 */
public class XenMasterSite {

    public static final String XENMASTER_ORG = "http://xen-master.org";
    public static final String XENMASTER_ORG_DOWNLOAD = "http://dl.xen-master.org";
    
    public static InputStream getFileAsStream(String file) throws IOException {
        URL url = new URL(XENMASTER_ORG_DOWNLOAD + "/" + file);
        URLConnection uc = url.openConnection();
        uc.connect();
        
        InputStream is = uc.getInputStream();
        return is;
    }

    public static void downloadFile(String file, File destination) throws IOException {
        InputStream is = getFileAsStream(file);
        FileOutputStream fos = new FileOutputStream(destination);
        IOUtils.copy(is, fos);
        is.close();
        fos.flush();
        fos.close();
    }
}
