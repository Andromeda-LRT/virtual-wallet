package com.virtualwallet;

import com.virtualwallet.models.*;
import com.virtualwallet.models.mvc_input_model_dto.UpdateUserPasswordDto;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;

public class Helpers {

    public static Card createMockCard() {
        Card card = new Card();
        card.setId(1);
        card.setNumber("1234567812345678");
        card.setExpirationDate(LocalDateTime.of
                (2030, Month.JANUARY, 31,
                        0, 0, 0));
        card.setCardHolder("Mock User");
        card.setCheckNumber(createMockCheckNumber());
        card.setCardType(createMockCardType());
        card.setArchived(false);
        return card;
    }

    public static CheckNumber createMockCheckNumber() {
        CheckNumber checkNumber = new CheckNumber();
        checkNumber.setId(1);
        checkNumber.setCvv("123");
        return checkNumber;
    }

    public static CardType createMockCardType() {
        CardType cardType = new CardType();
        cardType.setId(1);
        cardType.setType("Credit");
        return cardType;
    }

    public static User createAnotherMockUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("AnotherMockUser");
        user.setPassword("Pass4321!");
        user.setFirstName("Mock");
        user.setLastName("User");
        user.setEmail("mockuser@example.com");
        user.setRole(createAnotherMockRole());
        user.setBlocked(false);
        user.setIsArchived(false);
        user.setPhoneNumber("0898443322");
        user.setProfilePicture(null);
        user.setCards(new HashSet<>());
        return user;
    }

    public static User createMockAdminUser() {
        User user = new User();
        user.setId(5);
        user.setUsername("AnotherMockAdminUser");
        user.setPassword("Pass4321!");
        user.setFirstName("Mock");
        user.setLastName("AdminUser");
        user.setEmail("mockadminuser@example.com");
        user.setRole(createAdminMockRole());
        user.setBlocked(false);
        user.setIsArchived(false);
        user.setPhoneNumber("0798443322");
        user.setProfilePicture(null);
        user.setCards(new HashSet<>());
        return user;
    }

    public static Role createAnotherMockRole () {
        Role role = new Role();
        role.setId(1);
        role.setName("User");
        return role;
    }
    public static Role createAdminMockRole() {
        Role role = new Role();
        role.setId(2);
        role.setName("admin");
        return role;
    }

    public static UpdateUserPasswordDto createMockPasswordDto() {
        UpdateUserPasswordDto passwordDto = new UpdateUserPasswordDto();
        passwordDto.setCurrentPassword("Pass1234!");
        passwordDto.setNewPassword("Pass12345!");
        passwordDto.setConfirmNewPassword("Pass12345!");
        return passwordDto;
    }
}
