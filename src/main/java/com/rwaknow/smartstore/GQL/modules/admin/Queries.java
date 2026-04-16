package com.rwaknow.smartstore.GQL.modules.admin;

import com.rwaknow.smartstore.GQL.modules.admin.Types.*;
import com.rwaknow.smartstore.model.Order;
import com.rwaknow.smartstore.model.OrderStatus;
import com.rwaknow.smartstore.model.Product;
import com.rwaknow.smartstore.model.User;
import com.rwaknow.smartstore.model.UserRole;
import com.rwaknow.smartstore.repository.OrderRepository;
import com.rwaknow.smartstore.repository.ProductRepository;
import com.rwaknow.smartstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin dashboard queries - analytics, stats, user management
 * All queries require ADMIN role
 */
@Controller("adminQueries")
@RequiredArgsConstructor
public class Queries {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    // ==== DASHBOARD OVERVIEW ================================================

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public DashboardStats adminDashboard() {
        long totalCustomers = userRepository.countByRole(UserRole.CUSTOMER);
        long totalOrders = orderRepository.count();

        double totalRevenue = orderRepository.findAll().stream()
                .mapToDouble(Order::getTotal)
                .sum();

        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long lowStockProducts = productRepository.countByStockLessThan(10);

        // Recent activity
        List<Order> recentOrders = orderRepository.findTop5ByOrderByCreatedAtDesc();
        List<User> recentCustomers = userRepository.findTop5ByOrderByCreatedAtDesc();
        List<Product> recentProducts = productRepository.findTop5ByOrderByCreatedAtDesc();

        RecentActivity recentActivity = new RecentActivity(
                recentOrders,
                recentCustomers,
                recentProducts
        );

        return new DashboardStats(
                totalCustomers,
                totalOrders,
                totalRevenue,
                pendingOrders,
                lowStockProducts,
                recentActivity
        );
    }

    // ==== REVENUE ANALYTICS ================================================

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RevenueAnalytics revenueAnalytics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = now.minusDays(7);
        LocalDateTime startOfMonth = now.minusDays(30);
        LocalDateTime startOfYear = now.minusDays(365);

        double todayRevenue = calculateRevenueBetween(startOfDay, now);
        double weekRevenue = calculateRevenueBetween(startOfWeek, now);
        double monthRevenue = calculateRevenueBetween(startOfMonth, now);
        double yearRevenue = calculateRevenueBetween(startOfYear, now);

        // Daily breakdown for last 7 days
        List<DailyRevenue> dailyBreakdown = calculateDailyRevenue(7);

        return new RevenueAnalytics(
                todayRevenue,
                weekRevenue,
                monthRevenue,
                yearRevenue,
                dailyBreakdown
        );
    }

    // ==== CUSTOMER ANALYTICS ================================================

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerAnalytics customerAnalytics() {
        long totalCustomers = userRepository.countByRole(UserRole.CUSTOMER);
        long activeCustomers = userRepository.countByRoleAndActive(UserRole.CUSTOMER, true);

        LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
        long newCustomersThisMonth = userRepository.countByRoleAndCreatedAtAfter(
                UserRole.CUSTOMER,
                monthAgo
        );

        double totalRevenue = orderRepository.findAll().stream()
                .mapToDouble(Order::getTotal)
                .sum();
        long orderCount = orderRepository.count();
        double averageOrderValue = orderCount > 0 ? totalRevenue / orderCount : 0;

        // Find top customer by total spent
        User topCustomer = findTopCustomer();

        return new CustomerAnalytics(
                totalCustomers,
                activeCustomers,
                newCustomersThisMonth,
                averageOrderValue,
                topCustomer
        );
    }

    // ==== ORDER ANALYTICS ================================================

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public OrderAnalytics orderAnalytics() {
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long completedOrders = orderRepository.countByStatus(OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);

        double conversionRate = totalOrders > 0
                ? (double) completedOrders / totalOrders * 100
                : 0;

        return new OrderAnalytics(
                totalOrders,
                pendingOrders,
                completedOrders,
                cancelledOrders,
                conversionRate
        );
    }

    // ==== STOCK ALERTS ================================================

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<StockAlert> stockAlerts() {
        List<Product> lowStockProducts = productRepository.findByStockLessThan(10);

        return lowStockProducts.stream()
                .map(product -> {
                    String alertLevel;
                    if (product.getStock() == 0) {
                        alertLevel = "OUT_OF_STOCK";
                    } else if (product.getStock() < 5) {
                        alertLevel = "CRITICAL";
                    } else {
                        alertLevel = "LOW";
                    }
                    return new StockAlert(product, product.getStock(), alertLevel);
                })
                .collect(Collectors.toList());
    }

    // ==== USER MANAGEMENT ================================================

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserPaginatedResult allUsers(
            @Argument Integer page,
            @Argument Integer size) {

        Page<User> result = userRepository.findAll(
                PageRequest.of(
                        page != null ? page : 0,
                        size != null ? size : 20,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                )
        );

        return new UserPaginatedResult(
                result.getContent(),
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber()
        );
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User userById(@Argument Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ==== HELPER METHODS ================================================

    private double calculateRevenueBetween(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByCreatedAtBetween(start, end).stream()
                .mapToDouble(Order::getTotal)
                .sum();
    }

    private List<DailyRevenue> calculateDailyRevenue(int days) {
        LocalDate today = LocalDate.now();
        return java.util.stream.IntStream.range(0, days)
                .mapToObj(i -> {
                    LocalDate date = today.minusDays(days - 1 - i);
                    LocalDateTime startOfDay = date.atStartOfDay();
                    LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

                    List<Order> ordersForDay = orderRepository.findByCreatedAtBetween(startOfDay, endOfDay);
                    double revenue = ordersForDay.stream().mapToDouble(Order::getTotal).sum();
                    long orderCount = ordersForDay.size();

                    return new DailyRevenue(date.toString(), revenue, orderCount);
                })
                .collect(Collectors.toList());
    }

    private User findTopCustomer() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.CUSTOMER)
                .max((u1, u2) -> {
                    double u1Total = orderRepository.findByUserId(u1.getId()).stream()
                            .mapToDouble(Order::getTotal).sum();
                    double u2Total = orderRepository.findByUserId(u2.getId()).stream()
                            .mapToDouble(Order::getTotal).sum();
                    return Double.compare(u1Total, u2Total);
                })
                .orElse(null);
    }
}