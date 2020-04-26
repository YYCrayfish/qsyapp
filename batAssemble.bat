@echo off


for /f "tokens=*" %%c in (applicationIdInfo.txt) do (
    echo %%c
    FOR /F "usebackq  tokens=1-5" %%I IN ('%%c') DO (
        if  %%I==1 (
            echo %%J
            echo %%K
                @call gradlew.bat assemble%%JChannels -PchannelList=%%M -PapplicationIds=%%K -Palias=%%L -PisHx=1
        )else if %%I==2 (
            echo %%J
            echo %%K
                @call gradlew.bat assemble%%JChannels -PchannelList=%%M -PapplicationIds=%%K -Palias=%%L -PisHx=0
        )

    )
)