package com.virtualwallet.controllers;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.model_mappers.UserMapper;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;
import com.virtualwallet.models.model_dto.UserDto;
import com.virtualwallet.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationHelper authHelper;

    public UserController(UserService userService,
                          UserMapper userMapper,
                          AuthenticationHelper authHelper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authHelper = authHelper;
    }

    @GetMapping
    public List<User> getAllUsers(@RequestHeader HttpHeaders headers,
                                  @RequestParam(required = false) String username,
                                  @RequestParam(required = false) String email,
                                  @RequestParam(required = false) String phoneNumber,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) String sortOrder) {
        UserModelFilterOptions userFilter = new UserModelFilterOptions(
                username, email, phoneNumber, sortBy, sortOrder);
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            return userService.getAll(loggedUser, userFilter);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public User getUserById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            return userService.get(id, loggedUser);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/recipient")
    public String getRecipient(@RequestHeader HttpHeaders headers,
                               @RequestParam(required = false) String username,
                               @RequestParam(required = false) String email,
                               @RequestParam(required = false) String phoneNumber) {

        UserModelFilterOptions userFilter = new UserModelFilterOptions(
                username, email, phoneNumber);
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            User recipient = userService.getRecipient(userFilter);
            return recipient.getUsername();
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("{id}/cards")
    public List<Card> getAllUserCards(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            return userService.getUserCards(loggedUser, id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    @GetMapping("/{user_id}/cards/{card_id}")
    public Card getUserCard(@RequestHeader HttpHeaders headers,
                            @PathVariable int user_id,
                            @PathVariable int card_id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            return userService.getCardById(user_id, card_id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public User createUser(@Valid @RequestBody UserDto userDto) {
        try {
            User user = userMapper.fromDto(userDto);
            userService.create(user);
            return user;
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{user_id}/cards/")
    public Card createUserCard(@RequestHeader HttpHeaders headers,
                               @PathVariable int user_id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            return userService.createCard(user_id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public User updateUser(@RequestHeader HttpHeaders headers,
                           @Valid @RequestBody UserDto userDto,
                           @PathVariable int id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            User user = userMapper.fromDto(id, userDto);
            userService.update(user, loggedUser);
            return user;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}/block")
    public void blockUser(@RequestHeader HttpHeaders headers,
                          @PathVariable int id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            userService.blockUser(id, loggedUser);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}/unblock")
    public void unblockUser(@RequestHeader HttpHeaders headers,
                            @PathVariable int id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            userService.unblockUser(id, loggedUser);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{user_id}/cards/{card_id}")
    public Card updateUserCard(@RequestHeader HttpHeaders headers,
                               @PathVariable int user_id,
                               @Valid @RequestBody CardDto cardDto,
                               @PathVariable int card_id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            Card card = cardMapper.fromDto(card_id, cardDto);
            return userService.updateCard(user_id, card);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            userService.delete(id, loggedUser);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{user_id}/cards/{card_id}")
    public void deleteUserCard(@RequestHeader HttpHeaders headers,
                               @PathVariable int user_id,
                               @PathVariable int card_id) {
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            User user = userMapper.fromDto(user_id);
            userService.deleteCard(user, card_id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
