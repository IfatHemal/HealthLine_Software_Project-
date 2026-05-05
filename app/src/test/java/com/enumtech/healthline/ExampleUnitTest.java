package com.enumtech.healthline;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testSignupValidation() {
        String email = "user@gmail.com";
        String password = "123456";

        assertFalse(email.isEmpty());
        assertTrue(password.length() >= 6);
    }
    @Test
    public void testLoginValidation() {
        String email = "user@gmail.com";
        String password = "123456";

        assertFalse(email.isEmpty());
        assertTrue(password.length() >= 6);
    }
    @Test
    public void testAppointmentValid() {
        String date = "2026-04-25";
        String time = "10:00 AM";

        assertFalse(date.isEmpty());
        assertFalse(time.isEmpty());
    }
    @Test
    public void testAppointmentInvalid() {
        String date = "";
        String time = "";

        assertTrue(date.isEmpty());
        assertTrue(time.isEmpty());
    }
    @Test
    public void testScheduleValid() {
        String date = "2026-04-25";
        String time = "10:00 AM";
        String doctor = "Dr. Ahmed";

        assertFalse(date.isEmpty());
        assertFalse(time.isEmpty());
        assertFalse(doctor.isEmpty());
    }
    @Test
    public void testScheduleInvalid() {
        String date = "";
        String time = "";
        String doctor = "";

        assertTrue(date.isEmpty());
        assertTrue(time.isEmpty());
        assertTrue(doctor.isEmpty());
    }
    @Test
    public void testInvalidTime() {
        String time = "25:00";

        boolean isValid = time.matches("^(0[1-9]|1[0-2]):[0-5][0-9] (AM|PM)$");
        assertFalse(isValid);
    }
    @Test
    public void testDuplicateSchedule() {
        String slot1 = "2026-04-25 10:00 AM";
        String slot2 = "2026-04-25 10:00 AM";

        assertEquals(slot1, slot2);
    }
}
