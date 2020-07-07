package it.polimi.tiw.bank.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Contacts {
	private long customerId;
    private Map<Long, String> contacts = new HashMap<>();

    public Contacts(long customerId) {
        this.customerId = customerId;
    }

    public Contacts(long customerId, Map<Long, String> contacts) {
        this(customerId);
        this.contacts.putAll(Objects.requireNonNull(contacts));
    }

	public long getCustomerId() {
		return customerId;
	}

	public Map<Long, String> getContacts() {
		return contacts;
	}

	public void setContacts(Map<Long, String> contacts) {
		this.contacts = Objects.requireNonNull(contacts);
	}

    public void addContacts(Map<Long, String> contacts) {
        this.contacts.putAll(Objects.requireNonNull(contacts));
	}

    public void addContact(Long accountId, String fullName) {
        this.contacts.put(Objects.requireNonNull(accountId),
                          Objects.requireNonNull(fullName));
	}

}
