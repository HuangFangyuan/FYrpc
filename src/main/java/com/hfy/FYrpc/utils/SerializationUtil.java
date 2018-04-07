package com.hfy.FYrpc.utils;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializationUtil {

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private static Objenesis objenesis = new ObjenesisStd(true);

    private SerializationUtil() {
    }

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>)cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.getSchema(cls);
            if (schema != null) {
                cachedSchema.put(cls, schema);
            }
        }
        return schema;
    }
    /*
        参数类型不用Object,而用T的理由:

     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T message) {
        Class<T> cls =  (Class<T>)message.getClass();
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        return ProtobufIOUtil.toByteArray(message, getSchema(cls), linkedBuffer);
    }

    public static <T> T deserialize(byte[] data, Class<T> cls) {
        T message = objenesis.newInstance(cls);
        ProtobufIOUtil.mergeFrom(data, message, getSchema(cls));
        return message;
    }


}
