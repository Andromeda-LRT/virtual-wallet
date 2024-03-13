package com.virtualwallet.controllers.mvc;

import com.virtualwallet.exceptions.AuthenticationFailureException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.InsufficientFundsException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.model_mappers.TransactionMapper;
import com.virtualwallet.model_mappers.TransactionResponseMapper;
import com.virtualwallet.model_mappers.UserMapper;
import com.virtualwallet.model_mappers.WalletMapper;
import com.virtualwallet.models.*;
import com.virtualwallet.models.input_model_dto.CardTransactionDto;
import com.virtualwallet.models.input_model_dto.TransactionDto;
import com.virtualwallet.models.mvc_input_model_dto.TransactionModelFilterDto;
import com.virtualwallet.models.mvc_input_model_dto.UserModelFilterDto;
import com.virtualwallet.models.response_model_dto.RecipientResponseDto;
import com.virtualwallet.models.response_model_dto.TransactionResponseDto;
import com.virtualwallet.models.input_model_dto.WalletDto;
import com.virtualwallet.services.contracts.CardService;
import com.virtualwallet.services.contracts.UserService;
import com.virtualwallet.services.contracts.WalletService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/wallets")
public class WalletMvcController {
    private final AuthenticationHelper authHelper;
    private final WalletService walletService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final CardService cardService;
    private final WalletMapper walletMapper;
    private final TransactionResponseMapper transactionResponseMapper;
    private final TransactionMapper transactionMapper;

    public WalletMvcController(AuthenticationHelper authHelper,
                               WalletService walletService,
                               UserService userService,
                               UserMapper userMapper,
                               CardService cardService,
                               WalletMapper walletMapper,
                               TransactionResponseMapper transactionResponseMapper,
                               TransactionMapper transactionMapper) {
        this.authHelper = authHelper;
        this.walletService = walletService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.cardService = cardService;
        this.walletMapper = walletMapper;
        this.transactionResponseMapper = transactionResponseMapper;
        this.transactionMapper = transactionMapper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
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
        List<Wallet> wallets = walletService.getAllWallets(user);
        model.addAttribute("wallets", wallets);
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

            model.addAttribute("walletId", id);
            model.addAttribute("wallet", wallet);
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
        return "CreateNewWalletView";
    }

    @PostMapping("/new")
    public String createWallet(@ModelAttribute("newWallet") @Valid WalletDto walletDto,
                               BindingResult errors,
                               HttpSession session,
                               Model model) {
        if (errors.hasErrors()) {
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
            return "redirect:/wallets";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
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
                    walletService.getAllWalletTransactionsWithFilter(transactionFilter, user, wallet_id);
            List<TransactionResponseDto> outputTransactions = transactionResponseMapper
                    .convertToDto(walletTransactions, wallet_id);
            model.addAttribute("walletTransactions", outputTransactions);
            model.addAttribute("walletFilterOptions", transactionFilter);
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

    @GetMapping("{wallet_id}/transfers")
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
            //todo getAllCardTransaction should work with filterOttions
//            List<CardToWalletTransaction> cardTransactions =
//                    walletService.getAllCardTransactions(transactionFilter, user, wallet_id);
            //todo transactionMapper to return CardTransactionResponseDto
//            List<CardTransactionResponseDto> outputTransactions = transactionResponseMapper
//                    .convertToDto(walletTransactions, wallet_id);
            //model.addAttribute("cardTransactions", outputTransactions);
            model.addAttribute("cardFilterOptions", transactionFilter);
            return "CardTransactionsview";
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

    @GetMapping("/{wallet_id}/transactions/new")
    public String showCreateTransactionPage(Model model,
                                            @PathVariable int wallet_id,
                                            @ModelAttribute("transactionFilter")
                                            UserModelFilterDto userFilterDto,
                                            HttpSession session) {
        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            UserModelFilterOptions userFilter = populateUserFilterOptions(userFilterDto);
            List<User> userList = walletService.getRecipient(userFilter);
            List<RecipientResponseDto> recipientList = userMapper.toRecipientDto(userList);
            walletService.getWalletById(user, wallet_id);
            TransactionDto transactionDto = new TransactionDto();
            //transactionDto.setWalletId(wallet_id);
            model.addAttribute("recipient", recipientList);
            model.addAttribute("newTransaction", transactionDto);
            model.addAttribute("walletId", wallet_id);
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
                                    @PathVariable int wallet_id,
                                    BindingResult errors,
                                    HttpSession session,
                                    Model model) {
        if (errors.hasErrors()) {
            return "CreateNewTransactionVIew";
        }

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            WalletToWalletTransaction walletTransaction = transactionMapper.fromDto(transactionDto, user, wallet_id);
            walletService.walletToWalletTransaction(user, walletTransaction.getWalletId(), walletTransaction);
            return "redirect:/wallets/" + wallet_id + "transactions";
        } catch (InsufficientFundsException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
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
            model.addAttribute("walletId", wallet.getWalletId());
            model.addAttribute("cardList", cardList);
            model.addAttribute("cardDto", new CardTransactionDto());
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

    @PostMapping("/{wallet_id}/transfer/{card_id}")
    public String createTransactionWithCard(HttpSession session,
                                            @PathVariable int wallet_id,
                                            @PathVariable int card_id,
                                            @ModelAttribute("cardDto") @Valid CardTransactionDto cardDto,
                                            BindingResult errors,
                                            Model model) {

        if (errors.hasErrors()) {
            return "CardTransferView";
        }

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        try {
            cardService.authorizeCardAccess(card_id, user);
            CardToWalletTransaction cardTransaction = transactionMapper.fromDto(cardDto);
            walletService.transactionWithCard(user, card_id, wallet_id, cardTransaction);
            return "CardTransactionsView";
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
