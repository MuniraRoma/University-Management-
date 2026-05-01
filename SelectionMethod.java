import java.util.*;
import java.io.*;

public class SelectionMethod {

	static Scanner input = new Scanner(System.in);
	static int option;
	static java.util.Date date = new java.util.Date();
	static String name;

	// ────────────────────────────────────────────────
	//  Interface for bill-providing categories
	// ────────────────────────────────────────────────
	interface BillProvider {
		int getBillAmount();
		String getCategoryName();   // used only for logging/debug if needed
	}

	// Concrete bill providers (each knows only its own responsibility)
	static class FastFoodBillProvider implements BillProvider {
		@Override public int getBillAmount() {
			return new MyFastFood().FastfoodBill();
		}
		@Override public String getCategoryName() { return "Fast Food"; }
	}

	static class DesiFoodBillProvider implements BillProvider {
		@Override public int getBillAmount() {
			return new MyDesiFood().DesifoodBill();
		}
		@Override public String getCategoryName() { return "Desi Food"; }
	}

	static class SoftDrinkBillProvider implements BillProvider {
		@Override public int getBillAmount() {
			return new MySoftDrinks().SoftDrinkbill(Main.regNumber);
		}
		@Override public String getCategoryName() { return "Soft Drinks"; }
	}

	static class CoffeeBillProvider implements BillProvider {
		@Override public int getBillAmount() {
			return new MyCoffee().CoffeeBill(Main.regNumber);
		}
		@Override public String getCategoryName() { return "Coffee"; }
	}

	static class JuicePlantDrinkBillProvider implements BillProvider {
		@Override public int getBillAmount() {
			return new MyJuiceOrPlantDrink().JuiceORPlantbill();
		}
		@Override public String getCategoryName() { return "Juice / Plant Drink"; }
	}

	// ────────────────────────────────────────────────
	//  Main menu loop
	// ────────────────────────────────────────────────
	public static void chooseMethod(String regNumber) throws Exception {

		final int INITIAL_AMOUNT = 2000;

		FoodSelection foodSelectionObj   = new FoodSelection();
		DrinkSelection drinkSelectionObj = new DrinkSelection();

		List<BillProvider> billProviders = Arrays.asList(
				new FastFoodBillProvider(),
				new DesiFoodBillProvider(),
				new SoftDrinkBillProvider(),
				new CoffeeBillProvider(),
				new JuicePlantDrinkBillProvider()
		);

		while (true) {
			printMainMenu();
			try {
				System.out.print("Enter Your Choice: ");
				option = input.nextInt();
				System.out.println();

				switch (option) {
					case 1:
						foodSelectionObj.food(regNumber);
						System.out.println("********************************************");
						break;

					case 2:
						drinkSelectionObj.myDrink(regNumber);
						System.out.println("********************************************");
						break;

					case 3:
						generateAndShowBill(regNumber, INITIAL_AMOUNT, billProviders);
						break;

					case 4:
						clearTemporaryBillFile();
						CafeManagement.manage(regNumber);
						return;   // usually we return after going back to previous menu

					case 5:
						clearTemporaryBillFile();
						System.out.println("********************************************");
						System.exit(0);

					default:
						System.out.println("Please select correct option");
				}
			} catch (InputMismatchException e) {
				System.out.println("Invalid Inputs");
				input.nextLine();
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}

	// ────────────────────────────────────────────────
	//  Separated responsibilities
	// ────────────────────────────────────────────────

	private static void printMainMenu() {
		System.out.println("1. Food");
		System.out.println("2. Drinks");
		System.out.println("3. Bill Generate");
		System.out.println("4. Previous Menu");
		System.out.println("5. Exit");
		System.out.println();
	}

	private static void clearTemporaryBillFile() throws IOException {
		File tempFile = new File("cafe/CafeBills/BillFastfood.txt");
		try (PrintWriter writer = new PrintWriter(tempFile)) {
			writer.print("");
		}
	}

	private static String readStudentName(String regNumber) throws FileNotFoundException {
		File studentFile = new File("students_data/" + regNumber + ".txt");
		try (Scanner sc = new Scanner(studentFile)) {
			String[] parts = sc.nextLine().split(":", 2);
			if (parts.length >= 2) {
				return parts[1].trim();
			}
		}
		return "Unknown";
	}

	private static List<String> readBillItems() throws FileNotFoundException {
		File billFile = new File("cafe/CafeBills/BillFastfood.txt");
		List<String> lines = new ArrayList<>();

		try (Scanner reader = new Scanner(billFile)) {
			while (reader.hasNextLine()) {
				String line = reader.nextLine().trim();
				if (!line.isEmpty()) {
					lines.add(line);
				}
			}
		}
		return lines;
	}

	private static void generateAndShowBill(String regNumber, int accountBalance, List<BillProvider> providers)
			throws Exception {

		name = readStudentName(regNumber);

		// Calculate total
		int total = 0;
		for (BillProvider provider : providers) {
			total += provider.getBillAmount();
		}

		if (total == 0) {
			System.out.println("Please Buy Something First");
			return;
		}

		if (total > accountBalance) {
			System.out.println("Sir, You don't have enough Account Balance");
			return;
		}

		// ──── We can pay ────
		List<String> billLines = readBillItems();

		System.out.println("\n\n\n");
		System.out.println("\t\t\t-----------------------------------");
		System.out.println("\t\t\tDate: " + date);
		System.out.println("\t\t\tName: " + name);
		System.out.println("\t\t\tStudent ID: " + regNumber);
		System.out.println("\t\t\t---------Thanks For Coming---------");
		System.out.println("\t\t\t-----------Your Bill Is------------\n");
		System.out.println("\t\t\tItems Quantity Prices");

		for (String line : billLines) {
			System.out.println("\t\t\t" + line);
		}

		System.out.printf("\t\t\tTotal Bill %d \n", total);
		System.out.println("\t\t\t---------------------------------------");
		System.out.println("\t\t\t---------------------------------------");
		System.out.println("\t\t\t---------------------------------------");
		System.out.println();

		// Save final bill
		saveFinalBill(regNumber, billLines, total);
	}

	private static void saveFinalBill(String regNumber, List<String> items, int total) throws IOException {
		File finalBillFile = new File("cafe/CafeBills/FinalBills/" + regNumber + ".txt");

		// Create parent directories if needed
		finalBillFile.getParentFile().mkdirs();

		try (FileWriter writer = new FileWriter(finalBillFile, true);
			 PrintWriter pw = new PrintWriter(writer)) {

			pw.println("Date: " + date);
			pw.println("-----------------------------------");
			pw.println("Name : " + name);
			pw.println("Student ID : " + regNumber);
			pw.println("-----------------------------------");
			pw.println("---------Thanks For Coming---------");
			pw.println("-----------Your Bill Is------------");
			pw.println("\t\t\tItems Quantity Prices");

			for (String item : items) {
				pw.println("\t\t\t" + item);
			}

			pw.println("Total Bill " + total);
			pw.println("-----------------------------------");
			pw.println("-----------------------------------");
			pw.println();
			pw.println();
		}
	}
}
