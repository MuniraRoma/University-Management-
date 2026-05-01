import java.util.Arrays;
import java.util.List;


public class TimeService {


   private final List<String> pickupTimes =
           Arrays.asList("8 AM", "10 AM", "12 PM");


   private final List<String> dropTimes =
           Arrays.asList("1:30 PM", "2:30 PM", "5:30 PM");


   public void displayPickupTimes() {
       for (int i = 0; i < pickupTimes.size(); i++)
           System.out.println((i + 1) + ". " + pickupTimes.get(i));
   }


   public void displayDropTimes() {
       for (int i = 0; i < dropTimes.size(); i++)
           System.out.println((i + 1) + ". " + dropTimes.get(i));
   }


   public String getPickup(int index) {
       return pickupTimes.get(index - 1);
   }


   public String getDrop(int index) {
       return dropTimes.get(index - 1);
   }
}
