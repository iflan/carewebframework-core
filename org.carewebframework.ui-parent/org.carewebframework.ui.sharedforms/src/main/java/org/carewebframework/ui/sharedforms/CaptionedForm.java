/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.ui.sharedforms;

import java.util.Arrays;

import org.carewebframework.common.StrUtil;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.web.fn.ThemeFns;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Panel;

/**
 * Controller for captioned form.
 */
public class CaptionedForm extends BaseForm {
    
    private static final long serialVersionUID = 1L;
    
    public enum CaptionStyle {
        HIDDEN, TITLE, FRAME, LEFT, RIGHT, CENTER;
        
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    };
    
    private CaptionStyle captionStyle = CaptionStyle.HIDDEN;
    
    private Panel panel;
    
    private String color1;
    
    private String color2;
    
    private final Caption caption = new Caption();
    
    @Override
    protected void init() {
        super.init();
        getContainer().registerProperties(this, "caption", "captionStyle", "icon", "color1", "color2");
        root = panel;
        updateStyle();
    }
    
    public CaptionStyle getCaptionStyle() {
        return captionStyle;
    }
    
    public void setCaptionStyle(CaptionStyle captionStyle) {
        this.captionStyle = captionStyle;
        updateStyle();
    }
    
    /**
     * Update styles by current caption style setting.
     */
    private void updateStyle() {
        CaptionStyle cs = captionStyle == null ? CaptionStyle.HIDDEN : captionStyle;
        String background = null;
        
        switch (cs) {
            case FRAME:
                break;
            
            case TITLE:
                break;
            
            case LEFT:
                background = getGradValue(color1, color2);
                break;
            
            case RIGHT:
                background = getGradValue(color2, color1);
                break;
            
            case CENTER:
                background = getGradValue(color1, color2, color1);
                break;
        
        }
        
        panel.setSclass("sharedForms-captioned sharedForms-captioned-caption-" + cs.name().toLowerCase());
        ZKUtil.updateStyle(caption, "background", background);
        
        if (cs == CaptionStyle.HIDDEN) {
            caption.detach();
        } else {
            caption.setParent(panel);
        }
        
        Clients.resize(panel);
    }
    
    /**
     * Returns css gradient specifier for current browser.
     * 
     * @param colors List of colors for gradient.
     * @return The gradient specifier.
     */
    private String getGradValue(String... colors) {
        return ThemeFns.gradValue("hor", StrUtil.fromList(Arrays.asList(colors), ";", "none"));
    }
    
    /**
     * Get first color in gradient range.
     * 
     * @return First color in gradient range.
     */
    public String getColor1() {
        return color1;
    }
    
    /**
     * Set first color in gradient range.
     * 
     * @param color1 First color in gradient range.
     */
    public void setColor1(String color1) {
        this.color1 = color1;
        updateStyle();
    }
    
    /**
     * Get second color in gradient range.
     * 
     * @return Second color in gradient range.
     */
    public String getColor2() {
        return color2;
    }
    
    /**
     * Set second color in gradient range.
     * 
     * @param color2 Second color in gradient range.
     */
    public void setColor2(String color2) {
        this.color2 = color2;
        updateStyle();
    }
    
    /**
     * Return the current caption.
     * 
     * @return Current caption.
     */
    public String getCaption() {
        return caption.getLabel();
    }
    
    /**
     * Set the current caption.
     * 
     * @param caption Current caption.
     */
    public void setCaption(String caption) {
        this.caption.setLabel(caption);
    }
    
    /**
     * Return the caption icon URL.
     * 
     * @return Caption icon URL.
     */
    public String getIcon() {
        return caption.getImage();
    }
    
    /**
     * Set the caption icon.
     * 
     * @param url URL of icon.
     */
    public void setIcon(String url) {
        caption.setImage(url);
    }
    
}
