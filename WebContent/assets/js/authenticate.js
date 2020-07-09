"use strict";

(function() { // avoid variables ending up in the global scope

    function processForm(e) {
        if (e.preventDefault) e.preventDefault();

        if (this.checkValidity()) {
            makeCall(
                "POST", './authenticate', this,
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        let message = req.responseText;
                        switch (req.status) {
                        case 200:
                            window.location.href = "./home";
                            return true;
                            break;
                        case 400: // bad request
                        case 401: // unauthorized
                        case 500: // server error
            	            document.getElementById("error-message").textContent = message;
                            break;
                        }
                    }
                },
                false
            );
        } else {
    	    this.reportValidity();
        }

        return false;
    }

    let form = document.getElementById('login-form');
    if (form.attachEvent) {
        form.attachEvent("submit", processForm);
    } else {
        form.addEventListener("submit", processForm);
    }
})();
