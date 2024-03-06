package com.virtualwallet.controllers.rest;

import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.InsufficientFundsException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.model_mappers.TransactionMapper;
import com.virtualwallet.model_mappers.TransactionResponseMapper;
import com.virtualwallet.model_mappers.WalletMapper;
import com.virtualwallet.models.CardToWalletTransaction;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.model_dto.CardTransactionDto;
import com.virtualwallet.models.model_dto.TransactionResponseDto;
import com.virtualwallet.services.contracts.UserService;
import com.virtualwallet.services.contracts.WalletService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.virtualwallet.models.model_dto.WalletDto;
import com.virtualwallet.models.model_dto.TransactionDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    private final WalletService walletService;
    private final WalletMapper walletMapper;
    private final UserService userService;
    private final AuthenticationHelper authHelper;
    private final TransactionResponseMapper transactionResponseMapper;
    private final TransactionMapper transactionMapper;

    public WalletController(UserService userService,
                            AuthenticationHelper authHelper,
                            WalletService walletService,
                            WalletMapper walletMapper,
                            TransactionResponseMapper transactionResponseMapper,
                            TransactionMapper transactionMapper) {
        this.userService = userService;
        this.authHelper = authHelper;
        this.walletService = walletService;
        this.walletMapper = walletMapper;
        this.transactionResponseMapper = transactionResponseMapper;
        this.transactionMapper = transactionMapper;
    }

    @GetMapping
    public ResponseEntity<?> getAllWallets(@RequestHeader HttpHeaders headers) {
        try {
            User user = authHelper.tryGetUser(headers);
            List<Wallet> walletList = walletService.getAllWallets(user);
            return ResponseEntity.status(HttpStatus.OK).body(walletList);
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWalletById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authHelper.tryGetUser(headers);
            Wallet wallet = walletService.getWalletById(user, id);
            return ResponseEntity.status(HttpStatus.OK).body(wallet);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<?> createWallet(@RequestHeader HttpHeaders headers,
                                          @RequestBody @Valid WalletDto walletDto) {
        try {
            User user = authHelper.tryGetUser(headers);
            Wallet wallet = walletMapper.fromDto(walletDto);
            walletService.createWallet(user, wallet);
            return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWallet(@RequestHeader HttpHeaders headers,
                                          @RequestBody @Valid WalletDto walletDto,
                                          @PathVariable int id) {
        try {
            User user = authHelper.tryGetUser(headers);
            // TODO Check why -> walletMapper.fromDto(walletDto, id);
            //  is not working when updating wallet - TEAM
            Wallet wallet = walletMapper.fromDto(walletDto, id);
            walletService.updateWallet(user, wallet);
            return ResponseEntity.status(HttpStatus.OK).body(wallet);
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authHelper.tryGetUser(headers);
            walletService.delete(user, id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{wallet_id}/transactions")
    public ResponseEntity<?> getWalletTransactionHistory(@RequestHeader HttpHeaders headers,
                                                                       @PathVariable int wallet_id,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime  startDate,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                                       @RequestParam(required = false) String sender,
                                                                       @RequestParam(required = false) String recipient,
                                                                       @RequestParam(required = false) String direction,
                                                                       @RequestParam(required = false) String sortBy,
                                                                       @RequestParam(required = false) String sortOrder) {
        try {
            WalletTransactionModelFilterOptions transactionModelFilterOptions = new WalletTransactionModelFilterOptions(
                    startDate, endDate, sender, recipient, direction, sortBy, sortOrder);

            User user = authHelper.tryGetUser(headers);
            List<WalletToWalletTransaction> walletToWalletTransactionList =
                    walletService.getAllWalletTransactionsWithFilter(transactionModelFilterOptions, user, wallet_id);
//            return transactionResponseMapper.convertToDto(walletToWalletTransactionList, wallet_id);
            return ResponseEntity.status(HttpStatus.OK).body(walletToWalletTransactionList);
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    }

//    @GetMapping("/transactions")
//    public List<TransactionResponseDto> getTransactionHistory(@RequestHeader HttpHeaders headers) {
//        try {
//            User user = authHelper.tryGetUser(headers);
//            List<WalletToWalletTransaction> walletToWalletTransactionList = walletService.getAllWalletTransactions(user);
//            return transactionResponseMapper.convertToDto(walletToWalletTransactionList, wallet_id);
//        } catch (UnauthorizedOperationException e) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
//        }
//    }

    @GetMapping("/{wallet_id}/transactions/{transaction_id}")
    public ResponseEntity<?> getTransactionById(@RequestHeader HttpHeaders headers,
                                                @PathVariable int wallet_id,
                                                @PathVariable int transaction_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            WalletToWalletTransaction walletToWalletTransaction = walletService.getTransactionById(user, wallet_id, transaction_id);
            TransactionResponseDto transaction = transactionResponseMapper.convertToDto(walletToWalletTransaction);
            return ResponseEntity.status(HttpStatus.OK).body(transaction);
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{wallet_id}/transactions")
    public ResponseEntity<?> creteTransaction(@RequestHeader HttpHeaders headers,
                                              @PathVariable int wallet_id,
                                              @RequestBody @Valid TransactionDto transactionDto) {
        try {
            User user = authHelper.tryGetUser(headers);
            WalletToWalletTransaction walletToWalletTransaction = transactionMapper.fromDto(transactionDto, user);
            walletService.walletToWalletTransaction(user, wallet_id, walletToWalletTransaction);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(transactionResponseMapper.convertToDto(walletToWalletTransaction));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InsufficientFundsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

//    @PutMapping("/{wallet_id}/transactions/{transaction_id}/update")
//    public TransactionResponseDto updateTransaction(@RequestHeader HttpHeaders headers,
//                                            @PathVariable int wallet_id,
//                                            @RequestBody @Valid TransactionDto transactionDto,
//                                            @PathVariable int transaction_id) {
//        try {
//            User user = authHelper.tryGetUser(headers);
//            Transaction transaction = transactionMapper.fromDto(transactionDto, transaction_id);
//            walletService.updateTransaction(user, transaction, wallet_id);
//            return transactionResponseMapper.convertToDto(transaction);
//        } catch (UnauthorizedOperationException e) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
//        } catch (EntityNotFoundException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        } catch(InsufficientFundsException e) {
    //throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
//}
//        //Only a transaction that has not been approved can be updated
//    }

    //TODO remove at the end

    @PutMapping("/{wallet_id}/transactions/{transaction_id}/approve")
    public ResponseEntity<Void> approveTransaction(@RequestHeader HttpHeaders headers,
                                                   @PathVariable int wallet_id,
                                                   @PathVariable int transaction_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            walletService.approveTransaction(user, transaction_id, wallet_id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PutMapping("/{wallet_id}/transactions/{transaction_id}/cancel")
    public ResponseEntity<Void> cancelTransaction(@RequestHeader HttpHeaders headers,
                                                  @PathVariable int wallet_id,
                                                  @PathVariable int transaction_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            walletService.cancelTransaction(user, transaction_id, wallet_id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        //Only a transaction that has NOT been approved can be canceled
    }

    @PostMapping("/{wallet_id}/transactions/{card_id}")
    public ResponseEntity<Void> createTransactionWithCard(@RequestHeader HttpHeaders headers,
                                                          @PathVariable int wallet_id,
                                                          @PathVariable int card_id,
                                                          @RequestBody CardTransactionDto cardTransactionDto) {
        try {
            User user = authHelper.tryGetUser(headers);
            CardToWalletTransaction cardTransaction = transactionMapper.fromDto(cardTransactionDto);
            walletService.transactionWithCard(user, card_id, wallet_id, cardTransaction);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        //TODO will probably have to add more error handling here
    }

//        @GetMapping("/recipient")
//    public String getRecipient(@RequestHeader HttpHeaders headers,
//                               @RequestParam(required = false) String username,
//                               @RequestParam(required = false) String email,
//                               @RequestParam(required = false) String phoneNumber) {
//
//        UserModelFilterOptions userFilter = new UserModelFilterOptions(
//                username, email, phoneNumber);
//        try {
//            User loggedUser = authHelper.tryGetUser(headers);
//            User recipient = walletService.getRecipient(userFilter);
//            return recipient.getUsername();
//        } catch (UnauthorizedOperationException e) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
//        } catch (EntityNotFoundException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        }
//    }

}
