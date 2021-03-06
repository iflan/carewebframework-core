/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.shell.designer;

import org.carewebframework.shell.layout.UIElementBase;
import org.carewebframework.shell.property.PropertyInfo;
import org.carewebframework.ui.icons.IconLibraryRegistry;
import org.carewebframework.ui.icons.IconPickerEx;
import org.carewebframework.ui.icons.IconUtil;
import org.carewebframework.ui.zk.IconPicker;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Image;

/**
 * Property editor for icon properties. If the associated property has defined choices, the icon
 * picker will be limited to those values only. Otherwise, all registered icons will be available.
 */
public class PropertyEditorIcon extends PropertyEditorBase {
    
    private final IconPickerEx iconPicker;
    
    public PropertyEditorIcon() {
        super(new IconPickerEx());
        iconPicker = (IconPickerEx) component;
        iconPicker.setAutoAdd(false);
        iconPicker.addEventListener("onSetValue", new EventListener<Event>() {
            
            @Override
            public void onEvent(Event event) throws Exception {
                Object value = event.getData();
                Image icon = value == null ? null : iconPicker.findIcon(value.toString());
                iconPicker.setSelectedItem(icon);
                updateValue();
            }
            
        });
    }
    
    @Override
    protected void init(UIElementBase target, PropertyInfo propInfo, PropertyGrid propGrid) {
        super.init(target, propInfo, propGrid);
        component.addForward(IconPicker.ON_SELECT_ITEM, propGrid, Events.ON_CHANGE);
        String[] values = propInfo.getConfigValueArray("values");
        
        if (values == null) {
            String dflt = IconLibraryRegistry.getInstance().getDefaultLibrary();
            iconPicker.setIconLibrary(dflt);
        } else {
            iconPicker.setSelectorVisible(false);
            
            for (String choice : values) {
                if (choice.startsWith("~./")) {
                    iconPicker.addIconByUrl(choice);
                } else {
                    String[] pcs = choice.split("\\:", 3);
                    String library = pcs.length == 0 ? null : pcs[0];
                    String name = pcs.length < 2 ? "*" : pcs[1];
                    String dimension = pcs.length < 3 ? null : pcs[2];
                    iconPicker.addIconsByUrl(IconUtil.getMatching(library, name, dimension));
                }
            }
        }
    }
    
    @Override
    protected String getValue() {
        Image icon = iconPicker.getSelectedItem();
        return icon == null ? null : icon.getSrc();
    }
    
    @Override
    protected void setValue(Object value) {
        Events.postEvent("onSetValue", iconPicker, value);
    }
}
