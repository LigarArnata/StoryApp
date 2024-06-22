package com.dicoding.picodiploma.loginwithanimation.viewModel

import androidx.paging.PagingSource
import androidx.paging.PagingState
import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import androidx.paging.AsyncPagingDataDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.picodiploma.loginwithanimation.DataDummy
import MainDispatcherRule
import androidx.lifecycle.LiveData
import com.dicoding.picodiploma.loginwithanimation.Api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.Constant
import com.dicoding.picodiploma.loginwithanimation.data.Response.DetailStory
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.sharedPreference.SharedPreferenceHelper
import com.dicoding.picodiploma.loginwithanimation.getOrAwaitValue
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MyViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var sharedPreferenceHelper: SharedPreferenceHelper

    private lateinit var myViewModel: MyViewModel

    @Before
    fun setUp() {
        Mockito.`when`(sharedPreferenceHelper.getToken(Constant.TOKEN_KEY)).thenReturn("test_token")

        myViewModel = MyViewModel(storyRepository, sharedPreferenceHelper)
    }

    @Test
    fun `when Get Quote Should Not Null and Return Data`() = runTest {
        val dummyQuote = DataDummy.generateDummyStoryResponse()
        val data: PagingData<DetailStory> = StoryPagingSource.snapshot(dummyQuote)
        val expectedQuote = MutableLiveData<PagingData<DetailStory>>()
        expectedQuote.value = data

        Mockito.`when`(storyRepository.getStories("test_token")).thenReturn(expectedQuote)

        val actualQuote: PagingData<DetailStory> = myViewModel.myDataFlow.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = object : DiffUtil.ItemCallback<DetailStory>() {
                override fun areItemsTheSame(oldItem: DetailStory, newItem: DetailStory): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: DetailStory, newItem: DetailStory): Boolean {
                    return oldItem == newItem
                }
            },
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Main
        )

        advanceUntilIdle()
        differ.submitData(actualQuote)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyQuote.size, differ.snapshot().size)
        Assert.assertEquals(dummyQuote[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Quote Empty Should Return No Data`() = runTest {
        val data: PagingData<DetailStory> = PagingData.from(emptyList())
        val expectedQuote = MutableLiveData<PagingData<DetailStory>>()
        expectedQuote.value = data

        Mockito.`when`(storyRepository.getStories("test_token")).thenReturn(expectedQuote)

        val actualQuote: PagingData<DetailStory> = myViewModel.myDataFlow.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = TestDiffCallback<DetailStory>(),
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Main
        )

        differ.submitData(actualQuote)

        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, DetailStory>() {
    companion object {
        fun snapshot(items: List<DetailStory>): PagingData<DetailStory> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DetailStory>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DetailStory> {
        return LoadResult.Page(emptyList(), null, null)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

class TestDiffCallback<T : Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}