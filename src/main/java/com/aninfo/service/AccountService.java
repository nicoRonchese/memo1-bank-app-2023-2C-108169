package com.aninfo.service;

import com.aninfo.exceptions.DepositNegativeSumException;
import com.aninfo.exceptions.InsufficientFundsException;
import com.aninfo.model.Account;
import com.aninfo.model.Transaction;
import com.aninfo.repository.AccountRepository;
import com.aninfo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.aninfo.model.TransactionType.DEPOSIT;
import static com.aninfo.model.TransactionType.WITHDRAW;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    //private TransactionRepository transactionRepository;

    public Account createAccount(Account account) {
        accountRepository.save(account);
        //transactionRepository.save(account.getTransactions().get(0));
        return account;
    }

    public Collection<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long cbu) {
        return accountRepository.findById(cbu);
    }

    public void save(Account account) {accountRepository.save(account);
    }

    public void deleteById(Long cbu) {
        accountRepository.deleteById(cbu);
    }

    public Optional<List<Transaction>> getTransactionsFromAccount(Long cbu){
        Optional<Account> account = findById(cbu);
        if (account.isPresent()){
            return Optional.of(account.get().getTransactions());
        }
        return Optional.empty();
    }

}
