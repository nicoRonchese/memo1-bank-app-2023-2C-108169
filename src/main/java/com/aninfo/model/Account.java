package com.aninfo.model;


import com.aninfo.exceptions.InsufficientFundsException;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static com.aninfo.model.TransactionType.*;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cbu;

    private Double balance;


    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions;
    public Account(){
    }

    public Account(Double balance) {
        this.balance = balance;
        this.transactions = new ArrayList<>();
        Transaction transaction = new Transaction(DEPOSIT,balance,this);
        addTransaction(transaction);
    }

    public Long getCbu() {
        return cbu;
    }

    public void setCbu(Long cbu) {
        this.cbu = cbu;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        if (this.balance == null) {
            this.transactions = new ArrayList<>();
            Transaction transaction = new Transaction(DEPOSIT,balance,this);
            addTransaction(transaction);
        }
        this.balance = balance;
    }

    public List<Transaction> getTransactions(){return transactions;}

    public void setTransactions(List<Transaction> transactions){this.transactions = transactions;}


    public void addTransaction(Transaction transaction){;
        transactions.add(transaction);
    }

    public void deleteTransaction(Transaction transaction) {
            transactions.remove(transaction);
    }

}
