/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.api.context;

import org.carewebframework.common.AbstractRegistry;
import org.carewebframework.common.ISerializer;
import org.carewebframework.common.RegistryMap.DuplicateAction;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;

/**
 * Registry for context serializers indexed by the class type they support.
 */
public class ContextSerializerRegistry extends AbstractRegistry<Class<?>, ISerializer<?>> implements DestructionAwareBeanPostProcessor {
    
    private static ContextSerializerRegistry instance = new ContextSerializerRegistry();
    
    public static ContextSerializerRegistry getInstance() {
        return instance;
    }
    
    /**
     * Enforce singleton instance.
     */
    private ContextSerializerRegistry() {
        super(DuplicateAction.ERROR);
    }
    
    @Override
    protected Class<?> getKey(ISerializer<?> item) {
        return item.getType();
    }
    
    /**
     * Returns the item associated with the specified key, or null if not found.
     * 
     * @param clazz The class whose serializer is sought.
     * @return The context serializer.
     */
    @Override
    public ISerializer<?> get(Class<?> clazz) {
        ISerializer<?> contextSerializer = super.get(clazz);
        
        if (contextSerializer != null) {
            return contextSerializer;
        }
        
        for (ISerializer<?> item : this) {
            if (item.getType().isAssignableFrom(clazz)) {
                return item;
            }
        }
        
        return null;
    }
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ISerializer) {
            register((ISerializer<?>) bean);
        }
        
        return bean;
    }
    
    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (bean instanceof ISerializer) {
            unregister((ISerializer<?>) bean);
        }
    }
    
}
