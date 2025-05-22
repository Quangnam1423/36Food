package Manager.Restaurant.mai.config;


import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class DataInitializer {    // Helper method to create categories for a restaurant
    private void createCategoriesForRestaurant(Restaurant restaurant, List<String> categoryNames, CategoryRepository categoryRepo) {
        // Add category names directly to the restaurant's categories collection
        if (restaurant.getCategories() == null) {
            restaurant.setCategories(new ArrayList<>());
        }
        restaurant.getCategories().addAll(categoryNames);
    }
    
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
            CartItemRepository itemRepo,
            CategoryRepository categoryRepo
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
        List<Restaurant> restaurants = new ArrayList<>();
        
        // Restaurant 1: Quán Ăn Mai
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setName("Quán Ăn Mai");
        restaurant1.setImageUrl("https://example.com/image1.jpg");
        restaurant1.setRating(4.5f);
        restaurant1.setRatingCount(120);
        restaurant1.setPriceRange("50k-200k");
        restaurant1.setOpeningStatus("OPEN");
        restaurant1.setBusinessHours("08:00-22:00");
        restaurant1.setPhoneNumber("0988888888");
        restaurant1.setLikes(1200);
        restaurant1.setReviewsCount(200);
        restaurant1.setDistance(0.0);
        restaurant1.setCreatedAt(System.currentTimeMillis());
        restaurant1.setLatitude(21.028511);
        restaurant1.setLongitude(105.804817);
        restaurant1.setMenuItems(new ArrayList<>());
        restaurant1.setCategories(new ArrayList<>());
        restaurants.add(restaurant1);
        
        // Restaurant 2: Bún Chả Hà Thành
        Restaurant restaurant2 = new Restaurant();
        restaurant2.setName("Bún Chả Hà Thành");
        restaurant2.setImageUrl("https://example.com/image2.jpg");
        restaurant2.setRating(4.2f);
        restaurant2.setRatingCount(90);
        restaurant2.setPriceRange("40k-100k");
        restaurant2.setOpeningStatus("OPEN");
        restaurant2.setBusinessHours("09:00-21:00");
        restaurant2.setPhoneNumber("0999999999");
        restaurant2.setLikes(980);
        restaurant2.setReviewsCount(140);
        restaurant2.setDistance(0.0);
        restaurant2.setCreatedAt(System.currentTimeMillis());
        restaurant2.setLatitude(21.033333);
        restaurant2.setLongitude(105.850000);
        restaurant2.setMenuItems(new ArrayList<>());
        restaurant2.setCategories(new ArrayList<>());
        restaurants.add(restaurant2);
        
        // Restaurant 3: Lẩu Thái Tomyum
        Restaurant restaurant3 = new Restaurant();
        restaurant3.setName("Lẩu Thái Tomyum");
        restaurant3.setImageUrl("https://example.com/image3.jpg");
        restaurant3.setRating(4.7f);
        restaurant3.setRatingCount(150);
        restaurant3.setPriceRange("100k-300k");
        restaurant3.setOpeningStatus("OPEN");
        restaurant3.setBusinessHours("10:00-23:00");
        restaurant3.setPhoneNumber("0888888888");
        restaurant3.setLikes(1600);
        restaurant3.setReviewsCount(300);
        restaurant3.setDistance(0.0);
        restaurant3.setCreatedAt(System.currentTimeMillis());
        restaurant3.setLatitude(21.030000);
        restaurant3.setLongitude(105.830000);
        restaurant3.setMenuItems(new ArrayList<>());
        restaurant3.setCategories(new ArrayList<>());
        restaurants.add(restaurant3);
        
        // Restaurant 4: Bánh Mì Minh Nhật
        Restaurant restaurant4 = new Restaurant();
        restaurant4.setName("Bánh Mì Minh Nhật");
        restaurant4.setImageUrl("https://example.com/image4.jpg");
        restaurant4.setRating(4.8f);
        restaurant4.setRatingCount(200);
        restaurant4.setPriceRange("20k-50k");
        restaurant4.setOpeningStatus("OPEN");
        restaurant4.setBusinessHours("06:00-20:00");
        restaurant4.setPhoneNumber("0866666666");
        restaurant4.setLikes(800);
        restaurant4.setReviewsCount(120);
        restaurant4.setDistance(0.0);
        restaurant4.setCreatedAt(System.currentTimeMillis());
        restaurant4.setLatitude(21.025000);
        restaurant4.setLongitude(105.840000);
        restaurant4.setMenuItems(new ArrayList<>());
        restaurant4.setCategories(new ArrayList<>());
        restaurants.add(restaurant4);
        
        // Restaurant 5: Sushi Hokkaido
        Restaurant restaurant5 = new Restaurant();
        restaurant5.setName("Sushi Hokkaido");
        restaurant5.setImageUrl("https://example.com/image5.jpg");
        restaurant5.setRating(4.9f);
        restaurant5.setRatingCount(180);
        restaurant5.setPriceRange("200k-500k");
        restaurant5.setOpeningStatus("OPEN");
        restaurant5.setBusinessHours("11:00-22:00");
        restaurant5.setPhoneNumber("0877777777");
        restaurant5.setLikes(1800);
        restaurant5.setReviewsCount(350);
        restaurant5.setDistance(0.0);
        restaurant5.setCreatedAt(System.currentTimeMillis());
        restaurant5.setLatitude(21.040000);
        restaurant5.setLongitude(105.845000);
        restaurant5.setMenuItems(new ArrayList<>());
        restaurant5.setCategories(new ArrayList<>());
        restaurants.add(restaurant5);
        
        // Restaurant 6: Phở Thìn Bờ Hồ
        Restaurant restaurant6 = new Restaurant();
        restaurant6.setName("Phở Thìn Bờ Hồ");
        restaurant6.setImageUrl("https://example.com/image6.jpg");
        restaurant6.setRating(4.6f);
        restaurant6.setRatingCount(350);
        restaurant6.setPriceRange("50k-120k");
        restaurant6.setOpeningStatus("OPEN");
        restaurant6.setBusinessHours("06:00-22:00");
        restaurant6.setPhoneNumber("0955555555");
        restaurant6.setLikes(1200);
        restaurant6.setReviewsCount(180);
        restaurant6.setDistance(0.0);
        restaurant6.setCreatedAt(System.currentTimeMillis());
        restaurant6.setLatitude(21.036389);
        restaurant6.setLongitude(105.852222);
        restaurant6.setMenuItems(new ArrayList<>());
        restaurant6.setCategories(new ArrayList<>());
        restaurants.add(restaurant6);
        
        // Restaurant 7: Cơm Tấm Sài Gòn
        Restaurant restaurant7 = new Restaurant();
        restaurant7.setName("Cơm Tấm Sài Gòn");
        restaurant7.setImageUrl("https://example.com/image7.jpg");
        restaurant7.setRating(4.4f);
        restaurant7.setRatingCount(280);
        restaurant7.setPriceRange("60k-150k");
        restaurant7.setOpeningStatus("OPEN");
        restaurant7.setBusinessHours("10:00-21:00");
        restaurant7.setPhoneNumber("0933333333");
        restaurant7.setLikes(950);
        restaurant7.setReviewsCount(160);
        restaurant7.setDistance(0.0);
        restaurant7.setCreatedAt(System.currentTimeMillis());
        restaurant7.setLatitude(21.022222);
        restaurant7.setLongitude(105.831111);
        restaurant7.setMenuItems(new ArrayList<>());
        restaurant7.setCategories(new ArrayList<>());
        restaurants.add(restaurant7);
        
        // Restaurant 8: Pizza Express
        Restaurant restaurant8 = new Restaurant();
        restaurant8.setName("Pizza Express");
        restaurant8.setImageUrl("https://example.com/image8.jpg");
        restaurant8.setRating(4.5f);
        restaurant8.setRatingCount(210);
        restaurant8.setPriceRange("150k-350k");
        restaurant8.setOpeningStatus("OPEN");
        restaurant8.setBusinessHours("10:00-22:30");
        restaurant8.setPhoneNumber("0944444444");
        restaurant8.setLikes(1500);
        restaurant8.setReviewsCount(220);
        restaurant8.setDistance(0.0);
        restaurant8.setCreatedAt(System.currentTimeMillis());
        restaurant8.setLatitude(21.014444);
        restaurant8.setLongitude(105.823889);
        restaurant8.setMenuItems(new ArrayList<>());
        restaurant8.setCategories(new ArrayList<>());
        restaurants.add(restaurant8);
        
        // Restaurant 9: Quán Nướng Hàn Quốc
        Restaurant restaurant9 = new Restaurant();
        restaurant9.setName("Quán Nướng Hàn Quốc");
        restaurant9.setImageUrl("https://example.com/image9.jpg");
        restaurant9.setRating(4.7f);
        restaurant9.setRatingCount(180);
        restaurant9.setPriceRange("200k-500k");
        restaurant9.setOpeningStatus("OPEN");
        restaurant9.setBusinessHours("16:00-23:00");
        restaurant9.setPhoneNumber("0911111111");
        restaurant9.setLikes(1650);
        restaurant9.setReviewsCount(280);
        restaurant9.setDistance(0.0);
        restaurant9.setCreatedAt(System.currentTimeMillis());
        restaurant9.setLatitude(21.046111);
        restaurant9.setLongitude(105.833333);
        restaurant9.setMenuItems(new ArrayList<>());
        restaurant9.setCategories(new ArrayList<>());
        restaurants.add(restaurant9);
          // Restaurant 10: Bún Đậu Mắm Tôm Hà Nội
        Restaurant restaurant10 = new Restaurant();
        restaurant10.setName("Bún Đậu Mắm Tôm Hà Nội");
        restaurant10.setImageUrl("https://example.com/image10.jpg");
        restaurant10.setRating(4.3f);
        restaurant10.setRatingCount(320);
        restaurant10.setPriceRange("50k-120k");
        restaurant10.setOpeningStatus("OPEN");
        restaurant10.setBusinessHours("10:00-20:00");
        restaurant10.setPhoneNumber("0922222222");
        restaurant10.setLikes(850);
        restaurant10.setReviewsCount(150);
        restaurant10.setDistance(0.0);
        restaurant10.setCreatedAt(System.currentTimeMillis());
        restaurant10.setLatitude(21.029167);
        restaurant10.setLongitude(105.847778);
        restaurant10.setMenuItems(new ArrayList<>());
        restaurant10.setCategories(new ArrayList<>());
        restaurants.add(restaurant10);
        
        // Thêm các nhà hàng khu vực Hà Đông
        
        // Restaurant 11: Phở Hà Đông
        Restaurant restaurant11 = new Restaurant();
        restaurant11.setName("Phở Hà Đông");
        restaurant11.setImageUrl("https://example.com/pho-ha-dong.jpg");
        restaurant11.setRating(4.7f);
        restaurant11.setRatingCount(250);
        restaurant11.setPriceRange("40k-80k");
        restaurant11.setOpeningStatus("OPEN");
        restaurant11.setBusinessHours("06:00-22:00");
        restaurant11.setPhoneNumber("0966888777");
        restaurant11.setLikes(950);
        restaurant11.setReviewsCount(180);
        restaurant11.setDistance(0.0);
        restaurant11.setCreatedAt(System.currentTimeMillis());
        restaurant11.setLatitude(20.982342);  // Tọa độ khu vực Hà Đông
        restaurant11.setLongitude(105.787498);
        restaurant11.setMenuItems(new ArrayList<>());
        restaurant11.setCategories(new ArrayList<>());
        restaurants.add(restaurant11);
        
        // Restaurant 12: Bún Cá Hà Đông
        Restaurant restaurant12 = new Restaurant();
        restaurant12.setName("Bún Cá Ngon Hà Đông");
        restaurant12.setImageUrl("https://example.com/bun-ca-ha-dong.jpg");
        restaurant12.setRating(4.5f);
        restaurant12.setRatingCount(180);
        restaurant12.setPriceRange("45k-90k");
        restaurant12.setOpeningStatus("OPEN");
        restaurant12.setBusinessHours("07:00-21:00");
        restaurant12.setPhoneNumber("0977123456");
        restaurant12.setLikes(780);
        restaurant12.setReviewsCount(150);
        restaurant12.setDistance(0.0);
        restaurant12.setCreatedAt(System.currentTimeMillis());
        restaurant12.setLatitude(20.980129);  // Tọa độ khác ở Hà Đông
        restaurant12.setLongitude(105.775642);
        restaurant12.setMenuItems(new ArrayList<>());
        restaurant12.setCategories(new ArrayList<>());
        restaurants.add(restaurant12);
        
        // Restaurant 13: Cháo Ngon Hà Đông
        Restaurant restaurant13 = new Restaurant();
        restaurant13.setName("Cháo Ngon Hà Đông");
        restaurant13.setImageUrl("https://example.com/chao-ha-dong.jpg");
        restaurant13.setRating(4.6f);
        restaurant13.setRatingCount(160);
        restaurant13.setPriceRange("30k-60k");
        restaurant13.setOpeningStatus("OPEN");
        restaurant13.setBusinessHours("06:00-21:00");
        restaurant13.setPhoneNumber("0988777666");
        restaurant13.setLikes(680);
        restaurant13.setReviewsCount(130);
        restaurant13.setDistance(0.0);
        restaurant13.setCreatedAt(System.currentTimeMillis());
        restaurant13.setLatitude(20.974567);  // Tọa độ khác ở Hà Đông
        restaurant13.setLongitude(105.789321);
        restaurant13.setMenuItems(new ArrayList<>());
        restaurant13.setCategories(new ArrayList<>());
        restaurants.add(restaurant13);
        
        // Restaurant 14: Chè Ngon Hà Đông
        Restaurant restaurant14 = new Restaurant();
        restaurant14.setName("Chè Ngon Hà Đông");
        restaurant14.setImageUrl("https://example.com/che-ha-dong.jpg");
        restaurant14.setRating(4.8f);
        restaurant14.setRatingCount(220);
        restaurant14.setPriceRange("20k-50k");
        restaurant14.setOpeningStatus("OPEN");
        restaurant14.setBusinessHours("09:00-22:00");
        restaurant14.setPhoneNumber("0933444555");
        restaurant14.setLikes(1100);
        restaurant14.setReviewsCount(210);
        restaurant14.setDistance(0.0);
        restaurant14.setCreatedAt(System.currentTimeMillis());
        restaurant14.setLatitude(20.969876);  // Tọa độ khác ở Hà Đông
        restaurant14.setLongitude(105.782134);
        restaurant14.setMenuItems(new ArrayList<>());
        restaurant14.setCategories(new ArrayList<>());
        restaurants.add(restaurant14);
        
        restaurantRepo.saveAll(restaurants);
        
        // --- CATEGORIES ---
        // Create categories for each restaurant
        // Restaurant 0: Quán Ăn Mai
        createCategoriesForRestaurant(restaurants.get(0), List.of("Món chính", "Món phụ", "Đồ uống", "Tráng miệng"), categoryRepo);
        
        // Restaurant 1: Bún Chả Hà Thành  
        createCategoriesForRestaurant(restaurants.get(1), List.of("Bún chả", "Món nướng", "Món nước", "Đồ uống"), categoryRepo);
        
        // Restaurant 2: Lẩu Thái Tomyum
        createCategoriesForRestaurant(restaurants.get(2), List.of("Lẩu", "Món hấp", "Đồ uống", "Ăn vặt"), categoryRepo);
        
        // Restaurant 3: Bánh Mì Minh Nhật
        createCategoriesForRestaurant(restaurants.get(3), List.of("Bánh mì", "Đồ uống", "Bánh ngọt"), categoryRepo);
        
        // Restaurant 4: Sushi Hokkaido
        createCategoriesForRestaurant(restaurants.get(4), List.of("Sushi", "Sashimi", "Món nướng", "Đồ uống"), categoryRepo);
        
        // Restaurant 5: Phở Thìn Bờ Hồ
        createCategoriesForRestaurant(restaurants.get(5), List.of("Phở bò", "Phở gà", "Món thêm", "Đồ uống"), categoryRepo);
        
        // Restaurant 6: Cơm Tấm Sài Gòn
        createCategoriesForRestaurant(restaurants.get(6), List.of("Cơm tấm", "Món thêm", "Đồ uống", "Tráng miệng"), categoryRepo);
        
        // Restaurant 7: Pizza Express
        createCategoriesForRestaurant(restaurants.get(7), List.of("Pizza", "Mỳ Ý", "Salad", "Đồ uống"), categoryRepo);
        
        // Restaurant 8: Quán Nướng Hàn Quốc
        createCategoriesForRestaurant(restaurants.get(8), List.of("Món nướng", "Lẩu", "Món ăn kèm", "Đồ uống"), categoryRepo);
          // Restaurant 9: Bún Đậu Mắm Tôm
        createCategoriesForRestaurant(restaurants.get(9), List.of("Bún đậu", "Nem rán", "Đồ nguội", "Đồ uống"), categoryRepo);
        
        // Thêm categories cho các nhà hàng khu vực Hà Đông
        // Restaurant 10: Phở Hà Đông
        createCategoriesForRestaurant(restaurants.get(10), List.of("Phở bò", "Phở gà", "Phở vịt", "Đồ uống"), categoryRepo);
        
        // Restaurant 11: Bún Cá Hà Đông
        createCategoriesForRestaurant(restaurants.get(11), List.of("Bún cá", "Bún mọc", "Bún riêu", "Đồ uống"), categoryRepo);
        
        // Restaurant 12: Cháo Ngon Hà Đông
        createCategoriesForRestaurant(restaurants.get(12), List.of("Cháo gà", "Cháo tim cật", "Cháo sườn", "Đồ uống"), categoryRepo);
        
        // Restaurant 13: Chè Ngon Hà Đông
        createCategoriesForRestaurant(restaurants.get(13), List.of("Chè đỗ đen", "Chè bưởi", "Chè thập cẩm", "Đồ uống"), categoryRepo);
        
        // Save the restaurants with their categories
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
        voucherRepo.saveAll(vouchers);        // --- ORDER ---
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
        
        // Thêm các đơn hàng hoàn thành cho người dùng từ các nhà hàng mới thêm vào
        for (int u = 0; u < users.size(); u++) {
            // Thêm đơn hàng từ nhà hàng Phở
            Order phoOrder = new Order();
            phoOrder.setUser(users.get(u));
            phoOrder.setPayment(payments.get(u % payments.size()));
            phoOrder.setOrderDate(LocalDateTime.now().minusDays(u + 2));
            phoOrder.setTotalAmount(new BigDecimal("120000"));
            phoOrder.setOrderStatus("COMPLETED");
            phoOrder.setShippingAddress(addresses.get(u));
            phoOrder.setRestaurantId(String.valueOf(restaurants.get(10).getId()));
            phoOrder.setRestaurantAddress("Số 25 Quang Trung, Hà Đông, Hà Nội");
            phoOrder.setCustomerAddress(addresses.get(u).getAdr());
            phoOrder.setOrderCreatedAt(LocalDateTime.now().minusDays(u + 2));
            phoOrder.setOrderUpdatedAt(LocalDateTime.now().minusDays(u + 1));
            phoOrder.setDeleted(false);
            orders.add(phoOrder);
            
            // Thêm đơn hàng từ nhà hàng Chè
            Order cheOrder = new Order();
            cheOrder.setUser(users.get(u));
            cheOrder.setPayment(payments.get(u % payments.size()));
            cheOrder.setOrderDate(LocalDateTime.now().minusDays(u + 3));
            cheOrder.setTotalAmount(new BigDecimal("70000"));
            cheOrder.setOrderStatus("COMPLETED");
            cheOrder.setShippingAddress(addresses.get(u));
            cheOrder.setRestaurantId(String.valueOf(restaurants.get(13).getId()));
            cheOrder.setRestaurantAddress("Số 56 Nguyễn Viết Xuân, Hà Đông, Hà Nội");
            cheOrder.setCustomerAddress(addresses.get(u).getAdr());
            cheOrder.setOrderCreatedAt(LocalDateTime.now().minusDays(u + 3));
            cheOrder.setOrderUpdatedAt(LocalDateTime.now().minusDays(u + 2));
            cheOrder.setDeleted(false);
            orders.add(cheOrder);
        }
        
        orderRepo.saveAll(orders);


        // --- MENU ITEM ---
        List<MenuItem> menuItems = List.of(
                new MenuItem(restaurants.get(1), "Cơm gà xối mỡ", "Cơm gà giòn rụm với nước sốt đặc biệt", BigDecimal.valueOf(45000), "Món chính", "https://example.com/com-ga.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(2), "Trà đào cam sả", "Trà đào tươi mát, thơm mùi sả và cam", BigDecimal.valueOf(30000), "Đồ uống", "https://example.com/tra-dao.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(1), "Bún chả Hà Nội", "Thịt nướng kèm bún, rau sống", BigDecimal.valueOf(40000), "Món chính", "https://example.com/bun-cha.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(2), "Matcha Latte", "Trà xanh Nhật Bản, thơm mùi trà xanh và sữa tươi", BigDecimal.valueOf(30000), "Đồ uống", "https://example.com/tra-dao.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(0), "Phở bò tái", "Phở bò truyền thống, đậm đà", BigDecimal.valueOf(50000), "Món chính", "https://example.com/pho-bo.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(0), "Sinh tố bơ", "Sinh tố bơ tươi, ngọt mát", BigDecimal.valueOf(35000), "Đồ uống", "https://example.com/sinh-to-bo.jpg", LocalDateTime.now(), LocalDateTime.now()),
                // Món ăn cho nhà hàng Bánh Mì Minh Nhật (restaurants.get(3))
                new MenuItem(restaurants.get(3), "Bánh mì thịt nguội", "Bánh mì với thịt nguội, pate, rau thơm", BigDecimal.valueOf(25000), "Món chính", "https://example.com/banh-mi-thit.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(3), "Bánh mì gà", "Bánh mì với thịt gà xé, sốt mayonnaise", BigDecimal.valueOf(28000), "Món chính", "https://example.com/banh-mi-ga.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(3), "Cà phê sữa đá", "Cà phê đen đá với sữa đặc", BigDecimal.valueOf(18000), "Đồ uống", "https://example.com/cafe-sua.jpg", LocalDateTime.now(), LocalDateTime.now()),
                
                // Món ăn cho nhà hàng Sushi Hokkaido (restaurants.get(4))
                new MenuItem(restaurants.get(4), "Sushi cá hồi", "Cơm cuộn với cá hồi tươi", BigDecimal.valueOf(120000), "Món chính", "https://example.com/sushi-ca-hoi.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(4), "Sashimi mix", "Đĩa sashimi tổng hợp gồm cá hồi, cá ngừ, bạch tuộc", BigDecimal.valueOf(250000), "Món chính", "https://example.com/sashimi.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(4), "Sake nóng", "Rượu gạo Nhật Bản", BigDecimal.valueOf(80000), "Đồ uống", "https://example.com/sake.jpg", LocalDateTime.now(), LocalDateTime.now()),
                
                // Món ăn cho nhà hàng Phở Thìn Bờ Hồ (restaurants.get(5))
                new MenuItem(restaurants.get(5), "Phở bò đặc biệt", "Phở với các loại thịt bò cao cấp", BigDecimal.valueOf(75000), "Món chính", "https://example.com/pho-dac-biet.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(5), "Phở gà", "Phở nước dùng gà truyền thống", BigDecimal.valueOf(65000), "Món chính", "https://example.com/pho-ga.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(5), "Chanh muối", "Nước chanh muối giải khát", BigDecimal.valueOf(20000), "Đồ uống", "https://example.com/chanh-muoi.jpg", LocalDateTime.now(), LocalDateTime.now()),
                
                // Món ăn cho nhà hàng Cơm Tấm Sài Gòn (restaurants.get(6))
                new MenuItem(restaurants.get(6), "Cơm tấm sườn bì chả", "Cơm tấm với sườn nướng, bì heo và chả trứng", BigDecimal.valueOf(70000), "Món chính", "https://example.com/com-tam.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(6), "Cơm tấm gà nướng", "Cơm tấm với gà nướng sốt đặc biệt", BigDecimal.valueOf(65000), "Món chính", "https://example.com/com-tam-ga.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(6), "Trà đá", "Trà đá miễn phí", BigDecimal.valueOf(0), "Đồ uống", "https://example.com/tra-da.jpg", LocalDateTime.now(), LocalDateTime.now()),
                
                // Món ăn cho nhà hàng Pizza Express (restaurants.get(7))
                new MenuItem(restaurants.get(7), "Pizza hải sản", "Pizza với tôm, mực, sò điệp", BigDecimal.valueOf(220000), "Món chính", "https://example.com/pizza-seafood.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(7), "Pizza thịt nguội nấm", "Pizza với thịt nguội, nấm và phô mai", BigDecimal.valueOf(190000), "Món chính", "https://example.com/pizza-ham.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(7), "Coca Cola", "Nước ngọt có gas", BigDecimal.valueOf(25000), "Đồ uống", "https://example.com/coca.jpg", LocalDateTime.now(), LocalDateTime.now()),
                  // Món ăn cho nhà hàng Quán Nướng Hàn Quốc (restaurants.get(8))
                new MenuItem(restaurants.get(8), "Set nướng 2 người", "Set nướng gồm thịt bò, thịt heo, kim chi, kèm rau và đồ ăn kèm", BigDecimal.valueOf(350000), "Món chính", "https://example.com/korean-bbq.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(8), "Lẩu kim chi", "Lẩu cay kiểu Hàn Quốc với kim chi, thịt bò, hải sản", BigDecimal.valueOf(280000), "Món chính", "https://example.com/kimchi-hotpot.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(8), "Soju", "Rượu gạo Hàn Quốc", BigDecimal.valueOf(100000), "Đồ uống", "https://example.com/soju.jpg", LocalDateTime.now(), LocalDateTime.now()),
                
                // Món ăn cho nhà hàng Bún Đậu Mắm Tôm (restaurants.get(9))
                new MenuItem(restaurants.get(9), "Set bún đậu 2 người", "Bún, đậu hũ chiên, chả cốm, thịt luộc, kèm rau sống", BigDecimal.valueOf(120000), "Món chính", "https://example.com/bun-dau.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(9), "Nem rán", "Nem rán giòn với nhân thịt, nấm, miến", BigDecimal.valueOf(40000), "Món khai vị", "https://example.com/nem-ran.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(9), "Trà đá chanh", "Trà đá với chanh tươi", BigDecimal.valueOf(15000), "Đồ uống", "https://example.com/tra-chanh.jpg", LocalDateTime.now(), LocalDateTime.now()),
                
                // Món ăn cho Phở Hà Đông (restaurants.get(10))
                new MenuItem(restaurants.get(10), "Phở bò phổ thông", "Phở bò truyền thống với thịt bò tái", BigDecimal.valueOf(50000), "Phở bò", "https://example.com/pho-bo-thuong.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(10), "Phở bò đặc biệt", "Phở bò với đủ loại thịt: tái, nạm, gầu, sách", BigDecimal.valueOf(70000), "Phở bò", "https://example.com/pho-bo-dac-biet.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(10), "Phở gà phổ thông", "Phở với thịt gà ta", BigDecimal.valueOf(45000), "Phở gà", "https://example.com/pho-ga-thuong.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(10), "Phở gà đặc biệt", "Phở với thịt gà ta nguyên con", BigDecimal.valueOf(65000), "Phở gà", "https://example.com/pho-ga-dac-biet.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(10), "Phở vịt quay", "Phở với thịt vịt quay", BigDecimal.valueOf(60000), "Phở vịt", "https://example.com/pho-vit-quay.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(10), "Trà đá", "Trà đá miễn phí", BigDecimal.valueOf(0), "Đồ uống", "https://example.com/tra-da.jpg", LocalDateTime.now(), LocalDateTime.now()),
                
                // Món ăn cho Bún Cá Hà Đông (restaurants.get(11))
                new MenuItem(restaurants.get(11), "Bún cá rô đồng", "Bún với cá rô đồng tươi", BigDecimal.valueOf(55000), "Bún cá", "https://example.com/bun-ca-ro.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(11), "Bún cá chép", "Bún với cá chép tươi", BigDecimal.valueOf(60000), "Bún cá", "https://example.com/bun-ca-chep.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(11), "Bún mọc", "Bún với chả mọc thịt lợn", BigDecimal.valueOf(50000), "Bún mọc", "https://example.com/bun-moc.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(11), "Bún riêu cua", "Bún với gạch cua, đậu phụ rán", BigDecimal.valueOf(55000), "Bún riêu", "https://example.com/bun-rieu.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(11), "Chanh muối", "Nước chanh muối giải khát", BigDecimal.valueOf(15000), "Đồ uống", "https://example.com/chanh-muoi.jpg", LocalDateTime.now(), LocalDateTime.now()),
                
                // Món ăn cho Cháo Ngon Hà Đông (restaurants.get(12))
                new MenuItem(restaurants.get(12), "Cháo gà phổ thông", "Cháo nấu với thịt gà ta", BigDecimal.valueOf(40000), "Cháo gà", "https://example.com/chao-ga-thuong.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(12), "Cháo gà đặc biệt", "Cháo với gà ta nguyên con", BigDecimal.valueOf(60000), "Cháo gà", "https://example.com/chao-ga-dac-biet.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(12), "Cháo tim cật", "Cháo với tim, gan, cật lợn", BigDecimal.valueOf(45000), "Cháo tim cật", "https://example.com/chao-tim-cat.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(12), "Cháo sườn", "Cháo với sườn non", BigDecimal.valueOf(50000), "Cháo sườn", "https://example.com/chao-suon.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(12), "Nước sấu", "Nước sấu đặc trưng Hà Nội", BigDecimal.valueOf(15000), "Đồ uống", "https://example.com/nuoc-sau.jpg", LocalDateTime.now(), LocalDateTime.now()),
                
                // Món ăn cho Chè Ngon Hà Đông (restaurants.get(13))
                new MenuItem(restaurants.get(13), "Chè đỗ đen size nhỏ", "Chè đỗ đen với nước cốt dừa", BigDecimal.valueOf(20000), "Chè đỗ đen", "https://example.com/che-do-den-nho.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(13), "Chè đỗ đen size lớn", "Chè đỗ đen với nước cốt dừa, phần lớn", BigDecimal.valueOf(30000), "Chè đỗ đen", "https://example.com/che-do-den-lon.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(13), "Chè bưởi size nhỏ", "Chè bưởi thanh mát", BigDecimal.valueOf(25000), "Chè bưởi", "https://example.com/che-buoi-nho.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(13), "Chè bưởi size lớn", "Chè bưởi thanh mát, phần lớn", BigDecimal.valueOf(35000), "Chè bưởi", "https://example.com/che-buoi-lon.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(13), "Chè thập cẩm size nhỏ", "Chè với đủ loại topping", BigDecimal.valueOf(25000), "Chè thập cẩm", "https://example.com/che-thap-cam-nho.jpg", LocalDateTime.now(), LocalDateTime.now()),
                new MenuItem(restaurants.get(13), "Chè thập cẩm size lớn", "Chè với đủ loại topping, phần lớn", BigDecimal.valueOf(40000), "Chè thập cẩm", "https://example.com/che-thap-cam-lon.jpg", LocalDateTime.now(), LocalDateTime.now())
        );
        menuItemRepo.saveAll(menuItems);        // --- REVIEW ---
        List<Review> reviews = List.of(
                Review.builder()
                        .user(users.get(1))
                        .food(menuItems.get(0))
                        .restaurant(menuItems.get(0).getRestaurant())
                        .order(orders.get(0))
                        .content("Ngon, giá hợp lý")
                        .rating(4.5f)
                        .imageUrls(List.of("https://example.com/images/review1.jpg"))
                        .createdAt(LocalDateTime.now())
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
                        .createdAt(LocalDateTime.now())
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
                        .createdAt(LocalDateTime.now())
                        .isAnonymous(false)
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
                        .createdAt(LocalDateTime.now())
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
                        .createdAt(LocalDateTime.now())
                        .isAnonymous(false)
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
                        .createdAt(LocalDateTime.now())
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
                        .createdAt(LocalDateTime.now())
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
                        .createdAt(LocalDateTime.now())
                        .isAnonymous(false)
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
                        .createdAt(LocalDateTime.now())
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
                        .createdAt(LocalDateTime.now())
                        .isAnonymous(false)
                        .isDeleted(false)
                        .build(),
                
                // Thêm đánh giá cho các món ăn của Phở Hà Đông
                Review.builder()
                        .user(users.get(0))
                        .food(menuItems.get(27))  // Phở bò phổ thông
                        .restaurant(menuItems.get(27).getRestaurant())
                        .order(orders.get(12))
                        .content("Phở bò ngon, nước dùng trong và ngọt thanh")
                        .rating(4.7f)
                        .imageUrls(List.of("https://example.com/images/review-pho-bo.jpg"))
                        .createdAt(LocalDateTime.now().minusDays(2))
                        .isAnonymous(false)
                        .isDeleted(false)
                        .build(),
                        
                Review.builder()
                        .user(users.get(1))
                        .food(menuItems.get(28))  // Phở bò đặc biệt
                        .restaurant(menuItems.get(28).getRestaurant())
                        .order(orders.get(16))
                        .content("Phở bò đặc biệt thịt nhiều, đầy đặn, ngon!")
                        .rating(4.9f)
                        .imageUrls(List.of("https://example.com/images/review-pho-bo-dac-biet.jpg"))
                        .createdAt(LocalDateTime.now().minusDays(3))
                        .isAnonymous(false)
                        .isDeleted(false)
                        .build(),
                        
                Review.builder()
                        .user(users.get(2))
                        .food(menuItems.get(29))  // Phở gà phổ thông
                        .restaurant(menuItems.get(29).getRestaurant())
                        .order(orders.get(18))
                        .content("Phở gà thịt mềm, nước dùng nhạt")
                        .rating(3.5f)
                        .imageUrls(List.of())
                        .createdAt(LocalDateTime.now().minusDays(4))
                        .isAnonymous(false)
                        .isDeleted(false)
                        .build(),
                          Review.builder()
                        .user(users.get(3))
                        .food(menuItems.get(31))  // Phở vịt quay
                        .restaurant(menuItems.get(31).getRestaurant())
                        .order(orders.get(19))  // Changed from 20 to 19 (valid index)
                        .content("Phở vịt quay vịt giòn thơm, nước dùng đậm đà")
                        .rating(4.8f)
                        .imageUrls(List.of("https://example.com/images/review-pho-vit.jpg"))
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .isAnonymous(false)
                        .isDeleted(false)
                        .build(),
                
                // Thêm đánh giá cho các món ăn của Chè Ngon Hà Đông
                Review.builder()
                        .user(users.get(0))
                        .food(menuItems.get(43))  // Chè đỗ đen size nhỏ
                        .restaurant(menuItems.get(43).getRestaurant())
                        .order(orders.get(13))
                        .content("Chè đỗ đen ngọt vừa, thơm mùi dừa")
                        .rating(4.6f)
                        .imageUrls(List.of("https://example.com/images/review-che-do-den.jpg"))
                        .createdAt(LocalDateTime.now().minusDays(3))
                        .isAnonymous(false)
                        .isDeleted(false)
                        .build(),
                        
                Review.builder()
                        .user(users.get(1))
                        .food(menuItems.get(45))  // Chè bưởi size nhỏ
                        .restaurant(menuItems.get(45).getRestaurant())
                        .order(orders.get(17))
                        .content("Chè bưởi thơm mát, bưởi tươi ngon")
                        .rating(4.8f)
                        .imageUrls(List.of("https://example.com/images/review-che-buoi.jpg"))
                        .createdAt(LocalDateTime.now().minusDays(4))
                        .isAnonymous(false)
                        .isDeleted(false)
                        .build(),
                        
                Review.builder()
                        .user(users.get(2))
                        .food(menuItems.get(47))  // Chè thập cẩm size nhỏ
                        .restaurant(menuItems.get(47).getRestaurant())
                        .order(orders.get(19))
                        .content("Chè thập cẩm đầy đặn, nhiều topping")
                        .rating(4.7f)
                        .imageUrls(List.of("https://example.com/images/review-che-thap-cam.jpg"))
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .isAnonymous(false)
                        .isDeleted(false)
                        .build(),                Review.builder()
                        .user(users.get(3))
                        .food(menuItems.get(47))  // Chè thập cẩm size lớn (changed from 48 to 47)
                        .restaurant(menuItems.get(47).getRestaurant())
                        .order(orders.get(18))  // Changed from 21 to 18 (valid index)
                        .content("Chè thập cẩm size lớn nhiều quá không ăn hết, nhưng rất ngon!")
                        .rating(4.5f)
                        .imageUrls(List.of("https://example.com/images/review-che-thap-cam-lon.jpg"))
                        .createdAt(LocalDateTime.now().minusDays(6))
                        .isAnonymous(false)
                        .isDeleted(false)
                        .build(),
                        
                // Thêm đánh giá cho các món ăn của Bún Cá Hà Đông
                Review.builder()
                        .user(users.get(0))
                        .food(menuItems.get(33))  // Bún cá rô đồng
                        .restaurant(menuItems.get(33).getRestaurant())
                        .content("Bún cá rô đồng thơm ngon, cá tươi")
                        .rating(4.5f)
                        .imageUrls(List.of())
                        .createdAt(LocalDateTime.now().minusDays(7))
                        .isAnonymous(false)
                        .isDeleted(false)
                        .build(),
                        
                Review.builder()
                        .user(users.get(1))
                        .food(menuItems.get(35))  // Bún mọc
                        .restaurant(menuItems.get(35).getRestaurant())
                        .content("Bún mọc chả mềm, nước dùng ngọt, rất ngon")
                        .rating(4.8f)
                        .imageUrls(List.of("https://example.com/images/review-bun-moc.jpg"))
                        .createdAt(LocalDateTime.now().minusDays(8))
                        .isAnonymous(false)
                        .isDeleted(false)
                        .build(),
                
                // Thêm đánh giá cho các món ăn của Cháo Ngon Hà Đông
                Review.builder()
                        .user(users.get(2))
                        .food(menuItems.get(38))  // Cháo gà phổ thông
                        .restaurant(menuItems.get(38).getRestaurant())
                        .content("Cháo gà nhạt, gà ít")
                        .rating(3.2f)
                        .imageUrls(List.of())
                        .createdAt(LocalDateTime.now().minusDays(9))
                        .isAnonymous(false)
                        .isDeleted(false)
                        .build(),
                        
                Review.builder()
                        .user(users.get(3))
                        .food(menuItems.get(40))  // Cháo tim cật
                        .restaurant(menuItems.get(40).getRestaurant())
                        .content("Cháo tim cật đầy đặn, lòng tươi ngon")
                        .rating(4.7f)
                        .imageUrls(List.of("https://example.com/images/review-chao-tim-cat.jpg"))
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .isAnonymous(false)
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