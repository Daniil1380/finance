import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PersonalFinanceApp {

    private static Map<String, User> users = new HashMap<>();
    private static User currentUser = null;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Repository repository = new Repository();
        users = repository.loadData();
        runApp();
        repository.saveData(users);
        System.out.println("Данные сохранены. До свидания!");
    }

    private static void runApp() {
        while (true) {
            if (currentUser == null) {
                MenuUtils.showAuthenticationMenu();
            } else {
                MenuUtils.showMainMenu();
            }

            System.out.print("Введите команду: ");
            String command = scanner.nextLine().trim();

            if (command.equalsIgnoreCase("выход")) {
                break;
            }

            executeCommand(command);
        }
    }

    private static void executeCommand(String command) {
        if (currentUser == null) {
            switch (command) {
                case "1":
                    login();
                    break;
                case "2":
                    register();
                    break;
                default:
                    System.out.println("Некорректная команда. Пожалуйста, выберите из предложенных вариантов.");
                    break;
            }
        } else {
            switch (command) {
                case "1":
                    addIncome();
                    break;
                case "2":
                    addExpense();
                    break;
                case "3":
                    setBudget();
                    break;
                case "4":
                    showStatistics();
                    break;
                case "5":
                    transferFunds();
                    break;
                case "6":
                    logout();
                    break;
                default:
                    System.out.println("Некорректная команда. Пожалуйста, выберите из предложенных вариантов.");
                    break;
            }
        }
    }

    private static void logout() {
        currentUser = null;
        System.out.println("Вы вышли из системы.");
    }

    private static void register() {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine().trim();
        if (users.containsKey(username)) {
            System.out.println("Пользователь с таким логином уже существует.");
            return;
        }

        System.out.print("Введите пароль: ");
        String password = scanner.nextLine().trim();
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Логин и пароль не могут быть пустыми.");
            return;
        }
        users.put(username, new User(username, password));
        System.out.println("Пользователь успешно зарегистрирован.");
    }

    private static void login() {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine().trim();

        System.out.print("Введите пароль: ");
        String password = scanner.nextLine().trim();

        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            System.out.println("Вход выполнен успешно, " + username + "!");
            if (user.getWallet() == null) {
                user.setWallet(new Wallet());
            }
        } else {
            System.out.println("Неверный логин или пароль.");
        }
    }


    private static void addIncome() {
        if (currentUser.getWallet() == null) {
            System.out.println("Кошелек не найден. Ошибка при добавлении дохода.");
            return;
        }
        System.out.print("Введите категорию дохода: ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            System.out.println("Категория не может быть пустой.");
            return;
        }
        System.out.print("Введите сумму дохода: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine().trim());
            if (amount <= 0) {
                System.out.println("Сумма дохода должна быть больше нуля.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат суммы. Пожалуйста, введите число.");
            return;
        }

        currentUser.getWallet().addTransaction(new Transaction(category, amount, TransactionType.INCOME));
        System.out.println("Доход успешно добавлен.");
    }

    private static void addExpense() {
        if (currentUser.getWallet() == null) {
            System.out.println("Кошелек не найден. Ошибка при добавлении расхода.");
            return;
        }
        System.out.print("Введите категорию расхода: ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            System.out.println("Категория не может быть пустой.");
            return;
        }
        System.out.print("Введите сумму расхода: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine().trim());
            if (amount <= 0) {
                System.out.println("Сумма расхода должна быть больше нуля.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат суммы. Пожалуйста, введите число.");
            return;
        }

        currentUser.getWallet().addTransaction(new Transaction(category, -amount, TransactionType.EXPENSE));
        System.out.println("Расход успешно добавлен.");
        checkBudgetLimit(category);
    }

    private static void setBudget() {
        if (currentUser.getWallet() == null) {
            System.out.println("Кошелек не найден. Ошибка при установке бюджета.");
            return;
        }
        System.out.print("Введите категорию для установки бюджета: ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            System.out.println("Категория не может быть пустой.");
            return;
        }
        System.out.print("Введите сумму бюджета для категории " + category + ": ");
        double budget;
        try {
            budget = Double.parseDouble(scanner.nextLine().trim());
            if (budget <= 0) {
                System.out.println("Сумма бюджета должна быть больше нуля.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат суммы. Пожалуйста, введите число.");
            return;
        }

        currentUser.getWallet().setBudget(category, budget);
        System.out.println("Бюджет для категории " + category + " успешно установлен.");
    }

    private static void showStatistics() {
        Wallet wallet = currentUser.getWallet();

        if (wallet == null) {
            System.out.println("Кошелек не найден. Ошибка при отображении статистики.");
            return;
        }

        wallet.showStatistics();
    }

    private static void transferFunds() {
        if (currentUser.getWallet() == null) {
            System.out.println("Кошелек не найден. Ошибка при переводе средств.");
            return;
        }
        System.out.print("Введите логин получателя: ");
        String recipientUsername = scanner.nextLine().trim();

        if (recipientUsername.equals(currentUser.getUsername())) {
            System.out.println("Перевод самому себе невозможен.");
            return;
        }
        User recipient = users.get(recipientUsername);
        if (recipient == null) {
            System.out.println("Пользователь с таким логином не найден.");
            return;
        }
        if (recipient.getWallet() == null) {
            recipient.setWallet(new Wallet());
        }

        System.out.print("Введите сумму перевода: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine().trim());
            if (amount <= 0) {
                System.out.println("Сумма перевода должна быть больше нуля.");
                return;
            }
            if (currentUser.getWallet().calculateBalance() < amount) {
                System.out.println("Недостаточно средств на балансе для перевода.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат суммы. Пожалуйста, введите число.");
            return;
        }

        currentUser.getWallet().addTransaction(new Transaction("Перевод " + recipientUsername, -amount, TransactionType.TRANSFER));
        recipient.getWallet().addTransaction(new Transaction("Перевод от " + currentUser.getUsername(), amount, TransactionType.TRANSFER));
        System.out.println("Перевод успешно выполнен.");
    }

    private static void checkBudgetLimit(String category) {
        Wallet wallet = currentUser.getWallet();

        if (wallet == null) {
            System.out.println("Кошелек не найден. Ошибка при проверке бюджета.");
            return;
        }

        if (!wallet.checkBudgetLimit(category)) {
            System.out.println("\nВнимание! Превышен лимит бюджета для категории '" + category + "'.");
        }

    }

}
