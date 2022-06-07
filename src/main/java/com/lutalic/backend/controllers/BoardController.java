package com.lutalic.backend.controllers;

import java.io.StringWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lutalic.backend.dao.LuDao;
import com.lutalic.backend.entities.Post;
import com.lutalic.backend.entities.Table;

@RestController
@RequestMapping("boards")
public class BoardController {
    private final LuDao dao = LuDao.getDao();

    @GetMapping()
    public List<Table> allBoards() throws Exception {
        return dao.getAllTables();
    }

    @GetMapping("{id}")
    public Table getBoardById(@PathVariable("tableId") int id) {

    }

    @GetMapping("posts/{tableId}")
    public List<Post> allPostsForTable(@PathVariable("tableId") int id) throws Exception {
        return dao.getAllPosts(id);
    }

    @GetMapping("user/{user_email}")
    public List<Table> allTablesForUser(@PathVariable("user_email") String email) throws Exception {
        return dao.getUserTables(email);
    }
}
