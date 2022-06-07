package com.lutalic.backend.dao;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.lutalic.backend.entities.Post;
import com.lutalic.backend.entities.Table;
import com.lutalic.backend.entities.User;
import com.lutalic.backend.exceptions.AccountAlreadyExistsException;
import com.lutalic.backend.exceptions.NoSuchUserException;
import com.lutalic.backend.exceptions.PasswordMismatchException;

public class LuDao implements Dao {
    private static final String CONFIG = "src\\main\\resources\\app.properties";
    private static LuDao INSTANCE = null;

    // Must be closed
    private final Connection connection;
    private final Properties properties;

    public synchronized static LuDao getDao() {
        if (INSTANCE == null) {
            try {
                INSTANCE = new LuDao();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return INSTANCE;
    }

    private LuDao() throws Exception {
        Properties properties = new Properties();
        properties.load(new FileReader(CONFIG));

        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        this.connection = DriverManager.getConnection(url, user, password);
        this.properties = properties;
    }

    @Override
    public void upsertNewUser(User user) throws IOException, SQLException {
        if (isUserExists(user.getEmail()) != null) {
            throw new AccountAlreadyExistsException();
        }

        // TODO implement hashPassword()
        String sql = properties.getProperty("UPSERT_USER");
        try (PreparedStatement statement = connection.prepareStatement(getSql(sql))) {
            // Set users params
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getPassword());
            statement.executeUpdate();
        }
    }

    @Override
    public void authorization(User user) throws Exception {
        User current = isUserExists(user.getEmail());
        if (current == null) {
            throw new NoSuchUserException();
        }

        if (!current.getPassword().equals(user.getPassword())) {
            throw new PasswordMismatchException();
        }
    }

    @Override
    public void createNewTable(Table table) throws Exception {
        User current = isUserExists(table.getAdmin());
        if (current == null) throw new NoSuchUserException();

        connection.setAutoCommit(false);
        String sql = properties.getProperty("UPSERT_TABLE");
        try (PreparedStatement statement = connection.prepareStatement(getSql(sql))) {
            // Set table params
            statement.setInt(1, table.getId());
            statement.setString(2, table.getName());
            statement.setString(3, table.getAdmin());
            statement.executeUpdate();

            addTableToUser(table, current);
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void addTableToUser(Table table, User user) throws Exception {
        String sql = properties.getProperty("ADD_TABLE");
        try (PreparedStatement statement = connection.prepareStatement(getSql(sql))) {
            // set user_table params
            statement.setString(1, user.getEmail());
            statement.setInt(2, table.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void upsertPost(Post post) throws Exception {
        String sql = properties.getProperty("ADD_POST");
        try (PreparedStatement statement = connection.prepareStatement(getSql(sql))) {
            // set post's params
            statement.setInt(1, post.getId());
            statement.setString(2, post.getName());
            statement.setString(3, post.getDescription());
            statement.setDate(4, Date.valueOf(post.getDate()));
            statement.setString(5, post.getColour());
            statement.setInt(6, post.getTableId());
            statement.executeUpdate();
        }
    }

    @Override
    public void updateTable(Table table) throws Exception {
        String sql = properties.getProperty("UPD_TABLE");
        try (PreparedStatement statement = connection.prepareStatement(getSql(sql))) {
            // set table params
            statement.setString(1, table.getName());
            statement.setString(2, table.getAdmin());
            statement.setInt(3, table.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void updatePost(Post post) throws Exception {
        String sql = properties.getProperty("UPD_POST");
        try (PreparedStatement statement = connection.prepareStatement(getSql(sql))) {
            // set post's params
            statement.setString(1, post.getName());
            statement.setString(2, post.getDescription());
            statement.setDate(3, Date.valueOf(post.getDate()));
            statement.setString(4, post.getColour());
            statement.setInt(5, post.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void removeTable(Table table) throws Exception {
        String sql = properties.getProperty("REMOVE_TABLE");
        try (PreparedStatement statement = connection.prepareStatement(getSql(sql))) {
            // Set table's id for SQL-request
            statement.setInt(1, table.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void removePost(Post post) throws Exception {
        String sql = properties.getProperty("REMOVE_POST");
        try (PreparedStatement statement = connection.prepareStatement(getSql(sql))) {
            // Set post's id for SQL-request
            statement.setInt(1, post.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    @Override
    public List<Table> getUserTables(String email) throws Exception {
        List<Table> result = new ArrayList<>();
        String sql = properties.getProperty("ALL_TABLES");
        try (PreparedStatement statement = connection.prepareStatement(getSql(sql))) {
            // Set user's email for SQL-request
            statement.setString(1, email);
            try (ResultSet set = statement.executeQuery()) {
                // Get all found tables
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String adminEmail = set.getString("admin_email");
                    result.add(new Table(id, name, adminEmail));
                }
            }
        }
        return result;
    }

    @Override
    public List<Post> getAllPosts(int parentId) throws Exception {
        List<Post> result = new ArrayList<>();
        String sql = properties.getProperty("ALL_POSTS");
        try (PreparedStatement statement = connection.prepareStatement(getSql(sql))) {
            // Set table's id for SQL-request
            statement.setInt(1, parentId);
            try (ResultSet set = statement.executeQuery()) {
                // Get all found posts
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String description = set.getString("description");
                    LocalDate date = set.getDate("date").toLocalDate();
                    String colour = set.getString("colour");
                    int tableId = set.getInt("table_id");
                    result.add(new Post(id, name, description, date, colour, tableId));
                }
            }
        }
        return result;
    }

    @Override
    public List<Table> getAllTables() throws Exception {
        List<Table> result = new ArrayList<>();
        String sql = properties.getProperty("FULL_TABLES");
        try (PreparedStatement statement = connection.prepareStatement(getSql(sql))) {
            try (ResultSet set = statement.executeQuery()) {
                // Get all found tables
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String adminEmail = set.getString("admin_email");
                    result.add(new Table(id, name, adminEmail));
                }
            }
        }
        return result;
    }

    /**
     * Check user for exists
     * @return Found user if exists, else null
     */
    private User isUserExists(String email) throws IOException, SQLException {
        String sql = properties.getProperty("IS_USER_EXISTS");
        try (PreparedStatement statement = connection.prepareStatement(getSql(sql))) {
            // Set user's email for SQL-request
            statement.setString(1, email);
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    String password = set.getString("password");
                    return new User(email, password);
                }
                return null;
            }
        }
    }

    private String hashPassword() {
        throw new UnsupportedOperationException();
    }
}
