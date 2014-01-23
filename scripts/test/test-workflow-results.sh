#!/bin/bash

# Script for automatically comparing workflow results on Celos HDFS and expected results
# workflow name should be passed as a parameter for a script. For instance, "file-copy"

WORKFLOW=$1

exitStatus=0;
declare -a hdfsResultFiles

# for each file in Celos HDFS find corresponding file in local fs and make a diff
# result is stored in exitStatus

for f in $(hadoop fs -ls -R hdfs:///user/celos/samples/$WORKFLOW/output | tr -s " " |  cut -d " " -f 8)
do
    if ! hadoop fs -test -d $f
    then
        expectedFile=$HOME$(sed "s/hdfs:[/]\+user\/celos/\/deploy/" <<< $f)
	# echo "result file = $f    expectedFile = $expectedFile"
        hadoop fs -cat $f | diff - $expectedFile
	status=$?;
        # echo "status = $status"
        if [ $exitStatus -eq 0 ]; then exitStatus=$status; fi
        hdfsResultFiles+=($expectedFile)
    fi
done

declare -a missedResultFiles


# Check that all expected result files were there in actual results on HDFS

for f in $(find $HOME/deploy/samples/$WORKFLOW/output -type f)
do
    fileWasProcessed=false;
    for i in "${!hdfsResultFiles[@]}"; do
        hdfsFile="${hdfsResultFiles[i]}"
        if [ "$hdfsFile" == $f  ]; then
            fileWasProcessed=true;
        fi
    done
    if ! $fileWasProcessed; then
    missedResultFiles+=($f)
    fi
done

if [ ${#missedResultFiles[@]} -gt 0 ]; then
    echo "Error: files missed in result set: ${missedResultFiles[*]}"
    exitStatus=1;
fi

exit $exitStatus