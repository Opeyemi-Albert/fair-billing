package org.btgroup.billingservice;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;


public class SessionAnalyzer {
    public static class Session {
        private final String userName;
        private final String eventType;
        private final Long timestamp;

        public Session(String userName, String eventType, Long timestamp) {
            this.userName = userName;
            this.eventType = eventType;
            this.timestamp = timestamp;
        }
        public String getUserName() {
            return userName;
        }
        public String getEventType() {
            return eventType;
        }
        public Long getTimestamp() {
            return timestamp;
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java SessionAnalyzer.java <path_to_log_file>");
            System.exit(1);
        }

       String filePath =  args[0]; // "/Users/decagon/Downloads/fair-billing/src/main/resources/session.txt";
        Long earliestTime = 0L;
        Long latestTime = 0L;
        int count = 0;

        Map<String, PriorityQueue<Session>> sessionsMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            Session session = null;
            while ((line = br.readLine()) != null) {
                session = parseLineAndMapToSessionObject(line.trim());

                if (session != null) {
                    if(count == 0){
                        earliestTime = session.getTimestamp();
                    }
                    sessionsMap.computeIfAbsent(session.getUserName(), k -> new PriorityQueue<>(Comparator.comparingLong(Session::getTimestamp)))
                            .add(session);
                }
                count++;
            }
            if(session != null){
                latestTime = session.getTimestamp();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        printSessionReport(sessionsMap, earliestTime, latestTime);
    }

    public static Session parseLineAndMapToSessionObject(String line) {

        String[] parts = line.split("\\s+");
        if (parts.length == 3) {
            String time = parts[0];
            String userName = parts[1];
            String eventType = parts[2];

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                sdf.setLenient(false);
                long timestamp = sdf.parse(time).getTime();

                if(!(eventType.equalsIgnoreCase("Start") || eventType.equalsIgnoreCase("End")))
                    throw new IllegalArgumentException("EventType should be either 'Start' or 'End' ");

                return new Session(userName, eventType, timestamp);
            } catch (Exception e) {
                System.err.println(e.getMessage() + " at " + line);
            }
        } else {
            System.err.println("Each session should have 3 parts - " + line);
        }
        return null;
    }

    public static void printSessionReport(Map<String, PriorityQueue<Session>> sessionsMap, Long earliestTime, Long latestTime) {
        for (Map.Entry<String, PriorityQueue<Session>> entry : sessionsMap.entrySet()) {
            String userName = entry.getKey();
            PriorityQueue<Session> sessions = entry.getValue();
            int totalSessions = 0;
            long totalDuration = 0;


            while (!sessions.isEmpty()) {
                Session newSessionLine = sessions.poll();

                if (newSessionLine.getEventType().equalsIgnoreCase("Start")) {
                    Session newSessionMatchingEnd = findMatchingEnd(sessions, userName, newSessionLine.getTimestamp());
                    // for sessions with a "Start" and a corresponding "End"
                    if (newSessionMatchingEnd != null) {
                        totalDuration += newSessionMatchingEnd.getTimestamp() - newSessionLine.getTimestamp();
                    } else {
                        // sessions having a "Start" but no "End"
                        totalDuration += latestTime - newSessionLine.getTimestamp();
                    }
                } else {
                    // For sessions with "End" but no corresponding "Start". Thus, their start time is the earliest time.
                    totalDuration += newSessionLine.getTimestamp() - earliestTime;
                }
                totalSessions++;
            }

            System.out.println(userName + " " + totalSessions + " " + totalDuration / 1000); // Convert milliseconds to seconds
        }
    }

    public static Session findMatchingEnd(PriorityQueue<Session> sessions, String userName, long startTime) {

        List<Session> tempQueue = new ArrayList<>();
        while (!sessions.isEmpty()) {

            Session session = sessions.poll();
            if (session.getUserName().equals(userName) && session.getEventType().equals("End") && session.getTimestamp() > startTime) {
                // remove the matching end from the queue
                sessions.remove(session);

                // add all the unprocessed start session to the original queue
                sessions.addAll(tempQueue);

                // return the found end session
                return session;
            }
            // temporarily save start session for processing
            tempQueue.add(session);
        }
        return null;
    }

}



