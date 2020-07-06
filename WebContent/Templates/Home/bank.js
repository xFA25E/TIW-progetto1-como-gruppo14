(function () {

	// page components
	window.addEventListener("load", () => {
		document.getElementById("div-header").addEventListener("click", showAnimation);
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
			$("#img-form-header").attr("src", "arrow-up-24.png")
		} else {
			$("#div-form").slideUp("slow");
			$("#img-form-header").attr("src", "arrow-down-24.png")
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

})();

