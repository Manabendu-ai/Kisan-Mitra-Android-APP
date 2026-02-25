package com.riku.kisanmitra.navigation

sealed class Screen(val route: String) {
    object LanguageSelection : Screen("language_selection")
    object RoleSelection : Screen("role_selection")
    object Login : Screen("login/{role}") {
        fun createRoute(role: String) = "login/$role"
    }
    object Register : Screen("register/{role}") {
        fun createRoute(role: String) = "register/$role"
    }

    // Farmer
    object FarmerDashboard : Screen("farmer_dashboard")
    object CreateListing : Screen("create_listing")
    object FarmerOrderStatus : Screen("farmer_order_status")
    object TransportBooking : Screen("transport_booking")
    object FarmerEarnings : Screen("farmer_earnings")

    // Buyer
    object BuyerDashboard : Screen("buyer_dashboard")
    object BrowseListings : Screen("browse_listings")
    object ListingDetail : Screen("listing_detail/{listingId}") {
        fun createRoute(listingId: String) = "listing_detail/$listingId"
    }
    object BuyerOrderTracking : Screen("buyer_order_tracking")

    // Driver
    object DriverDashboard : Screen("driver_dashboard")
    object ActiveTrip : Screen("active_trip")
    object DriverEarnings : Screen("driver_earnings")

    // Trader
    object TraderDashboard : Screen("trader_dashboard")
}
