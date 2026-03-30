import java.util.LinkedList;
import java.util.Queue;

/**
 * ============================================================
 * CLASS - Reservation
 * ============================================================
 *
 * Use Case 5: Booking Request (FIFO)
 *
 * @version 5.0
 */
class Reservation {

    /** Name of the guest making the booking. */
    private String guestName;

    /** Requested room type. */
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
 * Use Case 5: Booking Request (FIFO)
 *
 * @version 5.0
 */
class BookingRequestQueue {

    /** Queue that stores booking requests. */
    private Queue<Reservation> requestQueue;

    /** Initializes empty queue */
    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    /** Add request */
    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
    }

    /** Get next request (FIFO) */
    public Reservation getNextRequest() {
        return requestQueue.poll();
    }

    /** Check if queue not empty */
    public boolean hasPendingRequests() {
        return !requestQueue.isEmpty();
    }
}

/**
 * ============================================================
 * MAIN CLASS - UseCase5BookingRequestQueue
 * ============================================================
 *
 * Use Case 5: Booking Request (FIFO)
 *
 * @version 5.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        // Header
        System.out.println("Booking Request Queue");

        // Create queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Create reservations
        Reservation r1 = new Reservation("Abhi", "Single");
        Reservation r2 = new Reservation("Subha", "Double");
        Reservation r3 = new Reservation("Vanmathi", "Suite");

        // Add to queue
        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

        // Process in FIFO order
        while (bookingQueue.hasPendingRequests()) {
            Reservation r = bookingQueue.getNextRequest();

            System.out.println(
                    "Processing booking for Guest: " +
                            r.getGuestName() +
                            ", Room Type: " +
                            r.getRoomType()
            );
        }
    }
}