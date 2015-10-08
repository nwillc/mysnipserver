@echo off
call gradlew.bat -q clean stage
java -cp build\staging;build\staging\* com.github.nwillc.mysnipserver.MySnipServer %*
