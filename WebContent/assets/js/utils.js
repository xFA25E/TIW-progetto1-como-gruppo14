/**
 * AJAX call management
 */

function makeCall(method, url, formElement, cback, reset = true) {
	let req = new XMLHttpRequest(); // visible by closure
	req.onreadystatechange = function() {
	    cback(req)
	}; // closure
	req.open(method, url);
	if (formElement == null) {
	    req.send();
	} else {
	    req.send(new FormData(formElement));
	}
	if (formElement !== null && reset === true) {
	    formElement.reset();
	}
}

function createInput(name, value) {
    let input = document.createElement("input");
    input.name = name;
    input.value = value;
    return input;
}

function createForm(data) {
    let form = document.createElement("form");
    for (let key in data) {
        form.appendChild(createInput(key, data[key]));
    }
    return form;
}

function insertAfter(referenceNode, newNode) {
    referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
}
