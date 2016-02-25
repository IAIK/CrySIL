/* jshint esversion: 6, sub: false, devel: true, browser: true */
/* jshint -W069, -W117*/
/* globals chrome */
"use strict";

const EXTENSION_ID = "kmendfapggjehodndflmmgagdbamhnfd";

console.log("Background is registering with U2F extension");

/**
 * Register with the U2F extension
 */
chrome.runtime.sendMessage(EXTENSION_ID, chrome.runtime.id);

/**
 * Handles conversion of an U2F message from the crypto token extension to CrySIL commands
 * @param {string} msg
 * @param {string} url
 * @param {function} callback
 * @see crysiladapter.js
 */
function U2FReceiverHandler(msg, url, callback) {
    var crySilForwarder = new CrySilForwarder(url);
    if (msg.indexOf("helper_request") > -1) {
        if (msg.indexOf("sign_helper_request") > -1) {
            if (msg.indexOf("challengeHash") > -1) {
                new AuthenticateMultipleHandler(new AuthenticateInternalHandler(crySilForwarder)).handle(msg, callback);
            } else {
                new AuthenticateMultipleHandler(new AuthenticateExternalHandler(new AuthenticateInternalHandler(crySilForwarder))).handle(msg, callback);
            }
        } else {
            if (msg.indexOf("challengeHash") > -1) {
                new RegisterMultipleHandler(new RegisterInternalHandler(crySilForwarder)).handle(msg, callback);
            } else {
                new RegisterMultipleHandler(new RegisterExternalHandler(new RegisterInternalHandler(crySilForwarder))).handle(msg, callback);
            }
        }
    } else {
        if (msg.indexOf("challengeHash") > -1) {
            if (msg.indexOf("keyHandle") > -1) {
                new AuthenticateInternalHandler(crySilForwarder).handle(msg, callback);
            } else {
                new RegisterInternalHandler(crySilForwarder).handle(msg, callback);
            }
        } else {
            if (msg.indexOf("keyHandle") > -1) {
                new AuthenticateExternalHandler(new AuthenticateInternalHandler(crySilForwarder)).handle(msg, callback);
            } else {
                new RegisterExternalHandler(new RegisterInternalHandler(crySilForwarder)).handle(msg, callback);
            }
        }
    }
}

/**
 * Listen for message from the U2F extension, calls the U2FReceiverHandler to delegate message handling
 */
chrome.runtime.onMessageExternal.addListener(function(request, sender, sendResponse) {
    console.log("Background got via external message: " + JSON.stringify(request));
    if (typeof request.type == 'undefined' || !request.type) {
        console.log("Not handling this one");
        return false;
    }
    if (request.type != "enroll_helper_request" && request.type != "sign_helper_request") {
        console.log("Not handling this one");
        return false;
    }
    var url = "https://localhost/api/u2f/?id=1";
    chrome.storage.local.get("crysil-instance", function(items) {
        if (!chrome.runtime.error && typeof items["crysil-instance"] !== 'undefined') {
            url = items["crysil-instance"];
        }
        var toSend = JSON.stringify({"signData" : request.signData, "enrollChallenges" : request.enrollChallenges, "type" : request.type});
        U2FReceiverHandler(toSend, url, (response) => {
            var resp = JSON.parse(response);
            var index;
            var sentOne = false;
            var r;
            // first try to return response with code 0
            for (index = 0; index < resp.responses.length; index++) {
                r = resp.responses[index];
                if (r.code === 0) {
                    console.log("Sending response: " + JSON.stringify(r));
                    sendResponse(r);
                    return true;
                }
            }
            // then return any response
            for (index = 0; index < resp.responses.length; index++) {
                r = resp.responses[index];
                if (r.code !== 0) {
                    console.log("Sending response: " + JSON.stringify(r));
                    sendResponse(r);
                    return true;
                }
            }
            return true;
        });
        return true;
    });
    return true;
});
