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

import org.nightcode.bip39.dictionary.EnglishDictionary;
import org.nightcode.bip39.dictionary.JapaneseDictionary;
import org.nightcode.common.base.Hexs;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;

import org.junit.Assert;
import org.junit.Test;

public class Bip39Test {

  private static final Hexs HEX = Hexs.hex().lowerCase();

  private static final Path EN_VECTORS = Paths.get("test-resources/vectors_en_BIP39.json");
  private static final Path JP_VECTORS = Paths.get("test-resources/vectors_jp_BIP39.json");

  private final Gson gson = new Gson();
  
  @Test public void testCreateMnemonicVectorEn() throws IOException {
    TestVector[] vectors = new Gson().fromJson(Files.newBufferedReader(EN_VECTORS), TestVector[].class);

    Bip39 bip39 = new Bip39(EnglishDictionary.instance());
    for (TestVector vector : vectors) {
      String mnemonic = bip39.createMnemonic(HEX.toByteArray(vector.getEntropy()));
      Assert.assertEquals(vector.getMnemonic(), mnemonic);
    }
  }

  @Test public void testCreateMnemonicVectorJp() throws IOException {
    TestVector[] vectors = new Gson().fromJson(Files.newBufferedReader(JP_VECTORS), TestVector[].class);

    Bip39 bip39 = new Bip39(JapaneseDictionary.instance());
    for (TestVector vector : vectors) {
      String mnemonic = bip39.createMnemonic(HEX.toByteArray(vector.getEntropy()));
      Assert.assertEquals(Normalizer.normalize(vector.getMnemonic(), Normalizer.Form.NFKD), mnemonic);
    }
  }

  @Test public void testCreateSeedEn() throws IOException, Bip39Exception {
    TestVector[] vectors = gson.fromJson(Files.newBufferedReader(EN_VECTORS, StandardCharsets.UTF_8), TestVector[].class);

    Bip39 bip39 = new Bip39(EnglishDictionary.instance());
    for (TestVector vector : vectors) {
      byte[] seed = bip39.createSeed(vector.getMnemonic(), vector.getPassphrase());
      Assert.assertEquals(vector.getSeed(), HEX.fromByteArray(seed).toLowerCase());
    }
  }

  @Test public void testCreateSeedJp() throws IOException, Bip39Exception {
    TestVector[] vectors = gson.fromJson(Files.newBufferedReader(JP_VECTORS, StandardCharsets.UTF_8), TestVector[].class);

    Bip39 bip39 = new Bip39(JapaneseDictionary.instance());
    for (TestVector vector : vectors) {
      byte[] seed = bip39.createSeed(vector.getMnemonic(), vector.getPassphrase());
      Assert.assertEquals(vector.getSeed(), HEX.fromByteArray(seed).toLowerCase());
    }
  }

  @Test public void testMnemonicToEntropyVectorEn() throws IOException, Bip39Exception {
    TestVector[] vectors = new Gson().fromJson(Files.newBufferedReader(EN_VECTORS), TestVector[].class);

    Bip39 bip39 = new Bip39(EnglishDictionary.instance());
    for (TestVector vector : vectors) {
      byte[] entropy = bip39.mnemonicToEntropy(vector.getMnemonic());
      Assert.assertEquals(vector.getEntropy(), HEX.fromByteArray(entropy));
    }
  }

  @Test public void testMnemonicToEntropyVectorJp() throws IOException, Bip39Exception {
    TestVector[] vectors = new Gson().fromJson(Files.newBufferedReader(JP_VECTORS), TestVector[].class);

    Bip39 bip39 = new Bip39(JapaneseDictionary.instance());
    for (TestVector vector : vectors) {
      byte[] entropy = bip39.mnemonicToEntropy(vector.getMnemonic());
      Assert.assertEquals(vector.getEntropy(), HEX.fromByteArray(entropy));
    }
  }

  @Test public void testGenerateEntropy() {
    Bip39 bip39 = new Bip39(EnglishDictionary.instance());

    for (EntropyDesc description : EntropyDesc.values()) {
      byte[] entropy = bip39.generateEntropy(description);
      Assert.assertEquals(description.entropyLength(), entropy.length << 3);
    }
  }

  @Test public void testMnemonicToEntropyWrongNumberOfWords() {
    Bip39 bip39 = new Bip39(EnglishDictionary.instance());
    
    String badMnemonic = "word word word";

    try {
      bip39.mnemonicToEntropy(badMnemonic);
      Assert.fail("should throw Bip39Exception");
    } catch (Bip39Exception ex) {
      Assert.assertEquals("invalid number of words [3] in mnemonic 'word word word'", ex.getMessage());
    }
  }

  @Test public void testMnemonicToEntropyWrongWord() {
    Bip39 bip39 = new Bip39(EnglishDictionary.instance());

    String badMnemonic = "word word word word word word word word word word word w0rd";

    try {
      bip39.mnemonicToEntropy(badMnemonic);
      Assert.fail("should throw Bip39Exception");
    } catch (Bip39Exception ex) {
      Assert.assertEquals("invalid word [w0rd] in mnemonic '" + badMnemonic + "'", ex.getMessage());
    }
  }

  @Test public void testMnemonicToEntropyInvalidMnemonic() {
    Bip39 bip39 = new Bip39(EnglishDictionary.instance());

    String badMnemonic = "word word word word word word word word word word word abandon";
    try {
      bip39.mnemonicToEntropy(badMnemonic);
      Assert.fail("should throw Bip39Exception");
    } catch (Bip39Exception ex) {
      Assert.assertEquals("invalid mnemonic, checksum incorrect", ex.getMessage());
    }
  }
}
