#!/usr/bin/env sh

# gradlew - Gradle wrapper script for Unix/Linux/macOS

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Determine location of wrapper jar
APP_HOME=$(dirname "$0")

GRADLE_WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# Execute Java
java -cp "$GRADLE_WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
