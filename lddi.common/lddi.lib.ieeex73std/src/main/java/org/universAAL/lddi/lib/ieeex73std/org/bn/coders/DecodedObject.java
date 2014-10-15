/*
 Copyright 2006-2011 Abdulla Abdurakhmanov (abdulla@latestbit.com)
 Original sources are available at www.latestbit.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package org.universAAL.lddi.lib.ieeex73std.org.bn.coders;

public final class DecodedObject<T> {
        T value;
        int size;

        public DecodedObject() {
        }
        
        public DecodedObject(T result) {
            setValue(result);
        }
        
        public DecodedObject(T result, int size) {
            setValue(result);
            setSize(size);
        }

        public T getValue() {
            return value;
        }

        public void setValue(T result) {
            this.value = result;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
}
