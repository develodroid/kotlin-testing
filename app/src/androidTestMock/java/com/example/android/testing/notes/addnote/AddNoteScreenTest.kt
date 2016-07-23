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

package com.example.android.testing.notes.addnote

import com.example.android.testing.notes.R

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.provider.MediaStore
import android.support.test.espresso.Espresso
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4

import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.hasAction
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import com.example.android.testing.notes.custom.matcher.ImageViewHasDrawableMatcher.hasDrawable
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not

/**
 * Tests for the add note screen.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class AddNoteScreenTest {

    /**
     * [IntentsTestRule] is an [ActivityTestRule] which inits and releases Espresso
     * Intents before and after each test run.

     *
     *
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    var mAddNoteIntentsTestRule = IntentsTestRule<AddNoteActivity>(AddNoteActivity::class.java)

    /**
     * Prepare your test fixture for this test. In this case we register an IdlingResources with
     * Espresso. IdlingResource resource is a great way to tell Espresso when your app is in an
     * idle state. This helps Espresso to synchronize your test actions, which makes tests significantly
     * more reliable.
     */
    @Before
    fun registerIdlingResource() {
        Espresso.registerIdlingResources(
                mAddNoteIntentsTestRule.activity.countingIdlingResource)
    }

    @Test
    fun addImageToNote_ShowsThumbnailInUi() {
        // Create an Activity Result which can be used to stub the camera Intent
        val result = createImageCaptureActivityResultStub()
        // If there is an Intent with ACTION_IMAGE_CAPTURE, intercept the Intent and respond with
        // a stubbed result.
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result)

        // Check thumbnail view is not displayed
        onView(withId(R.id.add_note_image_thumbnail)).check(matches(not(isDisplayed())))

        selectTakeImageFromMenu()

        // Check that the stubbed thumbnail is displayed in the UI
        onView(withId(R.id.add_note_image_thumbnail)).perform(scrollTo()) // Scroll to thumbnail
                .check(matches(allOf(
                        hasDrawable(), // Check ImageView has a drawable set with a custom matcher
                        isDisplayed())))
    }

    @Test
    fun errorShownOnEmptyMessage() {
        onView(withId(R.id.fab_add_notes)).perform(click())
        // Add note title and description
        onView(withId(R.id.add_note_title)).perform(typeText(""))
        onView(withId(R.id.add_note_description)).perform(typeText(""),
                closeSoftKeyboard())
        // Save the note
        onView(withId(R.id.fab_add_notes)).perform(click())

        // Verify empty notes snackbar is shown
        val emptyNoteMessageText = getTargetContext().getString(R.string.empty_note_message)
        onView(withText(emptyNoteMessageText)).check(matches(isDisplayed()))
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        Espresso.unregisterIdlingResources(
                mAddNoteIntentsTestRule.activity.countingIdlingResource)
    }

    /**
     * Convenience method which opens the options menu and select the take image option.
     */
    private fun selectTakeImageFromMenu() {
        openActionBarOverflowOrOptionsMenu(getTargetContext())

        // Click on add picture option
        onView(withText(R.string.take_picture)).perform(click())
    }

    private fun createImageCaptureActivityResultStub(): ActivityResult {
        // Create the ActivityResult, with a null Intent since we do not want to return any data
        // back to the Activity.
        return ActivityResult(Activity.RESULT_OK, null)
    }

}