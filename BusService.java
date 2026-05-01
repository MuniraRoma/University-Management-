import java.util.Random;
import java.util.Scanner;


public class BusService {


   private RouteService routeService;
   private TimeService timeService = new TimeService();
   private Scanner input = new Scanner(System.in);


   public BusService(RouteService routeService) {
       this.routeService = routeService;
   }


   public Bus register(String regNo) {


       routeService.displayRoutes();


       String route;
       while (true) {
           System.out.print("Select Route: ");
           route = input.next();
           if (routeService.isValidRoute(route)) break;
           System.out.println("==========Invalid route!==========\n");
       }


       System.out.println("Available Stops: " + routeService.getStops(route));
       input.nextLine();
       System.out.print("Enter Stop: ");
       String stop = input.nextLine();


       String fee = routeService.getFee(route);


       System.out.println("Pickup Times:");
       timeService.displayPickupTimes();
       int pickupChoice = input.nextInt();
       String pickup = timeService.getPickup(pickupChoice);


       System.out.println("Drop Times:");
       timeService.displayDropTimes();
       int dropChoice = input.nextInt();
       String drop = timeService.getDrop(dropChoice);


       String busId = String.valueOf(new Random().nextInt(9000) + 1000);


       return new Bus(regNo, busId, route, stop, fee, pickup, drop);
   }


   public Bus update(String regNo) {
       System.out.println("==========Update Information==========\n");
       return register(regNo);
   }
}
