package com.usermanagement.userservice;

import com.usermanagement.binding.ActivateAccount;
import com.usermanagement.binding.Login;
import com.usermanagement.binding.User;
import com.usermanagement.entity.UserMaster;
import com.usermanagement.repo.UserMasterRepo;
import com.usermanagement.EmailUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    static final String ACTIVE="Active";
    static final String IN_ACTIVE="In-Active";
    static final String REGISTRATION_BODY_FILE= "src/main/java/com/usermanagement/REG-EMAIL-BODY.txt";
    static final String FORGOT_PASSWORD_BODY_FILE= "FORGOT-EMAIL-BODY.txt";

    @Autowired
    private UserMasterRepo userMasterRepo;

    @Autowired
    private EmailUtils emailUtils;

    @Override
    public boolean createAccount(User user) {
        UserMaster userMaster = new UserMaster();
        BeanUtils.copyProperties(user,userMaster);
        userMaster.setPassword(generateTempPassword());
        userMaster.setAccountStatus(IN_ACTIVE);

        UserMaster save = userMasterRepo.save(userMaster);

        String mailBody = readRegEmailBody(userMaster.getFullName(), userMaster.getPassword(),REGISTRATION_BODY_FILE);
        //TODO: Send email with account activation link to activated.
//        emailUtils.sendEmail(user.getEmail(),"Activate account", mailBody);

        return save.getUserId()!=null;
    }

    @Override
    public boolean activateUserAccount(ActivateAccount activateAccount) {

        // creating object for query data in database
        UserMaster userMaster= new UserMaster();
        userMaster.setEmail(activateAccount.getEmail());
        userMaster.setPassword(activateAccount.getTempPassword());

        //query database using Email and temporary password
        Example<UserMaster> of = Example.of(userMaster);
        List<UserMaster> findAll = userMasterRepo.findAll(of);

        //checking if findAll contain user or not if contain we will process further
        if (findAll.isEmpty()){
            return false;
        }else {

            //updating existing new user with new password and account status - Active
            UserMaster updateUserMaster = findAll.get(0);
            updateUserMaster.setPassword(activateAccount.getNewPassword());
            updateUserMaster.setAccountStatus(ACTIVE);
            userMasterRepo.save(updateUserMaster);
            return true;
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<UserMaster>  userMasterList = userMasterRepo.findAll();
        List<User> userList = new ArrayList<>();

        for (UserMaster userMaster : userMasterList){
            User user = new User();
            BeanUtils.copyProperties(userMaster,user);
            userList.add(user);
        }
        return userList;
    }

    @Override
    public User getUserById(Integer userId) {
        Optional<UserMaster> userMaster = userMasterRepo.findById(userId);
        if (userMaster.isPresent()){
            User user = new User();
            BeanUtils.copyProperties(userMaster.get(),user);
        return user;
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        UserMaster userByEmailId = userMasterRepo.findByEmail(email);
        User user = new User();
        BeanUtils.copyProperties(userByEmailId, user);
        return user;
    }

    @Override
    public boolean deleteUserById(Integer userId) {
        try {
            userMasterRepo.deleteById(userId);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean changeAccountStatus(Integer userId, String accountStatus) {
        Optional<UserMaster> userById = userMasterRepo.findById(userId);
        if (userById.isPresent()){
            UserMaster userMaster = userById.get();
            userMaster.setAccountStatus(accountStatus);
            userMasterRepo.save(userMaster);
            return true;
        }
        return false;
    }

    @Override
    public String login(Login login) {
//        UserMaster entity= new UserMaster();
//        entity.setEmail(login.getEmail());
//        entity.setPassword(login.getPassword());
//
//        Example<UserMaster> of = Example.of(entity);
//        List<UserMaster> userMasterList = userMasterRepo.findAll(of);

        UserMaster userMaster = userMasterRepo.findByEmailAndPassword(login.getEmail(), login.getPassword());

        if (userMaster==null){
            return "Invalid User Credential";
        }else {
            if (userMaster.getAccountStatus().equals(ACTIVE)){
                return "Success";
            }else {
                return "Account is not Activate, please activate account";
            }
        }
    }

    @Override
    public String forgotPassword(String email) {
        UserMaster userMaster = userMasterRepo.findByEmail(email);

        if (userMaster == null){
            return "Invalid User";
        }
        boolean isMailSend = emailUtils.sendEmail(email, "forget password", FORGOT_PASSWORD_BODY_FILE);

        if (isMailSend){
            return "Password sent to your registered mail id ";
        }
        return "Invalid Email ID";
    }

    private String generateTempPassword(){
            int l=8;
            String AlphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";
            StringBuilder s = new StringBuilder(l);
            for (int i=0; i<l; i++) {
                int ch = (int)(AlphaNumericStr.length() * Math.random());
                s.append(AlphaNumericStr.charAt(ch));
            }
            return s.toString();
    }

    private String readRegEmailBody(String fullName, String tempPassword, String fileName){
        String mailBody="";
        String activateAccountURL="";
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while(line!=null){
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            mailBody = stringBuilder.toString();
            mailBody=mailBody.replace("{FullName}",fullName);
            mailBody=mailBody.replace("{tempPassword}",tempPassword);
            mailBody=mailBody.replace("{url}", activateAccountURL);
            mailBody=mailBody.replace("{password}",tempPassword);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mailBody;
    }


}
