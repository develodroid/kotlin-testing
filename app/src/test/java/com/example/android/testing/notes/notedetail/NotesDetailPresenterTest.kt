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

import com.example.android.testing.notes.data.Note
import com.example.android.testing.notes.data.NotesRepository
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Matchers.eq
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * Unit tests for the implementation of [NoteDetailPresenter]
 */
class NotesDetailPresenterTest {

    @Mock
    private val mNotesRepository: NotesRepository? = null

    @Mock
    private val mNoteDetailView: NoteDetailContract.View? = null

    /**
     * [ArgumentCaptor] is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private val mGetNoteCallbackCaptor: ArgumentCaptor<NotesRepository.GetNoteCallback>? = null

    private var mNotesDetailsPresenter: NoteDetailPresenter? = null

    @Before
    fun setupNotesPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        mNotesDetailsPresenter = NoteDetailPresenter(mNotesRepository!!, mNoteDetailView!!)
    }

    @Test
    fun getNoteFromRepositoryAndLoadIntoView() {
        // Given an initialized NoteDetailPresenter with stubbed note
        val note = Note(TITLE_TEST, DESCRIPTION_TEST)

        // When notes presenter is asked to open a note
        mNotesDetailsPresenter!!.openNote(note.id)

        // Then note is loaded from model, callback is captured and progress indicator is shown
        verify<NotesRepository>(mNotesRepository).getNote(eq(note.id), mGetNoteCallbackCaptor!!.capture())
        verify<NoteDetailContract.View>(mNoteDetailView).setProgressIndicator(true)

        // When note is finally loaded
        mGetNoteCallbackCaptor.value.onNoteLoaded(note) // Trigger callback

        // Then progress indicator is hidden and title and description are shown in UI
        verify<NoteDetailContract.View>(mNoteDetailView).setProgressIndicator(false)
        verify<NoteDetailContract.View>(mNoteDetailView).showTitle(TITLE_TEST)
        verify<NoteDetailContract.View>(mNoteDetailView).showDescription(DESCRIPTION_TEST)
    }

    @Test
    fun getUnknownNoteFromRepositoryAndLoadIntoView() {
        // When loading of a note is requested with an invalid note ID.
        mNotesDetailsPresenter!!.openNote(INVALID_ID)

        // Then note with invalid id is attempted to load from model, callback is captured and
        // progress indicator is shown.
        verify<NoteDetailContract.View>(mNoteDetailView).setProgressIndicator(true)
        verify<NotesRepository>(mNotesRepository).getNote(eq(INVALID_ID), mGetNoteCallbackCaptor!!.capture())

        // When note is finally loaded
        mGetNoteCallbackCaptor.value.onNoteLoaded(null) // Trigger callback

        // Then progress indicator is hidden and missing note UI is shown
        verify<NoteDetailContract.View>(mNoteDetailView).setProgressIndicator(false)
        verify<NoteDetailContract.View>(mNoteDetailView).showMissingNote()
    }

    companion object {

        val INVALID_ID = "INVALID_ID"

        val TITLE_TEST = "title"

        val DESCRIPTION_TEST = "description"
    }
}
