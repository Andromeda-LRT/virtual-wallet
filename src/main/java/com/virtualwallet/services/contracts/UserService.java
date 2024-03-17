package com.virtualwallet.services.contracts;

import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.models.User;
import com.virtualwallet.models.mvc_input_model_dto.UpdateUserPasswordDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    List<User> getAll();

    List<User> getAllWithFilter(User user, UserModelFilterOptions userFilter);

    List<User> getRecipient(UserModelFilterOptions userFilter);

    User get(int id, User user);

    User getByUsername(String username);

    User getByEmail(String email);

    User getByPhone(String phone);

    void create(User user);

    User update(User userToUpdate, User loggedUser);

    void delete(int id, User loggedUser);

    void blockUser(int id, User user);

    void unblockUser(int id, User user);

    void giveUserAdminRights(User user, User loggedUser);

    void removeUserAdminRights(User user, User loggedUser);

    boolean verifyAdminAccess(User user);

    void verifyUserAccess(User loggedUser, int id);

    User verifyUserExistence(int id);

    void updateProfilePicture(User user, MultipartFile multipartFile) throws IOException;

    boolean confirmIfPasswordsMatch(int id, UpdateUserPasswordDto passwordDto);
}
