import java.util.*;
import java.util.function.Supplier;

public class TMS {

    private RouteService routeService = new RouteService();
    private FileService fileService = new FileService();
    private PaymentService paymentService = new PaymentService();
    private BusService busService = new BusService(routeService);

    private Scanner input = new Scanner(System.in);
    private boolean feePaid = false;

    private MenuActionFactory actionFactory = new MenuActionFactory();

    public void manage(String regNo) {
        while (true) {
            displayMenu();
            int choice = input.nextInt();

            MenuAction action = actionFactory.createAction(choice);
            if (action != null) {
                action.execute(regNo);
                if (action instanceof ExitAction) {
                    break;
                }
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

    // ===== Factory without switch =====
    class MenuActionFactory {

        private final Map<Integer, Supplier<MenuAction>> actionMap = new HashMap<>();

        public MenuActionFactory() {
            actionMap.put(1, RoutesAction::new);
            actionMap.put(2, FeesAction::new);
            actionMap.put(3, RegisterAction::new);
            actionMap.put(4, UpdateAction::new);
            actionMap.put(5, ProfileAction::new);
            actionMap.put(6, PayFeeAction::new);
            actionMap.put(7, DeleteAction::new);
            actionMap.put(8, ExitAction::new);
        }

        public MenuAction createAction(int choice) {
            Supplier<MenuAction> actionSupplier = actionMap.get(choice);
            return actionSupplier != null ? actionSupplier.get() : null;
        }
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
        }
    }
}