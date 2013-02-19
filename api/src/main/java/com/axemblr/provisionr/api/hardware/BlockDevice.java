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

import com.axemblr.provisionr.api.util.WithOptions;
import com.google.common.base.Objects;

import java.util.Map;

public class BlockDevice extends WithOptions {

    private int size;

    public BlockDevice(int size, Map<String, String> options) {
        super(options);
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(size, getOptions());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BlockDevice other = (BlockDevice) obj;
        return Objects.equal(this.size, other.size)
            && Objects.equal(this.getOptions(), other.getOptions());
    }

    @Override
    public String toString() {
        return "BlockDevice {" +
            "size=" + size +
            ", options=" + getOptions() + "}";
    }

}
