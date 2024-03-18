package com.virtualwallet.model_mappers;

import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.mvc_input_model_dto.RegisterDto;
import com.virtualwallet.models.input_model_dto.UserDto;
import com.virtualwallet.models.response_model_dto.WalletUserDto;
import com.virtualwallet.models.response_model_dto.RecipientResponseDto;
import com.virtualwallet.models.response_model_dto.WalletIbanResponseDto;
import com.virtualwallet.services.contracts.RoleService;
import com.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.DEFAULT_PROFILE_IMAGE;
import static com.virtualwallet.model_helpers.ModelConstantHelper.USER_ROLE_ID;

@Component
public class UserMapper {

    private final UserService userService;

    private final RoleService roleService;

    @Autowired
    public UserMapper(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    public User fromDto(int id, UserDto userDto, User loggedUser) {
        User user = userService.get(id, loggedUser);
        toDtoObj(user, userDto);
        return user;
    }


    public User fromDto(RegisterDto dto) {
        User user = new User();
        toDtoObj(user, dto);
        return user;

    }

    public User fromDto(UserDto userDto) {
        User user = new User();
        toDtoObj(user, userDto);
        return user;
    }

    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    private void toDtoObj(User user, UserDto userDto) {
        PopulateUserObj(user, userDto.getUsername(), userDto.getPassword(),
                userDto.getFirstName(), userDto.getLastName(),
                userDto.getPhoneNumber(), userDto.getEmail());
    }

    private void toDtoObj(User user, RegisterDto registerDto) {
        PopulateUserObj(user, registerDto.getUsername(), registerDto.getPassword(),
                registerDto.getFirstName(), registerDto.getLastName(),
                registerDto.getPhoneNumber(), registerDto.getEmail());
    }

    public List<UserDto> toDto(List<User> users) {
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(toDto(user));
        }
        return userDtos;
    }

    public List<RecipientResponseDto> toRecipientDto(List<User> users) {
        List<RecipientResponseDto> recipientList = new ArrayList<>();
        for (User user : users) {
            RecipientResponseDto recipient = new RecipientResponseDto();
            List<WalletIbanResponseDto> walletIbanList = new ArrayList<>();
            recipient.setWalletIban(walletIbanList);
            recipient.setUsername(user.getUsername());
            recipient.setProfilePicture(user.getProfilePicture());
            if (user.getWallets().isEmpty()){
                continue;
            }
            for (Wallet wallet : user.getWallets()) {
                WalletIbanResponseDto walletIban = new WalletIbanResponseDto();
                walletIban.setIban(wallet.getIban());
                recipient.getWalletIban().add(walletIban);
            }
            recipientList.add(recipient);
        }
        return recipientList;
    }

    public List<WalletUserDto> toWalletUserDto(List<User> users) {
        List<WalletUserDto> walletUserList = new ArrayList<>();
        for (User user : users) {
            WalletUserDto walletUser = new WalletUserDto(
                    user.getUsername(), user.getProfilePicture());
            walletUserList.add(walletUser);
        }
        return walletUserList;
    }

    private void PopulateUserObj(User user, String username,
                                 String password, String firstName,
                                 String lastName, String phoneNumber, String email) {
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setRole(roleService.getRole(USER_ROLE_ID));
    }
}
