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

package org.nightcode.bip39;

import java.util.NoSuchElementException;

public enum EntropyDesc {

  ENT_128(128, 4, 12),
  ENT_160(160, 5, 15),
  ENT_192(192, 6, 18),
  ENT_224(224, 7, 21),
  ENT_256(256, 8, 24);

  public static EntropyDesc fromNumberOfWords(int numberOfWords) {
    EntropyDesc entropyDesc;
    switch (numberOfWords) {
      case 12:
        entropyDesc = ENT_128;
        break;
      case 15:
        entropyDesc = ENT_160;
        break;
      case 18:
        entropyDesc = ENT_192;
        break;
      case 21:
        entropyDesc = ENT_224;
        break;
      case 24:
        entropyDesc = ENT_256;
        break;
      default:
        throw new NoSuchElementException("there is no EntropyDesc with number of words = " + numberOfWords);
    }
    return entropyDesc;
  }

  private final int entropyLength;
  private final int checksumLength;
  private final int numberOfWords;

  EntropyDesc(int entropyLength, int checksumLength, int numberOfWords) {
    this.entropyLength = entropyLength;
    this.checksumLength = checksumLength;
    this.numberOfWords = numberOfWords;
  }

  public int checksumLength() {
    return checksumLength;
  }

  public int entropyLength() {
    return entropyLength;
  }

  public int numberOfWords() {
    return numberOfWords;
  }
}
