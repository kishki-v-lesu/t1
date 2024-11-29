import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StabilityChecker {

    public static void main(String[] args) {
        String csvFile = "data.csv"; // Путь к вашему CSV файлу
        String line;
        String csvSplitBy = ";"; // Используем точку с запятой в качестве разделителя

        List<Double> valuesStream1 = new ArrayList<>();
        List<Double> valuesStream2 = new ArrayList<>();
        List<String> timestamps = new ArrayList<>(); // Список для хранения временных меток

        // Создание сканера для ввода данных
        Scanner scanner = new Scanner(System.in);

        // Ввод значения скачка
        System.out.print("Введите максимальный скачок: ");
        double maxJumpValue;

        // Проверка корректности ввода
        while (true) {
            try {
                maxJumpValue = Double.parseDouble(scanner.nextLine());
                break; // Если ввод корректен, выходим из цикла
            } catch (NumberFormatException e) {
                System.out.print("Некорректное значение. Пожалуйста, введите число: ");
            }
        }

        // Чтение файла CSV
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Пропустить заголовок, если он есть
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(csvSplitBy);
                // Проверяем, что массив имеет достаточное количество элементов
                if (values.length >= 3) {
                    // Сохраняем временную метку и значения из второго и третьего столбцов
                    timestamps.add(values[0]); // Сохраняем временную метку
                    double value1 = Double.parseDouble(values[1]);
                    double value2 = Double.parseDouble(values[2]);
                    valuesStream1.add(value1);
                    valuesStream2.add(value2);
                } else {
                    System.out.println("Пропущена строка из-за недостаточного количества колонок: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Отображение графиков
        System.out.println("\nГрафик для Поток 1:");
        displayGraph(valuesStream1, "Поток 1");
        System.out.println("\nГрафик для Поток 2:");
        displayGraph(valuesStream2, "Поток 2");

        // Проверка стабильности данных
        System.out.println("\nПроверка стабильности для Поток 1:");
        checkStability(valuesStream1, timestamps, maxJumpValue, "Поток 1");
        System.out.println("\nПроверка стабильности для Поток 2:");
        checkStability(valuesStream2, timestamps, maxJumpValue, "Поток 2");

        // Закрытие сканера
        scanner.close();
    }

    private static void checkStability(List<Double> values, List<String> timestamps, double maxJumpValue, String streamName) {
        if (values.isEmpty()) {
            System.out.println(streamName + " не содержит данных для проверки стабильности.");
            return;
        }

        for (int i = 0; i < values.size() - 1; i++) {
            double jump = Math.abs(values.get(i) - values.get(i + 1));
            if (jump > maxJumpValue) {
                // Выводим временную метку для первого значения
                System.out.println("Нестабильность в " + streamName + " на " + timestamps.get(i) + ": между значениями " + values.get(i) + " и " + values.get(i + 1) + ": скачок = " + jump);
            }
        }
    }

    private static void displayGraph(List<Double> values, String streamName) {
        double maxValue = values.stream().max(Double::compare).orElse(1.0); // Находим максимальное значение
        double scale = 40 / maxValue; // Масштабирование графика

        for (double value : values) {
            int scaledValue = (int) (value * scale); // Масштабируем значение
            System.out.print(streamName + ": ");
            for (int j = 0; j < scaledValue; j++) {
                System.out.print("*"); // Отображаем звёздочки
            }
            System.out.println(" (" + value + ")");
        }
    }
}


