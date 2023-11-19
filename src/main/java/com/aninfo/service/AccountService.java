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
    @Autowired
    private TransactionRepository transactionRepository;

    public Account createAccount(Account account) {
        accountRepository.save(account);
        transactionRepository.save(account.getTransactions().get(0));
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

    public Optional<Transaction> getTransaction(Long transaction_id){
        return transactionRepository.findById(transaction_id);
    }

    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findTransactionByid(id);
        Account account = transaction.getAccount();
        if (transaction.getType().equals(DEPOSIT)){
            if (transaction.getAmount()<account.getBalance()){
                account.setBalance(account.getBalance() - transaction.getAmount());
                transaction.setAccount(null);
            }
            else{
                throw new InsufficientFundsException("Insufficient funds");
            }
        }
        else{
            account.setBalance(account.getBalance() + transaction.getAmount());
            transaction.setAccount(null);
        }
        account.deleteTransaction(transaction);
        accountRepository.save(account);
        transactionRepository.deleteById(transaction.getId());


    }

    @Transactional
    public Account withdraw(Long cbu, Double sum) {
        Account account = accountRepository.findAccountByCbu(cbu);

        if (account.getBalance() < sum) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        Transaction transaction = new Transaction(WITHDRAW,sum,account);
        account.setBalance(account.getBalance() - sum);
        //account.addTransaction(transaction);
        accountRepository.save(account);
        transactionRepository.save(transaction);

        return account;
    }

    private Double promoDeposit(Double sum){

        if (sum >= 2000) {
            if (sum<=5000){
                sum += sum*0.1;
            }
            else{
                sum +=500;
            }
        }
        return sum;
    }
    @Transactional
    public Account deposit(Long cbu, Double sum) {

        if (sum <= 0) {
            throw new DepositNegativeSumException("Cannot deposit negative sums");
        }
        sum = promoDeposit(sum);
        Account account = accountRepository.findAccountByCbu(cbu);
        Transaction transaction = new Transaction(DEPOSIT,sum,account);
        account.setBalance(account.getBalance() + sum);
        //account.addTransaction(transaction);
        accountRepository.save(account);
        transactionRepository.save(transaction);

        return account;
    }

}
