set -x
set -e
export HDFS_ROOT=$1

# Build JAR
./gradlew clean jar

# Create deploy dir at build/celos-deploy
export DEPLOY_DIR=build/celos-deploy
rm -rf $DEPLOY_DIR
mkdir -p $DEPLOY_DIR/hdfs/lib

# Copy files into deploy dir
cp src/main/celos/* $DEPLOY_DIR/
cp src/main/oozie/* $DEPLOY_DIR/hdfs/
cp build/libs/* $DEPLOY_DIR/hdfs/lib/

# Run Celos CI
java -jar ../../celos-ci/build/libs/celos-ci-fat.jar --workflowName wordcount --mode deploy --deployDir $DEPLOY_DIR --target $TARGET_FILE --hdfsRoot $HDFS_ROOT
