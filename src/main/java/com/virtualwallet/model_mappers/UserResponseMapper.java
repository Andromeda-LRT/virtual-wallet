package com.virtualwallet.model_mappers;

import com.virtualwallet.models.User;
import com.virtualwallet.models.response_model_dto.UserResponseDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserResponseMapper {

    public UserResponseMapper() {
    }

    public UserResponseDto convertToDto(User user){
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setPhoneNumber(user.getPhoneNumber());
        userResponseDto.setProfilePicture(user.getProfilePicture());
        return userResponseDto;
    }

    public List<UserResponseDto> convertToDtoList(List<User> users){
        List<UserResponseDto> userResponseDtoList = new ArrayList<>();
        for (User user : users){
            userResponseDtoList.add(convertToDto(user));
        }
        return userResponseDtoList;
    }
}
