package it.polimi.tiw.bank.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Contacts {
	private long customerId;
    private Map<Long, Map<Long, String>> contacts = new HashMap<>();

    public Contacts(long customerId) {
        this.customerId = customerId;
    }

	public long getCustomerId() {
		return customerId;
	}

	public Map<Long, Map<Long, String>> getContacts() {
		return contacts;
	}

    public void addContact(long customerId, long accountId, String fullName) {
        Objects.requireNonNull(fullName);

        if (contacts.get(customerId) == null) {
            contacts.put(customerId, new HashMap<Long, String>());
        }
        contacts.get(customerId).put(accountId, fullName);
	}

}
