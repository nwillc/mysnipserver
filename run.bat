@if "%DEBUG%" == "" @echo off
@rem Basic build and start script.

call env.bat

echo Rebuild server...
call gradlew.bat -q clean stage -x test

echo Start server...
java -cp build\staging;build\staging\* com.github.nwillc.mysnipserver.MySnipServer %*
