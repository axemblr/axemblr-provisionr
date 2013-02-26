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

package com.axemblr.provisionr.api.hardware;

import static com.google.common.base.Preconditions.checkNotNull;

import com.axemblr.provisionr.api.util.BuilderWithOptions;
import com.google.common.collect.Lists;

import java.util.List;

public class HardwareBuilder extends BuilderWithOptions<HardwareBuilder> {

    private String type = "default";
    private List<BlockDevice> blockDevices = Lists.newArrayList();

    @Override
    protected HardwareBuilder getThis() {
        return this;
    }

    /**
     * This is similar to <a href="http://aws.amazon.com/ec2/instance-types/">'instance type'</a> on Amazon
     * or <a href="http://docs.cloudstack.org/Glossary">'service offering'</a> on CloudStack
     *
     * @param type
     * @return
     */
    public HardwareBuilder type(String type) {
        this.type = checkNotNull(type, "type is null");
        return this;
    }

    public HardwareBuilder blockDevices(List<BlockDevice> blockDevices) {
        this.blockDevices = checkNotNull(blockDevices, "the list of block devices is null");
        return this;
    }

    public Hardware createHardware() {
        return new Hardware(type, blockDevices, buildOptions());
    }
}
