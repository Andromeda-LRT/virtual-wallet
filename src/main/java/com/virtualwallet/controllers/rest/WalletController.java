package com.virtualwallet.controllers.rest;

import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_mappers.TransactionMapper;
import com.virtualwallet.model_mappers.TransactionResponseMapper;
import com.virtualwallet.model_mappers.WalletMapper;
import com.virtualwallet.models.Transaction;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.model_dto.TransactionResponseDto;
import com.virtualwallet.services.contracts.UserService;
import com.virtualwallet.services.contracts.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.virtualwallet.models.model_dto.WalletDto;
import com.virtualwallet.models.model_dto.TransactionDto;

import java.util.List;

@RestController
@RequestMapping("api/wallets")
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
                            TransactionResponseMapper transactionResponseMapper, TransactionMapper transactionMapper) {
        this.userService = userService;
        this.authHelper = authHelper;
        this.walletService = walletService;
        this.walletMapper = walletMapper;
        this.transactionResponseMapper = transactionResponseMapper;
        this.transactionMapper = transactionMapper;
    }

    @GetMapping
    public List<Wallet> getAllWallets(@RequestHeader HttpHeaders headers) {
        try {
            User user = authHelper.tryGetUser(headers);
            List<Wallet> walletList = walletService.getAllWallets(user);
            return walletList;
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Wallet getWalletById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authHelper.tryGetUser(headers);
            Wallet wallet = walletService.getWalletById(user, id);
            return wallet;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping()
    public Wallet createWallet(@RequestHeader HttpHeaders headers, @RequestBody @Valid WalletDto walletDto) {
        try {
            User user = authHelper.tryGetUser(headers);
            Wallet wallet = walletMapper.fromDto(walletDto);
            walletService.createWallet(user, wallet);
            return wallet;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Wallet updateWallet(@RequestHeader HttpHeaders headers,
                               @RequestBody @Valid WalletDto walletDto,
                               @PathVariable int id) {
        try {
            User user = authHelper.tryGetUser(headers);
            Wallet wallet = walletMapper.fromDto(walletDto, id);
            walletService.updateWallet(user, wallet);
            return wallet;
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public void deleteWallet(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authHelper.tryGetUser(headers);
            walletService.delete(user, id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{wallet_id}/transactions")
    public List<TransactionDto> getTransactionHistory(@RequestHeader HttpHeaders headers, @PathVariable int wallet_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            List<Transaction> transactionList = walletService.getAllTransactions(user, wallet_id);
            return transactionResponseMapper.convertToDto(transactionList, wallet_id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{wallet_id}/transactions/{transaction_id}")
    public TransactionResponseDto getTransactionById(@RequestHeader HttpHeaders headers,
                                             @PathVariable int wallet_id,
                                             @PathVariable int transaction_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            Transaction transaction = walletService.getTransactionById(user, wallet_id, transaction_id);
            return transactionResponseMapper.convertToDto(transaction);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{wallet_id}/transactions")
    public TransactionResponseDto creteTransaction(@RequestHeader HttpHeaders headers,
                                                   @PathVariable int wallet_id,
                                                   @RequestBody @Valid TransactionDto transactionDto) {
        try {
            User user = authHelper.tryGetUser(headers);
            Transaction transaction = transactionMapper.fromDto(transactionDto);
            walletService.createTransaction(user, transaction, wallet_id);
            return transactionResponseMapper.convertToDto(transaction);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        //TODO add an error regarding insufficient amount
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
//        }
//
//        //TODO add an error regarding insufficient amount
//        //Only a transaction that has not been approved can be updated
//    }

    @PutMapping("/{wallet_id}/transactions/{transaction_id/approve}")
    public void approveTransaction(@RequestHeader HttpHeaders headers,
                                   @PathVariable int wallet_id,
                                   @PathVariable int transaction_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            walletService.approveTransaction(user, transaction_id, wallet_id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    @PutMapping("/{wallet_id}/transactions/{transaction_id}/cancel")
    public void cancelTransaction(@RequestHeader HttpHeaders headers,
                                  @PathVariable int wallet_id,
                                  @PathVariable int transaction_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            walletService.cancelTransaction(user, transaction_id, wallet_id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        //Only a transaction that has NOT been approved can be canceled
    }

    @PostMapping("/{wallet_id}/transactions/{card_id}")
    public void createTransactionWithCard(@RequestHeader HttpHeaders headers,
                                          @PathVariable int wallet_id,
                                          @PathVariable int card_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            walletService.transactionWithCard(user, card_id, wallet_id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        //TODO will probably have to add move error handling here
    }

}