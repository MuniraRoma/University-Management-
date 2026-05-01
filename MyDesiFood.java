import java.util.*;
import java.io.*;

public class MyDesiFood {

	private static final Scanner scanner = new Scanner(System.in);
	private static final java.util.Date DATE = new java.util.Date();

	// ─────────────────────────────────────────────
	//  Enum – single place to define all desi items (OCP)
	// ─────────────────────────────────────────────
	enum DesiDish {
		HALEEM              ("Haleem",               120),
		ROGAN_GOSHT         ("Rogan Gosht",          250),
		MATAR_PANEER        ("Matar Paneer",         150),
		ALOO_KA_PARATHA     ("Aloo Ka Paratha",      100),
		SPICY_SWEET_POTATOES("Spicy Sweet Potatoes",  80),
		CHOLE_PALAK         ("Chole Palak",          110),
		MASH_KI_DAL         ("Mash ki Dal",          130),
		BIRYANI             ("Biryani",              200),
		CHICKEN_QORMA       ("Chicken Quorma",       180),
		SAMOSA              ("Samosa",                25);

		final String displayName;
		final int price;

		DesiDish(String name, int price) {
			this.displayName = name;
			this.price = price;
		}

		static DesiDish fromChoice(int choice) {
			if (choice < 1 || choice > values().length) return null;
			return values()[choice - 1];
		}
	}

	// ─────────────────────────────────────────────
	//  Ordered item record
	// ─────────────────────────────────────────────
	static class OrderedDish {
		final DesiDish dish;
		final int orderedQuantity;
		int returnedQuantity = 0;

		OrderedDish(DesiDish dish, int qty) {
			this.dish = dish;
			this.orderedQuantity = qty;
		}

		int getFinalQuantity() {
			return Math.max(0, orderedQuantity - returnedQuantity);
		}

		int getFinalCost() {
			return getFinalQuantity() * dish.price;
		}

		void returnSome(int qty) {
			if (qty > 0 && qty <= orderedQuantity - returnedQuantity) {
				this.returnedQuantity += qty;
			}
		}

		String getBillLine() {
			int qty = getFinalQuantity();
			if (qty <= 0) return null;
			return String.format("%-22s %3d    %6d",
					dish.displayName, qty, getFinalCost());
		}
	}

	// ─────────────────────────────────────────────
	//  Main method – returns total for billing system
	// ─────────────────────────────────────────────
	public static int DesifoodBill() {
		List<OrderedDish> orders = new ArrayList<>();
		int grandTotal = 0;

		File personalFile = new File("cafe/CafeBills/DesiFood/" + Main.regNumber + ".txt");
		File sharedBillFile = new File("cafe/CafeBills/BillFastfood.txt");

		try (
				FileWriter personalWriter = new FileWriter(personalFile, true);
				FileWriter sharedWriter = new FileWriter(sharedBillFile, true)
		) {
			personalWriter.write("\nDate: " + DATE + "\n");

			while (true) {
				displayMenu();

				System.out.println("\nSelect your Desi food (0 = Finish)");
				System.out.print("Enter Your Choice: ");
				int choice = readNonNegativeInt();

				if (choice == 0) break;

				DesiDish selected = DesiDish.fromChoice(choice);
				if (selected == null) {
					System.out.println("Invalid option.");
					continue;
				}

				int qty = readPositiveQuantity();
				if (qty <= 0) continue;

				OrderedDish order = new OrderedDish(selected, qty);
				orders.add(order);

				personalWriter.write("You've ordered a " + selected.displayName + "\n");
				personalWriter.write("Quantity: " + qty + "\n");

				// Return logic
				if (askToReturn()) {
					int returnQty = readReturnQuantity(qty);
					if (returnQty > 0) {
						order.returnSome(returnQty);
						personalWriter.write("You've returned a " + selected.displayName + "\n");
						personalWriter.write("Quantity returned: " + returnQty + "\n");
					}
				}

				// Write to shared bill file (only if something remains)
				String billLine = order.getBillLine();
				if (billLine != null) {
					sharedWriter.write(billLine + "\n");
				}

				grandTotal += order.getFinalCost();
			}

			personalWriter.write("----------------------------------------\n");
			personalWriter.write("Total Amount: " + grandTotal + "\n");
			personalWriter.write("----------------------------------------\n\n");

			return grandTotal;

		} catch (IOException e) {
			System.out.println("File error: " + e.getMessage());
			return 0;
		}
	}

	// ─────────────────────────────────────────────
	//  Helpers – small, focused methods
	// ─────────────────────────────────────────────

	private static void displayMenu() {
		File menuFile = new File("cafe/CafeBills/DesiFoodMenu.txt");

		System.out.println("\n===== DESI FOOD MENU =====");

		if (!menuFile.exists() || !menuFile.canRead()) {
			// fallback
			for (int i = 0; i < DesiDish.values().length; i++) {
				DesiDish d = DesiDish.values()[i];
				System.out.printf("%2d. %-22s %5d TK\n", i+1, d.displayName, d.price);
			}
			System.out.println(" 0. Finish");
			return;
		}

		try (Scanner reader = new Scanner(menuFile)) {
			while (reader.hasNextLine()) {
				System.out.println(reader.nextLine());
			}
		} catch (Exception e) {
			System.out.println("Cannot read menu file → fallback used");
		}
	}

	private static int readNonNegativeInt() {
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

	private static int readPositiveQuantity() {
		System.out.print("Enter quantity: ");
		while (true) {
			int q = readNonNegativeInt();
			if (q > 0) return q;
			System.out.println("Quantity must be at least 1.");
		}
	}

	private static boolean askToReturn() {
		System.out.println("\n1. Return some quantity");
		System.out.println("   Any other number → keep all");
		System.out.print("Choice: ");
		return readNonNegativeInt() == 1;
	}

	private static int readReturnQuantity(int max) {
		System.out.print("Enter returned quantity (max " + max + "): ");
		while (true) {
			int q = readNonNegativeInt();
			if (q > max) {
				System.out.println("Cannot return more than ordered.");
				continue;
			}
			return q;
		}
	}

	// ─────────────────────────────────────────────
	//  Entry point – for testing or direct run
	// ─────────────────────────────────────────────
	public static void main(String[] args) {
		String regNumber = (args.length > 0) ? args[0] : "";
		if (regNumber.isEmpty()) {
			System.out.print("Enter registration number: ");
			regNumber = scanner.nextLine().trim();
		}

		int total = DesifoodBill();
		if (total > 0) {
			System.out.println("\nTotal desi food bill: " + total + " TK");
		} else {
			System.out.println("No items ordered.");
		}
	}
}
