package com.virtualwallet.controllers.rest;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.ExpiredCardException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.model_mappers.*;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;
import com.virtualwallet.models.input_model_dto.CardDto;
import com.virtualwallet.models.response_model_dto.CardResponseDto;
import com.virtualwallet.models.input_model_dto.UpdateUserDto;
import com.virtualwallet.models.input_model_dto.UserDto;
import com.virtualwallet.services.contracts.CardService;
import com.virtualwallet.services.contracts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.AUTHORIZATION;
import static com.virtualwallet.model_helpers.SwaggerConstantHelper.*;

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
    private final UserResponseMapper userResponseMapper;

    @Autowired
    public UserController(UserService userService,
                          CardService cardService,
                          UserMapper userMapper,
                          UpdateUserMapper updateUserMapper,
                          CardResponseMapper cardResponseMapper,
                          CardMapper cardMapper,
                          AuthenticationHelper authHelper,
                          UserResponseMapper userResponseMapper) {
        this.userService = userService;
        this.cardService = cardService;
        this.userMapper = userMapper;
        this.updateUserMapper = updateUserMapper;
        this.cardResponseMapper = cardResponseMapper;
        this.cardMapper = cardMapper;
        this.authHelper = authHelper;
        this.userResponseMapper = userResponseMapper;
    }

    @Operation(summary = GET_ALL_USERS_SUMMARY, description = GET_ALL_USERS_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
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
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(userResponseMapper
                            .convertToDtoList(userService.getAllWithFilter(loggedUser, userFilter)));
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(summary = GET_USER_BY_ID_SUMMARY, description = GET_USER_BY_ID_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            User user = userService.get(id, loggedUser);
            return ResponseEntity.status(HttpStatus.OK).body(userResponseMapper.convertToDto(user));
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = GET_ALL_USER_CARDS_SUMMARY, description = GET_ALL_USER_CARDS_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
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

    @Operation(summary = GET_USER_CARD_SUMMARY, description = GET_USER_CARD_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
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

    @Operation(summary = CREATE_USER_SUMMARY, description = CREATE_USER_DESCRIPTION)
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            User user = userMapper.fromDto(userDto);
            userService.create(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponseMapper.convertToDto(user));
        } catch (DuplicateEntityException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = CREATE_USER_CARD_SUMMARY, description = CREATE_USER_CARD_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
    @PostMapping("/cards")
    public ResponseEntity<?> createUserCard(@RequestHeader HttpHeaders headers,
                                            @Valid @RequestBody CardDto cardDto) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            Card cardToBeCreated = cardMapper.fromDto(cardDto, loggedUser);
            Card outputCard = cardService.createCard(loggedUser, cardToBeCreated);
            return ResponseEntity.status(HttpStatus.CREATED).body(outputCard);
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (DuplicateEntityException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (ExpiredCardException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = UPDATE_USER_SUMMARY, description = UPDATE_USER_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestHeader HttpHeaders headers,
                                        @Valid @RequestBody UpdateUserDto userDto,
                                        @PathVariable int id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            User user = updateUserMapper.fromDto(id, userDto, loggedUser);
            userService.update(user, loggedUser);
            return ResponseEntity.status(HttpStatus.OK).body(userResponseMapper.convertToDto(user));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DuplicateEntityException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(summary = BLOCK_USER_SUMMARY, description = BLOCK_USER_DESCRIPTION +
            ONLY_BY_ADMINS)
    @SecurityRequirement(name = AUTHORIZATION)
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

    @Operation(summary = UNBLOCK_USER_SUMMARY, description = UNBLOCK_USER_DESCRIPTION +
            ONLY_BY_ADMINS)
    @SecurityRequirement(name = AUTHORIZATION)
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

    @Operation(summary = UPDATE_USER_CARD_SUMMARY, description = UPDATE_USER_CARD_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
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

    @Operation(summary = DELETE_USER_SUMMARY, description = DELETE_USER_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
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

    @Operation(summary = DELETE_USER_CARD_SUMMARY, description = DELETE_USER_CARD_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
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

    @Operation(summary = GIVE_ADMIN_RIGHTS_SUMMARY, description = GIVE_ADMIN_RIGHTS_DESCRIPTION +
            ONLY_BY_ADMINS)
    @SecurityRequirement(name = AUTHORIZATION)
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

    @Operation(summary = REMOVE_ADMIN_RIGHTS_SUMMARY, description = REMOVE_ADMIN_RIGHTS_DESCRIPTION +
            ONLY_BY_ADMINS)
    @SecurityRequirement(name = AUTHORIZATION)
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
