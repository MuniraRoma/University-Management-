package libraryV2;

import libraryV2.interfaces.BookRepository;
import java.util.*;
import java.io.*;
import java.time.LocalDate;

public class FileBookRepository implements BookRepository {

    private final String availableFile = "libraryV2/AvailableBooks.txt";
    private final String borrowedFolder = "libraryV2/BorrowedBooks/";

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
                    borrowed.add(new Book(line, LocalDate.now().minusDays(40))); // preserve old logic for date
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
