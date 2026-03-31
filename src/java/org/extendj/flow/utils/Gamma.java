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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.extendj.ast.Variable;
import org.extendj.flow.utils.AbsDomain;

public class Gamma extends HashMap<Variable, AbsDomain> {

  public Gamma() {}

  public Gamma(Gamma gamma) { super(gamma); }

  public void join(Variable var, AbsDomain abs) {
    if (var == null)
      return;
    AbsDomain oldAbs = get(var);
    if (oldAbs == null) {
      put(var, abs);
    } else {
      switch (oldAbs) {
      case NULL:
        put(var, AbsDomain.NULL);
        break;
      case NOTNULL:
        put(var, abs);
        break;
      default:
        break;
      }
    }
  }

  public void join(Gamma gamma) {
    for (Map.Entry<Variable, AbsDomain> entry : gamma.entrySet()) {
      this.join(entry.getKey(), entry.getValue());
    }
  }

  public Gamma _put(Variable var, AbsDomain abs) {
    if (var != null)
      put(var, abs);
    return this;
  }
}