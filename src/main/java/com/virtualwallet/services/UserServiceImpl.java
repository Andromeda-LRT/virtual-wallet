package com.virtualwallet.services;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.exceptions.UnusedWalletBalanceException;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
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
        duplicateCheck(userToUpdate);
        return userToUpdate;

    }

    @Override
    public void delete(int id, User loggedUser) {

        verifyUserAccess(loggedUser, id);
        User user = repository.getById(id);
        for (Wallet wallet : user.getWallets()) {
            if (wallet.getBalance() > 0) {
                throw new UnusedWalletBalanceException(wallet.getIban(), String.valueOf(wallet.getBalance()));
            }
        }

        user.setIsArchived(true);
        repository.update(user);
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
        if (!verifyAdminAccess(user) || user.getId() == id) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
        repository.getById(user.getId());
        repository.blockUser(id);
    }

    @Override
    public void unblockUser(int id, User user) {
        if (!verifyAdminAccess(user) || user.getId() == id) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
        repository.getById(user.getId());
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