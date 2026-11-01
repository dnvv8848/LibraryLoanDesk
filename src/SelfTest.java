import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SelfTest {
    private static int passed;
    private static int failed;

    private static void check(String name, Object expected, Object actual) {
        boolean ok = expected == null ? actual == null : expected.equals(actual);
        System.out.println((ok ? "PASS " : "FAIL ") + name + " | expected=" + expected + " | actual=" + actual);
        if (ok) passed++; else failed++;
    }

    private static Book book(String id, int copies) { return new Book(id, "Title " + id, "CS", copies); }

    private static String order(BookList list) {
        StringBuilder result = new StringBuilder();
        for (BookList.Node p = list.head; p != null; p = p.next) result.append(p.book.bookId).append(" ");
        return result.toString();
    }

    public static void main(String[] args) throws IOException {
        BookList list = new BookList();
        check("Empty search", null, list.find("B1"));
        check("Empty removal", null, list.remove("B1"));
        check("Empty recursive total", 0, list.totalCopies("CS"));
        list.add(book("B1", 1));
        list.remove("B1");
        check("Single node removal head", null, list.head);
        check("Single node removal tail", null, list.tail);
        check("Single node removal size", 0, list.size);
        list.add(book("B1", 1)); list.add(book("B2", 2)); list.add(book("B3", 3));
        check("Duplicate book rejected", false, list.add(book("B2", 5)));
        check("Negative copies rejected", false, list.add(book("BAD", -1)));
        check("Empty title rejected", false, list.add(new Book("BAD", "", "CS", 1)));
        check("Failed additions preserve order", "B1 B2 B3 ", order(list));
        list.remove("B1"); list.remove("B3");
        check("Head and tail removal", "B2 ", order(list));
        check("Tail after removal", "B2", list.tail.book.bookId);
        list.add(book("B4", 4));
        check("Append after tail removal", "B2 B4 ", order(list));
        check("Recursive total", 6, list.totalCopies("CS"));
        check("Recursive no match", 0, list.totalCopies("Art"));

        LoanQueue queue = new LoanQueue();
        check("Empty dequeue", null, queue.dequeue());
        check("Empty peek", null, queue.peek());
        queue.enqueue(new LoanRequest("R1", "U1", "B1"));
        queue.enqueue(new LoanRequest("R2", "U2", "B1"));
        check("Peek oldest", "R1", queue.peek().requestId);
        check("FIFO first", "R1", queue.dequeue().requestId);
        check("FIFO second", "R2", queue.dequeue().requestId);
        check("Queue emptied", true, queue.isEmpty());
        queue.enqueue(new LoanRequest("R3", "U3", "B1"));
        check("Enqueue after empty", "R3", queue.dequeue().requestId);

        Library lib = new Library();
        lib.addBook(book("B1", 1));
        check("Undo add", true, lib.undo());
        check("Undo add removed book", null, lib.books.find("B1"));
        lib.addBook(book("B1", 1)); lib.addBook(book("B2", 2)); lib.addBook(book("B3", 3));
        lib.updateBook("B2", "Changed", "Art", 8);
        lib.undo();
        check("Undo update title", "Title B2", lib.books.find("B2").title);
        check("Undo update category", "CS", lib.books.find("B2").category);
        check("Undo update copies", 2, lib.books.find("B2").copies);
        lib.removeBook("B2"); lib.undo();
        check("Undo middle removal position", "B1 B2 B3 ", order(lib.books));
        lib.removeBook("B1"); lib.undo();
        check("Undo head removal position", "B1 B2 B3 ", order(lib.books));
        lib.removeBook("B3"); lib.undo();
        check("Undo tail removal position", "B1 B2 B3 ", order(lib.books));
        Library single = new Library();
        single.addBook(book("B1", 1)); single.removeBook("B1"); single.undo();
        check("Undo only node removal", "B1 ", order(single.books));
        check("Invalid update rejected", false, single.updateBook("B1", "Changed", "CS", -1));
        single.undo();
        check("Failed update did not enter history", 0, single.books.size);

        check("Request accepted", null, lib.submitRequest("R1", "U1", "B1"));
        check("Enqueue clears undo", true, lib.history.isEmpty());
        check("Pending duplicate rejected", "Duplicate request ID.", lib.submitRequest("R1", "U2", "B1"));
        check("Pending book cannot be removed", false, lib.removeBook("B1"));
        lib.updateBook("B2", "Other", "CS", 2);
        lib.submitRequest("R2", "U2", "B1");
        check("Successful queue change clears new history", true, lib.history.isEmpty());
        lib.updateBook("B2", "Another", "CS", 2);
        check("First request approved", "R1: APPROVED", lib.processNext());
        check("Processing clears history", true, lib.history.isEmpty());
        check("Second request rejected", "R2: REJECTED: NO STOCK", lib.processNext());
        check("Stock remains zero", 0, lib.books.find("B1").copies);
        check("Processed queue empty", true, lib.requests.isEmpty());
        check("Processed approved ID rejected", "Duplicate request ID.", lib.submitRequest("R1", "U1", "B1"));
        check("Processed rejected ID rejected", "Duplicate request ID.", lib.submitRequest("R2", "U1", "B1"));
        check("Processed book can be removed", true, lib.removeBook("B1"));
        check("Unknown book rejected", "Book does not exist.", lib.submitRequest("R3", "U1", "MISSING"));
        check("Failed request preserves history", false, lib.history.isEmpty());
        check("Processing empty queue", "Queue is empty.", lib.processNext());
        check("Empty processing preserves history", false, lib.history.isEmpty());

        Path valid = Files.createTempFile("library-valid-", ".csv");
        Path invalid = Files.createTempFile("library-invalid-", ".csv");
        try {
            CsvStorage.save(lib.books, valid.toString());
            BookList loaded = CsvStorage.read(valid.toString());
            check("Round trip order", order(lib.books), order(loaded));
            check("Round trip book data", lib.books.find("B2").toString(), loaded.find("B2").toString());
            lib.submitRequest("R3", "U3", "B2");
            lib.updateBook("B2", "Before invalid load", "CS", 2);
            BookList original = lib.books;
            Files.write(invalid, ("bookId,title,category,copies\nB9,Good,CS,1\nB9,Duplicate,CS,2\n").getBytes(StandardCharsets.UTF_8));
            String message = "no error";
            try { lib.replaceCatalogue(CsvStorage.read(invalid.toString())); }
            catch (IOException e) { message = e.getMessage(); }
            check("Duplicate CSV line number", true, message.startsWith("Line 3:"));
            check("Invalid load keeps catalogue", true, lib.books == original);
            check("Invalid load keeps data", "Before invalid load", lib.books.find("B2").title);
            check("Invalid load keeps pending queue", "R3", lib.requests.peek().requestId);
            check("Invalid load keeps processed IDs", true, lib.processedIds.contains("R1"));
            check("Invalid load keeps undo", false, lib.history.isEmpty());
            Files.write(invalid, ("bookId,title,category,copies\nB9,Title,CS,-1\n").getBytes(StandardCharsets.UTF_8));
            try { CsvStorage.read(invalid.toString()); message = "no error"; }
            catch (IOException e) { message = e.getMessage(); }
            check("Negative CSV rejected with line", true, message.startsWith("Line 2:"));
            Files.write(invalid, ("bookId,title,category,copies\nB9,Title,CS,abc\n").getBytes(StandardCharsets.UTF_8));
            try { CsvStorage.read(invalid.toString()); message = "no error"; }
            catch (IOException e) { message = e.getMessage(); }
            check("Noninteger CSV rejected", true, message.contains("integer"));
            lib.replaceCatalogue(CsvStorage.read(valid.toString()));
            check("Successful load clears queue", true, lib.requests.isEmpty());
            check("Successful load clears IDs", true, lib.processedIds.isEmpty());
            check("Successful load clears undo", true, lib.history.isEmpty());
            check("Request ID reusable after load", null, lib.submitRequest("R1", "U1", "B2"));
        } finally {
            Files.deleteIfExists(valid);
            Files.deleteIfExists(invalid);
        }
        System.out.println("\nPassed: " + passed + ", failed: " + failed);
        if (failed > 0) throw new AssertionError("Some tests failed");
    }
}
