@if "%DEBUG%" == "" @echo off
@rem Basic build and start script.

echo Rebuild server...
call gradlew.bat -q clean stage

echo Start server...
java -cp build\staging;build\staging\* com.github.nwillc.mysnipserver.MySnipServer %*
