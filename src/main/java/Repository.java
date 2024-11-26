import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Repository {

    private static final String DATA_FILE = "user_data.txt";

    public void saveData(Map<String, User> users) {
        ObjectMapper objectMapper = new ObjectMapper();
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Map.Entry<String, User> entry : users.entrySet()) {

                String string = objectMapper.writeValueAsString(entry.getValue());
                writer.println(string);
                writer.flush();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }


    public Map<String, User> loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            ObjectMapper objectMapper = new ObjectMapper();
            return reader.lines()
                    .map((line) -> {
                        try {
                            return objectMapper.readValue(line, User.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toMap(User::getUsername, (user) -> user));
        } catch (FileNotFoundException e) {
            System.out.println("Файл с данными не найден. Будет создан новый файл.");
        } catch (Exception e) {
            System.err.println("Ошибка при чтении данных из файла: " + e.getMessage());
        }

        return new HashMap<>();
    }

}
