package com.varshika.expensetracker.service;

import com.varshika.expensetracker.model.Expense;
import com.varshika.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public void saveExpense(Expense expense) {
        expenseRepository.save(expense);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public double calculateTotal(List<Expense> expenses) {
        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }
    public Expense getExpenseById(Long id) {
    return expenseRepository.findById(id).orElse(null);
}

public void updateExpense(Expense expense) {
    expenseRepository.save(expense);
}
}