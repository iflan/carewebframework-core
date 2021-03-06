/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Allows collecting exceptions generated during an iterative process and reporting them as a single
 * single composite exception.
 */
public class CompositeException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final List<Throwable> exceptions = new ArrayList<>();
    
    public CompositeException(String msg) {
        super(msg);
    }
    
    /**
     * Adds an exception.
     * 
     * @param exception Exception to add.
     */
    public void add(Throwable exception) {
        exceptions.add(exception);
    }
    
    /**
     * Returns true if this instance contains any exceptions.
     * 
     * @return True if exceptions have been added.
     */
    public boolean hasExceptions() {
        return !exceptions.isEmpty();
    }
    
    /**
     * Throws the exception if it is not empty.
     */
    public void throwIfExceptions() {
        if (hasExceptions()) {
            throw this;
        }
    }
    
    /**
     * Creates composite exception message
     */
    @Override
    public String getMessage() {
        return getMessage(false);
    }
    
    /**
     * Creates composite exception message
     */
    @Override
    public String getLocalizedMessage() {
        return getMessage(true);
    }
    
    /**
     * Creates composite exception message (localized or not).
     * 
     * @param localized If true, return the localized version.
     * @return A composite of all exception messages.
     */
    private String getMessage(boolean localized) {
        String msg = localized ? super.getLocalizedMessage() : super.getMessage();
        StringBuilder sb = new StringBuilder(msg == null ? "" : msg);
        
        for (Throwable exception : exceptions) {
            msg = localized ? exception.getLocalizedMessage() : exception.getMessage();
            
            if (msg != null) {
                if (sb.length() != 0) {
                    sb.append("\n\n");
                }
                
                sb.append(msg);
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Returns the stack trace, which is the union of all stack traces of contained exceptions.
     */
    @Override
    public StackTraceElement[] getStackTrace() {
        ArrayList<StackTraceElement> stackTrace = new ArrayList<>();
        
        for (Throwable exception : exceptions) {
            stackTrace.addAll(Arrays.asList(exception.getStackTrace()));
        }
        
        return stackTrace.toArray(new StackTraceElement[stackTrace.size()]);
    }
    
    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        return object instanceof CompositeException && ((CompositeException) object).exceptions.equals(exceptions);
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return exceptions.hashCode();
    }
    
}
