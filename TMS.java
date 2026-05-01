import java.util.*;

public class TMS {

    private RouteService routeService = new RouteService();
    private FileService fileService = new FileService();
    private PaymentService paymentService = new PaymentService();
    private BusService busService = new BusService(routeService);

    private Scanner input = new Scanner(System.in);
    private boolean feePaid = false;

    private Map<Integer, MenuAction> actions = new HashMap<>();


    public TMS() {

        actions.put(1, new RoutesAction());
        actions.put(2, new FeesAction());
        actions.put(3, new RegisterAction());
        actions.put(4, new UpdateAction());
        actions.put(5, new ProfileAction());
        actions.put(6, new PayFeeAction());
        actions.put(7, new DeleteAction());
        actions.put(8, new ExitAction());

    }


    public void manage(String regNo) {

        while (true) {

            displayMenu();

            int choice = input.nextInt();

            MenuAction action = actions.get(choice);

            if (action != null) {
                action.execute(regNo);
            } else {
                System.out.println("Invalid Option!");
            }

        }
    }


    private void displayMenu() {
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
    }


    // ===== Interface =====

    interface MenuAction {
        void execute(String regNo);
    }


    // ===== Actions =====

    class RoutesAction implements MenuAction {
        public void execute(String regNo) {
            routeService.displayRoutes();
        }
    }


    class FeesAction implements MenuAction {
        public void execute(String regNo) {
            routeService.displayFees();
        }
    }


    class RegisterAction implements MenuAction {
        public void execute(String regNo) {

            Bus bus = busService.register(regNo);
            fileService.save(bus);

            System.out.println("=====Registered Successfully!=====");
        }
    }


    class UpdateAction implements MenuAction {
        public void execute(String regNo) {

            Bus bus = busService.update(regNo);
            fileService.save(bus);

            System.out.println("=====Updated Successfully!=====");
        }
    }


    class ProfileAction implements MenuAction {
        public void execute(String regNo) {

            if (feePaid)
                fileService.read(regNo);
            else
                System.out.println("=====Pay fee first!=====");
        }
    }


    class PayFeeAction implements MenuAction {

        public void execute(String regNo) {

            System.out.println("""
                    1. JazzCash
                    2. HBL
                    3. Alfalah
                    """);

            int option = input.nextInt();

            paymentService.pay(option);

            feePaid = true;
        }
    }


    class DeleteAction implements MenuAction {
        public void execute(String regNo) {
            fileService.delete(regNo);
        }
    }


    class ExitAction implements MenuAction {
        public void execute(String regNo) {
            System.out.println("Exiting Transport Management...");
            return;
        }
    }

}