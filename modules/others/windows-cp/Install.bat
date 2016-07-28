reg import "%~dp0\CredentialProvider\Register.reg"
"%programfiles(x86)%\MSBuild\14.0\Bin\MSBuild.exe" "%~dp0\CredentialProvider/SampleV2CredentialProvider.vcxproj" /t:Build /p:Configuration=Release /p:Platform=x64
copy "%~dp0\CredentialProvider\x64\Release\CrySILU2FCredentialProvider.dll" "%systemroot%\System32\"
pause