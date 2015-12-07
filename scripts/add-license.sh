#!/usr/bin/env bash
set x

for x in $(find . -name *.js -o -name *.java)
do
    if [ "$(grep '* Copyright 2015 Collective, Inc.' ${x})" ]
    then
        echo skipping ${x}
    else
        echo processing ${x}
        cp ./scripts/templates/license.tpl tmp.txt
        cat ${x} >> tmp.txt
        mv tmp.txt ${x}
    fi
done
