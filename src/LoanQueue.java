public class LoanQueue {
    private static class Node {
        LoanRequest request;
        Node next;
        Node(LoanRequest request) { this.request = request; }
    }

    private Node front;
    private Node rear;

    public boolean isEmpty() { return front == null; }

    public void enqueue(LoanRequest request) {
        Node node = new Node(request);
        if (rear == null) front = node;
        else rear.next = node;
        rear = node;
    }

    public LoanRequest peek() {
        return front == null ? null : front.request;
    }

    public LoanRequest dequeue() {
        if (front == null) return null;
        LoanRequest result = front.request;
        front = front.next;
        if (front == null) rear = null;
        return result;
    }

    public boolean hasId(String id) {
        for (Node p = front; p != null; p = p.next)
            if (p.request.requestId.equals(id)) return true;
        return false;
    }

    public boolean hasBook(String id) {
        for (Node p = front; p != null; p = p.next)
            if (p.request.bookId.equals(id)) return true;
        return false;
    }

    public void display() {
        if (isEmpty()) System.out.println("No pending requests.");
        for (Node p = front; p != null; p = p.next) System.out.println(p.request);
    }
}
