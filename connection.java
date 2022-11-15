import java.util.*;
import java.util.Date;

import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Integers;

// import org.postgresql.shaded.com.ongres.stringprep.StringPrep;
// import org.postgresql.ssl.SingleCertValidatingFactory.SingleCertTrustManager;

import java.text.SimpleDateFormat;
import java.sql.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class connection {
    public static void main(String[] args) throws Exception {

        // connecting to postgres.
        String url = "jdbc:postgresql://localhost:5433/";
        String username = "postgres";
        String password = "dbms";
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection(url, username, password);
        Statement st = conn.createStatement();

        String[] AcCoach = { "SU", "LB", "LB", "UB", "UB", "SL", "SU", "LB", "LB", "UB", "UB", "SL", "SU", "LB", "LB",
                "UB", "UB", "SL" };
        String[] SlCoach = { "SU", "LB", "MB", "UB", "LB", "MB", "UB", "SL", "SU", "LB", "MB", "UB", "LB", "MB", "UB",
                "SL", "SU", "LB", "MB", "UB", "LB", "MB", "UB", "SL" };

        try 
        {
            File file = new File("log.txt");
            PrintStream stream = new PrintStream(file);
            System.setOut(stream);

            File Obj = new File("our.txt");
            Scanner Reader = new Scanner(Obj);
            String endoffile = "#";
            while (true) 
            {
                // System.out.println("\n\n");
                String data = Reader.nextLine();
                if (data.equals(endoffile)) 
                {
                    break;
                }

                // train scheduling.
                if (data.charAt(data.length() - 1) >= '0' && data.charAt(data.length() - 1) <= '9') 
                {
                    // System.out.println("train");
                    String[] words = data.split("\\s");

                    String DOJ = words[1];
                    int trainNo = Integer.parseInt(words[0]);
                    int ACcoach = Integer.parseInt(words[2]);
                    int SleeperCoach = Integer.parseInt(words[4]);

                    // System.out.println(DOJ + " " + trainNo + " " + ACcoach + " " + SleeperCoach);

                    String query_check = "SELECT train_present_check(" + trainNo + ");";
                    ResultSet rs = st.executeQuery(query_check);
                    int addorNot = 0;
                    while (rs.next()) 
                    {
                        addorNot = rs.getInt(1);
                    }

                    if (addorNot == 0) 
                    {
                        // System.out.println("adding train..." + trainNo);
                        String query_add = "SELECT add_train( " + trainNo + ");";
                        st.executeQuery(query_add);
                    } 
                    else 
                    {
                        // System.out.println("Train already there.." + trainNo);
                    }

                    String query_journey = "SELECT check_journey( '" + DOJ + "', " + trainNo + ");";
                    rs = st.executeQuery(query_journey);
                    addorNot = 0;
                    while (rs.next()) 
                    {
                        addorNot = rs.getInt(1);
                    }

                    if (addorNot == 0) 
                    {     
                        // System.out.println("Adding journey.." + DOJ + " " + trainNo);
                        query_journey = "SELECT add_journey( '" + DOJ + "', " + trainNo + ", " + ACcoach + ", "
                                + SleeperCoach + ");";
                        st.executeQuery(query_journey);
                    } 
                    else 
                    {
                        // System.out.println("Journey Present. " + DOJ + " " + trainNo);
                    }

                } 
                else 
                {
                    // ticket booking
                    String[] words = data.split("\\s");

                    int numofpeople = Integer.parseInt(words[0]);

                    String[] names = new String[numofpeople];
                    int i = 1;
                    for (i = 1; i < numofpeople; i++) 
                    {
                        String n = words[i].substring(0, words[i].length() - 1);
                        names[i - 1] = n;
                    }
                    names[numofpeople - 1] = words[numofpeople];


                    String DOJ = words[numofpeople + 2];
                    int trainNo = Integer.parseInt(words[numofpeople + 1]);
                    String coachType = words[numofpeople + 3];
                    // System.out.println(DOJ + " "+ trainNo+ " "+ coach);

                    String query_check = "SELECT train_present_check(" + trainNo + ");";
                    int cantbook = 0;

                    ResultSet rs = st.executeQuery(query_check);
                    while (rs.next()) 
                    {
                        cantbook = rs.getInt(1);
                    }
                    if (cantbook == 0) 
                    {
                        System.out.println("Journey is not possible. (train not running.)");
                        continue;
                    }

                    String query_journey = "SELECT check_journey( '" + DOJ + "', " + trainNo + ");";
                    rs = st.executeQuery(query_journey);
                    while (rs.next()) {
                        cantbook = rs.getInt(1);
                    }
                    if (cantbook == 0) 
                    {
                        System.out.println("Journey is not possible. (train not running.)");
                        continue;
                    }

                    String query_seats = "SELECT is_seats_avaiable( '" + DOJ + "', " + trainNo + ", '" + coachType
                    + "', " + numofpeople + ");";
                    rs = st.executeQuery(query_seats);
                    
                    
                    int seats_avaiable = 0;
                    while (rs.next()) 
                    {
                        seats_avaiable = rs.getInt(1);
                    }
                    if (seats_avaiable < 0) 
                    {
                        System.out.println("Journey is not possible. (train not empty.)");
                        continue;
                    }


                    String query_ticket = "SELECT create_ticket( '" + DOJ + "', " + numofpeople + ", " + trainNo + ", '"
                    + coachType + "');";
                    rs = st.executeQuery(query_ticket);
                    
                    String pNR = "";
                    while (rs.next()) 
                    {
                        pNR = rs.getString(1);
                    }
                    
                    String[] printing_ticket = new String[7*numofpeople+1];
                    printing_ticket[0] = pNR;

                    for (i = 1; i <= numofpeople; i++) 
                    {
                        if (coachType == "AC") 
                        {
                            int berthNo = (i + seats_avaiable) % 18;
                            int coachNo = (i + seats_avaiable) / 18 + 1;
                            
                            // String berthType = AcCoach[berthNo];
                            String query_passenger = "SELECT adding_passenger( '" + DOJ + "', '" + pNR + "', " + berthNo + ",'AC', " + coachNo + ", " + trainNo + ",'" + names[i - 1] + "');";
                            st.executeQuery(query_passenger);
                            // System.out.println(query_passenger);

                            printing_ticket[(i-1)*7 + 1] = names[i-1];
                            printing_ticket[(i-1)*7 + 2] = Integer.toString(berthNo);
                            printing_ticket[(i-1)*7 + 3] = AcCoach[berthNo];
                            printing_ticket[(i-1)*7 + 4] = "AC";
                            printing_ticket[(i-1)*7 + 5] = Integer.toString(coachNo);
                            printing_ticket[(i-1)*7 + 6] = Integer.toString(trainNo);
                            printing_ticket[(i-1)*7 + 7] = DOJ;
                            
                            // name berthNo berthType coachType coachNo trainNo date 
                        } 
                        else 
                        {
                            int berthNo = (i + seats_avaiable) % 24;
                            int coachNo = (i + seats_avaiable) / 24 + 1;

                            String query_passenger = "SELECT adding_passenger( '" + DOJ + "','" + pNR + "', " + berthNo + ", 'AC', " + coachNo + ", " + trainNo + ",'" + names[i - 1] + "');";
                            st.executeQuery(query_passenger);
                            // System.out.println(query_passenger);

                            printing_ticket[(i-1)*7 + 1] = names[i-1];
                            printing_ticket[(i-1)*7 + 2] = Integer.toString(berthNo);
                            printing_ticket[(i-1)*7 + 3] = SlCoach[berthNo];
                            printing_ticket[(i-1)*7 + 4] = "SL";
                            printing_ticket[(i-1)*7 + 5] = Integer.toString(coachNo);
                            printing_ticket[(i-1)*7 + 6] = Integer.toString(trainNo);
                            printing_ticket[(i-1)*7 + 7] = DOJ;

                        }
                    }

                    System.out.println("Ticket Booked with " + pNR + "");
                    for(int j=1; j <= 7*numofpeople; )
                    {
                        System.out.print("\tName: " + printing_ticket[j++]);
                        System.out.print("\tBerth No: " + printing_ticket[j++]);
                        System.out.println("\tBerth Type: " + printing_ticket[j++]);
                        System.out.print("\tCoach Type: " + printing_ticket[j++]);
                        System.out.println("\tCoach No: " + printing_ticket[j++]);
                        System.out.print("\tTrain No: " + printing_ticket[j++]);
                        System.out.println("\tDate: " + printing_ticket[j++] + "\n");
                    }

                }
            }
            Reader.close();
        } catch (

        FileNotFoundException e) {
            System.out.println("An error has occurred.");
            e.printStackTrace();
        }
    }
}

// st.executeQuery(query);
// st.close();
// rs.close();
// add_train(3);
