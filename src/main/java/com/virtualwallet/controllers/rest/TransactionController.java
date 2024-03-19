package com.virtualwallet.controllers.rest;

import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.exceptions.InvalidOperationException;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.model_mappers.TransactionResponseMapper;
import com.virtualwallet.models.CardToWalletTransaction;
import com.virtualwallet.models.User;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.services.contracts.IntermediateTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.AUTHORIZATION;
import static com.virtualwallet.model_helpers.SwaggerConstantHelper.*;

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

    @Operation(summary = GET_ALL_TRANSACTION_SUMMARY, description = GET_ALL_TRANSACTION_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping("/wallets")
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
            return ResponseEntity.status(HttpStatus.OK).body(transactionResponseMapper.convertWalletTransactionsToDto(walletToWalletTransactionList));
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = GET_ALL_CARD_TRANSACTIONS_SUMMARY, description = GET_ALL_CARD_TRANSACTIONS_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping("/cards")
    public ResponseEntity<?> getAllCardTransfers(@RequestHeader HttpHeaders headers,
                                                 @RequestParam(required = false)
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                 LocalDateTime startDate,
                                                 @RequestParam(required = false)
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                 LocalDateTime endDate,
                                                 @RequestParam(required = false) String cardLastFourDigits,
                                                 @RequestParam(required = false) String recipient,
                                                 @RequestParam(required = false) String direction,
                                                 @RequestParam(required = false) String sortBy,
                                                 @RequestParam(required = false) String sortOrder) {
        try {
            CardTransactionModelFilterOptions cardTransactionModelFilterOptions = new CardTransactionModelFilterOptions(
                    startDate, endDate, cardLastFourDigits, recipient, direction, sortBy, sortOrder);

            User user = authHelper.tryGetUser(headers);
            List<CardToWalletTransaction> cardToWalletTransactionList =
                    middleTransactionService.getAllCardTransactionsWithFilter(user, cardTransactionModelFilterOptions);
//            return transactionResponseMapper.convertToDto(walletToWalletTransactionList, wallet_id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(transactionResponseMapper
                            .convertCardTransactionsToDto(cardToWalletTransactionList));
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = APPROVE_TRANSACTION_SUMMARY, description = APPROVE_TRANSACTION_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
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

    @Operation(summary = CANCEL_TRANSACTION_SUMMARY, description = CANCEL_TRANSACTION_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
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
