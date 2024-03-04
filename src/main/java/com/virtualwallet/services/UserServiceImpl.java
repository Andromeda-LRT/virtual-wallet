package com.virtualwallet.services;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.UserRepository;
import com.virtualwallet.services.contracts.UserService;
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
        //ToDo if the withdrawing logic is implemented, make it so that a user cannot delete his account if there are funds in it
        verifyUserAccess(loggedUser, id);
        repository.delete(id);
    }

    @Override
    public void blockUser(int id, User user) {
        if (!verifyAdminAccess(user)) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
        repository.blockUser(id);
    }

    @Override
    public void unblockUser(int id, User user) {
        if (!verifyAdminAccess(user)) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
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

        try {
            repository.getByStringField("username", user.getUsername());
        } catch (EntityNotFoundException e) {
            duplicateUserNameExists = false;
        }
        if (duplicateUserNameExists) {
            throw new DuplicateEntityException("User", "username", user.getUsername());
        }

        try {
            repository.getByStringField("email", user.getEmail());
        } catch (EntityNotFoundException e) {
            duplicateEmailExists = false;
        }
        if (duplicateEmailExists) {
            throw new DuplicateEntityException("User", "email", user.getEmail());
        }

        try {
            repository.getByStringField("phoneNumber", user.getPhoneNumber());
        } catch (EntityNotFoundException e) {
            duplicatePhoneExists = false;
        }
        if (duplicatePhoneExists) {
            throw new DuplicateEntityException("User", "phone number", user.getPhoneNumber());
        }

    }
}