package com.virtualwallet.services;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.models.User;
import com.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<User> getAll(User user, UserModelFilterOptions userFilter) {
        if (verifyAdminAccess(user)) {
            return repository.getAll(userFilter);
        }
        throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
    }

    @Override
    public User get(int id, User user) {
        verifyUserAccess(user, id);
        return repository.get(id);
    }

    @Override
    public User getByUsername(String username) {
        return repository.getByUsername(username);
    }

    @Override
    public User getByEmail(String email) {
        return repository.getByEmail(email);
    }

    @Override
    public User getByPhone(String phone) {
        return repository.getByPhone(phone);
    }

    @Override
    public void create(User user) {
        boolean duplicateUserNameExists = true;
        boolean duplicateEmailExists = true;
        boolean duplicatePhoneExists = true;

        try {
            repository.getByUsername(user.getUsername());
        } catch (EntityNotFoundException e) {
            duplicateUserNameExists = false;
        }
        if (duplicateUserNameExists) {
            throw new DuplicateEntityException("User", "username", user.getUsername());
        }

        try {
            repository.getByEmail(user.getEmail());
        } catch (EntityNotFoundException e) {
            duplicateEmailExists = false;
        }
        if (duplicateEmailExists) {
            throw new DuplicateEntityException("User", "email", user.getEmail());
        }

        try {
            repository.getByPhone(user.getPhoneNumber());
        } catch (EntityNotFoundException e) {
            duplicatePhoneExists = false;
        }
        if (duplicatePhoneExists) {
            throw new DuplicateEntityException("User", "phone number", user.getPhoneNumber());
        }
        repository.create(user);
    }

    @Override
    public User update(User userToUpdate, User loggedUser) {
        verifyUserAccess(loggedUser, userToUpdate.getId());
        if (repository.emailExists(userToUpdate)) {
            throw new DuplicateEntityException("User", "email", loggedUser.getEmail());
        }
        if (repository.phoneExists(userToUpdate)) {
            throw new DuplicateEntityException("User", "phone", loggedUser.getPhoneNumber());
        }
        if (repository.usernameExists(userToUpdate)) {
            throw new DuplicateEntityException("User", "username", loggedUser.getUsername());
        }

        return repository.update(userToUpdate);
    }

    @Override
    public void delete(int id, User loggedUser) {
        verifyUserAccess(loggedUser, id);
        repository.delete(id);
    }

    @Override
    public void blockUser(int id, User user) {
        if (!verifyAdminAccess(user)){
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
        repository.blockUser(id);
    }

    @Override
    public void unblockUser(int id, User user) {
        if (!verifyAdminAccess(user)){
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
        repository.unblockUser(id);
    }

    @Override
    public void giveUserAdminRights(User user, User loggedUser) {
        if (!verifyAdminAccess(loggedUser)){
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
        if (user.isBlocked()) {
            repository.unblockUser(user.getId());
        }

        repository.giveUserAdminRights(user);
    }

    @Override
    public void removeUserAdminRights(User user, User loggedUser) {
        if (!verifyAdminAccess(loggedUser)){
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
        repository.removeUserAdminRights(user);
    }


    @Override
    public boolean verifyAdminAccess(User user) {
        return repository.checkIfAdmin(user);
    }

    @Override
    public void verifyUserAccess(User loggedUser, int id) {
        if (!verifyAdminAccess(loggedUser) && id != loggedUser.getId()) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
    }
}
