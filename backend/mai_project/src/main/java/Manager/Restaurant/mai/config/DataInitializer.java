package Manager.Restaurant.mai.config;


import Manager.Restaurant.mai.entity.*;
import Manager.Restaurant.mai.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


/*
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
            ReviewRepository reviewRepo
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
                    new Restaurant("Quán Ăn Mai", "contact@quanmai.com", "res123", "0988888888", "ACTIVE", true, "[\"MANAGER\"]"),
                    new Restaurant("Bún Chả Hà Thành", "buncha@hathanh.vn", "buncha123", "0999999999", "ACTIVE", true, "[\"MANAGER\"]"),
                    new Restaurant("Lẩu Thái Tomyum", "tomyum@lauthai.com", "thai123", "0888888888", "ACTIVE", true, "[\"MANAGER\"]")
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

                orderRepo.save(newOrder);
            }

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
                    new Review(null, users.get(1), menuItems.get(0), "Ngon, giá hợp lý", LocalDateTime.now()),
                    new Review(null, users.get(2), menuItems.get(1), "Đồ uống mát, thơm mùi cam", LocalDateTime.now()),
                    new Review(null, users.get(3), menuItems.get(2), "Bún chả chuẩn vị Hà Nội", LocalDateTime.now()),
                    new Review(null, users.get(1), menuItems.get(3), "Phở ngon, nước dùng ngọt", LocalDateTime.now()),
                    new Review(null, users.get(2), menuItems.get(4), "Bánh mì giòn, pate béo", LocalDateTime.now()),
                    new Review(null, users.get(0), menuItems.get(5), "Sinh tố bơ tuyệt vời!", LocalDateTime.now()),
                    new Review(null, users.get(2), menuItems.get(0), "Cơm gà xối mỡ ngon!", LocalDateTime.now()),
                    new Review(null, users.get(3), menuItems.get(1), "Trà đào mát mẻ, hơi ngọt", LocalDateTime.now()),
                    new Review(null, users.get(1), menuItems.get(5), "Sinh tố bơ hơi đặc", LocalDateTime.now()),
                    new Review(null, users.get(3), menuItems.get(3), "Phở bò đậm đà, thơm ngon", LocalDateTime.now())
            );
            reviewRepo.saveAll(reviews);


            System.out.println("✅ Dữ liệu mẫu đầy đủ đã được khởi tạo.");
        };
    }
}

 */