package com.virtualwallet.controllers.mvc;

import com.virtualwallet.exceptions.*;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.model_mappers.*;
import com.virtualwallet.models.*;
import com.virtualwallet.models.input_model_dto.TransactionDto;
import com.virtualwallet.models.mvc_input_model_dto.CardMvcTransactionDto;
import com.virtualwallet.models.mvc_input_model_dto.TransactionModelFilterDto;
import com.virtualwallet.models.mvc_input_model_dto.UserModelFilterDto;
import com.virtualwallet.models.response_model_dto.*;
import com.virtualwallet.models.input_model_dto.WalletDto;
import com.virtualwallet.services.contracts.CardService;
import com.virtualwallet.services.contracts.UserService;
import com.virtualwallet.services.contracts.WalletService;
import com.virtualwallet.services.contracts.WalletTypeService;
import com.virtualwallet.utils.UtilHelpers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Limit;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.DUPLICATE_WALLETUSER_ERROR_MESSAGE;
import static com.virtualwallet.model_helpers.ModelConstantHelper.UNAUTHORIZED_OPERATION_ERROR_MESSAGE;
import static java.lang.String.format;

@Controller
@RequestMapping("/wallets")
public class WalletMvcController {
    private final AuthenticationHelper authHelper;
    private final WalletService walletService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final CardService cardService;
    private final WalletTypeService walletTypeService;
    private final WalletMapper walletMapper;
    private final CardResponseMapper cardMapper;
    private final TransactionResponseMapper transactionResponseMapper;
    private final TransactionMapper transactionMapper;

    public WalletMvcController(AuthenticationHelper authHelper,
                               WalletService walletService,
                               UserService userService,
                               UserMapper userMapper,
                               CardService cardService, WalletTypeService walletTypeService,
                               WalletMapper walletMapper, CardResponseMapper cardMapper,
                               TransactionResponseMapper transactionResponseMapper,
                               TransactionMapper transactionMapper) {
        this.authHelper = authHelper;
        this.walletService = walletService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.cardService = cardService;
        this.walletTypeService = walletTypeService;
        this.walletMapper = walletMapper;
        this.cardMapper = cardMapper;
        this.transactionResponseMapper = transactionResponseMapper;
        this.transactionMapper = transactionMapper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping()
    public String showUserWallets(HttpSession session, Model model) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        //todo do we want walletResponseDto?
        List<Wallet> personalWallets = walletService.getAllPersonalWallets(user);
        List<Wallet> joinWallets = walletService.getAllJoinWallets(user);
        model.addAttribute("personalWallets", personalWallets);
        model.addAttribute("joinWallets", joinWallets);
        model.addAttribute("userId", user.getId());
        return "WalletsView";
    }

    @GetMapping("/{id}")
    public String showSingleWallet(@PathVariable int id,
                                   Model model,
                                   HttpSession session) {
        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        try {
            Wallet wallet = walletService.getWalletById(user, id);
            List<User> walletUsers = walletService.getWalletUsers(user, id);
            WalletResponseDto outputWallet = walletMapper.toDto(wallet, walletUsers);
            model.addAttribute("walletId", id);
            model.addAttribute("wallet", outputWallet);
            model.addAttribute("isWalletAdmin", walletService.verifyIfUserIsWalletOwner(user, wallet));
            model.addAttribute("walletAdminId", wallet.getCreatedBy());
            return "WalletView";
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

    @GetMapping("/new")
    public String showCreateWalletPage(Model model, HttpSession session) {
        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        model.addAttribute("newWallet", new WalletDto());
        model.addAttribute("walletTypes", walletTypeService.getAllWalletTypes());
        return "CreateNewWalletView";
    }

    @PostMapping("/new")
    public String createWallet(@ModelAttribute("newWallet") @Valid WalletDto walletDto,
                               BindingResult errors,
                               HttpSession session,
                               Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("walletTypes", walletTypeService.getAllWalletTypes());
            return "CreateNewWalletView";
        }

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet wallet = walletMapper.fromDto(walletDto);
            walletService.createWallet(user, wallet);
            model.addAttribute("walletTypes", walletTypeService.getAllWalletTypes());
            return "redirect:/wallets";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        } catch (LimitReachedException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "BadRequestView";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("walletTypes", walletTypeService.getAllWalletTypes());
            return "CreateNewWalletView";
        }
    }

    @GetMapping("/{id}/update")
    public String showEditWalletPage(@PathVariable int id,
                                     Model model,
                                     HttpSession session) {
        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        Wallet wallet = walletService.getWalletById(user, id);
        model.addAttribute("walletId", id);
        model.addAttribute("wallet", wallet);
        return "UpdateWalletView";
    }

    @PostMapping("/{id}/update")
    public String updateWallet(@PathVariable int id,
                               @Valid @ModelAttribute("wallet") WalletDto walletDto,
                               BindingResult errors,
                               HttpSession session,
                               Model model) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        if (errors.hasErrors()) {
            model.addAttribute("walletId", id);
            model.addAttribute("wallet", walletDto);
            return "UpdateWalletView";
        }
        try {
            Wallet newWallet = walletMapper.fromDto(walletDto, id, user);
            walletService.updateWallet(user, newWallet);
            return "redirect:/wallets/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteWallet(@PathVariable int id, Model model, HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            walletService.delete(user, id);
            return "redirect:/wallets";
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

    @GetMapping("/{wallet_id}/transactions")
    public String showWalletTransactionPage(@PathVariable int wallet_id,
                                            Model model,
                                            @ModelAttribute("walletFilterOptions")
                                            TransactionModelFilterDto transactionFilterDto,
                                            HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        try {
            WalletTransactionModelFilterOptions transactionFilter =
                    populateWalletTransactionFilterOptions(transactionFilterDto);
            List<WalletToWalletTransaction> walletTransactions =
                    walletService.getUserWalletTransactions(transactionFilter, user, wallet_id);
            List<TransactionResponseDto> outputTransactions = transactionResponseMapper
                    .convertWalletTransactionsToDto(walletTransactions);
            model.addAttribute("walletTransactions", outputTransactions);
            model.addAttribute("walletId", wallet_id);
            model.addAttribute("walletFilterOptions", transactionFilterDto);
            return "WalletTransactionsview";
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

    @GetMapping("/{wallet_id}/transfers")
    public String showCardToWalletTransactionsPage(@PathVariable int wallet_id,
                                                   Model model,
                                                   @ModelAttribute("cardFilterOptions")
                                                   TransactionModelFilterDto transactionFilterDto,
                                                   HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            CardTransactionModelFilterOptions transactionFilter =
                    populateCardTransactionFilterOptions(transactionFilterDto);

            List<CardToWalletTransaction> cardTransactions =
                    walletService.getUserCardTransactions(wallet_id, user, transactionFilter);

            List<TransactionResponseDto> outputTransactions = transactionResponseMapper
                    .convertCardTransactionsToDto(cardTransactions);
            model.addAttribute("walletId", wallet_id);
            model.addAttribute("cardTransactions", outputTransactions);
            model.addAttribute("cardFilterOptions", transactionFilterDto);
            return "CardTransactionsView";
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

    @GetMapping("/{wallet_id}/transactions/new")
    public String showCreateTransactionPage(Model model,
                                            @PathVariable int wallet_id,
                                            @ModelAttribute("recipientFilter")
                                            UserModelFilterDto userFilterDto,
                                            HttpSession session) {
        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            userService.isUserBlocked(user);
            UserModelFilterOptions userFilter = populateUserFilterOptions(userFilterDto);
            List<User> userList = walletService.getRecipient(userFilter);
            List<RecipientResponseDto> recipientList = userMapper.toRecipientDto(userList);
            walletService.getWalletById(user, wallet_id);
            //transactionDto.setWalletId(wallet_id);
            model.addAttribute("recipientList", recipientList);
            model.addAttribute("newTransaction", new TransactionDto());
            model.addAttribute("walletId", wallet_id);
            model.addAttribute("recipientFilter", userFilterDto);
            return "CreateNewTransactionVIew";
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

    @PostMapping("/{wallet_id}/transactions/new")
    public String createTransaction(@ModelAttribute("newTransaction") @Valid TransactionDto transactionDto,
                                    BindingResult errors,
                                    @PathVariable int wallet_id,
                                    HttpSession session,
                                    Model model) {


        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        if (errors.hasErrors()) {
            UserModelFilterDto userFilterDto = new UserModelFilterDto();
            UserModelFilterOptions userFilter = populateUserFilterOptions(userFilterDto);
            List<User> userList = walletService.getRecipient(userFilter);
            List<RecipientResponseDto> recipientList = userMapper.toRecipientDto(userList);
            model.addAttribute("recipientList", recipientList);
            model.addAttribute("newTransaction", transactionDto);
            model.addAttribute("walletId", wallet_id);
            model.addAttribute("recipientFilter", new UserModelFilterDto());
            return "CreateNewTransactionVIew";
        }

        try {
            userService.isUserBlocked(user);
            WalletToWalletTransaction walletTransaction = transactionMapper.fromDto(transactionDto, user, wallet_id);
            walletService.walletToWalletTransaction(user, walletTransaction.getWalletId(), walletTransaction);
            return "redirect:/wallets/" + wallet_id + "/transactions";
        } catch (InsufficientFundsException | UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @GetMapping("/{wallet_id}/transfer")
    public String showCreateTransactionWithCardPage(HttpSession session,
                                                    @PathVariable int wallet_id,
                                                    Model model) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet wallet = walletService.getWalletById(user, wallet_id);
            List<Card> cardList = cardService.getAllUserCards(user);
            List<CardResponseDto> cardDtoList = cardMapper.toResponseDtoList(cardList);
            model.addAttribute("walletId", wallet.getWalletId());
            model.addAttribute("cardList", cardDtoList);
            model.addAttribute("cardDto", new CardMvcTransactionDto());
            return "CardTransferView";
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

    @PostMapping("/{wallet_id}/transfer")
    public String createTransactionWithCard(HttpSession session,
                                            @PathVariable int wallet_id,
                                            @ModelAttribute("cardDto") @Valid CardMvcTransactionDto cardDto,
                                            BindingResult errors,
                                            Model model) {
        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }


        if (errors.hasErrors()) {
            List<Card> cardList = cardService.getAllUserCards(user);
            List<CardResponseDto> cardDtoList = cardMapper.toResponseDtoList(cardList);
            model.addAttribute("walletId", wallet_id);
            model.addAttribute("cardList", cardDtoList);
            model.addAttribute("cardDto", cardDto);
            return "CardTransferView";
        }

        try {
            cardService.authorizeCardAccess(cardDto.getCardId(), user);
            CardToWalletTransaction cardTransaction = transactionMapper.fromDto(cardDto);
            walletService.transactionWithCard(user, cardDto.getCardId(), wallet_id, cardTransaction);
            return "redirect:/wallets/" + wallet_id + "/transfers";
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

    @GetMapping("/{wallet_id}/users")
    public String showAddUserToWalletPage(@PathVariable int wallet_id,
                                          @ModelAttribute("walletUser") WalletUserDto walletUserDto,
                                          @ModelAttribute("userWalletFilter") UserModelFilterDto userFilterDto,
                                          HttpSession session,
                                          Model model) {
        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            UserModelFilterOptions userFilter = populateUserFilterOptions(userFilterDto);
            List<User> userList = walletService.getRecipient(userFilter);
            List<WalletUserDto> walletUserList = userMapper.toWalletUserDto(userList);
            Wallet wallet = walletService.getWalletById(user, wallet_id);
            walletService.checkWalletOwnership(user, wallet_id);
            model.addAttribute("userWalletFilter", userFilterDto);
            model.addAttribute("walletUserList", walletUserList);
            model.addAttribute("userToAddToWallet", new WalletUserDto());
            model.addAttribute("currentUserWalletList",
                    userMapper.toWalletUserDto(walletService.getWalletUsers(user, wallet_id)));
            return "AddUserToWalletView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (IllegalArgumentException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "BadRequestView";
        }
    }

    @PostMapping("/{wallet_id}/users")
    public String addUserToWallet(@PathVariable int wallet_id,
                                  @ModelAttribute("walletUser") @Valid WalletUserDto walletUserDto,
                                  BindingResult errors,
                                  HttpSession session,
                                  Model model) {

        if (errors.hasErrors()) {
            return "AddUserToWalletView";
        }

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        try {
            walletService.getWalletById(user, wallet_id);
            User userToAddToWallet = userService.getByUsername(walletUserDto.getUsername());
            walletService.addUserToWallet(user, wallet_id, userToAddToWallet.getId());
            return "redirect:/wallets/" + wallet_id + "/users";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", format(DUPLICATE_WALLETUSER_ERROR_MESSAGE, user.getUsername()));
            return "ConflictView";
        } catch (LimitReachedException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ConflictView";
        }
    }

    @GetMapping("/{wallet_id}/users/{user_id}")
    public String removeUserFromWallet(@PathVariable int wallet_id,
                                       @PathVariable int user_id,
                                       Model model, HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            walletService.removeUserFromWallet(user, wallet_id, user_id);
            return "redirect:/wallets/" + wallet_id;
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

    private UserModelFilterOptions populateUserFilterOptions(UserModelFilterDto dto) {
        return new UserModelFilterOptions(
                dto.getUsername(),
                dto.getEmail(),
                dto.getPhoneNumber(),
                dto.getSortBy(),
                dto.getSortOrder());
    }

    private WalletTransactionModelFilterOptions populateWalletTransactionFilterOptions
            (TransactionModelFilterDto dto) {
        return new WalletTransactionModelFilterOptions(
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getSender(),
                dto.getRecipient(),
                dto.getDirection(),
                dto.getSortBy(),
                dto.getSortOrder()
        );
    }

    private CardTransactionModelFilterOptions populateCardTransactionFilterOptions
            (TransactionModelFilterDto dto) {
        return new CardTransactionModelFilterOptions(
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getSender(),
                dto.getRecipient(),
                dto.getDirection(),
                dto.getSortBy(),
                dto.getSortOrder()
        );
    }
}
