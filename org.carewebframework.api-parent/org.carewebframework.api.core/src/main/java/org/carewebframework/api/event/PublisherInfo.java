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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class for holding information about a publisher.
 */
public class PublisherInfo implements IPublisherInfo, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String userName;
    
    private final Map<String, String> attributes = new HashMap<>();
    
    public PublisherInfo() {
        
    }
    
    @JsonCreator
    public PublisherInfo(@JsonProperty("userName") String userName,
        @JsonProperty("attributes") Map<String, String> attributes) {
        this.userName = userName;
        this.attributes.putAll(attributes);
    }
    
    public PublisherInfo(IPublisherInfo publisherInfo) {
        this(publisherInfo.getUserName(), publisherInfo.getAttributes());
    }
    
    @Override
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    @Override
    public String getUserId() {
        return attributes.get("userId");
    }
    
    @JsonIgnore
    public void setUserId(String userId) {
        put("userId", "u-", userId);
    }
    
    @Override
    public String getAppName() {
        return attributes.get("appName");
    }
    
    @JsonIgnore
    public void setAppName(String appName) {
        put("appName", "a-", appName);
    }
    
    @Override
    public String getNodeId() {
        return attributes.get("nodeId");
    }
    
    @JsonIgnore
    public void setNodeId(String nodeId) {
        put("nodeId", "n-", nodeId);
    }
    
    @Override
    public String getEndpointId() {
        return attributes.get("ep");
    }
    
    @JsonIgnore
    public void setEndpointId(String endpointId) {
        put("ep", "", endpointId);
    }
    
    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IPublisherInfo && attributes.equals(((IPublisherInfo) obj).getAttributes());
    }
    
    @Override
    public int hashCode() {
        return attributes.hashCode();
    }
    
    public void put(String key, String prefix, String value) {
        if (value == null) {
            attributes.keySet().remove(key);
        } else {
            attributes.put(key, prefix + value.replace(",", "_"));
        }
    }
}
