package com.rwaknow.smartstore.GQL.modules.admin;

import com.rwaknow.smartstore.model.Order;
import com.rwaknow.smartstore.model.Product;
import com.rwaknow.smartstore.model.User;
import java.util.List;

/**
 * Admin dashboard types - analytics, stats, and admin-only data
 */
public class Types {

    /**
     * Dashboard overview stats
     */
    public record DashboardStats(
            long totalCustomers,
            long totalOrders,
            double totalRevenue,
            long pendingOrders,
            long lowStockProducts,
            RecentActivity recentActivity
    ) {}

    /**
     * Recent activity for dashboard
     */
    public record RecentActivity(
            List<Order> recentOrders,
            List<User> recentCustomers,
            List<Product> recentProducts
    ) {}

    /**
     * Revenue analytics by time period
     */
    public record RevenueAnalytics(
            double todayRevenue,
            double weekRevenue,
            double monthRevenue,
            double yearRevenue,
            List<DailyRevenue> dailyBreakdown
    ) {}

    /**
     * Daily revenue data point
     */
    public record DailyRevenue(
            String date,
            double revenue,
            long orderCount
    ) {}

    /**
     * Product stock alert
     */
    public record StockAlert(
            Product product,
            int currentStock,
            String alertLevel  // LOW, CRITICAL, OUT_OF_STOCK
    ) {}

    /**
     * Customer analytics
     */
    public record CustomerAnalytics(
            long totalCustomers,
            long activeCustomers,
            long newCustomersThisMonth,
            double averageOrderValue,
            User topCustomer
    ) {}

    /**
     * Order analytics
     */
    public record OrderAnalytics(
            long totalOrders,
            long pendingOrders,
            long completedOrders,
            long cancelledOrders,
            double conversionRate
    ) {}

    /**
     * User management - paginated user list
     */
    public record UserPaginatedResult(
            List<User> items,
            int totalItems,
            int totalPages,
            int currentPage
    ) {}
}