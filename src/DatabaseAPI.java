import java.util.*;
import java.io.*;

/**
 * 
 * CS 5V81.001: Implementation of data structures and algorithms - Project 3
 * 
 * Multi-dimensional search
 * 
 * @author Sreesha Nagaraj
 */
public class DatabaseAPI {
	static int[] categories;
	static final int NUM_CATEGORIES = 1000, MOD_NUMBER = 997;
	static int DEBUG = 9;
	private int phase = 0;
	private long startTime, endTime, elapsedTime;
	private TreeMap<Long, Customer> db = new TreeMap<>(); // stores all the
															// customers

	/**
	 * Class that represents a Customer
	 * 
	 */
	public class Customer {

		private long id; // primary key
		private HashSet<Integer> categories; // Set of department codes of the
												// company (encoded as an
												// integer in [1,999]) that
												// stock items of interest to
												// the customer.
		private float amount; // stores the total revenue that the customer has
								// generated for the company
		private int purchases; // The number of times customer has purchased
								// products

		// getters and setters

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public HashSet<Integer> getCategories() {
			return categories;
		}

		public void setCategories(HashSet<Integer> categories) {
			this.categories = categories;
		}

		public float getAmount() {
			return amount;
		}

		public void setAmount(float amount) {
			this.amount = amount;
		}

		public int getPurchases() {
			return purchases;
		}

		public void setPurchases(int purchases) {
			this.purchases = purchases;
		}

		/**
		 * Method to check if a two customers have atleast 5 categories of
		 * interest in common
		 * 
		 * @param nextCust
		 *            : Customer - customer with which the calling customer is
		 *            compared with
		 * @return Boolean: returns true if atleast 5 categories match with each
		 *         other
		 */

		public boolean containsAtleastFive(Customer nextCust) {

			if (this.categories.containsAll(nextCust.categories)
					|| nextCust.categories.containsAll(this.categories)) {
				return true;
			} else {

				ArrayList<Integer> listCat1 = new ArrayList<>();
				for (Integer cat : this.categories) {
					listCat1.add(cat);
				}

				ArrayList<Integer> listCat2 = new ArrayList<>();
				for (Integer cat : nextCust.categories) {
					listCat2.add(cat);
				}

				int size = listCat1.size() > listCat2.size() ? listCat2.size()
						: listCat1.size();

				int i = 0;
				int j = 0;
				int count = 0; // keeps track of matching category count
				for (int k = 0; k < size; k++) {
					if (count >= 5) {
						return true;
					} else {
						if (listCat1.get(i).equals(listCat2.get(j))) {
							i++;
							j++;
							count++;
						} else if (listCat1.get(i).compareTo(listCat2.get(j)) < 0) {
							i++;
						} else {
							j++;
						}
					}

				}
				return false;

			}

		}

	}

	public static void main(String[] args) throws FileNotFoundException {
		categories = new int[NUM_CATEGORIES];
		Scanner in;
		if (args.length > 0) {
			in = new Scanner(new File(args[0]));
		} else {
			in = new Scanner(System.in);
		}
		DatabaseAPI x = new DatabaseAPI();
		x.timer();
		long rv = x.driver(in);
		System.out.println(rv);
		x.timer();
	}

	/**
	 * Read categories from in until a 0 appears. Values are copied into static
	 * array categories. Zero marks end.
	 * 
	 * @param in
	 *            : Scanner from which inputs are read
	 * @return : Number of categories scanned
	 */
	public static int readCategories(Scanner in) {
		int cat = in.nextInt();
		int index = 0;
		while (cat != 0) {
			categories[index++] = cat;
			cat = in.nextInt();
		}
		categories[index] = 0;
		return index;
	}

	public long driver(Scanner in) {
		String s;
		long rv = 0, id;
		int cat;
		double purchase;

		while (in.hasNext()) {
			s = in.next();
			if (s.charAt(0) == '#') { // comment
				s = in.nextLine(); // go to next line
				continue;
			}
			if (s.equals("Insert")) {
				id = in.nextLong();
				readCategories(in);
				rv += insert(id, categories);
			} else if (s.equals("Find")) {
				id = in.nextLong();
				rv += find(id);
			} else if (s.equals("Delete")) {
				id = in.nextLong();
				rv += delete(id);
			} else if (s.equals("TopThree")) {
				cat = in.nextInt();
				rv += topthree(cat);
			} else if (s.equals("AddInterests")) {
				id = in.nextLong();
				readCategories(in);
				rv += addinterests(id, categories);
			} else if (s.equals("RemoveInterests")) {
				id = in.nextLong();
				readCategories(in);
				rv += removeinterests(id, categories);
			} else if (s.equals("AddRevenue")) {
				id = in.nextLong();
				purchase = in.nextDouble();
				rv += addrevenue(id, purchase);
			} else if (s.equals("Range")) {
				double low = in.nextDouble();
				double high = in.nextDouble();
				rv += range(low, high);
			} else if (s.equals("SameSame")) {
				rv += samesame();
			} else if (s.equals("NumberPurchases")) {
				id = in.nextLong();
				rv += numberpurchases(id);
			} else if (s.equals("End")) {
				return rv % 997;
			} else {
				System.out
						.println("Houston, we have a problem.\nUnexpected line in input: "
								+ s);
				System.exit(0);
			}
		}
		// This can be inside the loop, if overflow is a problem
		rv = rv % MOD_NUMBER;

		return rv;
	}

	public void timer() {
		if (phase == 0) {
			startTime = System.currentTimeMillis();
			phase = 1;
		} else {
			endTime = System.currentTimeMillis();
			elapsedTime = endTime - startTime;
			System.out.println("Time: " + elapsedTime + " msec.");
			memory();
			phase = 0;
		}
	}

	public void memory() {
		long memAvailable = Runtime.getRuntime().totalMemory();
		long memUsed = memAvailable - Runtime.getRuntime().freeMemory();
		System.out.println("Memory: " + memUsed / 1000000 + " MB / "
				+ memAvailable / 1000000 + " MB.");
	}

	/**
	 * Method to add a new customer (with amount = 0) who is interested in the
	 * given set of categories.
	 * 
	 * @param categories
	 *            []: int[] - An array of categories that the customer is
	 *            interested in.
	 * @param id
	 *            : long - Customers are uniquely identified by their id (key
	 *            field).
	 * @return : Returns 1 if the operation was successful, and -1 if there is
	 *         already another customer with the same id (in which case no
	 *         changes are made).
	 */

	int insert(long id, int[] categories) {

		// check if the customer exists
		if (!db.containsKey(id)) {

			Customer customer = new Customer();
			customer.setId(id);
			customer.setAmount(0.0f);
			customer.setPurchases(0);

			HashSet<Integer> cat = new HashSet<>();
			for (int i = 0; i < categories.length && categories[i] != 0; i++) {
				cat.add(categories[i]);
			}
			customer.setCategories(cat);
			db.put(id, customer);

			return 1;
		} else {

			return -1;
		}

	}

	/**
	 * Method to find mount spent by customer until now
	 * 
	 * @param id
	 *            : long - Customers are uniquely identified by their id (key
	 *            field).
	 * @return : int - Amount field of the customer
	 * 
	 */

	int find(long id) {

		if (!db.containsKey(id)) {
			return -1;
		}

		else {
			Customer customer = db.get(id); // fetch the customer

			return (int) customer.getAmount();
		}

	}

	/**
	 * Method to delete customer's records from storage.
	 * 
	 * @param id
	 *            : long - Customers are uniquely identified by their id (key
	 *            field).
	 * @return : int - Amount field of the deleted customer
	 * 
	 */

	int delete(long id) {
		if (!db.containsKey(id)) {
			return -1;
		}

		else {
			Customer customer = db.get(id); // fetch the customer
			int amount = (int) customer.getAmount();
			db.remove(id);

			return amount;
		}

	}

	/**
	 * Method to find the top three customers (in terms of amount spent) who are
	 * interested in category k.
	 * 
	 * @param cat
	 *            : int - category of interest.
	 * @return : int - Returns the sum of the amounts of the top three
	 *         customers, truncated to just dollars
	 * 
	 */

	int topthree(int cat) {

		float sum = 0.0f;
		List<Customer> listOfInterest = new LinkedList<>();
		TreeSet<Float> setOfAmounts = new TreeSet<>(); // maintain a tree map of
														// amounts

		// find the top three customers (in terms of amount spent) who are
		// interested in category k.

		for (Customer customer : db.values()) { // for each customer

			// find the customers interested in category k

			if (customer.getCategories().contains(cat)) {
				listOfInterest.add(customer);
			}
		}

		for (Customer c : listOfInterest) {
			setOfAmounts.add(c.getAmount());
		}

		// fetch last three entries and return sum

		if (setOfAmounts.size() > 3) {
			for (int i = 0; i < 3; i++) {
				sum += setOfAmounts.pollLast();

			}
		} else {
			for (int i = 0; i <= setOfAmounts.size(); i++) {
				sum += setOfAmounts.pollLast();

			}
		}

		return (int) sum;
	}

	/**
	 * Method to add new interests to the list of a customer's categories. Some
	 * of them may already be in the list of categories of this customer.
	 * 
	 * @param id
	 *            : long - Customers are uniquely identified by their id (key
	 *            field).
	 * @param categories
	 *            : int[] - Array of customer's categories.
	 * @return : int - Return the number of new categories added to that
	 *         customer's record. Return -1 if no such customer exists.
	 * 
	 */

	int addinterests(long id, int[] categories) {

		if (!db.containsKey(id)) {
			return -1;
		}

		else {
			Customer customer = db.get(id); // fetch the customer
			HashSet<Integer> cat = customer.getCategories(); // fetch the
																// categories
			int count = 0;
			for (int i = 0; i < categories.length && categories[i] != 0; i++) { // check
																				// the
																				// categories

				if (!cat.contains(categories[i])) { // new category
					count++;
					cat.add(categories[i]);
				}

			}

			customer.setCategories(cat); // set the new categories to the
											// customer

			return count;
		}

	}

	/**
	 * Method to Remove some categories from the list of categories associated
	 * with a customer.
	 * 
	 * @param id
	 *            : long - Customers are uniquely identified by their id (key
	 *            field).
	 * @param categories
	 *            : int[] - Array of customer's categories.
	 * @return : int - Return the number of categories left in the customer's
	 *         record.
	 * 
	 */

	int removeinterests(long id, int[] categories) {

		if (!db.containsKey(id)) {
			return -1;
		}

		else {
			Customer customer = db.get(id); // fetch the customer
			HashSet<Integer> cat = customer.getCategories(); // fetch the
																// categories

			for (int i = 0; i < categories.length && categories[i] != 0; i++) { // remove
																				// the
																				// categories

				cat.remove(categories[i]);

			}

			customer.setCategories(cat); // set the new categories to the
											// customer

			return cat.size();
		}
	}

	/**
	 * Method to update customer record by adding a purchase amount spent by a
	 * customer on our company's product.
	 * 
	 * @param id
	 *            : long - Customers are uniquely identified by their id (key
	 *            field).
	 * @param purchase
	 *            : double - Amount of purchase
	 * @return : int - Returns the net amount spent by the customer after adding
	 *         this purchase, truncated to just dollars (-1 if no such customer
	 *         exists).
	 * 
	 */

	int addrevenue(long id, double purchase) {

		if (!db.containsKey(id)) {
			return -1;
		}

		else {
			Customer customer = db.get(id); // fetch the customer
			customer.setPurchases(customer.getPurchases() + 1);
			float newAmount = (float) (customer.getAmount() + purchase); // add
			customer.setAmount(newAmount); // set new amount

			return (int) newAmount;
		}
	}

	/**
	 * Method to find the number of customers whose amount is at least "low" and
	 * at most "high".
	 * 
	 * @param low
	 *            : double - lower bound amount.
	 * @param high
	 *            : double - upper bound amount.
	 * @return : int - number of customers whose amount is at least "low" and at
	 *         most "high"
	 * 
	 */
	int range(double low, double high) {

		List<Customer> listOfInterest = new LinkedList<>();

		for (Customer customer : db.values()) { // for each customer

			// find the customers interested in category k

			if ((float) high >= customer.getAmount()
					&& customer.getAmount() >= (float) low) {
				listOfInterest.add(customer);
			}
		}

		return listOfInterest.size();
	}

	/**
	 * Method to find customers who have exactly the same set of 5 or more
	 * categories of interest.
	 * 
	 * 
	 * @return : int - the number of distinct customers who have exactly the
	 *         same set of 5 or more interests as another customer.
	 * 
	 */

	int samesame() {

		Set<Customer> listOfInterest = new HashSet<>(); // holds the customers
														// having interest in
														// atleast 5 categories
		ArrayList<Customer> custList = new ArrayList<>();

		for (Customer c : db.values()) {
			custList.add(c);
		}

		Iterator<Customer> itr = custList.iterator();
		for (; itr.hasNext();) { // runs faster than while(itr.hasNext()) about
									// 70ms for large input

			Customer cust = itr.next();
			if (itr.hasNext()) {
				Customer nextCust = itr.next();

				if (cust.containsAtleastFive(nextCust)) {
					listOfInterest.add(nextCust);
					listOfInterest.add(cust);

				}
			} else {
				break;
			}

		}

		return listOfInterest.size();

	}

	/**
	 * Method to find the number of times customer has purchased products
	 * 
	 * @param id
	 *            : long - Customers are uniquely identified by their id (key
	 *            field).
	 * @return : int - the number of times customer has purchased products. -1
	 *         if no such customer exits
	 * 
	 */

	int numberpurchases(long id) {

		if (!db.containsKey(id)) {
			return -1;
		}

		else {
			Customer customer = db.get(id); // fetch the customer

			return customer.getPurchases();
		}
	}
}