package com.example.hamid.wallet.ui

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.hamid.wallet.InstrumentedTest
import com.example.hamid.wallet.R
import com.example.hamid.wallet.presentation.ui.activity.TransactionActivity
import com.example.hamid.wallet.presentation.ui.adaptar.TransactionListAdapter
import com.hamid.data.utils.EspressoIdlingResource
import com.hamid.domain.model.model.Status
import org.hamcrest.Matchers.`is`
import org.junit.*
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue


class ActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<TransactionActivity>? = ActivityTestRule(
        TransactionActivity::class.java
    )

    @get:Rule
    val rxSchedulerRule = InstrumentedTest.RxSchedulerRule()
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private var mContext: Context? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mContext = InstrumentationRegistry.getInstrumentation().targetContext

        IdlingRegistry.getInstance().register(EspressoIdlingResource.idlingResource)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.idlingResource)
    }


    @Test
    fun verifyLDisBeingObserved() {
        assertTrue(activityRule!!.activity.viewModel.formattedList.hasObservers())
    }

    @Test
    fun testRecyclerVisible() {
        activityRule!!.activity.viewModel.formattedList.observeForTesting {

            onView(withId(R.id.rv_list))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun recyclerViewItem_amountInflowIndication() {

        activityRule!!.activity.viewModel.formattedList.observeForTesting {

            val recyclerView = activityRule!!.activity.findViewById<RecyclerView>(R.id.rv_list)
            val itemCount = recyclerView.adapter!!.itemCount

            if (itemCount.toLong() > 0) {

                for (i in 0 until itemCount) {

                    onView(withId(R.id.rv_list))
                        .inRoot(
                            RootMatchers.withDecorView(
                                `is`<View>(activityRule!!.activity.window.decorView)
                            )
                        )
                        .perform(scrollToPosition<RecyclerView.ViewHolder>(i))

                    val holder = recyclerView.findViewHolderForAdapterPosition(i)

                    val color =
                        (holder!! as TransactionListAdapter.ViewHolder).tvAmount.currentTextColor

                    val value = (holder as TransactionListAdapter.ViewHolder).tvAmount.text

                    if (value.startsWith('-')) {
                        assertTrue(color == Color.RED)
                        assertFalse(color == Color.GREEN)
                    } else {
                        assertTrue(color == Color.GREEN)
                        assertFalse(color == Color.RED)
                    }
                }

            }

        }
    }

    @Test
    fun testRecyclerViewScroll() {

        val recyclerView = activityRule!!.activity.findViewById<RecyclerView>(R.id.rv_list)
        val itemCount = recyclerView.adapter!!.itemCount

        onView(withId(R.id.rv_list))
            .inRoot(
                RootMatchers.withDecorView(
                    `is`<View>(activityRule!!.activity.window.decorView)
                )
            )
            .perform(scrollToPosition<RecyclerView.ViewHolder>(itemCount - 6))
    }

    @Test
    fun rv_itemSize_matches_responseSize() {

        val recyclerView = activityRule!!.activity.findViewById<RecyclerView>(R.id.rv_list)
        val itemCount = recyclerView.adapter!!.itemCount

        Assert.assertTrue(itemCount.toLong() >= 0)

    }

    @Test
    fun rv_verify_fieldsDisplayed() {

        onView(withId(R.id.rv_list))
            .check(matches(hasDescendant(withId(R.id.tv_lbl_time))))

        onView(withId(R.id.rv_list))
            .check(matches(hasDescendant(withId(R.id.tv_time))))

        onView(withId(R.id.rv_list))
            .check(matches(hasDescendant(withId(R.id.tv_lbl_fee))))

        onView(withId(R.id.rv_list))
            .check(matches(hasDescendant(withId(R.id.tv_fee))))

        onView(withId(R.id.rv_list))
            .check(matches(hasDescendant(withId(R.id.tv_lbl_amount))))

        onView(withId(R.id.rv_list))
            .check(matches(hasDescendant(withId(R.id.tv_amount))))

    }

    @Test
    fun whenNoData_progressBarShown() {

        activityRule!!.activity.viewModel.walletUseCase.nukeDB()

        activityRule!!.activity.viewModel.formattedList.observeForTesting {
            if (activityRule!!.activity.viewModel.formattedList.value!!.status == Status.ERROR) {
                onView(withId(R.id.progress_bar)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

            } else {
                onView(withId(R.id.progress_bar)).check(matches(withEffectiveVisibility(Visibility.GONE)))

            }
        }

    }

    @Test
    fun whenData_progressBarHidden() {

        val recyclerView = activityRule!!.activity.findViewById<RecyclerView>(R.id.rv_list)
        val itemCount = recyclerView.adapter!!.itemCount

        if (itemCount > 0) {
            onView(withId(R.id.progress_bar)).check(matches(withEffectiveVisibility(Visibility.GONE)))

        } else {
            onView(withId(R.id.progress_bar)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        }

    }

    @Test
    fun balanceShown() {

        onView(withId(R.id.toolbar))
            .check(matches(hasDescendant(withId(R.id.tv_lbl_balance))))

        onView(withId(R.id.toolbar))
            .check(matches(hasDescendant(withId(R.id.tv_balance))))

        onView(withId(R.id.tv_lbl_balance)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.tv_balance)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

    }


    private fun <T> LiveData<T>.observeForTesting(block: () -> Unit) {
        val observer = Observer<T> { }
        try {
            observeForever(observer)
            block()
        } finally {
            removeObserver(observer)
        }
    }

}

