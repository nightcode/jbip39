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

import org.nightcode.bip39.dictionary.Dictionary;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Bip39 {

  private static final int LAST_11_BITS_MASK = 0x07FF;

  private static final int PBKDF2_ROUNDS = 2048;

  private final Random random;
  private final Dictionary dictionary;

  public Bip39(Dictionary dictionary) {
    Objects.requireNonNull(dictionary, "dictionary");
    try {
      this.random = SecureRandom.getInstance("SHA1PRNG");
    } catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
    this.dictionary = dictionary;
  }

  public String createMnemonic(byte[] entropy) {
    BitArray entropyWithChecksum = addChecksum(entropy);
    String[] words = getWords(entropyWithChecksum, (entropy.length << 3) + (entropy.length >>> 2));

    return Normalizer.normalize(String.join(" ", words), Normalizer.Form.NFKD);
  }

  public byte[] createSeed(String mnemonic, String passphrase) throws Bip39Exception {
    char[] chars = Normalizer.normalize(mnemonic, Normalizer.Form.NFKD).toCharArray();
    byte[] salt = Normalizer.normalize("mnemonic" + passphrase, Normalizer.Form.NFKD).getBytes(StandardCharsets.UTF_8);

    PBEKeySpec spec = new PBEKeySpec(chars, salt, PBKDF2_ROUNDS, 64 * 8);
    try {
      SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
      return skf.generateSecret(spec).getEncoded();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
      throw new Bip39Exception("can't create seed", ex);
    }
  }

  public byte[] mnemonicToEntropy(String mnemonic) throws Bip39Exception {
    Objects.requireNonNull(mnemonic, "mnemonic");
    if (!Normalizer.isNormalized(mnemonic, Normalizer.Form.NFKD)) {
      mnemonic = Normalizer.normalize(mnemonic, Normalizer.Form.NFKD);
    }
    String[] words = mnemonic.split("\\s");

    int numberOfWords = words.length;
    if (numberOfWords % 3 != 0 || numberOfWords < 12 || numberOfWords > 24) {
      throw new Bip39Exception("invalid number of words [%s] in mnemonic '%s'", numberOfWords, mnemonic);
    }

    EntropyDesc entropyDesc = EntropyDesc.fromNumberOfWords(numberOfWords);

    BitArray bitArray = BitArray.ZERO;
    for (String word : words) {
      int index = dictionary.getIndex(word);
      if (index == -1) {
        throw new Bip39Exception("invalid word [%s] in mnemonic '%s'", word, mnemonic);
      }
      bitArray = bitArray.shiftLeft(11).or(index);
    }

    int checksumMask = ~(0xFFFFFFFF << entropyDesc.checksumLength());
    int checksum = bitArray.intValue() & checksumMask;
    byte[] buffer = bitArray.shiftRight(entropyDesc.checksumLength()).toByteArray();

    byte[] entropy;
    if (entropyDesc.entropyLength() > (buffer.length << 3)) {
      entropy = new byte[entropyDesc.entropyLength() >>> 3];
      System.arraycopy(buffer, 0, entropy, entropy.length - buffer.length, buffer.length);
    } else {
      entropy = Arrays.copyOf(buffer, buffer.length);
    }

    byte[] hash = hash(entropy);
    int expectedChecksum = (int) hash[0] & 0xFF;
    expectedChecksum >>= (8 - entropyDesc.checksumLength());
    if (checksum != expectedChecksum) {
      throw new Bip39Exception("invalid mnemonic, checksum incorrect");
    }

    return entropy;
  }

  public byte[] generateEntropy(EntropyDesc entropyDescription) {
    Objects.requireNonNull(entropyDescription, "entropyDescription");
    byte[] initialEntropy = new byte[entropyDescription.entropyLength() >>> 3];
    random.nextBytes(initialEntropy);
    return initialEntropy;
  }

  private BitArray addChecksum(byte[] initialEntropy) {
    byte[] hash = hash(initialEntropy);
    if (initialEntropy.length == 32) {
      byte[] buf = new byte[33];
      System.arraycopy(initialEntropy, 0, buf, 0, initialEntropy.length);
      buf[32] = hash[0];
      return new BitArray(buf);
    }

    int checksumLength = initialEntropy.length >> 2;
    int b = (int) hash[0] & 0xFF;
    b >>= (8 - checksumLength);

    BitArray bitArray = new BitArray(initialEntropy).shiftLeft(checksumLength);

    return bitArray.or(b);
  }

  private String[] getWords(BitArray entropy, int entropyBitsLength) {
    int numberOfWords = entropyBitsLength / 11;
    String[] words = new String[numberOfWords];
    for (int i = numberOfWords - 1; i >= 0; i--) {
      words[i] = dictionary.getWord(entropy.and(LAST_11_BITS_MASK).intValue());
      entropy = entropy.shiftRight(11);
    }
    return words;
  }

  private byte[] hash(byte[] src) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
    return md.digest(src);
  }
}
