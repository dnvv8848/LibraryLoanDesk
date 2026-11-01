public class Book {
    String bookId;
    String title;
    String category;
    int copies;

    public Book(String bookId, String title, String category, int copies) {
        this.bookId = bookId;
        this.title = title;
        this.category = category;
        this.copies = copies;
    }

    public Book copy() {
        return new Book(bookId, title, category, copies);
    }

    public static boolean validText(String text) {
        return text != null && !text.trim().isEmpty()
            && !text.contains(",") && !text.contains("\n") && !text.contains("\r");
    }

    public boolean isValid() {
        return validText(bookId) && validText(title) && validText(category) && copies >= 0;
    }

    public String toString() {
        return bookId + "," + title + "," + category + "," + copies;
    }
}
