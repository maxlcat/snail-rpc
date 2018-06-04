package org.snail.common.serialization;

/**
 * Created by maxlcat on 2018/6/3.
 */
public interface Serializer {

    /**
     * 将对象序列化为byte[]
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] writeObject(T obj);

    /**
     * 将byte[]反序列化为对象
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T readObject(byte[] bytes, Class<T> clazz);
}
