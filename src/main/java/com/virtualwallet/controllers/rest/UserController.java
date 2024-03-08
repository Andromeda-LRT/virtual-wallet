package com.virtualwallet.controllers.rest;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.model_mappers.CardMapper;
import com.virtualwallet.model_mappers.CardResponseMapper;
import com.virtualwallet.model_mappers.UpdateUserMapper;
import com.virtualwallet.model_mappers.UserMapper;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;
import com.virtualwallet.models.input_model_dto.CardDto;
import com.virtualwallet.models.response_model_dto.CardResponseDto;
import com.virtualwallet.models.input_model_dto.UpdateUserDto;
import com.virtualwallet.models.input_model_dto.UserDto;
import com.virtualwallet.services.contracts.CardService;
import com.virtualwallet.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CardService cardService;
    private final UserMapper userMapper;
    private final UpdateUserMapper updateUserMapper;
    private final CardResponseMapper cardResponseMapper;
    private final CardMapper cardMapper;
    private final AuthenticationHelper authHelper;

    @Autowired
    public UserController(UserService userService,
                          CardService cardService,
                          UserMapper userMapper, UpdateUserMapper updateUserMapper, CardResponseMapper cardResponseMapper,
                          CardMapper cardMapper,
                          AuthenticationHelper authHelper) {
        this.userService = userService;
        this.cardService = cardService;
        this.userMapper = userMapper;
        this.updateUserMapper = updateUserMapper;
        this.cardResponseMapper = cardResponseMapper;
        this.cardMapper = cardMapper;
        this.authHelper = authHelper;
    }


    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader HttpHeaders headers,
                                         @RequestParam(required = false) String phoneNumber,
                                         @RequestParam(required = false) String username,
                                         @RequestParam(required = false) String email,
                                         @RequestParam(required = false) String sortBy,
                                         @RequestParam(required = false) String sortOrder) {
        UserModelFilterOptions userFilter = new UserModelFilterOptions(
                username, email, phoneNumber, sortBy, sortOrder);
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            return ResponseEntity.status(HttpStatus.OK).body(userService.getAllWithFilter(loggedUser, userFilter));
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            User user = userService.get(id, loggedUser);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/cards")
    public ResponseEntity<?> getAllUserCards(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            List<CardResponseDto> cards = cardResponseMapper.toResponseDtoList(
                    cardService.getAllUserCards(userService.get(id, loggedUser))
            );
            return ResponseEntity.status(HttpStatus.OK).body(cards);
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    @GetMapping("/{user_id}/cards/{card_id}")
    public ResponseEntity<?> getUserCard(@RequestHeader HttpHeaders headers,
                                         @PathVariable int user_id,
                                         @PathVariable int card_id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            CardResponseDto cardResponseDto = cardResponseMapper.toResponseDto(
                    cardService.getCard(card_id, loggedUser, user_id)
            );
            return ResponseEntity.status(HttpStatus.OK).body(cardResponseDto);
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            User user = userMapper.fromDto(userDto);
            userService.create(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (DuplicateEntityException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/cards")
    public ResponseEntity<?> createUserCard(@RequestHeader HttpHeaders headers,
                                            @Valid @RequestBody CardDto cardDto) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            Card cardToBeCreated = cardMapper.fromDto(cardDto, loggedUser);
            // TODO Think about the card holder - Logged user or the one from the dto - LYUBIMA
//            cardService.createCard(loggedUser, cardToBeCreated, cardDto.getCardHolder());
            cardService.createCard(loggedUser, cardToBeCreated);
            return ResponseEntity.status(HttpStatus.CREATED).body(cardToBeCreated);
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (DuplicateEntityException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestHeader HttpHeaders headers,
                                        @Valid @RequestBody UpdateUserDto userDto,
                                        @PathVariable int id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            User user = updateUserMapper.fromDto(id, userDto, loggedUser);
            userService.update(user, loggedUser);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DuplicateEntityException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<?> blockUser(@RequestHeader HttpHeaders headers,
                                          @PathVariable int id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            userService.blockUser(id, loggedUser);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<Void> unblockUser(@RequestHeader HttpHeaders headers,
                                            @PathVariable int id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            userService.unblockUser(id, loggedUser);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{user_id}/cards/{card_id}")
    public ResponseEntity<?> updateUserCard(@RequestHeader HttpHeaders headers,
                                            @PathVariable int user_id,
                                            @Valid @RequestBody CardDto cardDto,
                                            @PathVariable int card_id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            Card card = cardMapper.fromDto(cardDto, card_id, loggedUser);
            cardService.updateCard(card, userService.get(user_id, loggedUser));
            CardResponseDto cardResponseDto = cardResponseMapper.toResponseDto(card);
            return ResponseEntity.status(HttpStatus.OK).body(cardResponseDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DuplicateEntityException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            userService.delete(id, loggedUser);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{user_id}/cards/{card_id}")
    public ResponseEntity<?> deleteUserCard(@RequestHeader HttpHeaders headers,
                                               @PathVariable int user_id,
                                               @PathVariable int card_id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            cardService.deleteCard(card_id, userService.get(user_id, loggedUser));
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{user_id}/admin-approval")
    public ResponseEntity<?> giveUserAdminRights(@RequestHeader HttpHeaders headers,
                                                    @PathVariable int user_id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            userService.giveUserAdminRights(userService.get(user_id, loggedUser), loggedUser);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{user_id}/admin-cancellation")
    public ResponseEntity<?> removeUserAdminRights(@RequestHeader HttpHeaders headers,
                                                      @PathVariable int user_id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            userService.removeUserAdminRights(userService.get(user_id, loggedUser), loggedUser);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
