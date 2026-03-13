package com.rwaknow.smartstore.config;

import com.rwaknow.smartstore.model.Category;
import com.rwaknow.smartstore.model.User;
import com.rwaknow.smartstore.model.UserRole;
import com.rwaknow.smartstore.repository.CategoryRepository;
import com.rwaknow.smartstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        loadAdminUser();
        loadCategories();
    }

    private void loadAdminUser() {
        if (!userRepository.existsByEmail("admin@smartstore.com")) {
            User admin = new User();
            admin.setEmail("admin@smartstore.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(UserRole.ADMIN);
            admin.setActive(true);
            admin.setCreatedAt(LocalDateTime.now());

            userRepository.save(admin);
            log.info("✅ Admin user created: admin@smartstore.com / Admin@123");
        } else {
            log.info("ℹ️ Admin user already exists");
        }
    }

    private void loadCategories() {
        if (categoryRepository.count() == 0) {
            List<Category> categories = List.of(
                    createCategory("Electronics", "Phones, laptops, tablets, and electronic accessories"),
                    createCategory("Clothing", "Men's and women's fashion, shoes, and accessories"),
                    createCategory("Home & Kitchen", "Furniture, appliances, cookware, and home decor"),
                    createCategory("Books", "Fiction, non-fiction, educational, and children's books"),
                    createCategory("Sports & Outdoors", "Sports equipment, outdoor gear, and fitness accessories"),
                    createCategory("Beauty & Health", "Skincare, makeup, health supplements, and wellness products"),
                    createCategory("Toys & Games", "Kids toys, board games, puzzles, and educational toys"),
                    createCategory("Automotive", "Car accessories, parts, tools, and maintenance products")
            );

            categoryRepository.saveAll(categories);
            log.info("✅ {} categories created", categories.size());
        } else {
            log.info("ℹ️ Categories already exist");
        }
    }

    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }
}