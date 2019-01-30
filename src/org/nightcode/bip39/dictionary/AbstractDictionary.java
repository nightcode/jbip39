/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.nightcode.bip39.dictionary;

import java.util.Arrays;

abstract class AbstractDictionary implements Dictionary {

  private static final int MAGIC = 0xB46394CD;

  private final String[] dictionary;
  private final int[] indexes;
  private final int shift;

  AbstractDictionary(String[] dictionary) {
    this.dictionary = dictionary;

    int length = Integer.highestOneBit(dictionary.length) << 1;
    shift = 32 - Integer.numberOfTrailingZeros(length);
    indexes = new int[length];
    Arrays.fill(indexes, -1);

    for (int index = 0; index < dictionary.length; index++) {
      String word = dictionary[index];
      int i = (word.hashCode() * MAGIC) >>> shift;
      while (indexes[i] != -1) {
        if (i == 0) {
          i = indexes.length;
        }
        i--;
      }
      indexes[i] = index;
    }
  }

  @Override public int getIndex(String word) {
    int i = (word.hashCode() * MAGIC) >>> shift;
    String k;
    while (!word.equals(k = getWord(indexes[i]))) {
      if (k == null) {
        return -1;
      }
      if (i == 0) {
        i = indexes.length;
      }
      i--;
    }
    return indexes[i];
  }

  @Override public String getWord(int index) {
    if (index < 0 || index >= dictionary.length) {
      return null;
    }
    return dictionary[index];
  }

  @Override public int size() {
    return dictionary.length;
  }
}
