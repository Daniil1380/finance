import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Wallet {
    private final List<Transaction> transactions;
    private final Map<String, Double> budgets;

    public Wallet() {
        this.transactions = new ArrayList<>();
        this.budgets = new HashMap<>();
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public double calculateTotalIncome() {
        return transactions.stream()
                .filter(t -> t.amount() > 0)
                .mapToDouble(Transaction::amount)
                .sum();
    }

    public double calculateTotalExpenses() {
        return transactions.stream()
                .filter(t -> t.amount() < 0)
                .mapToDouble(Transaction::amount)
                .sum();
    }

    public double calculateBalance() {
        return transactions.stream()
                .mapToDouble(Transaction::amount)
                .sum();
    }

    public void setBudget(String category, double budget) {
        this.budgets.put(category, budget);
    }

    public Map<String, Double> getBudgets() {
        return budgets;
    }

    public Map<String, Double> getIncomeByCategory() {
        return transactions.stream()
                .filter(t -> t.type() == TransactionType.INCOME)
                .collect(Collectors.groupingBy(Transaction::category, Collectors.summingDouble(Transaction::amount)));
    }

    public Map<String, Double> getExpensesByCategory() {
        return transactions.stream()
                .filter(t -> t.type() == TransactionType.EXPENSE || t.type() == TransactionType.TRANSFER && t.amount() < 0)
                .collect(Collectors.groupingBy(Transaction::category, Collectors.summingDouble(t -> Math.abs(t.amount()))));
    }

    public boolean checkBudgetLimit(String category) {
        Double budget = getBudgets().get(category);
        if (budget != null) {
            double spent = getExpensesByCategory().getOrDefault(category, 0.0);
            return !(spent > budget);
        }
        return true;
    }

    public void showStatistics() {
        System.out.println("\n--- Статистика ---");
        double totalIncome = calculateTotalIncome();
        double totalExpenses = calculateTotalExpenses();

        System.out.println("Общий доход: " + totalIncome);
        System.out.println("Общие расходы: " + totalExpenses);

        System.out.println("\nДоходы по категориям:");
        Map<String, Double> incomeByCategory = getIncomeByCategory();
        if (incomeByCategory.isEmpty()) {
            System.out.println("Доходы отсутствуют.");
        } else {
            for (Map.Entry<String, Double> entry : incomeByCategory.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }


        System.out.println("\nРасходы по категориям:");
        Map<String, Double> expensesByCategory = getExpensesByCategory();
        if (expensesByCategory.isEmpty()) {
            System.out.println("Расходы отсутствуют.");
        } else {
            for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }

        System.out.println("\nБюджет по категориям:");
        Map<String, Double> budgets = getBudgets();
        if (budgets.isEmpty()) {
            System.out.println("Бюджеты не установлены.");
        } else {
            for (Map.Entry<String, Double> entry : budgets.entrySet()) {
                double budget = entry.getValue();
                double spent = expensesByCategory.getOrDefault(entry.getKey(), 0.0);
                double remaining = budget - spent;
                System.out.println(entry.getKey() + ": " + budget + ", Потрачено: " + spent + ", Оставшийся бюджет: " + remaining);
            }
        }

        if (totalExpenses > totalIncome) {
            System.out.println("\nВнимание! Ваши расходы превышают доходы.");
        }
    }
}
