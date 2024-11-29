import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StabilityChecker {

    public static void main(String[] args) {
        String csvFile = "data.csv"; // Путь к вашему CSV файлу
        String csvSplitBy = ";"; // Используем точку с запятой в качестве разделителя

        List<Double> valuesStream1 = new ArrayList<>();
        List<Double> valuesStream2 = new ArrayList<>();
        List<String> timestamps = new ArrayList<>(); // Список для хранения временных меток

        double maxJumpValue = getMaxJumpValue();
        readCsvData(csvFile, csvSplitBy, timestamps, valuesStream1, valuesStream2);
        displayGraphsAndCheckStability(valuesStream1, valuesStream2, timestamps, maxJumpValue);
    }

    private static double getMaxJumpValue() {
        Scanner scanner = new Scanner(System.in);
        double maxJumpValue;
        while (true) {
            System.out.print("Введите максимальный скачок: ");
            try {
                maxJumpValue = Double.parseDouble(scanner.nextLine());
                return maxJumpValue; // Возвращаем значение сразу после успешного ввода
            } catch (NumberFormatException e) {
                System.out.println("Некорректное значение. Пожалуйста, введите число.");
            }
        }
    }

    private static void readCsvData(String csvFile, String csvSplitBy, List<String> timestamps,
                                     List<Double> valuesStream1, List<Double> valuesStream2) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Пропустить заголовок, если он есть
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(csvSplitBy);
                if (values.length >= 3) {
                    timestamps.add(values[0]);
                    valuesStream1.add(parseDouble(values[1]));
                    valuesStream2.add(parseDouble(values[2]));
                } else {
                    System.out.println("Пропущена строка из-за недостаточного количества колонок: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.out.println("Некорректное значение для преобразования: " + value);
            return 0.0; // Возвращаем 0.0 или можно выбросить исключение
        }
    }

    private static void displayGraphsAndCheckStability(List<Double> valuesStream1, List<Double> valuesStream2,
                                                        List<String> timestamps, double maxJumpValue) {
        displayGraph(valuesStream1, "Поток 1");
        displayGraph(valuesStream2, "Поток 2");
        checkStability(valuesStream1, timestamps, maxJumpValue, "Поток 1");
        checkStability(valuesStream2, timestamps, maxJumpValue, "Поток 2");
    }

    private static void checkStability(List<Double> values, List<String> timestamps, double maxJumpValue, String streamName) {
        if (values.isEmpty()) {
            System.out.println(streamName + " не содержит данных для проверки стабильности.");
            return;
        }

        for (int i = 0; i < values.size() - 1; i++) {
            double jump = Math.abs(values.get(i) - values.get(i + 1));
            if (jump > maxJumpValue) {
                System.out.printf("Нестабильность в %s на %s: между значениями %.2f и %.2f: скачок = %.2f%n",
                        streamName, timestamps.get(i), values.get(i), values.get(i + 1), jump);
            }
        }
    }

    private static void displayGraph(List<Double> values, String streamName) {
        double maxValue = values.stream().max(Double::compare).orElse(1.0); // Находим максимальное значение
        double scale = 40 / maxValue; // Масштабирование графика

        for (double value : values) {
            int scaledValue = (int) (value * scale); // Масштабируем значение
            System.out.printf("%s: %s (%.2f)%n", streamName, "*".repeat(scaledValue), value);
        }
    }
}
