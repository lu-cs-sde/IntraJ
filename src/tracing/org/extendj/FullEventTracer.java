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

import java.util.WeakHashMap;

public class FullEventTracer extends Tracer implements ASTState.Trace.Receiver {
    // Unique ID per object.
    // Technically we want a "WeakIdentityHashMap", but AST nodes don't implement
    // equals (the only counter-examples I found implement it via reference equality,
    // i.e., duplicating the default implementation).
    WeakHashMap<Object, Long> objectIDs = new WeakHashMap<Object, Long>();
    long objectIDCounter = 0;


    protected long objectID(Object obj) {
        if (objectIDs.containsKey(obj)) {
            return objectIDs.get(obj);
        }
        long newID = ++objectIDCounter;
        objectIDs.put(obj, newID);
        return newID;
    }

    @Override
    public void accept(Event event, ASTNode node,
                       String attribute,
                       Object params, Object value) {
        String paramsStr = "";
        if (params != null) {
            paramsStr = params.toString();
        }
        String nodeStr = node.getClass().getName() + "@" + objectID(node);
        String phase = IntraJ.inAnalysis() ? "analysis" : "frontend";
        System.out.println("[TRACE] " + phase + "\t" + event + "\t" + nodeStr + " . " + attribute + "(" +  paramsStr + ")"
                           + ((value == null)
                              ? ""
                              : " -> [" + value + "] : " + value.getClass().getName()));
    }

    @Override
    public void finish() {
    }
}

