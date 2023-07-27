package t.me.afx51.sorting.sortingTools;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class MergeSortingFile {

    public static void sort(String[] filesInputName, String fileOutput, boolean isString, boolean isIncreasing) throws IOException {
        if (filesInputName == null || filesInputName.length == 0) {
            throw new IllegalArgumentException("Имя входного файла(-ов) не должно быть пустым");
        }
        if (fileOutput == null || fileOutput.isEmpty()) {
            throw new IllegalArgumentException("Имя выходного файла не должно пустым");
        }

        //Создаём Buffered Reader для каждого входного файла, если это возможно
        List<BufferedReader> bufferedReaderList = new ArrayList<>();
        List<String> currentFileNames = new ArrayList<>(); //Для вывода имени файла, в котором произошла ошибка

        for (String inputFileName : filesInputName) {
            File file = new File(inputFileName);
            if (!file.exists()) {
                System.out.println("Входной файл " + inputFileName + " не найден");
                continue;
            }
            if (!file.canRead()) {
                System.out.println("Файл " + inputFileName + " недоступен для чтения");
                continue;
            }
             bufferedReaderList.add(new BufferedReader(new FileReader(file)));
            currentFileNames.add(inputFileName);
        }

        File outFile = new File(fileOutput);
        if (outFile.exists() && !outFile.canWrite()) {
            System.out.println("Выходной файл " + fileOutput + " не доступен для записи");
            System.out.println("Попытка создать новый выходной файл");
            boolean isNewOutputFileGenerated = false;
            //Пробуем создать доступный файл для записи данных сортировки
            for (int i = 1; i <= 5; i++) {
                String newFileOutputName = "#newOutFile" + i + ".txt";
                outFile = new File(newFileOutputName);
                outFile.createNewFile();
                if (outFile.exists() && outFile.canWrite()) {
                    isNewOutputFileGenerated = true;
                    System.out.println("Новый файл " + newFileOutputName + " для данных сортировки успешно создан");
                    break;
                }
            }

            if (!isNewOutputFileGenerated) {
                throw new IOException("Ошибка при попытки создать новый выходной файл");
            }
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outFile))) {
            //Используем PriorityQueue для сортировки слиянием файлов
            PriorityQueue<Node> priorityQueue = new PriorityQueue<>();

            for (int i = 0; i < bufferedReaderList.size(); i++) {
                String currentFileName = currentFileNames.get(i);
                String currentStringLine = bufferedReaderList.get(i).readLine();
                 if (isCorrectStringLine(currentFileName, currentStringLine, isString)) {
                    priorityQueue.add(new Node(i, currentStringLine, isString, isIncreasing));
                }
            }

            //Записываем наименший элемент из очереди в выходной файл
            while (!priorityQueue.isEmpty()) {
                Node node = priorityQueue.poll();
                int fileIndex = node.fileIndex;
                String value = node.value;
                bufferedWriter.write(value + "\n");
                String nextString = bufferedReaderList.get(fileIndex).readLine();
                String currentFileName = currentFileNames.get(fileIndex);

                if (isCorrectStringLine(currentFileName, nextString, isString)) {
                    if (isCorrectSorting(currentFileName, value, nextString, isString, isIncreasing)) {
                        priorityQueue.add(new Node(fileIndex, nextString, isString, isIncreasing));
                    }
                }
            }

            bufferedReaderList.forEach(e -> {
                try {
                    e.close();
                } catch (IOException ex) {
                    throw new RuntimeException("Ошибка при закрытие Buffered Reader");
                }
            });
        }
        System.out.println("Сортировка завершена");
    }

    private static class Node implements Comparable<Node> {
        int fileIndex;
        String value;
        boolean isString;
        boolean isIncreasing;

        private Node(int fileIndex, String value, boolean isString, boolean isIncreasing) {
            this.fileIndex = fileIndex;
            this.value = value;
            this.isString = isString;
            this.isIncreasing = isIncreasing;
        }

        @Override
        public int compareTo(Node o) {
            if (isString) {
                return isIncreasing ? value.compareTo(o.value) : o.value.compareTo(value);
            } else {
                long currentValue = Long.parseLong(this.value);
                long value = Long.parseLong(o.value);
                return isIncreasing ? Long.compare(currentValue, value) : Long.compare(value, currentValue);
            }
        }
    }

    private static boolean isCorrectStringLine(String fileName, String line, boolean isString) {
        String errorMessage = "Ошибка в строке файла " + fileName + " данный файл исключён из дальнейшей сортировки";
        boolean checkNull = line != null;
        if (checkNull) {
            if (line.contains(" ")) {
                System.out.println(errorMessage + " - обнаружен лишний пробел");
                return false;
            }

            if (!isString) {
                try {
                    Long.parseLong(line);
                } catch (NumberFormatException e) {
                    System.out.println(errorMessage + " - строка не является целым числом");
                    return false;
                }
            }
        }
        return checkNull;
    }

    private static boolean isCorrectSorting(String currentFileName, String currenValue, String nextValue,
                                            boolean isString, boolean isIncreasing) {
        boolean isSorted;
        if (isString) {
            isSorted = isIncreasing ? nextValue.compareTo(currenValue) >= 0 : nextValue.compareTo(currenValue) <= 0;
        } else {
            isSorted = isIncreasing ? Long.parseLong(nextValue) >= Long.parseLong(currenValue) :
                    Long.parseLong(nextValue) <= Long.parseLong(currenValue);
        }

        if (!isSorted) {
            System.out.println("Нарушение сортировки в файле " + currentFileName + " данный файл исключён из дальнейшей сортировки");
        }
        return isSorted;
    }
}



