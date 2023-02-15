package com.debugagent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.*;

public class Wordle {
    private static final String WORD = "LYMPH";
    private static List<String> DICTIONARY;
    private static List<String> words = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        File statusFile = new File("state.txt");
        List<State> status;
        if(statusFile.exists()) {
            status = Files.readAllLines(new File("state.txt").toPath())
                    .stream().map(val -> {
                        String[] values = val.split(",");
                        long time = Long.parseLong(values[0]);
                        String word = values[1];
                        var attempts = Arrays.asList(values).subList(2, values.length);
                        return new State(word, time, attempts);
                    }).toList();
        } else {
            status = Collections.emptyList();
        }
        DICTIONARY = Files.readAllLines(new File("words.txt").toPath());
        DICTIONARY = DICTIONARY.stream().map(String::toUpperCase).toList();

        System.out.println("Write a guess:");
        Scanner scanner = new Scanner(System.in);
        int attempts = 0;
        for(String line = scanner.nextLine() ; line != null ; line = scanner.nextLine()) {
            if(line.length() != 5) {
                System.out.println("5 characters only... Please try again:");
                continue;
            }
            line = line.toUpperCase();
            if(line.equals(WORD)) {
                System.out.println("Success!!!");
                words.add(line);
                finishGame();
            }
            if(!DICTIONARY.contains(line)) {
                System.out.println("Word not in dictionary... Try again:");
            } else {
                words.add(line);
                attempts++;
                printWordResult(line);
                if(attempts > 7) {
                    System.out.println("Game over!");
                    finishGame();
                }
            }
        }
    }

    private static void finishGame() throws IOException {
        try (Writer writer = new FileWriter("state.txt", true)) {
            writer.write(System.currentTimeMillis() + ","
                    + WORD + "," +
                    String.join(",", words) + "\n");
        }
        System.exit(0);
    }


    private static void printWordResult(String word) {
        for(int iter = 0 ; iter < word.length() ; iter++) {
            char currentChar = word.charAt(iter);
            if(currentChar == WORD.charAt(iter)) {
                System.out.print("G"); // Green
                continue;
            }
            if(WORD.indexOf(currentChar) > -1) {
                System.out.print("Y"); // Yellow
                continue;
            }
            System.out.print("B"); // Black
        }
        System.out.println();
    }
}