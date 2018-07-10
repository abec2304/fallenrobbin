// Copyright 2011 the V8 project authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

class Shine {
 
static int getFlairNum(String s) {
  int end = s.length();
  final int radix = 36;

  // The following code causes accumulating rounding error for numbers greater
  // than ~2^56. It's explicitly allowed in the spec: "if R is not 2, 4, 8, 10,
  // 16, or 32, then mathInt may be an implementation-dependent approximation to
  // the mathematical integer value" (15.1.2.2).

  final int lim_0 = '0' + (radix < 10 ? radix : 10);
  final int lim_a = 'a' + (radix - 10);
  final int lim_A = 'A' + (radix - 10);

  // NOTE: The code for computing the value may seem a bit complex at
  // first glance. It is structured to use 32-bit multiply-and-add
  // loops as long as possible to avoid loosing precision.

  double v = 0.0;
  boolean done = false; int current = 0, skip = 0;
  do {
    // Parse the longest part of the string starting at index j
    // possible while keeping the multiplier, and thus the part
    // itself, within 32 bits.
    long part = 0, multiplier = 1;
    while (true) {
      int d, _current = s.charAt(current);
      if (_current >= '0' && _current < lim_0) {
        d = _current - '0';
      } else if (_current >= 'a' && _current < lim_a) {
        d = _current - 'a' + 10;
      } else if (_current >= 'A' && _current < lim_A) {
        d = _current - 'A' + 10;
      } else if (_current == '-' || _current == '_') {
        ++skip;
        ++current;
        if (current == end) {
          done = true;
          break;
        }
        continue;
      } else {
        done = true;
        break;
      }

      // Update the value of the part as long as the multiplier fits
      // in 32 bits. When we can't guarantee that the next iteration
      // will not overflow the multiplier, we stop parsing the part
      // by leaving the loop.
      final long kMaximumMultiplier = 0xffffffffL / 36;
      long m = multiplier * radix;
      if (m > kMaximumMultiplier) break;
      part = part * radix + d;
      multiplier = m;
      //DCHECK(multiplier > part);

      ++current;
      if (current == end) {
        done = true;
        break;
      }
    }

    // Update the value and skip the part in the string.
    v = v * multiplier + part;
  } while (!done);

  if(skip == end) return 6;
  
  // Java 1.1 apparently can't do math.
  return (int)(v % 6 % 6 % 6);
}

}
