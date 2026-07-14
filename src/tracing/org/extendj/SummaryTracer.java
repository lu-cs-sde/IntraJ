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

import org.extendj.ast.ASTNode;
import org.extendj.ast.ASTState;
import org.extendj.ast.ASTState.Trace.Event;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;;
import java.util.EnumMap;
import java.util.Comparator;

public class SummaryTracer extends Tracer implements ASTState.Trace.Receiver {
    HashSet<Class<?>> observedClasses = new HashSet<Class<?>>();
    HashSet<String> observedAttrNames = new HashSet<String>();
    HashMap<Attr, EnumMap<Event, Integer>> frontendCounts = new HashMap<>();
    HashMap<Attr, EnumMap<Event, Integer>> analysisCounts = new HashMap<>();

    @Override
    public void accept(Event event, ASTNode node,
                       String attribute,
        Object params, Object value) {

        HashMap<Attr, EnumMap<Event, Integer>> counts;

        if (IntraJ.inAnalysis()) {
            // analysis time
            counts = analysisCounts;
        } else {
            counts = frontendCounts;
        }

        Attr attr = new Attr(node.getClass(), attribute);
        if (!counts.containsKey(attr)) {
            counts.put(attr, new EnumMap<Event, Integer>(Event.class));
        }
        EnumMap<Event, Integer> emap = counts.get(attr);
        if (!emap.containsKey(event)) {
            emap.put(event, 1);
        } else {
            emap.put(event, 1 + emap.get(event));
        }
    }

    protected void printAll(String prefix, HashMap<Attr, EnumMap<Event, Integer>> counts,
                            ArrayList<String> attrNames, ArrayList<Class<?>> classes) {
        for (String name: attrNames) {
            for (Class<?> cls: classes) {
                Attr attr = new Attr(cls, name);
                EnumMap<Event, Integer> events = counts.get(attr);
                if (events != null) {
                    for (Event ev: Event.values()) {
                        Integer count = events.get(ev);
                        if (count != null) {
                            System.out.println("[TRACE] " + prefix + " " + name + "\t" + cls.getSimpleName() + "\t" + ev + "\t" + count);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void finish() {
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>(observedClasses);
        ArrayList<String> attrNames = new ArrayList<String>(observedAttrNames);
        classes.sort(new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> lhs, Class<?> rhs) {
                return lhs.getSimpleName().compareTo(rhs.getSimpleName());
            }
        });
        attrNames.sort(Comparator.naturalOrder());
        printAll("frontend", frontendCounts, attrNames, classes);
        printAll("analysis", analysisCounts, attrNames, classes);
    }

    final class Attr {
        Class<?> cls;
        String attrName;

        public Attr(Class<?> cls, String attrName) {
            this.cls = cls;
            this.attrName = attrName;
            observedClasses.add(cls);
            observedAttrNames.add(attrName);
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Attr) {
                Attr oattr = (Attr) other;
                return oattr.cls.equals(this.cls) && oattr.attrName.equals(this.attrName);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.cls.hashCode() ^ this.attrName.hashCode();
        }
    }
}

