/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at 
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.api.event;

/**
 * This is the event callback interface for handling generic events. Any object that wishes to
 * subscribe to a generic event must implement this interface.
 * 
 * @param <T> Return type of event data.
 */
public interface IGenericEvent<T> {
    
    /**
     * The callback interface used to notify a subscriber of a subscribed event.
     * 
     * @param eventName Name of the event.
     * @param eventData Associated data.
     */
    void eventCallback(String eventName, T eventData);
}
