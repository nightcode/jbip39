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

import org.nightcode.bip39.dictionary.ChineseSimplifiedDictionary;
import org.nightcode.bip39.dictionary.ChineseTraditionalDictionary;
import org.nightcode.bip39.dictionary.Dictionary;
import org.nightcode.bip39.dictionary.EnglishDictionary;
import org.nightcode.bip39.dictionary.FrenchDictionary;
import org.nightcode.bip39.dictionary.ItalianDictionary;
import org.nightcode.bip39.dictionary.JapaneseDictionary;
import org.nightcode.bip39.dictionary.KoreanDictionary;
import org.nightcode.bip39.dictionary.SpanishDictionary;

import org.junit.Assert;
import org.junit.Test;

public class DictionaryTest {

  @Test public void testGetIndex() {
    testGetIndex(EnglishDictionary.instance());
    testGetIndex(FrenchDictionary.instance());
    testGetIndex(ItalianDictionary.instance());
    testGetIndex(JapaneseDictionary.instance());
    testGetIndex(KoreanDictionary.instance());
    testGetIndex(SpanishDictionary.instance());
    testGetIndex(ChineseSimplifiedDictionary.instance());
    testGetIndex(ChineseTraditionalDictionary.instance());
  }

  private void testGetIndex(Dictionary dictionary) {
    for (int i = 0; i < dictionary.size(); i++) {
      String word = dictionary.getWord(i);
      Assert.assertEquals(i, dictionary.getIndex(word));
    }
  }
}
