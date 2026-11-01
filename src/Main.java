import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final Scanner input = new Scanner(System.in, "UTF-8");
    private static final Library library = new Library();

    private static String ask(String message) {
        System.out.print(message);
        return input.nextLine().trim();
    }

    private static int number(String message) {
        while (true) {
            try { return Integer.parseInt(ask(message)); }
            catch (NumberFormatException e) { System.out.println("Please enter an integer."); }
        }
    }

    private static void result(boolean success) {
        System.out.println(success ? "Done." : "Failed. Check ID, fields, stock count or pending requests.");
    }

    public static void main(String[] args) {
        System.out.println("LIBRARY LOAN DESK");
        boolean running = true;
        while (running) {
            System.out.println("\n1.Show books  2.Add  3.Find  4.Update  5.Remove");
            System.out.println("6.Request loan  7.Show queue  8.Peek  9.Process next");
            System.out.println("10.Undo  11.Category total  12.Save  13.Load  0.Exit");
            if (!input.hasNextLine()) break;
            String choice = ask("Choose: ");
            try {
                switch (choice) {
                    case "1": library.books.display(); break;
                    case "2":
                        result(library.addBook(new Book(ask("Book ID: "), ask("Title: "),
                            ask("Category: "), number("Copies: "))));
                        break;
                    case "3":
                        Book book = library.books.find(ask("Book ID: "));
                        System.out.println(book == null ? "Book not found." : book);
                        break;
                    case "4":
                        result(library.updateBook(ask("Book ID: "), ask("New title: "),
                            ask("New category: "), number("New copies: ")));
                        break;
                    case "5": result(library.removeBook(ask("Book ID: "))); break;
                    case "6":
                        String error = library.submitRequest(ask("Request ID: "), ask("Borrower ID: "), ask("Book ID: "));
                        System.out.println(error == null ? "Request added." : error);
                        break;
                    case "7": library.requests.display(); break;
                    case "8":
                        LoanRequest next = library.requests.peek();
                        System.out.println(next == null ? "Queue is empty." : next);
                        break;
                    case "9": System.out.println(library.processNext()); break;
                    case "10": System.out.println(library.undo() ? "Undo done." : "Nothing to undo."); break;
                    case "11": System.out.println("Available copies: " + library.books.totalCopies(ask("Category: "))); break;
                    case "12": CsvStorage.save(library.books, ask("Save path: ")); System.out.println("Saved."); break;
                    case "13":
                        BookList loaded = CsvStorage.read(ask("Load path: "));
                        if (library.hasSessionData()) {
                            System.out.println("Loading replaces the catalogue and clears requests, processed IDs and undo.");
                            if (!ask("Continue? (y/n): ").equalsIgnoreCase("y")) break;
                        }
                        library.replaceCatalogue(loaded);
                        System.out.println("Loaded " + loaded.size + " books.");
                        break;
                    case "0": running = false; break;
                    default: System.out.println("Invalid menu choice.");
                }
            } catch (IOException | java.nio.file.InvalidPathException e) {
                System.out.println("File error: " + e.getMessage());
            }
        }
    }
}
