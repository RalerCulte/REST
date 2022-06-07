package com.lutalic.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lutalic.backend.dao.LuDao;
import com.lutalic.backend.entities.Post;
import com.lutalic.backend.entities.Table;
import com.lutalic.backend.entities.User;


@RestController
@RequestMapping("user")
public class UserController {
    @GetMapping()
    public List<User> allUsers() {
        return null;
    }

    @GetMapping("boards/{email}")
    public List<Table> allBoards(@PathVariable("email") User user) {
        return null;
    }

    @GetMapping("posts/{email}")
    public List<Post> allPosts(@PathVariable("email") User user) {
        return null;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return null;
    }
}
