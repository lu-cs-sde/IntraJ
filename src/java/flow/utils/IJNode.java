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

import org.extendj.ast.ASTNode;

public class IJNode {

  Boolean isDeadAssign = false;
  String prettyPrint;
  String color = "";
  String complete_ID = "";
  String simple_ID = "";
  String dotDescription = "";
  String dotDependencies = "";
  Boolean isCFGNode = false;
  Integer rank = 0;

  /**
   * The complete_ID is composed by ID[x,y][z,w].
   * simple_ID will be just ID
   */
  public IJNode(ASTNode astnode) {
    String complete_ID = astnode.CFGName();
    this.setDependency(astnode.nodeOrder());
    this.setPrettyPrint(astnode.CFGDump());
    this.setRank(astnode.getRank());
    this.complete_ID = complete_ID;
    this.simple_ID = complete_ID.indexOf("[") == -1
                         ? complete_ID
                         : complete_ID.substring(0, complete_ID.indexOf("["));
    setIsCFGNode(false);
  }

  public void setIsCFGNode(Boolean isCFGNode) {
    this.isCFGNode = isCFGNode;
    if (!isCFGNode)
      color = "style= dotted   fillcolor=\"#eeeeee\" fontcolor=\"#aaaaaa\"";
    else
      color = "fillcolor=white   style=filled";
  }

  public Boolean isCFGNode() { return this.isCFGNode; }

  public void setDependency(String dep) { dotDependencies = dep; }
  public String getDependency() { return dotDependencies; }

  public void setIsDeadAssign(Boolean isDeadAssign) {
    this.isDeadAssign = isDeadAssign;
    if (isDeadAssign)
      color = "fillcolor=\"1 0.2 1\"   style=filled";
    else
      color = "fillcolor=white   style=filled";
  }

  public Boolean getIsDeadAssign() { return isDeadAssign; }

  public String getSimpleName() { return simple_ID; }

  public String getID() { return complete_ID; }

  public Integer getRank() { return rank; }

  public void setRank(Integer i) { this.rank = i; }

  public void setColor(String color) { this.color = color; }

  public String getColor() { return color; }

  public String getPrettyPrint() { return prettyPrint; }

  public void setPrettyPrint(String pp) { prettyPrint = pp; }

  public String getDotDescription() {
    dotDescription = "\"" + getID() + "\" [label=\"" + getSimpleName() +
                     "\\n " + getPrettyPrint() + "\", " + getColor() + "  ]\n";

    return dotDescription;
  }
}