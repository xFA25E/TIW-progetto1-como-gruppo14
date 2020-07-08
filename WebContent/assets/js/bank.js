"use strict";

(function () {

	// page components
	window.addEventListener("load", () => {
		document.getElementById("div-header").addEventListener("click", showAnimation);
		document.getElementById("search-filter").addEventListener("keyup", searchFilterEvent);

		// searchFilterEvent();
		modalEvent();

		let accountRows = document.getElementsByClassName("tr-account");
		let transfersRows = document.getElementsByClassName("tr-transfers");
		for (let i = 0; i < accountRows.length; i++) {
			accountRows.item(i).addEventListener
			("click",
			function () {
				let transfers = transfersRows;
				let displayed = false;
				function display() {
					if (displayed) {
						transfersRows.item(i).style.display = "none";
						displayed = false
					} else {
						transfersRows.item(i).style.display = "contents";
						displayed = true
					}
				}
				return display;
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
		table = document.getElementById("customers");
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
})();

function populateContacts(contacts) {
    console.log(contacts);
}

function getContacts() {
    makeCall(
        "POST", './get-contacts', null,
        function(req) {
            if (req.readyState == XMLHttpRequest.DONE) {
                let contacts = req.responseText;
                switch (req.status) {
                case 200:
                    populateContacts(contacts);
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

function addContact(accountId) {
    makeCall(
        "POST", './add-contact', createForm({"account-id": accountId.toString()}),
        function(req) {
            if (req.readyState == XMLHttpRequest.DONE) {
                let message = req.responseText;
                switch (req.status) {
                case 200:
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
