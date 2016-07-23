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

package com.example.android.testing.notes.notedetail

import android.app.Activity
import android.content.Intent
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.example.android.testing.notes.R
import com.example.android.testing.notes.data.FakeNotesServiceApiImpl
import com.example.android.testing.notes.data.Note
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the notes screen, the main screen which contains a list of all notes.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class NoteDetailScreenTest {

    /**
     * [ActivityTestRule] is a JUnit [@Rule][Rule] to launch your activity under test.

     *
     *
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.

     *
     *
     * Sometimes an [Activity] requires a custom start [Intent] to receive data
     * from the source Activity. ActivityTestRule has a feature which let's you lazily start the
     * Activity under test, so you can control the Intent that is used to start the target Activity.
     */


    @Rule @JvmField
    var mNoteDetailActivityTestRule = ActivityTestRule(NoteDetailActivity::class.java, true /* Initial touch mode  */,
            false /* Lazily launch activity */)

    /**
     * Setup your test fixture with a fake note id. The [NoteDetailActivity] is started with
     * a particular note id, which is then loaded from the service API.

     *
     *
     * Note that this test runs hermetically and is fully isolated using a fake implementation of
     * the service API. This is a great way to make your tests more reliable and faster at the same
     * time, since they are isolated from any outside dependencies.
     */
    @Before
    fun intentWithStubbedNoteId() {
        // Add a note stub to the fake service api layer.
        FakeNotesServiceApiImpl.addNotes(NOTE)

        // Lazily start the Activity from the ActivityTestRule this time to inject the start Intent
        val startIntent = Intent()
        startIntent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, NOTE.id)
        mNoteDetailActivityTestRule.launchActivity(startIntent)

        registerIdlingResource()
    }

    @Test
    @Throws(Exception::class)
    fun noteDetails_DisplayedInUi() {
        // Check that the note title, description and image are displayed
        onView(withId(R.id.note_detail_title)).check(matches(withText(NOTE_TITLE)))
        onView(withId(R.id.note_detail_description)).check(matches(withText(NOTE_DESCRIPTION)))
        onView(withId(R.id.note_detail_image)).check(matches(allOf(
                //hasDrawable(),
                isDisplayed())))
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        Espresso.unregisterIdlingResources(
                mNoteDetailActivityTestRule.activity.countingIdlingResource)
    }

    /**
     * Convenience method to register an IdlingResources with Espresso. IdlingResource resource is
     * a great way to tell Espresso when your app is in an idle state. This helps Espresso to
     * synchronize your test actions, which makes tests significantly more reliable.
     */
    private fun registerIdlingResource() {
        Espresso.registerIdlingResources(
                mNoteDetailActivityTestRule.activity.countingIdlingResource)
    }

    companion object {

        private val NOTE_TITLE = "ATSL"

        private val NOTE_DESCRIPTION = "Rocks"

        private val NOTE_IMAGE = "file:///android_asset/atsl-logo.png"

        /**
         * [Note] stub that is added to the fake service API layer.
         */
        private val NOTE = Note(NOTE_TITLE, NOTE_DESCRIPTION, NOTE_IMAGE)
    }
}