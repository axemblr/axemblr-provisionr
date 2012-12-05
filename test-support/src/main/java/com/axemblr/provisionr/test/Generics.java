package com.axemblr.provisionr.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper methods for class type parameters.
 *
 * @see <a href="https://github.com/codahale/dropwizard/blob/master/dropwizard-core/src/main/java/com/yammer/dropwizard/util/Generics.java">Generics in Codahale Dropwizard</a>
 * @see <a href="http://gafter.blogspot.com/2006/12/super-type-tokens.html">Super Type Tokens</a>
 */
public class Generics {
    private Generics() { /* singleton */ }

    /**
     * Finds the type parameter for the given class which is assignable to the bound class.
     *
     * @param klass a parameterized class
     * @param bound the type bound
     * @param <T>   the type bound
     * @return the class's type parameter
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getTypeParameter(Class<?> klass, Class<? super T> bound) {
        Type t = checkNotNull(klass);
        while (t instanceof Class<?>) {
            t = ((Class<?>) t).getGenericSuperclass();
        }
        /* This is not guaranteed to work for all cases with convoluted piping
         * of type parameters: but it can at least resolve straight-forward
         * extension with single type parameter (as per [Issue-89]).
         * And when it fails to do that, will indicate with specific exception.
         */
        if (t instanceof ParameterizedType) {
            // should typically have one of type parameters (first one) that matches:
            for (Type param : ((ParameterizedType) t).getActualTypeArguments()) {
                if (param instanceof Class<?>) {
                    final Class<?> cls = (Class<?>) param;
                    if (bound.isAssignableFrom(cls)) {
                        return (Class<T>) cls;
                    }
                }
            }
        }
        throw new IllegalStateException("Cannot figure out type parameterization for " + klass.getName());
    }

    /**
     * Finds the type parameter for the given class.
     *
     * @param klass a parameterized class
     * @return the class's type parameter
     */
    public static Class<?> getTypeParameter(Class<?> klass) {
        return getTypeParameter(klass, Object.class);
    }
}