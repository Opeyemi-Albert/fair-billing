## Fair-Billing Application

### Created By: Opeyemi Albert Adeoye 
## Overview
This command-line application is designed for hosted application providers who charge users based on the duration of their sessions.
The billing is done on a per-second basis, and the usage data is provided in a log file. 
The log file contains entries for session start and end times, usernames, and the session status (Start or End). 
However, there is no explicit pairing information between start and end lines, and the log files may lack certain crucial details.

The primary goal of this application is to generate a report that includes the number of sessions, and the minimum possible total duration of each user's sessions in seconds. 
The application handles scenarios where sessions may overlap the time boundaries of the log file, and sessions may be ongoing when the log file starts or ends.


## Usage
To use the Fair Billing application, follow these steps:

   1. Ensure you have Java installed on your system.
   2. Open a terminal or command prompt.
   3. from the root project, cd into src/main/java/org/btgroup/billingservice
   4. Run the application with the following command:

    javac SessionAnalyzer.java
    java SessionAnalyzer.java path/to/logfile.txt

Replace "path/to/logfile.txt" with the actual path to your log file.

## Input Format
The log file should contain entries with the following format:

    HH:MM:SS USERNAME Start/End

1. HH:MM:SS: Time stamp indicating the session start or end time.
2. USERNAME: Alphanumeric string representing the user.
3. Start/End: Marker indicating whether the entry corresponds to the start or end of a session.

#### Example Entries:
    `14:02:03 ALICE99 Start`
    `14:02:05 CHARLIE End`
    `14:02:34 ALICE99 End`
Invalid or irrelevant lines, lacking a valid time-stamp, username, or a Start/End marker, will be silently ignored.

## Output Format
The application will generate a report with the following information for each user:

    Username
    Number of sessions
    Minimum possible total duration of sessions in seconds

#### Example output for the provided data:
    `ALICE99 4 240`
    `CHARLIE 3 37`

## Running the Tests

To run the tests in this application:
1. Navigate to the root directory of this project ../../fair-billing
2. Run the following command.

       mvn clean test
Ensure you have maven installed on you computer.

## Note
    1. The application assumes correct chronological ordering of data and that all records are from within a single day (no spanning midnight).
    2. Any lines lacking valid information will be ignored.

### Technologies Used
* Java 8
* Maven 3.9.5
* TDD

### Dependencies
* Junit 4.13.2





