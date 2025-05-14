package Manager.Restaurant.mai.config;


import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            RoleRepository roleRepo,
            UserRepository userRepo,
            RestaurantRepository restaurantRepo,
            AddressRepository addressRepo,
            MenuItemRepository menuItemRepo,
            NotificationRepository notificationRepo,
            OrderRepository orderRepo,
            PaymentRepository paymentRepo,
            VoucherRepository voucherRepo,
            ReviewRepository reviewRepo,
            CartRepository cartRepo,
            CartItemRepository itemRepo
    ) {
            return args -> {
                // --- ROLE ---
                List<Role> roles = List.of(
                        new Role("Admin", "admin", "ACTIVE"),
                        new Role("User", "user", "ACTIVE"),
                        new Role("Deliver", "deliver", "ACTIVE")
                );
                roleRepo.saveAll(roles);

            // --- USER ---
            List<User> users = List.of(
                    new User("mai-quynh", "Mai Quỳnh", "mai@example.com", "hashedpassword", "randomsalt", "Nữ", "0912345678", "https://example.com/avatar.jpg", LocalDateTime.of(1998, 5, 20, 0, 0), roles.get(1), "ACTIVE"),
                    new User("tuan-anh", "Tuấn Anh", "tuan@example.com", "hashedpassword2", "salt2", "Nam", "0911222333", "https://example.com/avatar2.jpg", LocalDateTime.of(1995, 8, 15, 0, 0), roles.get(1), "ACTIVE"),
                    new User("thu-hien", "Thu Hiền", "hien@example.com", "hashedpassword3", "salt3", "Nữ", "0911000099", "https://example.com/avatar3.jpg", LocalDateTime.of(2000, 1, 5, 0, 0), roles.get(1), "ACTIVE"),
                    new User("minh-tri", "Minh Trí", "tri@example.com", "hashedpassword4", "salt4", "Nam", "0999888777", "https://example.com/avatar4.jpg", LocalDateTime.of(1992, 12, 12, 0, 0), roles.get(1), "ACTIVE")
            );
            userRepo.saveAll(users);

            // --- RESTAURANT ---
                List<Restaurant> restaurants = List.of(
                        new Restaurant(
                                null,
                                "Quán Ăn Mai",
                                "https://example.com/image1.jpg",
                                4.5f,
                                120,
                                "50k-200k",
                                "OPEN",
                                "08:00-22:00",
                                "DINE_IN",
                                "0988888888",
                                1200,
                                200,
                                0.0,
                                List.of("Vietnamese", "Seafood"),
                                System.currentTimeMillis(),
                                21.028511,
                                105.804817,
                                new ArrayList<>()
                        ),
                        new Restaurant(
                                null,
                                "Bún Chả Hà Thành",
                                "https://example.com/image2.jpg",
                                4.2f,
                                90,
                                "40k-100k",
                                "OPEN",
                                "09:00-21:00",
                                "DINE_IN",
                                "0999999999",
                                980,
                                140,
                                0.0,
                                List.of("Vietnamese", "Grill"),
                                System.currentTimeMillis(),
                                21.033333,
                                105.850000,
                                new ArrayList<>()
                        ),
                        new Restaurant(
                                null,
                                "Lẩu Thái Tomyum",
                                "https://example.com/image3.jpg",
                                4.7f,
                                150,
                                "100k-300k",
                                "OPEN",
                                "10:00-23:00",
                                "DINE_IN",
                                "0888888888",
                                1600,
                                300,
                                0.0,
                                List.of("Thai", "Hotpot"),
                                System.currentTimeMillis(),
                                21.030000,
                                105.830000,
                                new ArrayList<>()
                        )
                );
                restaurantRepo.saveAll(restaurants);


                // --- ADDRESS ---
            List<Address> addresses = List.of(
                    new Address("123 Lê Lợi, Quận 1, TP.HCM", users.get(0), LocalDateTime.now(), LocalDateTime.now()),
                    new Address("456 Trần Hưng Đạo, Đà Nẵng", users.get(1), LocalDateTime.now(), LocalDateTime.now()),
                    new Address("789 Nguyễn Trãi, Hà Nội", users.get(2), LocalDateTime.now(), LocalDateTime.now()),
                    new Address("321 Lý Thường Kiệt, Cần Thơ", users.get(3), LocalDateTime.now(), LocalDateTime.now())
            );
            addressRepo.saveAll(addresses);


            // --- NOTIFICATION ---
            List<Notification> notifications = List.of(
                    new Notification(null, users.get(0), "Bạn có đơn hàng mới!", LocalDateTime.now(), "UNREAD"),
                    new Notification(null, users.get(1), "Voucher mới đã được thêm!", LocalDateTime.now().minusDays(1), "READ"),
                    new Notification(null, users.get(2), "Nhà hàng mới mở gần bạn!", LocalDateTime.now().minusDays(2), "UNREAD"),
                    new Notification(null, users.get(3), "Bạn được tặng mã giảm giá!", LocalDateTime.now().minusDays(3), "UNREAD"),
                    new Notification(null, users.get(0), "Đơn hàng của bạn đã giao thành công", LocalDateTime.now().minusDays(4), "READ")
            );
            notificationRepo.saveAll(notifications);

            // --- PAYMENT ---
            List<Payment> payments = List.of(
                    new Payment(null, "MOMO", "SUCCESS", BigDecimal.valueOf(75000), LocalDateTime.now().minusDays(1), "PMT001", "MOMO"),
                    new Payment(null, "ZALOPAY", "FAILED", BigDecimal.valueOf(50000), LocalDateTime.now().minusDays(2), "PMT002", "ZALOPAY"),
                    new Payment(null, "CASH", "SUCCESS", BigDecimal.valueOf(120000), LocalDateTime.now().minusDays(3), "PMT003", "N/A"),
                    new Payment(null, "VNPAY", "SUCCESS", BigDecimal.valueOf(95000), LocalDateTime.now().minusDays(4), "PMT004", "VNPAY")
            );
            paymentRepo.saveAll(payments);

            // --- VOUCHER ---
            List<Voucher> vouchers = List.of(
                    new Voucher(null, "WELCOME10", 10.0f, LocalDateTime.now().plusDays(30), "Giảm 10% cho đơn đầu tiên"),
                    new Voucher(null, "FREESHIP", 0.0f, LocalDateTime.now().plusDays(15), "Miễn phí vận chuyển cho đơn trên 100k"),
                    new Voucher(null, "SUMMER20", 20.0f, LocalDateTime.now().plusDays(7), "Ưu đãi mùa hè -20%"),
                    new Voucher(null, "LUNCH15", 15.0f, LocalDateTime.now().plusDays(10), "Giảm 15% vào giờ trưa")
            );
            voucherRepo.saveAll(vouchers);

                // --- ORDER ---
                List<Order> orders = new ArrayList<>();

                for (int i = 0; i < 12; i++) {
                    int userIndex = i % users.size();

                    Order newOrder = new Order();
                    newOrder.setUser(users.get(userIndex));
                    newOrder.setPayment(payments.get(userIndex));
                    newOrder.setOrderDate(LocalDateTime.now().minusDays(i));
                    newOrder.setTotalAmount(new BigDecimal("50000").add(BigDecimal.valueOf(i * 1000)));
                    newOrder.setOrderStatus(i % 2 == 0 ? "DELIVERED" : "PENDING");
                    newOrder.setShippingAddress(addresses.get(userIndex));
                    newOrder.setOrderCreatedAt(LocalDateTime.now().minusDays(i));
                    newOrder.setOrderUpdatedAt(LocalDateTime.now().minusDays(i / 2));
                    if (i % 3 == 0 && userIndex < vouchers.size()) {
                        newOrder.setVoucher(vouchers.get(userIndex));
                    }
                    newOrder.setDeleted(false);

                    orders.add(newOrder);
                }

                orderRepo.saveAll(orders);


                // --- MENU ITEM ---
            List<MenuItem> menuItems = List.of(
                    new MenuItem(restaurants.get(1), "Cơm gà xối mỡ", "Cơm gà giòn rụm với nước sốt đặc biệt", BigDecimal.valueOf(45000), "Món chính", "https://example.com/com-ga.jpg", LocalDateTime.now(), LocalDateTime.now()),
                    new MenuItem(restaurants.get(2), "Trà đào cam sả", "Trà đào tươi mát, thơm mùi sả và cam", BigDecimal.valueOf(30000), "Đồ uống", "https://example.com/tra-dao.jpg", LocalDateTime.now(), LocalDateTime.now()),
                    new MenuItem(restaurants.get(1), "Bún chả Hà Nội", "Thịt nướng kèm bún, rau sống", BigDecimal.valueOf(40000), "Món chính", "https://example.com/bun-cha.jpg", LocalDateTime.now(), LocalDateTime.now()),
                    new MenuItem(restaurants.get(2), "Matcha Latte", "Trà xanh Nhật Bản, thơm mùi trà xanh và sữa tươi", BigDecimal.valueOf(30000), "Đồ uống", "https://example.com/tra-dao.jpg", LocalDateTime.now(), LocalDateTime.now()),
                    new MenuItem(restaurants.get(0), "Phở bò tái", "Phở bò truyền thống, đậm đà", BigDecimal.valueOf(50000), "Món chính", "https://example.com/pho-bo.jpg", LocalDateTime.now(), LocalDateTime.now()),
                    new MenuItem(restaurants.get(0), "Sinh tố bơ", "Sinh tố bơ tươi, ngọt mát", BigDecimal.valueOf(35000), "Đồ uống", "https://example.com/sinh-to-bo.jpg", LocalDateTime.now(), LocalDateTime.now())
            );
            menuItemRepo.saveAll(menuItems);


            // --- REVIEW ---
                List<Review> reviews = List.of(
                        Review.builder()
                                .user(users.get(1))
                                .food(menuItems.get(0))
                                .restaurant(menuItems.get(0).getRestaurant())
                                .order(orders.get(0))
                                .content("Ngon, giá hợp lý")
                                .rating(4.5f)
                                .imageUrls(List.of("https://example.com/images/review1.jpg"))
                                .createdAt(Instant.now())
                                .isAnonymous(false)
                                .isDeleted(false)
                                .build(),

                        Review.builder()
                                .user(users.get(2))
                                .food(menuItems.get(1))
                                .restaurant(menuItems.get(1).getRestaurant())
                                .order(orders.get(1))
                                .content("Đồ uống mát, thơm mùi cam")
                                .rating(4.0f)
                                .imageUrls(List.of("https://example.com/images/review2.jpg"))
                                .createdAt(Instant.now())
                                .isAnonymous(false)
                                .isDeleted(false)
                                .build(),

                        Review.builder()
                                .user(users.get(3))
                                .food(menuItems.get(2))
                                .restaurant(menuItems.get(2).getRestaurant())
                                .order(orders.get(2))
                                .content("Bún chả chuẩn vị Hà Nội")
                                .rating(5.0f)
                                .imageUrls(List.of())
                                .createdAt(Instant.now())
                                .isAnonymous(true)
                                .isDeleted(false)
                                .build(),

                        Review.builder()
                                .user(users.get(1))
                                .food(menuItems.get(3))
                                .restaurant(menuItems.get(3).getRestaurant())
                                .order(orders.get(3))
                                .content("Phở ngon, nước dùng ngọt")
                                .rating(4.2f)
                                .imageUrls(List.of("https://example.com/images/review4.jpg"))
                                .createdAt(Instant.now())
                                .isAnonymous(false)
                                .isDeleted(false)
                                .build(),

                        Review.builder()
                                .user(users.get(2))
                                .food(menuItems.get(4))
                                .restaurant(menuItems.get(4).getRestaurant())
                                .order(orders.get(4))
                                .content("Bánh mì giòn, pate béo")
                                .rating(4.8f)
                                .imageUrls(List.of())
                                .createdAt(Instant.now())
                                .isAnonymous(true)
                                .isDeleted(false)
                                .build(),
                        Review.builder()
                                .user(users.get(0))
                                .food(menuItems.get(5))
                                .restaurant(menuItems.get(5).getRestaurant())
                                .order(orders.get(5))
                                .content("Sinh tố bơ tuyệt vời! Rất mịn và không quá ngọt.")
                                .rating(4.7f)
                                .imageUrls(List.of("https://example.com/images/review6.jpg"))
                                .createdAt(Instant.now())
                                .isAnonymous(false)
                                .isDeleted(false)
                                .build(),

                        Review.builder()
                                .user(users.get(2))
                                .food(menuItems.get(0))
                                .restaurant(menuItems.get(0).getRestaurant())
                                .order(orders.get(6))
                                .content("Cơm gà xối mỡ giòn tan, gà mềm.")
                                .rating(4.3f)
                                .imageUrls(List.of())
                                .createdAt(Instant.now())
                                .isAnonymous(false)
                                .isDeleted(false)
                                .build(),

                        Review.builder()
                                .user(users.get(3))
                                .food(menuItems.get(1))
                                .restaurant(menuItems.get(1).getRestaurant())
                                .order(orders.get(7))
                                .content("Trà đào mát mẻ, nhưng hơi ngọt quá.")
                                .rating(3.8f)
                                .imageUrls(List.of("https://example.com/images/review8.jpg"))
                                .createdAt(Instant.now())
                                .isAnonymous(true)
                                .isDeleted(false)
                                .build(),

                        Review.builder()
                                .user(users.get(1))
                                .food(menuItems.get(5))
                                .restaurant(menuItems.get(5).getRestaurant())
                                .order(orders.get(8))
                                .content("Sinh tố bơ đặc quá, hơi khó uống.")
                                .rating(3.5f)
                                .imageUrls(List.of())
                                .createdAt(Instant.now())
                                .isAnonymous(false)
                                .isDeleted(false)
                                .build(),

                        Review.builder()
                                .user(users.get(3))
                                .food(menuItems.get(3))
                                .restaurant(menuItems.get(3).getRestaurant())
                                .order(orders.get(9))
                                .content("Phở bò đậm đà, nước dùng thơm và nóng.")
                                .rating(4.9f)
                                .imageUrls(List.of("https://example.com/images/review10.jpg"))
                                .createdAt(Instant.now())
                                .isAnonymous(true)
                                .isDeleted(false)
                                .build()
                );
                reviewRepo.saveAll(reviews);

                //Cart//

                List<Cart> carts = new ArrayList<>();
                for (int i = 0; i < users.size(); i++) {
                    User user = users.get(i);

                    // Kiểm tra nếu user đã có cart
                    Optional<Cart> existingCartOpt = cartRepo.findByUser_UserId(user.getUserId());
                    if (existingCartOpt.isPresent()) {
                        continue; // Bỏ qua user này nếu đã có cart
                    }

                    Cart cart = new Cart();
                    cart.setRestaurantId("restaurant-" + (i + 1));
                    cart.setUser(user);

                    List<CartItem> items = new ArrayList<>();
                    items.add(new CartItem(
                            UUID.randomUUID().toString(),
                            "Item A" + i,
                            10.0 + i,
                            1 + i,
                            "https://example.com/itemA.jpg",
                            "No onions",
                            cart
                    ));
                    items.add(new CartItem(
                            UUID.randomUUID().toString(),
                            "Item B" + i,
                            5.5 + i,
                            2,
                            "https://example.com/itemB.jpg",
                            "",
                            cart
                    ));

                    cart.setItems(items);
                    carts.add(cart);
                }
                cartRepo.saveAll(carts);



                System.out.println("✅ Dữ liệu mẫu đầy đủ đã được khởi tạo.");
        };
    }
}