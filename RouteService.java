import java.util.*;


public class RouteService {


   private final Map<String, List<String>> routes = new HashMap<>();
   private final Map<String, String> fees = new HashMap<>();


   public RouteService() {


       routes.put("1", Arrays.asList("F-11", "Golra More", "Margalla Road"));
       routes.put("2", Arrays.asList("G-11", "G-10", "G-9"));
       routes.put("3", Arrays.asList("Askari XI", "Qasim Market"));
       routes.put("4", Arrays.asList("Bahria Town", "PWD", "Saddar"));


       fees.put("1", "20000");
       fees.put("2", "15000");
       fees.put("3", "14000");
       fees.put("4", "18000");
   }


   public void displayRoutes() {
       routes.forEach((k, v) ->
               System.out.println("Route " + k + " -> " + v));
   }


   public void displayFees() {
       fees.forEach((k, v) ->
               System.out.println("Route " + k + " Fee: " + v));
   }


   public boolean isValidRoute(String route) {
       return routes.containsKey(route);
   }


   public List<String> getStops(String route) {
       return routes.get(route);
   }


   public String getFee(String route) {
       return fees.get(route);
   }
}
