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

import com.example.android.testing.notes.data.Note
import com.example.android.testing.notes.data.NotesRepository
import com.example.android.testing.notes.data.NotesRepository.LoadNotesCallback
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Matchers.any
import org.mockito.Mockito.verify

/**
 * Unit tests for the implementation of [NotesPresenter]
 */
class NotesPresenterTest {

    private val NOTES = arrayListOf(Note("Title1", "Description1"),
            Note("Title2", "Description2"))


    @Mock
    lateinit private var mNotesRepository: NotesRepository

    @Mock
    lateinit private var mNotesView: NotesContract.View

    /**
     * [ArgumentCaptor] is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    lateinit private var mLoadNotesCallbackCaptor: ArgumentCaptor<LoadNotesCallback>

    lateinit private var mNotesPresenter: NotesPresenter

    @Before
    fun setupNotesPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        mNotesPresenter = NotesPresenter(mNotesRepository, mNotesView)
    }

    @Test
    fun loadNotesFromRepositoryAndLoadIntoView() {
        // Given an initialized NotesPresenter with initialized notes
        // When loading of Notes is requested
        mNotesPresenter.loadNotes(true)

        // Callback is captured and invoked with stubbed notes
        verify(mNotesRepository).getNotes(mLoadNotesCallbackCaptor.capture())
        mLoadNotesCallbackCaptor.value.onNotesLoaded(NOTES)

        // Then progress indicator is hidden and notes are shown in UI
        val inOrder = Mockito.inOrder(mNotesView)
        with(inOrder.verify(mNotesView)){
            setProgressIndicator(true)
            setProgressIndicator(false)
            showNotes(NOTES)
        }

    }

    @Test
    fun clickOnFab_ShowsAddsNoteUi() {
        // When adding a new note
        mNotesPresenter.addNewNote()

        // Then add note UI is shown
        verify(mNotesView).showAddNote()
    }

    @Test
    fun clickOnNote_ShowsDetailUi() {
        // Given a stubbed note
        val requestedNote = Note("Details Requested", "For this note")

        // When open note details is requested
        mNotesPresenter.openNoteDetails(requestedNote)

        // Then note detail UI is shown
        verify(mNotesView).showNoteDetailUi(any(String::class.java))
    }

}
