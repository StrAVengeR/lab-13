import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;

class Product {
    private static final Logger logger = LogManager.getLogger(Product.class);
    private String name;
    private int quantity;

    public Product(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
        logger.debug("Шинэ бүтээгдэхүүн үүсэв: {}, тоо: {}", name, quantity);
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        logger.info("Бүтээгдэхүүн {}-ийн тоо шинэчлэгдэв: {}", name, quantity);
        this.quantity = quantity;
    }
}

class Warehouse {
    private static final Logger logger = LogManager.getLogger(Warehouse.class);
    private String id;
    private List<Product> products;

    public Warehouse(String id) {
        this.id = id;
        this.products = new ArrayList<>();
        logger.info("Агуулах үүсэв: {}", id);
    }

    public void receiveProduct(Product product) {
        for (Product p : products) {
            if (p.getName().equals(product.getName())) {
                p.setQuantity(p.getQuantity() + product.getQuantity());
                logger.info("Агуулах {} -д {} нэртэй бараа нэмэгдэв, тоо: {}", id, product.getName(), product.getQuantity());
                return;
            }
        }
        products.add(product);
        logger.info("Шинэ бүтээгдэхүүн агуулахад нэмэгдэв: {}, тоо: {}", product.getName(), product.getQuantity());
    }

    public void releaseProduct(Product product) {
        for (Product p : products) {
            if (p.getName().equals(product.getName())) {
                int newQuantity = p.getQuantity() - product.getQuantity();
                if (newQuantity < 0) {
                    logger.warn("Хангалтгүй тоо {}: шаардсан={}, байгаа={}", p.getName(), product.getQuantity(), p.getQuantity());
                }
                p.setQuantity(Math.max(newQuantity, 0));
                logger.info("Бараа гаргав: {}, тоо: {}, үлдэгдэл: {}", p.getName(), product.getQuantity(), p.getQuantity());
                return;
            }
        }
        logger.warn("Гаргах бараа олдсонгүй: {}", product.getName());
    }

    public List<Product> getInventory() {
        return products;
    }

    public String getId() {
        return id;
    }
}

class Manager {
    private static final Logger logger = LogManager.getLogger(Manager.class);
    private String name;
    private Warehouse warehouse;

    public Manager(String name, Warehouse warehouse) {
        this.name = name;
        this.warehouse = warehouse;
        logger.info("Менежер томилогдов: {}", name);
    }

    public void createIncomeReceipt(Product product, String giver, Date date) {
        warehouse.receiveProduct(product);
        IncomeReceipt receipt = new IncomeReceipt(product, giver, date);
        logger.info("Орлогын падаан үүсэв - {}: {} ширхэг, өгсөн: {}", product.getName(), product.getQuantity(), giver);
    }

    public void createOutcomeReceipt(Product product, String receiver, Date date) {
        warehouse.releaseProduct(product);
        OutcomeReceipt receipt = new OutcomeReceipt(product, receiver, date);
        logger.info("Зарлагын падаан үүсэв - {}: {} ширхэг, авсан: {}", product.getName(), product.getQuantity(), receiver);
    }

    public void generateStockReport(Date startDate, Date endDate) {
        logger.info("Нөөцийн тайлан: {} - {}", startDate, endDate);
        System.out.println("=== Нөөцийн тайлан ===");
        for (Product p : warehouse.getInventory()) {
            System.out.println(p.getName() + ": " + p.getQuantity());
        }
    }

    public void performInventoryCheck(Product product, int actualQuantity, Date date) {
        for (Product p : warehouse.getInventory()) {
            if (p.getName().equals(product.getName())) {
                int difference = actualQuantity - p.getQuantity();
                logger.info("Тооллого хийв: {}, илүү/дутаасан: {}", p.getName(), difference);
                p.setQuantity(actualQuantity);
                return;
            }
        }
        logger.warn("Тооллого хийх бараа олдсонгүй: {}", product.getName());
    }
}

class IncomeReceipt {
    private Product product;
    private String giver;
    private Date date;

    public IncomeReceipt(Product product, String giver, Date date) {
        this.product = product;
        this.giver = giver;
        this.date = date;
    }
}

class OutcomeReceipt {
    private Product product;
    private String receiver;
    private Date date;

    public OutcomeReceipt(Product product, String receiver, Date date) {
        this.product = product;
        this.receiver = receiver;
        this.date = date;
    }
}

public class WarehouseSystem {
    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse("WH01");
        Manager manager = new Manager("Нярав Бат", warehouse);

        Product rice = new Product("Будаа", 100);
        Product flour = new Product("Гурил", 50);

        manager.createIncomeReceipt(rice, "Нийлүүлэгч А", new Date());
        manager.createIncomeReceipt(flour, "Нийлүүлэгч Б", new Date());

        manager.createOutcomeReceipt(new Product("Будаа", 30), "Хэрэглэгч В", new Date());

        manager.generateStockReport(new Date(), new Date());

        manager.performInventoryCheck(new Product("Будаа", 0), 65, new Date());
    }
}
