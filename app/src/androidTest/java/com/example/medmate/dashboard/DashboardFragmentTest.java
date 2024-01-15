package com.example.medmate.dashboard;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static java.util.regex.Pattern.matches;

import com.example.medmate.MainActivity;
import com.example.medmate.R;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
@RunWith(AndroidJUnit4.class)
public class DashboardFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void navigateToDashboardFragment_andTestIPAddress() {
        // Navigate to the DashboardFragment
        onView(withId(R.id.navigation_history)).perform(click());

        // Check if the initial IP address is correct
        onView(withId(R.id.ipAddressInput)).check(matches(withText("192.168.0.105")));

        // Change the IP address
        String newIpAddress = "192.168.1.1";
        onView(withId(R.id.ipAddressInput)).perform(replaceText(newIpAddress), closeSoftKeyboard());

        // Check if the IP address is updated correctly
        onView(withId(R.id.ipAddressInput)).check(matches(withText(newIpAddress)));
    }
}