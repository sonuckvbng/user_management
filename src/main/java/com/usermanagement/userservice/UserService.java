package com.usermanagement.userservice;

import com.usermanagement.binding.ActivateAccount;
import com.usermanagement.binding.Login;
import com.usermanagement.binding.User;

import java.util.List;

public interface UserService {

    public boolean createAccount(User user);
    public boolean activateUserAccount(ActivateAccount activateAccount);
    public List<User> getAllUsers();

    public User getUserById(Integer userId);

    public User getUserByEmail(String email);
    public boolean deleteUserById(Integer userId);
    public boolean changeAccountStatus(Integer userId, String accountStatus);
    public String login(Login login);
    public String forgotPassword(String email);
}
