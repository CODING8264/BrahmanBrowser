@echo off
rem -----------------------------------------------------------------------------
rem Gradle start up batch script for Windows
rem -----------------------------------------------------------------------------

set DIRNAME=%~dp0
set GRADLE_WRAPPER_JAR=%DIRNAME%gradle\wrapper\gradle-wrapper.jar

java -cp "%GRADLE_WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
