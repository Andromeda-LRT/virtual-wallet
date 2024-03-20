package com.virtualwallet.services;

import com.virtualwallet.exceptions.*;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.mvc_input_model_dto.UpdateUserPasswordDto;
import com.virtualwallet.repositories.contracts.UserRepository;
import com.virtualwallet.services.contracts.UserService;
import com.virtualwallet.utils.PasswordEncoderUtil;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final WebClient dummyApiWebClient;
    @Value("${api.key}")
    private String key;

    @Autowired
    public UserServiceImpl(UserRepository repository, WebClient dummyApiWebClient) {
        this.repository = repository;
        this.dummyApiWebClient = dummyApiWebClient;
    }

    @Override
    public List<User> getAll() {
        return repository.getAll();

    }

    @Override
    public List<User> getAllWithFilter(User user, UserModelFilterOptions userFilter) {
        return repository.getAllWithFilter(userFilter);
    }

    @Override
    public List<User> getRecipient(UserModelFilterOptions userFilter) {
        return repository.getAllWithFilter(userFilter);
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
        user.setCards(new HashSet<>());
        repository.create(user);
    }

    @Override
    public User update(User userToUpdate, User loggedUser) {
        verifyUserAccess(loggedUser, userToUpdate.getId());
        duplicateCheck(userToUpdate);
        repository.update(userToUpdate);
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

    @Override
    public User verifyUserExistence(int id) {
        return repository.getById(id);
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

    @Override
    public void updateProfilePicture(User user, MultipartFile multipartFile) throws IOException {
        byte[] bytes = multipartFile.getBytes();
        String encodedFile = new String(Base64.encodeBase64(bytes), StandardCharsets.UTF_8);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        populateFormData(formData, encodedFile);
        try {
            String response = processImgUploadRequest(formData);
            JSONObject responseJson = new JSONObject(response);

            if (responseJson.getInt("status") == RESPONSE_STATUS_OK) {
                JSONObject data = responseJson.getJSONObject("data");
                user.setProfilePicture(data.getString("display_url"));
                repository.update(user);
            }
        } catch (WebClientResponseException.BadRequest e) {
            throw new InvalidOperationException("File could not be processed");
        }
    }

    @Override
    public boolean confirmIfPasswordsMatch(int id, UpdateUserPasswordDto passwordDto) {
        User userWhosePasswordMayBeChanged = repository.getById(id);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

//        passwordDto.setCurrentPassword(encoder.encode(passwordDto.getCurrentPassword()));

        return encoder.matches(passwordDto.getCurrentPassword(), userWhosePasswordMayBeChanged.getPassword());
    }

    @Override
    public void isUserBlocked(User user) {
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
        }
    }

    private void populateFormData(MultiValueMap<String, String> formData, String encodedFile) {
        formData.add(UPLOAD_IMG_API_REQUEST_KEY, key);
        // formData.add(EXPIRATION_QUERY_PARAM_REQUEST_KEY, "600");
        formData.add(REQUEST_KEY_IMAGE, encodedFile);
    }

    private String processImgUploadRequest(MultiValueMap<String, String> formData) {
        return dummyApiWebClient.post()
                .uri(IMGBB_IMG_UPLOAD_ENDPOINT_URL)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}