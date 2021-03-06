[![IntraJ Continuous Integration](https://github.com/lu-cs-sde/IntraJ/actions/workflows/main.yml/badge.svg)](https://github.com/lu-cs-sde/IntraJ/actions/workflows/main.yml)
<p align="center">
  <img width="300"  src="resources/logo.png">
</p>

---

**IntraJ** is an application of the language independent framework **[IntraCFG](https://github.com/lu-cs-sde/IntraCFG)** for the Java language, build as an extension of the **[ExtendJ](https://extendj.org)** Java Compiler. More details can be found in the following paper:
* __[A Precise Framework for Source-Level Control-Flow Analysis](https://github.com/lu-cs-sde/IntraJSCAM2021/blob/main/intraj-preprint.pdf)__, _[Idriss Riouak π](https://github.com/IdrissRio), [Christoph Reichenbach π](https://creichen.net), [GΓΆrel Hedin π](https://cs.lth.se/gorel-hedin/) and [Niklas Fors π](https://portal.research.lu.se/portal/en/persons/niklas-fors(c1e9efdd-5891-45ec-aa9d-87b8fb7f3dbc).html)_. _[IEEE-SCAM 2021 π](http://www.ieee-scam.org/2021/#home)._ 
<p float="left" align="center">
<a href="https://icsme2021.github.io/cfp/AEandROSETrack.html">
  <img  width="70"  src="resources/Open_Research.png"> </a>
  <a href="https://icsme2021.github.io/cfp/AEandROSETrack.html"><img width="70"  src="resources/Research_Objects.png"> </a>
</p>
---

With **IntraJ** you can:
- construct intra-procedural **Control Flow Graph**,
- (*DAA*) detect **Dead assignments** in your codebase, and
- (*NPA*) detect occurences of **NullPointerException**.


You can run IntraJ on other Java codebases (in Java-4, Java-5, Java-6, and Java-7) in order to construct CFGs and get DAA and NPA analysis results.



# Build IntraJ from the source code
## Prerequisites

We have run IntraJ on the following Java version:

*  **Java SDK version 7**. (tested with  SDK 7.0.292-zulu. See [sdkman](https://sdkman.io)).

It is possible to generate PDFs that show the CFGs visually. For this you need:
1) **Dot** (graphiz) - _PDF generation_
2) **Vim** - _PDF generation_
3) **Python3.x** with the following dependencies:
    * **PyPDF2 v1.26.0** - _PDF generation_
    * **numpy v1.20.1** - _Evaluation and Plots generation_
    * **pandas v1.2.4** - _Evaluation and Plots generation_
    * **matplotlib v3.3.4** - _Evaluation and Plots generation_
    * **seaborn v0.11.1** - _Evaluation and Plots generation_
    * **ipython v7.26.0** - _Evaluation and Plots generation_


The evaluation script uses `sdkman`.
To run the evaluation you need:
* The scripts `eval.sh` and `evaluation/run_eval.sh` uses `sdkman`. If you don't have `sdkman` installed but have Java SDK 7 installed, you can comment all the lines starting with `sdk` in `eval.sh` and in `evaluation/run_eval.sh`. You install `sdkman` by running the following commands:

  ```
  curl -s "https://get.sdkman.io" | bash
  source "$HOME/.sdkman/bin/sdkman-init.sh"
  sdk install java 7.0.292-zulu
  sdk use java 7.0.292-zulu
  ```

To install all the necessary Python dependencies, you can run the instruction described in the next section.


## Build
To clone the **IntraJ** code, run, in your working directory:
```
git clone https://github.com/lu-cs-sde/IntraJ.git
```

Move to the **IntraJ** directory:

```
cd IntraJ
```

To generate all the JARs necessary for the evaluation, execute

```
./gradlew build
```

To run all the tests, execute:

```
./gradlew test
```

### Python Dependencies

To install Python dependencies, you can execute the following instruction:

```
cd resources
pip3 install - requirements.txt
```

---



### Repository overview
The top-level structure of the repository:

    .
    βββ build                                # Compiled files
    βββ evaluation                           # Scripts and dependencies for evaluation
    βββ extendj                              # ExtendJ source code
    βββ resources                            # Scripts and logo
    βββ src                                  # IntraJ source code
    |    βββ jastadd                  
    |    |     βββ CFG                       # CFG spec in Jastadd
    |    |     βββ DataFlow                  # Data flow analyses spec
    |    βββ java
    |          βββ utils                     # General helpers for visualisation
    |          βββ 
    # JUnit test spec
    βββ tools                                # IntraJ source code
    |    βββ jastadd-2.3.6-custom            # Custom version of Jastadd
    βββ testfiles                            # Automated test files
    |    βββ DataFlow
    |    βββ CFG
    βββ eval.sh                              # Evaluation entry point
    βββ LICENSE
    βββ README.md

The _entry point_ of **IntraJ** (main) is defined in:
`extendj/src/fronted-main/org/extendj/IntraJ.java`.


### The _evaluation_ folder
The directory is structured as follow:

    .
    βββ antlr-2.7.2                          # ANTLR Benchmark                  (Paper Β§5)
    βββ pmd-4.2.5                            # PMD Benchmark                    (Paper Β§5)
    βββ jfreechar-1.0.0                      # JFC Benchmark                    (Paper Β§5)
    βββ fop-0.95                             # FOP Benchmark                    (Paper Β§5)
    βββ Results.xlsx                         # Analyses results in Excel        (Paper Β§5)
    βββ Results.htm                          # Analyses results in HTML
    βββ plots.py                             # Script that generates plots
    βββ run_eval.sh                          # Called by ../eval.sh
    βββ YYYYMMDD_HHMMSS                      # Evaluation results


### The _jastadd_ folder

    .
    βββ jastadd
         βββ CFG
         |    βββ IntraCFG
         |    |    βββ CFG.ast                # Lang-independent nodes
         |    |    βββ IntraCFG.jrag          # IntraCFG spec in Jastadd        (Paper Β§2.b)
         |    βββ java4                       #                                 (Paper Β§3)
         |    |    βββ Cache.jrag             # Cache settings
         |    |    βββ Exception.jrag         # Exception spec                  (Paper Β§3.c)
         |    |    βββ Initializer.jrag       # Initializers spec               (Paper Β§3.b)
         |    |    βββ Java4.jrag             # Java4 spec
         |    |    βββ ImplictNodes.ast       # Reified nodes
         |    βββ java5                       #                                 (Paper Β§3)
         |    |     βββ Java5.jrag            # Java5 spec
         |    βββ java7                       #                                 (Paper Β§3)
         |          βββ Java7.jrag            # Java7 spec
         βββ DataFlow                         # Data flow analyses spec         (Paper Β§4)
               βββ Analysis.jrag              # Collection attributes
               βββ DeadAssignment.jrag        # DAA spec                        (Paper Β§4.c)
               βββ LiveVariableAnalysis.jrag  # LVA spec                        (Paper Β§4.b)
               βββ NullAnalysis.jrag          # NPE sepc                        (Paper Β§4.a)
---

| β οΈ Note          |
|:---------------------------|
|There is no subdirectory for `java6`, since features introduced in Java 6 do not affect the construction of the CFG. |



## Available options to IntraJ:
  - `-help`: prints all the available options.
  - `-genpdf`: generates a pdf with AST structure of all the methods in the analysed files. It can be used combined with `-succ`,`-pred`.
  - `-succ`: generates a pdf with the successor relation for all the methods in the analysed files. It can be used combined with `-pred`.
  - `-pred`: generates a pdf with the predecessor relation for all the methods in the analysed files. It can be used combined with `-succ`.
  - `-statistics`: prints the number of _**CFGRoots**_, _**CFGNodes**_ and _**CFGEdges**_ in the analysed files.
  - `-nowarn`: the warning messages are not printed.

-------------- _ANALYSIS OPTIONS_ --------------------

Available analyses:
  * `DAA`: Detects unused dead assignments
  * `NPA`: Detects occurrences of Null Pointer Dereferencing

Options (where `id` corresponds to one of the analyses above):
   - `-Wid`: enable a given analysis, e.g., `-WDAA`
   - `-Wall`: enables all the available analyses
   - `-Wexcept=id`: enable all the available analyses except `id`, e.g., `-Wexcept=DAA`

---

# Example of running IntraJ

Suppose you would like to analyze a file `Example.java` located in your workspace:
```
public class Example {
  int example() {
    Integer m = null;
    m.toString();
    int x = 0;
    x = 1;
    return x;
  }
}
```
By running the following command:

```
    java -jar intraj.jar PATH/TO/Example.java -Wall -succ -statistics
```

**IntraJ** will print the following information
```
[NPA - PATH/TO/Example.java:4,4] The object 'm' may be null at this point.
[DAA - PATH/TO/Example.java:5,9] The value stored in 'x' is never read.
[INFO]: CFG rendering
[INFO]: DOT to PDF
[INFO]: PDF file generated correctly
[STATISTIC]: Elapsed time (CFG + Dataflow): 0.11s
[STATISTIC]: Total number
[STATISTIC]: Number roots:3
[STATISTIC]: Number CFGNodes:16
[STATISTIC]: Number Edges:13
[STATISTIC]: Largest CFG in terms of nodes:12
[STATISTIC]: Largest CFG in terms of edges:11
```

And the following PDF is generated:
![Example.pdf](resources/Example.png)




---
# Related repository repositories/links π
 - π **[IntraJSCAM2021](https://github.com/lu-cs-sde/IntraJSCAM2021)**: repository submitted for Artifact Evaluation (SCAM2021) (control-flow analysis for Java)
 - π **[IntraCFG](https://github.com/lu-cs-sde/IntraCFG)**: main repository for IntraCFG (language-independent framework for control-flow analysis)
 - π **[JastAdd](https://jastadd.org)**: meta-compilation system that supports Reference Attribute Grammars. We used a custom [JastAdd](https://bitbucket.org/jastadd/jastadd2/src/f00c118684f4cc9b42931b5a491046e41d68b6bf/) version which better supports interfaces.
 - π **[ExtendJ](https://extendj.org)**: extensible Java compiler built using JastAdd. We built **IntraJ** as an Static Analysis Extension of ExtendJ. More can be found [here](https://bitbucket.org/extendj/analysis-template/src/master/). 
 - π **[SonarQube](https://www.sonarqube.org/downloads/)**: platform developed by SonarSource for continuous inspection of code quality
 - π **[JastAddJ-Intraflow](https://bitbucket.org/jastadd/jastaddj-intraflow/src/master/)**: An earlier approach to implementing intra-procedural control flow, dataflow, and dead assignment analysis for Java, also using JastAdd.

