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

package com.axemblr.provisionr.core.logging;

import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.Marker;

public class InfoStreamLogger extends StreamLogger {

    public InfoStreamLogger(InputStream inputStream, Logger logger, Marker marker) {
        super(inputStream, logger, marker);
    }

    @Override
    protected void log(Logger logger, Marker marker, String line) {
        logger.info(marker, line);
    }
}
