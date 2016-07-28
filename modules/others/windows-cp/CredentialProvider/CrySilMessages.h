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

class PinHandler
{
protected:
	std::string label = "Enter PIN to authenticate against CrySIL: ";
public:
	virtual std::string ReadPin();
	void SetLabel(std::string label);
};

class ConsolePinHandler : public PinHandler
{
public:
	std::string ReadPin() override;
};

class WindowsPinHandler : public PinHandler
{
private:
	std::string password;
	HWND hwndOwner;
	std::string label = "Enter PIN to authenticate against CrySIL: ";

	// static wrapper that manages the "this" pointer
	static INT_PTR CALLBACK AppDlgProc(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam)
	{
		if (message == WM_INITDIALOG)
			SetProp(hDlg, L"WorkaroundForThis", (HANDLE)lParam);
		else
			if (message == WM_NCDESTROY)
				RemoveProp(hDlg, L"WorkaroundForThis");

		WindowsPinHandler* pThis = (WindowsPinHandler*)GetProp(hDlg, L"WorkaroundForThis");
		return pThis ? pThis->PasswordProc(hDlg, message, wParam, lParam) : FALSE;
	}
	INT_PTR CALLBACK PasswordProc(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam);
public:
	std::string ReadPin() override;
	void setHwndOwner(HWND owner);
};

class CrySilForwarder
{
private:
	Json::Value header;
	PWSTR userSid;
	PinHandler* pinHandler;
	std::string ExecuteAuthentication(std::string commandId, std::string secretValue);
	std::string ExecuteDoubleAuthentication(std::string commandId, std::string secretValue1, std::string secretValue2);
	std::string Send(Json::Value payload);
	std::string CheckForAuth(std::string response);
public:
	CrySilForwarder(PWSTR _userSid, PinHandler *pinHandler);
	~CrySilForwarder();
	std::string ExecuteGenerateWrappedKey(std::string clientParam, std::string appParam, std::string encodedRandom);
	std::string ExecuteSignatureRequest(std::string keyEncoded, std::string hashToBeSigned, bool addCounter);
};

class Handler
{
public:
	virtual ~Handler()
	{
	}

public:
	virtual std::string Handle(std::string message);
};

class AuthenticateMultipleHandler : public Handler
{
private:
	Handler& handler;
public:
	virtual ~AuthenticateMultipleHandler()
	{
	}
	AuthenticateMultipleHandler(Handler& _handler) : handler(_handler)
	{
	}
	std::string Handle(std::string message) override;
};
class AuthenticateExternalHandler : public Handler
{
private:
	Handler& handler;
public:
	virtual ~AuthenticateExternalHandler()
	{
	}
	AuthenticateExternalHandler(Handler& _handler) : handler(_handler)
	{
	}
	std::string Handle(std::string message) override;
};
class AuthenticateInternalHandler : public Handler
{
private:
	CrySilForwarder& crySilForwarder;
public:
	virtual ~AuthenticateInternalHandler()
	{
	}
	AuthenticateInternalHandler(CrySilForwarder& _crySilForwarder)
		: crySilForwarder(_crySilForwarder)
	{
	}
	std::string Handle(std::string message) override;
};
