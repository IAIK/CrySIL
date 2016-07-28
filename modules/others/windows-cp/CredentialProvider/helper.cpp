#include "helper.h"
#include "CrySilMessages.h"
#include <strsafe.h>

namespace Helper
{
	namespace Registry
	{
		// Reads a string value from any registry key
		LONG GetStringRegKey(HKEY hKey, LPCWSTR sValueName, PWSTR *sValue)
		{
			*sValue = L"";
			WCHAR szBuffer[512];
			DWORD dwBufferSize = sizeof(szBuffer);
			ULONG nError;
			nError = RegQueryValueExW(hKey, sValueName, 0, NULL, (LPBYTE)szBuffer, &dwBufferSize);
			if (ERROR_SUCCESS == nError)
				SHStrDupW(szBuffer, sValue);
			return nError;
		}

		// Reads a string value from the CrySIL U2F registry key, e.g. the Public Key of the user
		BOOL GetRegValue(LPCWSTR sValueName, PWSTR *sValue, PWSTR sUserSid)
		{
			HKEY hKey = 0;
			std::wstringstream ss;
			ss << CRYSIL_REG_KEY << sUserSid;
			LONG lRes = RegOpenKeyExW(HKEY_LOCAL_MACHINE, ss.str().c_str(), 0, KEY_READ, &hKey);
			if (!(lRes == ERROR_SUCCESS))
				return FALSE;
			if (!(GetStringRegKey(hKey, sValueName, sValue) == ERROR_SUCCESS))
				return FALSE;
			return TRUE;
		}
	}
	namespace String
	{
		// Converts a PWSTR to a std::string
		std::string wstr2str(PCWSTR s)
		{
			std::wstring wstr = std::wstring(s);
			return std::string(wstr.begin(), wstr.end());
		}

		// Converts a PWSTR to a std::string
		BOOL str2wstr(std::string s, PWSTR* val)
		{
			std::wstring wstr = std::wstring(s.begin(), s.end());
			return SHStrDupW(wstr.c_str(), val);
		}

		// Replaces a single char with another
		void Replace(TCHAR *haystack, WCHAR needle, WCHAR replacement)
		{
			while ((haystack = _tcschr(haystack, needle)) != NULL)
				*haystack = replacement;
		}

		// Decodes a base64 url-encoded string ('_', '-') into a basic base64 encoded string ('/', '+')
		BOOL Base64UrlDecode(TCHAR* cs)
		{
			Replace(cs, _T('_'), _T('/'));
			Replace(cs, _T('-'), _T('+'));
			return TRUE;
		}

		// Encodes a basic base64 encoded string ('/', '+') into an url-encoded string ('_', '-')
		BOOL Base64UrlEncode(TCHAR* cs)
		{
			Replace(cs, _T('/'), _T('_'));
			Replace(cs, _T('+'), _T('-'));
			return StrTrim(cs, _T("\r\n="));
		}

		// Prints the contents of the buffer in hex chars
		void PrintBuffer(BYTE* buf, const DWORD len)
		{
			printf("\n");
			for (DWORD i = 0; i < len; i++)
			{
				printf("%02x", buf[i]);
			}
			printf("\n");
		}

		// Encodes a base64 encoded string into a hex representation (01:02:AA...)
		BOOL HexEncode(PWSTR* val)
		{
			if (!Base64UrlDecode(*val))
				return false;
			DWORD lBytes = 0;
			PBYTE pBytes = 0;
			if (!Crypto::Base64Decode(*val, &pBytes, &lBytes))
				return false;

			*val = L"";
			WCHAR szBuffer[256];
			DWORD i, j = 0;
			for (i = 0; i < lBytes; i++)
			{
				if (i > 0)
				{
					swprintf(szBuffer + j, 256, L"%s", L":");
					j += 1;
				}
				swprintf(szBuffer + j, 256, L"%02X", pBytes[i]);
				j += 2;
			}
			SHStrDupW(szBuffer, val);
			return true;
		}
	}
	namespace Crypto
	{
		// Encodes the input byte array into a base64 encoded string
		BOOL Base64Encode(PBYTE pInput, DWORD lInput, std::string* sOutput)
		{
			DWORD dwStringSize = lInput * 2;
			TCHAR *wOutputTemp = new TCHAR[dwStringSize];
			if (!CryptBinaryToString(pInput, lInput, CRYPT_STRING_BASE64, wOutputTemp, &dwStringSize))
				return FALSE;
			Helper::String::Base64UrlEncode(wOutputTemp);
			std::wstring wOutput(wOutputTemp);
			*sOutput = std::string(wOutput.begin(), wOutput.end());
			delete[] wOutputTemp;
			return TRUE;
		}

		// Decodes a base64 string into a byte array
		BOOL Base64Decode(PWSTR sInput, PBYTE* pOutput, DWORD* lOutput)
		{
			String::Base64UrlDecode(sInput);
			if (!CryptStringToBinary(sInput, (DWORD)_tcslen(sInput), CRYPT_STRING_BASE64_ANY, nullptr, lOutput, nullptr, nullptr))
				return FALSE;
			*pOutput = new BYTE[*lOutput];
			ZeroMemory(*pOutput, *lOutput);
			CryptStringToBinary(sInput, (DWORD)_tcslen(sInput), CRYPT_STRING_BASE64_ANY, *pOutput, lOutput, nullptr, nullptr);
			return TRUE;
		}

		// Generates a random 32 byte challenge as a base64-url-encoded string
		BOOL CreateRandomChallenge(PWSTR *challenge)
		{
			HCRYPTPROV hProvider = 0;
			if (!::CryptAcquireContextW(&hProvider, 0, 0, PROV_RSA_FULL, CRYPT_VERIFYCONTEXT | CRYPT_SILENT))
				return FALSE;
			DWORD dwSize = 32;
			PBYTE pByte = new BYTE[dwSize];
			ZeroMemory(pByte, dwSize);
			if (!::CryptGenRandom(hProvider, dwSize, pByte))
			{
				::CryptReleaseContext(hProvider, 0);
				return FALSE;
			}
			DWORD dwStringSize = 64;
			TCHAR *sString = new TCHAR[dwStringSize];
			BOOL hr = CryptBinaryToString(pByte, dwSize, CRYPT_STRING_BASE64, sString, &dwStringSize);
			if (hr)
				hr = String::Base64UrlEncode(sString);
			if (hr)
				SHStrDupW(sString, challenge);
			delete[] pByte;
			delete[] sString;
			return hr;
		}

		// Verifies a ECDSA P256 signature given the hashed input, signature and public key
		// https://msdn.microsoft.com/en-us/library/windows/desktop/aa376304%28v=vs.85%29.aspx
		BOOL VerifySignature(PBYTE hashBuf, DWORD hashLen, PBYTE sigBuf, DWORD sigLen, PBYTE keyBuf, DWORD keyLen)
		{
			UNREFERENCED_PARAMETER(keyLen);
			BCRYPT_KEY_HANDLE hKey = NULL;
			BCRYPT_ALG_HANDLE hSignAlg = NULL;
			BYTE keyType[] = { 0x45, 0x43, 0x53, 0x31 };
			BYTE keyLength[] = { 0x20, 0x00, 0x00, 0x00 };
			PBYTE keyBlob = new BYTE[72];
			PBYTE sigBlob = new BYTE[64];
			NTSTATUS status = STATUS_UNSUCCESSFUL;
			BOOL result = false;

			if (!NT_SUCCESS(status = BCryptOpenAlgorithmProvider(&hSignAlg, BCRYPT_ECDSA_P256_ALGORITHM, NULL, 0)))
			{
				DebugPrintLn("Error returned by BCryptOpenAlgorithmProvider");
				DebugPrintLn(status);
				goto Cleanup;
			}

			// Converts the public key from U2F into a format accepted by bcrypt
			ZeroMemory(keyBlob, 72);
			memcpy(keyBlob, &keyType[0], 4 * sizeof(keyType));
			memcpy(keyBlob + 4, &keyLength[0], 4 * sizeof(keyLength));
			memcpy(keyBlob + 8, keyBuf + 1, 64 * sizeof(*keyBuf));

			if (!NT_SUCCESS(status = BCryptImportKeyPair(hSignAlg, NULL, BCRYPT_ECCPUBLIC_BLOB, &hKey, keyBlob, 72, 0)))
			{
				DebugPrintLn("Error returned by BCryptImportKeyPair");
				DebugPrintLn(status);
				goto Cleanup;
			}

			// Converts the signature from U2F into a format accepted by bcrypt
			// This essentially strips the ASN.1 data structure headers
			ZeroMemory(sigBlob, 64);
			if (sigLen == 64)
			{
				memcpy(sigBlob, sigBuf, 64 * sizeof(*sigBuf));
			}
			else {
				if (sigBuf[3] == 0x20)
				{
					memcpy(sigBlob, sigBuf + 4, 32 * sizeof(*sigBuf));
					if (sigBuf[37] == 0x20)
						memcpy(sigBlob + 32, sigBuf + 38, 32 * sizeof(*sigBuf));
					else if (sigBuf[37] == 0x21)
						memcpy(sigBlob + 32, sigBuf + 39, 32 * sizeof(*sigBuf));
				}
				else if (sigBuf[3] == 0x21)
				{
					memcpy(sigBlob, sigBuf + 5, 32 * sizeof(*sigBuf));
					if (sigBuf[38] == 0x20)
						memcpy(sigBlob + 32, sigBuf + 39, 32 * sizeof(*sigBuf));
					else if (sigBuf[38] == 0x21)
						memcpy(sigBlob + 32, sigBuf + 40, 32 * sizeof(*sigBuf));
				}
			}

			if (!NT_SUCCESS(status = BCryptVerifySignature(hKey, NULL, hashBuf, hashLen, sigBlob, 64, 0)))
			{
				DebugPrintLn("Error returned by BCryptVerifySignature");
				DebugPrintLn(status);
				goto Cleanup;
			}
			result = true;

		Cleanup:
			if (hSignAlg)
				BCryptCloseAlgorithmProvider(hSignAlg, 0);
			if (hKey)
				BCryptDestroyKey(hKey);
			if (keyBlob)
				delete[] keyBlob;
			if (sigBlob)
				delete[] sigBlob;

			return result;
		}

		// Calculates the SHA256 hash of a given buffer
		// https://msdn.microsoft.com/en-us/library/windows/desktop/aa376217%28v=vs.85%29.aspx
		PBYTE CalculateHash(BYTE* buf, const DWORD len)
		{
			BCRYPT_ALG_HANDLE hAlg = NULL;
			BCRYPT_HASH_HANDLE hHash = NULL;
			NTSTATUS status = STATUS_UNSUCCESSFUL;
			DWORD cbData = 0, cbHash = 0, cbHashObject = 0;
			PBYTE pbHashObject = NULL;
			PBYTE pbHash = NULL;

			if (!NT_SUCCESS(status = BCryptOpenAlgorithmProvider(&hAlg, BCRYPT_SHA256_ALGORITHM, NULL, 0)))
			{
				DebugPrintLn("Error returned by BCryptOpenAlgorithmProvider");
				DebugPrintLn(status);
				goto Cleanup;
			}

			if (!NT_SUCCESS(status = BCryptGetProperty(hAlg, BCRYPT_OBJECT_LENGTH, (PBYTE)&cbHashObject, sizeof(DWORD), &cbData, 0)))
			{
				DebugPrintLn("Error returned by BCryptGetProperty");
				DebugPrintLn(status);
				goto Cleanup;
			}

			pbHashObject = (PBYTE)HeapAlloc(GetProcessHeap(), 0, cbHashObject);
			if (NULL == pbHashObject)
			{
				DebugPrintLn("memory allocation failed\n");
				goto Cleanup;
			}

			if (!NT_SUCCESS(status = BCryptGetProperty(hAlg, BCRYPT_HASH_LENGTH, (PBYTE)&cbHash, sizeof(DWORD), &cbData, 0)))
			{
				DebugPrintLn("Error returned by BCryptGetProperty");
				DebugPrintLn(status);
				goto Cleanup;
			}

			pbHash = (PBYTE)HeapAlloc(GetProcessHeap(), 0, cbHash);
			if (NULL == pbHash)
			{
				DebugPrintLn("memory allocation failed");
				goto Cleanup;
			}

			if (!NT_SUCCESS(status = BCryptCreateHash(hAlg, &hHash, pbHashObject, cbHashObject, NULL, 0, 0)))
			{
				DebugPrintLn("Error returned by BCryptCreateHash");
				DebugPrintLn(status);
				goto Cleanup;
			}

			if (!NT_SUCCESS(status = BCryptHashData(hHash, (PBYTE)buf, len, 0)))
			{
				DebugPrintLn("Error returned by BCryptHashData");
				DebugPrintLn(status);
				goto Cleanup;
			}

			if (!NT_SUCCESS(status = BCryptFinishHash(hHash, pbHash, cbHash, 0)))
			{
				DebugPrintLn("Error returned by BCryptFinishHash");
				DebugPrintLn(status);
				goto Cleanup;
			}

		Cleanup:
			if (hAlg)
				BCryptCloseAlgorithmProvider(hAlg, 0);
			if (hHash)
				BCryptDestroyHash(hHash);
			if (pbHashObject)
				HeapFree(GetProcessHeap(), 0, pbHashObject);

			return pbHash;
		}
	}
	namespace CrySIL
	{
		// Handles conversion of a U2F message from the crypto token extension to CrySIL commands
		std::string U2FReceiverHandler(std::string msg, PWSTR userSid, PinHandler* pinHandler)
		{
			CrySilForwarder crySilForwarder{ userSid, pinHandler };
			AuthenticateInternalHandler authInternalHandler = AuthenticateInternalHandler(crySilForwarder);
			AuthenticateExternalHandler authExternalHandler = AuthenticateExternalHandler(authInternalHandler);
			if (msg.find("helper_request") != std::string::npos && msg.find("sign_helper_request") != std::string::npos)
			{
				if (msg.find("challengeHash") != std::string::npos)
					return (AuthenticateMultipleHandler(authInternalHandler)).Handle(msg);
				else
					return (AuthenticateMultipleHandler(authExternalHandler)).Handle(msg);
			}
			else if (msg.find("keyHandle") != std::string::npos)
			{
				if (msg.find("challengeHash") != std::string::npos)
					return authInternalHandler.Handle(msg);
				else
					return authExternalHandler.Handle(msg);
			}
			return{};
		}

		// Verifies the response from the U2F client
		BOOL HandleU2FResponse(Json::Value responseJson, PCWSTR requestChallenge, PCWSTR requestKeyHandle, PCWSTR requestAppId, PWSTR sPublicKey)
		{
			PWSTR sChallenge = L"";
			PWSTR sKeyHandle = L"";
			PWSTR sClientData = L"";
			PWSTR sSignature = L"";
			String::str2wstr(responseJson.get("challenge", 0).asString(), &sChallenge);
			String::str2wstr(responseJson.get("keyHandle", 0).asString(), &sKeyHandle);
			String::str2wstr(responseJson.get("clientData", 0).asString(), &sClientData);
			String::str2wstr(responseJson.get("signatureData", 0).asString(), &sSignature);

			if (wcscmp(sChallenge, requestChallenge) != 0)
				return false;
			if (wcscmp(sKeyHandle, requestKeyHandle) != 0)
				return false;

			DWORD lClientData = 0;
			PBYTE pClientData = 0;
			if (!Crypto::Base64Decode(sClientData, &pClientData, &lClientData))
				return false;

			DWORD lPublicKey = 0;
			PBYTE pPublicKey = 0;
			if (!Crypto::Base64Decode(sPublicKey, &pPublicKey, &lPublicKey))
				return false;

			DWORD lSignature = 0;
			PBYTE pSignature = 0;
			if (!Crypto::Base64Decode(sSignature, &pSignature, &lSignature))
				return false;
			if (lSignature < 70)
				return false;

			std::string sRequestAppId = String::wstr2str(requestAppId); // need a copy to get the correct bytes
			PBYTE pAppIdBase = new BYTE[sRequestAppId.length()];
			ZeroMemory(pAppIdBase, sRequestAppId.length());
			memcpy(pAppIdBase, sRequestAppId.c_str(), sRequestAppId.length() * sizeof(char));
			PBYTE pAppIdHash = Crypto::CalculateHash(pAppIdBase, (DWORD)sRequestAppId.length());

			PBYTE pClientDataHash = Crypto::CalculateHash(pClientData, lClientData);

			std::string challengeHash = "";
			if (!Helper::Crypto::Base64Encode(pClientDataHash, 32, &challengeHash))
				return{};

			DWORD lVerifyBytes = 69;
			PBYTE pVerifyBytes = new BYTE[lVerifyBytes];
			ZeroMemory(pVerifyBytes, lVerifyBytes);

			memcpy(pVerifyBytes, pAppIdHash, 32 * sizeof(*pAppIdHash));                // appIdHash
			memcpy(pVerifyBytes + 32, pSignature, 1 * sizeof(*pSignature));            // user presence
			memcpy(pVerifyBytes + 33, pSignature + 1, 4 * sizeof(*pSignature));        // counter
			memcpy(pVerifyBytes + 37, pClientDataHash, 32 * sizeof(*pClientDataHash)); // challengeHash

			PBYTE pVerifyBytesHashed = Crypto::CalculateHash(pVerifyBytes, lVerifyBytes);
			BOOL result = false;
			if (Crypto::VerifySignature(pVerifyBytesHashed, 32, pSignature + 5, lSignature - 5, pPublicKey, lPublicKey))
				result = true;

			delete[] pPublicKey;
			delete[] pAppIdBase;
			delete[] pSignature;
			delete[] pClientData;
			delete[] pVerifyBytes;

			return result;
		}

		// Performs a U2F request, by calling U2FReceiverHandler
		BOOL PerformU2FRequest(PWSTR userSid, PinHandler *pinHandler)
		{
			PWSTR sKeyHandle = L"";
			PWSTR sPublicKey = L"";
			PWSTR sChallenge = L"";
			PWSTR sAppId = CRYSIL_APP_ID;

			if (!Crypto::CreateRandomChallenge(&sChallenge))
				return false;
			if (!Registry::GetRegValue(L"KeyHandle", &sKeyHandle, userSid))
				return false;
			if (!Registry::GetRegValue(L"PublicKey", &sPublicKey, userSid))
				return false;

			Json::Value jsonRequest;
			jsonRequest["challenge"] = String::wstr2str(sChallenge);
			jsonRequest["version"] = CRYSIL_U2F_VERSION;
			jsonRequest["keyHandle"] = String::wstr2str(sKeyHandle);
			jsonRequest["appId"] = String::wstr2str(sAppId);
			Json::FastWriter fastWriter;
			std::string sRequest = fastWriter.write(jsonRequest);
			std::string sResponse = U2FReceiverHandler(sRequest, userSid, pinHandler);
			DebugPrintLn("Got a response");
			DebugPrintLn(sResponse.c_str());
			Json::Value jResponse;
			Json::Reader reader;
			if (!reader.parse(sResponse, jResponse))
				return false;

			return HandleU2FResponse(jResponse, sChallenge, sKeyHandle, sAppId, sPublicKey);
		}
	}
	namespace Debug
	{
		void PrintLn(const char *message, char *file, int line)
		{
			INIT_ZERO_CHAR(date_time, MAX_TIME_SIZE);
			GetCurrentTimeAndDate(date_time);
			WriteLogFile(date_time);

			char code[1024];
			sprintf_s(code, sizeof(code), "%d", line);

			OutputDebugStringA(message);
			WriteLogFile(message);
			OutputDebugStringA(" [at line ");
			WriteLogFile(" [at line ");
			OutputDebugStringA(code);
			WriteLogFile(code);
			OutputDebugStringA(" in '");
			WriteLogFile(" in '");
			OutputDebugStringA(file);
			WriteLogFile(file);
			OutputDebugStringA("']\n");
			WriteLogFile("']\n");
		}

		void PrintLn(int integer, char *file, int line)
		{
			INIT_ZERO_CHAR(date_time, MAX_TIME_SIZE);
			GetCurrentTimeAndDate(date_time);
			WriteLogFile(date_time);

			char code[1024];
			sprintf_s(code, sizeof(code), "Integer: %d (0x%X)", integer, integer);

			OutputDebugStringA(code);
			WriteLogFile(code);
			OutputDebugStringA(" [at line ");
			WriteLogFile(" [at line ");

			sprintf_s(code, sizeof(code), "%d", line);

			OutputDebugStringA(code);
			WriteLogFile(code);
			OutputDebugStringA(" in '");
			WriteLogFile(" in '");
			OutputDebugStringA(file);
			WriteLogFile(file);
			OutputDebugStringA("']\n");
			WriteLogFile("']\n");
		}

		void WriteLogFile(const char* szString)
		{
			FILE* pFile;
			if (fopen_s(&pFile, LOGFILE_NAME, "a") == 0)
			{
				fprintf(pFile, "%s", szString);
				fclose(pFile);
			}
		}

		void GetCurrentTimeAndDate(char(&time)[MAX_TIME_SIZE])
		{
			SYSTEMTIME st;
			GetSystemTime(&st);
			sprintf_s(time, ARRAYSIZE(time), "[%02d.%02d.%04d %02d:%02d:%02d.%04d]: ", st.wDay, st.wMonth, st.wYear, st.wHour, st.wMinute, st.wSecond, st.wMilliseconds);
		}
	}
}
