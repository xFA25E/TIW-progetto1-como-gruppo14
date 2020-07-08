"use strict";
(function () {

    let accountLoad = {};

	// page components
	window.addEventListener("load", () => {
		document.getElementById("div-header").addEventListener("click", showAnimation);
		document.getElementById("search-filter").addEventListener("keyup", searchFilterEvent);
        document.getElementById('transfer-form').addEventListener("submit", processTransferForm);

		// searchFilterEvent();
		modalEvent();

        let accountRows = document.getElementsByClassName("tr-account");
		for (let i = 0; i < accountRows.length; i++) {
            let accountRow = accountRows[i];
		    // create transfer row
            let transfersRow = document.createElement("tr");
            transfersRow.classList.add("tr-transfers");

            insertAfter(accountRow, transfersRow);

			accountRow.addEventListener(
                "click",
			    function () {
                    let loaded = false;
				    let displayed = false;

                    function loadTransfers(accountId) {
                        getTransfers(
                            accountId,
                            function (transfers) {
                                transfersRow.innerHTML = "";
                                transfersRow.appendChild(transfersToHtml(
                                    accountId, JSON.parse(transfers)
                                ));
                            }
                        );
                    }

                    let accountId = accountRow.getAttribute("account-id");
                    accountLoad[accountId] = function () { loadTransfers(accountId); };

                    return function () {
                        if (!loaded) {
                            loadTransfers(accountId);
                            loaded = true;
                        }

					    if (displayed) {
						    transfersRow.style.display = "none";
						    displayed = false
					    } else {
						    transfersRow.style.display = "contents";
						    displayed = true
					    }
				    };
			    }())

		}
		// if (sessionStorage.getItem("username") == null) {
		// 	window.location.href = "index.html";
		// } else {
		// pageOrchestrator.start(); // initialize the components
		// pageOrchestrator.refresh();
		// }
	}, false);

	function showAnimation() {
		if ($("#div-form").is(":hidden")) {
			$("#div-form").slideDown("slow");
			$("#img-form-header").attr("src", "./assets/images/arrow-up-24.png")
		} else {
			$("#div-form").slideUp("slow");
			$("#img-form-header").attr("src", "./assets/images/arrow-down-24.png")
		}
	}

	function AccountsList() {
		this.show = function () { ; }
		this.update = function () { ; }
	}

	function TransfersList() {
		this.show = function () { ; }
		this.update = function () { ; }
		this.reset = function () { ; }
		this.registerEvents = function () { ; }
	}

	function PaymentForm() {
		this.reset = function () { ; }
		this.registerEvents = function () { ; }
		this.pay = function () { ; }
	}

	function AddressBook() {
		this.reset = function () { ; }
		this.registerEvents = function () { ; }
		this.addContact = function () { ; }
	}

	function searchFilterEvent() {
		var input, filter, table, tr, td, i, txtValue;
		input = document.getElementById("search-filter");
		filter = input.value.toUpperCase();
		table = document.getElementById("customers-body");
		tr = table.getElementsByTagName("tr");
		for (i = 0; i < tr.length; i++) {
			td = tr[i].getElementsByTagName("td")[0];
			if (td) {
				txtValue = td.textContent || td.innerText;
				if (txtValue.toUpperCase().indexOf(filter) > -1) {
					tr[i].style.display = "";
				} else {
					tr[i].style.display = "none";
				}
			}
		}
	}

	function modalEvent() {
		var modal = document.getElementById("modal");

		// Get the button that opens the modal
		var btn = document.getElementById("btn-address-book");

		// Get the <span> element that closes the modal
		// var span = document.getElementsByClassName("close")[0];

		// When the user clicks the button, open the modal
		btn.onclick = function () {
			modal.style.display = "flex";

            let customers = document.getElementById("customers-body");
            customers.innerHTML = "";

            getContacts(function (contacts) {
                let contactsData = JSON.parse(contacts);
                for (let customerId in contactsData) {
                    for (let accountId in contactsData[customerId]) {
                        let fullName = contactsData[customerId][accountId];

                        function autoComplete(event) {
                            let div = document.getElementById("div-destination-information");
                            div.querySelector("input[name=destination-customer]").value = customerId.toString();
                            div.querySelector("input[name=destination-account]").value = accountId.toString();

				            modal.style.display = "none";
                        }

                        let tr = document.createElement("tr");
                        tr.addEventListener("click", autoComplete);
                        customers.appendChild(tr);

                        let tdFullName = document.createElement("td");
                        tdFullName.innerText = fullName;
                        tdFullName.addEventListener("click", autoComplete);
                        tr.appendChild(tdFullName);

                        let tdCustomer = document.createElement("td");
                        tdCustomer.innerText = customerId.toString();
                        tdCustomer.addEventListener("click", autoComplete);
                        tr.appendChild(tdCustomer);

                        let tdAccount = document.createElement("td");
                        tdAccount.innerText = accountId.toString();
                        tdAccount.addEventListener("click", autoComplete);
                        tr.appendChild(tdAccount);

                    }
                }
            });
		}

		// When the user clicks on <span> (x), close the modal
		// span.onclick = function () {
		// 	modal.style.display = "none";
		// }

		// When the user clicks anywhere outside of the modal, close it
		window.onclick = function (event) {
			if (event.target == modal) {
				modal.style.display = "none";
			}
		}
	}

    function transfersToHtml(accountId, transfers) {
        let td = document.createElement("td");
        td.setAttribute("colspan", "4");

        let div = document.createElement("div");
        div.setAttribute("id", "transfers-body");
        td.appendChild(div);

        let table = document.createElement("table");
        div.appendChild(table);

        for (let i = 0; i < transfers.length || false; i++) {
            // tr
            let tr = document.createElement("tr");
            tr.setAttribute(
                "class",
                accountId == transfers[i]["sourceAccountId"] ? "export" : "import"
            );
            table.appendChild(tr);

            // td image
            let tdImage = document.createElement("td");
            tr.appendChild(tdImage);

            let img = document.createElement("img");
            img.setAttribute(
                "src",
                "./assets/images/"
                    + (accountId == transfers[i]["sourceAccountId"] ? "upload.png" : "download.png")
            )
            img.setAttribute("width", "32");
            img.setAttribute("height", "32");
            tdImage.appendChild(img);

            // td account
            let tdAccount = document.createElement("td");
            tr.appendChild(tdAccount);

            let strongAccount = document.createElement("strong");
            strongAccount.innerText = "Conto";
            tdAccount.appendChild(strongAccount);

            let spanAccount = document.createElement("span");
            spanAccount.innerText = (accountId == transfers[i]["sourceAccountId"]
                                     ? transfers[i]["destinationAccountId"]
                                     : transfers[i]["sourceAccountId"]);
            strongAccount.appendChild(spanAccount);

            // td amount
            let tdAmount = document.createElement("td");
            tr.appendChild(tdAmount);

            let strongAmount = document.createElement("strong");
            tdAmount.appendChild(strongAmount);

            let spanAmount = document.createElement("span");
            spanAmount.innerText =
                (accountId == transfers[i]["sourceAccountId"] ? "-" : "+")
                + formatAmount(transfers[i]["amount"]);
            strongAmount.appendChild(spanAmount);

            // td date
            let tdDate = document.createElement("td");
            tr.appendChild(tdDate);

            let em = document.createElement("em");
            tdDate.appendChild(em);

            let spanDate = document.createElement("span");
            spanDate.innerText = transfers[i]["creationDate"];
            em.appendChild(spanDate);

            // td help
            let tdHelp = document.createElement("td");
            tr.appendChild(tdHelp);

            let imgHelp = document.createElement("img");
            imgHelp.setAttribute("src", "./assets/images/icons8-help-64.png");
            imgHelp.setAttribute("height", "32");
            imgHelp.setAttribute("width", "32");
            imgHelp.setAttribute("title", transfers[i]["cause"]);
            tdHelp.appendChild(imgHelp);
        }

        return td;
    }

    function getContacts(cback) {
        makeCall(
            "POST", './get-contacts', null,
            function(req) {
                if (req.readyState == XMLHttpRequest.DONE) {
                    let contacts = req.responseText;
                    switch (req.status) {
                    case 200:
                        cback(contacts);
                        break;
                    default:
                        console.log("Internal error on get contacts");
                        cossole.log(contacts);
                        break;
                    }
                }
            }
        );
    }

    function addContact(accountId, cback) {
        makeCall(
            "POST", './add-contact', createForm({"account-id": accountId.toString()}),
            function(req) {
                if (req.readyState == XMLHttpRequest.DONE) {
                    let message = req.responseText;
                    switch (req.status) {
                    case 200:
                        cback();
                        break;
                    default:
                        console.log("Internal error on add contact");
                        console.log(message);
                        break;
                    }
                }
            }
        );
    }

    function getTransfers(accountId, cback) {
        makeCall(
            "POST", './get-transfers', createForm({"account-id": accountId.toString()}),
            function(req) {
                if (req.readyState == XMLHttpRequest.DONE) {
                    let transfers = req.responseText;
                    switch (req.status) {
                    case 200:
                        cback(transfers);
                        break;
                    default:
                        console.log("Internal error on add contact");
                        console.log(transfers);
                        break;
                    }
                }
            }
        );
    }

    function createTransfer(
        sourceAccountId,
        destinationAccountId,
        destinationCustomerId,
        amount,
        cause,
        cback
    ) {
        makeCall(
            "POST", './create-transfer',
            createForm({
                "source-account": sourceAccountId.toString(),
                "destination-account": destinationAccountId.toString(),
                "destination-customer": destinationCustomerId.toString(),
                "amount": amount.toString(),
                "cause": cause.toString()
            }),
            function(req) {
                if (req.readyState == XMLHttpRequest.DONE) {
                    let account = req.responseText;
                    switch (req.status) {
                    case 200:
                        cback(account);
                        break;
                    default:
                        console.log("Internal error on create transfer");
                        console.log(account);
                        break;
                    }
                }
            }
        );
    }

    function formatAmount(amount) {
        return Math.floor(amount / 100).toString() + "," + (amount % 100).toString().padStart(2, "0");
    }

    function processTransferForm(e) {
        if (e.preventDefault) e.preventDefault();

        if (this.checkValidity()) {

            makeCall(
                "POST", './create-transfer', this,
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        let accounts = req.responseText;
                        switch (req.status) {
                        case 200:
                            let ids = JSON.parse(accounts);
                            for (let i = 0; i < ids.length; i++) {
                                accountLoad[ids[i]]();
                            }
                            break;
                        case 400: // bad request
                        case 401: // unauthorized
                        case 500: // server error
        	                console.log(account);
                            break;
                        }
                    }
                }
            );
        } else {
    	    this.reportValidity();
        }

        return false;
    }

})();
