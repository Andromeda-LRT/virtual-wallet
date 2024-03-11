package com.virtualwallet.controllers.mvc;

import com.virtualwallet.exceptions.AuthenticationFailureException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.model_mappers.UserMapper;
import com.virtualwallet.models.User;
import com.virtualwallet.models.input_model_dto.UserDto;
import com.virtualwallet.models.mvc_input_model_dto.UserModelFilterDto;
import com.virtualwallet.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admins")
public class AdminController {
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;
    @Autowired
    public AdminController(UserService userService, AuthenticationHelper authenticationHelper, UserMapper userMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }

    @GetMapping("/users")
    public ResponseEntity<?> showAllUsers(Model model,
                                          @ModelAttribute("userFilterOptions") UserModelFilterDto userModelFilterDto,
                                          HttpSession session) {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
//            return "redirect:/auth/login";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("redirect:/auth/login");
        } catch (EntityNotFoundException e) {
//            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "NotFoundView";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NotFoundView");
        }

        UserModelFilterOptions userFilter = new UserModelFilterOptions(
                userModelFilterDto.getUsername(),
                userModelFilterDto.getEmail(),
                userModelFilterDto.getPhoneNumber(),
                userModelFilterDto.getSortBy(),
                userModelFilterDto.getSortOrder()
        );

        try {
            List<User> users = userService.getAllWithFilter(loggedUser, userFilter);
            List<UserDto> userDtos = userMapper.toDto(users);
            model.addAttribute("users", userDtos);
            model.addAttribute("userFilterOptions", userFilter);
//            return "AllUsersView";
            return ResponseEntity.status(HttpStatus.OK).body("AllUsersView");
        } catch (EntityNotFoundException e) {
//            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "NotFoundView";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NotFoundView");
        } 
    }

    @GetMapping("/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable int id, Model model, HttpSession session) {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
//            return "redirect:/auth/login";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("redirect:/auth/login");
        }catch (EntityNotFoundException e) {
//            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "NotFoundView";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NotFoundView");
        }

        try {
            userService.blockUser(id, loggedUser);
//            return "redirect:/admins/users";
            return ResponseEntity.status(HttpStatus.OK).body("redirect:/admins/users");
        } catch (EntityNotFoundException e) {
//            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "NotFoundView";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NotFoundView");
        } catch (UnauthorizedOperationException e) {
//            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "UnauthorizedView";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UnauthorizedView");
        }
    }

    @GetMapping("/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable int id, Model model, HttpSession session) {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
//            return "redirect:/auth/login";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("redirect:/auth/login");
        }catch (EntityNotFoundException e) {
//            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "NotFoundView";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NotFoundView");
        }

        try {
            userService.unblockUser(id, loggedUser);
//            return "redirect:/admins/users";
            return ResponseEntity.status(HttpStatus.OK).body("redirect:/admins/users");
        } catch (EntityNotFoundException e) {
//            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "NotFoundView";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NotFoundView");
        } catch (UnauthorizedOperationException e) {
//            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "UnauthorizedView";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UnauthorizedView");
        }
    }

    @PostMapping("/users/{id}/admin-approval")
    public ResponseEntity<?> giveUserAdminRights(@PathVariable int id,
                                      HttpSession session,
                                      Model model) {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
//            return "redirect:/auth/login";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("redirect:/auth/login");
        }catch (EntityNotFoundException e) {
//            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "NotFoundView";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NotFoundView");
        }

        try {
            User user = userService.get(id, loggedUser);
            userService.giveUserAdminRights(user, loggedUser);
//            return "redirect:/admin/users";
            return ResponseEntity.status(HttpStatus.OK).body("redirect:/admin/users");
        } catch (EntityNotFoundException e) {
//            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "NotFoundView";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NotFoundView");
        } catch (UnauthorizedOperationException e) {
//            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "UnauthorizedView";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UnauthorizedView");
        }
    }
    @PostMapping("/users/{id}/admin-cancellation")
    public ResponseEntity<?> removeUserAdminRights(@PathVariable int id,
                                      HttpSession session,
                                      Model model) {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
//            return "redirect:/auth/login";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("redirect:/auth/login");
        }catch (EntityNotFoundException e) {
//            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "NotFoundView";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NotFoundView");
        }

        try {
            User user = userService.get(id, loggedUser);
            userService.removeUserAdminRights(user, loggedUser);
//            return "redirect:/admin/users";
            return ResponseEntity.status(HttpStatus.OK).body("redirect:/admin/users");
        } catch (EntityNotFoundException e) {
//            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "NotFoundView";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NotFoundView");
        } catch (UnauthorizedOperationException e) {
//            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "UnauthorizedView";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UnauthorizedView");
        }
    }
}
