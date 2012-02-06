/*
 * TemplateHook.java
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
package org.xenmaster.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import net.wgr.server.web.handling.WebHook;
import net.wgr.settings.Settings;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 
 * @created Oct 5, 2011
 * @author double-u
 */
public class TemplateHook extends WebHook {
    
    public TemplateHook() {
        super("view");
    }
    
    @Override
    public void handle(RequestBundle rb) throws IOException {
        if (rb.getPathParts().length < 1) {
            return;
        }
        String path = "";
        try {
            String concat = StringUtils.join(rb.getPathParts(), '/');
            URI uri = new URI(concat);
            uri = uri.normalize();
            path = uri.getPath();
        } catch (URISyntaxException ex) {
            Logger.getLogger(getClass()).error(ex);
        }
        if (path.isEmpty()) {
            return;
        }
        
        path = Settings.getInstance().getString("WebContentPath") + "/" + this.getSelector() + "/" + path;
        File f = new File(path);
        if (f.exists() && f.isDirectory()) {
            Path p = f.toPath();
            JsonObject contentTree = new JsonObject();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
                for (Path file : stream) {
                    if (file.toFile().isFile() && !file.startsWith(".")) {
                        contentTree.addProperty(FilenameUtils.getBaseName(file.toString()), IOUtils.toString(new FileInputStream(file.toFile())));
                    }
                }
            }
            Gson gson = new Gson();
            rb.replyWithString(gson.toJson(contentTree));
        }
    }
}
