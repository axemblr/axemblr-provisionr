/*
 * Copyright (c) 2013 S.C. Axemblr Software Solutions S.R.L
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

package com.axemblr.provisionr.test;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import java.io.IOException;

public class Constants {

    static final String PUBLIC_KEY_FILE_NAME = "id_rsa_test.pub";
    static final String PRIVATE_KEY_FILE_NAME = "id_rsa_test";

    public static String PUBLIC_KEY;
    public static String PRIVATE_KEY;

    static {
        try {
            PUBLIC_KEY = Resources.toString(Resources.getResource(PUBLIC_KEY_FILE_NAME), Charsets.UTF_8);
            PRIVATE_KEY = Resources.toString(Resources.getResource(PRIVATE_KEY_FILE_NAME), Charsets.UTF_8);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }
}
