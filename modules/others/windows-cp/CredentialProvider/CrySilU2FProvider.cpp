//
// THIS CODE AND INFORMATION IS PROVIDED "AS IS" WITHOUT WARRANTY OF
// ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO
// THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
// PARTICULAR PURPOSE.
//
// Copyright (c) Microsoft Corporation. All rights reserved.
//
// CrySilU2FProvider implements ICredentialProvider, which is the main
// interface that logonUI uses to decide which tiles to display.
// In this sample, we will display one tile that uses each of the nine
// available UI controls.

#include <initguid.h>
#include "CrySilU2FProvider.h"
#include "CrySilU2FCredential.h"
#include "guid.h"

CrySilU2FProvider::CrySilU2FProvider() :
	_cRef(1),
	_dwNumCreds(0),
    _pCredProviderUserArray(nullptr)
{
    DllAddRef();
	ZeroMemory(_pCredentials, sizeof(_pCredentials));
}

CrySilU2FProvider::~CrySilU2FProvider()
{
    if (_pCredentials != nullptr)
    {
		for (size_t i = 0; i < _dwNumCreds; i++)
		{
			if (_pCredentials[i] != nullptr)
				_pCredentials[i]->Release();
		}
    }
    if (_pCredProviderUserArray != nullptr)
    {
        _pCredProviderUserArray->Release();
        _pCredProviderUserArray = nullptr;
    }

    DllRelease();
}

// SetUsageScenario is the provider's cue that it's going to be asked for tiles
// in a subsequent call.
HRESULT CrySilU2FProvider::SetUsageScenario(
    CREDENTIAL_PROVIDER_USAGE_SCENARIO cpus,
    DWORD /*dwFlags*/)
{
    HRESULT hr;

    // Decide which scenarios to support here. Returning E_NOTIMPL simply tells the caller
    // that we're not designed for that scenario.
    switch (cpus)
    {
    case CPUS_LOGON:
    case CPUS_UNLOCK_WORKSTATION:
        // The reason why we need _fRecreateEnumeratedCredentials is because ICredentialProviderSetUserArray::SetUserArray() is called after ICredentialProvider::SetUsageScenario(),
        // while we need the ICredentialProviderUserArray during enumeration in ICredentialProvider::GetCredentialCount()
        _cpus = cpus;
        _fRecreateEnumeratedCredentials = true;
        hr = S_OK;
        break;

    case CPUS_CHANGE_PASSWORD:
    case CPUS_CREDUI:
        hr = E_NOTIMPL;
        break;

    default:
        hr = E_INVALIDARG;
        break;
    }

    return hr;
}

// SetSerialization takes the kind of buffer that you would normally return to LogonUI for
// an authentication attempt.  It's the opposite of ICredentialProviderCredential::GetSerialization.
// GetSerialization is implement by a credential and serializes that credential.  Instead,
// SetSerialization takes the serialization and uses it to create a tile.
//
// SetSerialization is called for two main scenarios.  The first scenario is in the credui case
// where it is prepopulating a tile with credentials that the user chose to store in the OS.
// The second situation is in a remote logon case where the remote client may wish to
// prepopulate a tile with a username, or in some cases, completely populate the tile and
// use it to logon without showing any UI.
//
// If you wish to see an example of SetSerialization, please see either the SampleCredentialProvider
// sample or the SampleCredUICredentialProvider sample.  [The logonUI team says, "The original sample that
// this was built on top of didn't have SetSerialization.  And when we decided SetSerialization was
// important enough to have in the sample, it ended up being a non-trivial amount of work to integrate
// it into the main sample.  We felt it was more important to get these samples out to you quickly than to
// hold them in order to do the work to integrate the SetSerialization changes from SampleCredentialProvider
// into this sample.]
HRESULT CrySilU2FProvider::SetSerialization(
    _In_ CREDENTIAL_PROVIDER_CREDENTIAL_SERIALIZATION const * /*pcpcs*/)
{
    return E_NOTIMPL;
}

// Called by LogonUI to give you a callback.  Providers often use the callback if they
// some event would cause them to need to change the set of tiles that they enumerated.
HRESULT CrySilU2FProvider::Advise(
    _In_ ICredentialProviderEvents * /*pcpe*/,
    _In_ UINT_PTR /*upAdviseContext*/)
{
    return E_NOTIMPL;
}

// Called by LogonUI when the ICredentialProviderEvents callback is no longer valid.
HRESULT CrySilU2FProvider::UnAdvise()
{
    return E_NOTIMPL;
}

// Called by LogonUI to determine the number of fields in your tiles.  This
// does mean that all your tiles must have the same number of fields.
// This number must include both visible and invisible fields. If you want a tile
// to have different fields from the other tiles you enumerate for a given usage
// scenario you must include them all in this count and then hide/show them as desired
// using the field descriptors.
HRESULT CrySilU2FProvider::GetFieldDescriptorCount(
    _Out_ DWORD *pdwCount)
{
    *pdwCount = SFI_NUM_FIELDS;
    return S_OK;
}

// Gets the field descriptor for a particular field.
HRESULT CrySilU2FProvider::GetFieldDescriptorAt(
    DWORD dwIndex,
    _Outptr_result_nullonfailure_ CREDENTIAL_PROVIDER_FIELD_DESCRIPTOR **ppcpfd)
{
    HRESULT hr;
    *ppcpfd = nullptr;

    // Verify dwIndex is a valid field.
    if ((dwIndex < SFI_NUM_FIELDS) && ppcpfd)
    {
        hr = FieldDescriptorCoAllocCopy(s_rgCredProvFieldDescriptors[dwIndex], ppcpfd);
    }
    else
    {
        hr = E_INVALIDARG;
    }

    return hr;
}

// Sets pdwCount to the number of tiles that we wish to show at this time.
// Sets pdwDefault to the index of the tile which should be used as the default.
// The default tile is the tile which will be shown in the zoomed view by default. If
// more than one provider specifies a default the last used cred prov gets to pick
// the default. If *pbAutoLogonWithDefault is TRUE, LogonUI will immediately call
// GetSerialization on the credential you've specified as the default and will submit
// that credential for authentication without showing any further UI.
HRESULT CrySilU2FProvider::GetCredentialCount(
    _Out_ DWORD *pdwCount,
    _Out_ DWORD *pdwDefault,
    _Out_ BOOL *pbAutoLogonWithDefault)
{
    *pdwDefault = CREDENTIAL_PROVIDER_NO_DEFAULT;
    *pbAutoLogonWithDefault = FALSE;

    if (_fRecreateEnumeratedCredentials)
    {
        _fRecreateEnumeratedCredentials = false;
        _ReleaseEnumeratedCredentials();
        _CreateEnumeratedCredentials();
    }

    *pdwCount = _dwNumCreds;

    return S_OK;
}

// Returns the credential at the index specified by dwIndex. This function is called by logonUI to enumerate
// the tiles.
HRESULT CrySilU2FProvider::GetCredentialAt(
    DWORD dwIndex,
    _Outptr_result_nullonfailure_ ICredentialProviderCredential **ppcpc)
{
    HRESULT hr = E_INVALIDARG;
    *ppcpc = nullptr;

    if ((dwIndex < _dwNumCreds) && ppcpc)
    {
        hr = _pCredentials[dwIndex]->QueryInterface(IID_PPV_ARGS(ppcpc));
    }
    return hr;
}

// This function will be called by LogonUI after SetUsageScenario succeeds.
// Sets the User Array with the list of users to be enumerated on the logon screen.
HRESULT CrySilU2FProvider::SetUserArray(_In_ ICredentialProviderUserArray *users)
{
    if (_pCredProviderUserArray)
    {
        _pCredProviderUserArray->Release();
    }
    _pCredProviderUserArray = users;
    _pCredProviderUserArray->AddRef();
    return S_OK;
}

void CrySilU2FProvider::_CreateEnumeratedCredentials()
{
    switch (_cpus)
    {
    case CPUS_LOGON:
    case CPUS_UNLOCK_WORKSTATION:
        {
            _EnumerateCredentials();
            break;
        }
    default:
        break;
    }
}

void CrySilU2FProvider::_ReleaseEnumeratedCredentials()
{
	if (_pCredentials != nullptr)
	{
		for (size_t i = 0; i < _dwNumCreds; i++)
		{
			if (_pCredentials[i] != nullptr)
				_pCredentials[i]->Release();
		}
	}
}

HRESULT CrySilU2FProvider::_EnumerateCredentials()
{
    HRESULT hr = E_UNEXPECTED;
    if (_pCredProviderUserArray != nullptr)
    {
        DWORD dwUserCount;
		DWORD index = 0;
        _pCredProviderUserArray->GetCount(&dwUserCount);
        if (dwUserCount > 0)
        {
            ICredentialProviderUser *pCredUser;
			for (index = 0; index < dwUserCount; index++)
			{
				hr = _pCredProviderUserArray->GetAt(index, &pCredUser);
				if (SUCCEEDED(hr))
				{
					CrySilU2FCredential* pCredential = new(std::nothrow) CrySilU2FCredential();
					if (pCredential != nullptr)
					{
						hr = pCredential->Initialize(_cpus, s_rgCredProvFieldDescriptors, s_rgFieldStatePairs, pCredUser);
						if (SUCCEEDED(hr))
						{
							_pCredentials[index] = pCredential;
							_dwNumCreds++;							
						}
						else
						{
							pCredential->Release();
							pCredential = nullptr;
						}
					}
					else
					{
						hr = E_OUTOFMEMORY;
					}
					pCredUser->Release();
				}
			}
        }
    }
    return hr;
}

// Boilerplate code to create our provider.
HRESULT CrySil_CreateInstance(_In_ REFIID riid, _Outptr_ void **ppv)
{
    HRESULT hr;
    CrySilU2FProvider *pProvider = new(std::nothrow) CrySilU2FProvider();
    if (pProvider)
    {
        hr = pProvider->QueryInterface(riid, ppv);
        pProvider->Release();
    }
    else
    {
        hr = E_OUTOFMEMORY;
    }
    return hr;
}
