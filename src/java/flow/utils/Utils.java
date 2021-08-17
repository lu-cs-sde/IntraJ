/* Copyright (c) 2021, Idriss Riouak <idriss.riouak@cs.lth.se>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.extendj.flow.utils;

import java.io.PrintStream;

public class Utils {
  private static final String RESET = "\033[0m";          // Text Reset
  private static final String YELLOW = "\033[0;33m";      // YELLOW
  private static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW_BOLD
  private static final String GREEN_BOLD = "\033[1;32m";  // GREEN
  private static final String RED_BOLD = "\033[1;31m";    // RED
  private static final String WHITE_BOLD = "\033[1;37m";  // WHITE
  public static void printWarning(PrintStream out, String location,
                                  String msg) {
    out.print(RED_BOLD);
    out.print("[" + location + "]: ");
    out.print(WHITE_BOLD);
    out.println(msg);
    out.print(RESET);
  }

  public static void printTest(PrintStream out, String location, String msg) {
    // out.println("[" + location + " - " + msg + " ]");
    out.println("[" + location + "]");
  }

  public static void printInfo(PrintStream out, String msg) {
    out.print(GREEN_BOLD);
    out.print("[INFO]: ");
    out.print(WHITE_BOLD);
    out.println(msg);
    out.print(RESET);
  }

  public static void printStatistics(PrintStream out, String msg) {
    out.print(YELLOW_BOLD);
    out.print("[STATISTIC]: ");
    out.print(WHITE_BOLD);
    out.println(msg);
    out.print(RESET);
  }

  public static String[] removeElement(String[] arr, int index) {
    if (arr == null || index < 0 || index >= arr.length) {
      return arr;
    }
    String[] anotherArray = new String[arr.length - 1];
    for (int i = 0, k = 0; i < arr.length; i++) {
      if (i == index) {
        continue;
      }
      anotherArray[k++] = arr[i];
    }
    return anotherArray;
  }
}