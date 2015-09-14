set -x
set -e
export HDFS_ROOT=$1

# Put deploy dir into build/
export DEPLOY_DIR=build/celos-deploy
rm -rf $DEPLOY_DIR
mkdir -p $DEPLOY_DIR/hdfs/lib

# Build and copy files into deploy dir
./gradlew clean jar
cp src/main/celos/* $DEPLOY_DIR/
cp src/main/oozie/* $DEPLOY_DIR/hdfs/
cp build/libs/* $DEPLOY_DIR/hdfs/lib/

# Run Celos CI
java -jar ../../celos-ci/build/libs/celos-ci-fat.jar --workflowName wordcount --mode deploy --deployDir $DEPLOY_DIR --target $TARGET --hdfsRoot $HDFS_ROOT
