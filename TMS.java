​​import java.util.Scanner;


public class TMS {


   private RouteService routeService = new RouteService();
   private FileService fileService = new FileService();
   private PaymentService paymentService = new PaymentService();
   private BusService busService = new BusService(routeService);


   public void manage(String regNo) {


       Scanner input = new Scanner(System.in);
       boolean feePaid = false;


       while (true) {


           System.out.println("""
                  
                   1. Routes
                   2. Fees
                   3. Register
                   4. Update Info
                   5. Profile
                   6. Pay Fee
                   7. Delete
                   8. Exit
                  
                   """);


           int choice = input.nextInt();


           switch (choice) {


               case 1 -> routeService.displayRoutes();


               case 2 -> routeService.displayFees();


               case 3 -> {
                   Bus bus = busService.register(regNo);
                   fileService.save(bus);
                   System.out.println("=====Registered Successfully!=====\n");
               }


               case 4 -> {
                   Bus bus = busService.update(regNo);
                   fileService.save(bus);
                   System.out.println("=====Updated Successfully!=====\n");
               }


               case 5 -> {
                   if (feePaid)
                       fileService.read(regNo);
                   else
                       System.out.println("=====Pay fee first!=====\n");
               }


               case 6 -> {
                   System.out.println("1.JazzCash\n 2.HBL\n 3.Alfalah\n");
                   int option = input.nextInt();
                   paymentService.pay(option);
                   feePaid = true;
               }


               case 7 -> fileService.delete(regNo);


               case 8 -> { return; }
           }
       }
   }
}
