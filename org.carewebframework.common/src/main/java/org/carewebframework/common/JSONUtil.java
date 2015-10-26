/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.common;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeSerializer;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.util.ClassUtil;

/**
 * A set of static methods supporting serialization and deserialization of objects using the JSON
 * format. This particular implementation uses the Jackson JSON library, but other JSON
 * implementations could be used.
 */
public class JSONUtil {
    
    private static final String DEFAULT_TYPE_PROPERTY = "@class";
    
    private static final Map<String, ObjectMapper> mappers = new ConcurrentHashMap<String, ObjectMapper>();
    
    /**
     * Identifies properties that require type metadata (via property specified in typeProperty).
     */
    private static class CWTypeResolverBuilder extends StdTypeResolverBuilder {
        
        @Override
        public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType,
                                                      Collection<NamedType> subtypes) {
            return noTypeInfo(baseType) ? null
                    : new AsPropertyTypeDeserializer(baseType, _customIdResolver, _typeProperty, _typeIdVisible,
                            baseType.getRawClass());
        }
        
        @Override
        public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType,
                                                  Collection<NamedType> subtypes) {
            return noTypeInfo(baseType) ? null : new AsPropertyTypeSerializerEx(_customIdResolver, null, _typeProperty);
        }
        
        /**
         * Returns true if no type info should be written for this base type.
         * 
         * @param baseType Base type.
         * @return True to suppress writing of type information.
         */
        private boolean noTypeInfo(JavaType baseType) {
            return baseType.isPrimitive() || baseType.isArrayType() || baseType.isCollectionLikeType()
                    || Date.class.isAssignableFrom(baseType.getRawClass());
        }
    }
    
    /**
     * Resolves type identifiers to classes. Supports aliases and class names.
     */
    private static class CWTypedIdResolver implements TypeIdResolver {
        
        private JavaType baseType;
        
        private final ObjectMapper mapper;
        
        protected CWTypedIdResolver(ObjectMapper mapper) {
            this.mapper = mapper;
        }
        
        @Override
        public void init(JavaType baseType) {
            this.baseType = baseType;
        }
        
        @Override
        public String idFromValue(Object value) {
            return findId(value.getClass());
        }
        
        @Override
        public String idFromValueAndType(Object value, Class<?> suggestedType) {
            return findId(suggestedType);
        }
        
        @Override
        public String idFromBaseType() {
            return findId(baseType.getRawClass());
        }
        
        @Override
        public JavaType typeFromId(String id) {
            try {
                return mapper.getTypeFactory().constructType(findClass(id));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public JavaType typeFromId(DatabindContext context, String id) {
            try {
                return context.getTypeFactory().constructType(findClass(id));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public Id getMechanism() {
            return Id.CUSTOM;
        }
        
    }
    
    /**
     * Required to suppress writing of type information except for top-level objects.
     */
    public final static class AsPropertyTypeSerializerEx extends AsPropertyTypeSerializer {
        
        public AsPropertyTypeSerializerEx(TypeIdResolver idRes, BeanProperty property, String propName) {
            super(idRes, property, propName);
        }
        
        @Override
        public void writeTypePrefixForObject(Object value, JsonGenerator jgen) throws IOException, JsonProcessingException {
            boolean needTypeId = jgen.getOutputContext().inRoot() || jgen.getOutputContext().inArray();
            jgen.writeStartObject();
            
            if (needTypeId) {
                jgen.writeStringField(_typePropertyName, idFromValue(value));
            }
        }
        
    }
    
    private static final Map<String, Class<?>> aliasToClass = new HashMap<String, Class<?>>();
    
    private static final Map<Class<?>, String> classToAlias = new HashMap<Class<?>, String>();
    
    /**
     * Returns an instance of the mapper for the default type property.
     * 
     * @return A mapper.
     */
    public static ObjectMapper getMapper() {
        return getMapper(null);
    }
    
    /**
     * Returns an instance of the mapper for the specified type property.
     * 
     * @param typeProperty The name of the property signifying the data type.
     * @return A mapper.
     */
    public static ObjectMapper getMapper(String typeProperty) {
        typeProperty = typeProperty == null ? DEFAULT_TYPE_PROPERTY : typeProperty;
        ObjectMapper mapper = mappers.get(typeProperty);
        return mapper == null ? initMapper(typeProperty) : mapper;
    }
    
    /**
     * Initializes a mapper in a thread-safe way.
     * 
     * @return The initialized mapper.
     */
    private static ObjectMapper initMapper(String typeProperty) {
        synchronized (mappers) {
            ObjectMapper mapper = mappers.get(typeProperty);
            if (mapper == null) {
                mapper = new ObjectMapper();
                TypeResolverBuilder<?> typer = new CWTypeResolverBuilder();
                typer = typer.init(JsonTypeInfo.Id.CUSTOM, new CWTypedIdResolver(mapper));
                typer = typer.inclusion(JsonTypeInfo.As.PROPERTY);
                typer = typer.typeProperty(typeProperty);
                mapper.setDefaultTyping(typer);
                mappers.put(typeProperty, mapper);
            }
            
            return mapper;
        }
    }
    
    /**
     * Register an alias for the specified class. The alias will be used when serializing objects of
     * this class. A given class or alias may only be registered once.
     * 
     * @param alias Alias to be used for serialization.
     * @param clazz Class to be associated with the alias.
     */
    public static synchronized void registerAlias(String alias, Class<?> clazz) {
        if (aliasToClass.containsKey(alias) && aliasToClass.get(alias) != clazz) {
            throw new RuntimeException("Alias '" + alias + "' is already registered to another class.");
        }
        
        if (classToAlias.containsKey(clazz) && classToAlias.get(clazz).equals(alias)) {
            throw new RuntimeException("Class '" + clazz.getName() + "' is already registered to another alias.");
        }
        
        aliasToClass.put(alias, clazz);
        classToAlias.put(clazz, alias);
    }
    
    /**
     * Removes a registered alias.
     * 
     * @param name Alias to be unregistered.
     */
    public static synchronized void unregisterAlias(String name) {
        classToAlias.remove(aliasToClass.get(name));
        aliasToClass.remove(name);
    }
    
    /**
     * Returns an alias given its associated class.
     * 
     * @param clazz The class whose alias is sought.
     * @return The alias associated with the specified class, or null if one does not exist.
     */
    public static final String getAlias(Class<?> clazz) {
        return classToAlias.get(clazz);
    }
    
    /**
     * Returns a class given its alias or class name.
     * 
     * @param id Alias or class name.
     * @return The associated class.
     * @throws ClassNotFoundException If class not found.
     */
    private static Class<?> findClass(String id) throws ClassNotFoundException {
        Class<?> clazz = aliasToClass.get(id);
        return clazz == null ? ClassUtil.findClass(id) : clazz;
    }
    
    /**
     * Returns the alias for a class or its class name if an alias has not been registered. This
     * value is used to identify the class type when serializing.
     * 
     * @param clazz Class whose id is sought.
     * @return The identifier to be used for serialization.
     */
    private static String findId(Class<?> clazz) {
        String id = classToAlias.get(clazz);
        return id == null ? clazz.getName() : id;
    }
    
    /**
     * Sets the date format to be used when serializing dates.
     * 
     * @param dateFormat Date format to use.
     */
    public static void setDateFormat(DateFormat dateFormat) {
        setDateFormat(null, dateFormat);
    }
    
    /**
     * Sets the date format to be used when serializing dates.
     * 
     * @param typeProperty The name of the property signifying the data type.
     * @param dateFormat Date format to use.
     */
    public static void setDateFormat(String typeProperty, DateFormat dateFormat) {
        getMapper(typeProperty).setDateFormat(dateFormat);
    }
    
    /**
     * Serializes an object to JSON format.
     * 
     * @param object Object to be serialized.
     * @return Serialized form of the object in JSON format.
     */
    public static String serialize(Object object) {
        return serialize(null, object);
    }
    
    /**
     * Serializes an object to JSON format.
     * 
     * @param typeProperty The name of the property signifying the data type.
     * @param object Object to be serialized.
     * @return Serialized form of the object in JSON format.
     */
    public static String serialize(String typeProperty, Object object) {
        try {
            return getMapper(typeProperty).writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Deserializes an object from JSON format.
     * 
     * @param data Serialized form of the object.
     * @return An instance of the deserialized object.
     */
    public static Object deserialize(String data) {
        return deserialize(null, data);
    }
    
    /**
     * Deserializes an object from JSON format.
     * 
     * @param typeProperty The name of the property signifying the data type.
     * @param data Serialized form of the object.
     * @return An instance of the deserialized object.
     */
    public static Object deserialize(String typeProperty, String data) {
        if (data == null) {
            return null;
        }
        
        if (data.startsWith("[")) {
            return deserializeList(typeProperty, data, Object.class);
        }
        
        try {
            return getMapper(typeProperty).readValue(data, Object.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Deserializes a list of objects.
     * 
     * @param data Serialized form of the list in JSON format.
     * @param clazz The class of objects found in the list.
     * @return A list of objects of the specified type.
     */
    public static <T> List<T> deserializeList(String data, Class<T> clazz) {
        return deserializeList(null, data, clazz);
    }
    
    /**
     * Deserializes a list of objects.
     * 
     * @param typeProperty The name of the property signifying the data type.
     * @param data Serialized form of the list in JSON format.
     * @param clazz The class of objects found in the list.
     * @return A list of objects of the specified type.
     */
    public static <T> List<T> deserializeList(String typeProperty, String data, Class<T> clazz) {
        try {
            return getMapper(typeProperty).readValue(data, new TypeReference<List<T>>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Enforce static class.
     */
    private JSONUtil() {
    };
}
