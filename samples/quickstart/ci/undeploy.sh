set -x
set -e
export HDFS_ROOT=$1

# Run Celos CI
java -jar ../../celos-ci/build/libs/celos-ci-fat.jar --workflowName wordcount --mode undeploy --target $TARGET_FILE --hdfsRoot $HDFS_ROOT
