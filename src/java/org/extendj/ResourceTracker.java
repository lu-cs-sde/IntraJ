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

import java.lang.management.ManagementFactory;
import com.sun.management.ThreadMXBean;

/**
 * Convenience class for tracking execution time, memory usage etc.
 */
public class ResourceTracker {
    State aggregate = State.zero();

    static com.sun.management.ThreadMXBean threadManagement = null;
    static {
        threadManagement = (ThreadMXBean) ManagementFactory.getThreadMXBean();
    }
    static long getAllocBytes() {
        return threadManagement.getCurrentThreadAllocatedBytes();
    }

    public ResourceTracker() {}

    public State start() {
        return State.now();
    }

    public void stop(State atStart) {
        State stop = State.now();
        this.aggregate.timeNanos += stop.timeNanos - atStart.timeNanos;
        this.aggregate.allocBytes += stop.allocBytes - atStart.allocBytes;
    }

    public State getAggregate() {
        return this.aggregate;
    }

    public long getTotalTimeNanos() {
        return aggregate.getTotalTimeNanos();
    }

    public long getTotalMemBytes() {
        return aggregate.getTotalMemBytes();
    }

    final static class State {
        long timeNanos;
        long allocBytes;

        private State(long time, long alloc) {
            this.timeNanos = time;
            this.allocBytes = alloc;
        }

        static State now() {
            return new State(System.nanoTime(), getAllocBytes());
        }

        static State zero() {
            return new State(0, 0);
        }

        public long getTotalTimeNanos() {
            return timeNanos;
        }

        public long getTotalMemBytes() {
            return allocBytes;
        }

        @Override
        public String toString() {
            return String.format("%16s ns\t%12s bytes", timeNanos, allocBytes);
        }
    }

    @Override
    public String toString() {
        return aggregate.toString();
    }
}
