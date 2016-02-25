#include "CrySilMessages.h"
#include "helper.h"
#include <iostream>
#include <winuser.h>
#include "CrySilU2FCredential.h"
#include "Dll.h"

std::string PinHandler::ReadPin()
{
	return{};
}

void PinHandler::SetLabel(std::string label)
{
	this->label = label;
}

// Reads the SecretAuth for CrySIL from the user on the console
std::string ConsolePinHandler::ReadPin()
{
	std::cout << this->label;
	HANDLE hStdin = GetStdHandle(STD_INPUT_HANDLE);
	DWORD mode = 0;
	GetConsoleMode(hStdin, &mode);
	SetConsoleMode(hStdin, mode & (~ENABLE_ECHO_INPUT));
	std::string pin;
	std::getline(std::cin, pin);
	std::cout << std::endl;
	return pin;
}

// Displays a dialog to the user to enter the SecretAuth for CrySIL
// Handles dialog creation and stuff
INT_PTR CALLBACK WindowsPinHandler::PasswordProc(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam)
{
	UNREFERENCED_PARAMETER(lParam);
	WORD cchPassword;
	CHAR* lpszPassword;
	switch (message)
	{
	case WM_INITDIALOG:
		SendDlgItemMessage(hDlg, IDC_EDIT1, EM_SETPASSWORDCHAR, (WPARAM) '*', (LPARAM)0);
		SendMessage(hDlg, DM_SETDEFID, (WPARAM)IDOK, (LPARAM)0);
		SetWindowTextA(GetDlgItem(hDlg, IDC_STATIC), this->label.c_str());
		SetFocus(GetDlgItem(hDlg, IDC_EDIT1));
		return TRUE;

	case WM_COMMAND:
		// Set the default push button to "OK" when the user enters text. 
		if (HIWORD(wParam) == EN_CHANGE && LOWORD(wParam) == IDC_EDIT1)
		{
			SendMessage(hDlg, DM_SETDEFID, (WPARAM)IDOK, (LPARAM)0);
		}
		switch (wParam)
		{
		case IDOK:
			// Get number of characters.	
			DebugPrintLn("message==WM_COMMAND, wParam==IDOK");
			cchPassword = GetWindowTextLength(GetDlgItem(hDlg, IDC_EDIT1)) + 1;
			DebugPrintLn(cchPassword);
			if (cchPassword == 0)
			{
				MessageBox(hDlg, L"No characters entered.", L"Error", MB_OK);
				EndDialog(hDlg, TRUE);
				return FALSE;
			}
			lpszPassword = new CHAR[cchPassword];
			// Get the characters. 
			GetDlgItemTextA(hDlg, IDC_EDIT1, lpszPassword, cchPassword);
			// Null-terminate the string. 
			lpszPassword[cchPassword] = 0;
			this->password = &(lpszPassword[0]);
			DebugPrintLn("Got a PIN");
			DebugPrintLn(this->password.c_str());
			EndDialog(hDlg, TRUE);
			return TRUE;

		case IDCANCEL:
			EndDialog(hDlg, TRUE);
			return TRUE;
		}
		return 0;
	}
	return FALSE;
}

// Displays a dialog to the user to enter the SecretAuth for CrySIL
std::string WindowsPinHandler::ReadPin()
{
	DebugPrintLn("WindowsPinHandler::ReadPin");
	HINSTANCE hInst = g_hinst;
	if (DialogBoxParam(hInst, MAKEINTRESOURCE(IDD_DIALOG1), hwndOwner, WindowsPinHandler::AppDlgProc, (LPARAM)this) == IDOK)
	{
		DebugPrintLn("returning");
		DebugPrintLn(this->password.c_str());
		return this->password;
	}
	return{};
}

// Dialog anchor
void WindowsPinHandler::setHwndOwner(HWND owner)
{
	this->hwndOwner = owner;
}

CrySilForwarder::CrySilForwarder(PWSTR _userSid, PinHandler *pinHandler)
{
	this->pinHandler = pinHandler;
	this->userSid = _userSid;
	this->header["type"] = "standardHeader";
	this->header["commandId"] = "";
	this->header["sessionId"] = "";
	this->header["path"] = Json::arrayValue;
	this->header["protocolVersion"] = "2.0";
}

CrySilForwarder::~CrySilForwarder()
{
}

// Decodes a basic base64 encoded string ('/', '+') into a base64 url-encoded string ('_', '-') into a basic
std::string UrlSafe(std::string base64)
{
	if (base64.empty())
		return base64;
	std::string s(base64.begin(), base64.end());
	std::replace(s.begin(), s.end(), '+', '-');
	std::replace(s.begin(), s.end(), '/', '_');
	s.erase(std::remove(s.begin(), s.end(), '\n'), s.end());
	s.erase(std::remove(s.begin(), s.end(), '\r'), s.end());
	s.erase(std::remove(s.begin(), s.end(), '='), s.end());
	return s;
}

// Decodes a base64 url-encoded string ('_', '-') into a basic base64 encoded string ('/', '+')
std::string DeUrlSafe(std::string base64)
{
	if (base64.empty())
		return base64;
	std::string s(base64.begin(), base64.end());
	std::replace(s.begin(), s.end(), '-', '+');
	std::replace(s.begin(), s.end(), '_', '/');
	s.erase(std::remove(s.begin(), s.end(), '\r'), s.end());
	s.erase(std::remove(s.begin(), s.end(), '\n'), s.end());
	while (s.length() % 4 != 0)
		s.append("=");
	return s;
}

// Sends a "generateU2FKeyRequest" to get the wrapped key for CrySIL from the key handle from U2F
std::string CrySilForwarder::ExecuteGenerateWrappedKey(std::string clientParam, std::string appParam, std::string encodedRandom)
{
	DebugPrintLn("CrySilForwarder::ExecuteGenerateWrappedKey");
	this->header["type"] = "standardHeader";
	this->header.removeMember("counter");
	Json::Value payload;
	payload["type"] = "generateU2FKeyRequest";
	payload["certificateSubject"] = "CN=CrySIL";
	if (!appParam.empty())
		payload["appParam"] = DeUrlSafe(appParam);
	if (!clientParam.empty())
		payload["clientParam"] = DeUrlSafe(clientParam);
	if (!encodedRandom.empty())
		payload["encodedRandom"] = DeUrlSafe(encodedRandom);
	return this->Send(payload);
}

// Sends a new "signRequest" to actually sign the U2F challenge with the wrapped key
std::string CrySilForwarder::ExecuteSignatureRequest(std::string keyEncoded, std::string hashToBeSigned, bool addCounter)
{
	DebugPrintLn("CrySilForwarder::ExecuteSignatureRequest");
	this->header["type"] = "standardHeader";
	this->header.removeMember("counter");
	if (addCounter)
	{
		this->header["type"] = "u2fHeader";
		this->header["counter"] = 0;
	}
	Json::Value payload;
	payload["type"] = "signRequest";
	payload["algorithm"] = "SHA256withECDSA";
	payload["hashesToBeSigned"] = Json::arrayValue;
	payload["hashesToBeSigned"].append(DeUrlSafe(hashToBeSigned));
	Json::Value signatureKey;
	signatureKey["type"] = "wrappedKey";
	signatureKey["encodedWrappedKey"] = keyEncoded;
	payload["signatureKey"] = signatureKey;
	return this->Send(payload);
}

// Sends the Authentication response for type "SecretAuthInfo"
std::string CrySilForwarder::ExecuteAuthentication(std::string commandId, std::string secretValue)
{
	DebugPrintLn("CrySilForwarder::ExecuteAuthentication");
	this->header["type"] = "standardHeader";
	this->header.removeMember("counter");
	this->header["commandId"] = commandId;
	Json::Value payload;
	payload["type"] = "authChallengeResponse";
	Json::Value authInfo;
	authInfo["type"] = "SecretAuthInfo";
	authInfo["secret"] = secretValue;
	payload["authInfo"] = authInfo;
	return this->Send(payload);
}

// Sends the Authentication response for type "SecretDoubleAuthInfo"
std::string CrySilForwarder::ExecuteDoubleAuthentication(std::string commandId, std::string secretValue1, std::string secretValue2)
{
	DebugPrintLn("CrySilForwarder::ExecuteDoubleAuthentication");
	this->header["type"] = "standardHeader";
	this->header.removeMember("counter");
	this->header["commandId"] = commandId;
	Json::Value payload;
	payload["type"] = "authChallengeResponse";
	Json::Value authInfo;
	authInfo["type"] = "SecretDoubleAuthInfo";
	authInfo["secret1"] = secretValue1;
	authInfo["secret2"] = secretValue2;
	payload["authInfo"] = authInfo;
	return this->Send(payload);
}

// Sends the payload as a CrySIL command to the server in JSON
// Server URL is read from registry
std::string CrySilForwarder::Send(Json::Value payload)
{
	DebugPrintLn("In CrySilForwarder::Send");
	PWSTR sHost = L"";
	PWSTR sPort = L"";
	PWSTR sUrl = L"";

	if (!Helper::Registry::GetRegValue(L"Host", &sHost, userSid))
		return{};
	if (!Helper::Registry::GetRegValue(L"Port", &sPort, userSid))
		return{};
	if (!Helper::Registry::GetRegValue(L"URL", &sUrl, userSid))
		return{};

	Json::Value request;
	request["header"] = header;
	request["payload"] = payload;
	Json::FastWriter fastWriter;
	std::string sRequest = fastWriter.write(request);
	DebugPrintLn(sRequest.c_str());

	HINTERNET hSession = NULL, hConnect = NULL, hRequest = NULL;
	BOOL bResults = FALSE;
	BOOL retry = FALSE;
	DWORD dwSize = 0;
	DWORD dwDownloaded = 0;
	LPSTR pszOutBuffer;
	std::string sResponse;

	DebugPrintLn("Calling WinHttpOpen");
	hSession = WinHttpOpen(L"WinHTTP Example/1.0", WINHTTP_ACCESS_TYPE_NO_PROXY, WINHTTP_NO_PROXY_NAME, WINHTTP_NO_PROXY_BYPASS, 0);
	if (hSession)
	{
		DebugPrintLn("Calling WinHttpConnect");
		WinHttpSetTimeouts(hSession, 10000, 10000, 10000, 10000);
		INTERNET_PORT port = (INTERNET_PORT)std::stoi(sPort);
		hConnect = WinHttpConnect(hSession, sHost, port, 0);
	}
	DebugPrintLn("Calling WinHttpOpenRequest");
	if (hConnect)
		hRequest = WinHttpOpenRequest(hConnect, L"POST", sUrl, NULL, WINHTTP_NO_REFERER, WINHTTP_DEFAULT_ACCEPT_TYPES, WINHTTP_FLAG_SECURE);
	//hRequest = WinHttpOpenRequest(hConnect, L"POST", sUrl, NULL, WINHTTP_NO_REFERER, WINHTTP_DEFAULT_ACCEPT_TYPES, 0);
	if (hRequest)
	{
		DebugPrintLn("Calling WinHttpSetOption");
		WinHttpSetOption(hRequest, WINHTTP_OPTION_CLIENT_CERT_CONTEXT, WINHTTP_NO_CLIENT_CERT_CONTEXT, 0);
		bResults = WinHttpAddRequestHeaders(hRequest, L"Content-Type: application/json", (ULONG)-1L, WINHTTP_ADDREQ_FLAG_ADD);
	}
	if (hRequest)
	{
		DebugPrintLn("Calling retry loop");
		do {
			retry = false;
			// FIXME: Every TLS certificate error is suppressed
			DebugPrintLn("Calling WinHttpSendRequest");
			bResults = WinHttpSendRequest(hRequest, WINHTTP_NO_ADDITIONAL_HEADERS, 0, (LPVOID)sRequest.c_str(), (DWORD)sRequest.length(), (DWORD)sRequest.length(), 0);
			if (!bResults) {
				DWORD result = GetLastError();
				if (result == ERROR_WINHTTP_SECURE_FAILURE)
				{
					DWORD dwFlags =
						SECURITY_FLAG_IGNORE_UNKNOWN_CA |
						SECURITY_FLAG_IGNORE_CERT_WRONG_USAGE |
						SECURITY_FLAG_IGNORE_CERT_CN_INVALID |
						SECURITY_FLAG_IGNORE_CERT_DATE_INVALID;
					if (WinHttpSetOption(hRequest, WINHTTP_OPTION_SECURITY_FLAGS, &dwFlags, sizeof(dwFlags)))
						retry = true;
				}
			}
		} while (retry);
	}
	DebugPrintLn("Calling WinHttpReceiveResponse");
	if (bResults)
		bResults = WinHttpReceiveResponse(hRequest, NULL);
	if (bResults)
	{
		do
		{
			dwSize = 0;
			if (!WinHttpQueryDataAvailable(hRequest, &dwSize))
			{
				DebugPrintLn("Error in WinHttpQueryDataAvailable");
				DebugPrintLn(GetLastError());
				break;
			}
			if (!dwSize)
				break;
			pszOutBuffer = new char[dwSize + 1];
			if (!pszOutBuffer)
			{
				DebugPrintLn("Out of memory");
				break;
			}
			ZeroMemory(pszOutBuffer, dwSize + 1);
			if (WinHttpReadData(hRequest, (LPVOID)pszOutBuffer, dwSize, &dwDownloaded))
			{
				if (hRequest) WinHttpCloseHandle(hRequest);
				if (hConnect) WinHttpCloseHandle(hConnect);
				if (hSession) WinHttpCloseHandle(hSession);
				sResponse = this->CheckForAuth(pszOutBuffer);
				delete[] pszOutBuffer;
				break;
			}
			else
			{
				DebugPrintLn("Error in WinHttpReadData");
				DebugPrintLn(GetLastError());
			}
			delete[] pszOutBuffer;
			if (!dwDownloaded)
				break;
		} while (dwSize > 0);
	}
	else
	{
		DebugPrintLn("Error in WinHttpQueryDataAvailable");
		DebugPrintLn(GetLastError());
	}

	if (hRequest) WinHttpCloseHandle(hRequest);
	if (hConnect) WinHttpCloseHandle(hConnect);
	if (hSession) WinHttpCloseHandle(hSession);

	DebugPrintLn("CrySilForwarder::Send is returning");
	DebugPrintLn(sResponse.c_str());

	return sResponse;
}

// Checks whether the response from CrySIL requires authentication (SecretAuthType) from the user
std::string CrySilForwarder::CheckForAuth(std::string responseStr)
{
	if (responseStr.empty() || responseStr == "null")
		return responseStr;;
	DebugPrintLn("CrySilForwarder::CheckForAuth");
	DebugPrintLn(responseStr.c_str());
	Json::Value response;
	Json::Reader reader;
	Json::FastWriter fastWriter;
	if (!reader.parse(responseStr, response))
		return responseStr;

	std::string commandId = response["header"]["commandId"].asString();
	Json::Value payload = response["payload"];
	if (payload["type"].asString() == "authChallengeRequest")
	{
		std::string authTypeFound = "";
		for (auto itr : payload["authTypes"]) {
			if (itr["type"].asString() == "SecretAuthType" || itr["type"].asString() == "SecretDoubleAuthType")
			{
				authTypeFound = itr["type"].asString();
				break;
			}
		}
		if (authTypeFound == "")
			return responseStr;
		if (authTypeFound == "SecretAuthType")
		{
			this->pinHandler->SetLabel("Please enter your secret PIN to authenticate against CrySIL");
			std::string pin = this->pinHandler->ReadPin();
			DebugPrintLn("CheckForAuth is calling ExecuteAuthentication with PIN");
			DebugPrintLn(pin.c_str());
			return this->ExecuteAuthentication(commandId, pin);
		}
		if (authTypeFound == "SecretDoubleAuthType")
		{
			this->pinHandler->SetLabel("Please enter your signature PIN to authenticate against CrySIL");
			std::string pin1 = this->pinHandler->ReadPin();
			this->pinHandler->SetLabel("Please enter your card PIN to authenticate against CrySIL");
			std::string pin2 = this->pinHandler->ReadPin();
			DebugPrintLn("CheckForAuth is calling ExecuteDoubleAuthentication with PINs");
			DebugPrintLn(pin1.c_str());
			DebugPrintLn(pin2.c_str());
			return this->ExecuteDoubleAuthentication(commandId, pin1, pin2);
		}
	}
	DebugPrintLn("CheckForAuth is returning same old responseStr");
	return responseStr;
}

std::string Handler::Handle(std::string message)
{
	return{};
}

// Takes a U2F authenticate command in form of{ type, signData = [{ appIdHash, challengeHash, keyHandle, version }] },
// performs some data transformation(no crypto operations involved)
// and passes the command on in the form of{ appIdHash, challengeHash, keyHandle, version }
// @returns {string} in form of{ responses = [{ type, code, responseData = { version, challengeHash, appIdHash, keyHandle, signatureData } }] }
// @see AuthenticateInternalHandler
std::string AuthenticateMultipleHandler::Handle(std::string message)
{
	Json::Value u2fRequest;
	Json::Reader reader;
	Json::FastWriter fastWriter;
	if (!reader.parse(message, u2fRequest))
		return{};

	Json::Value response;
	response["responses"] = Json::arrayValue;
	for (auto itr : u2fRequest["signData"]) {
		std::string request = fastWriter.write(itr);
		std::string responseStr = this->handler.Handle(request);
		std::string signatureData = "";
		uint8_t code = 4;
		if (!responseStr.empty())
		{
			code = 0;
			Json::Value innerResponse;
			reader.parse(responseStr, innerResponse);
			signatureData = innerResponse["signatureData"].asString();
		}
		Json::Value singleResponse;
		singleResponse["type"] = "sign_helper_reply";
		singleResponse["code"] = code;
		Json::Value responseData;
		responseData["version"] = "U2F_V2";
		responseData["challengeHash"] = itr["challengeHash"];
		responseData["appIdHash"] = itr["appIdHash"];
		responseData["keyHandle"] = itr["keyHandle"];
		responseData["signatureData"] = signatureData;
		singleResponse["responseData"] = responseData;
		response["responses"].append(singleResponse);
	}
	std::string responseStr = fastWriter.write(response);
	return responseStr;
}


// Takes a U2F authenticate command in form of { appId, challenge, keyHandle, version },
// performs some hashing operations
// and passes the command on in the form of { appIdHash, challengeHash, keyHandle, version }
// @returns {string} in form of { challenge, clientData, keyHandle, signatureData }
// @see AuthenticateInternalHandler
std::string AuthenticateExternalHandler::Handle(std::string message)
{
	Json::Value u2fRequest;
	Json::Reader reader;
	Json::FastWriter fastWriter;
	if (!reader.parse(message, u2fRequest))
		return{};
	std::string appId = u2fRequest["appId"].asString();
	std::string version = u2fRequest["version"].asString();
	std::string challenge = u2fRequest["challenge"].asString();
	std::string keyHandle = u2fRequest["keyHandle"].asString();
	Json::Value clientData;
	clientData["origin"] = appId;
	clientData["challenge"] = challenge;
	clientData["typ"] = "navigator.id.getAssertion";

	std::string clientDataString = fastWriter.write(clientData);
	PBYTE pClientData = new BYTE[clientDataString.length()];
	ZeroMemory(pClientData, clientDataString.length());
	memcpy(pClientData, clientDataString.c_str(), clientDataString.length() * sizeof(char));
	PBYTE clientParam = Helper::Crypto::CalculateHash(pClientData, (DWORD)clientDataString.length());

	PBYTE pAppId = new BYTE[appId.length()];
	ZeroMemory(pAppId, appId.length());
	memcpy(pAppId, appId.c_str(), appId.length() * sizeof(char));
	PBYTE appParam = Helper::Crypto::CalculateHash(pAppId, (DWORD)appId.length());

	std::string challengeHash = "";
	if (!Helper::Crypto::Base64Encode(clientParam, 32, &challengeHash))
		return{};

	std::string appIdHash = "";
	if (!Helper::Crypto::Base64Encode(appParam, 32, &appIdHash))
		return{};

	std::string clientDataBase64 = "";
	if (!Helper::Crypto::Base64Encode(pClientData, (DWORD)clientDataString.length(), &clientDataBase64))
		return{};

	Json::Value innerRequest;
	innerRequest["appIdHash"] = UrlSafe(appIdHash);
	innerRequest["keyHandle"] = UrlSafe(keyHandle);
	innerRequest["challengeHash"] = UrlSafe(challengeHash);
	innerRequest["version"] = version;
	std::string innerRequestStr = fastWriter.write(innerRequest);

	std::string crysilResponse = this->handler.Handle(innerRequestStr);
	if (crysilResponse.empty())
		return{};
	Json::Value internalResponse;
	reader.parse(crysilResponse, internalResponse);

	Json::Value response;
	response["challenge"] = UrlSafe(challenge);
	response["clientData"] = UrlSafe(clientDataBase64);
	response["keyHandle"] = UrlSafe(keyHandle);
	response["signatureData"] = UrlSafe(internalResponse["signatureData"].asString());

	delete[] pClientData;
	delete[] pAppId;

	std::string responseStr = fastWriter.write(response);
	return responseStr;
}

// Takes a U2F authenticate command in form of { appIdHash, challengeHash, keyHandle, version },
// extracts the public key and performs the CrySIL commands to sign data
// @returns {string} in form of { signatureData }
// @see CrySilForwarder
std::string AuthenticateInternalHandler::Handle(std::string message)
{
	Json::Value u2fRequest;
	Json::Reader reader;
	Json::FastWriter fastWriter;
	if (!reader.parse(message, u2fRequest))
		return{};

	std::string encodedRandom = u2fRequest["keyHandle"].asString();
	std::string crysilResponse = this->crySilForwarder.ExecuteGenerateWrappedKey({}, u2fRequest["appIdHash"].asString(), encodedRandom);
	if (crysilResponse.empty())
		return{};
	Json::Value responseGenKey;
	if (!reader.parse(crysilResponse, responseGenKey))
		return{};
	Json::Value payloadGenKey = responseGenKey["payload"];
	std::string wrappedKey = payloadGenKey["encodedWrappedKey"].asString();

	PWSTR sChallengeHash = L"";
	Helper::String::str2wstr(DeUrlSafe(u2fRequest["challengeHash"].asString()), &sChallengeHash);
	DWORD lChallengeHash = 0;
	PBYTE pChallengeHash = 0;
	if (!Helper::Crypto::Base64Decode(sChallengeHash, &pChallengeHash, &lChallengeHash))
		return{};

	PWSTR sAppIdHash = L"";
	Helper::String::str2wstr(DeUrlSafe(u2fRequest["appIdHash"].asString()), &sAppIdHash);
	DWORD lAppIdHash = 0;
	PBYTE pAppIdHash = 0;
	if (!Helper::Crypto::Base64Decode(sAppIdHash, &pAppIdHash, &lAppIdHash))
		return{};

	DWORD lBytesToSign = lAppIdHash + 1 + 4 + lChallengeHash;
	PBYTE pBytesToSign = new BYTE[lBytesToSign];
	ZeroMemory(pBytesToSign, lBytesToSign);

	BYTE pReserved[1] = { 1 };
	BYTE pCounter[4] = { 0, 0, 0, 0 };

	memcpy(pBytesToSign, pAppIdHash, lAppIdHash);
	memcpy(pBytesToSign + lAppIdHash, &pReserved, 1);
	memcpy(pBytesToSign + lAppIdHash + 1, &pCounter, 4);
	memcpy(pBytesToSign + lAppIdHash + 5, pChallengeHash, lChallengeHash);

	std::string sBytesToSign = "";
	if (!Helper::Crypto::Base64Encode(pBytesToSign, lBytesToSign, &sBytesToSign))
		return{};

	std::string responseStr = this->crySilForwarder.ExecuteSignatureRequest(wrappedKey, sBytesToSign, true);
	if (responseStr.empty())
		return{};
	Json::Value responseSign;
	if (!reader.parse(responseStr, responseSign))
		return{};
	Json::Value payloadSign = responseSign["payload"];
	Json::Value headerSign = responseSign["header"];

	if (headerSign["type"].asString() == "u2fHeader" && headerSign.isMember("counter"))
	{
		int counter = headerSign["counter"].asInt();
		pCounter[0] = (counter >> 24) & 0xFF;
		pCounter[1] = (counter >> 16) & 0xFF;
		pCounter[2] = (counter >> 8) & 0xFF;
		pCounter[3] = (counter >> 0) & 0xFF;
	}

	PWSTR sSignature = L"";
	Helper::String::str2wstr(payloadSign["signedHashes"][0u].asString(), &sSignature);

	DWORD lSignature = 0;
	PBYTE pSignature = 0;
	if (!Helper::Crypto::Base64Decode(sSignature, &pSignature, &lSignature))
		return{};

	Json::Value response;
	if (lSignature > 72) // from U2F Actor
	{
		response["signatureData"] = UrlSafe(payloadSign["signedHashes"][0u].asString());
	}
	else
	{
		DWORD lResponseBytes = 1 + 4 + lSignature;
		PBYTE pResponseBytes = new BYTE[lResponseBytes];
		ZeroMemory(pResponseBytes, lResponseBytes);

		memcpy(pResponseBytes, &pReserved, 1);
		memcpy(pResponseBytes + 1, &pCounter, 4);
		memcpy(pResponseBytes + 1 + 4, pSignature, lSignature);

		std::string sResponseBytes = "";
		if (!Helper::Crypto::Base64Encode(pResponseBytes, lResponseBytes, &sResponseBytes))
			return{};

		response["signatureData"] = UrlSafe(sResponseBytes);
		delete[] pResponseBytes;
	}
	delete[] pAppIdHash;
	delete[] pChallengeHash;
	delete[] pSignature;
	delete[] pBytesToSign;

	return fastWriter.write(response);
}
