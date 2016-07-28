/* jshint esversion: 6, sub: false, devel: true, browser: true */
/* jshint -W069, -W117 */
"use strict";

/**
 * Couples the external libraries used to perform crypto operations
 * Sources for adapted libraries:
 * https://github.com/beatgammit/base64-js
 * http://www-cs-students.stanford.edu/~tjw/jsbn/base64.js
 * https://github.com/brillout/forge-sha256
 * https://github.com/GlobalSign/ASN1.js
 * https://github.com/GlobalSign/PKI.js
 */
class CryptoAdapter {

    /**
     * Converts a base64-encoded string into a byte array
     * @param {string} s
     * @returns {Uint8Array}
     */
    static decodeBase64(s) {
        return base64js.toByteArray(s);
    }

    /**
     * Encodes a byte array into a base64 string
     * @param {Uint8Array} b
     * @returns {string}
     */
    static encodeBase64(b) {
        return base64js.fromByteArray(b);
    }

    /**
     * Encodes a byte array into an url-safe base64 string
     * @param {Uint8Array} b
     * @returns {string}
     */ 
    static encodeBase64UrlSafe(b) {
        return U2FUtil.urlSafe(base64js.fromByteArray(b));
    }
    
    /**
     * Calculates the SHA256 hash of a given string and returns a byte array
     * @param {string} s
     * @returns {Uint8Array}
     */
    static hashToByteArray(s) {
        return base64js.toByteArray(hex2b64(forge_sha256(s)));
    }

    /**
     * Gets the public key encoded as byte array from an base64-encoded X.509 certificate
     * @param {string} s
     * @returns {Uint8Array}
     */
    static getPublicKeyEncoded(s) {
        var asn1 = org.pkijs.fromBER(CryptoAdapter.decodeBase64(s).buffer);
        var cert = new org.pkijs.simpl.CERT({schema:asn1.result});
        var arr = new Uint8Array(cert.subjectPublicKeyInfo.subjectPublicKey.toBER(false));
        if (arr.length == 68) {
            return arr.subarray(3, 68);
        }
        return arr;
    }
}

/**
 * Several useful functions for U2F
 */
class U2FUtil {

    /**
     * Converts an already base64-encoded string into a URL-safe variant
     * Replaces "+" with "-", "/" with "_", removes "=" and line breaks
     * @returns {string}
     */
    static urlSafe(s) {
        if (s) {
            s = s.replace(/\+/g,"-").replace(/\//g,"_").replace(/\r/g,"").replace(/\n/g,"").replace("=","");
        }
        return s;
    }
    
    /**
     * Converts an already base64-URL-encoded string into a basic variant
     * Replaces "-" with "+", "_" with "/", removes line breaks and adds "=" for padding
     * @returns {string}
     */
    static deUrlSafe(s) {
        if (s) {
            s = s.replace(/-/g,"+").replace(/_/g,"/").replace(/\r/g,"").replace(/\n/g,"");
            while (s.length % 4 !== 0) {
                s += "=";
            }
        }
        return s;
    }

    /**
     * Concatenates all buffers passed in the arguments to a single large buffer
     * @returns {Uint8Array}
     */
    static arrayBufferConcat() {
        var length = 0;
        var buffer = null;
        for (var i in arguments) {
            buffer = arguments[i];
            length += buffer.byteLength;
        }
        var joined = new Uint8Array(length);
        var offset = 0;
        for (var j in arguments) {
            buffer = arguments[j];
            joined.set(new Uint8Array(buffer), offset);
            offset += buffer.byteLength;
        }
        return joined;
    }

    /**
     * Checks whether the haystack contains the needle or not
     * @param {buffer} haystack
     * @param {buffer} needle
     * @returns {boolean}
     */
    static arrayBufferContains(haystack, needle) {
        var len = needle.length;
        var limit = haystack.length;
        for (var i = 0; i <= limit; i++) {
            var k = 0;
            for (; k < len; k++) {
                if (needle[k] != haystack[i+k])
                    break;
            }
            if (k == len)
                return true;
        }
        return false;
    }

}

/**
 * Base class for all U2F adapters
 */
class Handler {
    handle(msg, callback) {
        console.log("Handler base; calling callback with " + msg);
        callback(msg);
    }
}

/**
 * Manages communication with a CrySIL instance, e.g. knows the JSON response/request format
 * Also handles SecretKey Authentication by prompting the user to enter the secret
 */
class CrySilForwarder {

    constructor(url) {
        this.url = url;
        this.header = {
            "type" : "standardHeader",
            "commandId" : "",
            "sessionId" : "",
            "path" : [ ],
            "protocolVersion" : "2.0"
        };
    }

    /**
     * Sends a new "generateU2FKeyRequest" to get the wrapped key (for CrySIL) from the key handle (stored by the U2F server)
     */
    executeGenerateWrappedKey(clientParam, appParam, encodedRandom, callback) {
        this.header["type"] = "standardHeader";
        delete this.header["counter"];
        var payload = {
            "type" : "generateU2FKeyRequest",
            "certificateSubject" : "CN=CrySIL",
            "appParam" : U2FUtil.deUrlSafe(appParam),
            "clientParam" : U2FUtil.deUrlSafe(clientParam),
            "encodedRandom" : U2FUtil.deUrlSafe(encodedRandom)
        };
        this.send(payload, callback);
    }

    /**
     * Sends a new "signRequest" to actually sign the U2F challenge with the wrapped key
     */
    executeSignatureRequest(keyEncoded, hashToBeSigned, addCounter, callback) {
        this.header["type"] = "standardHeader";
        delete this.header["counter"];   
        if (addCounter === true) {
            this.header["type"] = "u2fHeader";
            this.header["counter"] = 0;
        }
        var payload = {
            "type" : "signRequest",
            "algorithm" : "SHA256withECDSA",
            "hashesToBeSigned" : [
                U2FUtil.deUrlSafe(hashToBeSigned)
            ],
            "signatureKey" : {
                "type" : "wrappedKey",
                "encodedWrappedKey" : keyEncoded
            }
        };
        this.send(payload, callback);
    }

    /**
     * Sends a new "authChallengeResponse" to send the secret entered by the user
     */
    executeAuthentication(commandId, secretValue, callback) {
        this.header["type"] = "standardHeader";
        delete this.header["counter"];
        this.header["commandId"] = commandId;
        var payload = {
            "type" : "authChallengeResponse",
            "authInfo" : {
                "type" : "SecretAuthInfo",
                "secret" : secretValue
            }
        };
        this.send(payload, callback);
    }

    /**
      * Sends a new "authChallengeResponse" to send the two secrets entered by the user
      */
    executeDoubleAuthentication(commandId, secretValue1, secretValue2, callback) {
        this.header["type"] = "standardHeader";
        delete this.header["counter"];
        this.header["commandId"] = commandId;
        var payload = {
            "type" : "authChallengeResponse",
            "authInfo" : {
                "type" : "SecretDoubleAuthInfo",
                "secret1" : secretValue1,
                "secret2" : secretValue2
            }
        };
        this.send(payload, callback);
    }

    /**
     * Sends the payload as a CrySIL command to the server in JSON
     */
    send(payload, callback) {
        var request = { "header" : this.header, "payload" : payload };
        var xhr = new XMLHttpRequest();
        xhr.open("POST", this.url, true);
        xhr.setRequestHeader("Content-Type","application/json");
        xhr.setRequestHeader("Accept","application/json");
        xhr.onerror = function (e) {
            console.error(xhr.statusText);
        };
        xhr.onload = (e) => {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    this.checkForAuth(xhr.response, callback);
                    return;
                } else {
                    console.error(xhr.statusText);
                    return;
                }
            }
            callback();
            return;
        };      
        xhr.send(JSON.stringify(request));
    }

    /**
     * Checks whether the response from CrySIL contains a "authChallengeRequest".
     * If it is a "SecretAuthType", it will prompt the user for the secret,
     * otherwise the response is unhandled and passed on to the callback
     */
    checkForAuth(responseStr, callback) {
        if (typeof responseStr == 'undefined') {
            callback(responseStr);
            return;
        }
        var response = JSON.parse(responseStr);
        var header = response["header"];
        var payload = response["payload"];
        if (payload["type"] == "authChallengeRequest") {
            var authTypeFound = "";
            payload["authTypes"].forEach((authType) => {
                if (authType["type"] == "SecretAuthType" || authType["type"] == "SecretDoubleAuthType") {
                    authTypeFound = authType["type"];
                }
            });
            if (authTypeFound === "") {
                console.log("Error: No valid auth type found!");
                callback(responseStr);
            } else if (authTypeFound == "SecretAuthType") {
                var pin = prompt("Please enter your PIN to authenticate against CrySIL", "");
                if (pin !== null) {
                    this.executeAuthentication(header["commandId"], pin, (authResponseStr) => {
                        if (typeof authResponseStr == 'undefined') {
                            callback(responseStr);
                        } else {
                            callback(authResponseStr);
                        }
                    });
                } else {
                    console.log("Error: No PIN entered by user");
                    callback(responseStr);
                }
            } else if (authTypeFound == "SecretDoubleAuthType") {
                var pin1 = prompt("Please enter your signature PIN to authenticate against CrySIL", "");
                if (pin1 !== null) {
                    var pin2 = prompt("Please enter your card PIN to authenticate against CrySIL", "");
                    if (pin2 !== null) {
                        this.executeDoubleAuthentication(header["commandId"], pin1, pin2, (authResponseStr) => {
                            if (typeof authResponseStr == 'undefined') {
                                callback(responseStr);
                            } else {
                                callback(authResponseStr);
                            }
                        });
                    } else {
                        console.log("Error: No first PIN entered by user");
                        callback(responseStr);
                    }
                } else {
                    console.log("Error: No second PIN entered by user");
                    callback(responseStr);
                }
            }
        } else if (payload["type"] == "status") {
            callback();
        } else {
            callback(responseStr);
        }
    }
}

/**
 * Takes a U2F register command in form of { appId, challenge, version },
 * performs some hashing operations
 * and passes the command on in the form of { appIdHash, challengeHash, version }
 * @returns {string} in form of { clientData, registrationData }
 * @see RegisterInternalHandler
 */
class RegisterExternalHandler extends Handler {
    constructor(handler) {
        super();
        this.handler = handler;
    }
    handle(msg, callback) {
        var u2fRequest = JSON.parse(msg);
        var appId = u2fRequest["appId"];
        var version = u2fRequest["version"];
        var challenge = u2fRequest["challenge"];
        var clientData = {
            "origin" : appId,
            "challenge" : challenge,
            "typ" : "navigator.id.finishEnrollment"
        };
        var clientDataString = JSON.stringify(clientData);
        var clientParam = CryptoAdapter.hashToByteArray(clientDataString);
        var appParam = CryptoAdapter.hashToByteArray(appId);
        var innerRequest = {
            "appIdHash" : CryptoAdapter.encodeBase64UrlSafe(appParam),
            "challengeHash" : CryptoAdapter.encodeBase64UrlSafe(clientParam),
            "version" : version
        };
        this.handler.handle(JSON.stringify(innerRequest), (crysilResponse) => {
            if (typeof crysilResponse == 'undefined') {
                callback();
                return;
            }
            var internalResponse = JSON.parse(crysilResponse);
            var response = {
                "clientData" : CryptoAdapter.encodeBase64UrlSafe(new TextEncoder().encode(clientDataString)),
                "registrationData" : internalResponse["registrationData"]
            };
            callback(JSON.stringify(response));
        });
    }
}

/**
 * Takes a U2F register command in form of { appIdHash, challengeHash, version },
 * extracts the public key, random and performs the CrySIL commands to sign data
 * @returns {string} in form of { registrationData }
 * @see CrySilForwarder
 */
class RegisterInternalHandler extends Handler {
    constructor(crySilForwarder) {
        super();
        this.crySilForwarder = crySilForwarder;
    }
    handle(msg, callback) {
        var u2fRequest = JSON.parse(msg);
        var challengeParam = CryptoAdapter.decodeBase64(u2fRequest["challengeHash"]);
        var appParam = CryptoAdapter.decodeBase64(u2fRequest["appIdHash"]);
        this.crySilForwarder.executeGenerateWrappedKey(u2fRequest["challengeHash"], u2fRequest["appIdHash"], null, (crysilResponse) => {
            if (typeof crysilResponse == 'undefined') {
                callback();
                return;
            }
            var responseGenKey = JSON.parse(crysilResponse);
            var payloadGenKey = responseGenKey["payload"];
            var keyEncoded = CryptoAdapter.getPublicKeyEncoded(payloadGenKey["encodedX509Certificate"]);
            var certEncoded = CryptoAdapter.decodeBase64(payloadGenKey["encodedX509Certificate"]);
            var keyHandleBytes = CryptoAdapter.decodeBase64(payloadGenKey["encodedRandom"]);
            var signatureBytes = U2FUtil.arrayBufferConcat(new Uint8Array([0]), appParam, challengeParam, keyHandleBytes, keyEncoded);
            this.crySilForwarder.executeSignatureRequest(payloadGenKey["encodedWrappedKey"], CryptoAdapter.encodeBase64(signatureBytes), false, (responseStr) => {
                if (typeof responseStr == 'undefined') {
                    callback();
                    return;
                }
                var responseSign = JSON.parse(responseStr);
                var payloadSign = responseSign["payload"];
                var signature = CryptoAdapter.decodeBase64(payloadSign["signedHashes"][0]);
                var signatureData = U2FUtil.arrayBufferConcat(new Uint8Array([5]), keyEncoded, new Uint8Array([keyHandleBytes.length]), keyHandleBytes, certEncoded, signature);
                var fromU2FActor = U2FUtil.arrayBufferContains(signature, keyHandleBytes) && U2FUtil.arrayBufferContains(signature, certEncoded);
                if (fromU2FActor) {
                    signatureData = signature;
                }
                var response = { "registrationData" : CryptoAdapter.encodeBase64UrlSafe(signatureData) };
                callback(JSON.stringify(response));
            });
        });
    }
}

/**
 * Takes a U2F register command in form of { type, enrollChallenges = [ { appIdHash, challengeHash, version }], signData = [ { appIdHash, challengeHash, keyHandle, version } ] },
 * performs some data transformation (no crypto operations involved)
 * and passes the command on in the form of { appIdHash, challengeHash, version }
 * @returns {string} in form of { responses = [ { type, code, version, enrollData } ] }
 * @see RegisterInternalHandler
 */
class RegisterMultipleHandler extends Handler {
    constructor(handler) {
        super();
        this.handler = handler;
    }
    handle(msg, callback) {
        var u2fRequest = JSON.parse(msg);
        var responseData = [];
        var callbacksRemaining = u2fRequest["enrollChallenges"].length;
        u2fRequest["enrollChallenges"].forEach((request) => {
            this.handler.handle(JSON.stringify(request), (responseStr) => {
                --callbacksRemaining;
                if (typeof responseStr !== 'undefined') {
                    var registerResponse = JSON.parse(responseStr);
                    responseData.push({
                        "type" : "enroll_helper_reply",
                        "code" : 0,
                        "version" : "U2F_V2",
                        "enrollData" : registerResponse["registrationData"]
                    });
                }
                if (callbacksRemaining === 0) {
                    var response = { "responses" : responseData };
                    callback(JSON.stringify(response));
                }
            });
        });
    }
}

/**
 * Takes a U2F authenticate command in form of { appId, challenge, keyHandle, version },
 * performs some hashing operations
 * and passes the command on in the form of { appIdHash, challengeHash, keyHandle, version }
 * @returns {string} in form of { challenge, clientData, keyHandle, signatureData }
 * @see AuthenticateInternalHandler
 */
class AuthenticateExternalHandler extends Handler {
    constructor(handler) {
        super();
        this.handler = handler;
    }
    handle(msg, callback) {
        var u2fRequest = JSON.parse(msg);
        var appId = u2fRequest["appId"];
        var version = u2fRequest["version"];
        var challenge = u2fRequest["challenge"];
        var keyHandle = u2fRequest["keyHandle"];
        var clientData = {
            "origin" : appId,
            "challenge" : challenge,
            "typ" : "navigator.id.getAssertion"
        };
        var clientDataString = JSON.stringify(clientData);
        var clientParam = CryptoAdapter.hashToByteArray(clientDataString);
        var appParam = CryptoAdapter.hashToByteArray(appId);
        var innerRequest = {
            "appIdHash" : CryptoAdapter.encodeBase64UrlSafe(appParam),
            "keyHandle" : keyHandle,
            "challengeHash" : CryptoAdapter.encodeBase64UrlSafe(clientParam),
            "version" : version
        };
        this.handler.handle(JSON.stringify(innerRequest), (crysilResponse) => {
            if (typeof crysilResponse == 'undefined') {
                callback();
                return;
            }
            var internalResponse = JSON.parse(crysilResponse);
            var response = {
                "challenge" : challenge,
                "clientData" : CryptoAdapter.encodeBase64UrlSafe(new TextEncoder().encode(clientDataString)),
                "keyHandle" : keyHandle,
                "signatureData" : internalResponse["signatureData"]
            };
            callback(JSON.stringify(response));
        });
    }
}

/**
 * Takes a U2F authenticate command in form of { appIdHash, challengeHash, keyHandle, version },
 * extracts the public key and performs the CrySIL commands to sign data
 * @returns {string} in form of { signatureData }
 * @see CrySilForwarder
 */
class AuthenticateInternalHandler extends Handler {
    constructor(crySilForwarder) {
        super();
        this.crySilForwarder = crySilForwarder;
    }
    handle(msg, callback) {
        var u2fRequest = JSON.parse(msg);
        var encodedRandom = U2FUtil.deUrlSafe(u2fRequest["keyHandle"]);
        this.crySilForwarder.executeGenerateWrappedKey(null, u2fRequest["appIdHash"], encodedRandom, (crysilResponse) => {
            if (typeof crysilResponse == 'undefined') {
                callback();
                return;
            }
            var responseGenKey = JSON.parse(crysilResponse);
            var payloadGenKey = responseGenKey["payload"];
            var wrappedKey = payloadGenKey["encodedWrappedKey"];
            var challengeParam = CryptoAdapter.decodeBase64(u2fRequest["challengeHash"]);
            var applicationBytes = CryptoAdapter.decodeBase64(u2fRequest["appIdHash"]);
            var counterArray = new Uint8Array([0,0,0,0]);
            var bytesToSign = U2FUtil.arrayBufferConcat(applicationBytes, new Uint8Array([1]), counterArray, challengeParam);
            this.crySilForwarder.executeSignatureRequest(wrappedKey, CryptoAdapter.encodeBase64(bytesToSign), true, (responseStr) => {
                if (typeof responseStr == 'undefined') {
                    callback();
                    return;
                }
                var responseSign = JSON.parse(responseStr);
                var payloadSign = responseSign["payload"];
                var headerSign = responseSign["header"];
                if (typeof payloadSign == 'undefined' || typeof payloadSign["signedHashes"] == 'undefined') {
                    callback();
                    return;
                }
                    
                var signature = CryptoAdapter.decodeBase64(payloadSign["signedHashes"][0]);
                
                if (headerSign["type"] == "u2fHeader" && typeof headerSign["counter"] !== 'undefined') {
                    // CrySIL has changed the counter value it has signed
                    var counter = headerSign["counter"];
                    counterArray[0] = (counter >> 24) & 0xFF;
                    counterArray[1] = (counter >> 16) & 0xFF;
                    counterArray[2] = (counter >>  8) & 0xFF;
                    counterArray[3] = (counter >>  0) & 0xFF;
                }
                var responseBytes = U2FUtil.arrayBufferConcat(new Uint8Array([1]), counterArray, signature);
                if (signature.length > 72) { // from U2F Actor
                    responseBytes = signature;
                }                
                var response = { "signatureData" : CryptoAdapter.encodeBase64UrlSafe(responseBytes) };
                callback(JSON.stringify(response));
            });
        });        
    }
}

/**
 * Takes a U2F authenticate command in form of { type, signData = [ { appIdHash, challengeHash, keyHandle, version } ] },
 * performs some data transformation (no crypto operations involved)
 * and passes the command on in the form of { appIdHash, challengeHash, keyHandle, version }
 * @returns {string} in form of { responses = [ { type, code, responseData = { version, challengeHash, appIdHash, keyHandle, signatureData } } ] }
 * @see AuthenticateInternalHandler
 */
class AuthenticateMultipleHandler extends Handler {
    constructor(handler) {
        super();
        this.handler = handler;
    }
    handle(msg, callback) {
        var u2fRequest = JSON.parse(msg);
        var singleResponses = [];
        var callbacksRemaining = u2fRequest["signData"].length;
        u2fRequest["signData"].forEach((request) => {
            this.handler.handle(JSON.stringify(request), (responseStr) => {
                var signatureData = "";
                var code = 4;
                if (typeof responseStr !== 'undefined') {
                    var innerResponse = JSON.parse(responseStr);
                    if (typeof innerResponse !== 'undefined' && typeof innerResponse["signatureData"] !== 'undefined') {
                        signatureData = innerResponse["signatureData"];
                        code = 0;
                    }
                }
                --callbacksRemaining;
                singleResponses.push({
                    "type" : "sign_helper_reply",
                    "code" : code,
                    "responseData": {
                        "version" : "U2F_V2",
                        "challengeHash" : request["challengeHash"],
                        "appIdHash" : request["appIdHash"],
                        "keyHandle" : request["keyHandle"],
                        "signatureData" : signatureData
                    }
                });
                if (callbacksRemaining === 0) {
                    var response = { "responses" : singleResponses };
                    callback(JSON.stringify(response));
                }
            });
        });
    }
}
