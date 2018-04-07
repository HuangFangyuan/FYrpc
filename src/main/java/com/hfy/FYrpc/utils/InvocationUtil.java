package com.hfy.FYrpc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InvocationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvocationUtil.class);

    public static Object invoke(Object object, String methodName, Class<?>[] parametersType, Object[] parameters) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object result;
        javaReflect(object, methodName, parametersType, parameters);
        result = cglib(object, methodName, parametersType, parameters);
        return result;
    }

    private static Object javaReflect(Object object, String methodName, Class<?>[] parametersType, Object[] parameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        long start = System.currentTimeMillis();
        Class<?> clazz = object.getClass();
        Method method = clazz.getMethod(methodName, parametersType);
        Object result = method.invoke(object, parameters);
        LOGGER.info("java reflection cost:{}", System.currentTimeMillis() - start);
        return result;
    }


    private static Object cglib(Object object, String methodName, Class<?>[] parametersType, Object[] parameters) throws InvocationTargetException {
        long start = System.currentTimeMillis();
        FastClass clazz = FastClass.create(object.getClass());
        FastMethod method = clazz.getMethod(methodName, parametersType);
        Object result = method.invoke(object, parameters);
        LOGGER.info("cglib cost:{}", System.currentTimeMillis() - start);
        return result;
    }
}
