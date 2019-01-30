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

import com.google.gson.annotations.SerializedName;

public class TestVector {

  @SerializedName("entropy")
  private String entropy;

  @SerializedName("mnemonic")
  private String mnemonic;

  @SerializedName("passphrase")
  private String passphrase;

  @SerializedName("seed")
  private String seed;

  @SerializedName("bip32_xprv")
  private String bip32Xprv;

  public String getBip32Xprv() {
    return bip32Xprv;
  }

  public String getEntropy() {
    return entropy;
  }

  public String getMnemonic() {
    return mnemonic;
  }

  public String getPassphrase() {
    return passphrase;
  }

  public String getSeed() {
    return seed;
  }

  public void setBip32Xprv(String bip32Xprv) {
    this.bip32Xprv = bip32Xprv;
  }

  public void setEntropy(String entropy) {
    this.entropy = entropy;
  }

  public void setMnemonic(String mnemonic) {
    this.mnemonic = mnemonic;
  }

  public void setPassphrase(String passphrase) {
    this.passphrase = passphrase;
  }

  public void setSeed(String seed) {
    this.seed = seed;
  }
}
