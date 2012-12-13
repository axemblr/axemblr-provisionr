/*
 * Copyright (c) 2012 S.C. Axemblr Software Solutions S.R.L
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.axemblr.provisionr.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @see <a href="http://stackoverflow.com/questions/3840356" />
 */
public class AssertSerializable {

    public static <T extends Serializable> void assertSerializable(T instance, Class<T> klass) {
        try {
            assertThat(unpickle(pickle(instance), klass)).isEqualTo(instance);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private static <T extends Serializable> byte[] pickle(T instance) throws IOException {
        ByteArrayOutputStream backingStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(backingStream);
        out.writeObject(instance);
        out.close();
        return backingStream.toByteArray();
    }

    private static <T extends Serializable> T unpickle(byte[] bytes, Class<T> klass) throws IOException, ClassNotFoundException {
        ByteArrayInputStream backingStream = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(backingStream);
        Object o = in.readObject();
        return klass.cast(o);
    }
}
