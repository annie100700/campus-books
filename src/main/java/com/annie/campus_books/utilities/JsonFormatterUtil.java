package com.annie.campus_books.utilities;

import lombok.AllArgsConstructor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.format.AbstractJsonFormatMapper;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Type;

@AllArgsConstructor
public final class JsonFormatterUtil extends AbstractJsonFormatMapper {

    private final JsonMapper jsonMapper;

    public JsonFormatterUtil() {
        this(JsonMapper.builder().build());
    }


    @Override
    protected <T> T fromString(CharSequence charSequence, Type type) {
        return jsonMapper.readValue(charSequence.toString(),jsonMapper.constructType(type));
    }

    @Override
    protected <T> String toString(T value, Type type) {
        return jsonMapper.writerFor(jsonMapper.constructType(type)).writeValueAsString(value);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return JsonMapper.class.isAssignableFrom(sourceType);
    }

    @Override
    public boolean supportsTargetType(Class<?> targetType) {
        return JsonMapper.class.isAssignableFrom(targetType);
    }

    @Override
    public <T> void writeToTarget(T value, JavaType<T> javaType, Object target, WrapperOptions options) {

        jsonMapper.writerFor(jsonMapper.constructType(javaType.getJavaType())).
                writeValue((JsonGenerator)target, value);
    }

    @Override
    public <T> T readFromSource(JavaType<T> javaType, Object source, WrapperOptions options) {
        return jsonMapper.readValue((JsonParser) source, jsonMapper.constructType(javaType.getJavaType()));
    }

}
