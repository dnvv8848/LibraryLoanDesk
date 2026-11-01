LIBRARY LOAN DESK - Assignment 1 practice version

Requires JDK 8 or newer. No extra Java libraries.
Open a terminal inside LibraryLoanDesk, then run:

Windows shortcut: double-click run.bat, or run .\run.bat test for tests.
The script uses the local JDK 8 path if javac is missing from PATH.

Manual commands:

mkdir out
javac -encoding UTF-8 -source 8 -target 8 -d out src/*.java
java -cp out Main

Run tests:
java -cp out SelfTest

If javac is not on PATH, PowerShell on this computer can use:
& 'C:\Program Files\Java\jdk1.8.0_202\bin\javac.exe' -encoding UTF-8 -d out src/*.java

Menu:
1 show books; 2 add; 3 find; 4 update; 5 remove.
6 add loan request; 7 show pending requests; 8 peek; 9 process next.
10 undo catalogue edit; 11 recursive category total; 12 save; 13 load.
0 exits. Enter paths without surrounding quotes.

Start with option 13 and path sample-data/books.csv.
Category matching and IDs are case-sensitive.
Update keeps the existing book ID and asks for all three other fields.
Request IDs must be unique among pending and processed requests.
No stock is checked at processing time, not at submission time.
Queue changes and processing clear undo. Failed operations do not.
Loading validates a temporary list first. An invalid file keeps the old session.
A valid load asks before replacing a non-empty session and resets queue/IDs/undo.
Save only writes the catalogue, not requests or history.

Quick example after loading the sample data:
Submit R001,U001,B001 and R002,U002,B001 using option 6 twice.
Use option 9 twice: R001 is APPROVED; R002 is REJECTED: NO STOCK.
Submitting R001 again is rejected. B001 has zero copies.

SelfTest prints named checks with expected/actual values and a final total.
It creates temporary CSV files and removes them afterwards.

This is a practice implementation, not a completed group submission.
report-draft.md is a report draft; review and export it to a 3-5 page PDF.
Fill members.csv and contributions.csv with real group details and evidence.
Do not claim reviews or work that have not happened.
Review ai-use.txt and record your own checks and understanding.
Before submission compile/test from a fresh directory, review with all members,
and package the required files under the lecturer's ZIP naming convention.
Do not package out/, compiled classes, IDE caches or personal files.
