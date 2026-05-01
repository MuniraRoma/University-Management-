package libraryV2;

import libraryV2.interfaces.FinePolicy;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class StandardFinePolicy implements FinePolicy {

    private final int maxDaysWithoutFine = 30;
    private final int finePerDay = 50;

    @Override
    public int calculateFine(LocalDate borrowedDate, LocalDate returnDate) {
        long daysLate = ChronoUnit.DAYS.between(borrowedDate, returnDate) - maxDaysWithoutFine;
        return daysLate > 0 ? (int) (daysLate * finePerDay) : 0;
    }
}
