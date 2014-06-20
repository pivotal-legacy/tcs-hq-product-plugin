JAVA_HOME="/export/webapps/scommon/jdk/jdk1.6.0"
JVM_OPTS="-Xmx512M -Xss192K"
MULTI_LINE="data1 \
		    data2"
# This is a just a comment that should not appear in MULTI_LINE
MULTI2=data1 data2\
			data3
SAVE_JVM_OPTS=$JVM_OPTS
JVM_OPTS=$SAVE_JVM_OPTS
