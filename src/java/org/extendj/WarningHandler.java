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

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.extendj.ast.Warning;
import org.extendj.ast.StaticAnalysis;

/**
 * Handle a warning message for a specified source file
 */
interface WarningHandler {
    public void handle(String sourceFile, Warning warning);
    public void reset();

    /**
     * Dispatches to multiple warning handlers
     */
    public static class Multi implements WarningHandler{
        protected ArrayList<WarningHandler> handlers = new ArrayList<>();
        public Multi(WarningHandler ... handlers) {
            this.handlers.addAll(java.util.Arrays.asList(handlers));
        }

        public void add(WarningHandler handler) {
            this.handlers.add(handler);
        }

        public void reset() {
            for (WarningHandler handler : this.handlers) {
                handler.reset();
            }
        }

        @Override
        public void handle(String sourceFile, Warning warning) {
            for (WarningHandler handler : this.handlers) {
                handler.handle(sourceFile, warning);
            }
        }
    }

    /**
     * Collects all warnings in an ordered map
     *
     * Provides facilities for computing an sha256 hash over the warnings
     */
    public static class Collect implements WarningHandler {
        TreeMap<String, PerFileCollector> fileMap;
        { reset(); }

        @Override
        public void handle(String sourceFile, Warning warning) {
            if (!fileMap.containsKey(sourceFile)) {
                fileMap.put(sourceFile, new PerFileCollector());
            }
            warning.print(fileMap.get(sourceFile).writer);
        }

        public void reset() {
            fileMap = new TreeMap<String, PerFileCollector>();
        }

        /**
         * Compute cryptographic hash digest of the output
         *
         * @param digestKind A supported digest such as "MD5" or "SHA-256"
         */
        public String digest(String digestKind) {
            try {
		MessageDigest digest = MessageDigest.getInstance(digestKind);
                for (Map.Entry<String, PerFileCollector> entry: fileMap.entrySet()) {
                    digest.update(entry.getKey().getBytes(StandardCharsets.UTF_8));
                    entry.getValue().addTo(digest);
                }
                byte[] hash = digest.digest();

                StringBuilder sb = new StringBuilder(hash.length * 2);
                for(byte b: hash) {
                    sb.append(String.format("%02x", b));
                }
                return sb.toString();
	    } catch (NoSuchAlgorithmException exn) {
		throw new RuntimeException(exn);
	    }
        }

        public String md5() {
            return digest("MD5");
        }

        static class PerFileCollector {
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            PrintStream writer;
            {
                try {
                    writer =new PrintStream(ostream, true, StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException exn) {
                    throw new RuntimeException(exn);
                }
            }
            void addTo(MessageDigest digest) {
                digest.update(ostream.toByteArray());
            }
        }
    }

    /**
     * Counts number of warnings
     */
    public static class Count implements WarningHandler {
        int counter = 0;
        @Override
        public void handle(String sourceFile, Warning warning) {
            ++counter;
        }
        public int get() {
            return counter;
        }
        public void reset() {
            this.counter = 0;
        }
    }

    /**
     * Prints warnings to a specified output stream
     */
    public static class Print implements WarningHandler {
        protected PrintStream output;
        public Print(PrintStream output) {
            this.output = output;
        }
        @Override
        public void handle(String sourceFile, Warning warning) {
            warning.print(this.output);
        }
        public void reset() {
        }
    }
}
