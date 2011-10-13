# Setup build evironment
export JAVA_HOME="/usr/lib/jvm/java-1.7.0-openjdk-`dpkg-architecture -qDEB_HOST_ARCH`"
JAVA="$JAVA_HOME/jre/bin/java"

# Perform an update to get latest backend
git pull

# Build latest binary
mvn clean install -Dmaven.test.skip=true

# Change working directory with custom settings.xml file
cd conf

# Run java executable
$JAVA -jar ../target/XenMaster-jar-with-dependencies.jar
