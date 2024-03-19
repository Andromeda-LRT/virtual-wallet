package com.virtualwallet.controllers.mvc;

import com.virtualwallet.exceptions.AuthenticationFailureException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.InvalidOperationException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.model_mappers.TransactionResponseMapper;
import com.virtualwallet.model_mappers.UserMapper;
import com.virtualwallet.models.CardToWalletTransaction;
import com.virtualwallet.models.User;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.models.mvc_input_model_dto.TransactionModelFilterDto;
import com.virtualwallet.models.mvc_input_model_dto.UserModelFilterDto;
import com.virtualwallet.models.response_model_dto.TransactionResponseDto;
import com.virtualwallet.services.contracts.CardService;
import com.virtualwallet.services.contracts.IntermediateTransactionService;
import com.virtualwallet.services.contracts.UserService;
import com.virtualwallet.services.contracts.WalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.virtualwallet.utils.UtilHelpers.populateCardTransactionFilterOptions;
import static com.virtualwallet.utils.UtilHelpers.populateWalletTransactionFilterOptions;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final CardService cardService;
    private final WalletService walletService;
    private final IntermediateTransactionService middleTransactionService;
    private final AuthenticationHelper authenticationHelper;
    private final TransactionResponseMapper transactionResponseMapper;
    private final UserMapper userMapper;

    @Autowired
    public AdminController(UserService userService, CardService cardService,
                           WalletService walletService, IntermediateTransactionService middleTransactionService,
                           AuthenticationHelper authenticationHelper, TransactionResponseMapper transactionResponseMapper,
                           UserMapper userMapper) {
        this.userService = userService;
        this.cardService = cardService;
        this.walletService = walletService;
        this.middleTransactionService = middleTransactionService;
        this.authenticationHelper = authenticationHelper;
        this.transactionResponseMapper = transactionResponseMapper;
        this.userMapper = userMapper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping()
    public String showAdminDashboard(HttpSession session, Model model) {

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

        try {
            userService.verifyAdminAccess(loggedUser);
            model.addAttribute("admin", loggedUser);
            return "AdminPanelView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        }
    }

    @GetMapping("/users")
    public String showAllUsers(Model model,
                               @ModelAttribute("userFilterOptions") UserModelFilterDto userModelFilterDto,
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

        UserModelFilterOptions userFilter = new UserModelFilterOptions(
                userModelFilterDto.getUsername(),
                userModelFilterDto.getEmail(),
                userModelFilterDto.getPhoneNumber(),
                userModelFilterDto.getSortBy(),
                userModelFilterDto.getSortOrder()
        );

        try {
            List<User> users = userService.getAllWithFilter(loggedUser, userFilter);
            //TODO: Implement UserMapper FOR USER RESPONSE DTO - LYUBIMA
//            List<UserDto> userDtos = userMapper.toDto(users);
            model.addAttribute("users", users);
            model.addAttribute("userFilterOptions", userModelFilterDto);
            return "AllUsersView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @PostMapping("/users/{id}/block")
    public String blockUser(@PathVariable int id, Model model, HttpSession session) {
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

        try {
            userService.blockUser(id, loggedUser);
            return "redirect:/admin/users";
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

    @PostMapping("/users/{id}/unblock")
    public String unblockUser(@PathVariable int id, Model model, HttpSession session) {
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

        try {
            userService.unblockUser(id, loggedUser);
            return "redirect:/admin/users";
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

    @PostMapping("/users/{id}/admin-approval")
    public String giveUserAdminRights(@PathVariable int id,
                                      HttpSession session,
                                      Model model) {
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

        try {
            User user = userService.get(id, loggedUser);
            userService.giveUserAdminRights(user, loggedUser);
            return "redirect:/admin/users";
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

    @PostMapping("/users/{id}/admin-cancellation")
    public String removeUserAdminRights(@PathVariable int id,
                                        HttpSession session,
                                        Model model) {
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

        try {
            User user = userService.get(id, loggedUser);
            userService.removeUserAdminRights(user, loggedUser);
            return "redirect:/admin/users";
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

    @GetMapping("/cards/transfers")
    public String showCardTransfersPage(Model model,
                                        @ModelAttribute("cardTransactionFilter") TransactionModelFilterDto cardFilterDto,
                                        HttpSession session) {

        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        try {
            CardTransactionModelFilterOptions cardFilter = populateCardTransactionFilterOptions(
                    cardFilterDto);
//            List<CardToWalletTransaction> cardTransactionList =
//                    middleTransactionService.getAllCardTransactionsWithFilter(user, cardFilter);
            List<TransactionResponseDto> cardTransactionResponseList =
                    transactionResponseMapper.convertCardTransactionsToDto(middleTransactionService
                            .getAllCardTransactionsWithFilter(user, cardFilter)
            );
            model.addAttribute("cardTransfersList", cardTransactionResponseList);
            model.addAttribute("cardTransactionFilter", cardFilterDto);
            return "AllCardTransfersView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        } catch (IllegalArgumentException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "BadRequestView";
        } catch (Exception e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            return "BadRequestView";
        }
    }

    @GetMapping("/wallets/transactions")
    public String showWalletTransfersPage(Model model,
                                          @ModelAttribute("walletTransactionFilter")
                                          TransactionModelFilterDto walletFilterDto,
                                          HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        try {
            WalletTransactionModelFilterOptions walletFilter = populateWalletTransactionFilterOptions(walletFilterDto);
//            List<WalletToWalletTransaction> walletTransactions =
//                    middleTransactionService.getAllWithFilter(user, walletFilter);

            List<TransactionResponseDto> walletTransactionList = transactionResponseMapper.convertWalletTransactionsToDto(
                    middleTransactionService.getAllWithFilter(user, walletFilter)
            );
            model.addAttribute("walletTransactionList", walletTransactionList);
            return "AllWalletTransactionsView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        } catch (IllegalArgumentException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "BadRequestView";
        }
    }

    @PostMapping("/transactions/{transactionId}/approval")
    public String approveTransaction(@PathVariable int transactionId,
                                     HttpSession session,
                                     Model model) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            middleTransactionService.approveTransaction(user, transactionId);
            return "redirect:/admin/wallets/transactions";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        } catch (InvalidOperationException e) {
            //todo InvalidOperation is meant to be thrown when transactionId belongs to
            // either a declined or approved in other words already processed transaction
            // in such cases we do not want to do anything to it.
            model.addAttribute("statusCode", HttpStatus.NOT_ACCEPTABLE.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotAcceptableView";
        }
    }

    @PostMapping("/transactions/{transactionId}/cancellation")
    public String cancelTransaction(@PathVariable int transactionId,
                                    HttpSession session,
                                    Model model) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            middleTransactionService.cancelTransaction(user, transactionId);
            return "redirect:/admin/wallets/transactions";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        } catch (InvalidOperationException e) {
            //todo InvalidOperation is meant to be thrown when transactionId belongs to
            // either a declined or approved in other words already processed transaction
            // in such cases we do not want to do anything to it.
            model.addAttribute("statusCode", HttpStatus.NOT_ACCEPTABLE.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotAcceptableView";
        }
    }

}
