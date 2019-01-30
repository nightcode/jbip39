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

import org.nightcode.common.base.Hexs;

import org.junit.Assert;
import org.junit.Test;

public class BitArrayTest {

  private static final Hexs HEX = Hexs.hex();

  @Test public void testNewInstance() {
    check(HEX.toByteArray("01"));
    check(HEX.toByteArray("0102"));
    check(HEX.toByteArray("010203"));
    check(HEX.toByteArray("01020304"));
    check(HEX.toByteArray("0102030401"));
    check(HEX.toByteArray("010203040102"));
    check(HEX.toByteArray("01020304010203"));
    check(HEX.toByteArray("0102030401020304"));
    check(HEX.toByteArray("010203040102030401"));
    check(HEX.toByteArray("01020304010203040102"));
    check(HEX.toByteArray("0102030401020304010203"));
    check(HEX.toByteArray("123456781234567812345678"));
  }

  @Test public void testShiftLeft() {
    byte[] array = HEX.toByteArray("0102030401");
    BitArray bitArray = new BitArray(array);

    checkShiftLeft(0, bitArray, "0102030401");
    checkShiftLeft(1, bitArray, "0204060802");
    checkShiftLeft(2, bitArray, "04080C1004");
    checkShiftLeft(3, bitArray, "0810182008");
    checkShiftLeft(4, bitArray, "1020304010");
    checkShiftLeft(5, bitArray, "2040608020");
    checkShiftLeft(6, bitArray, "4080C10040");
    checkShiftLeft(7, bitArray, "8101820080");
    checkShiftLeft(8, bitArray, "010203040100");
    checkShiftLeft(9, bitArray, "020406080200");

    bitArray = new BitArray(new byte[0]);
    BitArray target = bitArray.shiftLeft(7);
    Assert.assertArrayEquals(new byte[0], target.toByteArray());
  }

  @Test public void testShiftRight() {
    byte[] array = HEX.toByteArray("020406080200");
    BitArray bitArray = new BitArray(array);

    checkShiftRight(0, bitArray, "020406080200");
    checkShiftRight(1, bitArray, "010203040100");
    checkShiftRight(2, bitArray, "8101820080");
    checkShiftRight(3, bitArray, "4080C10040");
    checkShiftRight(4, bitArray, "2040608020");
    checkShiftRight(5, bitArray, "1020304010");
    checkShiftRight(6, bitArray, "0810182008");
    checkShiftRight(7, bitArray, "04080C1004");
    checkShiftRight(8, bitArray, "0204060802");
    checkShiftRight(9, bitArray, "0102030401");

    bitArray = new BitArray(new byte[0]);
    BitArray target = bitArray.shiftRight(7);
    Assert.assertArrayEquals(new byte[0], target.toByteArray());
  }

  @Test public void testIntValue() {
    BitArray bitArray;

    bitArray = new BitArray(HEX.toByteArray("7FFFFFFF"));

    Assert.assertEquals(Integer.MAX_VALUE, bitArray.intValue(0, 31));
    Assert.assertEquals(2047, bitArray.intValue(20, 11));

    bitArray = new BitArray(HEX.toByteArray("7FFF"));
    Assert.assertEquals(2047, bitArray.intValue(2, 11));

    bitArray = new BitArray(HEX.toByteArray("B6DB6DB6DB6DB6DB"));
    for (int i = 0; i < 64 - 11; i++) {
      Assert.assertEquals(1462, bitArray.intValue(i++, 11));
      Assert.assertEquals(877, bitArray.intValue(i++, 11));
      Assert.assertEquals(1755, bitArray.intValue(i, 11));
    }
  }

  private void check(byte[] buffer) {
    BitArray target = new BitArray(buffer);
    byte[] byteArray = target.toByteArray();
    Assert.assertArrayEquals(buffer, byteArray);
  }

  private void checkShiftLeft(int shift, BitArray bitArray, String expected) {
    BitArray target = bitArray.shiftLeft(shift);
    Assert.assertArrayEquals(HEX.toByteArray(expected), target.toByteArray());
  }

  private void checkShiftRight(int shift, BitArray bitArray, String expected) {
    BitArray target = bitArray.shiftRight(shift);
    Assert.assertArrayEquals(HEX.toByteArray(expected), target.toByteArray());
  }
}
