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

import com.example.android.testing.notes.R

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollTo
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import com.google.common.base.Preconditions.checkArgument
import org.hamcrest.Matchers.allOf

/**
 * Tests for the notes screen, the main screen which contains a grid of all notes.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class NotesScreenTest {

    /**
     * A custom [Matcher] which matches an item in a [RecyclerView] by its text.

     *
     *
     * View constraints:
     *
     *  * View must be a child of a [RecyclerView]
     *

     * @param itemText the text to match
     * *
     * @return Matcher that matches text in the given view
     */
    private fun withItemText(itemText: String): Matcher<View> {
        checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty")
        return object : TypeSafeMatcher<View>() {
            public override fun matchesSafely(item: View): Boolean {
                return allOf(
                        isDescendantOfA(isAssignableFrom(RecyclerView::class.java!!)),
                        withText(itemText)).matches(item)
            }

            override fun describeTo(description: Description) {
                description.appendText("is isDescendantOfA RV with text " + itemText)
            }
        }
    }

    /**
     * [ActivityTestRule] is a JUnit [@Rule][Rule] to launch your activity under test.

     *
     *
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    var mNotesActivityTestRule = ActivityTestRule<NotesActivity>(NotesActivity::class.java)

    @Test
    @Throws(Exception::class)
    fun clickAddNoteButton_opensAddNoteUi() {
        // Click on the add note button
        onView(withId(R.id.fab_add_notes)).perform(click())

        // Check if the add note screen is displayed
        onView(withId(R.id.add_note_title)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun addNoteToNotesList() {
        val newNoteTitle = "Espresso"
        val newNoteDescription = "UI testing for Android"

        // Click on the add note button
        onView(withId(R.id.fab_add_notes)).perform(click())

        // Add note title and description
        // Type new note title
        onView(withId(R.id.add_note_title)).perform(typeText(newNoteTitle), closeSoftKeyboard())
        onView(withId(R.id.add_note_description)).perform(typeText(newNoteDescription),
                closeSoftKeyboard()) // Type new note description and close the keyboard

        // Save the note
        onView(withId(R.id.fab_add_notes)).perform(click())

        // Scroll notes list to added note, by finding its description
        onView(withId(R.id.notes_list)).perform(
                scrollTo<ViewHolder>(hasDescendant(withText(newNoteDescription))))

        // Verify note is displayed on screen
        onView(withItemText(newNoteDescription)).check(matches(isDisplayed()))
    }

}