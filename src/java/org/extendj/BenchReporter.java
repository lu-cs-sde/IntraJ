/***************************************************************************
 * Copyright (C) 2026 The JastAdd Team
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

package org.extendj;

import java.util.OptionalLong;
import java.util.HashSet;

/**
 * Benchmarking result reporter
 */
public interface BenchReporter {
    /**
     * Report a benchmarking result
     */
    public void log(String property, String value);

    default public void logMap(String property, String key, String value) {
        log(property, key + "\t" + value);
    }

    default public void log(String property, long value) {
        log(property, Long.toString(value));
    }

    /**
     * Log double with specififed number of digits in precision
     */
    default public void logDouble(String property, double value, int digitsPrecision) {
        log(property, String.format("%." + digitsPrecision + "f", value));
    }

    default public void log(String property, OptionalLong value) {
        String s = "U";
        if (value.isPresent()) {
            s = Long.toString(value.getAsLong());
        }
        log(property, s);
    }

    default public void logWallTime() {
        logDouble("wall-time", System.currentTimeMillis() / 1_000.0, 3);
    }

    static void $log(String subId, String property, String value) {
        System.out.println("L " + subId + "\t" + property + "\t" + value);
    }

    /**
     * Per-iteration reporter
     */
    public abstract static class Iterated implements BenchReporter {

        protected static int iteration = 0;
        protected String activity;
        protected static HashSet<String> registeredIteratedReporters = new HashSet<String>();

        private Iterated(String activity) {
            this.activity = activity;
        }

        public int nextIteration() {
            return ++iteration;
        }

        public Iterated subReporter(String name) {
            if (iteration > 0) {
                throw new RuntimeException("Must not create new subReporter after iteration started");
            }
            if (registeredIteratedReporters.contains(name)) {
                throw new RuntimeException("BenchReporter.Iterated: subReporter("+name+") already registered");
            }
            registeredIteratedReporters.add(name);
            RUN.logMap("parent-activity", name, this.activity);
            return new Iterated(name) {};
        }

        @Override
        public void log(String property, String value) {
            $log(iteration + "-" + activity, property, value);
        }


        static Iterated _ITER = null;
        static Iterated _RESET = null;
        static Iterated _RUN = new Iterated(":RUN:") {
            @Override
            public void log(String property, String value) {
                // Don't report iteration count
                $log(":RUN:", property, value);
            }
        };
    }

    /**
     * Global run reporter (header)
     */
    public static BenchReporter RUN = Iterated._RUN;

    /**
     * Per-iteration reporter
     */
    public static Iterated iter() {
        if (Iterated._ITER == null) {
            Iterated._ITER = Iterated._RUN.subReporter("iter");
        }
        return Iterated._ITER;
    }

    /**
     * Reset reporter (not part of iter, but runs between iterations)
     */
    public static Iterated reset() {
        if (Iterated._RESET == null) {
            Iterated._RESET = Iterated._RUN.subReporter("reset");
        }
        return Iterated._RESET;
    }
}

