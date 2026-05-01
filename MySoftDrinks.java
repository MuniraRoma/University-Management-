import java.util.*;
import java.io.*;

public class MySoftDrinks {

	private static final Scanner scanner = new Scanner(System.in);
	private static final java.util.Date DATE = new java.util.Date();

	// ─────────────────────────────────────────────
	//  Enum – easy to add new drinks in the future
	// ─────────────────────────────────────────────
	enum SoftDrink {
		COCA_COLA  ("Coca-Cola",  60),
		PEPSI      ("Pepsi",      60),
		FANTA      ("Fanta",      60),
		MOUNTAIN_DEW("Dew",       60),   // assuming "Dew" means Mountain Dew
		STING      ("Sting",      70),
		SPRITE     ("Sprite",     60);

		final String displayName;
		final int pricePerUnit;

		SoftDrink(String displayName, int price) {
			this.displayName = displayName;
			this.pricePerUnit = price;
		}

		static SoftDrink fromChoice(int choice) {
			if (choice < 1 || choice > values().length) return null;
			return values()[choice - 1];
		}
	}

	// ─────────────────────────────────────────────
	//  Simple record-like class for each ordered item
	// ─────────────────────────────────────────────
	static class OrderedDrink {
		final SoftDrink drink;
		final int orderedQuantity;
		int returnedQuantity = 0;

		OrderedDrink(SoftDrink drink, int qty) {
			this.drink = drink;
			this.orderedQuantity = qty;
		}

		int getFinalQuantity() {
			return Math.max(0, orderedQuantity - returnedQuantity);
		}

		int getFinalCost() {
			return getFinalQuantity() * drink.pricePerUnit;
		}

		void returnQuantity(int qty) {
			if (qty > 0 && qty <= orderedQuantity - returnedQuantity) {
				this.returnedQuantity += qty;
			}
		}

		String getBillLine() {
			int finalQty = getFinalQuantity();
			if (finalQty <= 0) return null;
			return String.format("%-22s %3d    %6d",
					drink.displayName, finalQty, getFinalCost());
		}
	}

	// ─────────────────────────────────────────────
	//  Main business method – returns total for billing
	// ─────────────────────────────────────────────
	public static int SoftDrinkbill(String regNumber) {
		List<OrderedDrink> orders = new ArrayList<>();
		int grandTotal = 0;

		File personalLog = new File("cafe/CafeBills/MySoftDrinks/" + Main.regNumber + ".txt");
		File sharedBill  = new File("cafe/CafeBills/BillFastfood.txt");

		try (
				FileWriter personalWriter = new FileWriter(personalLog, true);
				FileWriter sharedWriter   = new FileWriter(sharedBill, true)
		) {
			personalWriter.write("\nDate: " + DATE + "\n");

			while (true) {
				displayMenu();

				System.out.println("\nSelect your Soft Drink (0 = Finish)");
				System.out.print("Enter Your Choice: ");
				int choice = readIntOrZero();

				if (choice == 0) break;

				SoftDrink selected = SoftDrink.fromChoice(choice);
				if (selected == null) {
					System.out.println("Invalid option.");
					continue;
				}

				int qty = readQuantity("Enter quantity: ");
				if (qty <= 0) continue;

				OrderedDrink order = new OrderedDrink(selected, qty);
				orders.add(order);

				personalWriter.write("You've ordered a " + selected.displayName + "\n");
				personalWriter.write("Quantity: " + qty + "\n");

				// Return option
				if (askForReturn()) {
					int returnQty = readReturnQty(qty);
					if (returnQty > 0) {
						order.returnQuantity(returnQty);
						personalWriter.write("You've returned a " + selected.displayName + "\n");
						personalWriter.write("Quantity returned: " + returnQty + "\n");
					}
				}

				// Write to shared bill file (only if something remains)
				String line = order.getBillLine();
				if (line != null) {
					sharedWriter.write(line + "\n");
				}

				grandTotal += order.getFinalCost();
			}

			// Summary in personal file
			personalWriter.write("----------------------------------------\n");
			personalWriter.write("Total Amount: " + grandTotal + "\n");
			personalWriter.write("----------------------------------------\n\n");

			return grandTotal;

		} catch (IOException e) {
			System.out.println("Error writing bill files: " + e.getMessage());
			return 0;
		}
	}

	// ─────────────────────────────────────────────
	//  Helper methods – small & focused (SRP)
	// ─────────────────────────────────────────────

	private static void displayMenu() {
		File menuFile = new File("cafe/CafeBills/SoftDrinkMenu.txt");

		System.out.println("\n===== SOFT DRINKS MENU =====");

		if (!menuFile.exists() || !menuFile.canRead()) {
			// fallback
			for (int i = 0; i < SoftDrink.values().length; i++) {
				SoftDrink d = SoftDrink.values()[i];
				System.out.printf("%2d. %-20s %4d TK\n", i+1, d.displayName, d.pricePerUnit);
			}
			System.out.println(" 0. Finish order");
			System.out.println("=============================");
			return;
		}

		try (Scanner reader = new Scanner(menuFile)) {
			while (reader.hasNextLine()) {
				System.out.println(reader.nextLine());
			}
		} catch (Exception e) {
			System.out.println("Cannot read menu file → using fallback");
		}
	}

	private static int readIntOrZero() {
		while (true) {
			try {
				int val = scanner.nextInt();
				scanner.nextLine();
				return Math.max(0, val);
			} catch (InputMismatchException e) {
				System.out.print("Please enter a number: ");
				scanner.nextLine();
			}
		}
	}

	private static int readQuantity(String prompt) {
		System.out.print(prompt);
		while (true) {
			int q = readIntOrZero();
			if (q > 0) return q;
			System.out.println("Quantity must be at least 1.");
		}
	}

	private static boolean askForReturn() {
		System.out.println("\n1. Return some quantity");
		System.out.println("   Any other number → keep all");
		System.out.print("Choice: ");
		return readIntOrZero() == 1;
	}

	private static int readReturnQty(int max) {
		System.out.print("Enter returned quantity (max " + max + "): ");
		while (true) {
			int q = readIntOrZero();
			if (q > max) {
				System.out.println("You cannot return more than ordered.");
				continue;
			}
			return q;
		}
	}

	// ─────────────────────────────────────────────
	//  Compatibility / testing entry point
	// ─────────────────────────────────────────────
	public static void main(String[] args) {
		String regNumber = (args.length > 0) ? args[0] : "";
		if (regNumber.isEmpty()) {
			System.out.print("Enter registration number: ");
			regNumber = scanner.nextLine().trim();
		}
		int total = SoftDrinkbill(regNumber);
		if (total > 0) {
			System.out.println("Total soft drinks bill: " + total + " TK");
		} else {
			System.out.println("No items ordered.");
		}
	}
}
