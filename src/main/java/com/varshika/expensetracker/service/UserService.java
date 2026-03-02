package com.varshika.expensetracker.service;

import com.varshika.expensetracker.model.User;

public interface UserService {

    User saveUser(User user);

    User findByUsername(String username);
}