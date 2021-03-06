/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.ui.zk;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SortEvent;
import org.zkoss.zul.Column;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.impl.HeaderElement;

/**
 * Serves as a generic comparator for list and grid displays. Includes a number of static methods
 * for auto-wiring comparators into sortable header elements.
 */
public class RowComparator implements Comparator<Object>, Serializable {
    
    /**
     * Convenience method for auto-wiring list box headers.
     * <p>
     * {@link #autowireColumnComparators(List)}
     * 
     * @param listbox List box whose headers are to be auto-wired.
     */
    public static void autowireColumnComparators(Listbox listbox) {
        autowireColumnComparators(listbox.getListhead());
    }
    
    /**
     * Convenience method for auto-wiring grid headers.
     * <p>
     * {@link #autowireColumnComparators(List)}
     * 
     * @param grid Grid whose columns are to be auto-wired.
     */
    public static void autowireColumnComparators(Grid grid) {
        autowireColumnComparators(grid.getColumns());
    }
    
    /**
     * Convenience method for auto-wiring tree columns.
     * <p>
     * {@link #autowireColumnComparators(List)}
     * 
     * @param tree Tree whose columns are to be auto-wired.
     */
    public static void autowireColumnComparators(Tree tree) {
        autowireColumnComparators(tree.getTreecols());
    }
    
    /**
     * Auto-wire all children of the parent (may be null).
     * 
     * @param parent The parent component.
     */
    private static void autowireColumnComparators(Component parent) {
        if (parent != null) {
            autowireColumnComparators(parent.getChildren());
        }
    }
    
    /**
     * Automatically wires column headers to generic comparators.
     * <p>
     * The getter method for each header element is derived either from a custom attribute named
     * "getter" or, absent that, from the element's id. This value may either be a property name or
     * the name of the getter method itself. This method is used when comparing values across rows
     * under that header. If getter is specified for a header, no comparator is generated for that
     * header.
     * <p>
     * Custom comparators may be specified in a custom attribute named "comparator" on the header
     * element. This attribute may be an object instance that implements the Comparator interface or
     * the name of a class that implements the Comparator interface.
     * <p>
     * Finally, if a header element has been marked with the custom attribute named "natural", that
     * header element will be used to establish the "natural" ordering of the model. The value of
     * the attribute may be either "ascending" or "descending" (default is "ascending" if any other
     * value is specified) to indicate the direction of the natural ordering. When this attribute is
     * present, the sort toggle becomes tri-state, toggling from "ascending" to "descending" to
     * "natural" in that order.
     * 
     * @param headers List of headers (of type Column, Listheader, or Treecol).
     */
    public static void autowireColumnComparators(List<Component> headers) {
        Component defaultSortHeader = getDefaultSortHeader(headers);
        SortListener sortListener = defaultSortHeader == null ? null : new SortListener(defaultSortHeader);
        
        for (Component cmp : headers) {
            if (cmp instanceof HeaderElement) {
                String getter = getGetter(cmp);
                
                if (getter == null) {
                    continue;
                }
                
                Comparator<?> comparator = getCustomComparator(cmp);
                RowComparator asc = new RowComparator(true, getter, comparator);
                RowComparator dsc = new RowComparator(false, getter, comparator);
                
                if (sortListener != null) {
                    cmp.addEventListener(Events.ON_SORT, sortListener);
                }
                
                if (cmp instanceof Column) {
                    ((Column) cmp).setSortAscending(asc);
                    ((Column) cmp).setSortDescending(dsc);
                } else if (cmp instanceof Listheader) {
                    ((Listheader) cmp).setSortAscending(asc);
                    ((Listheader) cmp).setSortDescending(dsc);
                } else if (cmp instanceof Treecol) {
                    ((Treecol) cmp).setSortAscending(asc);
                    ((Treecol) cmp).setSortDescending(dsc);
                }
            } else {
                throw new IllegalArgumentException("Component not a supported type: " + cmp.getClass().getName());
            }
        }
    }
    
    /**
     * Sort event listener that intercepts sort events to implement tri-state sort toggling.
     */
    private static class SortListener implements EventListener<SortEvent> {
        
        private final Component sortDefault;
        
        public SortListener(Component sortDefault) {
            this.sortDefault = sortDefault;
        }
        
        @Override
        public void onEvent(SortEvent event) throws Exception {
            Component cmp = event.getTarget();
            
            if (cmp instanceof Column) {
                Column header = (Column) cmp;
                
                if ("descending".equals(header.getSortDirection())) {
                    header.setSortDirection("natural");
                    header = (Column) sortDefault;
                    header.sort(event.isAscending());
                    header.setSortDirection("natural");
                    event.stopPropagation();
                }
            } else if (cmp instanceof Listheader) {
                Listheader header = (Listheader) cmp;
                
                if ("descending".equals(header.getSortDirection())) {
                    header.setSortDirection("natural");
                    header = (Listheader) sortDefault;
                    header.sort(event.isAscending());
                    header.setSortDirection("natural");
                    event.stopPropagation();
                }
            } else if (cmp instanceof Treecol) {
                Treecol header = (Treecol) cmp;
                
                if ("descending".equals(header.getSortDirection())) {
                    header.setSortDirection("natural");
                    header = (Treecol) sortDefault;
                    header.sort(event.isAscending());
                    header.setSortDirection("natural");
                    event.stopPropagation();
                }
            }
        }
    };
    
    /**
     * If the component has a custom attribute named "getter", that value is returned. Otherwise,
     * the component's id is assumed to be either a property name or the getter method name. If the
     * former, assumes the getter method is getXxxx.
     * 
     * @param component Component used to derive getter method.
     * @return Null if the component has no id and no getter attribute, or has a ZK-generated id.
     *         Otherwise, if the id is prefixed with a standard getter prefix ("get", "has", "is"),
     *         it is assumed to be the name of the getter method. Lacking such a prefix, a prefix of
     *         "get" is prepended to the case-adjusted id to obtain the getter method.
     */
    private static String getGetter(Component component) {
        if (component.hasAttribute("getter")) {
            return (String) component.getAttribute("getter");
        }
        
        String id = component.getId();
        
        if (id == null || id.isEmpty() || id.startsWith("z")) {
            return null;
        }
        
        String lc = id.toLowerCase();
        
        if (lc.startsWith("is") || lc.startsWith("has") || lc.startsWith("get")) {
            return StringUtils.uncapitalize(id); // is the getter name
        }
        return "get".concat(StringUtils.capitalize(id)); // infer getter name
    }
    
    /**
     * Returns an optional custom comparator from a component. A custom comparator is specific in
     * the custom attribute named "comparator" and may be an instance of a Comparator or the name of
     * a class implementing Comparator.
     * 
     * @param component Component whose custom comparator is sought.
     * @return A custom comparator, if any.
     */
    private static Comparator<?> getCustomComparator(Component component) {
        Object cmpr = component.getAttribute("comparator");
        
        if (cmpr instanceof String) {
            try {
                Class<?> clazz = ClassUtils.getClass((String) cmpr);
                
                if (Comparator.class.isAssignableFrom(clazz)) {
                    cmpr = clazz.newInstance();
                }
            } catch (Exception e) {
                cmpr = null;
            }
        }
        
        return cmpr instanceof Comparator ? (Comparator<?>) cmpr : null;
    }
    
    /**
     * Gets the header element marked as governing the "natural" sort order of the model.
     * 
     * @param headers The list of headers to search.
     * @return The header that has been designated for default sorting, or null if none.
     */
    private static Component getDefaultSortHeader(List<Component> headers) {
        for (Component header : headers) {
            if (header.hasAttribute("natural")) {
                return header;
            }
        }
        
        return null;
    }
    
    private static final Log log = LogFactory.getLog(RowComparator.class);
    
    private static final long serialVersionUID = 1L;
    
    private final boolean _asc;
    
    private final String _getter;
    
    private final Comparator<Object> _customComparator;
    
    /**
     * Constructs a row comparator.
     * 
     * @param asc If true, an ascending comparator is created. If false, a descending comparator is
     *            created.
     * @param beanProperty This is the name of the getter method that will be used to retrieve a
     *            value from the underlying model object for comparison.
     */
    public RowComparator(boolean asc, String beanProperty) {
        this(asc, beanProperty, null);
    }
    
    /**
     * Constructs a row comparator.
     * 
     * @param asc If true, an ascending comparator is created. If false, a descending comparator is
     *            created.
     * @param getter This is the name of the getter method that will be used to retrieve a value
     *            from the underlying model object for comparison.
     * @param customComparator Optional custom comparator.
     */
    @SuppressWarnings("unchecked")
    public RowComparator(boolean asc, String getter, Comparator<?> customComparator) {
        _asc = asc;
        _getter = getter;
        _customComparator = (Comparator<Object>) customComparator;
    }
    
    /**
     * Performs a comparison between two objects. If the objects implement the Comparable interface,
     * that method is used. Otherwise, the objects are converted to their string representations and
     * that method is used. Null values are handled.
     */
    @SuppressWarnings("unchecked")
    @Override
    public int compare(Object o1, Object o2) {
        Object v1 = getValue(o1), v2 = getValue(o2);
        int result;
        
        if (v1 == null && v2 == null) {
            result = 0;
        } else if (v2 == null) {
            result = -1;
        } else if (v1 == null) {
            result = 1;
        } else if (_customComparator != null) {
            result = _customComparator.compare(v1, v2);
        } else if (v1 instanceof Comparable && !(v1 instanceof String)) {
            result = ((Comparable<Object>) v1).compareTo(v2);
        } else {
            result = v1.toString().compareToIgnoreCase(v2.toString());
        }
        
        return _asc ? result : -result;
    }
    
    /**
     * Gets a value from the model object using the getter method.
     * 
     * @param o The model object.
     * @return Value returned by the getter method.
     */
    private Object getValue(Object o) {
        // Special case that returns the model object itself, not a property of the object.
        if ("this".equals(_getter)) {
            return o;
        }
        // Otherwise, use getter to retrieve a property value from the model object.
        try {
            Object[] params = null;
            Method method = o.getClass().getMethod(_getter, (Class<?>[]) params);
            return method.invoke(o, params);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
