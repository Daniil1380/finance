import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Repository repository = new Repository();
        PersonalFinanceApp personalFinanceApp = new PersonalFinanceApp(repository, scanner);
        personalFinanceApp.start();
    }

}
