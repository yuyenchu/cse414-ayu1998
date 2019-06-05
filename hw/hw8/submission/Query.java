import java.sql.*;
import java.util.ArrayList;

public class Query extends QuerySearchOnly {
	//debug mode
	private static final boolean DEBUG = false;

	// Logged In User
	private String username; // customer username is unique

	// transactions
	private static final String BEGIN_TRANSACTION_SQL = "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE; BEGIN TRANSACTION;";
	protected PreparedStatement beginTransactionStatement;

	private static final String COMMIT_SQL = "COMMIT TRANSACTION";
	protected PreparedStatement	commitTransactionStatement;

	private static final String ROLLBACK_SQL = "ROLLBACK TRANSACTION";
	protected PreparedStatement rollbackTransactionStatement;

	private static final String INSERT_USER_SQL = "INSERT INTO Users VALUES(?,?,?);";
	protected PreparedStatement insertUserStatement;

	private static final String INSERT_TRAVEL_SQL = "INSERT INTO Travel VALUES(?,?,?);";
	protected PreparedStatement insertTravelStatement;

	private static final String INSERT_RESERVATION_SQL = "INSERT INTO Reservations VALUES(?,?,?,?,?,?,?);";
	protected PreparedStatement insertReservationStatement;

	private static final String INSERT_RID_SQL = "INSERT INTO RID VALUES(1);";
	protected PreparedStatement insertRidStatement;

	private static final String UPDATE_BALANCE_SQL = "UPDATE Users SET balance = ? WHERE username = ? COLLATE SQL_Latin1_General_CP1_CI_AS;";
	protected PreparedStatement updateBalanceStatement;

	private static final String UPDATE_RID_SQL = "UPDATE RID SET id = ? WHERE id = ?;";
	protected PreparedStatement updateRidStatement;

	private static final String UPDATE_RESERVATION_CANCEL_SQL = "UPDATE Reservations SET canceled = 1 WHERE rid = ?;";
	protected PreparedStatement updateReservationCancelStatement;

	private static final String UPDATE_RESERVATION_PAID_SQL = "UPDATE Reservations SET paid = 1 WHERE rid = ?;";
	protected PreparedStatement updateReservationPaidStatement;

	private static final String CHECK_USERNAME_SQL = "SELECT COUNT(*) FROM Users WHERE username = ? COLLATE SQL_Latin1_General_CP1_CI_AS;";
	protected PreparedStatement checkUserameStatement;

	private static final String CHECK_LOGIN_SQL = "SELECT COUNT(*) FROM Users WHERE username = ? "
												+ "AND password = ? COLLATE SQL_Latin1_General_CP1_CI_AS;";
	protected PreparedStatement checkLoginStatement;

	private static final String CHECK_UNPAID_RESERVATION_SQL = "SELECT COUNT(*) FROM Reservations WHERE rid = ? "
													  		 + "AND uname = ? COLLATE SQL_Latin1_General_CP1_CI_AS AND paid = 0;";
	protected PreparedStatement checkUnpaidReservationStatement;

	private static final String CHECK_NOT_CANCELED_SQL = "SELECT COUNT(*) FROM Reservations WHERE rid = ? "
													   + "AND uname = ? COLLATE SQL_Latin1_General_CP1_CI_AS AND canceled = 0;";
	protected PreparedStatement checkNotCanceledReservationStatement;

	private static final String CHECK_TRAVEL_SQL = "SELECT COUNT(*) FROM Travel WHERE month_id = ? "
												 + "AND day_of_month = ? AND uname = ? COLLATE SQL_Latin1_General_CP1_CI_AS;";
	protected PreparedStatement checkTravelStatement;

	private static final String GET_NUM_RESERVATION_SQL = "SELECT COUNT(*) FROM Reservations  WHERE flight1 = ? "
												  		+ "OR (flight2 IS NOT NULL AND flight2 = ?);";
	protected PreparedStatement getNumReservationsStatement;

	private static final String GET_RESERVATIONS_SQL = "SELECT * FROM Reservations WHERE uname = ? COLLATE SQL_Latin1_General_"
													 + "CP1_CI_AS AND canceled = 0 ORDER BY rid ASC;";
	protected PreparedStatement getReservationsStatement;

	private static final String GET_RESERVATION_SQL = "SELECT * FROM Reservations WHERE uname = ? COLLATE SQL_Latin1_General_CP1_CI_AS "
													+ "AND rid = ?;";
	protected PreparedStatement getReservationStatement;

	private static final String GET_FLIGHT_SQL = "SELECT * FROM Flights WHERE fid = ?";
	protected PreparedStatement getFlightStatement;

	private static final String GET_BALANCE_SQL = "SELECT balance FROM Users WHERE username = ? COLLATE SQL_Latin1_General_CP1_CI_AS;";
	protected PreparedStatement getBalanceStatement;

	// private static final String GET_NAME_SQL = "SELECT username FROM Users WHERE username = ? COLLATE SQL_Latin1_General_CP1_CI_AS;";
	// protected PreparedStatement getNameStatement;

	private static final String GET_RID_SQL = "SELECT * FROM RID";
	protected PreparedStatement getRidStatement;	

	private static final String DELETE_TRAVEL_SQL = "DELETE FROM Travel WHERE month_id = ? AND day_of_month = ?;";
	protected PreparedStatement deleteTravelStatement;

	private static final String CLEAR_SQL = "DELETE FROM Reservations;DELETE FROM Travel;DELETE FROM Users;DELETE FROM RID;";
	protected PreparedStatement clearStatement;



	public Query(String configFilename) {
		super(configFilename);
		this.username = null;
	}


	/**
	 * Clear the data in any custom tables created. Do not drop any tables and do not
	 * clear the flights table. You should clear any tables you use to store reservations
	 * and reset the next reservation ID to be 1.
	 */
	public void clearTables()
	{
		try {
			clearStatement.executeUpdate();
		} catch (Exception e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * prepare all the SQL statements in this method.
	 * "preparing" a statement is almost like compiling it.
	 * Note that the parameters (with ?) are still not filled in
	 */
	@Override
	public void prepareStatements() throws Exception
	{
		super.prepareStatements();
		//transaction statements
		beginTransactionStatement = conn.prepareStatement(BEGIN_TRANSACTION_SQL);
		commitTransactionStatement = conn.prepareStatement(COMMIT_SQL);
		rollbackTransactionStatement = conn.prepareStatement(ROLLBACK_SQL);
		//check statements 
		checkUserameStatement = conn.prepareStatement(CHECK_USERNAME_SQL);
		checkLoginStatement = conn.prepareStatement(CHECK_LOGIN_SQL);
		checkUnpaidReservationStatement = conn.prepareStatement(CHECK_UNPAID_RESERVATION_SQL);
		checkTravelStatement = conn.prepareStatement(CHECK_TRAVEL_SQL);
		checkNotCanceledReservationStatement = conn.prepareStatement(CHECK_NOT_CANCELED_SQL);
		//insert statements
		insertUserStatement = conn.prepareStatement(INSERT_USER_SQL);
		insertTravelStatement = conn.prepareStatement(INSERT_TRAVEL_SQL);
		insertReservationStatement = conn.prepareStatement(INSERT_RESERVATION_SQL);
		insertRidStatement = conn.prepareStatement(INSERT_RID_SQL);
		//update statements
		updateBalanceStatement = conn.prepareStatement(UPDATE_BALANCE_SQL);
		updateRidStatement = conn.prepareStatement(UPDATE_RID_SQL);
		updateReservationCancelStatement = conn.prepareStatement(UPDATE_RESERVATION_CANCEL_SQL);
		updateReservationPaidStatement = conn.prepareStatement(UPDATE_RESERVATION_PAID_SQL);
		//get statements
		//get statements
		getReservationsStatement = conn.prepareStatement(GET_RESERVATIONS_SQL);
		getFlightStatement = conn.prepareStatement(GET_FLIGHT_SQL);
		getBalanceStatement = conn.prepareStatement(GET_BALANCE_SQL);
		getReservationStatement = conn.prepareStatement(GET_RESERVATION_SQL);
		getNumReservationsStatement = conn.prepareStatement(GET_NUM_RESERVATION_SQL);
		getRidStatement = conn.prepareStatement(GET_RID_SQL);
		//clear statements
		deleteTravelStatement = conn.prepareStatement(DELETE_TRAVEL_SQL);
		clearStatement = conn.prepareStatement(CLEAR_SQL);

	}


	/**
	 * Takes a user's username and password and attempts to log the user in.
	 *
	 * @return If someone has already logged in, then return "User already logged in\n"
	 * For all other errors, return "Login failed\n".
	 *
	 * Otherwise, return "Logged in as [username]\n".
	 */
	public String transaction_login(String username, String password)
	{
		if (this.username != null && !this.username.isEmpty()) {
			return "User already logged in\n";
		}
		if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
			return "Login failed\n" + (DEBUG ? "1\n" : "");
		}
		try {
			if (checkLogin(username, password)) {
				this.username = username;
				itineraries = new ArrayList<>();
				return "Logged in as " + username + "\n";
			} else {
				return "Login failed\n" + (DEBUG ? "2\n" : "");
			}
		} catch (Exception e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			return "Login failed\n" + (DEBUG ? "3\n" : "");
		}
	}

	/**
	 * Implement the create user function.
	 *
	 * @param username new user's username. User names are unique the system.
	 * @param password new user's password.
	 * @param initAmount initial amount to deposit into the user's account, should be >= 0 (failure otherwise).
	 *
	 * @return either "Created user {@code username}\n" or "Failed to create user\n" if failed.
	 */
	public String transaction_createCustomer (String username, String password, int initAmount)
	{
		if (username == null || password == null || username.isEmpty() || password.isEmpty() || initAmount < 0) {
			return "Failed to create user\n" + (DEBUG ? "1\n" : "");
		}
		try {
			beginTransaction();
			if (!checkUserame(username)) {
				insertUser(username, password, initAmount);
				commitTransaction();
				return "Created user " + username + "\n";
			} else {
				rollbackTransaction();
				return "Failed to create user\n" + (DEBUG ? "2\n" : "");
			}
		} catch (Exception e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			rollbackTransaction();
			return "Failed to create user\n" + (DEBUG ? "3\n" : "");
		}
	}

	/**
	 * Implements the book itinerary function.
	 *
	 * @param itineraryId ID of the itinerary to book. This must be one that is returned by search in the current session.
	 *
	 * @return If the user is not logged in, then return "Cannot book reservations, not logged in\n".
	 * If try to book an itinerary with invalid ID, then return "No such itinerary {@code itineraryId}\n".
	 * If the user already has a reservation on the same day as the one that they are trying to book now, then return
	 * "You cannot book two flights in the same day\n".
	 * For all other errors, return "Booking failed\n".
	 *
	 * And if booking succeeded, return "Booked flight(s), reservation ID: [reservationId]\n" where
	 * reservationId is a unique number in the reservation system that starts from 1 and increments by 1 each time a
	 * successful reservation is made by any user in the system.
	 */
	public String transaction_book(int itineraryId)
	{
		if (this.username == null) {
			return "Cannot book reservations, not logged in\n";
		} else if (itineraryId >= itineraries.size()){
			return "No such itinerary " + itineraryId + "\n";
		} 
		try {
			beginTransaction();
			Fid2 temp = itineraries.get(itineraryId);
			Flight f1 = getFlight(temp._1);
			Flight f2 = temp._2 > 0 ? getFlight(temp._2) : null;
			if (checkTravel(f1.month, f1.dayOfMonth) || (f2 != null && checkTravel(f2.month, f2.dayOfMonth))) {
				rollbackTransaction();
				return "You cannot book two flights in the same day\n";
			} else if (f1.capacity <= getNumReservations(f1.fid) || (f2 != null && f2.capacity <= getNumReservations(f2.fid))) {
				rollbackTransaction();
				return "Booking failed\n" + (DEBUG ? "1\n" : "");
			}
			int id = insertReservation(f1, f2);
			insertTravel(f1.month, f1.dayOfMonth);
			if (f2 != null) {
				insertTravel(f2.month, f2.dayOfMonth);
			}
			commitTransaction();
			return "Booked flight(s), reservation ID: " + id + "\n";
		} catch (Exception e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			rollbackTransaction();
			return "Booking failed\n" + (DEBUG ? "2\n" : "");
		}
	}

	/**
	 * Implements the pay function.
	 *
	 * @param reservationId the reservation to pay for.
	 *
	 * @return If no user has logged in, then return "Cannot pay, not logged in\n"
	 * If the reservation is not found / not under the logged in user's name, then return
	 * "Cannot find unpaid reservation [reservationId] under user: [username]\n"
	 * If the user does not have enough money in their account, then return
	 * "User has only [balance] in account but itinerary costs [cost]\n"
	 * For all other errors, return "Failed to pay for reservation [reservationId]\n"
	 *
	 * If successful, return "Paid reservation: [reservationId] remaining balance: [balance]\n"
	 * where [balance] is the remaining balance in the user's account.
	 */
	public String transaction_pay (int reservationId)
	{
		if (this.username == null) {
			return "Cannot pay, not logged in\n";
		} 
		try {
			beginTransaction();
			if (!checkUnpaidReservation(reservationId) || !checkNotCanceledReservation(reservationId)) {
				rollbackTransaction();
				return "Cannot find unpaid reservation " + reservationId + " under user: " + this.username + "\n";
			} 
			getReservationStatement.clearParameters();
			getReservationStatement.setString(1, this.username);
			getReservationStatement.setInt(2, reservationId);
			ResultSet result = getReservationStatement.executeQuery();
			result.next();
			int balance = getBalance();
			if (balance < result.getInt(7)) {
				rollbackTransaction();
				return "User has only " + balance + " in account but itinerary costs " + result.getInt(7) + "\n";
			}
			balance -= result.getInt(7);
			updateBalance(balance);
			payReservation(result.getInt(1));
			result.close();
			commitTransaction();
			return "Paid reservation: " + reservationId + " remaining balance: " + balance + "\n";
		} catch (Exception e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			rollbackTransaction();
			return "Failed to pay for reservation " + reservationId + "\n";
		} 
	}

	/**
	 * Implements the reservations function.
	 *
	 * @return If no user has logged in, then return "Cannot view reservations, not logged in\n"
	 * If the user has no reservations, then return "No reservations found\n"
	 * For all other errors, return "Failed to retrieve reservations\n"
	 *
	 * Otherwise return the reservations in the following format:
	 *
	 * Reservation [reservation ID] paid: [true or false]:\n"
	 * [flight 1 under the reservation]
	 * [flight 2 under the reservation]
	 * Reservation [reservation ID] paid: [true or false]:\n"
	 * [flight 1 under the reservation]
	 * [flight 2 under the reservation]
	 * ...
	 *
	 * Each flight should be printed using the same format as in the {@code Flight} class.
	 *
	 * @see Flight#toString()
	 */
	public String transaction_reservations()
	{
		if (this.username == null) {
			return "Cannot view reservations, not logged in\n";
		}
		String out = "";
		try {
			getReservationsStatement.clearParameters();
			getReservationsStatement.setString(1, this.username);
			ResultSet result = getReservationsStatement.executeQuery();
			while(result.next()) {
				out += "Reservation " + result.getInt(1) + " paid: " + (result.getInt(5) == 1) + ":\n";
				out += getFlight(result.getInt(3)).toString() + "\n";
				if (result.getInt(4) > 0) {
					out += getFlight(result.getInt(4)).toString() + "\n";
				}
			}
			return out.equals("") ? "No reservations found\n" : out;
		} catch (Exception e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			return "Failed to retrieve reservations\n";
		}
	}


	/**
	 * Implements the cancel operation.
	 *
	 * @param reservationId the reservation ID to cancel
	 *
	 * @return If no user has logged in, then return "Cannot cancel reservations, not logged in\n"
	 * For all other errors, return "Failed to cancel reservation [reservationId]"
	 *
	 * If successful, return "Canceled reservation [reservationId]"
	 *
	 * Even though a reservation has been canceled, its ID should not be reused by the system.
	 */
	public String transaction_cancel(int reservationId)
	{
		// only implement this if you are interested in earning extra credit for the HW!
		if (this.username == null) {
			return "Cannot cancel reservations, not logged in\n";
		}
		try {
			beginTransaction();
			if (checkNotCanceledReservation(reservationId)){
				cancelReservation(reservationId);
				commitTransaction();
				return "Canceled reservation " + reservationId + "\n";
			} 
			rollbackTransaction();
			return "Failed to cancel reservation " + reservationId + "\n" + (DEBUG ? "1\n" : "");
		} catch (Exception e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			rollbackTransaction();
			return "Failed to cancel reservation " + reservationId + "\n" + (DEBUG ? "2\n" : "");
		}
	}


	/* some utility functions below */

	public void beginTransaction() throws SQLException
	{
		conn.setAutoCommit(false);
		beginTransactionStatement.executeUpdate();
	}

	public void commitTransaction() throws SQLException
	{
		commitTransactionStatement.executeUpdate();
		conn.setAutoCommit(true);
	}

	public void rollbackTransaction() {
		try {		
			rollbackTransactionStatement.executeUpdate();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
	}

	private boolean checkUserame(String name) throws SQLException {
		checkUserameStatement.clearParameters();
		checkUserameStatement.setString(1, name);
		ResultSet result = checkUserameStatement.executeQuery();
		result.next();
		boolean out = result.getInt(1) == 1;
		if (DEBUG) {
			System.out.println("checkUserame: "+result.getInt(1)+"\n");
		}
		result.close();
		return out;
	}
	
	private boolean checkLogin(String name, String password) throws SQLException {
		checkLoginStatement.clearParameters();
		checkLoginStatement.setString(1, name);
		checkLoginStatement.setString(2, password);
		ResultSet result = checkLoginStatement.executeQuery();
		result.next();
		boolean out = result.getInt(1) == 1;
		if (DEBUG) {
			System.out.println("checkLogin:"+result.getInt(1)+"\n");
		}
		result.close();
		return out;
	}

	private boolean checkUnpaidReservation(int id) throws SQLException {
		checkUnpaidReservationStatement.clearParameters();
		checkUnpaidReservationStatement.setInt(1, id);
		checkUnpaidReservationStatement.setString(2, this.username);
		ResultSet result = checkUnpaidReservationStatement.executeQuery();
		result.next();
		boolean out = result.getInt(1) == 1;
		if (DEBUG) {
			System.out.println("checkUnpaidReservation: "+result.getInt(1)+"\n");
		}
		result.close();
		return out;
	}

	private boolean checkTravel(int month, int day) throws SQLException {
		checkTravelStatement.clearParameters();
		checkTravelStatement.setInt(1, month);
		checkTravelStatement.setInt(2, day);
		checkTravelStatement.setString(3, this.username);
		ResultSet result = checkTravelStatement.executeQuery();
		result.next();
		boolean out = result.getInt(1) == 1;
		if (DEBUG) {
			System.out.println("checkTravel: "+result.getInt(1)+"\n");
		}
		result.close();
		return out;
	}

	private boolean checkNotCanceledReservation(int id) throws SQLException {
		checkNotCanceledReservationStatement.clearParameters();
		checkNotCanceledReservationStatement.setInt(1, id);
		checkNotCanceledReservationStatement.setString(2, this.username);
		ResultSet result = checkNotCanceledReservationStatement.executeQuery();
		result.next();
		boolean out = result.getInt(1) == 1;
		if (DEBUG) {
			System.out.println("checkNotCanceledReservation: "+result.getInt(1)+"\n");
		}
		result.close();
		return out;
	}

	private void insertUser(String name, String password, int balance) throws SQLException {
		insertUserStatement.clearParameters();
		insertUserStatement.setString(1, name);
		insertUserStatement.setString(2, password);
		insertUserStatement.setInt(3, balance);
		insertUserStatement.executeUpdate();
	}

	private void insertTravel(int month, int day) throws SQLException {
		insertTravelStatement.clearParameters();
		insertTravelStatement.setInt(1, month);
		insertTravelStatement.setInt(2, day);
		insertTravelStatement.setString(3, this.username);
		insertTravelStatement.executeUpdate();
	}

	private int insertReservation(Flight f1, Flight f2) throws SQLException {
		insertReservationStatement.clearParameters();
		int id = getRID();
		updateRID(id + 1);
		insertReservationStatement.setInt(1, id+1);
		insertReservationStatement.setString(2, this.username);
		insertReservationStatement.setInt(3, f1.fid);
		if (f2 != null) {
			insertReservationStatement.setInt(4, f2.fid);
		} else {
			insertReservationStatement.setNull(4, java.sql.Types.INTEGER);
		}
		insertReservationStatement.setInt(5, 0);
		insertReservationStatement.setInt(6, 0);
		insertReservationStatement.setInt(7, f2 != null ? f1.price + f2.price : f1.price);
		insertReservationStatement.executeUpdate();
		return id + 1;
	}

	private int getRID() throws SQLException {
		ResultSet result = getRidStatement.executeQuery();
		if (result.next()) {
			int out = result.getInt(1);
			result.close();
			if (DEBUG) {
				System.out.println("getRID: "+out+"\n");
			}
			return out;
		} else {
			if (DEBUG) {
				System.out.println("getRID: "+0+"\n");
			}
			return 0;
		}
	}

	private int getBalance() throws SQLException {
		getBalanceStatement.clearParameters();
		getBalanceStatement.setString(1, this.username);
		ResultSet result = getBalanceStatement.executeQuery();
		result.next();
		int out = result.getInt(1);
		result.close();
		return out;
	}

	private Flight getFlight(int fid) throws SQLException{
		getFlightStatement.clearParameters();
		getFlightStatement.setInt(1, fid);
		ResultSet result = getFlightStatement.executeQuery();
		result.next();
		Flight out = new Flight(result, 0);
		result.close();
		return out;
	}

	private int getNumReservations(int fid) throws SQLException {
		getNumReservationsStatement.clearParameters();
		getNumReservationsStatement.setInt(1, fid);
		getNumReservationsStatement.setInt(2, fid);
		ResultSet result = getNumReservationsStatement.executeQuery();
		result.next();
		int out = result.getInt(1);
		result.close();
		return out;
	}

	private void updateBalance(int balance) throws SQLException {
		updateBalanceStatement.clearParameters();
		updateBalanceStatement.setInt(1, balance);
		updateBalanceStatement.setString(2, this.username);
		updateBalanceStatement.executeUpdate();
	}

	private void updateRID(int id) throws SQLException {
		if (id <= 1) {
			insertRidStatement.executeUpdate();
		} else {
			updateRidStatement.clearParameters();
			updateRidStatement.setInt(1, id);
			updateRidStatement.setInt(2, id - 1);
			updateRidStatement.executeUpdate();
			if (DEBUG) {
				System.out.println("updateRID: "+id+"\n");
			}
		}
	}

	private void cancelReservation(int id) throws SQLException {
		updateReservationCancelStatement.clearParameters();
		updateReservationCancelStatement.setInt(1, id);
		updateReservationCancelStatement.executeUpdate();
		getReservationStatement.clearParameters();
		getReservationStatement.setString(1, this.username);
		getReservationStatement.setInt(2, id);
		ResultSet result = getReservationStatement.executeQuery();
		result.next();
		Flight f1 = getFlight(result.getInt(3));
		deleteTravel(f1.month, f1.dayOfMonth);
		if (result.getInt(4) > 0){
			Flight f2 = getFlight(result.getInt(4));
			deleteTravel(f2.month, f2.dayOfMonth);
		}
	}

	private void payReservation(int id) throws SQLException {
		updateReservationPaidStatement.clearParameters();
		updateReservationPaidStatement.setInt(1, id);
		updateReservationPaidStatement.executeUpdate();
	}

	private void deleteTravel(int month, int day) throws SQLException {
		deleteTravelStatement.clearParameters();
		deleteTravelStatement.setInt(1, month);
		deleteTravelStatement.setInt(2, day);
		deleteTravelStatement.executeUpdate();
	}
}
