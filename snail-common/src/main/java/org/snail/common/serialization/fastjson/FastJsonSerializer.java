package org.snail.common.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.snail.common.serialization.Serializer;

/**
 * Created by maxlcat on 2018/6/4.
 */
public class FastJsonSerializer implements Serializer{
    @Override
    public <T> byte[] writeObject(T obj) {
        return JSON.toJSONBytes(obj, SerializerFeature.SortField);
    }

    @Override
    public <T> T readObject(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz, Feature.SortFeidFastMatch);
    }
}
