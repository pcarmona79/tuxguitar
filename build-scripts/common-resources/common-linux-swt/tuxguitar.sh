#!/bin/sh
##SCRIPT DIR
TG_DIR=`dirname $(realpath "$0")`
##JAVA
JAVA="/usr/bin/java"
##LIBRARY_PATH
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${TG_DIR}/lib/
##CLASSPATH
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/tuxguitar.jar
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/tuxguitar-ui-toolkit.jar
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/tuxguitar-ui-toolkit-swt.jar
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/tuxguitar-lib.jar
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/tuxguitar-editor-utils.jar
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/tuxguitar-gm-utils.jar
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/tuxguitar-awt-graphics.jar
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/swt.jar
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/gervill.jar
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/itext-pdf.jar
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/itext-xmlworker.jar
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/commons-compress.jar
CLASSPATH=${CLASSPATH}:${TG_DIR}/lib/icedtea-sound.jar
CLASSPATH=${CLASSPATH}:${TG_DIR}/share/
CLASSPATH=${CLASSPATH}:${TG_DIR}/dist/
##MAINCLASS
MAINCLASS=org.herac.tuxguitar.app.TGMainSingleton
##JVM ARGUMENTS
VM_ARGS="-Xmx512m"
##EXPORT VARS
export CLASSPATH
export LD_LIBRARY_PATH
export SWT_GTK3=0
##LAUNCH
${JAVA} ${VM_ARGS} -cp :${CLASSPATH} -Dtuxguitar.home.path="${TG_DIR}" -Dtuxguitar.share.path="share/" -Djava.library.path="${LD_LIBRARY_PATH}" ${MAINCLASS} "$@"
