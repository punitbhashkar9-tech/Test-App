package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.BottomNavTab
import com.example.ui.theme.PrimaryBlue

@Composable
fun BottomNavBar(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("main_bottom_nav"),
        containerColor = Color.White
    ) {
        NavigationBarItem(
            selected = selectedTab == BottomNavTab.HOME,
            onClick = { onTabSelected(BottomNavTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == BottomNavTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    "Home",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == BottomNavTab.HOME) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                indicatorColor = Color(0xFFDBEAFE)
            ),
            modifier = Modifier.testTag("nav_item_home")
        )

        NavigationBarItem(
            selected = selectedTab == BottomNavTab.TESTS,
            onClick = { onTabSelected(BottomNavTab.TESTS) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == BottomNavTab.TESTS) Icons.Filled.Quiz else Icons.Outlined.Quiz,
                    contentDescription = "Tests"
                )
            },
            label = {
                Text(
                    "Tests",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == BottomNavTab.TESTS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                indicatorColor = Color(0xFFDBEAFE)
            ),
            modifier = Modifier.testTag("nav_item_tests")
        )

        NavigationBarItem(
            selected = selectedTab == BottomNavTab.PDF_STORE,
            onClick = { onTabSelected(BottomNavTab.PDF_STORE) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == BottomNavTab.PDF_STORE) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                    contentDescription = "PDF Store"
                )
            },
            label = {
                Text(
                    "PDF Store",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == BottomNavTab.PDF_STORE) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                indicatorColor = Color(0xFFDBEAFE)
            ),
            modifier = Modifier.testTag("nav_item_pdf_store")
        )

        NavigationBarItem(
            selected = selectedTab == BottomNavTab.MY_PURCHASES,
            onClick = { onTabSelected(BottomNavTab.MY_PURCHASES) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == BottomNavTab.MY_PURCHASES) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                    contentDescription = "My Purchases"
                )
            },
            label = {
                Text(
                    "Purchases",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == BottomNavTab.MY_PURCHASES) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                indicatorColor = Color(0xFFDBEAFE)
            ),
            modifier = Modifier.testTag("nav_item_purchases")
        )

        NavigationBarItem(
            selected = selectedTab == BottomNavTab.PROFILE,
            onClick = { onTabSelected(BottomNavTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == BottomNavTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = {
                Text(
                    "Profile",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == BottomNavTab.PROFILE) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                indicatorColor = Color(0xFFDBEAFE)
            ),
            modifier = Modifier.testTag("nav_item_profile")
        )
    }
}
