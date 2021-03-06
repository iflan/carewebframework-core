/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.maven.plugin.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents an input resource to be processed.
 */
public interface IResource {
    
    /**
     * Returns an input stream for the resource.
     * 
     * @return An input stream.
     * @throws IOException Exception opening an input stream.
     */
    InputStream getInputStream() throws IOException;
    
    /**
     * Returns the relative path to the source file of this resource.
     * 
     * @return The relative path to the source file.
     */
    String getSourcePath();
    
    /**
     * Returns the relative path to the target file of this resource. This is often the same as the
     * source path. If null is returned, the resource is ignored during processing.
     * 
     * @return The relative path to the target file.
     */
    String getTargetPath();
    
    /**
     * Returns the timestamp of the resource.
     * 
     * @return Resource timestamp.
     */
    long getTime();
    
    /**
     * Returns true if resource is a directory.
     * 
     * @return True if the resource is a directory.
     */
    boolean isDirectory();
}
