# Library Loan Desk - report draft

This is a draft to review and expand into the required 3-5 page report.
Group members and contributions still need to be completed by the group.

## 1. Design

We use a singly linked list for books. Each node contains a Book and a next
reference. The list stores head, tail and size. A Book contains its ID, title,
category and available copies. IDs and categories are case-sensitive.

LoanQueue uses front and rear references. New requests go to rear and processing
removes front. IdList is another simple linked list that keeps processed request
IDs. IDs of both approved and rejected requests stay reserved until a new load.

UndoStack stores actions at top. An action contains a type, a copy of the old
book and the old index when needed. Copying the book prevents later edits from
changing the saved history. Main handles input and output. Library coordinates
business rules; the node operations do not read console input.

## 2. Pointer update example

Start with head -> B001 -> B002 -> B003 -> null and tail = B003.
To remove B002, search until previous points to B001 and current to B002.
Set previous.next = current.next. Now B001 points to B003. Head and tail do
not change and size decreases from 3 to 2.

For head removal, head becomes current.next. For tail removal, tail becomes
previous. Removing the only node makes both head and tail null. Enqueue after
an emptied queue also sets both front and rear to the new node.

## 3. Undo example

Start with [B001, B002, B003]. Removing B002 records REMOVE, a copy of B002,
and index 1. The list becomes [B001, B003]. Undo pops this action and inserts
B002 at index 1, so the original order is restored.

An update saves the old title, category and copies before changing them.
Undo add removes the added ID. Failed edits do not push actions.
Queue insertion and request processing clear history with an explanation.
For example, an update followed by a processed loan cannot be undone and
therefore cannot accidentally restore stock from before the loan.
Peek and display do not clear history. Successful load clears history too.

## 4. Recursive report

totalCopies(node, category) returns 0 when node is null: this is the base case.
Otherwise it takes this node's copies if the category matches, or 0 if not,
then adds totalCopies(node.next, category). This is the recursive step.
It visits every node, so worst-case time is O(n). Up to n recursive calls are
waiting at once, so call-stack space is O(n). Empty lists and unmatched
categories return 0. Very long lists may exceed Java's call stack; the sample
catalogue is small and the assignment requires recursive traversal.

## 5. Worst-case costs

Let n be the number of books, q pending requests and p processed IDs.
These costs treat bounded-size field comparisons as constant work.

| Operation | Time | Reason |
| --- | --- | --- |
| Find / update book | O(n) | Search by ID |
| Add book | O(n) | Duplicate search; tail append itself is O(1) |
| Remove through Library | O(q+n) | Check pending queue and search the catalogue |
| Display / recursive total | O(n) | Visit all books |
| Raw enqueue / dequeue / peek | O(1) | Front/rear references |
| Submit request | O(q+p+n) | Check pending IDs, processed IDs and book existence |
| Process next | O(n) | Dequeue and ID insertion are O(1); book lookup is O(n) |
| Stack push / pop / logical clear | O(1) | Change top reference |
| Undo add / update / remove | O(n) | ID search or traversal to an old index |
| Save | O(n) | Write every book |
| Load n records | O(n^2) | Each insertion checks earlier IDs by linear search |

Display pending requests costs O(q). The current implementation scans twice
when adding validated CSV rows; this changes the constant, not O(n^2).
Loading needs O(n) extra temporary nodes before replacing the old catalogue.
Catalogue, requests, processed IDs and history use O(n+q+p+h) stored nodes,
where h is the number of undo actions. Clearing only drops the root reference;
Java's garbage collector reclaims unreachable nodes separately.

## 6. Persistence and validation

CSV uses UTF-8 with header bookId,title,category,copies. We use split with -1
to preserve empty trailing fields. Fields cannot contain commas or newlines.
A new list is built before the session is replaced. Invalid field counts,
empty text, invalid integers, negative copies and duplicate IDs fail with a
line number. Reading stops at the first error; no partly validated catalogue
is installed. I/O errors also leave the active session unchanged.

The menu asks before replacing non-empty session data. Successful replacement
clears pending requests, processed IDs and history. Save preserves list order
and does not reset the session.

## 7. Testing and individual review

SelfTest uses custom comparisons and prints expected and actual values. It
covers empty structures, single nodes, head/tail removal, FIFO, ID rejection,
competing requests, undo position/snapshots, history boundaries, recursive
totals, CSV round-trip and invalid-load preservation of all session components.

Verification on this computer: compiled using JDK 1.8.0_202 with Java 8 source
and target settings. SelfTest completed with 64 passed checks and 0 failures.
Each member
must independently review and explain their contributed implementation or
tests and review another member's work before submission. Fill the member
and contribution CSVs with actual evidence; this draft does not establish it.
