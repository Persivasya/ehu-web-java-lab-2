package org.example;

import org.example.service.OrderProcessor;
import org.example.service.StatsProcessor;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        System.out.println("Hello at Java Caffe!");

        boolean isRunning = true;
        while (isRunning) {
            System.out.println();
            System.out.println("Available commands:");
            System.out.println("  menu               - Show the coffee menu");
            System.out.println("  order <ids> <name> - Place an order (e.g. order 1 2 3 Viktor)");
            System.out.println("  stats list         - Show all stats");
            System.out.println("  stats <id>         - Show stats by id (e.g. stats 1)");
            System.out.println("  exit               - Exit the application");
            System.out.println();

            String command = System.console().readLine().trim();

            if (command.equals("menu")) {
                System.out.println("Showing menu...");
                OrderProcessor.Instance.printMenu();

            } else if (command.startsWith("order ")) {
                // format: order 1 3 5 6 Viktor
                // last token is the name, all tokens in between are coffee ids
                String[] parts = command.substring("order ".length()).trim().split("\\s+");
                if (parts.length < 2) {
                    System.out.println("Invalid order format. Example: order 1 3 5 Viktor");
                } else {
                    String name = parts[parts.length - 1];
                    String[] idTokens = java.util.Arrays.copyOf(parts, parts.length - 1);
                    System.out.println("Ordering coffee...");
                    OrderProcessor.Instance.doOrder(name, idTokens);
                }

            } else if (command.equals("stats list")) {
                System.out.println("Showing stats list...");
                StatsProcessor.Instance.printStatsMenu();

            } else if (command.startsWith("stats ")) {
                // format: stats 1
                String idPart = command.substring("stats ".length()).trim();
                try {
                    int statId = Integer.parseInt(idPart);
                    System.out.println("Showing stats...");
                    StatsProcessor.Instance.showStats(statId);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid stats id: " + idPart);
                }

            } else if (command.equals("exit")) {
                System.out.println("Exiting...");
                isRunning = false;

            } else {
                System.out.println("Unknown command. Please try again.");
            }
        }
    }
}
