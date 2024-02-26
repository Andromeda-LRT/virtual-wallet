package com.virtualwallet.model_mappers;

import com.virtualwallet.models.User;
import com.virtualwallet.models.model_dto.RegisterDto;
import com.virtualwallet.models.model_dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final UserService userService;

    @Autowired
    public UserMapper(UserService userService) {
        this.userService = userService;
    }

    public User fromDto (int id, UserDto userDto) {
       User user = userService.getById(id);
       toDtoObj(user, userDto);
       return user;
    }


    public User fromDto(RegisterDto dto){
        User user = new User();
        toDtoObj(user, dto);
        return user;

    }

    public User fromDto (UserDto userDto) {
        User user = new User();
        toDtoObj(user, userDto);
        return user;
    }

    private void toDtoObj(User user, UserDto userDto) {
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setEmail(userDto.getEmail());
    }

    private void toDtoObj(User user, RegisterDto registerDto) {
        user.setUsername(registerDto.getUsername());
        user.setPassword(registerDto.getPassword());
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setPhoneNumber(registerDto.getPhoneNumber());
        user.setEmail(registerDto.getEmail());
    }

}
