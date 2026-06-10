package br.com.dio.ui;

import java.util.Scanner;

public class InputReader {
    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public int readInt(String message) {
        while (true) {
            System.out.print(message);
            var line = scanner.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Digite um numero valido.");
            }
        }
    }

    public Long readLong(String message) {
        while (true) {
            System.out.print(message);
            var line = scanner.nextLine();
            try {
                return Long.parseLong(line.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Digite um numero valido.");
            }
        }
    }

    public String readText(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
}
