#pragma once
#include <Windows.h>
#include <winreg.h>
#include <algorithm>
#include <string>
#include <iterator>
#include <wincrypt.h>
#include <winhttp.h>
#include "json.h"
#include <tchar.h>
#include <shlwapi.h>
#include <atlbase.h>
#include <atlconv.h>
#include "CrySilMessages.h"

#pragma warning(disable : 4146)
#pragma comment(lib, "winhttp.lib")
#pragma comment(lib, "advapi32.lib")
#pragma comment(lib, "crypt32.lib")
#pragma comment(lib, "credui.lib")
#pragma comment(lib, "bcrypt.lib")
#pragma comment(lib, "ncrypt.lib")
#pragma comment(lib, "shlwapi.lib")

namespace Helper
{
	namespace Registry
	{
		#define CRYSIL_REG_KEY       L"SOFTWARE\\CrySIL\\U2F\\"
		LONG GetStringRegKey(HKEY hKey, LPCWSTR sValueName, PWSTR *sValue);
		BOOL GetRegValue(LPCWSTR sValueName, PWSTR *sValue, PWSTR sUserSid);
	}
	namespace String
	{
		std::string wstr2str(PCWSTR s);
		BOOL str2wstr(std::string s, PWSTR* val);
		BOOL Base64UrlDecode(TCHAR* cs);
		BOOL Base64UrlEncode(TCHAR* cs);
		void PrintBuffer(BYTE* buf, const DWORD len);
		BOOL HexEncode(PWSTR* val);
	}
	namespace Crypto
	{
		#define NT_SUCCESS(Status)   (((NTSTATUS)(Status)) >= 0)
		#define STATUS_UNSUCCESSFUL  ((NTSTATUS)0xC0000001L)
		BOOL Base64Encode(PBYTE pInput, DWORD lInput, std::string* sOutput);
		BOOL Base64Decode(PWSTR sInput, PBYTE* pOutput, DWORD* lOutput);
		BOOL CreateRandomChallenge(PWSTR *challenge);
		BOOL VerifySignature(PBYTE hashBuf, DWORD hashLen, PBYTE sigBuf, DWORD sigLen, PBYTE keyBuf, DWORD keyLen);
		PBYTE CalculateHash(BYTE* buf, const DWORD len);
	}
	namespace CrySIL
	{
		#define CRYSIL_APP_ID        L"windows10"
		#define CRYSIL_U2F_VERSION   "U2F_V2"
		BOOL HandleU2FResponse(LPSTR pszOutBuffer, PCWSTR requestChallenge, PCWSTR requestKeyHandle, PCWSTR requestAppId, PWSTR sPublicKey);
		BOOL PerformU2FRequest(PWSTR userSid, PinHandler* pinHandler);
	}
	namespace Debug
	{
		#define ZERO(NAME) \
							ZeroMemory(NAME, sizeof(NAME))
		#define INIT_ZERO_CHAR(NAME, SIZE) \
							char NAME[SIZE]; \
							ZERO(NAME)
		#define LOGFILE_NAME "C:\\logfile.txt"
		#define MAX_TIME_SIZE 250
		#define DebugPrintLn(message) Helper::Debug::PrintLn(message,__FILE__,__LINE__) 

		void PrintLn(const char *message, char *file, int line);
		void PrintLn(int integer, char *file, int line);
		void WriteLogFile(const char* szString);

		void GetCurrentTimeAndDate(char(&time)[MAX_TIME_SIZE]);
	}
}