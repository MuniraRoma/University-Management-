package libraryV2;

import libraryV2.interfaces.BookRepository;
import java.util.*;
import java.io.*;
import java.time.LocalDate;

// =========================================================
// DESIGN PATTERN: Singleton Pattern
// ---------------------------------------------------------
// WHAT IT IS:
//   A Singleton means only ONE instance of this class can
//   ever exist in the entire program. Instead of calling
//   "new FileBookRepository()" to create it, you call
//   FileBookRepository.getInstance() — which always returns
//   the same single object.
//
// WHY WE APPLIED IT HERE:
//   FileBookRepository reads and writes files on disk.
//   If two different parts of the program each created their
//   own FileBookRepository object, they could both be reading
//   and writing the same files at the same time, which can
//   cause data corruption or inconsistent results.
//
//   Making it a Singleton guarantees that no matter how many
//   times getInstance() is called, everyone is sharing the
//   exact same object — just like one shared printer in an
//   office, which is exactly the analogy from your slides.
//
// HOW IT WAS IMPLEMENTED (3 steps from your slides):
//   Step 1: Private constructor — no one can do "new FileBookRepository()"
//   Step 2: Private static field holds the one instance
//   Step 3: Public static getInstance() returns that one instance
// =========================================================
public class FileBookRepository implements BookRepository {

    // Step 2: The single instance, stored privately
    private static FileBookRepository instance;

    // Step 1: Private constructor — prevents anyone from doing "new FileBookRepository()"
    private FileBookRepository() {}

    // Step 3: The only way to get this object — always returns the same one
    public static FileBookRepository getInstance() {
        if (instance == null) {
            instance = new FileBookRepository();
        }
        return instance;
    }

    private final String availableFile = "libraryV2/AvailableBooks.txt";
    private final String borrowedFolder = "libraryV2/BorrowedBooks/";

    // (Previously fixed smell: magic number extracted to a named constant)
    private static final int SIMULATED_DAYS_BORROWED = 40;

    @Override
    public List<Book> getAvailableBooks() {
        List<Book> books = new ArrayList<>();
        try {
            File file = new File(availableFile);
            if (!file.exists())
                file.createNewFile();
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!line.isEmpty())
                    // FACTORY PATTERN: using BookFactory instead of "new Book(line)"
                    books.add(BookFactory.createAvailableBook(line));
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error reading available books.");
        }
        return books;
    }

    @Override
    public void saveAvailableBooks(List<Book> books) {
        try (FileWriter writer = new FileWriter(availableFile)) {
            for (Book b : books) {
                writer.write(b.getTitle() + "\n");
            }
        } catch (Exception e) {
            System.out.println("Error writing available books.");
        }
    }

    @Override
    public List<Book> getBorrowedBooks(String regNumber) {
        List<Book> borrowed = new ArrayList<>();
        try {
            File file = new File(borrowedFolder + regNumber + ".txt");
            if (!file.exists())
                return borrowed;
            Scanner sc = new Scanner(file);
            sc.nextLine(); // skip borrowed at date
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!line.isEmpty())
                    // FACTORY PATTERN: using BookFactory instead of "new Book(line, date)"
                    borrowed.add(BookFactory.createBorrowedBook(
                        line,
                        LocalDate.now().minusDays(SIMULATED_DAYS_BORROWED)
                    ));
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error reading borrowed books.");
        }
        return borrowed;
    }

    @Override
    public void saveBorrowedBooks(String regNumber, List<Book> books) {
        try {
            File folder = new File(borrowedFolder);
            if (!folder.exists())
                folder.mkdirs();
            File file = new File(borrowedFolder + regNumber + ".txt");
            if (!file.exists())
                file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("Borrowed at: " + LocalDate.now() + "\n");
            for (Book b : books) {
                writer.write(b.getTitle() + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error saving borrowed books.");
        }
    }

    @Override
    public void deleteBorrowedBooks(String regNumber) {
        File file = new File(borrowedFolder + regNumber + ".txt");
        if (file.exists())
            file.delete();
    }
}