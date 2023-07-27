package t.me.afx51.sorting.controler;

import t.me.afx51.sorting.sortingTools.MergeSortingFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserInputHandler {
    public static void parseUserArgument(String[] userArguments) {
        List<String> userInputArgument = new ArrayList<String>(Arrays.asList(userArguments));

        if (userInputArgument.size() < 3) {
            System.out.println("Отсутсвуют обязательные аргументны");
            System.exit(1);
        }

        boolean isIncreasing = true;
        boolean isString = false;

        if (userInputArgument.get(0).equals("-a") || userInputArgument.get(0).equals("-d")) {
            isIncreasing = userInputArgument.get(0).equals("-a");
            userInputArgument.remove(0);
        }

        if (!(userInputArgument.get(0).equals("-i") || userInputArgument.get(0).equals("-s"))) {
            System.out.println("Отсутсвует обязятельный аргумент или нарушен порядо ввода");
            System.exit(2);
        } else {
            isString = userInputArgument.get(0).equals("-s");
            userInputArgument.remove(0);
        }

        String outPutFile = userInputArgument.get(0);
        userInputArgument.remove(0);

        String[] inputFilesName = userInputArgument.toArray(String[]::new);

        try {
            MergeSortingFile.sort(inputFilesName, outPutFile, isString, isIncreasing);
        } catch (IOException | RuntimeException e) {
            System.out.println(e.getLocalizedMessage());
            System.out.println("Ошибка во время сортировки");
            System.exit(1);
        }
    }
}
