public class LoanRequest {
    String requestId;
    String borrowerId;
    String bookId;

    public LoanRequest(String requestId, String borrowerId, String bookId) {
        this.requestId = requestId;
        this.borrowerId = borrowerId;
        this.bookId = bookId;
    }

    public String toString() {
        return requestId + "," + borrowerId + "," + bookId;
    }
}
