/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.ui.wonderbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.carewebframework.ui.wonderbar.Wonderbar.MatchMode;

/**
 * Wonder bar utilities.
 */
public class WonderbarUtil {
    
    /**
     * Comparator to sort tokens by size, largest first.
     */
    private static final Comparator<String> tokenLengthComparator = new Comparator<String>() {
        
        @Override
        public int compare(String s1, String s2) {
            int len1 = s1 == null ? -1 : s1.length();
            int len2 = s2 == null ? -1 : s2.length();
            return len2 - len1;
        }
        
    };
    
    /**
     * Returns true if value matches the specified pattern.
     * 
     * @param pattern Pattern for matching.
     * @param value Value to test.
     * @param mode Matching mode.
     * @return True if matched.
     */
    public static boolean matches(String pattern, String value, MatchMode mode) {
        return matches(tokenize(pattern, mode == MatchMode.ANY_ORDER), value, mode);
    }
    
    /**
     * Returns true if value matches the specified pattern set. This API is more efficient when
     * comparing multiple values against the same pattern set.
     * 
     * @param patterns Pattern list for matching.
     * @param value Value to test.
     * @param mode Matching mode.
     * @return True if matched.
     */
    public static boolean matches(List<String> patterns, String value, MatchMode mode) {
        List<String> values = tokenize(value);
        int start = 0;
        mode = mode == null ? MatchMode.ANY_ORDER : mode;
        
        for (String pattern : patterns) {
            boolean matched = false;
            
            for (int i = start; i < values.size(); i++) {
                if (matched = values.get(i).startsWith(pattern)) {
                    values.remove(i);
                    start = mode == MatchMode.ANY_ORDER ? 0 : i;
                    
                    if (mode == MatchMode.ADJACENT) {
                        mode = MatchMode.FROM_START;
                    }
                    break;
                } else if (mode == MatchMode.FROM_START) {
                    return false;
                }
            }
            
            if (!matched) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Breaks the text into individual tokens at word boundaries. Trims each token, converts to
     * lower case and removes any empty tokens.
     * 
     * @param text Text to be tokenized.
     * @return Token list.
     */
    public static List<String> tokenize(String text) {
        String[] pcs = text.toLowerCase().trim().split("\\W");
        List<String> list = new ArrayList<>(pcs.length);
        
        for (String pc : pcs) {
            pc = pc.trim();
            
            if (!pc.isEmpty()) {
                list.add(pc);
            }
        }
        
        return list;
    }
    
    /**
     * Breaks the text into individual tokens at word boundaries. Trims each token, converts to
     * lower case and removes any empty tokens.
     * 
     * @param text Text to be tokenized.
     * @param sortByLength If true, sort list by token length, largest first.
     * @return The token list.
     */
    public static List<String> tokenize(String text, boolean sortByLength) {
        List<String> list = tokenize(text);
        
        if (sortByLength) {
            Collections.sort(list, tokenLengthComparator);
        }
        
        return list;
    }
    
    /**
     * Adds default items to a wonderbar.
     * 
     * @param wonderbar Wonderbar to receive new default items.
     * @param items List of default items.
     */
    public static void addDefaultItems(Wonderbar<?> wonderbar, Collection<WonderbarAbstractItem> items) {
        WonderbarDefaults defaultItems = wonderbar.getDefaultItems();
        
        if (defaultItems == null) {
            wonderbar.appendChild(defaultItems = new WonderbarDefaults());
        }
        
        defaultItems.getItems().clear();
        
        if (items != null) {
            defaultItems.getItems().addAll(items);
        }
    }
    
    private WonderbarUtil() {
    }
    
}
