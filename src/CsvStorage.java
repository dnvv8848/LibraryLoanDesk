import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CsvStorage {
    private static final String HEADER = "bookId,title,category,copies";

    public static void save(BookList books, String path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8)) {
            writer.write(HEADER);
            writer.newLine();
            for (BookList.Node p = books.head; p != null; p = p.next) {
                writer.write(p.book.toString());
                writer.newLine();
            }
        }
    }

    // Only returns a new catalogue after the whole file has passed validation.
    public static BookList read(String path) throws IOException {
        BookList loaded = new BookList();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            String first = reader.readLine();
            if (first != null && first.startsWith("\uFEFF")) first = first.substring(1);
            if (!HEADER.equals(first)) throw new IOException("Line 1: expected " + HEADER);
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] fields = line.split(",", -1);
                if (fields.length != 4) throw error(lineNumber, "expected 4 fields");
                for (int i = 0; i < fields.length; i++) fields[i] = fields[i].trim();
                int copies;
                try { copies = Integer.parseInt(fields[3]); }
                catch (NumberFormatException e) { throw error(lineNumber, "copies must be an integer"); }
                Book book = new Book(fields[0], fields[1], fields[2], copies);
                if (!book.isValid()) throw error(lineNumber, "empty/invalid field or negative copies");
                if (loaded.find(book.bookId) != null) throw error(lineNumber, "duplicate book ID: " + book.bookId);
                loaded.add(book);
            }
        }
        return loaded;
    }

    private static IOException error(int line, String reason) {
        return new IOException("Line " + line + ": " + reason);
    }
}
