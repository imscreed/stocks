package com.qapital.stocks.search.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qapital.stocks.ui.theme.StocksTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SearchScreenTest {
    
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @Test
    fun searchScreen_displaysSearchBar() {
        composeTestRule.setContent {
            StocksTheme {
                SearchScreen()
            }
        }
        
        composeTestRule.onNodeWithTag("search_field").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search stocks by symbol or name").assertIsDisplayed()
    }
    
    @Test
    fun searchScreen_displaysTitle() {
        composeTestRule.setContent {
            StocksTheme {
                SearchScreen()
            }
        }
        
        composeTestRule.onNodeWithText("Qapital stocks").assertIsDisplayed()
    }
    
    @Test
    fun searchScreen_canEnterSearchQuery() {
        composeTestRule.setContent {
            StocksTheme {
                SearchScreen()
            }
        }
        
        composeTestRule.onNodeWithTag("search_field")
            .performTextInput("AAPL")
        
        composeTestRule.onNodeWithText("AAPL").assertIsDisplayed()
    }
    
    @Test
    fun searchScreen_showsLoadingInitially() {
        composeTestRule.setContent {
            StocksTheme {
                SearchScreen()
            }
        }
        
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.loading_stocks)
        ).assertIsDisplayed()
    }
    
    @Test
    fun searchScreen_canClearSearch() {
        composeTestRule.setContent {
            StocksTheme {
                SearchScreen()
            }
        }
        
        composeTestRule.onNodeWithTag("search_field")
            .performTextInput("AAPL")
        
        composeTestRule.onNodeWithText("Clear search")
            .performClick()
        
        composeTestRule.onNodeWithText("AAPL").assertDoesNotExist()
    }
} 