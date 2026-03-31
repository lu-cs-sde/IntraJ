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

public class IJEdge {
  public enum FlowRelation { SUCC, PRED, NONE }
  private String color = "";
  private String ID;
  private String source;
  private String destination;
  private IJNode node_source;
  private IJNode node_destination;
  private String label = "";
  private String dotDescription;
  private FlowRelation flow = FlowRelation.NONE;
  /**
   * @param flow if true then the edge represent the successor relation.
   * @param flow Otherwise the edge represent the predecessor relation.
   */

  public IJEdge(String ID, String source, String destination, FlowRelation flow,
                IJNode node_source, IJNode node_dest) {
    this.ID = ID;
    this.source = source;
    this.destination = destination;
    this.flow = flow;
    this.node_source = node_source;
    this.node_destination = node_dest;
    this.label = "";
    if (flow.equals(FlowRelation.SUCC))
      color = "color=blue, constraint=false";
    if (flow.equals(FlowRelation.PRED))
      color = "color=red, constraint=false";
    if (flow.equals(FlowRelation.NONE))
      color = "style=dashed, color=gray";
  }

  public void setLabel(String lbl) { this.label = lbl; }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof IJEdge))
      return false;
    IJEdge edge = (IJEdge)o;
    if (flow.equals(edge.getFlow()) && node_source.equals(edge.node_source) &&
        node_destination.equals(edge.node_destination))
      return true;
    return false;
  }

  public IJNode getSourceNode() { return node_source; }

  public IJNode getDestNode() { return node_destination; }

  public String getSourceString() { return source; }

  public String getDotDescription() {
    if (flow.equals(FlowRelation.SUCC))
      dotDescription = "\"" + getSourceString() + "\""
                       + " -> "
                       + "\"" + getDestString() + "\" [" + color +
                       ", xlabel=\"" + label + " \"] \n";
    if (flow.equals(FlowRelation.PRED))
      dotDescription = "\"" + getSourceString() + "\""
                       + " -> "
                       + "\"" + getDestString() + "\" [" + color + "]\n";
    if (flow.equals(FlowRelation.NONE))
      dotDescription = "\"" + getSourceString() + "\""
                       + " -> "
                       + "\"" + getDestString() + "\" [" + color + "]\n";
    return dotDescription;
  }

  public String getDestString() { return destination; }

  public FlowRelation getFlow() { return flow; }

  public String getID() { return ID; }
}