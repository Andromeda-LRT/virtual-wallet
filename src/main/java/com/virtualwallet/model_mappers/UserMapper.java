package com.virtualwallet.model_mappers;

import com.virtualwallet.models.User;
import com.virtualwallet.models.model_dto.RegisterDto;
import com.virtualwallet.models.model_dto.UserDto;
import com.virtualwallet.repositories.contracts.RoleRepository;
import com.virtualwallet.services.contracts.RoleService;
import com.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
public class UserMapper {

    private final UserService userService;

    private final RoleService roleService;

    @Autowired
    public UserMapper(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    public User fromDto (int id, UserDto userDto, User loggedUser) {
        User user = userService.get(id,loggedUser);
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

    public UserDto toDto(User user){
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    private void toDtoObj(User user, UserDto userDto) {
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setEmail(userDto.getEmail());
        user.setRole(roleService.getRole(1));
    }

    private void toDtoObj(User user, RegisterDto registerDto) {
        user.setUsername(registerDto.getUsername());
        user.setPassword(registerDto.getPassword());
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setPhoneNumber(registerDto.getPhoneNumber());
        user.setEmail(registerDto.getEmail());
    }

    public List<UserDto> toDto(List<User> users) {
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(toDto(user));
        }
        return userDtos;
    }
}
