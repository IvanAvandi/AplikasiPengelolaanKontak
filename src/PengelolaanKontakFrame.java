/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.swing.*; // Untuk komponen GUI seperti JFrame, JButton, dll.
import javax.swing.table.DefaultTableModel; // Untuk model tabel
import java.awt.event.*; // Untuk event listener
import java.util.List; // Untuk penggunaan List
import java.io.*; // Untuk operasi file handling
import javax.swing.event.ListSelectionListener; // Untuk listener pada pemilihan baris di tabel
import javax.swing.event.ListSelectionEvent; // Untuk event terkait tabel


public class PengelolaanKontakFrame extends javax.swing.JFrame {
    private ContactManager contactManager;

    public PengelolaanKontakFrame() {
        initComponents();
        contactManager = new ContactManager();
        loadContacts();

        // Set up event listeners
        addButton.addActionListener(e -> addContact());
        editButton.addActionListener(e -> editContact());
        deleteButton.addActionListener(e -> deleteContact());
        searchButton.addActionListener(e -> searchContact());
        
        // Event listener untuk tombol impor dan ekspor
        btnImport.addActionListener(e -> importContacts());
        btnExport.addActionListener(e -> exportContacts());
    
        // Disable tombol Edit dan Delete saat awal
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        // Tambahkan listener untuk mengatur tombol Edit dan Delete berdasarkan pemilihan tabel
        contactsTable.getSelectionModel().addListSelectionListener(e -> updateButtonStates());
        
        // Add mouse listener to show data in input fields when table row is clicked
        contactsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = contactsTable.getSelectedRow();
                if (row >= 0) {
                    // Ambil data dari tabel
                    String nama = (String) contactsTable.getValueAt(row, 0);
                    String telepon = (String) contactsTable.getValueAt(row, 1);
                    String kategori = (String) contactsTable.getValueAt(row, 2);

                    // Set data ke input field
                    nameTextField.setText(nama);
                    phoneTextField.setText(telepon);
                    categoryComboBox.setSelectedItem(kategori);
                }
            }
        });
    }

    private void loadContacts() {
        try {
            List<Contact> contacts = contactManager.getAllContacts();
            DefaultTableModel model = (DefaultTableModel) contactsTable.getModel();
            model.setRowCount(0); // Hapus baris yang ada
            for (Contact contact : contacts) {
                model.addRow(new Object[]{contact.getNama(), contact.getTelepon(), contact.getKategori()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void addContact() {
        String nama = nameTextField.getText();
        String telepon = phoneTextField.getText();
        String kategori = (String) categoryComboBox.getSelectedItem();

        if (nama.isEmpty() || telepon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan Telepon harus diisi.");
            return;
        }

        Contact newContact = new Contact(nama, telepon, kategori);
        boolean success = contactManager.addContact(newContact);
        if (success) {
            loadContacts();
            clearInputFields(); // Kosongkan inputan
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan kontak.");
        }
    }

    private void editContact() {
        int row = contactsTable.getSelectedRow();
        if (row >= 0) {
            String oldTelepon = (String) contactsTable.getValueAt(row, 1); // Telepon lama digunakan untuk identifikasi

            String nama = nameTextField.getText();
            String telepon = phoneTextField.getText();
            String kategori = (String) categoryComboBox.getSelectedItem();

            if (nama.isEmpty() || telepon.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama dan Telepon harus diisi.");
                return;
            }

            // Update kontak dengan data baru
            Contact updatedContact = new Contact(nama, telepon, kategori);
            boolean success = contactManager.updateContact(oldTelepon, updatedContact); // Gunakan metode update dengan parameter yang sesuai

            if (success) {
                JOptionPane.showMessageDialog(this, "Kontak berhasil diperbarui.");
                loadContacts(); // Refresh tabel
                clearInputFields(); // Kosongkan inputan
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengedit kontak.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin diedit.");
        }
    }

    private void deleteContact() {
        int row = contactsTable.getSelectedRow();
        if (row >= 0) {
            String telepon = (String) contactsTable.getValueAt(row, 1);
            boolean success = contactManager.deleteContact(telepon);
            if (success) {
                loadContacts();
                clearInputFields(); // Kosongkan inputan
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus kontak.");
            }
        }
    }

    private void searchContact() {
        String searchTerm = searchTextField.getText();
        try {
            List<Contact> contacts = contactManager.searchContactsByName(searchTerm);
            DefaultTableModel model = (DefaultTableModel) contactsTable.getModel();
            model.setRowCount(0); // Hapus baris yang ada
            for (Contact contact : contacts) {
                model.addRow(new Object[]{contact.getNama(), contact.getTelepon(), contact.getKategori()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateButtonStates() {
        int row = contactsTable.getSelectedRow();
        editButton.setEnabled(row >= 0);
        deleteButton.setEnabled(row >= 0);
    }

    private void importContacts() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(","); // Sesuaikan dengan pemisah CSV Anda
                    if (data.length >= 3) {
                        String nama = data[0];
                        String telepon = data[1];
                        String kategori = data[2];
                        Contact contact = new Contact(nama, telepon, kategori);
                        contactManager.addContact(contact);
                    }
                }
                JOptionPane.showMessageDialog(this, "Kontak berhasil diimpor!");
                loadContacts();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mengimpor: " + e.getMessage());
            }
        }
    }

    private void exportContacts() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Nama,Telepon,Kategori\n");
                List<Contact> contacts = contactManager.getAllContacts();
                for (Contact contact : contacts) {
                    writer.write(contact.getNama() + "," + contact.getTelepon() + "," + contact.getKategori() + "\n");
                }
                JOptionPane.showMessageDialog(this, "Kontak berhasil diekspor!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mengekspor: " + e.getMessage());
            }
        }
    }
    
    private void clearInputFields() {
        nameTextField.setText("");
        phoneTextField.setText("");
        categoryComboBox.setSelectedIndex(0); // Mengatur combo box ke pilihan pertama
    }



    // Removed empty actionPerformed methods since they are handled by lambda expressions.
    // Other generated code for initComponents() remains the same.


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        phoneTextField = new javax.swing.JTextField();
        categoryComboBox = new javax.swing.JComboBox<>();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        contactsTable = new javax.swing.JTable();
        searchTextField = new javax.swing.JTextField();
        btnImport = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Nama:");

        jLabel2.setText("Telepon:");

        jLabel3.setText("Kategori:");

        categoryComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pekerjaan ", "Keluarga", "Teman" }));

        addButton.setText("Tambah");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        editButton.setText("Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Hapus");

        searchButton.setText("Cari");

        contactsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Nama", "Telepon", "Kategori"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(contactsTable);

        btnImport.setText("Import");

        btnExport.setText("Export");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3))
                                .addGap(28, 28, 28)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(phoneTextField)
                                    .addComponent(categoryComboBox, 0, 416, Short.MAX_VALUE)
                                    .addComponent(nameTextField)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(searchButton))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(addButton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(81, 81, 81)
                                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnImport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExport)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(phoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(categoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(editButton)
                    .addComponent(deleteButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnImport)
                    .addComponent(btnExport))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents




    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
       // TODO add your handling code here:
    }//GEN-LAST:event_addButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PengelolaanKontakFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnImport;
    private javax.swing.JComboBox<String> categoryComboBox;
    private javax.swing.JTable contactsTable;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JTextField phoneTextField;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchTextField;
    // End of variables declaration//GEN-END:variables
}
