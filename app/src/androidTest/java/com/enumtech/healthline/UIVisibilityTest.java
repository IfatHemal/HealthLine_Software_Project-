package com.enumtech.healthline;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
public class UIVisibilityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testUIElementsVisible() {


        onView(withId(R.id.etEmail))
                .check(matches(isDisplayed()));


        onView(withId(R.id.etPassword))
                .check(matches(isDisplayed()));


        onView(withId(R.id.btnLogin))
                .check(matches(isDisplayed()));
    }
}