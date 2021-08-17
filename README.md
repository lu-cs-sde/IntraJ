# [![Build Status](https://travis-ci.com/IdrissRio/IntraJ.svg?token=LhjNDeT6NSQyX5nVEcpn&branch=master)](https://travis-ci.com/IdrissRio/IntraJ)


<p align="center">
  <img width="300"  src="resources/logo.png">
</p>

---

**IntraJ** is an application of the **[IntraCFG](https://github.com/lu-cs-sde/IntraCFG)** framework for the Java language, build as an extension of the **[ExtendJ](https://extendj.org)** Java Compiler. More details can be found in the following paper:
* __A Precise Framework for Source-Level Control-Flow Analysis__, _[Idriss Riouak 🔗](https://github.com/IdrissRio), [Christoph Reichenbach 🔗](https://creichen.net), [Görel Hedin 🔗](https://cs.lth.se/gorel-hedin/) and [Niklas Fors 🔗](https://portal.research.lu.se/portal/en/persons/niklas-fors(c1e9efdd-5891-45ec-aa9d-87b8fb7f3dbc).html)_. _[IEEE-SCAM 2021 🔗](http://www.ieee-scam.org/2021/#home)._ 

---

With **IntraJ** you can:
- 🏗 construct intra-procedural **Control Flow Graph**,
- 🔎 (*DAA*) detect **Dead assignments** in your codebase, and
- 🔎 (*NPA*) detect occurences of **NullPointerException**.


**IntraJ** supports Java versions from 4 up to 7. 

🚧 Support for Java 8 is a work in progress 🚧

---

## 🚀 How to run IntraJ 
### 1️⃣ Prerequisite

To run IntraJ is sufficient to have installed

1) **Java SDK version 7**. (tested with  SDK 7.0.292-zulu. See [sdkman](https://sdkman.io).)

To generate the CFGs PDF you need
1) **Dot** (graphiz)
2) **Vim**
3) **Python3.x** with the following dependencies:
	* **Py2PDF**


To install all the necessary Python dependencies you can run the instruction described in the next section.


### 2️⃣ Build 🛠
To clone the **IntraJ** code, run, in your working directory:
```
git clone https://github.com/IdrissRio/IntraJ.git --recursive
```

🗂 Move to the **IntraJ** directory:

```
cd IntraJ
```

⚠️ *Note the `--recursive` flag when cloning the repository! If you have previously cloned this repository and forgot the `--recursive` flag, use:* ⚠️

```
    git submodule init
    git submodule update
```

🍯 To generate the Jar file, execute

```
./gradlew jar
```

🧪 To run all the tests, execute:

```
./gradlew test
```

### 3️⃣ Python 🐍 Dependencies

To install Python dependencies, you can execute the following instruction:

```
cd resources
pip3 install - requirements.txt
```

---

### Available options:
  - `-help`: prints all the available options.
  - `-genpdf`: generates a pdf with AST structure of all the methods in the analysed files. It can be used combined with `-succ`,`-pred`.
  - `-succ`: generates a pdf with the successor relation for all the methods in the analysed files. It can be used combined with `-pred`.
  - `-pred`: generates a pdf with the predecessor relation for all the methods in the analysed files. It can be used combined with `-succ`.
  - `-statistics`: prints the number of _**CFGRoots**_, _**CFGNodes**_ and _**CFGEdges**_ in the analysed files.
  - `-nowarn`: the warning messages are not printed.

-------------- _ANALYSIS OPTIONS_ --------------------

Available analysis (`ID`):
  * `DAA`: Detects unused `dead` assignments
  * `NPA`: Detects occurrences of Null Pointer Dereferenciation
   - `-WID`: enable the analysis with the respective `ID`, e.g., `-WDAA`
   - `-Wall`: enables all the available analysis
   - `-Wexcept=ID`: enable all the available analysis except `ID`.

---

# Example 
Let us consider the `Example.java` file located in your workspace:
```
public  class  Example {
  int  example() {
    Integer  m  =  null;
    m.toString();
    int  x  =  0;
    x =  1;
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
# Related repository repositories 🔗
 - **IntraJ** (Snapshot 📸 SCAM2021) repository 🔗
 - **[IntraCFG](https://github.com/lu-cs-sde/IntraCFG)** repository 🔗
 - **IntraJEvaluation** evaluation repository 🔗

