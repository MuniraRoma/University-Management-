package libraryV2;

import java.time.LocalDate;

public class Book {
    private String title;
    private LocalDate borrowedDate;
    private LocalDate returnDate;

    public Book(String title) {
        this.title = title;
    }

    public Book(String title, LocalDate borrowedDate) {
        this.title = title;
        this.borrowedDate = borrowedDate;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDate borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}
