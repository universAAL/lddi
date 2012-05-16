call mvn install:install-file -DgroupId=com.sun.jmx -DartifactId=jmxri -Dversion=1.2.1 -Dpackaging=jar -Dfile=.\jmxri.jar
call mvn install:install-file -DgroupId=com.sun.jdmk -DartifactId=jmxtools -Dversion=1.2.1 -Dpackaging=jar -Dfile=.\jmxtools.jar
call mvn install:install-file -DgroupId=jp.go.ipa -DartifactId=jgcl -Dversion=1.0 -Dpackaging=jar -Dfile=.\jgcl-1.0.jar
call mvn install:install-file -DgroupId=org.bouncycastle -DartifactId=jce.jdk13 -Dversion=144 -Dpackaging=jar -Dfile=.\jce.jdk13-144.jar
call mvn install:install-file -DgroupId=org.ops4j.pax -DartifactId=confman -Dversion=0.2.2 -Dpackaging=jar -Dfile=.\org.ops4j.pax.configmanager_0.2.2.jar
call mvn install:install-file -DgroupId=org.apache.felix -DartifactId=org.apache.felix.devicemanager -Dversion=0.9.0-SNAPSHOT -Dpackaging=jar -Dfile=.\org.apache.felix.devicemanager-0.9.0-SNAPSHOT.jar
PAUSE
