import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// کلاس محصول
class Product {
    private String name;
    private double price;
    private String category;
    private int stock;

    public Product(String name, double price, String category, int stock) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock = stock;
    }

    // متدهای getter
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public int getStock() { return stock; }

    public void reduceStock(int quantity) {
        this.stock -= quantity;
    }

    @Override
    public String toString() {
        return String.format("%s - %.2f تومان (موجودی: %d)", name, price, stock);
    }
}

// کلاس سفارش
class Order {
    private String orderId;
    private List<Product> items;
    private double totalAmount;
    private LocalDateTime orderDate;
    private String status;

    public Order(List<Product> items, double totalAmount) {
        this.orderId = UUID.randomUUID().toString().substring(0, 8);
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.orderDate = LocalDateTime.now();
        this.status = "در حال پردازش";
    }

    // متدهای getter
    public String getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getOrderDate() { return orderDate; }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    public void displayOrder() {
        System.out.println("\n\u001B[36m=== سفارش #" + orderId + " ===");
        System.out.println("تاریخ: " + orderDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
        System.out.println("وضعیت: " + status);
        System.out.println("محصولات:");

        for (int i = 0; i < items.size(); i++) {
            System.out.println((i+1) + ". " + items.get(i).getName());
        }

        System.out.println("جمع کل: " + String.format("%.2f", totalAmount) + " تومان\u001B[0m");
    }
}

// کلاس سبد خرید
class ShoppingCart {
    private List<Product> items;
    private Map<Product, Integer> quantities;

    public ShoppingCart() {
        this.items = new ArrayList<>();
        this.quantities = new HashMap<>();
    }

    public void addItem(Product product, int quantity) {
        if (product.getStock() >= quantity) {
            items.add(product);
            quantities.put(product, quantities.getOrDefault(product, 0) + quantity);
            product.reduceStock(quantity);
            System.out.println("\u001B[32m" + quantity + " عدد " + product.getName() + " به سبد اضافه شد.\u001B[0m");
        } else {
            System.out.println("\u001B[31mموجودی کافی نیست! فقط " + product.getStock() + " عدد موجود است.\u001B[0m");
        }
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            Product removed = items.remove(index);
            int quantity = quantities.remove(removed);
            removed.reduceStock(-quantity); // بازگرداندن به موجودی
            System.out.println("\u001B[31m" + removed.getName() + " از سبد حذف شد.\u001B[0m");
        }
    }

    public double calculateTotal() {
        double total = 0;
        for (Map.Entry<Product, Integer> entry : quantities.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    public void displayCart() {
        if (items.isEmpty()) {
            System.out.println("\u001B[33mسبد خرید شما خالی است.\u001B[0m");
            return;
        }

        System.out.println("\n\u001B[36m=== سبد خرید شما ===");
        for (int i = 0; i < items.size(); i++) {
            Product p = items.get(i);
            int qty = quantities.get(p);
            System.out.println((i+1) + ". " + p.getName() + " (تعداد: " + qty + ") - " +
                    String.format("%.2f", p.getPrice() * qty) + " تومان");
        }
        System.out.println("\u001B[35mجمع کل: " + String.format("%.2f", calculateTotal()) + " تومان\u001B[0m");
    }

    public List<Product> getItems() {
        return new ArrayList<>(items);
    }

    public Map<Product, Integer> getQuantities() {
        return new HashMap<>(quantities);
    }

    public void clear() {
        for (Map.Entry<Product, Integer> entry : quantities.entrySet()) {
            entry.getKey().reduceStock(-entry.getValue()); // بازگرداندن موجودی
        }
        items.clear();
        quantities.clear();
    }
}

// کلاس کاربر
class User {
    private String username;
    private String password;
    private String address;
    private List<Order> orderHistory;
    private ShoppingCart cart;

    public User(String username, String password) {
        this.username = username;
        this.password = password; // در واقعیت باید رمزنگاری شود
        this.orderHistory = new ArrayList<>();
        this.cart = new ShoppingCart();
    }

    // متدهای getter و setter
    public String getUsername() { return username; }
    public String getAddress() { return address; }
    public ShoppingCart getCart() { return cart; }

    public void setAddress(String address) { this.address = address; }

    public void addToOrderHistory(Order order) {
        orderHistory.add(order);
    }

    public void displayOrderHistory() {
        if (orderHistory.isEmpty()) {
            System.out.println("\u001B[33mتاریخچه سفارشی وجود ندارد.\u001B[0m");
            return;
        }

        System.out.println("\n\u001B[36m=== تاریخچه سفارشات ===");
        for (Order order : orderHistory) {
            order.displayOrder();
        }
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}

// کلاس سیستم احراز هویت
class AuthSystem {
    private Map<String, User> users;
    private User currentUser;

    public AuthSystem() {
        this.users = new HashMap<>();
        // کاربر پیشفرض برای تست
        register("admin", "admin123", "آدرس تست");
    }

    public boolean register(String username, String password, String address) {
        if (users.containsKey(username)) {
            System.out.println("\u001B[31mاین نام کاربری قبلا ثبت شده است.\u001B[0m");
            return false;
        }

        User newUser = new User(username, password);
        newUser.setAddress(address);
        users.put(username, newUser);
        System.out.println("\u001B[32mثبت نام با موفقیت انجام شد.\u001B[0m");
        return true;
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.checkPassword(password)) {
            currentUser = user;
            System.out.println("\u001B[32mورود با موفقیت انجام شد. خوش آمدید " + username + "!\u001B[0m");
            return true;
        }
        System.out.println("\u001B[31mنام کاربری یا رمز عبور نامعتبر.\u001B[0m");
        return false;
    }

    public void logout() {
        currentUser = null;
        System.out.println("\u001B[32mبا موفقیت خارج شدید.\u001B[0m");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}

// کلاس درگاه پرداخت
interface PaymentGateway {
    boolean processPayment(double amount, String cardNumber);
}

class ZarinpalGateway implements PaymentGateway {
    @Override
    public boolean processPayment(double amount, String cardNumber) {
        // شبیه‌سازی پرداخت موفق
        System.out.println("\u001B[36mاتصال به درگاه پرداخت زرین‌پال...");
        System.out.println("پرداخت مبلغ " + String.format("%.2f", amount) + " تومان با موفقیت انجام شد.\u001B[0m");
        return true;
    }
}

// کلاس فروشگاه
class Store {
    private List<Product> inventory;
    private Map<String, List<Product>> categories;
    private PaymentGateway paymentGateway;

    public List<Product> getInventory() {
        return inventory;
    }

    public Store() {
        this.inventory = new ArrayList<>();
        this.categories = new HashMap<>();
        this.paymentGateway = new ZarinpalGateway();
        initializeInventory();
    }

    private void initializeInventory() {
        addProduct(new Product("لپ تاپ ایسوس", 25000000, "الکترونیک", 10));
        addProduct(new Product("گوشی سامسونگ", 15000000, "الکترونیک", 15));
        addProduct(new Product("کتاب برنامه‌نویسی", 500000, "کتاب", 20));
        addProduct(new Product("ماوس بی‌سیم", 800000, "الکترونیک", 30));
        addProduct(new Product("لباس ورزشی", 1200000, "پوشاک", 25));
    }

    public void addProduct(Product product) {
        inventory.add(product);
        categories.computeIfAbsent(product.getCategory(), k -> new ArrayList<>()).add(product);
    }

    public void displayProducts() {
        System.out.println("\n\u001B[36m=== محصولات موجود ===");
        for (int i = 0; i < inventory.size(); i++) {
            System.out.println((i+1) + ". " + inventory.get(i));
        }
    }

    public void displayByCategory(String category) {
        List<Product> catProducts = categories.get(category);
        if (catProducts == null || catProducts.isEmpty()) {
            System.out.println("\u001B[33mدسته‌بندی یافت نشد.\u001B[0m");
            return;
        }

        System.out.println("\n\u001B[36m=== دسته‌بندی: " + category + " ===");
        for (int i = 0; i < catProducts.size(); i++) {
            System.out.println((i+1) + ". " + catProducts.get(i));
        }
    }

    public List<String> getCategories() {
        return new ArrayList<>(categories.keySet());
    }

    public Product getProduct(int index) {
        if (index >= 0 && index < inventory.size()) {
            return inventory.get(index);
        }
        return null;
    }

    public boolean processPayment(double amount, String cardNumber) {
        return paymentGateway.processPayment(amount, cardNumber);
    }
}

// کلاس اصلی سیستم
public class OnlineShoppingSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Store store = new Store();
        AuthSystem authSystem = new AuthSystem();

        while (true) {
            if (!authSystem.isLoggedIn()) {
                showLoginMenu(scanner, authSystem);
            } else {
                showMainMenu(scanner, store, authSystem);
            }
        }
    }

    private static void showLoginMenu(Scanner scanner, AuthSystem authSystem) {
        System.out.println("\n\u001B[34m=== سیستم خرید آنلاین ===");
        System.out.println("1. ورود");
        System.out.println("2. ثبت نام");
        System.out.println("3. خروج");
        System.out.print("لطفا انتخاب کنید: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // مصرف خط جدید

        switch (choice) {
            case 1:
                System.out.print("نام کاربری: ");
                String username = scanner.nextLine();
                System.out.print("رمز عبور: ");
                String password = scanner.nextLine();
                authSystem.login(username, password);
                break;
            case 2:
                System.out.print("نام کاربری جدید: ");
                String newUsername = scanner.nextLine();
                System.out.print("رمز عبور جدید: ");
                String newPassword = scanner.nextLine();
                System.out.print("آدرس: ");
                String address = scanner.nextLine();
                authSystem.register(newUsername, newPassword, address);
                break;
            case 3:
                System.out.println("\u001B[35mبا تشکر از شما! خداحافظ.\u001B[0m");
                System.exit(0);
            default:
                System.out.println("\u001B[31mگزینه نامعتبر.\u001B[0m");
        }
    }

    private static void showMainMenu(Scanner scanner, Store store, AuthSystem authSystem) {
        User user = authSystem.getCurrentUser();
        ShoppingCart cart = user.getCart();

        System.out.println("\n\u001B[34m=== منوی اصلی ===");
        System.out.println("1. مشاهده محصولات");
        System.out.println("2. جستجوی محصولات");
        System.out.println("3. مشاهده سبد خرید");
        System.out.println("4. مدیریت سبد خرید");
        System.out.println("5. پرداخت");
        System.out.println("6. تاریخچه سفارشات");
        System.out.println("7. خروج از حساب");
        System.out.print("لطفا انتخاب کنید: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // مصرف خط جدید

        switch (choice) {
            case 1:
                browseProducts(scanner, store, cart);
                break;
            case 2:
                searchProducts(scanner, store);
                break;
            case 3:
                cart.displayCart();
                break;
            case 4:
                manageCart(scanner, cart);
                break;
            case 5:
                checkout(scanner, store, user, cart);
                break;
            case 6:
                user.displayOrderHistory();
                break;
            case 7:
                authSystem.logout();
                break;
            default:
                System.out.println("\u001B[31mگزینه نامعتبر.\u001B[0m");
        }
    }

    private static void browseProducts(Scanner scanner, Store store, ShoppingCart cart) {
        System.out.println("\n\u001B[36m=== دسته‌بندی محصولات ===");
        List<String> categories = store.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i+1) + ". " + categories.get(i));
        }
        System.out.println((categories.size()+1) + ". نمایش همه محصولات");
        System.out.print("لطفا انتخاب کنید: ");

        int catChoice = scanner.nextInt();
        scanner.nextLine();

        if (catChoice <= categories.size()) {
            store.displayByCategory(categories.get(catChoice-1));
        } else {
            store.displayProducts();
        }

        System.out.print("\nشماره محصول را برای افزودن به سبد وارد کنید (0 برای بازگشت): ");
        int productChoice = scanner.nextInt();
        scanner.nextLine();

        if (productChoice > 0) {
            Product p = store.getProduct(productChoice-1);
            if (p != null) {
                System.out.print("تعداد: ");
                int quantity = scanner.nextInt();
                scanner.nextLine();
                cart.addItem(p, quantity);
            }
        }
    }

    private static void searchProducts(Scanner scanner, Store store) {
        System.out.print("عبارت جستجو: ");
        String query = scanner.nextLine().toLowerCase();

        System.out.println("\n\u001B[36m=== نتایج جستجو ===");
        boolean found = false;
        for (Product p : store.getInventory()) {
            if (p.getName().toLowerCase().contains(query)) {
                System.out.println(p);
                found = true;
            }
        }

        if (!found) {
            System.out.println("\u001B[33mمحصولی یافت نشد.\u001B[0m");
        }
    }

    private static void manageCart(Scanner scanner, ShoppingCart cart) {
        cart.displayCart();
        if (cart.getItems().isEmpty()) return;

        System.out.println("\n1. حذف محصول");
        System.out.println("2. خالی کردن سبد");
        System.out.print("لطفا انتخاب کنید: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("شماره محصول برای حذف: ");
            int itemNum = scanner.nextInt();
            scanner.nextLine();
            cart.removeItem(itemNum-1);
        } else if (choice == 2) {
            cart.clear();
            System.out.println("\u001B[32mسبد خرید خالی شد.\u001B[0m");
        }
    }

    private static void checkout(Scanner scanner, Store store, User user, ShoppingCart cart) {
        if (cart.getItems().isEmpty()) {
            System.out.println("\u001B[33mسبد خرید شما خالی است.\u001B[0m");
            return;
        }

        cart.displayCart();
        System.out.println("\n\u001B[36mآدرس ارسال: " + user.getAddress());
        System.out.print("آیا مایل به پرداخت هستید؟ (بله/خیر): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("بله")) {
            System.out.print("شماره کارت: ");
            String cardNumber = scanner.nextLine();

            if (store.processPayment(cart.calculateTotal(), cardNumber)) {
                Order order = new Order(cart.getItems(), cart.calculateTotal());
                user.addToOrderHistory(order);
                cart.clear();
                order.updateStatus("پرداخت شده");
                System.out.println("\u001B[32mپرداخت با موفقیت انجام شد. سفارش شما ثبت گردید.\u001B[0m");
            }
        }
    }
}