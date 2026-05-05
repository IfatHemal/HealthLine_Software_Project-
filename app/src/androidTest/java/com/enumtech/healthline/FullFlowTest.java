package com.enumtech.healthline;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
public class FullFlowTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testFullLoginFlow() throws InterruptedException {


        Thread.sleep(2000);


        onView(withId(R.id.etEmail))
                .perform(typeText("test@gmail.com"), closeSoftKeyboard());


        onView(withId(R.id.etPassword))
                .perform(typeText("123456"), closeSoftKeyboard());


        onView(withId(R.id.btnLogin))
                .perform(click());


        Thread.sleep(4000);


        onView(withText("HealthLine"))
                .check(matches(isDisplayed()));
    }
}