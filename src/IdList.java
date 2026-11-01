// IDs are kept even when a processed request was rejected for no stock.
public class IdList {
    private static class Node {
        String id;
        Node next;
        Node(String id, Node next) { this.id = id; this.next = next; }
    }
    private Node head;

    public void add(String id) { head = new Node(id, head); }
    public boolean isEmpty() { return head == null; }
    public boolean contains(String id) {
        for (Node p = head; p != null; p = p.next)
            if (p.id.equals(id)) return true;
        return false;
    }
}
