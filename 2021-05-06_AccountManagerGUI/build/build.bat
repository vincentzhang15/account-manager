dir /s /B ..\src\*.java  > sources.txt

javac -d ../class  @sources.txt

xcopy /s /y ..\res ..\class

jar -cfe AccountManager.jar AccountManager  -C ..\class .

javadoc -d ..\javadoc ..\src\*.java

pause