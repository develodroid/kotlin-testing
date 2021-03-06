/*
 * Copyright 2015, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.testing.notes.notes

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.DrawerActions.open
import android.support.test.espresso.contrib.DrawerMatchers.isClosed
import android.support.test.espresso.contrib.DrawerMatchers.isOpen
import android.support.test.espresso.contrib.NavigationViewActions.navigateTo
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import com.example.android.testing.notes.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the [DrawerLayout] layout component in [NotesActivity] which manages
 * navigation within the app.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class AppNavigationTest {

    /**
     * [ActivityTestRule] is a JUnit [@Rule][Rule] to launch your activity under test.

     *
     *
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     *
     * @get:Rule
    public val mActivityRule: ActivityTestRule<MainActivity> = ActivityTestRule(javaClass<MainActivity>())
     */
    @Rule @JvmField
    val mActivityTestRule: ActivityTestRule<NotesActivity> = ActivityTestRule(NotesActivity::class.java)

    @Test
    fun clickOnStatisticsNavigationItem_ShowsStatisticsScreen() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open())
                // Open Drawer

        // Start statistics screen.
        onView(withId(R.id.nav_view)).perform(navigateTo(R.id.statistics_navigation_menu_item))

        // Check that statistics Activity was opened.
        val expectedNoStatisticsText = InstrumentationRegistry.getTargetContext().getString(R.string.no_statistics_available)
        onView(withId(R.id.no_statistics)).check(matches(withText(expectedNoStatisticsText)))
    }

    @Test
    fun clickOnAndroidHomeIcon_OpensNavigation() {
        // Check that left drawer is closed at startup
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.

        // Open Drawer
        val navigateUpDesc = mActivityTestRule.activity.getString(android.support.v7.appcompat.R.string.abc_action_bar_up_description)
        onView(withContentDescription(navigateUpDesc)).perform(click())

        // Check if drawer is open
        onView(withId(R.id.drawer_layout)).check(matches(isOpen(Gravity.LEFT))) // Left drawer is open open.
    }

}