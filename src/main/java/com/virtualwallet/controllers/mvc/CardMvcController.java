package com.virtualwallet.controllers.mvc;

import com.virtualwallet.exceptions.AuthenticationFailureException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.ExpiredCardException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_mappers.CardMapper;
import com.virtualwallet.model_mappers.CardResponseMapper;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;
import com.virtualwallet.models.input_model_dto.CardDto;
import com.virtualwallet.models.response_model_dto.CardResponseDto;
import com.virtualwallet.services.contracts.CardService;
import com.virtualwallet.utils.AESUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private final CardResponseMapper cardResponseMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public CardMvcController(CardService cardService,
                             CardMapper cardMapper,
                             CardResponseMapper cardResponseMapper,
                             AuthenticationHelper authenticationHelper) {
        this.cardService = cardService;
        this.cardMapper = cardMapper;
        this.cardResponseMapper = cardResponseMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping
    public String showUserCards(HttpSession session, Model model) {
        try {
            User loggedUser = authenticationHelper.tryGetUser(session);
            List<Card> cards = cardService.getAllUserCards(loggedUser);
            List<CardResponseDto> cardResponseDtos = cardResponseMapper.toResponseDtoList(cards);
            model.addAttribute("cards", cardResponseDtos);
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
            return "BadRequestView";
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
            CardDto cardDto = cardMapper.toDto(card);
            cardDto.setNumber(AESUtil.decrypt(cardDto.getNumber()));
            model.addAttribute("card", cardDto);
            model.addAttribute("cardFull", card);
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
        } catch (Exception e) {
            return "BadRequestView";
        }
    }

    @PostMapping("/{cardId}")
    public String updateCard(@PathVariable int cardId,
                             @Valid @ModelAttribute("card") CardDto cardDto,
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
            Card card = cardService.getCard(cardId, loggedUser, loggedUser.getId());
            model.addAttribute("card", cardDto);
            model.addAttribute("cardFull", card);
            return "CardDetailsView";
        }
        try {
            cardDto.setNumber(AESUtil.encrypt(cardDto.getNumber()));
            Card card = cardMapper.fromDto(cardDto, cardId, loggedUser);
            cardService.updateCard(card, loggedUser);
            return "redirect:/cards/" + cardId;
        } catch (ExpiredCardException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "BadRequestView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        }catch (Exception e) {
            return "BadRequestView";
        }
    }

    @GetMapping("/{cardId}/deletion")
    public String deleteCard(@PathVariable int cardId, Model model, HttpSession session) {
        try {
            User loggedUser = authenticationHelper.tryGetUser(session);
            Card card = cardService.getCard(cardId, loggedUser, loggedUser.getId());
            model.addAttribute("cardFull", card);
            cardService.deleteCard(cardId, loggedUser);
            return "redirect:/home";
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
