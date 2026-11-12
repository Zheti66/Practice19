package com.example.practice19;

import java.util.ArrayList;
import java.util.List;

public class UserStaticInfo {
    public static List<User> users = new ArrayList<>();
    public static final String POSITION = "position";

    public UserStaticInfo() {
        if (users.size() == 0) {
            AddUsersInList();
        }
    }

    private void AddUsersInList() {
        users.add(new User("Иван", "Я усталь", 19, 0));
        users.add(new User("Иван", "Я усталь", 19, 1));
        users.add(new User("Иван", "Я усталь", 19, 2));
        users.add(new User("Иван", "Я усталь", 19, 0));
    }
}