// Name: Vinay Kumar    2020csb1141
// Name: Yadwinder Singh    2020csb1143

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.print.attribute.standard.PrinterInfo;

import java.util.*;
// import javax.annotation.processing.SupportedOptions;

// import java.util.*;
import java.sql.*;
import java.sql.SQLException;

class QueryRunner implements Runnable 
{
    // Declare socket for client access
    protected Socket socketConnection;
    protected Connection cc = null;
    protected Statement stmt = null;

    public QueryRunner(Socket clientSocket, Connection c, Statement st) {
        this.socketConnection = clientSocket;
        this.cc = c;
        this.stmt = st;
    }


    public void run() 
    {

        try {
            // System.out.println("run started..");
            // Reading data from client
            InputStreamReader inputStream = new InputStreamReader(socketConnection.getInputStream());
            BufferedReader bufferedInput = new BufferedReader(inputStream);
            OutputStreamWriter outputStream = new OutputStreamWriter(socketConnection.getOutputStream());
            BufferedWriter bufferedOutput = new BufferedWriter(outputStream);
            PrintWriter printWriter = new PrintWriter(bufferedOutput, true);
            String clientCommand = "";

            // Read client query from the socket endpoint
            ResultSet rs = null;
            int query_number = 0;

            String final_string="";
            while (true) 
            {

                query_number++;
                clientCommand = bufferedInput.readLine();
                // System.out.println(clientCommand);
                printWriter.print("\n\n\n");
                // final_string += "\n\n\n";
                if (clientCommand.equals("#") || clientCommand.equals("\n")) 
                {
                    // final_string += "Final Query: End of Thread.\n";
                    printWriter.println("Final Query: End of Thread.\n");
                    inputStream.close();
                    bufferedInput.close();
                    outputStream.close();
                    bufferedOutput.close();
                    System.out.close();
                    socketConnection.close();
                    return;
                }
                // final_string += ("Query #" + query_number);
                printWriter.println("Query #" + query_number);
                StringTokenizer words = new StringTokenizer(clientCommand);
                int numofTokens = words.countTokens();
                System.out.println(numofTokens);
                String queryInput = words.nextToken();
                
                // if (queryInput.length() == 4) 
                if(numofTokens == 4)
                {
                    int trainNo = Integer.parseInt(queryInput);

                    queryInput = words.nextToken();
                    String DOJ = queryInput;

                    queryInput = words.nextToken();
                    int ACcoach = Integer.parseInt(queryInput);

                    queryInput = words.nextToken();
                    int SleeperCoach = Integer.parseInt(queryInput);


                    String query_check = "SELECT train_present_check(" + trainNo + ");";
                    try 
                    {
                        rs = stmt.executeQuery(query_check);
                    } 
                    catch (Exception e) 
                    {
                        System.out.println("exception.. ");
                        continue;
                    }
                    
                    int addorNot = 1;
                    while (rs.next()) 
                    {
                        addorNot = rs.getInt(1);
                        // System.out.println("Add: " + addorNot);
                    }
                    
                    if (addorNot == 0) 
                    {
                        
                        String query_add = "SELECT add_train( " + trainNo + ");";
                        try 
                        {
                            rs = stmt.executeQuery(query_add);
                        } 
                        catch (Exception e) 
                        {
                            System.err.println(e);
                            System.out.println("exception. 2 ");
                            continue;
                        }
                    } 
                    else 
                    {
                        // final_string += ("Train already there.." + trainNo + "\n");
                        printWriter.println("Train already there.." + trainNo);
                    }

                    String query_journey = "SELECT check_journey( '" + DOJ + "', " + trainNo + ");";
                    rs = stmt.executeQuery(query_journey);
                    
                    addorNot = 1;
                    while (rs.next()) 
                    {
                        addorNot = rs.getInt(1);
                    }
                    
                    if (addorNot == 0) 
                    {
                        // final_string += ("Adding journey.." + DOJ + " " + trainNo);
                        printWriter.println("Adding journey.." + DOJ + " " + trainNo);
                        query_journey = "SELECT add_journey( '" + DOJ + "', " + trainNo + ", " + ACcoach + ", "
                        + SleeperCoach + ");";
                        try 
                        {
                            stmt.executeQuery(query_journey);
                        } 
                        catch (Exception e) 
                        {
                            // final_string += ("exception.." + trainNo + " " + DOJ + "\n");
                            printWriter.println("exception.." + trainNo + " " + DOJ );
                            continue;
                        }
                    } 
                    else 
                    {
                        
                        // final_string +=  ("Journey Present. " + DOJ + " " + trainNo + "\n");
                        printWriter.println("Journey Present. " + DOJ + " " + trainNo);
                    }
                } 
                else 
                {
                    // ticket booking
                    // int numofpeople = Integer.parseInt(queryInput);
                    // printWriter.println("ticket..");

                    int numofpeople = Integer.parseInt(queryInput);
                    // System.out.println(numofpeople);
                    String names[] = new String[numofpeople];
                    String n = "";
                    for (int i = 1; i < numofpeople; i++)
                    {
                        n = words.nextToken();
                        names[i - 1] = n.substring(0, n.length() - 1);
                        // System.out.println(n);
                    }
                    names[numofpeople - 1] = words.nextToken();

                    
                    queryInput = words.nextToken();
                    int trainNo = Integer.parseInt(queryInput);

                    queryInput = words.nextToken();
                    String DOJ = queryInput;

                    queryInput = words.nextToken();
                    String coachType = queryInput;
                    
                    // System.out.println(DOJ + " "+ trainNo+ " "+ coachType);
                    // for(int i=0; i<numofpeople; i++)
                    // {
                    //     System.out.print(names[i] + " ");
                    // }
                    // System.out.println("\n\n");

                    String query_check = "SELECT train_present_check(" + trainNo + ");";
                    int cantbook = 0;
                    rs = stmt.executeQuery(query_check);

                    while (rs.next())
                    {
                        cantbook = rs.getInt(1);
                    }

                    if(cantbook == 0)
                    {
                        // final_string += ("Booking Failed. (train not Present " + trainNo + ")\n");
                        printWriter.println("Booking Failed. (train not Present " + trainNo + ")");
                        continue;
                    }
                            
                    String query_journey = "SELECT check_journey( '" + DOJ + "', " + trainNo + ");";
                    rs = stmt.executeQuery(query_journey);
                    while (rs.next()) 
                    {
                        cantbook = rs.getInt(1);
                    }

                    if (cantbook == 0)
                    {
                        // final_string += ("Journey is not possible. (train not running on that day. )" + trainNo + " " + DOJ + "\n");                
                        printWriter.println("Journey is not possible. (train not running on that day. )" + trainNo + " " + DOJ);                
                        continue;
                    }

                    boolean retry = true;
                    while(retry)
                    {
                        try {
                            String query_seats = "SELECT is_seats_avaiable( '" + DOJ + "', " + trainNo + ", '" + coachType + "', " + numofpeople + ");";
                            rs = stmt.executeQuery(query_seats);
                            retry = false;
                        } catch (Exception e) {
                            retry = true;
                            System.out.println("exception....");
                        }
                    }

                    int seats_avaiable = 0;
                    while (rs.next())
                    {
                        seats_avaiable = rs.getInt(1);
                    }
                    if (seats_avaiable < numofpeople)
                    {
                        // final_string += ("Journey is not possible. (train not empty.) " + trainNo + "\n");
                        printWriter.println("Journey is not possible. (train not empty.) " + trainNo);
                        continue;
                    }

                    // printWriter.println("Seat avaiable..  " + trainNo);
                                    
                    String query_ticket = "SELECT create_ticket( '" + DOJ + "', " + numofpeople + ", " + trainNo + ", '" + coachType + "');";
                    rs = stmt.executeQuery(query_ticket);
                    String pNR = "";
                    while (rs.next())
                    {
                        pNR = rs.getString(1);
                    }
                    // System.out.println(pNR);

                    
                    // final_string += ("Train Booked with PNR: " + pNR + "\n");
                    printWriter.println("Train Booked with PNR: " + pNR);
                    // final_string += ("\tTrainNo: " + trainNo + " running on " + DOJ + "\n");
                    printWriter.println("\tTrainNo: " + trainNo + " running on " + DOJ);
                    for (int i = 0; i < numofpeople; i++)
                    {
                        int berthNo = 0;
                        int coachNo = 0;
                        if (coachType == "AC")
                        {
                            berthNo = (seats_avaiable - i) % 18;
                            if(berthNo == 0)
                            {
                                berthNo = 18;
                            }
                            coachNo = (seats_avaiable - i) / 18 + 1;
                            
                            // String query_passenger = "SELECT adding_passenger( '" + DOJ + "', '" + pNR + "', " + berthNo + ",'AC', " + coachNo + ", " + trainNo + ",'" + names[i] + "');";
                            // stmt.executeQuery(query_passenger);
                        }
                        else
                        {
                            berthNo = (seats_avaiable - i) % 24;
                            if(berthNo == 0)
                            {
                                berthNo = 24;
                            }
                            coachNo = (seats_avaiable  - i) / 24 + 1;

                            // String query_passenger = "SELECT adding_passenger( '" + DOJ + "','" + pNR + "', " + berthNo + ", 'SL', " + coachNo + ", " + trainNo + ",'" + names[i ] + "');";
                            // stmt.executeQuery(query_passenger);
                            // System.out.println(query_passenger);
                        }   
                        

                        String berthType = "";
                        if(coachType.equals("AC"))
                        {
                            if(berthNo % 6 == 1)	berthType = "LB";
                            if(berthNo % 6 == 2)	berthType = "LB";
                            if(berthNo % 6 == 3)	berthType = "UB";
                            if(berthNo % 6 == 4)	berthType = "UB";
                            if(berthNo % 6 == 5)	berthType = "SL";
                            if(berthNo % 6 == 0)	berthType = "SU";
                        }
                        else 
                        {
                            if(berthNo % 8 == 1)	berthType = "LB";
                            if(berthNo % 8 == 2)	berthType = "MB";
                            if(berthNo % 8 == 3)	berthType = "UB";
                            if(berthNo % 8 == 4)	berthType = "LB";
                            if(berthNo % 8 == 5)	berthType = "MB";
                            if(berthNo % 8 == 6)	berthType = "UB";
                            if(berthNo % 8 == 7)	berthType = "SL";
                            if(berthNo % 8 == 0)	berthType = "SU";
                        }

                        // final_string += ("\tName: " + names[i] + "\n");
                        // final_string += ("\tBerth: " + berthNo + " " + berthType + "\n");
                        // final_string += ("\tCoach: " + coachNo + " " + coachType + "\n");
                        printWriter.print("\tName: " + names[i]);
                        printWriter.print("\tBerth: " + berthNo + " " + berthType);
                        printWriter.println("\tCoach: " + coachNo + " " + coachType);
                    }
                }
                printWriter.println(final_string);
            }
        } 
        catch (Exception e) 
        {
            return;
        }
    }
}

/**
 * Main Class to controll the program flow
 */
public class ServiceModule {
    // Server listens to port
    static int serverPort = 7008;
    // Max no of parallel requests the server can process
    static int numServerCores = 2;

    // ------------ Main----------------------
    public static void main(String[] args) throws IOException {
        Connection conn = null;
        Statement st = null;

        try {
            System.out.println("Trying to connect..");

            String url = "jdbc:postgresql://localhost:5432/";
            String username = "postgres";
            String password = "dbms";
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, username, password);
            st = conn.createStatement();

            System.out.println("Connected To Database :)");

        } catch (Exception e) {
            // exception
        }

        // Creating a thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(numServerCores);

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            Socket socketConnection = null;

            // Always-ON server
            while (true) {
                // System.out.println("Listening port : " + serverPort + "\nWaiting for
                // clients...");

                socketConnection = serverSocket.accept(); // Accept a connection from a client

                // System.out.println("Accepted client :" +
                // socketConnection.getRemoteSocketAddress().toString() + "\n");

                // Create a runnable task
                Runnable runnableTask = new QueryRunner(socketConnection, conn, st);
                // Submit task for execution
                executorService.submit(runnableTask);
            }
        }
    }
}
