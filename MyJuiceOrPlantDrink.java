import java.util.*;
import java.io.*;

// ─────────────────────────────────────────────
// SINGLE RESPONSIBILITY PRINCIPLE
// Each class has one reason to change
// ─────────────────────────────────────────────

// 1. ENUM for Juice/Plant Drinks - Single Responsibility: Define drink items
enum JuicePlantItem {
	MANGO("Mango Flavour", 50, 1),
	ORANGE("Orange Flavour", 40, 2),
	PINEAPPLE("Pinnapple Flaour", 70, 3),  // Keeping original spelling
	GRAPE("Grape Flavour", 50, 4),
	MINERAL_WATER("Mineral Water", 30, 5);

	private final String displayName;
	private final int price;
	private final int code;

	JuicePlantItem(String displayName, int price, int code) {
		this.displayName = displayName;
		this.price = price;
		this.code = code;
	}

	public String getDisplayName() { return displayName; }
	public int getPrice() { return price; }
	public int getCode() { return code; }

	public static Optional<JuicePlantItem> fromCode(int code) {
		return Arrays.stream(values())
				.filter(item -> item.code == code)
				.findFirst();
	}

	public static List<JuicePlantItem> getAllItems() {
		return Arrays.asList(values());
	}
}

// 2. ORDER ITEM - Single Responsibility: Represent an order item
class JuicePlantOrder {
	private final JuicePlantItem item;
	private final int quantity;
	private final int returnedQuantity;
	private final int finalQuantity;
	private final int totalPrice;

	private JuicePlantOrder(JuicePlantItem item, int quantity, int returnedQuantity) {
		this.item = item;
		this.quantity = quantity;
		this.returnedQuantity = returnedQuantity;
		this.finalQuantity = Math.max(0, quantity - returnedQuantity);
		this.totalPrice = this.finalQuantity * item.getPrice();
	}

	public static JuicePlantOrder create(JuicePlantItem item, int quantity) {
		return new JuicePlantOrder(item, quantity, 0);
	}

	public JuicePlantOrder withReturn(int returnQty) {
		return new JuicePlantOrder(this.item, this.quantity, returnQty);
	}

	public JuicePlantItem getItem() { return item; }
	public int getQuantity() { return quantity; }
	public int getReturnedQuantity() { return returnedQuantity; }
	public int getFinalQuantity() { return finalQuantity; }
	public int getTotalPrice() { return totalPrice; }
	public boolean hasReturns() { return returnedQuantity > 0; }
	public boolean isEmpty() { return finalQuantity <= 0; }

	public String getPersonalBillLine() {
		return String.format("You've ordered a %s\nQuantity: %d%s",
				item.getDisplayName(),
				quantity,
				hasReturns() ? "\nYou've returned: " + returnedQuantity : "");
	}

	public String getSharedBillLine() {
		if (isEmpty()) return null;
		return String.format("%-22s %3d    %6d",
				item.getDisplayName(), finalQuantity, totalPrice);
	}
}

// 3. MENU SERVICE - Single Responsibility: Handle menu display
interface JuicePlantMenuService {
	void displayMenu() throws Exception;
}

class FileBasedJuicePlantMenuService implements JuicePlantMenuService {
	private static final String MENU_FILE_PATH = "cafe/CafeBills/JuiceDrinkMenu.txt";

	@Override
	public void displayMenu() throws Exception {
		File menuFile = new File(MENU_FILE_PATH);

		if (!menuFile.exists()) {
			displayFallbackMenu();
			return;
		}

		try (Scanner reader = new Scanner(menuFile)) {
			String[] menuLines = new String[5];
			int i = 0;
			while (reader.hasNextLine() && i < menuLines.length) {
				menuLines[i] = reader.nextLine();
				i++;
			}

			for (String line : menuLines) {
				if (line != null) {
					System.out.println(line);
				}
			}
		}
	}

	private void displayFallbackMenu() {
		System.out.println("===== JUICE / PLANT DRINK MENU =====");
		System.out.println("1. Mango Flavour      50 TK");
		System.out.println("2. Orange Flavour     40 TK");
		System.out.println("3. Pinnapple Flaour   70 TK");
		System.out.println("4. Grape Flavour      50 TK");
		System.out.println("5. Mineral Water      30 TK");
		System.out.println("===================================");
	}
}

// 4. INPUT READER - Single Responsibility: Handle user input
interface JuicePlantInputReader {
	int readInt();
	int readPositiveInt();
	int readQuantity();
	int readReturnQuantity(int maxQuantity);
	boolean askForReturn();
	void close();
}

class JuicePlantConsoleInputReader implements JuicePlantInputReader {
	private final Scanner scanner;

	public JuicePlantConsoleInputReader() {
		this.scanner = new Scanner(System.in);
	}

	@Override
	public int readInt() {
		while (true) {
			try {
				int val = scanner.nextInt();
				scanner.nextLine();
				return val;
			} catch (InputMismatchException e) {
				System.out.println("Invalid! Enter Again");
				scanner.nextLine();
			}
		}
	}

	@Override
	public int readPositiveInt() {
		while (true) {
			int val = readInt();
			if (val >= 0) return val;
			System.out.println("Enter Again");
		}
	}

	@Override
	public int readQuantity() {
		System.out.print("Enter quantity: ");
		while (true) {
			int qty = readInt();
			if (qty > 0) return qty;
			System.out.println("Quantity must be positive. Enter Again:");
		}
	}

	@Override
	public int readReturnQuantity(int maxQuantity) {
		System.out.println("Enter returned quantity:");
		while (true) {
			int qty = readInt();
			if (qty >= 0 && qty <= maxQuantity) return qty;
			System.out.println("Invalid quantity. Must be between 0 and " + maxQuantity);
		}
	}

	@Override
	public boolean askForReturn() {
		System.out.println("\n1.Return");
		System.out.println("Press any Integer except '1'");
		return readInt() == 1;
	}

	@Override
	public void close() {
		// Don't close System.in
	}
}

// 5. BILL WRITER - Single Responsibility: Handle bill writing
interface JuicePlantBillWriter {
	void writeHeader(String regNumber, Date date) throws IOException;
	void writeOrder(JuicePlantOrder order) throws IOException;
	void writeTotal(int total) throws IOException;
	void close() throws IOException;
}

class JuicePlantPersonalBillWriter implements JuicePlantBillWriter {
	private final FileWriter writer;
	private static final String BASE_DIR = "cafe/CafeBills/MyJuiceOrPlantDrink/";

	public JuicePlantPersonalBillWriter(String regNumber) throws IOException {
		File dir = new File(BASE_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(dir, regNumber + ".txt");
		file.createNewFile();
		this.writer = new FileWriter(file, true);
	}

	@Override
	public void writeHeader(String regNumber, Date date) throws IOException {
		writer.write("\nDate: " + date + "\n");
	}

	@Override
	public void writeOrder(JuicePlantOrder order) throws IOException {
		writer.write(order.getPersonalBillLine() + "\n");
	}

	@Override
	public void writeTotal(int total) throws IOException {
		writer.write("Total Amount: " + total + "\n");
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}
}

class JuicePlantSharedBillWriter implements JuicePlantBillWriter {
	private final FileWriter writer;
	private static final String SHARED_BILL_FILE = "cafe/CafeBills/BillFastfood.txt";

	public JuicePlantSharedBillWriter() throws IOException {
		File dir = new File("cafe/CafeBills");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		this.writer = new FileWriter(new File(dir, "BillFastfood.txt"), true);
	}

	@Override
	public void writeHeader(String regNumber, Date date) throws IOException {
		// No header for shared bill
	}

	@Override
	public void writeOrder(JuicePlantOrder order) throws IOException {
		String billLine = order.getSharedBillLine();
		if (billLine != null) {
			writer.write(billLine + "\n");
		}
	}

	@Override
	public void writeTotal(int total) throws IOException {
		// No total in shared bill
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}
}

// 6. ORDER PROCESSOR - Single Responsibility: Process orders
class JuicePlantOrderProcessor {
	private final JuicePlantInputReader inputReader;
	private final List<JuicePlantOrder> orders;
	private int grandTotal;

	public JuicePlantOrderProcessor(JuicePlantInputReader inputReader) {
		this.inputReader = inputReader;
		this.orders = new ArrayList<>();
		this.grandTotal = 0;
	}

	public Optional<JuicePlantOrder> processOrder() {
		System.out.println("\nSelect your Juice Or Plant Drink");
		System.out.println("0. Nothing");
		System.out.print("Enter Your Choice: ");

		int choice = inputReader.readPositiveInt();

		if (choice == 0) {
			return Optional.empty();
		}

		Optional<JuicePlantItem> selectedItem = JuicePlantItem.fromCode(choice);

		if (!selectedItem.isPresent()) {
			System.out.println("Invalid option.");
			return Optional.empty();
		}

		JuicePlantItem item = selectedItem.get();
		System.out.println("You've ordered a " + item.getDisplayName());

		int quantity = inputReader.readQuantity();
		JuicePlantOrder order = JuicePlantOrder.create(item, quantity);

		// Handle returns
		if (inputReader.askForReturn()) {
			int returnQty = inputReader.readReturnQuantity(quantity);
			if (returnQty > 0) {
				order = order.withReturn(returnQty);
			}
		}

		return Optional.of(order);
	}

	public void addOrder(JuicePlantOrder order) {
		orders.add(order);
		grandTotal += order.getTotalPrice();
	}

	public List<JuicePlantOrder> getOrders() {
		return Collections.unmodifiableList(orders);
	}

	public int getGrandTotal() {
		return grandTotal;
	}

	public void clear() {
		orders.clear();
		grandTotal = 0;
	}
}

// 7. BILL SERVICE - Single Responsibility: Orchestrate bill generation
class JuicePlantBillGenerationService {
	private final JuicePlantMenuService menuService;
	private final JuicePlantInputReader inputReader;
	private final JuicePlantOrderProcessor orderProcessor;
	private final JuicePlantBillWriter personalWriter;
	private final JuicePlantBillWriter sharedWriter;
	private final String regNumber;
	private final Date date;

	public JuicePlantBillGenerationService(String regNumber,
										   JuicePlantMenuService menuService,
										   JuicePlantInputReader inputReader,
										   JuicePlantOrderProcessor orderProcessor,
										   JuicePlantBillWriter personalWriter,
										   JuicePlantBillWriter sharedWriter) {
		this.regNumber = regNumber;
		this.menuService = menuService;
		this.inputReader = inputReader;
		this.orderProcessor = orderProcessor;
		this.personalWriter = personalWriter;
		this.sharedWriter = sharedWriter;
		this.date = new Date();
	}

	public int generateBill() throws Exception {
		orderProcessor.clear();

		personalWriter.writeHeader(regNumber, date);

		while (true) {
			System.out.println();
			menuService.displayMenu();
			System.out.println();

			Optional<JuicePlantOrder> orderOpt = orderProcessor.processOrder();

			if (!orderOpt.isPresent()) {
				break;
			}

			JuicePlantOrder order = orderOpt.get();
			orderProcessor.addOrder(order);

			// Write to personal file
			personalWriter.writeOrder(order);

			// Write to shared bill file if not empty
			if (!order.isEmpty()) {
				sharedWriter.writeOrder(order);
			}
		}

		int grandTotal = orderProcessor.getGrandTotal();
		personalWriter.writeTotal(grandTotal);

		return grandTotal;
	}

	public void close() throws IOException {
		personalWriter.close();
		sharedWriter.close();
	}
}

// 8. SERVICE FACTORY - Dependency Inversion
class JuicePlantServiceFactory {

	public static JuicePlantBillGenerationService createService(String regNumber) throws IOException {
		JuicePlantMenuService menuService = new FileBasedJuicePlantMenuService();
		JuicePlantInputReader inputReader = new JuicePlantConsoleInputReader();
		JuicePlantOrderProcessor orderProcessor = new JuicePlantOrderProcessor(inputReader);
		JuicePlantBillWriter personalWriter = new JuicePlantPersonalBillWriter(regNumber);
		JuicePlantBillWriter sharedWriter = new JuicePlantSharedBillWriter();

		return new JuicePlantBillGenerationService(
				regNumber, menuService, inputReader,
				orderProcessor, personalWriter, sharedWriter
		);
	}
}

// 9. MAIN PUBLIC CLASS - Backward compatibility
public class MyJuiceOrPlantDrink {

	// Static fields for backward compatibility
	private static int total_amount = 0;
	private static int foodItem = 0;
	private static int quantity = 0;
	private static int quantity_return = 0;
	private static double itemPrice = 0;

	/**
	 * Main entry point - maintains backward compatibility
	 */
	public static void main(String[] regNumber) {
		String reg = (regNumber != null && regNumber.length > 0) ? regNumber[0] : "";

		if (reg.isEmpty()) {
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter registration number: ");
			reg = scanner.nextLine().trim();
		}

		int total = JuiceORPlantbill(reg);

		if (total > 0) {
			System.out.println("Total Juice/Plant Drinks Bill: " + total + " TK");
		}
	}

	/**
	 * Clean bill method - returns total for billing system
	 */
	public static int JuiceORPlantbill() {
		// Try to get regNumber from Main class or system property
		String regNumber = getRegistrationNumber();
		return JuiceORPlantbill(regNumber);
	}

	/**
	 * Overloaded method with registration number
	 */
	public static int JuiceORPlantbill(String regNumber) {
		try {
			// Reset static fields for new session
			resetStaticFields();

			JuicePlantBillGenerationService service = JuicePlantServiceFactory.createService(regNumber);
			int total = service.generateBill();
			service.close();

			// Update static field for backward compatibility
			total_amount = total;

			return total;

		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return 0;
		}
	}

	/**
	 * Original menu method - maintained for backward compatibility
	 */
	public static void menu() throws Exception {
		FileBasedJuicePlantMenuService menuService = new FileBasedJuicePlantMenuService();
		menuService.displayMenu();
	}

	/**
	 * Original itemPrice method - maintained for backward compatibility
	 */
	public static double itemPrice() {
		Optional<JuicePlantItem> item = JuicePlantItem.fromCode(foodItem);
		if (item.isPresent()) {
			itemPrice = item.get().getPrice();
		}
		return itemPrice;
	}

	/**
	 * Original subTotal method - maintained for backward compatibility
	 */
	public static double subTotal() {
		double subtotal = quantity * itemPrice;
		total_amount += subtotal;
		return subtotal;
	}

	/**
	 * Original returnItem method - maintained for backward compatibility
	 */
	public static double returnItem() {
		itemPrice();

		if (quantity_return < 0 || quantity_return > quantity) {
			System.out.println("Wrong Quantity.");
			return 0;
		}

		double returnAmount = quantity_return * itemPrice;
		total_amount -= returnAmount;
		return returnAmount;
	}

	/**
	 * Original JuiceORPlantbill method - maintained for backward compatibility
	 */
	public static int JuiceORPlantbillOld() {
		return total_amount;
	}

	// Helper methods
	private static String getRegistrationNumber() {
		// Try Main.regNumber first
		try {
			if (Main.regNumber != null && !Main.regNumber.isEmpty()) {
				return Main.regNumber;
			}
		} catch (Exception e) {
			// Main class might not exist
		}

		return System.getProperty("user.regNumber", "UNKNOWN");
	}

	private static void resetStaticFields() {
		total_amount = 0;
		foodItem = 0;
		quantity = 0;
		quantity_return = 0;
		itemPrice = 0;
	}
}

