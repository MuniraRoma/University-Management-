package libraryV2.interfaces;

import java.time.LocalDate;

public interface FinePolicy {
    int calculateFine(LocalDate borrowedDate, LocalDate returnDate);
}
