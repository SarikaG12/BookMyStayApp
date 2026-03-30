import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * ============================================================
 * CLASS - Reservation
 * ============================================================
 *
 * Represents a booking request.
 *
 * @version 11.0
 */
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

/**
 * ============================================================
 * CLASS - BookingRequestQueue
 * ============================================================
 *
 * Stores booking requests in FIFO order.
 *
 * @version 11.0
 */
class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
    }

    public Reservation getNextRequest() {
        return requestQueue.poll();
    }

    public boolean hasPendingRequests() {
        return !requestQueue.isEmpty();
    }
}

/**
 * ============================================================
 * CLASS - RoomInventory
 * ============================================================
 *
 * Stores room availability.
 *
 * @version 11.0
 */
class RoomInventory {
    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
        initializeInventory();
    }

    private void initializeInventory() {
        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }

    public Map<String, Integer> getRoomAvailability() {
        return roomAvailability;
    }

    public void updateAvailability(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }
}

/**
 * ============================================================
 * CLASS - RoomAllocationService
 * ============================================================
 *
 * Handles room allocation safely.
 *
 * @version 11.0
 */
class RoomAllocationService {
    private Set<String> allocatedRoomIds;
    private Map<String, Set<String>> assignedRoomsByType;

    public RoomAllocationService() {
        allocatedRoomIds = new HashSet<>();
        assignedRoomsByType = new HashMap<>();
    }

    public void allocateRoom(Reservation reservation, RoomInventory inventory) {
        String roomType = reservation.getRoomType();
        Integer available = inventory.getRoomAvailability().get(roomType);

        if (available != null && available > 0) {
            String roomId = generateRoomId(roomType);

            allocatedRoomIds.add(roomId);
            assignedRoomsByType.putIfAbsent(roomType, new HashSet<>());
            assignedRoomsByType.get(roomType).add(roomId);

            inventory.updateAvailability(roomType, available - 1);

            System.out.println("Booking confirmed for Guest: "
                    + reservation.getGuestName()
                    + ", Room ID: " + roomId);
        } else {
            System.out.println("No rooms available for Guest: "
                    + reservation.getGuestName()
                    + ", Requested Type: " + roomType);
        }
    }

    private String generateRoomId(String roomType) {
        assignedRoomsByType.putIfAbsent(roomType, new HashSet<>());

        int nextNumber = assignedRoomsByType.get(roomType).size() + 1;
        String roomId = roomType + "-" + nextNumber;

        while (allocatedRoomIds.contains(roomId)) {
            nextNumber++;
            roomId = roomType + "-" + nextNumber;
        }

        return roomId;
    }
}

/**
 * ============================================================
 * CLASS - ConcurrentBookingProcessor
 * ============================================================
 *
 * Use Case 11: Concurrent Booking Simulation
 *
 * Description:
 * This class represents a booking processor
 * that can be executed by multiple threads.
 *
 * It demonstrates how shared resources
 * such as booking queues and inventory
 * must be accessed in a thread-safe manner.
 *
 * @version 11.0
 */
class ConcurrentBookingProcessor implements Runnable {

    /**
     * Shared booking request queue.
     */
    private BookingRequestQueue bookingQueue;

    /**
     * Shared room inventory.
     */
    private RoomInventory inventory;

    /**
     * Shared room allocation service.
     */
    private RoomAllocationService allocationService;

    /**
     * Creates a new booking processor.
     *
     * @param bookingQueue shared booking queue
     * @param inventory shared inventory
     * @param allocationService shared allocation service
     */
    public ConcurrentBookingProcessor(
            BookingRequestQueue bookingQueue,
            RoomInventory inventory,
            RoomAllocationService allocationService
    ) {
        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
        this.allocationService = allocationService;
    }

    /**
     * Executes booking processing logic.
     *
     * This method is called when the thread starts.
     */
    @Override
    public void run() {
        while (true) {
            Reservation reservation;

            /*
             * Synchronize on the booking queue to ensure
             * that only one thread can retrieve a request
             * at a time.
             */
            synchronized (bookingQueue) {
                if (!bookingQueue.hasPendingRequests()) {
                    break;
                }
                reservation = bookingQueue.getNextRequest();
            }

            /*
             * Allocation also mutates shared inventory.
             * Synchronization ensures atomic allocation.
             */
            synchronized (inventory) {
                allocationService.allocateRoom(reservation, inventory);
            }
        }
    }
}

/**
 * ============================================================
 * MAIN CLASS - UseCase11ConcurrentBookingSimulation
 * ============================================================
 *
 * Use Case 11: Concurrent Booking Simulation
 *
 * Description:
 * This class simulates multiple users
 * attempting to book rooms at the same time.
 *
 * It highlights race conditions and
 * demonstrates how synchronization
 * prevents inconsistent allocations.
 *
 * @version 11.0
 */
public class BookMyStayApp {

    /**
     * Application entry point.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {

        System.out.println("Concurrent Booking Simulation");

        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();

        bookingQueue.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Double"));
        bookingQueue.addRequest(new Reservation("Kural", "Suite"));
        bookingQueue.addRequest(new Reservation("Subha", "Single"));

        // Create booking processor tasks
        Thread t1 = new Thread(
                new ConcurrentBookingProcessor(
                        bookingQueue, inventory, allocationService
                )
        );

        Thread t2 = new Thread(
                new ConcurrentBookingProcessor(
                        bookingQueue, inventory, allocationService
                )
        );

        // Start concurrent processing
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted.");
        }

        System.out.println();
        System.out.println("Remaining Inventory:");
        System.out.println("Single: " + inventory.getRoomAvailability().get("Single"));
        System.out.println("Double: " + inventory.getRoomAvailability().get("Double"));
        System.out.println("Suite: " + inventory.getRoomAvailability().get("Suite"));
    }
}