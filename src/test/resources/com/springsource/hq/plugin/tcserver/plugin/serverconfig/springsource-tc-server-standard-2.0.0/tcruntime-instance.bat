@echo off
rem --------------------------------------------------------------------------- 
rem tc Runtime Provisioning Script 
rem 
rem Copyright (c) 2010 VMware, Inc.  All rights reserved.
rem --------------------------------------------------------------------------- 
rem version: 6.0.25.A-RELEASE
rem build date: 20100406141135

setlocal

rem %~dp0 is location of current script under NT
set _TIPATH=%~dp0
set _SCRIPTPATH=%_TIPATH%
set _TIPATH=%_TIPATH%\tijars

rem Strip instance_base of trailing backslash
:beginstriptrail
IF "%_SCRIPTPATH:~-1%"=="\" (
  set _SCRIPTPATH=%_SCRIPTPATH:~0,-1%
  goto beginstriptrail
) else (
  goto donestriptrail
)
:donestriptrail


rem
set MYARGS=
:setArgs
if ""%1"" == """" goto doneSetArgs
set MYARGS=%MYARGS% %1
shift
goto setArgs
:doneSetArgs

rem Add in -d and -n as a default option. If the user specifies on command line, it overrides
cmd /c java -cp "%_TIPATH%\commons-cli.jar;%_TIPATH%\groovy-all.jar;%_TIPATH%\tcruntime-instance.jar" tcruntime_instance %MYARGS% -d "%_SCRIPTPATH%" -n "%_SCRIPTPATH%"

