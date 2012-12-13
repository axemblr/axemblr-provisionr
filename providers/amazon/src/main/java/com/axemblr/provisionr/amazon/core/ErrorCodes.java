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

package com.axemblr.provisionr.amazon.core;

public class ErrorCodes {

    private ErrorCodes() {
        /* singleton */
    }

    public static final String SECURITY_GROUP_NOT_FOUND = "InvalidGroup.NotFound";

    public static final String DUPLICATE_SECURITY_GROUP = "InvalidGroup.Duplicate";

    public static final String DUPLICATE_KEYPAIR = "InvalidKeyPair.Duplicate";

    public static final String KEYPAIR_NOT_FOUND = "InvalidKeyPair.NotFound";

}                         
