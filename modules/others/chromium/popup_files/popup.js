
document.addEventListener("DOMContentLoaded", function() {
    chrome.storage.local.get("crysil-instance", function(items) {
        var url = "https://localhost/api/u2f/?id=1";
        if (!chrome.runtime.error) {
            if (items["crysil-instance"] !== undefined) {
                url = items["crysil-instance"];
            }
        }
        document.getElementById("crysil-instance").value = url;
    });

    document.getElementById("set").onclick = function() {
        var theValue = document.getElementById("crysil-instance").value;
        if (!theValue) {
            message("Error: No value specified");
            return;
        }
        chrome.storage.local.set({"crysil-instance": theValue}, function() {
            if (chrome.runtime.error) {
                console.log("Runtime error");
            }
            message("Settings saved");
        });
        window.close();
    };
});

