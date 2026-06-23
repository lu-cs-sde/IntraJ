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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

/**
 * Unit tests for the lightweight {@link CLIOptions} parser
 *
 * These are deliberately frontend-free: they exercise option parsing in
 * isolation, so they run in milliseconds without the JastAdd apparatus.
 * Assertions read CLIOptions' (same-package) internal state directly.
 */
public class CLIOptionsTest {

    @Test
    public void testKnownFlagHandled() {
        List<String> observations = new ArrayList<>();
        CLIOptions opts = new CLIOptions();
        opts.flag("-WDAA", "enable DAA", v -> observations.add("DAA"));

        opts.parse(new String[] {"-WDAA"});

        assertEquals("handler triggers exactly once", Arrays.asList("DAA"), observations);
        assertTrue("unexpected CLI parse errors: " + opts.errors, opts.errors.isEmpty());
    }

    @Test
    public void testUnknownFlagReported() {
        CLIOptions opts = new CLIOptions();
        opts.flag("-WDAA", "enable DAA", v -> {});
        opts.parse(new String[] {"-WDAARGH"});

        assertEquals(1, opts.errors.size());
    }

    @Test
    public void testOptionArgHandledCorrectly() {
        final int[] counter = new int[]{0};
        CLIOptions opts = new CLIOptions();
        opts.option("-count", "counter", v -> { counter[0] += Integer.parseInt(v); });
        opts.parse(new String[] {"-count", "2", "-count", "40"});

        assertTrue("unexpected CLI parse errors: " + opts.errors, opts.errors.isEmpty());
        assertEquals("expected both option arguments to increment the counter", 42, counter[0]);
    }

    @Test
    public void testHandlerRuntimeException() {
        final int[] counter = new int[] {0};
        CLIOptions opts = new CLIOptions();
        opts.option("-count", "counter", v -> { counter[0] += Integer.parseInt(v); });
        opts.parse(new String[] {"-count", "NotASaneNumber"});

        assertEquals(1, opts.errors.size());
    }

    @Test
    public void testMissingArgForOption() {
        final int[] counter = new int[] {0};
        CLIOptions opts = new CLIOptions();
        opts.option("-count", "counter", v -> { counter[0] += Integer.parseInt(v); });
        opts.parse(new String[] {"-count", "2", "-count"});

        assertEquals(1, opts.errors.size());
    }

    @Test
    public void testTrailingFiles() {
        final int counter[] = new int[]{0};
        CLIOptions opts = new CLIOptions();
        opts.option("-count", "counter", v -> { counter[0] += Integer.parseInt(v); });
        opts.parse(new String[] {"-count", "2", "input.java"});

        assertTrue("unexpected CLI parse errors: " + opts.errors, opts.errors.isEmpty());
        assertEquals("expected counter update", 2, counter[0]);
        assertEquals(
            Arrays.asList(new String[] { "input.java" }),
            opts.getInputFiles());
    }

    @Test
    public void testExplicitTrailingFiles() {
        final int counter[] = new int[]{0};
        CLIOptions opts = new CLIOptions();
        opts.option("-count", "counter", v -> { counter[0] += Integer.parseInt(v); });
        opts.parse(new String[] {"input.java", "-count", "2", "--", "-count"});

        assertTrue("unexpected CLI parse errors: " + opts.errors, opts.errors.isEmpty());
        assertEquals("expected counter update", 2, counter[0]);
        assertEquals(
            Arrays.asList(new String[] { "input.java", "-count" }),
            opts.getInputFiles());
    }
}
