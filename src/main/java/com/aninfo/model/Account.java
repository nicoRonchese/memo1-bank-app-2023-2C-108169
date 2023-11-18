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
        addTransaction(DEPOSIT,balance);
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
            addTransaction(DEPOSIT,balance);
        }
        else if (this.balance < balance){
            addTransaction(DEPOSIT,balance - this.balance);
        }
        else if (this.balance > balance){
            addTransaction(WITHDRAW,this.balance-balance);
        }
        this.balance = balance;
    }

    public List<Transaction> getTransactions(){return transactions;}

    public void setTransactions(List<Transaction> transactions){this.transactions = transactions;}
    
    public Optional<Transaction> findTransaction(Long id){
        for (Transaction item : transactions) {
            if (item.getId().equals(id)){
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public void addTransaction(TransactionType type,Double sum){
        Transaction new_transaction = new Transaction(type,sum,this);
        transactions.add(new_transaction);
    }

    public void deleteTransaction(Long id) {
        int delete = -1;
        int i = 0;

        for (Transaction item : transactions) {
            if (item.getId().equals(id)) {
                if (item.getType() == DEPOSIT) {
                    if (balance >= item.getAmount()) {
                        delete = i;
                        balance = balance - item.getAmount();
                        item.setAccount(null);
                    } else {
                        throw new InsufficientFundsException("Insufficient funds");
                    }
                } else {
                    delete = i;
                    balance = balance + item.getAmount();
                    item.setAccount(null);
                }
                break;
            }
            i++;
        }

        // Correct the condition to delete if the item was found
        if (delete >= 0) {
            transactions.remove(delete);
        }
    }

}
