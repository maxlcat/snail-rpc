package org.snail.common.spi;

import java.util.ServiceLoader;

/**
 * Created by maxlcat on 2018/6/3.
 */
public final class BaseServiceLoader {

    public static<S> S load(Class<S> clazz) {
        return ServiceLoader.load(clazz).iterator().next();
    }
}
