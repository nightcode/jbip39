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

import java.util.Arrays;

class BitArray {

  static final BitArray ZERO = new BitArray(new byte[0]);

  private int[] bits;
  private int size;

  BitArray(byte[] src) {
    int nBytes = src.length;
    int offsetBytes = 0;
    for (; offsetBytes < nBytes && src[offsetBytes] == 0;) {
      offsetBytes++;
    }

    int nInts = (((nBytes - offsetBytes) + 3) & ~3) >>> 2;
    int[] buffer = new int[nInts];
    int b = nBytes - 1;
    for (int i = nInts - 1; i >= 0; i--) {
      buffer[i] = src[b--] & 0xFF;
      int bitsToCopy = Math.min(24, ((b - offsetBytes) + 1) << 3);
      for (int j = 8; j <= bitsToCopy; j += 8) {
        buffer[i] |= ((src[b--] & 0xFF) << j);
      }
    }

    bits = buffer;
    size = buffer.length;
  }

  private BitArray(int[] src) {
    this.bits = src;
    this.size = src.length;
  }

  BitArray and(int value) {
    if (size == 0) {
      return ZERO;
    }
    return new BitArray(new int[] {value & bits[size - 1]});
  }

  BitArray or(int value) {
    if (size == 0) {
      return new BitArray(new int[]{value});
    }
    int[] buffer = Arrays.copyOf(bits, size);
    buffer[buffer.length - 1] |= value;
    return new BitArray(buffer);
  }

  int intValue() {
    if (size == 0) {
      return 0;
    }
    return bits[size - 1];
  }

  int intValue(int offset, int length) {
    int bitLength = bitLength();
    if ((offset + length) > bitLength) {
      throw new IllegalArgumentException("there is no enough bits for getting int from position [" + offset + "]");
    }
    if (offset < 0) {
      throw new IllegalArgumentException("invalid offset value [" + offset + "], must be greater than 0");
    }
    if (length < 0 || length > 31) {
      throw new IllegalArgumentException("invalid length value [" + length + "], must be more then 0 and less then 32");
    }

    int intMask = ~(0xFFFFFFFF << length);
    int nBits = bitLength & 0x1F;
    int bitOffset = (nBits == 0) ? offset : offset + 32 - nBits;

    int first = bitOffset >>> 5;
    int last = (bitOffset + length - 1) >>> 5;

    int leftShift = (bitOffset & 0x1F) - 32 + length;
    int rightShift = 32 - ((bitOffset + length) & 0x1F);

    return ((bits[first] << leftShift) | (bits[last] >>> rightShift)) & intMask;
  }

  BitArray shiftLeft(int shift) {
    if (size == 0) {
      return ZERO;
    }
    int nBits = shift & 0x1F;
    int nInts = shift >>> 5;
    int[] buffer;

    if (nBits == 0) {
      buffer = new int[size + nInts];
      System.arraycopy(bits, 0, buffer, 0, size);
    } else {
      int i = 0;
      int diff = 32 - nBits;
      int mostLeft = bits[0] >>> diff;
      if (mostLeft == 0) {
        buffer = new int[size + nInts];
      } else {
        buffer = new int[size + nInts + 1];
        buffer[i++] = mostLeft;
      }
      int j;
      for (j = 0; j < size - 1; j++) {
        buffer[i++] = bits[j] << nBits | bits[j + 1] >>> diff;
      }
      buffer[i] = bits[j] << nBits;
    }

    return new BitArray(buffer);
  }

  BitArray shiftRight(int shift) {
    if (size == 0) {
      return ZERO;
    }
    int nBits = shift & 0x1F;
    int nInts = shift >>> 5;
    int[] buffer;

    if (nInts >= size) {
      return new BitArray(new byte[0]);
    }

    if (nBits == 0) {
      buffer = new int[size - nInts];
      System.arraycopy(bits, 0, buffer, 0, buffer.length);
    } else {
      int i = 0;
      int diff = 32 - nBits;
      int mostLeft = bits[0] >>> nBits;
      if (mostLeft == 0) {
        buffer = new int[size - nInts - 1];
      } else {
        buffer = new int[size - nInts];
        buffer[i++] = mostLeft;
      }
      for (int j = 0; j < size - 1; j++) {
        buffer[i++] = bits[j] << diff | bits[j + 1] >>> nBits;
      }
    }

    return new BitArray(buffer);
  }

  byte[] toByteArray() {
    int nBits = bitLength();
    int nBytes = ((nBits & 0x7) > 0) ? (nBits >>> 3) + 1 : (nBits >>> 3);
    byte[] buffer = new byte[nBytes];
    int diff = nBytes & 0x3;
    int offset = (diff > 0) ? 1 : 0;

    for (int i = 0; i < diff; i++) {
      buffer[i] = (byte) ((bits[0] >>> 8 * (diff - 1 - i)) & 0xFF);
    }
    copy(offset, size, buffer, diff);

    return buffer;
  }

  private int bitLength() {
    if (size == 0) {
      return 0;
    }
    return ((size - 1) << 5) + 32 - Integer.numberOfLeadingZeros(bits[0]);
  }

  private void copy(int srcOffset, int length, byte[] dest, int dstOffset) {
    for (int i = srcOffset, j = dstOffset; i < length; i++) {
      dest[j++] = (byte) ((bits[i] >>> 24) & 0xFF);
      dest[j++] = (byte) ((bits[i] >>> 16) & 0xFF);
      dest[j++] = (byte) ((bits[i] >>>  8) & 0xFF);
      dest[j++] = (byte) ((bits[i] >>>  0) & 0xFF);
    }
  }
}
