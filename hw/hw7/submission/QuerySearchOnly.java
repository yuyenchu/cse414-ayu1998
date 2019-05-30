import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Runs queries against a back-end database.
 * This class is responsible for searching for flights.
 */
public class QuerySearchOnly
{
  // `dbconn.properties` config file
  private String configFilename;

  // DB Connection
  protected Connection conn;

  // Canned queries
  private static final String CHECK_FLIGHT_CAPACITY = "SELECT capacity FROM Flights WHERE fid = ?";
  private static final String DIRECT_FLIGHT_SEARCH = "SELECT TOP (?) * FROM Flights "
                                                    +"WHERE origin_city = \'?\' AND dest_city = \'?\' "
                                                    +"AND day_of_month = ? AND canceled = 0 "
                                                    +"ORDER BY actual_time ASC, fid ASC";
  private static final String INDIRECT_FLIGHT_SEARCH = "SELECT TOP (?) f1.*, f2.* "
                                                      +"FROM Flights f1, Flights f2 "
                                                      +"WHERE f1.origin_city = \'?\' AND f2.dest_city = \'?\' AND f2.origin_city = f1.dest_city"
                                                      +"AND f1.day_of_month = ? AND f2.day_of_month = f1.day_of_month AND f1.canceled = 0 "
                                                      +"AND f2.canceled = 0 "
                                                      +"ORDER BY f1.actual_time+f2.actual_time ASC, f1.fid ASC, f2.fid ASC";
  protected PreparedStatement checkFlightCapacityStatement;
  protected PreparedStatement directFlightSearchStatement;
  protected PreparedStatement indirectFlightSearchStatement;

  class Flight2{
    public Flight _1, _2;

    public Flight2(ResultSet rs) {
        _1 = new Flight(rs, 0);
        _2 = new Flight(rs, 18);
    }

    @Override
    public String toString(){
      return this._1.toString() + "\n" + this._2.toString();
    }
  }

  class Flight
  {
    public int fid;
    public int dayOfMonth;
    public String carrierId;
    public String flightNum;
    public String originCity;
    public String destCity;
    public int time;
    public int capacity;
    public int price;

    // public Flight(ResultSet rs) {
    //     if(rs.getRow() != 0){
    //       fid = results.getInt("fid");
    //       dayOfMonth = results.getInt("day_of_month");
    //       carrierId = results.getString("carrier_id");
    //       flightNum = results.getString("flight_num");
    //       originCity = results.getString("origin_city");
    //       destCity = results.getString("dest_city");
    //       time = results.getInt("actual_time");
    //       capacity = results.getInt("capacity");
    //       price = results.getInt("price");
    //     }
    // }

    public Flight(ResultSet rs, int offset) {
      try {
        if (rs.getRow() != 0) {
          fid = rs.getInt(1+offset);
          dayOfMonth = rs.getInt(3+offset);
          carrierId = rs.getString(5+offset);
          flightNum = rs.getString(6+offset);
          originCity = rs.getString(7+offset);
          destCity = rs.getString(9+offset);
          time = rs.getInt(15+offset);
          capacity = rs.getInt(17+offset);
          price = rs.getInt(18+offset);
        }
      } catch (SQLException e) { e.printStackTrace(); }
  }

    @Override
    public String toString()
    {
      return "ID: " + fid + " Day: " + dayOfMonth + " Carrier: " + carrierId +
              " Number: " + flightNum + " Origin: " + originCity + " Dest: " + destCity + " Duration: " + time +
              " Capacity: " + capacity + " Price: " + price;
    }
  }

  public QuerySearchOnly(String configFilename)
  {
    this.configFilename = configFilename;
  }

  /** Open a connection to SQL Server in Microsoft Azure.  */
  public void openConnection() throws Exception
  {
    Properties configProps = new Properties();
    configProps.load(new FileInputStream(configFilename));

    String jSQLDriver = configProps.getProperty("flightservice.jdbc_driver");
    String jSQLUrl = configProps.getProperty("flightservice.url");
    String jSQLUser = configProps.getProperty("flightservice.sqlazure_username");
    String jSQLPassword = configProps.getProperty("flightservice.sqlazure_password");
    //String Setting = "jdbc:sqlserver://ayu1998.database.windows.net:1433;database=cse41419sp-ayu1998;user=andrewyu@ayu1998;password=87!!Andrew06;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

    System.out.println(jSQLDriver);
    System.out.println(jSQLUrl);
    System.out.println(jSQLUser);
    System.out.println(jSQLPassword);

    /* load jdbc drivers */
    Class.forName(jSQLDriver).newInstance();

    /* open connections to the flights database */
    conn = DriverManager.getConnection(jSQLUrl, // database
            jSQLUser, // user
            jSQLPassword); // password
    // conn = DriverManager.getConnection(Setting); // setting provided by Azure

    conn.setAutoCommit(true); //by default automatically commit after each statement
    /* In the full Query class, you will also want to appropriately set the transaction's isolation level:
          conn.setTransactionIsolation(...)
       See Connection class's JavaDoc for details.
    */
  }

  public void closeConnection() throws Exception
  {
    conn.close();
  }

  /**
   * prepare all the SQL statements in this method.
   * "preparing" a statement is almost like compiling it.
   * Note that the parameters (with ?) are still not filled in
   */
  public void prepareStatements() throws Exception
  {
    checkFlightCapacityStatement = conn.prepareStatement(CHECK_FLIGHT_CAPACITY);
    directFlightSearchStatement = conn.prepareStatement(DIRECT_FLIGHT_SEARCH);
    indirectFlightSearchStatement = conn.prepareStatement(INDIRECT_FLIGHT_SEARCH);
    /* add here more prepare statements for all the other queries you need */
    /* . . . . . . */
  }



  /**
   * Implement the search function.
   *
   * Searches for flights from the given origin city to the given destination
   * city, on the given day of the month. If {@code directFlight} is true, it only
   * searches for direct flights, otherwise it searches for direct flights
   * and flights with two "hops." Only searches for up to the number of
   * itineraries given by {@code numberOfItineraries}.
   *
   * The results are sorted based on total flight time.
   *
   * @param originCity
   * @param destinationCity
   * @param directFlight if true, then only search for direct flights, otherwise include indirect flights as well
   * @param dayOfMonth
   * @param numberOfItineraries number of itineraries to return
   *
   * @return If no itineraries were found, return "No flights match your selection\n".
   * If an error occurs, then return "Failed to search\n".
   *
   * Otherwise, the sorted itineraries printed in the following format:
   *
   * Itinerary [itinerary number]: [number of flights] flight(s), [total flight time] minutes\n
   * [first flight in itinerary]\n
   * ...
   * [last flight in itinerary]\n
   *
   * Each flight should be printed using the same format as in the {@code Flight} class. Itinerary numbers
   * in each search should always start from 0 and increase by 1.
   *
   * @see Flight#toString()
   */
  public String transaction_search(String originCity, String destinationCity, boolean directFlight, int dayOfMonth,
                                   int numberOfItineraries)
  {
    String out = "";
    try{
    // Please implement your own (safe) version that uses prepared statements rather than string concatenation.
    // You may use the `Flight` class (defined above).
      directFlightSearchStatement.clearParameters();
      directFlightSearchStatement.setInt(1, numberOfItineraries);
      directFlightSearchStatement.setString(2, originCity);
      directFlightSearchStatement.setString(3, destinationCity);
      directFlightSearchStatement.setInt(4, dayOfMonth);
      ResultSet dirResults = directFlightSearchStatement.executeQuery();
      if (!directFlight){
        indirectFlightSearchStatement.clearParameters();
        indirectFlightSearchStatement.setInt(1, numberOfItineraries);
        indirectFlightSearchStatement.setString(2, originCity);
        indirectFlightSearchStatement.setString(3, destinationCity);
        indirectFlightSearchStatement.setInt(4, dayOfMonth);
        ResultSet indirResults = indirectFlightSearchStatement.executeQuery();
        dirResults.first();
        indirResults.first();

        int count = 0;
        while (count < numberOfItineraries && (dirResults.getRow()!=0 || indirResults.getRow()!=0)){
          out += "Itinerary " + count;
          out = compareAndPut(out, dirResults, indirResults);
          count++;
        }
        dirResults.close();
        indirResults.close();
      } else {
        Flight temp;
        int count = 0;
        while (dirResults.next()){
          temp = new Flight(dirResults,0);
          out += "Itinerary " + count + ": 1 flight(s), " + temp.time + " minutes\n";
          out +=  temp.toString()+"\n";
          count++;
        } 
        dirResults.close();
      }
    } catch (SQLException e) { e.printStackTrace(); }
    
    return out;
    // return transaction_search_unsafe(originCity, destinationCity, directFlight, dayOfMonth, numberOfItineraries);
  }

  public String safe_transaction_search(String originCity, String destinationCity, boolean directFlight, int dayOfMonth,
                                 int numberOfItineraries)
  {
    String out = "";
    try{
      directFlightSearchStatement.clearParameters();
      directFlightSearchStatement.setInt(1, numberOfItineraries);
      directFlightSearchStatement.setString(2, originCity);
      directFlightSearchStatement.setString(3, destinationCity);
      directFlightSearchStatement.setInt(4, dayOfMonth);
      ResultSet dirResults = directFlightSearchStatement.executeQuery();

      int count = 0;
      while (count < numberOfItineraries && dirResults.next()){
        out += "Itinerary " + count;
        out = addDirect(out, new Flight(dirResults,0));
        count++;
      } 
      dirResults.close();

      if (!directFlight && count < numberOfItineraries){
        indirectFlightSearchStatement.clearParameters();
        indirectFlightSearchStatement.setInt(1, numberOfItineraries);
        indirectFlightSearchStatement.setString(2, originCity);
        indirectFlightSearchStatement.setString(3, destinationCity);
        indirectFlightSearchStatement.setInt(4, dayOfMonth);
        ResultSet indirResults = indirectFlightSearchStatement.executeQuery();

        while (count < numberOfItineraries && indirResults.next()){
          out += "Itinerary " + count;
          out = addIndirect(out, new Flight2(indirResults));
          count++;
        }
        indirResults.close();
      }
    } catch (SQLException e) { e.printStackTrace(); }

    return out;
  }


  private String compareAndPut(String str, ResultSet data1, ResultSet data2) throws SQLException{
    if (data1.getRow() == 0){
      return addIndirect(str, new Flight2(data2));
    }
    if (data2.getRow() == 0){
      return addDirect(str, new Flight(data1,0));
    }

    Flight temp1 = new Flight(data1,0);
    Flight2 temp2 = new Flight2(data2);

    try {
      if (temp1.time < temp2._1.time + temp2._2.time) {
        data1.next();
        return addDirect(str, temp1);
      } else if (temp1.time == temp2._1.time + temp2._2.time) {
        if (temp1.fid < temp2._1.fid) {
          data1.next();
          return addDirect(str, temp1);
        } else if (temp1.fid == temp2._1.fid) {
          if (temp1.fid < temp2._2.fid) {
            data1.next();
            return addDirect(str, temp1);
          } else { 
            data2.next();
            return addIndirect(str, temp2);
          }
        } else {
          data2.next();
          return addIndirect(str, temp2);
        }
      } else {
        data2.next();
        return addIndirect(str, temp2);
      }
    } catch (SQLException e) { e.printStackTrace(); }
    return null;
  }

  private String addDirect(String str, Flight f) {
    return str += ": 1 flight(s), " + f.time + " minutes\n" + f.toString() + "\n";
  }

  private String addIndirect(String str, Flight2 f) {
    return str += ": 2 flight(s), " + (f._1.time + f._2.time) + " minutes\n" + 
                  f.toString() + "\n";
  }

  /**
   * Same as {@code transaction_search} except that it only performs single hop search and
   * do it in an unsafe manner.
   *
   * @param originCity
   * @param destinationCity
   * @param directFlight
   * @param dayOfMonth
   * @param numberOfItineraries
   *
   * @return The search results. Note that this implementation *does not conform* to the format required by
   * {@code transaction_search}.
   */
  private String transaction_search_unsafe(String originCity, String destinationCity, boolean directFlight,
                                          int dayOfMonth, int numberOfItineraries)
  {
    StringBuffer sb = new StringBuffer();

    try
    {
      // one hop itineraries
      String unsafeSearchSQL =
              "SELECT TOP (" + numberOfItineraries + ") day_of_month,carrier_id,flight_num,origin_city,dest_city,actual_time,capacity,price "
                      + "FROM Flights "
                      + "WHERE origin_city = \'" + originCity + "\' AND dest_city = \'" + destinationCity + "\' AND day_of_month =  " + dayOfMonth + " "
                      + "ORDER BY actual_time ASC";

      Statement searchStatement = conn.createStatement();
      ResultSet oneHopResults = searchStatement.executeQuery(unsafeSearchSQL);

      while (oneHopResults.next())
      {
        int result_dayOfMonth = oneHopResults.getInt("day_of_month");
        String result_carrierId = oneHopResults.getString("carrier_id");
        String result_flightNum = oneHopResults.getString("flight_num");
        String result_originCity = oneHopResults.getString("origin_city");
        String result_destCity = oneHopResults.getString("dest_city");
        int result_time = oneHopResults.getInt("actual_time");
        int result_capacity = oneHopResults.getInt("capacity");
        int result_price = oneHopResults.getInt("price");

        sb.append("Day: ").append(result_dayOfMonth)
                .append(" Carrier: ").append(result_carrierId)
                .append(" Number: ").append(result_flightNum)
                .append(" Origin: ").append(result_originCity)
                .append(" Destination: ").append(result_destCity)
                .append(" Duration: ").append(result_time)
                .append(" Capacity: ").append(result_capacity)
                .append(" Price: ").append(result_price)
                .append('\n');
      }
      oneHopResults.close();
    } catch (SQLException e) { e.printStackTrace(); }

    return sb.toString();
  }

  /**
   * Shows an example of using PreparedStatements after setting arguments.
   * You don't need to use this method if you don't want to.
   */
  private int checkFlightCapacity(int fid) throws SQLException
  {
    checkFlightCapacityStatement.clearParameters();
    checkFlightCapacityStatement.setInt(1, fid);
    ResultSet results = checkFlightCapacityStatement.executeQuery();
    results.next();
    int capacity = results.getInt("capacity");
    results.close();

    return capacity;
  }
}
