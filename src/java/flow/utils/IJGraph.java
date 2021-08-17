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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.extendj.IntraJ.FlowProfiling;
import org.extendj.ast.ASTNode;
import org.extendj.ast.CFGNode;
import org.extendj.ast.SmallSet;
import org.extendj.flow.utils.IJEdge.FlowRelation;

public class IJGraph extends SmallSet<IJNode> {
  private final Boolean visualizePred;
  private final Boolean visualizeSucc;
  private Integer numberNodes = 1;
  private Integer numberEdges = 0;
  private Map<Integer, ArrayList<IJNode>> rank = new HashMap<>();
  private SmallSet<IJEdge> edges;

  /**
   *
   * @param graph         SCGraph implementation.
   * @param visualizePred If true the predecessor edges are visualized.
   * @param visualizeSucc If true the successor edges are visualized.
   */
  public IJGraph(Boolean visualizePred, Boolean visualizeSucc) {
    super();
    this.visualizePred = visualizePred;
    this.visualizeSucc = visualizeSucc;
    this.edges = new SmallSet<IJEdge>();
  }

  public SmallSet<IJEdge> getEdgeSet() { return edges; }

  public Integer getNumberNode() { return size(); }

  public Integer getNumberEdges() { return edges.size(); }

  public void incNbrNodes() { numberNodes++; }
  public void incNbrEdges() { numberEdges++; }

  public Integer getNumberNodesCFG() { return numberNodes; }

  public Integer getNumberEdgesCFG() { return numberEdges; }

  public void generatePDF(String filename) throws IOException {

    File dotF = new File(changeExtension(filename, "_FNext_CFG.dot"));
    FileWriter dotFile = new FileWriter(dotF);
    dotFile.write("digraph G {\n");
    dotFile.write("graph [splines=ortho, nodesep=\"1\", ranksep=\"1\"]\n");
    dotFile.write("node [shape=rect, fontname=Arial];\n");
    dotFile.write(dotInit());
    for (IJNode node : this)
      dotFile.write(node.getDotDescription());
    for (IJEdge edge : getEdgeSet())
      dotFile.write(edge.getDotDescription());
    for (IJNode node : this) {
      dotFile.write(node.getDependency());
    }
    dotFile.write(genDotLegend());
    dotFile.write("}");

    dotFile.close();
    ProcessBuilder pb =
        new ProcessBuilder("dot", "-Tpdf", dotF.getAbsolutePath(), "-o",
                           changeExtension(dotF.getAbsolutePath(), ".pdf"));
    Process process = pb.start();
    process.getOutputStream().close();
    try {
      process.waitFor();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void addLabelToEdge(String ID, String label) {
    for (IJEdge e : getEdgeSet()) {
      if (e.getID().equals(ID)) {
        e.setLabel(label);
      }
    }
  }

  public String genDotLegend() {
    String res = "\nsubgraph cluster_legend {\n";
    res += "{node [style=filled, fillcolor=1, colorscheme=\"pastel13\"]\n";
    res += "Nodes [label=\"Total Nr. Nodes\n" + getNumberNode() +
           "\", fillcolor=1]\n";
    res += "Edges [label=\"Total Nr. Edges\n" + getNumberEdges() +
           "\", fillcolor=1]\n";
    res += "NodesCFG [label=\"CFG Nr. Nodes\n" + getNumberNodesCFG() +
           "\", fillcolor=3]\n";
    res += "EdgesCFG [label=\"CFG Nr. Edges\n" + getNumberEdgesCFG() +
           "\", fillcolor=3]\n";
    res += "Nodes-> \"Program[0]\" -> NodesCFG  [style=\"invis\"]\n";
    res += "Edges -> \"Program[0]\" ->EdgesCFG [style=\"invis\"]}\n";
    res += "label = \"Statistics of the framework: NEXTFramework\"\n";
    res += "style=\"solid\"\n";
    res += "ranksep=0.05\n";
    res += "nodesep=0.01\n";
    res += "labelloc = b\n";
    res += "len=2\n";
    res += "}";
    return res;
  }

  public IJNode getNode(String id) {
    for (IJNode node : this) {
      if (node.getID().equals(id))
        return node;
    }
    throw new NullPointerException(id);
  }

  public IJNode addNode(ASTNode astnode) {
    IJNode node = new IJNode(astnode);
    add(node);
    Integer index = node.getRank();
    ArrayList<IJNode> tmp = rank.get(index);
    if (tmp == null)
      tmp = new ArrayList<>();
    tmp.add(node);
    rank.put(node.getRank(), tmp);
    return node;
  }

  public void addEdge(String src, String dst, FlowRelation flow) {
    IJNode n_src, n_dst;
    n_src = getNode(src);
    n_dst = getNode(dst);
    edges.add(new IJEdge(src + dst, src, dst, flow, n_src, n_dst));
  }

  private String dotInit() {
    String dot = "";
    for (Map.Entry<Integer, ArrayList<IJNode>> entry : rank.entrySet()) {
      dot += "{ rank = same; ";
      for (IJNode node : entry.getValue()) {
        dot += "\"" + node.getID() + "\"; ";
      }
      dot = dot.substring(0, dot.length() - 2);
      dot += "}\n";
    }

    return dot;
  }

  public String toString() {
    ArrayList<String> res = new ArrayList<>();
    String toStr = new String();
    for (IJNode n : this) {
      res.add(n.getID() + "\n");
    }
    for (IJEdge e : getEdgeSet())
      res.add(e.getSourceNode().getID() + " -> " + e.getDestNode().getID() +
              "\n");

    Collections.sort(res);
    for (String s : res)
      toStr += s;
    return toStr;
  }

  public void dump() { System.out.println(toString()); }

  public boolean drawSucc() { return visualizeSucc; }

  public boolean drawPred() { return visualizePred; }

  public static String changeExtension(String filename, String newExtension) {
    int index = filename.lastIndexOf('.');
    if (index != -1) {
      return filename.substring(0, index) + newExtension;
    } else {
      return filename + newExtension;
    }
  }
}