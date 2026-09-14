@echo off
echo Starting Event Registration App...
java -cp "bin;sqlite-jdbc-3.45.1.0.jar;slf4j-api-2.0.12.jar;slf4j-simple-2.0.12.jar" com.vityarthi.eventapp.MainApp
pause
