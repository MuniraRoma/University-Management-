package libraryV2.interfaces;

import java.util.List;
import libraryV2.Book;

public interface BookRepository {
    List<Book> getAvailableBooks();

    void saveAvailableBooks(List<Book> books);

    List<Book> getBorrowedBooks(String regNumber);

    void saveBorrowedBooks(String regNumber, List<Book> books);

    void deleteBorrowedBooks(String regNumber);
}
