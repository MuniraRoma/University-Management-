public class Bus {


   private String registrationNo;
   private String busId;
   private String route;
   private String stop;
   private String fee;
   private String pickupTime;
   private String dropTime;


   public Bus(String registrationNo, String busId,
              String route, String stop,
              String fee, String pickupTime,
              String dropTime) {


       this.registrationNo = registrationNo;
       this.busId = busId;
       this.route = route;
       this.stop = stop;
       this.fee = fee;
       this.pickupTime = pickupTime;
       this.dropTime = dropTime;
   }


   public String getRegistrationNo() {
       return registrationNo;
   }


   public String getDetails() {
       return """
               -----------------------------
               Registration No : %s
               Bus ID          : %s
               Route           : %s
               Stop            : %s
               Fee             : %s
               Pickup Time     : %s
               Drop Time       : %s
               -----------------------------
               """.formatted(
               registrationNo, busId, route,
               stop, fee, pickupTime, dropTime
       );
   }
}
