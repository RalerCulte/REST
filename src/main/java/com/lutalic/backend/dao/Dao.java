package com.lutalic.backend.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.lutalic.backend.entities.Post;
import com.lutalic.backend.entities.Table;
import com.lutalic.backend.entities.User;

public interface Dao extends AutoCloseable {
    /**
     * Upsert new user to database
     */
    void upsertNewUser(User user) throws IOException, SQLException;

    /**
     * Attempt to authorize a user
     */
    void authorization(User user) throws Exception;

    /**
     * Create new table with current user as admin
     */
    void createNewTable(Table table) throws Exception;

    /**
     * Add table, that already exists for current user to database
     */
    void addTableToUser(Table table, User user) throws Exception;

    /**
     * Upsert new post to database
     */
    void upsertPost(Post post) throws Exception;

    /**
     * Update table
     */
    void updateTable(Table table) throws Exception;

    /**
     * Update post
     */
    void updatePost(Post post) throws Exception;

    /**
     * Remove table from database
     */
    void removeTable(Table table) throws Exception;

    /**
     * Remove post from database
     */
    void removePost(Post post) throws Exception;

    /**
     * @return all tables for user
     */
    List<Table> getUserTables(String email) throws Exception;

    /**
     * @return all posts for table
     */
    List<Post> getAllPosts(int parentId) throws Exception;

    /**
     * @return all tables in app
     */
    List<Table> getAllTables() throws Exception;

    /**
     * @return table by id
     */
    Table getTableById(int id) throws Exception;

    /**
     * Read SQL-request from file
     */
    default String getSql(String fileName) throws IOException {
        StringBuilder request = new StringBuilder();
        try (FileReader fileReader = new FileReader(fileName);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String next;
            while ((next = reader.readLine()) != null) {
                request.append(next).append(" ");
            }
        }
        return request.toString();
    }
}
