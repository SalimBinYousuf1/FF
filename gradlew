#!/bin/sh
##############################################################################
# Lucid Player — Gradle wrapper
# FIX: JVM memory flags are passed as plain shell words (unquoted)
#      so Java receives -Xmx64m NOT the string "-Xmx64m" (which causes
#      "Error: Could not find or load main class -Xmx64m")
##############################################################################

set -e

APP_HOME=$(cd "$(dirname "$0")" && pwd -P)
APP_BASE_NAME=$(basename "$0")
CLASSPATH="${APP_HOME}/gradle/wrapper/gradle-wrapper.jar"

die() {
    echo ""
    echo "ERROR: $*"
    echo ""
    exit 1
}

# ── Locate java ───────────────────────────────────────────────────────────────
if [ -n "${JAVA_HOME}" ]; then
    JAVACMD="${JAVA_HOME}/bin/java"
    [ -x "${JAVACMD}" ] || die "JAVA_HOME points to invalid directory: ${JAVA_HOME}"
else
    JAVACMD="java"
    command -v java >/dev/null 2>&1 || die "java not found in PATH. Please set JAVA_HOME."
fi

# ── Raise open-file limit (Linux) ─────────────────────────────────────────────
case "$(uname -s)" in
  Linux*)
    _hard_limit=$(ulimit -H -n 2>/dev/null || true)
    [ -n "${_hard_limit}" ] && ulimit -n "${_hard_limit}" 2>/dev/null || true
    ;;
esac

# ── Verify wrapper JAR exists ─────────────────────────────────────────────────
[ -f "${CLASSPATH}" ] || die "gradle-wrapper.jar not found at: ${CLASSPATH}
Run the 'Bootstrap Gradle wrapper JAR' CI step or add the jar to your repo."

# ── Execute Gradle ────────────────────────────────────────────────────────────
# IMPORTANT: -Xmx / -Xms must be bare words before -classpath.
# Do NOT wrap them in single or double quotes when passing to exec,
# e.g.  exec java "-Xmx64m" ...  would make Java read "-Xmx64m" as a class name.
exec "${JAVACMD}" \
    -Xmx64m \
    -Xms64m \
    ${JAVA_OPTS} \
    ${GRADLE_OPTS} \
    "-Dorg.gradle.appname=${APP_BASE_NAME}" \
    -classpath "${CLASSPATH}" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
