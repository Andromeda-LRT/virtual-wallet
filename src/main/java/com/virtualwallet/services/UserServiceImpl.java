package com.virtualwallet.services;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.UserRepository;
import com.virtualwallet.services.contracts.UserService;
import com.virtualwallet.utils.PasswordEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<User> getAll() {
        return repository.getAll();

    }

    @Override
    public List<User> getAllWithFilter(User user, UserModelFilterOptions userFilter) {
        return repository.getAllWithFilter(user, userFilter);
    }

    @Override
    public User get(int id, User user) {
        verifyUserAccess(user, id);
        return repository.getById(id);
    }

    @Override
    public User getByUsername(String username) {
        return repository.getByStringField(USER_USERNAME, username);
    }

    @Override
    public User getByEmail(String email) {
        return repository.getByStringField(USER_EMAIL, email);
    }

    @Override
    public User getByPhone(String phone) {
        return repository.getByStringField(USER_PHONE_NUMBER, phone);
    }

    @Override
    public void create(User user) {
        duplicateCheck(user);
        user.setPassword(PasswordEncoderUtil.encodePassword(user.getPassword()));
        repository.create(user);
    }

    @Override
    public User update(User userToUpdate, User loggedUser) {
        verifyUserAccess(loggedUser, userToUpdate.getId());
//        Todo change the logic of duplicateCheck(userToUpdate);
//         or implement a new method for checking if the user is trying to update his profile info
//         because it checks for duplicated user at all
//         but not except the user who wants to update
//         his profile info - TEAM
        duplicateCheck(userToUpdate);
        return userToUpdate;

    }

    @Override
    public void delete(int id, User loggedUser) {
        //ToDo if the withdrawing logic is implemented, make it so that a user cannot delete his account if there are funds in it
        verifyUserAccess(loggedUser, id);
        //TODO Check if user exists - TEAM
        repository.delete(id);
        /*
        "message": "could not execute statement [(conn=404)
        Cannot delete or update a parent row: a foreign key constraint fails
        (`virtual_wallet`.`wallets`, CONSTRAINT `wallets_users_user_id_fk` FOREIGN KEY (`created_by`)
        REFERENCES `users` (`user_id`))] [delete from users where user_id=?];
        SQL [delete from users where user_id=?]; constraint [null]",
        */
    }

    @Override
    public void blockUser(int id, User user) {
        if (!verifyAdminAccess(user)) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
        // TODO Check if the user is not trying to block himself - TEAM
        // TODO Check if user exists - TEAM
        repository.blockUser(id);
    }

    @Override
    public void unblockUser(int id, User user) {
        if (!verifyAdminAccess(user)) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
        // TODO Check if the user is not trying to unblock himself - TEAM
        // TODO Check if user exists - TEAM
        repository.unblockUser(id);
    }

    @Override
    public void giveUserAdminRights(User user, User loggedUser) {
        if (!verifyAdminAccess(loggedUser)) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
        if (user.isBlocked()) {
            repository.unblockUser(user.getId());
        }

        repository.giveUserAdminRights(user);
    }

    @Override
    public void removeUserAdminRights(User user, User loggedUser) {
        if (!verifyAdminAccess(loggedUser)) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
        repository.removeUserAdminRights(user);
    }


    @Override
    public boolean verifyAdminAccess(User user) {
        return user.getRole().getName().equals("admin");
    }

    @Override
    public void verifyUserAccess(User loggedUser, int id) {
        if (!verifyAdminAccess(loggedUser) && id != loggedUser.getId()) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
    }

    private void duplicateCheck(User user) {
        boolean duplicateUserNameExists = true;
        boolean duplicateEmailExists = true;
        boolean duplicatePhoneExists = true;
        User userToCheck = null;

        try {
            userToCheck = repository.getByStringField("username", user.getUsername());
        } catch (EntityNotFoundException e) {
            duplicateUserNameExists = false;
        }
        if (duplicateUserNameExists && userToCheck.getId() != user.getId()) {
            throw new DuplicateEntityException("User", "username", user.getUsername());
        }

        try {
            userToCheck = repository.getByStringField("email", user.getEmail());
        } catch (EntityNotFoundException e) {
            duplicateEmailExists = false;
        }
        if (duplicateEmailExists && userToCheck.getId() != user.getId()) {
            throw new DuplicateEntityException("User", "email", user.getEmail());
        }

        try {
            userToCheck = repository.getByStringField("phoneNumber", user.getPhoneNumber());
        } catch (EntityNotFoundException e) {
            duplicatePhoneExists = false;
        }
        if (duplicatePhoneExists && userToCheck.getId() != user.getId()) {
            throw new DuplicateEntityException("User", "phone number", user.getPhoneNumber());
        }

    }
}