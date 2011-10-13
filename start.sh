export JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-amd64/
git pull
# sudo rm -R target
mvn clean install -Dmaven.test.skip=true
cd conf
/usr/lib/jvm/java-1.7.0-openjdk-amd64/jre/bin/java -jar ../target/XenMaster-jar-with-dependencies.jar
