import java.util.*;

public class FoodSelection {

	private static final Scanner scanner = new Scanner(System.in);

	/**
	 * Displays the food category menu and delegates to the appropriate food type handler.
	 *
	 * @param regNumber the student's registration number
	 */
	public static void food(String regNumber) {
		while (true) {
			printFoodCategoryMenu();

			int choice = getValidChoice();

			switch (choice) {
				case 1:
					handleFastFood(regNumber);
					break;

				case 2:
					handleDesiFood(regNumber);
					break;

				case 0:
					System.out.println("********************************************");
					return;  // Exit back to previous menu

				default:
					System.out.println("Please choose a correct option.");
					System.out.println("********************************************");
			}
		}
	}

	private static void printFoodCategoryMenu() {
		System.out.println("\n===== FOOD CATEGORIES =====");
		System.out.println("1. Fast Food");
		System.out.println("2. Desi Food");
		System.out.println("0. Nothing / Go Back");
		System.out.println("==========================");
	}

	private static int getValidChoice() {
		System.out.print("Enter Your Choice: ");
		while (true) {
			try {
				int choice = scanner.nextInt();
				scanner.nextLine(); // consume newline
				return choice;
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Please enter a number.");
				scanner.nextLine();
			}
		}
	}

	private static void handleFastFood(String regNumber) {
		System.out.println("\nEntering Fast Food section...");
		System.out.println("----------------------------------------");

		// Option A: Call the static method (recommended - cleaner)
		MyFastFood.FastfoodBill();

		// Option B: If you prefer instance style (what you had originally)
		// MyFastFood fastFood = new MyFastFood();
		// fastFood.FastfoodBill(regNumber);

		System.out.println("********************************************");
	}

	private static void handleDesiFood(String regNumber) {
		System.out.println("\nEntering Desi Food section...");
		System.out.println("----------------------------------------");

		// Option A: Call the static method (recommended)
		MyDesiFood.DesifoodBill();

		// Option B: If you prefer instance style
		// MyDesiFood desiFood = new MyDesiFood();
		// desiFood.DesifoodBill(regNumber);

		System.out.println("********************************************");
	}

	/**
	 * Overloaded method – kept for compatibility or future use.
	 * Currently throws exception as in your original code.
	 */
	public void food(String[] regNumber) {
		throw new UnsupportedOperationException(
				"food(String[]) is not implemented. Use food(String regNumber) instead."
		);
	}
}
