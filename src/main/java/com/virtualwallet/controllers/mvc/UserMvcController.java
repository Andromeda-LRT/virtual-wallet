package com.virtualwallet.controllers.mvc;

import com.virtualwallet.exceptions.*;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_mappers.UpdateUserMapper;
import com.virtualwallet.model_mappers.UserResponseMapper;
import com.virtualwallet.models.User;
import com.virtualwallet.models.input_model_dto.UpdateUserDto;
import com.virtualwallet.models.mvc_input_model_dto.UpdateUserPasswordDto;
import com.virtualwallet.models.response_model_dto.UserResponseDto;
import com.virtualwallet.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

@Controller
@RequestMapping("/users")
public class UserMvcController {
    public static final String WILL_NOT_THROW_ERROR_MESSAGE = "This should never happen," +
            " since file being uploaded is taken from file system";
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserResponseMapper userResponseMapper;
    private final UpdateUserMapper updateUserMapper;


    @Autowired
    public UserMvcController(UserService userService,
                             AuthenticationHelper authenticationHelper,
                             UserResponseMapper userResponseMapper,
                             UpdateUserMapper updateUserMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userResponseMapper = userResponseMapper;
        this.updateUserMapper = updateUserMapper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/userProfile")
    public String showCurrentUserProfile(Model model, HttpSession session) {
        try {
            User loggedUser = authenticationHelper.tryGetUser(session);
            User user = userService.get(loggedUser.getId(), loggedUser);
            setModel(new UpdateUserPasswordDto(), model, loggedUser);
            return "ProfileView";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        }
    }

    @PostMapping("/{id}")
    public String updateUserProfile(@PathVariable int id,
                                    @Valid @ModelAttribute("user") UpdateUserDto userDto,
                                    BindingResult bindingResult,
                                    Model model,
                                    HttpSession session) {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", new UpdateUserPasswordDto());
            return "NotFoundView";
        }

        try {
            User user = updateUserMapper.fromDto(id, userDto, loggedUser);

            if (bindingResult.hasErrors()) {
                UserResponseDto userResponseDto = userResponseMapper.convertToDto(loggedUser);
                model.addAttribute("passwordForm", new UpdateUserPasswordDto());
                model.addAttribute("userFull", loggedUser);
                return "ProfileView";
            }
            userService.update(user, loggedUser);
            return "redirect:/users/userProfile";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        }
    }


    @PostMapping("/picture")
    public String updateUserProfilePicture(@RequestParam("fileImage") MultipartFile multipartFile,
                                           HttpSession session,
                                           Model model) throws IOException {

        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        try {
            userService.updateProfilePicture(loggedUser, multipartFile);
            session.setAttribute("userFull", loggedUser);

            return "redirect:/users/userProfile";
        } catch (FileNotFoundException e) {
            throw new RuntimeException(WILL_NOT_THROW_ERROR_MESSAGE);
        } catch (InvalidOperationException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "BadRequestView";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteUserProfile(@PathVariable int id, Model model, HttpSession session) {
        try {
            User loggedUser = authenticationHelper.tryGetUser(session);
            userService.delete(id, loggedUser);
            return "redirect:/auth/logout";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException | UnusedWalletBalanceException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        }
    }

    @GetMapping("/{id}/password")
    public String showChangePasswordForm(@PathVariable int id, Model model) {
        System.out.println("Adding passwordForm to model");
        model.addAttribute("passwordForm", new UpdateUserPasswordDto());

        return "ProfileView";
    }

    @PostMapping("/{id}/password")
    public String updateUserPassword(@PathVariable int id,
                                     @Valid @ModelAttribute("passwordForm") UpdateUserPasswordDto passwordDto,
                                     BindingResult bindingResult,
                                     HttpSession session,
                                     Model model) {

        User loggedUser;

        try {
            loggedUser = authenticationHelper.tryGetUser(session);
            session.setAttribute("userFull", loggedUser);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        model.addAttribute("userFull", loggedUser);
        try {
            if (!userService.confirmIfPasswordsMatch(id, passwordDto)) {
                setModel(passwordDto, model, loggedUser);
                bindingResult.rejectValue
                        ("currentPassword", "password_error", "Wrong Password");
                return "ProfileView";
            }

            if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
                setModel(passwordDto, model, loggedUser);
                bindingResult.rejectValue
                        ("confirmNewPassword", "password_error", "Passwords mismatch.");
                return "ProfileView";
            }

            if (bindingResult.hasErrors()) {
                setModel(passwordDto, model, loggedUser);
                return "ProfileView";
            }

            User userWhosePasswordWillBeUpdated = updateUserMapper.fromDto(id, passwordDto, loggedUser);
            userService.update(userWhosePasswordWillBeUpdated, loggedUser);
            model.addAttribute("passwordUpdateSuccess", true);
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        }
        return "redirect:/users/userProfile";
    }

    private void setModel(UpdateUserPasswordDto passwordDto, Model model, User loggedUser) {
        UserResponseDto userResponseDto = userResponseMapper.convertToDto(loggedUser);
        model.addAttribute("passwordForm", passwordDto);
        model.addAttribute("userFull", loggedUser);
        model.addAttribute("user", userResponseDto);
    }


}
