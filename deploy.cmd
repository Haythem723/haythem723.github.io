@echo off

::你的MCL绝对路径
set MCL_PATH="C:\Users\Haythem Kenway\Desktop\mirai-console"

set JAR_NAME=%1

copy .\build\mirai\%JAR_NAME% %MCL_PATH%\plugins
::copy .\runMirai.cmd %MCL_PATH%