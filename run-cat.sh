#! /bin/bash

CAT_JAR=./tools/cat.jar

if [ ! -e ${CAT_JAR} ]; then
    echo "Make sure to install ./tools/cat.jar"
    exit 1
fi

java -jar ./tools/cat.jar -classpath ./tools/magpiebridge-0.1.6-SNAPSHOT-jar-with-dependencies.jar -o cache-config.csv -attributesOnly  `find ./build/generated-src/ -name "*.java"` `find ./src/ -name "*.java"` -rta -entryPoint org.extendj.IntraJ main
