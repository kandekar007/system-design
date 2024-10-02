package org.example.lld;

import java.util.*;
import lombok.Getter;
import lombok.Setter;
import org.example.lld.restaurantSystem.RestaurantPaymentMode;
import org.example.lld.restaurantSystem.RestaurantPaymentStatus;

@Getter
@Setter
class ParkingLot {
  private List<Level> levelOrder = new ArrayList<>();

  ParkingLot() {}

  public static void printRequirements() {
    System.out.println("1. Parking lot will have multiple levels");
    System.out.println("2. Two types of spots with different rates");
    System.out.println(
        "3. Allocate proper vacant spot to incoming vehicle, small vehicles can use bigger spot if small ones are occupied");
    System.out.println("4. After vehicle leave, generate invoice and process payment");
  }

  public static void main(String[] args) {
    System.out.println("Parking Lot Main Running");
    printRequirements();

    ParkingLotService service = new ParkingLotService();

    Vehicle vehicle = new Car(1L, "ZYZ", null, null);
    Vehicle vehicle2 = new Bike(2L, "ZZYZ", null, null);

    try {
      Boolean alloted = service.allocateVehicle(vehicle);
      System.out.println("Car allocated: " + alloted);
      if (alloted) {
        System.out.println("Vehicle allocated: " + vehicle.getParkingSpot().getSerialName());
      }

      Boolean alloted2 = service.allocateVehicle(vehicle2);
      System.out.println("Bike allocated: " + alloted2);
      if (alloted2) {
        System.out.println("Vehicle allocated: " + vehicle2.getParkingSpot().getSerialName());
      }

      // Test deallocation and payment
      if (alloted) {
        Double amount = service.deallocateVehicle(vehicle, RestaurantPaymentMode.CASH);
        System.out.println("Deallocation amount for car: " + amount);
        service.pay(vehicle);
      }

      if (alloted2) {
        Double amount = service.deallocateVehicle(vehicle2, RestaurantPaymentMode.ONLINE);
        System.out.println("Deallocation amount for bike: " + amount);
        service.pay(vehicle2);
      }
    } catch (Exception e) {
      System.out.println("An error occurred: " + e.getMessage());
    }
  }
}

class ParkingLotService {
  LevelHandler levelHandler = new LevelHandler();
  PaymentStrategy cashStrategy = new CashStrategy();
  PaymentStrategy onlineStrategy = new OnlineStrategy();

  public PaymentStrategy decideStrategy(RestaurantPaymentMode mode) {
    switch (mode) {
      case CASH:
        return cashStrategy;
      case ONLINE:
        return onlineStrategy;
      default:
        throw new IllegalArgumentException("Unsupported payment mode: " + mode);
    }
  }

  public Boolean allocateVehicle(Vehicle vehicle) {
    ParkingSpot parkingSpot;
    PaymentDetails paymentDetails = new PaymentDetails();

    try {
      parkingSpot = levelHandler.getFreeSpotAndMoveItToOccupied(vehicle.getSpotType());
      vehicle.setPaymentDetails(paymentDetails);
      vehicle.setParkingSpot(parkingSpot);
    } catch (Exception e) {
      if (SpotType.TWO_WHEELER.equals(vehicle.getSpotType())) {
        try {
          parkingSpot = levelHandler.getFreeSpotAndMoveItToOccupied(SpotType.FOUR_WHEELER);
          vehicle.setPaymentDetails(paymentDetails);
          vehicle.setParkingSpot(parkingSpot);
          vehicle.setStartDateTime(new Date());
          return true;
        } catch (Exception e1) {
          return false;
        }
      }
      return false;
    }
    vehicle.setStartDateTime(new Date());
    return true;
  }

  public Double deallocateVehicle(Vehicle vehicle, RestaurantPaymentMode mode) {
    final Date currentDate = new Date(); // Use current date instead of hardcoded future date
    vehicle.setEndDateTime(currentDate);

    try {
      levelHandler.moveSpotFromOccupiedToFreeSpot(vehicle.getParkingSpot());
      PaymentStrategy strategy = decideStrategy(mode);
      strategy.calculate(vehicle);
    } catch (Exception e) {
      return 0.0;
    }
    return vehicle.getPaymentDetails().getAmount();
  }

  public void pay(Vehicle vehicle) {
    PaymentStrategy strategy = decideStrategy(vehicle.getPaymentDetails().getMode());
    strategy.pay(vehicle);
  }
}

enum SpotType {
  TWO_WHEELER(100.0),
  FOUR_WHEELER(200.0);

  private final double ratePerHour;

  SpotType(double rate) {
    this.ratePerHour = rate;
  }

  public double getRatePerHour() {
    return this.ratePerHour;
  }
}

@Getter
@Setter
class Vehicle {
  private Long id;
  private String licensePlate;
  private SpotType spotType;
  private Date startDateTime;
  private Date endDateTime;
  private ParkingSpot parkingSpot;
  private PaymentDetails paymentDetails;

  Vehicle(Long id, String licensePlate, Date startDateTime, Date endDateTime, SpotType spotType) {
    this.id = id;
    this.licensePlate = licensePlate;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.spotType = spotType;
  }
}

class Car extends Vehicle {
  Car(Long id, String licensePlate, Date startDateTime, Date endDateTime) {
    super(id, licensePlate, startDateTime, endDateTime, SpotType.FOUR_WHEELER);
  }
}

class Bike extends Vehicle {
  Bike(Long id, String licensePlate, Date startDateTime, Date endDateTime) {
    super(id, licensePlate, startDateTime, endDateTime, SpotType.TWO_WHEELER);
  }
}

@Getter
@Setter
class Level {
  private Long id;
  private String levelName;
  private int order;
  private Map<SpotType, List<ParkingSpot>> spotTypeToOccupiedSpotsMap = new HashMap<>();
  private Map<SpotType, List<ParkingSpot>> spotTypeToFreeSpotsMap;

  Level(Long id, String name) {
    this.id = id;
    this.levelName = name;
  }
}

@Getter
@Setter
class ParkingSpot {
  private Long id;
  private String serialName;
  private SpotType type;
  private Level level;
  private Boolean isOccupied;

  ParkingSpot(Long id, String serialName, SpotType type, Boolean isOccupied) {
    this.id = id;
    this.serialName = serialName;
    this.type = type;
    this.isOccupied = isOccupied;
  }
}

@Getter
@Setter
class LevelHandler {
  private List<Level> levelOrder = new ArrayList<>();

  LevelHandler() {
    Level level1 = new Level(1L, "1G");
    Level level2 = new Level(2L, "2G");

    ParkingSpot spot1 = new ParkingSpot(1L, "100", SpotType.TWO_WHEELER, false);
    ParkingSpot spot2 = new ParkingSpot(2L, "101", SpotType.FOUR_WHEELER, false);
    ParkingSpot spot3 = new ParkingSpot(3L, "103", SpotType.FOUR_WHEELER, false);

    Map<SpotType, List<ParkingSpot>> spotTypeListMap = new HashMap<>();
    spotTypeListMap.put(SpotType.TWO_WHEELER, new ArrayList<>(Collections.singletonList(spot1)));
    spotTypeListMap.put(SpotType.FOUR_WHEELER, new ArrayList<>(Arrays.asList(spot2, spot3)));

    level1.setSpotTypeToFreeSpotsMap(spotTypeListMap);

    ParkingSpot spot11 = new ParkingSpot(4L, "200", SpotType.TWO_WHEELER, false);
    ParkingSpot spot22 = new ParkingSpot(5L, "201", SpotType.FOUR_WHEELER, false);
    Map<SpotType, List<ParkingSpot>> spotTypeListMap1 = new HashMap<>();
    spotTypeListMap1.put(SpotType.TWO_WHEELER, new ArrayList<>(Collections.singletonList(spot11)));
    spotTypeListMap1.put(SpotType.FOUR_WHEELER, new ArrayList<>(Collections.singletonList(spot22)));

    level2.setSpotTypeToFreeSpotsMap(spotTypeListMap1);

    levelOrder.add(level1);
    levelOrder.add(level2);
  }

  private void moveSpotToOccupied(SpotType spotType, ParkingSpot parkingSpot) {
    Level level = parkingSpot.getLevel();
    List<ParkingSpot> freeSpots = level.getSpotTypeToFreeSpotsMap().get(spotType);
    freeSpots.remove(parkingSpot);
    parkingSpot.setIsOccupied(true);

    level
        .getSpotTypeToOccupiedSpotsMap()
        .computeIfAbsent(spotType, k -> new ArrayList<>())
        .add(parkingSpot);
  }

  public ParkingSpot getFreeSpotAndMoveItToOccupied(SpotType spotType) {
    for (Level level : levelOrder) {
      Map<SpotType, List<ParkingSpot>> freeParkingSpotsForLevel = level.getSpotTypeToFreeSpotsMap();
      List<ParkingSpot> freeSpots = freeParkingSpotsForLevel.get(spotType);
      if (freeSpots != null && !freeSpots.isEmpty()) {
        ParkingSpot parkingSpot = freeSpots.get(0);
        parkingSpot.setLevel(level);
        moveSpotToOccupied(spotType, parkingSpot);
        return parkingSpot;
      }
    }
    throw new RuntimeException("No free spots found for spotType: " + spotType.name());
  }

  public void moveSpotFromOccupiedToFreeSpot(ParkingSpot parkingSpot) {
    if (!parkingSpot.getIsOccupied()) {
      throw new RuntimeException("Spot is already free");
    }

    Level level = parkingSpot.getLevel();
    SpotType spotType = parkingSpot.getType();

    parkingSpot.setIsOccupied(false);
    level.getSpotTypeToOccupiedSpotsMap().get(spotType).remove(parkingSpot);
    level.getSpotTypeToFreeSpotsMap().get(spotType).add(parkingSpot);
  }

  public void addLevel(Level level) {
    if (levelOrder.contains(level)) {
      throw new IllegalArgumentException("Level already present");
    }
    levelOrder.add(level);
  }
}

enum PaymentStatus {
  COMPLETED,
  PENDING,
}

enum PaymentMode {
  CASH,
  ONLINE,
}

@Getter
@Setter
class PaymentDetails {
  public String id;
  private String txId;
  private RestaurantPaymentMode mode;
  private Double amount;
  private RestaurantPaymentStatus status;

  PaymentDetails(String txId, RestaurantPaymentMode mode, Double amount) {
    this.id = UUID.randomUUID().toString();
    this.txId = txId;
    this.mode = mode;
    this.amount = amount;
    this.status = RestaurantPaymentStatus.PENDING;
  }

  PaymentDetails() {
    this.id = UUID.randomUUID().toString();
    this.status = RestaurantPaymentStatus.PENDING;
  }
}

interface PaymentStrategy {
  void pay(Vehicle vehicle);

  void calculate(Vehicle vehicle);
}

class CashStrategy implements PaymentStrategy {
  @Override
  public void pay(Vehicle vehicle) {
    PaymentDetails paymentDetails = vehicle.getPaymentDetails();
    paymentDetails.setTxId(UUID.randomUUID().toString());
    paymentDetails.setStatus(RestaurantPaymentStatus.COMPLETED);
  }

  @Override
  public void calculate(Vehicle vehicle) {
    long duration = (vehicle.getEndDateTime().getTime() - vehicle.getStartDateTime().getTime());
    long hours = (long) Math.ceil((double) duration / (1000 * 3600));

    Double amount = hours * vehicle.getSpotType().getRatePerHour();
    vehicle.getPaymentDetails().setAmount(amount);
    vehicle.getPaymentDetails().setMode(RestaurantPaymentMode.CASH);
  }
}

class OnlineStrategy implements PaymentStrategy {
  private static final Double ADHOC = 50.0;

  @Override
  public void pay(Vehicle vehicle) {
    PaymentDetails paymentDetails = vehicle.getPaymentDetails();
    paymentDetails.setTxId(UUID.randomUUID().toString());
    paymentDetails.setStatus(RestaurantPaymentStatus.COMPLETED);
  }

  @Override
  public void calculate(Vehicle vehicle) {
    long duration = (vehicle.getEndDateTime().getTime() - vehicle.getStartDateTime().getTime());
    long hours = (long) Math.ceil((double) duration / (1000 * 3600));

    Double amount = hours * vehicle.getSpotType().getRatePerHour() + ADHOC;
    vehicle.getPaymentDetails().setAmount(amount);
    vehicle.getPaymentDetails().setMode(RestaurantPaymentMode.ONLINE);
  }
}
