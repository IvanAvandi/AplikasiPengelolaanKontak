import java.util.List;

public class ContactManager {
    private DatabaseHelper databaseHelper;

    public ContactManager() {
        this.databaseHelper = new DatabaseHelper();
    }

    public List<Contact> getAllContacts() {
        return databaseHelper.getAllContacts();
    }

    public List<Contact> searchContactsByName(String searchTerm) {
        return databaseHelper.searchContactsByName(searchTerm);
    }

    public boolean addContact(Contact contact) {
        return databaseHelper.addContact(contact);
    }

    public boolean updateContact(String oldTelepon, Contact updatedContact) {
        return databaseHelper.updateContact(oldTelepon, updatedContact);
    }

    public boolean deleteContact(String phoneNumber) {
        return databaseHelper.deleteContact(phoneNumber); // Panggil method deleteContact dengan 
    }
}
