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

package com.example.android.testing.notes.data

import com.example.android.testing.notes.alias._is
import com.example.android.testing.notes.data.NotesServiceApi.NotesServiceCallback
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Matchers.any
import org.mockito.Matchers.eq
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
class InMemoryNotesRepositoryTest {

    private val NOTE_TITLE = "title"

    private val NOTES = arrayListOf(Note("Title1", "Description1"),
            Note("Title2", "Description2"))

    lateinit private var mNotesRepository: InMemoryNotesRepository

    @Mock
    lateinit private var mServiceApi: NotesServiceApiImpl

    @Mock
    lateinit private var mGetNoteCallback: NotesRepository.GetNoteCallback

    @Mock
    lateinit private var mLoadNotesCallback : NotesRepository.LoadNotesCallback

    /**
     * [ArgumentCaptor] is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    lateinit var mNotesServiceCallbackCaptor: ArgumentCaptor<NotesServiceCallback<List<Note>>>

    @Before
    fun setupNotesRepository() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        mNotesRepository = InMemoryNotesRepository(mServiceApi)
    }

    @Test
    fun getNotes_repositoryCachesAfterFirstApiCall() {
        // Given a setup Captor to capture callbacks
        // When two calls are issued to the notes repository
        twoLoadCallsToRepository(mLoadNotesCallback)

        // Then notes where only requested once from Service API
        verify(mServiceApi).getAllNotes(any(NotesServiceCallback::class.java))
    }

    @Test
    fun invalidateCache_DoesNotCallTheServiceApi() {
        // Given a setup Captor to capture callbacks
        twoLoadCallsToRepository(mLoadNotesCallback)

        // When data refresh is requested
        mNotesRepository.refreshData()
        mNotesRepository.getNotes(mLoadNotesCallback) // Third call to API

        // The notes where requested twice from the Service API (Caching on first and third call)
        verify(mServiceApi, times(2)).getAllNotes(any(NotesServiceCallback::class.java))
    }

    @Test
    fun getNotes_requestsAllNotesFromServiceApi() {
        // When notes are requested from the notes repository
        mNotesRepository.getNotes(mLoadNotesCallback)

        // Then notes are loaded from the service API
        verify(mServiceApi).getAllNotes(any(NotesServiceCallback::class.java))
    }

    @Test
    fun saveNote_savesNoteToServiceAPIAndInvalidatesCache() {
        // Given a stub note with title and description
        val newNote = Note(NOTE_TITLE, "Some Note Description")

        // When a note is saved to the notes repository
        mNotesRepository.saveNote(newNote)

        // Then the notes cache is cleared
        assertThat(mNotesRepository.mCachedNotes, _is(nullValue()))
    }

    @Test
    fun getNote_requestsSingleNoteFromServiceApi() {
        // When a note is requested from the notes repository
        mNotesRepository.getNote(NOTE_TITLE, mGetNoteCallback)

        // Then the note is loaded from the service API
        verify(mServiceApi).getNote(eq(NOTE_TITLE), any(NotesServiceCallback::class.java))
    }

    /**
     * Convenience method that issues two calls to the notes repository
     */
    private fun twoLoadCallsToRepository(callback: NotesRepository.LoadNotesCallback) {
        // When notes are requested from repository
        mNotesRepository.getNotes(callback) // First call to API

        // Use the Mockito Captor to capture the callback
        verify(mServiceApi).getAllNotes(mNotesServiceCallbackCaptor.capture())

        // Trigger callback so notes are cached
        mNotesServiceCallbackCaptor.value.onLoaded(NOTES)

        mNotesRepository.getNotes(callback) // Second call to API
    }

}