package org.snail.common.serialization;

import org.snail.common.spi.BaseServiceLoader;

/**
 * Created by maxlcat on 2018/6/3.
 */
public class SerializerHolder {

    private static final Serializer SERIALIZER = BaseServiceLoader.load(Serializer.class);

    public static Serializer serializerImpl() {
        return SERIALIZER;
    }
}
