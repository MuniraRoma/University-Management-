import java.awt.Desktop;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class PaymentService {

    private Map<Integer, PaymentAction> actions = new HashMap<>();

    public PaymentService() {

        actions.put(1, new JazzCashPayment());
        actions.put(2, new HBLPayment());
        actions.put(3, new AlfalahPayment());

    }

    public void pay(int option) {

        PaymentAction action = actions.get(option);

        if(action == null){
            System.out.println("Invalid payment option!");
            return;
        }

        try{

            action.execute();
            System.out.println("Payment Done!");

        }
        catch(Exception e){
            System.out.println("Payment Error!");
        }
    }


    interface PaymentAction{

        void execute() throws Exception;

    }


    class JazzCashPayment implements PaymentAction{

        public void execute() throws Exception{

            Desktop.getDesktop().browse(new URI("https://www.jazzcash.com.pk"));

        }

    }


    class HBLPayment implements PaymentAction{

        public void execute() throws Exception{

            Desktop.getDesktop().browse(new URI("https://www.hblibank.com.pk"));

        }

    }


    class AlfalahPayment implements PaymentAction{

        public void execute() throws Exception{

            Desktop.getDesktop().browse(new URI("https://netbanking.bankalfalah.com/"));

        }

    }

}