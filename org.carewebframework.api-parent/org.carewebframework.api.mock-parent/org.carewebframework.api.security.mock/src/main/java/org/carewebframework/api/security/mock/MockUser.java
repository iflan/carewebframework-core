/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.api.security.mock;

import org.carewebframework.api.domain.IUser;
import org.carewebframework.api.security.ISecurityDomain;

/**
 * Mock user for testing.
 */
public class MockUser implements IUser {
    
    private static final long serialVersionUID = 1L;
    
    private final String logicalId;
    
    private final String fullName;
    
    private final String loginName;
    
    private final String password;
    
    private final ISecurityDomain securityDomain;
    
    public MockUser() {
        this("mockId", "User, Mock", "username", "password", new MockSecurityDomain());
    }
    
    public MockUser(String logicalId, String fullName, String loginName, String password, ISecurityDomain securityDomain) {
        this.logicalId = logicalId;
        this.fullName = fullName;
        this.loginName = loginName;
        this.password = password;
        this.securityDomain = securityDomain;
    }
    
    @Override
    public String toString() {
        return fullName;
    }
    
    @Override
    public String getFullName() {
        return fullName;
    }
    
    @Override
    public String getLoginName() {
        return loginName;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public ISecurityDomain getSecurityDomain() {
        return securityDomain;
    }
    
    @Override
    public String getLogicalId() {
        return logicalId;
    }
    
    @Override
    public MockUser getNativeUser() {
        return this;
    }
    
}
