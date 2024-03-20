package com.virtualwallet.repositories.contracts;

import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.models.User;

import java.util.List;

public interface UserRepository {

    List<User> getAll();

    List<User> getAllWithFilter(UserModelFilterOptions userFilter);

    User getByStringField(String fieldName, String fieldValue);

    User getById(int id);

    void create(User user);

    void update(User user);

    void delete(int id);

    void blockUser(int id);

    void unblockUser(int id);

    void giveUserAdminRights(User user);

    void removeUserAdminRights(User user);
}
