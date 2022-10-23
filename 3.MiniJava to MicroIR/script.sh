#!/bin/bash
    COUNT=0
    PASS=0
    javac Main.java
    for f in $(ls TestFolder/MiniJava)
    do
        java Main < TestFolder/MiniJava/$f > text
        java -jar pgi.jar < text > actoutput
        java TestFolder/MiniJava/$f > expoutput
        cmp -s actoutput expoutput
        RES=$?
        if [ $RES == 1 ] 
        then 
            echo "Match Failure - $f"
        else 
            echo "Match Success - $f"
            PASS=$(( PASS+1 ))
        fi
        COUNT=$(( COUNT+1 ))
    done
    echo "Total ${PASS}/${COUNT} cases passed"
