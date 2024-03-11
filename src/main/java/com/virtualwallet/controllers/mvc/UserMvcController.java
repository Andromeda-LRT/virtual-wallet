package com.virtualwallet.controllers.mvc;

import com.virtualwallet.exceptions.AuthenticationFailureException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.ExpiredCardException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_mappers.CardMapper;
import com.virtualwallet.model_mappers.UserMapper;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;
import com.virtualwallet.models.input_model_dto.CardDto;
import com.virtualwallet.models.input_model_dto.UserDto;
import com.virtualwallet.services.contracts.CardService;
import com.virtualwallet.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserMvcController {
    private final UserService userService;
    private final CardService cardService;
    private final AuthenticationHelper authenticationHelper;

    private final UserMapper userMapper;

    private final CardMapper cardMapper;

    @Autowired
    public UserMvcController(UserService userService,
                             CardService cardService,
                             AuthenticationHelper authenticationHelper,
                             UserMapper userMapper, CardMapper cardMapper) {
        this.userService = userService;
        this.cardService = cardService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
        this.cardMapper = cardMapper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> showCurrentUserProfile(@PathVariable int id, Model model, HttpSession session) {
        try {
            User loggedUser = authenticationHelper.tryGetUser(session);
            User user = userService.get(id, loggedUser);
            UserDto userDto = userMapper.toDto(user);
            model.addAttribute("user", userDto);
            model.addAttribute("cards", cardService.getAllUserCards(loggedUser));
            return new ResponseEntity<>("ProfileView", HttpStatus.OK);
        } catch (AuthenticationFailureException e) {
//            return "redirect:/auth/login";
            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/auth/login");
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

    @PostMapping("/{id}")
    public ResponseEntity<?> updateUserProfile(@PathVariable int id,
                                               @ModelAttribute("user") UserDto userDto,
                                               BindingResult bindingResult,
                                               Model model,
                                               HttpSession session) {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
//            return "redirect:/auth/login";
            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/auth/login");
        } catch (EntityNotFoundException e) {
//            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "NotFoundView";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NotFoundView");
        }

        if (bindingResult.hasErrors()) {
//            return "ProfileView";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ProfileView");
        }

        try {
            User user = userMapper.fromDto(id, userDto, loggedUser);
            userService.update(user, loggedUser);
//            return "redirect:/users/" + id;
            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/users/" + id);
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

    @GetMapping("/{id}/delete")
    public ResponseEntity<?> deleteUserProfile(@PathVariable int id, Model model, HttpSession session) {
        try {
            User loggedUser = authenticationHelper.tryGetUser(session);
            userService.delete(id, loggedUser);
//            return "redirect:/auth/logout";
            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/auth/logout");
        } catch (AuthenticationFailureException e) {
//            return "redirect:/auth/login";
            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/auth/login");
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

    @GetMapping("/{id}/cards")
    public ResponseEntity<?> showUserCards(@PathVariable int id, Model model, HttpSession session) {
        try {
            User loggedUser = authenticationHelper.tryGetUser(session);
            List<Card> cards = cardService.getAllUserCards(loggedUser);
            model.addAttribute("cards", cards);
//            return "UserCardsView";
            return ResponseEntity.status(HttpStatus.OK).body("UserCardsView");
        } catch (AuthenticationFailureException e) {
            //  return "redirect:/auth/login";
            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/auth/login");
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

    @GetMapping("/{id}/cards/addition")
    public ResponseEntity<?> showAddCardPage(@PathVariable int id, Model model, HttpSession session) {
        try {
            User loggedUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
//            return "redirect:/auth/login";
            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/auth/login");
        } catch (EntityNotFoundException e) {
//            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "NotFoundView";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NotFoundView");
        }

        model.addAttribute("card", new CardDto());
//        return "AddNewCardView";
        return ResponseEntity.status(HttpStatus.OK).body("AddNewCardView");
    }


    @PostMapping("/{id}/cards/addition")
    public ResponseEntity<?> addCard(@PathVariable int id,
                                     @ModelAttribute("card") CardDto cardDto,
                                     BindingResult bindingResult,
                                     Model model,
                                     HttpSession session) {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
//            return "redirect:/auth/login";
            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/auth/login");
        } catch (EntityNotFoundException e) {
//            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "NotFoundView";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NotFoundView");
        }

        if (bindingResult.hasErrors()) {
           // return "AddNewCardView";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("AddNewCardView");
        }

        try {
            Card card = cardMapper.fromDto(cardDto, loggedUser);
            cardService.createCard(loggedUser, card);
//            return "redirect:/users/" + id + "/cards/addition";
            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/users/" + id + "/cards/addition");
        } catch (ExpiredCardException e) {
//            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "AddNewCardView";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("AddNewCardView");
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

    @GetMapping("/{userId}/cards/{cardId}")
    public ResponseEntity<?> showCardDetails(@PathVariable int userId, @PathVariable int cardId, Model model, HttpSession session) {
        try {
            User loggedUser = authenticationHelper.tryGetUser(session);
            Card card = cardService.getCard(cardId, loggedUser, userId);
            model.addAttribute("card", card);
//            return "CardDetailsView";
            return ResponseEntity.status(HttpStatus.OK).body("CardDetailsView");
        } catch (AuthenticationFailureException e) {
//            return "redirect:/auth/login";
            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/auth/login");
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

    @PostMapping("/{id}/cards/{cardId}")
    public ResponseEntity<?> updateCard(@PathVariable int id,
                             @PathVariable int cardId,
                             @ModelAttribute("card") CardDto cardDto,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session) {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
//            return "redirect:/auth/login";
            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/auth/login");
        } catch (EntityNotFoundException e) {
//            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "NotFoundView";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NotFoundView");
        }

        if (bindingResult.hasErrors()) {
//            return "CardDetailsView";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CardDetailsView");
        }

        try {
            Card card = cardMapper.fromDto(cardDto, cardId, loggedUser);
            cardService.updateCard(card, loggedUser);
//            return "redirect:/users/" + id + "/cards/" + cardId;
            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/users/" + id + "/cards/" + cardId);
        } catch (ExpiredCardException e) {
//            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
//            return "AddNewCardView";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("AddNewCardView");
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


    @GetMapping("/{id}/cards/{cardId}/deletion")
    public ResponseEntity<?> deleteCard(@PathVariable int id, @PathVariable int cardId, Model model, HttpSession session) {
        try {
            User loggedUser = authenticationHelper.tryGetUser(session);
            cardService.deleteCard(cardId, loggedUser);
//            return "redirect:/users/" + id + "/cards";
            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/users/" + id + "/cards");
        } catch (AuthenticationFailureException e) {
//            return "redirect:/auth/login";
            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/auth/login");
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
