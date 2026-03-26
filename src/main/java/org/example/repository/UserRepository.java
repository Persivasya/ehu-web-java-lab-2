package org.example.repository;

import org.example.entity.User;

import java.util.List;

public class UserRepository {
    public static final UserRepository Instance = new UserRepository();

    public int getTotalUsers() {
        return 0;
    }

    public List<User> getAllUsers() {
        return null;
    }

    public User getUser(long id) {
        return null;
    }


    public List<User> getUsersByOrderedCoffee(Long id) {
        return null;
    }

    public User getUserByUserName(String name) {
        return null;
    }

    public User save(User user) {
        return null;
    }
}
