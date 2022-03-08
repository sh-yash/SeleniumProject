@echo on
echo seleniumProject Automation Test is start
timeout 3
echo %DATE% , %TIME%
timeout 2
mvn clean test -D TestNG=Test.xml
echo  Test is Running....



