import java.util.*;

public class TransportManagement {

	private Scanner input = new Scanner(System.in);

	private Map<Integer, MenuAction> actions = new HashMap<>();

	private String regNumber;


	public TransportManagement() {

		actions.put(1, new VisitTMSAction());
		actions.put(2, new PreviousMenuAction());
		actions.put(3, new ExitAction());

	}


	public void manage(String regNumber){

		this.regNumber = regNumber;

		System.out.println("\n*******************************************************\n");
		System.out.println("\tTransport Management System\n");
		System.out.println("*******************************************************\n");

		displayMenu();

		while(true){

			try{

				System.out.print("Enter your choice : ");
				int choice = input.nextInt();

				MenuAction action = actions.get(choice);

				if(action != null){
					action.execute();
				}
				else{
					System.out.println("Invalid choice! Try again.");
				}

			}
			catch(Exception e){
				System.out.println("Invalid input! Try again.");
				input.nextLine();
			}

		}
	}


	private void displayMenu(){

		System.out.println("1. Visit Transport Management System");
		System.out.println("2. Previous Menu");
		System.out.println("3. Exit");

	}


	// ===== Interface =====

	interface MenuAction{
		void execute();
	}


	// ===== Actions =====

	class VisitTMSAction implements MenuAction{

		public void execute(){

			TMS transportSystem = new TMS();

			transportSystem.manage(regNumber);

		}
	}


	class PreviousMenuAction implements MenuAction{

		public void execute(){

			return;

		}
	}


	class ExitAction implements MenuAction{

		public void execute(){

			System.exit(0);

		}
	}

}