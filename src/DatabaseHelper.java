import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:contacts.db";

    public DatabaseHelper() {
        createTable();
    }

    private void createTable() {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS kontak ("
                    + "nama TEXT, "
                    + "telepon TEXT PRIMARY KEY, "
                    + "kategori TEXT)";
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createTableSQL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            String query = "SELECT * FROM kontak";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String nama = rs.getString("nama");
                    String telepon = rs.getString("telepon");
                    String kategori = rs.getString("kategori");
                    contacts.add(new Contact(nama, telepon, kategori));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    public List<Contact> searchContactsByName(String searchTerm) {
        List<Contact> contacts = new ArrayList<>();
        String query = "SELECT * FROM kontak WHERE nama LIKE ?";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + searchTerm + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nama = rs.getString("nama");
                    String telepon = rs.getString("telepon");
                    String kategori = rs.getString("kategori");
                    contacts.add(new Contact(nama, telepon, kategori));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    public boolean addContact(Contact contact) {
        String query = "INSERT INTO kontak (nama, telepon, kategori) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, contact.getNama());
            stmt.setString(2, contact.getTelepon());
            stmt.setString(3, contact.getKategori());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateContact(String oldTelepon, Contact updatedContact) {
        String query = "UPDATE kontak SET nama = ?, telepon = ?, kategori = ? WHERE telepon = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, updatedContact.getNama());
            stmt.setString(2, updatedContact.getTelepon());
            stmt.setString(3, updatedContact.getKategori());
            stmt.setString(4, oldTelepon); // Nomor telepon lama

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteContact(String phoneNumber) {
            String query = "DELETE FROM kontak WHERE telepon = ?";
            try (Connection connection = DriverManager.getConnection(DB_URL);
                 PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, phoneNumber);
                stmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
