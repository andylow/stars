cd /d "%~dp0"

set MAVEN_OPTS=-noverify -javaagent:%REBEL_HOME%\jrebel.jar %MAVEN_OPTS%

mvn jetty:run -DStars.Env=DEVELOPMENT
