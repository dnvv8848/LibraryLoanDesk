public class UndoStack {
    static class Action {
        String type;
        Book oldBook;
        int position;

        Action(String type, Book book, int position) {
            this.type = type;
            this.oldBook = book.copy();
            this.position = position;
        }
    }

    private static class Node {
        Action action;
        Node next;
        Node(Action action, Node next) { this.action = action; this.next = next; }
        
    }
    private Node top;

    public void push(Action action) { top = new Node(action, top); }
    public boolean isEmpty() { return top == null; }
    public void clear() { top = null; }
    public Action pop() {
        if (top == null) return null;
        Action result = top.action;
        top = top.next;
        return result;
    }
}
