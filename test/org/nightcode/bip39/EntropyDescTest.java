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

import org.junit.Assert;
import org.junit.Test;

public class EntropyDescTest {

  @Test public void testFromNumberOfWords() {
    Assert.assertEquals(EntropyDesc.ENT_128, EntropyDesc.fromNumberOfWords(12));
    Assert.assertEquals(EntropyDesc.ENT_160, EntropyDesc.fromNumberOfWords(15));
    Assert.assertEquals(EntropyDesc.ENT_192, EntropyDesc.fromNumberOfWords(18));
    Assert.assertEquals(EntropyDesc.ENT_224, EntropyDesc.fromNumberOfWords(21));
    Assert.assertEquals(EntropyDesc.ENT_256, EntropyDesc.fromNumberOfWords(24));

    try {
      EntropyDesc.fromNumberOfWords(16);
      Assert.fail("should throw NoSuchElementException");
    } catch (Exception ex) {
      Assert.assertEquals("there is no EntropyDesc with number of words = " + 16, ex.getMessage());
    }
  }

  @Test public void testNumberOfWords() {
    Assert.assertEquals(12, EntropyDesc.ENT_128.numberOfWords());
    Assert.assertEquals(15, EntropyDesc.ENT_160.numberOfWords());
    Assert.assertEquals(18, EntropyDesc.ENT_192.numberOfWords());
    Assert.assertEquals(21, EntropyDesc.ENT_224.numberOfWords());
    Assert.assertEquals(24, EntropyDesc.ENT_256.numberOfWords());
  }
}
