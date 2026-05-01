import java.awt.Desktop;
import java.net.URI;


public class PaymentService {


   public void pay(int option) {


       try {
           Desktop desk = Desktop.getDesktop();


           switch (option) {
               case 1 -> desk.browse(new URI("https://www.jazzcash.com.pk"));
               case 2 -> desk.browse(new URI("https://www.hblibank.com.pk"));
               case 3 -> desk.browse(new URI("https://netbanking.bankalfalah.com/"));
               default -> System.out.println("==========Invalid option==========\n");
           }


           System.out.println("==========Payment Done!==========\n");


       } catch (Exception e) {
           System.out.println("==========Payment Error!==========\n");
       }
   }
}
