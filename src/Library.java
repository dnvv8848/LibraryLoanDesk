public class Library {
    BookList books = new BookList();
    LoanQueue requests = new LoanQueue();
    IdList processedIds = new IdList();
    UndoStack history = new UndoStack();

    public boolean addBook(Book book) {
        if (!books.add(book)) return false;
        history.push(new UndoStack.Action("ADD", book, -1));
        return true;
    }

    public boolean updateBook(String id, String title, String category, int copies) {
        Book book = books.find(id);
        Book replacement = new Book(id, title, category, copies);
        if (book == null || !replacement.isValid()) return false;
        history.push(new UndoStack.Action("UPDATE", book, -1));
        book.title = title;
        book.category = category;
        book.copies = copies;
        return true;
    }

    public boolean removeBook(String id) {
        if (requests.hasBook(id)) return false;
        int position = books.indexOf(id);
        Book removed = books.remove(id);
        if (removed == null) return false;
        history.push(new UndoStack.Action("REMOVE", removed, position));
        return true;
    }

    public boolean undo() {
        UndoStack.Action action = history.pop();
        if (action == null) return false;
        Book old = action.oldBook;
        if (action.type.equals("ADD")) books.remove(old.bookId);
        else if (action.type.equals("REMOVE")) books.insertAt(old, action.position);
        else {
            Book book = books.find(old.bookId);
            book.title = old.title;
            book.category = old.category;
            book.copies = old.copies;
        }
        return true;
    }

    private void clearHistory(String reason) {
        history.clear();
        System.out.println("Undo history cleared: " + reason);
    }

    // null means success; otherwise the returned text explains the error.
    public String submitRequest(String id, String borrowerId, String bookId) {
        if (!Book.validText(id) || !Book.validText(borrowerId) || !Book.validText(bookId))
            return "Invalid request fields.";
        if (requests.hasId(id) || processedIds.contains(id)) return "Duplicate request ID.";
        if (books.find(bookId) == null) return "Book does not exist.";
        requests.enqueue(new LoanRequest(id, borrowerId, bookId));
        clearHistory("a request was added to the queue.");
        return null;
    }

    public String processNext() {
        LoanRequest request = requests.dequeue();
        if (request == null) return "Queue is empty.";
        processedIds.add(request.requestId);
        clearHistory("a loan request was processed.");
        Book book = books.find(request.bookId);
        if (book.copies == 0) return request.requestId + ": REJECTED: NO STOCK";
        book.copies--;
        return request.requestId + ": APPROVED";
    }

    public boolean hasSessionData() {
        return books.size > 0 || !requests.isEmpty() || !processedIds.isEmpty() || !history.isEmpty();
    }

    public void replaceCatalogue(BookList loaded) {
        books = loaded;
        requests = new LoanQueue();
        processedIds = new IdList();
        clearHistory("file loaded; a new session has started.");
    }
}
