import java.util.*;

/**
 * ============================================================
 * CLASS - Service (AddOnService)
 * ============================================================
 *
 * Use Case 7: Add-On Service Selection
 *
 * @version 7.0
 */
class Service {

    /** Name of the service */
    private String serviceName;

    /** Cost of the service */
    private double cost;

    public Service(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }
}

/**
 * ============================================================
 * CLASS - AddOnServiceManager
 * ============================================================
 *
 * @version 7.0
 */
class AddOnServiceManager {

    /**
     * Maps reservation ID → list of services
     */
    private Map<String, List<Service>> servicesByReservation;

    public AddOnServiceManager() {
        servicesByReservation = new HashMap<>();
    }

    /**
     * Add service to reservation
     */
    public void addService(String reservationId, Service service) {
        servicesByReservation.putIfAbsent(reservationId, new ArrayList<>());
        servicesByReservation.get(reservationId).add(service);
    }

    /**
     * Calculate total service cost
     */
    public double calculateTotalServiceCost(String reservationId) {
        double total = 0.0;

        List<Service> services = servicesByReservation.get(reservationId);

        if (services != null) {
            for (Service s : services) {
                total += s.getCost();
            }
        }

        return total;
    }
}

/**
 * ============================================================
 * MAIN CLASS - UseCase7AddOnServiceSelection
 * ============================================================
 *
 * @version 7.0
 */
public class BookMyStayApp{

    public static void main(String[] args) {

        System.out.println("Add-On Service Selection");

        // Example reservation ID (from UC6 output)
        String reservationId = "Single-1";

        AddOnServiceManager manager = new AddOnServiceManager();

        // Add services
        Service breakfast = new Service("Breakfast", 500.0);
        Service spa = new Service("Spa", 1000.0);

        manager.addService(reservationId, breakfast);
        manager.addService(reservationId, spa);

        double totalCost = manager.calculateTotalServiceCost(reservationId);

        System.out.println("Reservation ID: " + reservationId);
        System.out.println("Total Add-On Cost: " + totalCost);
    }
}