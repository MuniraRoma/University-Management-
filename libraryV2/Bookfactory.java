package libraryV2;

import java.time.LocalDate;

// =========================================================
// DESIGN PATTERN: Factory Method Pattern
// ---------------------------------------------------------
// WHAT IT IS:
//   Instead of using "new Book(...)" directly wherever we
//   need a Book object, we ask this factory class to create
//   it for us. The factory knows HOW to build each type
//   of Book correctly.
//
// WHY WE APPLIED IT HERE:
//   Before this change, "new Book(line)" and
//   "new Book(line, date)" were written directly inside
//   FileBookRepository. That means the repository was
//   responsible for BOTH storing books AND knowing exactly
//   how to construct them — two separate jobs.
//
//   If the Book constructor ever changes (e.g. we add an
//   ISBN field), we would have to hunt through every class
//   that uses "new Book(...)" and update them all.
//
//   With the factory, there is ONE place that creates Books.
//   Change it here, and the rest of the code is unaffected.
// =========================================================
public class BookFactory {

    // Creates a Book that is sitting on the shelf (no borrow date needed)
    public static Book createAvailableBook(String title) {
        return new Book(title);
    }

    // Creates a Book that has already been borrowed (needs a borrow date)
    public static Book createBorrowedBook(String title, LocalDate borrowedDate) {
        return new Book(title, borrowedDate);
    }
}