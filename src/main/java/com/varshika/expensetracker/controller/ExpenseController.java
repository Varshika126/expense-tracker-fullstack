package com.varshika.expensetracker.controller;

import com.varshika.expensetracker.model.Expense;
import com.varshika.expensetracker.model.User;
import com.varshika.expensetracker.repository.ExpenseRepository;
import com.varshika.expensetracker.repository.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseRepository expenseRepository,
                             UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    // 🔹 Dashboard
    @GetMapping("/expenses")
    public String viewHomePage(
            @RequestParam(value = "month", required = false) Integer month,
            Model model,
            Principal principal) {

        String username = principal.getName();

        // ✅ FIXED PART (unwrap Optional)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Expense> expenses;

        if (month != null) {
            LocalDate start = LocalDate.of(LocalDate.now().getYear(), month, 1);
            LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
            expenses = expenseRepository
                    .findByUserAndDateBetween(user, start, end);
        } else {
            expenses = expenseRepository.findByUser(user);
        }

        model.addAttribute("expenses", expenses);
        model.addAttribute("selectedMonth", month);

        // 🔹 Total
        double totalAmount = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
        model.addAttribute("totalAmount", totalAmount);

        // 🔹 Category Data
        Map<String, Double> categoryData = new HashMap<>();
        for (Expense exp : expenses) {
            categoryData.put(
                    exp.getCategory(),
                    categoryData.getOrDefault(exp.getCategory(), 0.0)
                            + exp.getAmount()
            );
        }
        model.addAttribute("categoryData", categoryData);

        // 🔹 Monthly Data
        Map<String, Double> monthlyData = new LinkedHashMap<>();
        List<Expense> allExpenses = expenseRepository.findByUser(user);

        for (Expense exp : allExpenses) {
            String monthName = exp.getDate().getMonth().toString();
            monthlyData.put(
                    monthName,
                    monthlyData.getOrDefault(monthName, 0.0)
                            + exp.getAmount()
            );
        }
        model.addAttribute("monthlyData", monthlyData);

        return "index";
    }

    // 🔹 Add Page
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("expense", new Expense());
        return "add-expense";
    }

    // 🔹 Save Expense
    @PostMapping("/save")
    public String saveExpense(@ModelAttribute Expense expense,
                              Principal principal) {

        String username = principal.getName();

        // ✅ FIXED HERE ALSO
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        expense.setUser(user);
        expenseRepository.save(expense);

        return "redirect:/expenses";
    }

    // 🔹 Delete
    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseRepository.deleteById(id);
        return "redirect:/expenses";
    }

    // 🔹 Edit
    @GetMapping("/edit/{id}")
    public String editExpense(@PathVariable Long id, Model model) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        model.addAttribute("expense", expense);
        return "add-expense";
    }
}