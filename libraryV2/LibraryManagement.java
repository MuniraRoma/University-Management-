package libraryV2;

import java.util.*;
import java.time.LocalDate;
import libraryV2.*;
import libraryV2.interfaces.BookRepository;
import libraryV2.interfaces.FinePolicy;

public class LibraryManagement {

    private static final Scanner input = new Scanner(System.in);
    // private static final Main mainClass = new Main();
    private static final BookRepository repository = new FileBookRepository();
    private static final FinePolicy finePolicy = new StandardFinePolicy();

    public static void manage(String regNumber) {

        System.out.println("\n*******************************************************\n");
        System.out.println("\tLibrary Management System\n");
        System.out.println("*******************************************************\n");

        System.out.print("1. Visit Library\n");
        System.out.print("2. Previous Menu\n");
        System.out.print("3. Exit\n");
        System.out.println();

        boolean flag = false;
        while (true) {
            try {
                System.out.print("Enter your choice : ");
                int c = input.nextInt();

                switch (c) {
                    case 1: {
                        flag = true;
                        libraryMenu(regNumber);
                        break;
                    }
                    case 2: {
                        flag = true;
                        // mainClass.management(regNumber);
                        break;
                    }
                    case 3: {
                        System.exit(0);
                        break;
                    }
                }
                if (flag)
                    break;
                else {
                    System.out.print("\nInvalid input, Try Again...\n");
                    input.nextLine();
                }
            } catch (Exception e) {
                System.out.print("\nInvalid input, Try Again...\n");
                input.nextLine();
            }
        }
    }

    private static void libraryMenu(String regNumber) {
        boolean flag = true;
        while (flag) {
            System.out.println("******************************************");
            System.out.println("Welcome to Library Managament System!");
            System.out.println("Enter 1 to search books ");
            System.out.println("Enter 2 to read membership criteria ");
            System.out.println("Enter 3 to borrow books ");
            System.out.println("Enter 4 to return books ");
            System.out.println("Enter 5 to previous menu");
            System.out.println("Enter 6 to exit");
            System.out.println("******************************************");

            try {
                System.out.print("Enter your choice: ");
                int choice = input.nextInt();
                input.nextLine();

                switch (choice) {
                    case 1:
                        searchBook();
                        break;
                    case 2:
                        library.TermsAndConditions.displayTerms();
                        break;
                    case 3:
                        borrowBook(regNumber);
                        break;
                    case 4:
                        returnBook(regNumber);
                        break;
                    case 5:
                        flag = false;
                        break;
                    case 6:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid Input! ");
                        break;
                }

            } catch (Exception e) {
                System.out.println("Invalid Input Try again!\n");
                input.nextLine();
            }
        }
    }

    private static void borrowBook(String regNumber) {
        System.out.println("*************************************************");
        System.out.println("Available books are: ");

        List<Book> available = repository.getAvailableBooks();
        if (available.isEmpty()) {
            System.out.println("!!!!!!! No Record Found !!!!!!!\n");
            return;
        }

        for (int i = 0; i < available.size(); i++) {
            System.out.println((i + 1) + ". " + available.get(i).getTitle());
        }

        System.out.println("*************************************************");

        boolean valid = false;
        while (!valid) {
            try {
                System.out.print("How many books you want to borrow: ");
                int numBooks = input.nextInt();
                input.nextLine();

                if (numBooks > 0 && numBooks <= available.size()) {
                    List<Book> borrowed = new ArrayList<>();
                    for (int i = 0; i < numBooks; i++) {
                        System.out.print("Which book you want to borrow, Enter its number: ");
                        int index = input.nextInt() - 1;
                        input.nextLine();

                        if (index >= 0 && index < available.size()) {
                            borrowed.add(available.get(index));
                        } else {
                            System.out.println("Invalid Book Number");
                            i--;
                        }
                    }

                    // Save borrowed books
                    repository.saveBorrowedBooks(regNumber, borrowed);
                    available.removeAll(borrowed);
                    repository.saveAvailableBooks(available);

                    System.out.println("Successfully Borrowed " + numBooks + " books");
                    valid = true;

                } else {
                    System.out.println("Invalid Input! Only books " + available.size() + " are available.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input! Try Again");
                input.nextLine();
            }
        }
    }

    private static void returnBook(String regNumber) {
        List<Book> borrowed = repository.getBorrowedBooks(regNumber);
        if (borrowed.isEmpty()) {
            System.out.println("You have not borrowed any book!\n");
            return;
        }

        System.out.print("\nYou have borrowed following books : \n");
        for (Book b : borrowed) {
            System.out.println(b.getTitle());
        }

        boolean flag = false;
        while (!flag) {
            try {
                System.out.print("\n************************************\n");
                System.out.print("\n1. Return books\n");
                System.out.print("2. Previous menu\n");
                System.out.print("Enter your choice : ");
                int choice = input.nextInt();
                input.nextLine();

                if (choice == 1) {
                    LocalDate borrowedDate = LocalDate.now().minusDays(40); // keep old logic
                    LocalDate returnDate = LocalDate.now();
                    int fine = finePolicy.calculateFine(borrowedDate, returnDate);
                    if (fine > 0)
                        System.out.println("Fine: " + fine + " rupees.");
                    else
                        System.out.println("No fine.");

                    // Return books
                    List<Book> available = repository.getAvailableBooks();
                    available.addAll(borrowed);
                    repository.saveAvailableBooks(available);
                    repository.deleteBorrowedBooks(regNumber);

                    System.out.println("Books has been returned Successfully!");
                    flag = true;

                } else if (choice == 2) {
                    flag = true;
                    System.out.println("Return to previous menu..");
                } else {
                    System.out.println("Invalid choice... Try again!\n");
                }

            } catch (Exception e) {
                System.out.println("Invalid choice... Try again!" + "\n");
                input.nextLine();
            }
        }
    }

    private static void searchBook() {
        System.out.print("Enter book name you want to search: ");
        String name = input.nextLine().toLowerCase();

        List<Book> available = repository.getAvailableBooks();
        boolean found = false;
        for (Book b : available) {
            if (b.getTitle().toLowerCase().contains(name)) {
                System.out.println(b.getTitle());
                found = true;
            }
        }
        if (!found) {
            System.out.printf("No book available with name \"%s\" Try Any other! \n", name);
        }
    }
}
