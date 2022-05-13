package org.extendj.magpiebridge.analysis;

import org.extendj.magpiebridge.StaticServerAnalysis;

public class AnalysisInjector {
  public static void initAnalysis(StaticServerAnalysis analysis) {
    // IntraJ
    analysis.addAnalysis(new StringEqAnalysis());
    analysis.addAnalysis(new DAAnalysis());
    analysis.addAnalysis(new NPAnalysis());
  }
}
