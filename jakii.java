package jacqueline;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class jakii extends JFrame {

    private JPanel contentPane;
    private JTextField tfAuthor;
    private JTextField tfBookID;
    private JTextField tfTitle;
    private JTable table;
    private DefaultTableModel tableModel;

    private static final String URL = "jdbc:mysql://localhost:3306/jakii_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private JTextField textField;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                jakii frame = new jakii();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public jakii() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblBookID = new JLabel("Book ID");
        lblBookID.setBounds(99, 78, 84, 14);
        contentPane.add(lblBookID);

        tfBookID = new JTextField();
        tfBookID.setBounds(155, 73, 178, 25);
        contentPane.add(tfBookID);
        tfBookID.setColumns(10);

        JLabel lblAuthor = new JLabel("Author");
        lblAuthor.setBounds(99, 114, 64, 14);
        contentPane.add(lblAuthor);

        tfAuthor = new JTextField();
        tfAuthor.setBounds(155, 109, 178, 25);
        contentPane.add(tfAuthor);
        tfAuthor.setColumns(10);

        JLabel lblTitle = new JLabel("Title");
        lblTitle.setBounds(99, 150, 64, 14);
        contentPane.add(lblTitle);

        tfTitle = new JTextField();
        tfTitle.setBounds(155, 145, 178, 25);
        contentPane.add(tfTitle);
        tfTitle.setColumns(10);

        JButton btnBorrow = new JButton("Add Book");
        btnBorrow.setBounds(155, 186, 112, 33);
        contentPane.add(btnBorrow);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(445, 19, 84, 25);
        contentPane.add(btnSearch);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(351, 261, 84, 25);
        contentPane.add(btnUpdate);
        
        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(445, 261, 84, 25);
        contentPane.add(btnDelete);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(25, 301, 520, 150);
        contentPane.add(scrollPane);

        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"Book ID", "Author", "Title"});
        table = new JTable(tableModel);
        scrollPane.setViewportView(table);
        
        textField = new JTextField();
        textField.setBounds(51, 21, 384, 20);
        contentPane.add(textField);
        textField.setColumns(10);

        // Load all books on start
        loadAllBooks();

        btnBorrow.addActionListener(e -> {
            String bookID = tfBookID.getText().trim();
            String author = tfAuthor.getText().trim();
            String title = tfTitle.getText().trim();

            if (bookID.isEmpty() || author.isEmpty() || title.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                return;
            }

            addBook(bookID, author, title);
        });

        btnSearch.addActionListener(e -> {
            String bookID = tfBookID.getText().trim();

            if (bookID.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter the Book ID.");
                return;
            }

            searchBook(bookID);
        });

        btnUpdate.addActionListener(e -> {
            String bookID = tfBookID.getText().trim();
            String author = tfAuthor.getText().trim();
            String title = tfTitle.getText().trim();

            if (bookID.isEmpty() || author.isEmpty() || title.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                return;
            }

            updateBook(bookID, author, title);
        });
        
        btnDelete.addActionListener(e -> {
            String bookID = tfBookID.getText().trim();
            
            if (bookID.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter the Book ID.");
                return;
            }
            
            deleteBook(bookID);
        });
    }

    private void loadAllBooks() {
        String sql = "SELECT * FROM books";
        tableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String bookID = rs.getString("BookID");
                String author = rs.getString("Author");
                String title = rs.getString("Title");
                tableModel.addRow(new Object[]{bookID, author, title});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading books: " + ex.getMessage());
        }
    }

    private void addBook(String bookID, String author, String title) {
        String sql = "INSERT INTO books (BookID, Author, Title) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookID);
            pstmt.setString(2, author);
            pstmt.setString(3, title);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Book Added Successfully!");

            tfBookID.setText("");
            tfAuthor.setText("");
            tfTitle.setText("");

            loadAllBooks();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    private void searchBook(String bookID) {
        String sql = "SELECT * FROM books WHERE BookID = ?";

        tableModel.setRowCount(0);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String author = rs.getString("Author");
                String title = rs.getString("Title");

                tfAuthor.setText(author);
                tfTitle.setText(title);

                tableModel.addRow(new Object[]{bookID, author, title});
                JOptionPane.showMessageDialog(null, "Book Found.");
            } else {
                JOptionPane.showMessageDialog(null, "Book not found.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    private void updateBook(String bookID, String author, String title) {
        String sql = "UPDATE books SET Author = ?, Title = ? WHERE BookID = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, author);
            pstmt.setString(2, title);
            pstmt.setString(3, bookID);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Book record updated successfully.");
                loadAllBooks();
            } else {
                JOptionPane.showMessageDialog(null, "Book ID not found.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }
    
    private void deleteBook(String bookID) {
        String sql = "DELETE FROM books WHERE BookID = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookID);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Book record deleted successfully.");
                tfBookID.setText("");
                tfAuthor.setText("");
                tfTitle.setText("");
                loadAllBooks();
            } else {
                JOptionPane.showMessageDialog(null, "Book ID not found.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }
}
