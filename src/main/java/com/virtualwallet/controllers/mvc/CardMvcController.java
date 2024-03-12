package com.virtualwallet.controllers.mvc;

import com.virtualwallet.exceptions.AuthenticationFailureException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.ExpiredCardException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_mappers.CardMapper;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;
import com.virtualwallet.models.input_model_dto.CardDto;
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
@RequestMapping("/cards")
public class CardMvcController {
    private final CardService cardService;
    private final CardMapper cardMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public CardMvcController(CardService cardService, CardMapper cardMapper, AuthenticationHelper authenticationHelper) {
        this.cardService = cardService;
        this.cardMapper = cardMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public String showUserCards(HttpSession session, Model model) {
        try {
            User loggedUser = authenticationHelper.tryGetUser(session);
            List<Card> cards = cardService.getAllUserCards(loggedUser);
            model.addAttribute("cards", cards);
            return "UserCardsView";
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

    @GetMapping("/addition")
    public String showAddCardPage(Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }

        model.addAttribute("card", new CardDto());
        return "AddNewCardView";
    }

    @PostMapping("/addition")
    public String addCard(@ModelAttribute("card") CardDto cardDto,
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
            return "NotFoundView";
        }

        if (bindingResult.hasErrors()) {
            return "AddNewCardView";
        }

        try {
            Card card = cardMapper.fromDto(cardDto, loggedUser);
            cardService.createCard(loggedUser, card);
            return "redirect:/cards";
        } catch (ExpiredCardException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "AddNewCardView";
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

    @GetMapping("/{cardId}")
    public String showCardDetails(@PathVariable int cardId,
                                  Model model,
                                  HttpSession session) {
        try {
            User loggedUser = authenticationHelper.tryGetUser(session);
            Card card = cardService.getCard(cardId, loggedUser, loggedUser.getId());
            model.addAttribute("card", card);
            return "CardDetailsView";
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

    @PostMapping("/{cardId}")
    public String updateCard(@PathVariable int id,
                                        @PathVariable int cardId,
                                        @ModelAttribute("card") CardDto cardDto,
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
            return "NotFoundView";
        }

        if (bindingResult.hasErrors()) {
            return "CardDetailsView";
        }
        try {
            Card card = cardMapper.fromDto(cardDto, cardId, loggedUser);
            cardService.updateCard(card, loggedUser);
            return "redirect:/cards/" + cardId;
        } catch (ExpiredCardException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "CreateCardView";
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

    @GetMapping("/{cardId}/deletion")
    public String deleteCard(@PathVariable int cardId, Model model, HttpSession session) {
        try {
            User loggedUser = authenticationHelper.tryGetUser(session);
            cardService.deleteCard(cardId, loggedUser);
            return "/cards" + cardId;
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
}
