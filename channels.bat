set channels=360,baidu

SET file=%1
if "%file%" NEQ  "" (
java -jar walle-cli-all.jar batch -c %channels% %file%
)