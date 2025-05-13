package service;

import model.User;
import util.DataAccessObject;

import java.util.List;

public class UserService {
    private final DataAccessObject<User> userDao;
    private static final String USER_FILE = "users.txt";

    public UserService(DataAccessObject<User> userDao) {
        this.userDao = userDao;
    }

    public User login(String username, String password) {
        List<User> users = userDao.load(USER_FILE);
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public boolean register(String username, String password, String role) {
        List<User> users = userDao.load(USER_FILE);
        if (users.stream().anyMatch(u -> u.getUsername().equals(username))) {
            return false;
        }
        users.add(new User(username, password, role));
        userDao.save(users, USER_FILE);
        return true;
    }

    public boolean updateUser(String username, String newPassword, String newRole) {
        List<User> users = userDao.load(USER_FILE);
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (!newPassword.isEmpty()) user.setPassword(newPassword);
                if (!newRole.isEmpty()) user.setRole(newRole);
                userDao.save(users, USER_FILE);
                return true;
            }
        }
        return false;
    }

    public boolean deleteUser(String username) {
        List<User> users = userDao.load(USER_FILE);
        boolean removed = users.removeIf(u -> u.getUsername().equals(username));
        if (removed) {
            userDao.save(users, USER_FILE);
        }
        return removed;
    }

    public List<User> getAllUsers() {
        return userDao.load(USER_FILE);
    }
}