/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package coursework.lib;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Lib extends JFrame {
    private JTextField bookIDField, titleField, authorField, yearField;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private Connection connection;

    public Lib() {
        initializeComponents();
        connectToDatabase();
        loadBookData();
    }

    private void initializeComponents() {
        setTitle("Library System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Book ID:"));
        bookIDField = new JTextField();
        bookIDField.setEditable(false); // Book ID will be auto-generated by the database
        inputPanel.add(bookIDField);

        inputPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Author:"));
        authorField = new JTextField();
        inputPanel.add(authorField);

        inputPanel.add(new JLabel("Year:"));
        yearField = new JTextField();
        inputPanel.add(yearField);

        JButton addButton = new JButton("Add Book");
        addButton.addActionListener(e -> addBook());
        inputPanel.add(addButton);

        JButton deleteButton = new JButton("Delete Book");
        deleteButton.addActionListener(e -> deleteBook());
        inputPanel.add(deleteButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadBookData());
        inputPanel.add(refreshButton);

        add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Book ID", "Title", "Author", "Year"}, 0);
        bookTable = new JTable(tableModel);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:ucanaccess://C:/Users/pc/Desktop/Library.accdb"; 
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBookData() {
        tableModel.setRowCount(0);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Books")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("BookID"),
                    rs.getString("Title"),
                    rs.getString("Author"),
                    rs.getInt("Year")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addBook() {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Books (Title, Author, Year) VALUES (?, ?, ?)")) {
            stmt.setString(1, titleField.getText());
            stmt.setString(2, authorField.getText());
            stmt.setInt(3, Integer.parseInt(yearField.getText()));
            stmt.executeUpdate();
            loadBookData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookID = (int) tableModel.getValueAt(selectedRow, 0);
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM Books WHERE BookID = ?")) {
                stmt.setInt(1, bookID);
                stmt.executeUpdate();
                loadBookData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a book to delete");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Lib().setVisible(true));
    }
}

 
 