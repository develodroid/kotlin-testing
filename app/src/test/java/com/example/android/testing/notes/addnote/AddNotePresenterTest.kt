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

import com.example.android.testing.notes.data.Note
import com.example.android.testing.notes.data.NotesRepository
import com.example.android.testing.notes.alias._when
import com.example.android.testing.notes.util.ImageFile
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers.*
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.io.IOException

/**
 * Unit tests for the implementation of [AddNotePresenter].
 */
class AddNotePresenterTest {

    @Mock
    lateinit private var mNotesRepository: NotesRepository

    @Mock
    lateinit private var mImageFile: ImageFile

    @Mock
    lateinit private var mAddNoteView: AddNoteContract.View

    lateinit private var mAddNotesPresenter: AddNotePresenter

    @Before
    fun setupAddNotePresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        mAddNotesPresenter = AddNotePresenter(mNotesRepository, mAddNoteView, mImageFile)
    }

    @Test
    fun saveNoteToRepository_showsSuccessMessageUi() {
        // When the presenter is asked to save a note
        mAddNotesPresenter.saveNote("New Note Title", "Some Note Description")

        // Then a note is,
        verify(mNotesRepository).saveNote(any<Note>(Note::class.java)) // saved to the model
        verify(mAddNoteView).showNotesList() // shown in the UI
    }

    @Test
    fun saveNote_emptyNoteShowsErrorUi() {
        // When the presenter is asked to save an empty note
        mAddNotesPresenter.saveNote("", "")

        // Then an empty not error is shown in the UI
        verify(mAddNoteView).showEmptyNoteError()
    }

    @Test
    @Throws(IOException::class)
    fun takePicture_CreatesFileAndOpensCamera() {
        // When the presenter is asked to take an image
        mAddNotesPresenter.takePicture()

        // Then an image file is created snd camera is opened
        with(verify(mImageFile)){
            create(anyString(), anyString())
            path
        }

        verify(mAddNoteView).openCamera(anyString())
    }

    @Test
    fun imageAvailable_SavesImageAndUpdatesUiWithThumbnail() {
        // Given an a stubbed image file
        val imageUrl = "path/to/file"
        _when(mImageFile.exists()).thenReturn(true)
        _when(mImageFile.path).thenReturn(imageUrl)

        // When an image is made available to the presenter
        mAddNotesPresenter.imageAvailable()

        // Then the preview image of the stubbed image is shown in the UI
        verify(mAddNoteView).showImagePreview(contains(imageUrl))
    }

    @Test
    fun imageAvailable_FileDoesNotExistShowsErrorUi() {
        // Given the image file does not exist
        _when(mImageFile.exists()).thenReturn(false)

        // When an image is made available to the presenter
        mAddNotesPresenter.imageAvailable()

        // Then an error is shown in the UI and the image file is deleted
        verify(mAddNoteView).showImageError()
        verify(mImageFile).delete()
    }

    @Test
    fun noImageAvailable_ShowsErrorUi() {
        // When the presenter is notified that image capturing failed
        mAddNotesPresenter.imageCaptureFailed()

        // Then an error is shown in the UI and the image file is deleted
        verify(mAddNoteView).showImageError()
        verify(mImageFile).delete()
    }

}
