package com.virtualwallet.controllers.rest;

import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.exceptions.InvalidOperationException;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.model_mappers.TransactionResponseMapper;
import com.virtualwallet.models.User;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.services.contracts.IntermediateTransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final IntermediateTransactionService middleTransactionService;
    private final AuthenticationHelper authHelper;
    private final TransactionResponseMapper transactionResponseMapper;

    public TransactionController(IntermediateTransactionService middleTransactionService,
                                 AuthenticationHelper authHelper,
                                 TransactionResponseMapper transactionResponseMapper) {
        this.middleTransactionService = middleTransactionService;
        this.authHelper = authHelper;
        this.transactionResponseMapper = transactionResponseMapper;
    }

    @GetMapping
    public ResponseEntity<?> getAllTransactions(@RequestHeader HttpHeaders headers,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                LocalDateTime startDate,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                LocalDateTime endDate,
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
                    middleTransactionService.getAllWithFilter(user, transactionModelFilterOptions);
//            return transactionResponseMapper.convertToDto(walletToWalletTransactionList, wallet_id);
            return ResponseEntity.status(HttpStatus.OK).body(walletToWalletTransactionList);
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{transaction_id}/approval")
    public ResponseEntity<Void> approveTransaction(@RequestHeader HttpHeaders headers,
                                                   @PathVariable int transaction_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            middleTransactionService.approveTransaction(user, transaction_id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
    }

    @PutMapping("/{transaction_id}/cancellation")
    public ResponseEntity<Void> cancelTransaction(@RequestHeader HttpHeaders headers,
                                                  @PathVariable int transaction_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            middleTransactionService.cancelTransaction(user, transaction_id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
    }
}
