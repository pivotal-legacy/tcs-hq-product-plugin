# Edit this file to set custom options
# Tomcat accepts two parameters JAVA_OPTS and CATALINA_OPTS
# JAVA_OPTS are used during START/STOP/RUN
# CATALINA_OPTS are used during START/RUN

JRE_HOME=/local/jre
AGENT_PATHS=""
JAVA_AGENTS=""
JAVA_LIBRARY_PATH=""
JVM_OPTS="-XX:MaxPermSize=1024m -Xms1024m -Xmx4096m -XX:PermSize=768m -Xss256k -d64 -XX:NewRatio=8 -XX:+UseConcMarkSweepGC -XX:+UseTLAB -XX:+DisableExplicitGC -XX:+CMSIncrementalMode -XX:+CMSClassUnloadingEnabled -XX:+UseCompressedOops -Djava.awt.headless=true -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Dcom.services.logging.disableRemoteList=true -Dcom.services.logging.disableRemoteLogging=true -Dcom.log.config.ignoreContextClassLoader=true -Dwebreportstudio.file.cleanup.interval=60 -Dspring.security.strategy=MODE_INHERITABLETHREADLOCAL -Dcom.log.config.url="file:////install/cfg1/config/Lev1/Web/Common/LogConfig" -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false -Dmulticast_udp_ip_ttl=0 -Dmulticast.address=239.1.1.1 -Dmulticast.port=31001 -Djms.authentication.decorator=false -Dscs.host=rdcesx05058.race.com -Dscs.port=8080 -Dcs.repository.dir="/install/Server1_1/Repository" -Dcom.server.isclustered=false -Dcontainer.identifier=vfabrictcsvr -Dcache.locators=rdcesx05058.race.com[41415] -Dspring.profiles.active=locators -Dauto.publish.port=8080 -Dappserver.instance.id=Server1_1_rdcesx05058.race.com -Dconfig.lev.web.appserver.logs.dir="/install/cfg1/config/Lev1/Web/Logs/Server1_1" -Djava.security.auth.login.config="/install/cfg1/config/Lev1/Web/WebAppServer/Server1_1/conf/jaas.config" -Dmetadata.use.cluster.properties=true -Ddeploy.dir="/install/cfg1/config/Lev1/Web/WebAppServer/Server1_1/_webapps" -Ddeployment.agent.client.config="/install/cfg1/Home/RemoteDeploymentAgentClient/2.1/deployagtclt.properties""
JAVA_OPTS="$JVM_OPTS $AGENT_PATHS $JAVA_AGENTS $JAVA_LIBRARY_PATH"
CLASSPATH="$CATALINA_HOME/bin/tomcat-juli-adapters.jar:$CATALINA_BASE/lib/log4j.jar:$CATALINA_BASE/lib:$CATALINA_BASE/conf:$JRE_HOME/../lib/tools.jar"
LOGGING_CONFIG="-Dnop"
CATALINA_OUT="$CATALINA_BASE/logs/server.log"
#JAVA_HOME=""
