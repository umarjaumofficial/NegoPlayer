package com.negoplayer.viewmodel

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.negoplayer.data.model.MediaItem
import com.negoplayer.data.model.MediaUiState
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class VideoViewModelTest {

    private lateinit var viewModel: VideoViewModel
    private lateinit var app: Application

    @Before
    fun setup() {
        app = ApplicationProvider.getApplicationContext()
        viewModel = VideoViewModel(app)
    }

    @Test
    fun testViewModelInitialization() {
        // Verify initial states
        assertEquals(SortOrder.DATE_DESC, viewModel.sortOrder.value)
    }

    @Test
    fun testSortOrderChange() {
        // Test changing sort order
        viewModel.setSortOrder(SortOrder.NAME_ASC)
        assertEquals(SortOrder.NAME_ASC, viewModel.sortOrder.value)

        viewModel.setSortOrder(SortOrder.SIZE_DESC)
        assertEquals(SortOrder.SIZE_DESC, viewModel.sortOrder.value)
    }

    @Test
    fun testSearchQuery() {
        // Test search functionality
        viewModel.search("test")
        assertEquals("test", viewModel.searchQuery.value)

        viewModel.search("")
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun testEmptySearchResults() {
        // Empty search should clear results
        viewModel.search("")
        assertTrue(viewModel.searchResults.value.isEmpty())
    }
}
