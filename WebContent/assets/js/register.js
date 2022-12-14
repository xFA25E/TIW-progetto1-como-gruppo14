"use strict";

(function() { // avoid variables ending up in the global scope

    function processForm(e) {
        if (e.preventDefault) e.preventDefault();

        if (this.checkValidity()) {
            let form = new FormData(this);
            if (form.get("password") !== form.get("password-repeat")) {
                document.getElementById("error-message").textContent = "Passwords should be equal";
                return false;
            }

            makeCall(
                "POST", './register', this,
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        let message = req.responseText;
                        switch (req.status) {
                        case 200:
            	            sessionStorage.setItem('email', message);
                            window.location.href = "./home";
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

    let form = document.getElementById('register-form');
    if (form.attachEvent) {
        form.attachEvent("submit", processForm);
    } else {
        form.addEventListener("submit", processForm);
    }
})();
