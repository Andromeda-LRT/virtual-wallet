package com.virtualwallet.controllers.mvc;

import com.virtualwallet.exceptions.AuthenticationFailureException;
import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_mappers.UserMapper;
import com.virtualwallet.models.User;
import com.virtualwallet.models.mvc_input_model_dto.LoginDto;
import com.virtualwallet.models.mvc_input_model_dto.RegisterDto;
import com.virtualwallet.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;

    @Autowired
    public AuthenticationController(UserService userService,
                                    AuthenticationHelper authenticationHelper,
                                    UserMapper userMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/login")
    public ResponseEntity<?> showLoginPage(Model model) {
        model.addAttribute("login", new LoginDto());
//        return "LoginView";
        return ResponseEntity.status(HttpStatus.OK).body("LoginView");
    }

    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@Valid @ModelAttribute("login") LoginDto dto,
                                         BindingResult bindingResult,
                                         HttpSession session) {
        if (bindingResult.hasErrors()) {
//            return "LoginView";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("LoginView");
        }

        try {
            User user = authenticationHelper.verifyAuthentication(dto.getUsername(), dto.getPassword());
            session.setAttribute("currentUser", dto.getUsername());
            session.setAttribute("isAdmin", userService.verifyAdminAccess(user));
//            return "redirect:/home";
            return ResponseEntity.status(HttpStatus.OK).body("redirect:/home");
        } catch (AuthenticationFailureException e) {
            bindingResult.rejectValue("username", "auth_error", e.getMessage());
//            return "LoginView";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("LoginView");
        }

    }

    @GetMapping("/logout")
    public ResponseEntity<?> handleLogout(HttpSession session) {
        session.removeAttribute("currentUser");
//        return "redirect:/home";
        return ResponseEntity.status(HttpStatus.OK).body("redirect:/home");
    }

    @GetMapping("/register")
    public ResponseEntity<?> showRegisterPage(Model model) {
        model.addAttribute("register", new RegisterDto());
//        return "RegisterView";
        return ResponseEntity.status(HttpStatus.OK).body("RegisterView");
    }


    @PostMapping("/register")
    public ResponseEntity<?> handleRegister(@Valid @ModelAttribute("register") RegisterDto register,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
//            return "RegisterView";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("RegisterView");
        }

        if (!register.getPassword().equals(register.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm",
                    "password_error", "Passwords mismatch.");
//            return "RegisterView";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("RegisterView");
        }

        try {
            User user = userMapper.fromDto(register);
            userService.create(user);
//            return "redirect:/auth/login";
            return ResponseEntity.status(HttpStatus.OK).body("redirect:/auth/login");
        } catch (DuplicateEntityException e) {
            bindingResult.rejectValue("username", "username_error", e.getMessage());
//            return "RegisterView";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("RegisterView");
        }
    }
}
