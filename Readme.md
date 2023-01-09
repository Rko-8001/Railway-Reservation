Contributors:
                Vinay Kumar      2020csb1141
                Yadwinder Singh  2020csb1143

RUN THE FOLLOWING COMMANDS IN POSTGRES:
    DROP SCHEMA PUBLIC CASCADE;
    CREATE SCHEMA PUBLIC:

    Now upload the all the postgres commands from 'Database.txt' file. 

Before running the server change url in 'ServiceModule.java'.

HOW TO GIVE INPUT:
    Add the files in Input directory.
    If you want to add the trains add only train scheduling queries in the files.
    If you want to book tickets then add only ticket booking queries in the files.

HOW TO RUN:
    First run Service Module to run the server by using the command
        javac ServiceMoudle.java
        java ServiceModule
    
    Open a new terminal and run Client to give queries
        javac client.java
        java client

HOW YOU WILL GET OUTPUT:
    All the output files will be generated in Output directory.
    For train scheduling queries - the output will be of form 
                                   Train/Journey already exit 
                                    or
                                   Train Added

    For ticket booking queries - the output will be of form 
                                 Journey not possible
                                  or
                                 [PNR 
                                 Name of passengers
                                 Coach No
                                 Berth No
                                 Berth Type
                                 Train No
                                 Date of Journey]
