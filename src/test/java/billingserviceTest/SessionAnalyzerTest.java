package billingserviceTest;

import org.btgroup.billingservice.SessionAnalyzer;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.*;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class SessionAnalyzerTest {
    @Test
    public void testIfMethodWillParseLineAndMapToSessionObjectorNot() {
        // Test valid input
        SessionAnalyzer.Session session = SessionAnalyzer.parseLineAndMapToSessionObject("14:02:03 ALICE99 Start");
        assertNotNull(session);
        assertEquals("ALICE99", session.getUserName());
        assertEquals("Start", session.getEventType());
        assertEquals(Optional.of(46923000L).get(), session.getTimestamp());

        // Test invalid input
        assertNull(SessionAnalyzer.parseLineAndMapToSessionObject("14:02:63 ALICE99 End"));
        assertNull(SessionAnalyzer.parseLineAndMapToSessionObject("14:02:03 ALICE99End"));
        assertNull(SessionAnalyzer.parseLineAndMapToSessionObject("14:02:03 ALICE99 Started"));
    }

    @Test
    public void testIfMethodWillFindAMatchingEndOrNot() {
        // Create sessions for testing
        SessionAnalyzer.Session session1 = new SessionAnalyzer.Session("ALICE99", "Start", 1639263723000L);
        SessionAnalyzer.Session session2 = new SessionAnalyzer.Session("CHARLIE", "End", 1639263725000L);
        SessionAnalyzer.Session session3 = new SessionAnalyzer.Session("ALICE99", "End", 1639263774000L);
        SessionAnalyzer.Session[] sessionsArray = {session1, session2, session3};
        PriorityQueue<SessionAnalyzer.Session> sessions = Arrays.stream(sessionsArray)
                .collect(Collectors.toCollection(() -> new PriorityQueue<>(Comparator.comparingLong(SessionAnalyzer.Session::getTimestamp))));

        // Test if method will find matching end found
        SessionAnalyzer.Session matchingEnd = SessionAnalyzer.findMatchingEnd(sessions, "ALICE99", 1639263723000L);
        assertNotNull(matchingEnd);
        assertEquals("ALICE99", matchingEnd.getUserName());
        assertEquals("End", matchingEnd.getEventType());
        assertEquals(Optional.of(1639263774000L).get(), matchingEnd.getTimestamp());

        // Test for matching-end not found
        assertNull(SessionAnalyzer.findMatchingEnd(sessions, "CHARLIE", 1639263725000L));
    }
    @Test
    public void testIfMethodWillPrintSessionReport() {
        // Create sessions for testing
        SessionAnalyzer.Session session1 = new SessionAnalyzer.Session("ALICE99", "Start", 1639263723000L);
        SessionAnalyzer.Session session2 = new SessionAnalyzer.Session("ALICE99", "End", 1639263774000L);
        SessionAnalyzer.Session session3 = new SessionAnalyzer.Session("CHARLIE", "Start", 1639263800000L);
        SessionAnalyzer.Session session4 = new SessionAnalyzer.Session("CHARLIE", "End", 1639263850000L);

        SessionAnalyzer.Session[] sessionsArray1 = {session1, session2};
        PriorityQueue<SessionAnalyzer.Session> sessions1 = Arrays.stream(sessionsArray1)
                .collect(Collectors.toCollection(() -> new PriorityQueue<>(Comparator.comparingLong(SessionAnalyzer.Session::getTimestamp))));

        SessionAnalyzer.Session[] sessionsArray2 = {session3, session4};
        PriorityQueue<SessionAnalyzer.Session> sessions2 = Arrays.stream(sessionsArray2)
                .collect(Collectors.toCollection(() -> new PriorityQueue<>(Comparator.comparingLong(SessionAnalyzer.Session::getTimestamp))));

        // Create sessions map for testing
        Map<String, PriorityQueue<SessionAnalyzer.Session>> sessionsMap = new HashMap<>();
        sessionsMap.put("ALICE99", sessions1);
        sessionsMap.put("CHARLIE", sessions2);

        // Redirect output stream for capturing printed output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Run the printSessionReport method
        SessionAnalyzer.printSessionReport(sessionsMap, 1639263723000L, 1639263850000L);

        // Verify the printed output
        String expectedOutput = "ALICE99 1 51\nCHARLIE 1 50";
        assertEquals(expectedOutput, outputStream.toString().trim());

    }
}


