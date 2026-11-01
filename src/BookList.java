public class BookList {
    static class Node {
        Book book;
        Node next;

        Node(Book book) {
            this.book = book;
        }
    }

    Node head;
    Node tail;
    int size;

    public Book find(String id) {
        Node current = head;
        while (current != null) {
            if (current.book.bookId.equals(id)) return current.book;
            current = current.next;
        }
        return null;
    }

    public int indexOf(String id) {
        int index = 0;
        for (Node p = head; p != null; p = p.next) {
            if (p.book.bookId.equals(id)) return index;
            index++;
        }
        return -1;
    }

    public boolean add(Book book) {
        if (book == null || !book.isValid() || find(book.bookId) != null) return false;
        insertAt(book.copy(), size);
        return true;
    }

    // Used by undo to put a removed book back in its old position.
    void insertAt(Book book, int index) {
        if (index < 0 || index > size) throw new IllegalArgumentException("Invalid position");
        Node node = new Node(book);
        if (index == 0) {
            node.next = head;
            head = node;
            if (size == 0) tail = node;
        } else if (index == size) {
            tail.next = node;
            tail = node;
        } else {
            Node previous = head;
            for (int i = 0; i < index - 1; i++) previous = previous.next;
            node.next = previous.next;
            previous.next = node;
        }
        size++;
    }

    public Book remove(String id) {
        Node previous = null;
        Node current = head;
        while (current != null && !current.book.bookId.equals(id)) {
            previous = current;
            current = current.next;
        }
        if (current == null) return null;
        if (previous == null) head = current.next;
        else previous.next = current.next;
        if (current == tail) tail = previous;
        size--;
        return current.book;
    }

    public int totalCopies(String category) {
        return totalCopies(head, category);
    }

    private int totalCopies(Node node, String category) {
        if (node == null) return 0;
        int here = node.book.category.equals(category) ? node.book.copies : 0;
        return here + totalCopies(node.next, category);
    }

    public void display() {
        if (head == null) System.out.println("Catalogue is empty.");
        for (Node p = head; p != null; p = p.next) System.out.println(p.book);
    }
}
