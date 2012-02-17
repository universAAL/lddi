@ECHO OFF
ECHO removing old 'rundir' folder ...
RMDIR /s /q "rundir"
ECHO removing old 'log' folder ...
RMDIR /s /q "log"
java -classpath lib/org.apache.felix.main-1.8.1.jar;lib/jce.jdk13-144.jar;lib/jgcl-1.0.jar;lib/vecmath-1.3.1.jar;lib/j3d-core-1.3.1.jar;lib/osgi_R4_compendium-1.0.jar;lib/slf4j-api-1.6.4.jar;lib/slf4j-simple-1.6.4.jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9997 -Dbundles.configuration.location=conf -Djava.security.policy=conf/all.policy -Dorg.osgi.framework.security="osgi" -Dorg.apache.felix.eventadmin.Timeout=000 -Dfelix.config.properties=file:conf/felix.client.run.properties org.apache.felix.main.Main

PAUSE
