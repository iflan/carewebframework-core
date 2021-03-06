/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.maven.plugin.theme;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.carewebframework.maven.plugin.resource.IResource;

/**
 * Represents a CSS resource entry.
 */
public class CSSResource implements IResource {
    
    private final File file;
    
    private final File mapper;
    
    public CSSResource(File file, File mapper) {
        this.file = file;
        this.mapper = mapper;
        
        if (!file.exists()) {
            throw new RuntimeException("Failed to find CSS resource: " + file);
        }
        
        if (mapper != null && !mapper.exists()) {
            throw new RuntimeException("Failed to find mapper file: " + mapper);
        }
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }
    
    @Override
    public String getSourcePath() {
        return file.getName();
    }
    
    @Override
    public String getTargetPath() {
        return getSourcePath();
    }
    
    @Override
    public long getTime() {
        return file.lastModified();
    }
    
    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }
    
    public File getMapper() {
        return mapper;
    }
    
}
