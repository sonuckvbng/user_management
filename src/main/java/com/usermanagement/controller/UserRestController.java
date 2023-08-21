package com.usermanagement.controller;

import com.usermanagement.binding.ActivateAccount;
import com.usermanagement.binding.Login;
import com.usermanagement.binding.User;
import com.usermanagement.userservice.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserRestController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/user")
    public ResponseEntity<String> createAccountForNewUser(@RequestBody User user){
        boolean isCreated = userService.createAccount(user);
        if(isCreated){
            return new ResponseEntity<>("Registration successfully", HttpStatus.CREATED);
        }else
            return new ResponseEntity<>("Registratiogit n failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activateUserAccount(@RequestBody ActivateAccount activateAccount){
        boolean isActivated = userService.activateUserAccount(activateAccount);
        if (isActivated){
            return new ResponseEntity<>("Account activated successfully", HttpStatus.OK);
        }else
            return new ResponseEntity<>("Invalid Temporary password", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser(){
        List<User> allUsers = userService.getAllUsers();
        return new ResponseEntity<>(allUsers,HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Integer userId){
        User user = userService.getUserById(userId);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Integer userId){
        boolean isDeleted = userService.deleteUserById(userId);
        if (isDeleted){
            return new ResponseEntity<>("User deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("Failed - No user found ", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}/{status}")
    public ResponseEntity<String> changeUserAccountStatus(@PathVariable Integer userId, @PathVariable String status){
        boolean isStatusChanged = userService.changeAccountStatus(userId, status);
        if (isStatusChanged){
            return new ResponseEntity<>("Account status changed successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Failed to change",HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Login login){
        String loginStatus = userService.login(login);
        return  new ResponseEntity<>(loginStatus, HttpStatus.ACCEPTED);
    }

    @GetMapping("/forgotpassword/{emailId}")
    public ResponseEntity<String> forgotPassword(@PathVariable String email){
        String forgotPasswordResponse = userService.forgotPassword(email);
        return new ResponseEntity<>(forgotPasswordResponse,HttpStatus.OK);
    }
}
