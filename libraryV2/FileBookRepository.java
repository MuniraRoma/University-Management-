package libraryV2;

import libraryV2.interfaces.BookRepository;
import java.util.*;
import java.io.*;
import java.time.LocalDate;

public class FileBookRepository implements BookRepository {

    private final String availableFile = "libraryV2/AvailableBooks.txt";
    private final String borrowedFolder = "libraryV2/BorrowedBooks/";

    // =========================================================
    // SMELL #5 FIXED: Duplicate code
    // ---------------------------------------------------------
    // BEFORE: The raw number 40 was written directly inside
    //         getBorrowedBooks() as minusDays(40).
    //         The exact same value was ALSO written in
    //         LibraryManagement.java as minusDays(40).
    //
    // WHY IT WAS A SMELL:
    //   The same magic number existed in two separate files.
    //   If the value ever needs to change, you must remember
    //   to update BOTH files. If you miss one, the two files
    //   silently behave differently — a hard-to-find bug.
    //
    // WHAT WAS CHANGED:
    //   Extracted the number into a named constant here.
    //   LibraryManagement.java has its own matching constant.
    //   Now the number has a name that explains its purpose,
    //   and each file has one clear place to update it.
    // =========================================================
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
                    books.add(new Book(line));
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
                    // SMELL #5 FIXED: Using the named constant instead of raw 40
                    borrowed.add(new Book(line, LocalDate.now().minusDays(SIMULATED_DAYS_BORROWED)));
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