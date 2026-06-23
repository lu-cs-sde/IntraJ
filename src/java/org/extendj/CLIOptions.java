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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Lightweight CLI options handler
 */
public class CLIOptions {
    interface Handler {
        /**
         * Trigger handler for this option
         *
         * @param value The value read for this option, or <tt>null</tt> if a flag
         */
        void apply(String value);
    }

    /**
     * Internal representatin of options
     *
     * Options may appear more than once in the <tt>options</tt> map if shortName is nonempty
     */
    protected class Opt {
        final String name;
        String shortName = ""; // "-h" or similar
        final boolean takesArg;
        final String description;
        final Handler handler;

        public Opt(String name, boolean takesArg, String description, Handler handler) {
            this.name = name;
            this.takesArg = takesArg;
            this.description = description;
            this.handler = handler;
        }
    }

    /// Options to pass through to ExtendJ, excluding input files
    protected ArrayList<String> passthroughOptions = new ArrayList<>();
    /// Input files (to pass through to ExtendJ, but also needed for pdf generation)
    protected ArrayList<String> inputFiles = new ArrayList<>();
    protected ArrayList<String> errors = new ArrayList<>();
    protected LinkedHashMap<String, Opt> options = new LinkedHashMap<>();
    /// Last option added, for fluent options
    private Opt lastOpt = null;

    /**
     * Add option with explicit option name
     */
    protected void addOption(String name, Opt newOpt) {
        if (options.containsKey(name)) {
            throw new RuntimeException("Ambiguous option ["+name+"]");
        }
        options.put(name, newOpt);
    }

    /**
     * Add option with its default name
     */
    protected CLIOptions add(Opt newOpt) {
        lastOpt = newOpt;
        addOption(newOpt.name, newOpt);
        return this;
    }

    /**
     * Add a boolean flag to the CLI options
     *
     * @param name Flag name, such as <tt>"-myflag"</tt>
     * @param description Help text
     * @param handler Action to execute if we encounter the flag;
     *   handler parameter will be <tt>null</tt>
     * @return <tt>this</tt>
     */
    public CLIOptions flag(String name, String description, Handler handler) {
        return add(new Opt(name, false, description, handler));
    }

    /**
     * Add a parameterised option to the CLI options
     *
     * Gracefully handles runtime exceptions during handler processing
     *
     * @param name Flag name, such as <tt>"-timeout"</tt>
     * @param description Help text
     * @param handler Action to execute if we encounter the flag;
     *   handler parameter will carry the specified parameter in string form.
     * @return <tt>this</tt>
     */
    public CLIOptions option(String name, String description, Handler handler) {
        return add(new Opt(name, true, description, handler));
    }

    /**
     * Add a parameterised option to the CLI options
     *
     * Gracefully handles runtime exceptions during handler processing
     *
     * @param name Flag name, such as <tt>"-timeout"</tt>
     * @param description Help text
     * @param handler Action to execute if we encounter the flag;
     *   handler parameter will carry the specified parameter in string form.
     * @return <tt>this</tt>
     */
    public CLIOptions passthrough(String name, boolean takesArg, String description) {
        return add(new Opt(name, takesArg, description, (v) -> {
            passthroughOptions.add(name);
            if (v != null) {
                passthroughOptions.add(v);
            }}));
    }

    /**
     * Add a short form alternative to the most recently added flag or option
     *
     * @param c Short-form name, such as <tt>'h'</tt> for <tt>"-h"</tt>
     * @return <tt>this</tt>
     */
    public CLIOptions withShort(char c) {
        lastOpt.shortName = "-" + c;
        addOption(lastOpt.shortName, lastOpt);
        return this;
    }

    /**
     * Parses input and finalises the internal state
     *
     */
    public void parse(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            final String arg = args[i];
            if (arg.startsWith("-")) {
                if (arg.equals("--")) {
                    inputFiles.addAll(Arrays.asList(args).subList(i+1, args.length));
                    return;
                }
                if (options.containsKey(arg)) {
                    Opt opt = options.get(arg);
                    String optArg = null;
                    if (opt.takesArg) {
                        i += 1;
                        if (i >= args.length) {
                            errors.add("Option is missing argument: '" + arg + "'");
                            return;
                        }
                        optArg = args[i];
                    }
                    try {
                        opt.handler.apply(optArg);
                    } catch (RuntimeException exn) {
                        if (optArg == null) {
                            // internal error
                            throw exn;
                        } else {
                            errors.add("Invalid value '" + optArg + "' for " + arg + ": " + exn);
                        }
                    }
                } else {
                    errors.add("Unknown option: '" + arg + "'");
                }
            } else {
                inputFiles.add(arg);
            }
        }
    }

    /**
     * Tabulates information about the available options
     */
    public void printHelp(PrintStream out) {
        final int width = options.values().stream()
            .mapToInt(opt -> opt.name.length())
            .max().orElse(0);
        // Eliminate duplicates
        LinkedHashSet<Opt> optionsSet = new LinkedHashSet<>(options.values());
        for (Opt opt: optionsSet) {
            if (opt.description != null) {
                out.println(String.format("  %-" + width + "s %4s  %s", opt.name, opt.shortName, opt.description));
            }
        }
    }

    /**
     * Print any errors encountered during option parsing
     *
     * @return <tt>true</tt> iff we encountered any errors
     */
    public boolean printErrors(PrintStream out) {
        for (String error: errors) {
            out.println(error);
        }
        return !errors.isEmpty();
    }

    /**
     * Obtain any input files after option parsing
     *
     * @return List of specified input files
     */
    public ArrayList<? extends String> getInputFiles() {
        return inputFiles;
    }

    /**
     * Obtain all ExtendJ options to pass through
     *
     * @return Passed-through ExtendJ options, including any specified input files
     */
    public String[] getExtendJOptions() {
        ArrayList<String> result = new ArrayList<>(passthroughOptions);
        result.addAll(inputFiles);
        return result.toArray(new String[result.size()]);
    }
}
