#!/bin/bash
    COUNT=0
    PASS=0
    javac P6.java
    for f in $(ls MiniRA)
    do
        java P6 < MiniRA/$f > A.s
        java -jar mars.jar nc sm A.s > actoutput
        java -jar kgi.jar < MiniRA/$f > expoutput
        diff -B actoutput expoutput
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
