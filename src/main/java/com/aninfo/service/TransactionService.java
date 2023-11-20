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
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;


    public Optional<Transaction> getTransaction(Long transaction_id){
        return transactionRepository.findById(transaction_id);
    }

    public Account deleteTransaction(Long id) {
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
        transactionRepository.deleteById(transaction.getId());
        return account;

    }

    @Transactional
    public Account withdraw(Account account, Double sum) {

        if (account.getBalance() < sum) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        Transaction transaction = new Transaction(WITHDRAW,sum,account);
        account.setBalance(account.getBalance() - sum);
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
    public Account deposit(Account account, Double sum) {

        if (sum <= 0) {
            throw new DepositNegativeSumException("Cannot deposit negative sums");
        }
        sum = promoDeposit(sum);
        Transaction transaction = new Transaction(DEPOSIT,sum,account);
        account.setBalance(account.getBalance() + sum);
        transactionRepository.save(transaction);

        return account;
    }

}
