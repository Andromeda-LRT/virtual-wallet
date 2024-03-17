package com.virtualwallet.model_mappers;

import com.virtualwallet.models.User;
import com.virtualwallet.models.input_model_dto.UpdateUserDto;
import com.virtualwallet.models.input_model_dto.UpdateUserPasswordDto;
import com.virtualwallet.services.contracts.UserService;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserMapper {

    private final UserService userService;

    public UpdateUserMapper(UserService userService) {
        this.userService = userService;
    }

    public User fromDto (int id, UpdateUserDto userDto, User loggedUser) {
        User user = userService.get(id,loggedUser);
        toDtoObj(user, userDto);
        return user;
    }

    public User fromDto (int id, UpdateUserPasswordDto userDto, User loggedUser) {
        User user = userService.get(id,loggedUser);
//        if (userDto.getNewPassword().equals(userDto.getConfirmNewPassword())){
            user.setPassword(userDto.getNewPassword());
//        }else {
//
//        }
        return user;
    }

    private void toDtoObj(User user, UpdateUserDto updateUserDto) {
//        user.setPassword(updateUserDto.getPassword());
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setPhoneNumber(updateUserDto.getPhoneNumber());
        user.setEmail(updateUserDto.getEmail());
    }
}
