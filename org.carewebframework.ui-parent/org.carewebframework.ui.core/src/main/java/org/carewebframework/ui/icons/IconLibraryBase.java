/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.ui.icons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

/**
 * A base implementation for an icon library.
 */
public class IconLibraryBase implements IIconLibrary, ApplicationContextAware {
    
    private static final Log log = LogFactory.getLog(IconLibraryBase.class);
    
    private final String id;
    
    private final String[] dimensions;
    
    private final String resourcePath;
    
    private final String urlPattern;
    
    private ApplicationContext applicationContext;
    
    protected IconLibraryBase(String id, String resourcePath, String dimensions) {
        this(id, resourcePath, dimensions, "%1$s/%2$s/%3$s/%4$s");
    }
    
    protected IconLibraryBase(String id, String resourcePath, String dimensions, String urlPattern) {
        this.id = id;
        this.resourcePath = resourcePath;
        this.dimensions = StringUtils.split(dimensions, ',');
        this.urlPattern = urlPattern;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getIconUrl(String iconName, String dimensions) {
        return formatURL(iconName, dimensions, "~.");
    }
    
    @Override
    public List<String> getMatching(String iconName, String dimensions) {
        List<String> urls = new ArrayList<>();
        
        try {
            for (Resource resource : applicationContext.getResources(formatURL(iconName, dimensions, "classpath:web"))) {
                String path = resource.getURL().getPath();
                int i = path.indexOf(resourcePath);
                urls.add("~." + path.substring(i));
            }
        } catch (IOException e) {
            log.error("Error enumerating icons.", e);
        }
        
        return urls;
    }
    
    @Override
    public String[] supportedDimensions() {
        return dimensions;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    protected String formatURL(String iconName, String dims, String prefix) {
        if (dims == null) {
            if (dimensions != null && dimensions.length > 0) {
                dims = dimensions[0];
            } else {
                dims = "";
            }
        }
        
        return prefix + String.format(urlPattern, resourcePath, id, dims, iconName).replace("//", "/");
    }
    
}
