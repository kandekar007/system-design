package org.example.lld;

// Restaurant System
import java.util.*;
import java.util.stream.*;

// Main class should be named 'Solution' and should not be public.
class Solution {
  static RestaurantSystem restaurantSystem = new RestaurantSystem();

  public static void main(String[] args) {
    System.out.println("Hello, World");
    restaurantSystem.printReq();

    // create user
    User user = new User("abc", "99889898989", new Address("Delhi"));

    // create few restaurants
    Restaurant r1 = new Restaurant("R1", "Delhi");
    Restaurant r2 = new Restaurant("R2", "Guwahati");

    // create dishes -- taken care of

    // search with restaurant name
    RestaurantService restaurantService = new RestaurantService(Arrays.asList(r1, r2));
    SearchFacade searchFacade = new SearchFacade(restaurantService);

    List<Restaurant> searchResult1 =
        searchFacade.search("R1", SearchCriteria.RESTAURANT, user.address.city);
    System.out.println(
        "searchResult1: " + searchResult1.get(0).name + searchResult1.get(0).address.city);

    // search with dish
    List<Restaurant> searchResult2 =
        searchFacade.search(DishCategory.BIRYANI.name(), SearchCriteria.DISH, user.address.city);
    System.out.println(
        "searchResult2: " + searchResult2.get(0).name + searchResult2.get(0).address.city);

    // place order
    OrderService orderService = new OrderService();
    Order placedOrder =
        orderService.placeOrder(
            user,
            searchResult1.get(0).dishMap.get(DishCategory.BIRYANI),
            user.address,
            searchResult1.get(0));
    Order placedOrder1 =
        orderService.placeOrder(
            user,
            searchResult1.get(0).dishMap.get(DishCategory.VADA_PAV),
            user.address,
            searchResult1.get(0));

    System.out.println(
        placedOrder.eta
            + " "
            + placedOrder.status.name()
            + " "
            + placedOrder.orderedDishes.get(0).name);
    System.out.println(
        placedOrder1.eta
            + " "
            + placedOrder1.status.name()
            + " "
            + placedOrder1.orderedDishes.get(0).name);
  }
}

class RestaurantSystem {

  public void printReq() {
    String r1 = "user should be able to search restaurant/dish";
    String r2 = "user should be able to order selected dish from restaurant";
    String r3 = "location based searches";
    String r4 = "user should be able to rate and provide feedback for restaurant";
    String r6 = "handle payments";

    System.out.println(r1);
    System.out.println(r2);
    System.out.println(r3);
    System.out.println(r4);
    System.out.println(r6);
  }

  RestaurantSystem() {}
}

// Core classes: search, restaurant, user, location, order, dish, bill
// enum: dishCategory, paymentStatus
enum SearchCriteria {
  DISH,
  RESTAURANT,
}

enum DishCategory {
  VADA_PAV,
  BURGER,
  MISAL,
  BIRYANI,
}

abstract class Dish {
  String id;
  String name;
  String description;
  DishCategory category;

  public Dish(String name, String description, DishCategory category) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.description = description;
    this.category = category;
  }
}

class RestaurantDish extends Dish {

  double price;
  double rating;

  public RestaurantDish(
      String name, String description, DishCategory dishCategory, double price, double rating) {
    super(name, description, dishCategory);
    this.price = price;
    this.rating = rating;
  }
}

class Restaurant {
  String id;
  String name;
  Address address;
  Map<DishCategory, List<RestaurantDish>> dishMap; // works as menu
  Double rating;

  public Restaurant(String name, String cityName) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.address = new Address(cityName);
    this.dishMap = new HashMap<>();
    this.rating = 4.0;

    RestaurantDish restaurantDish1 =
        new RestaurantDish(
            name + " Hyderabadi biryani", "rice, non-veg", DishCategory.BIRYANI, 100.0, 4.3);
    RestaurantDish restaurantDish2 =
        new RestaurantDish(
            name + " Lucknow Biryani", "rice, non-veg", DishCategory.BIRYANI, 130.0, 4.1);
    RestaurantDish restaurantDish3 =
        new RestaurantDish(
            name + " Mumbai vadapav", "patatoes, veg", DishCategory.VADA_PAV, 50.0, 4.7);

    this.dishMap.put(DishCategory.BIRYANI, Arrays.asList(restaurantDish1, restaurantDish2));
    this.dishMap.put(DishCategory.VADA_PAV, Arrays.asList(restaurantDish3));
  }
}

class RestaurantService {

  List<Restaurant> restaurants;
  Map<DishCategory, List<Restaurant>> dishToRestaurantMap = new HashMap<>();

  public void setDishToRestaurantMap(List<Restaurant> restaurants) {
    dishToRestaurantMap.put(DishCategory.BIRYANI, restaurants);
    dishToRestaurantMap.put(DishCategory.VADA_PAV, restaurants);
  }

  RestaurantService(List<Restaurant> restaurants) {
    this.restaurants = restaurants;
    setDishToRestaurantMap(restaurants);
  }

  public List<Restaurant> getRestaurantsForDishCategory(String name, String cityName) {

    if (dishToRestaurantMap.isEmpty()) {
      throw new RuntimeException("No dish found");
    }

    return dishToRestaurantMap
        .get(DishCategory.valueOf(name))
        .stream()
        .filter(r -> r.address.city.equals(cityName))
        .collect(Collectors.toList());
  }

  public List<Restaurant> getRestaurantsByName(String name, String cityName) {

    if (restaurants.isEmpty()) {
      throw new RuntimeException("No restaurants found nearby");
    }

    return restaurants
        .stream()
        .filter(r -> r.name.equals(name) && r.address.city.equals(cityName))
        .collect(Collectors.toList());
  }
}

class SearchFacade {

  private RestaurantService restaurantService;

  SearchFacade(RestaurantService restaurantService) {
    this.restaurantService = restaurantService;
  }

  public List<Restaurant> search(String query, SearchCriteria criteria, String userCity) {
    switch (criteria) {
      case DISH:
        return restaurantService.getRestaurantsForDishCategory(query, userCity);
      case RESTAURANT:
        return restaurantService.getRestaurantsByName(query, userCity);
      default:
        throw new IllegalArgumentException("Search not allowed");
    }
  }
}

class Address {

  String city;
  String street;
  String flatNumber;
  String pincode;
  String lat;
  String lng;

  public Address(String city) {
    this.city = city;
    this.street = city + "123";
    this.flatNumber = "1234";
    this.pincode = "110011";
    this.lat = "0.0.0.1";
    this.lng = "1.0.0.1";
  }
}

class User {

  String id;
  String name;
  Address address;
  String phone;

  public User(String name, String phone, Address address) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.phone = phone;
    this.address = address;
  }
}

enum RestaurantPaymentMode {
  ONLINE,
  CASH,
}

enum RestaurantPaymentStatus {
  PENDING,
  COMPLETED,
}

enum TaxSlab {
  FOOD(18.0),
  SNACKS(30.0),
  ;
  Double percentage;

  TaxSlab(Double percentage) {
    this.percentage = percentage;
  }

  public Double getPercentage() {
    return this.percentage;
  }
}

class Payment {
  String txnNo;
  Double orderAmount;
  RestaurantPaymentStatus status;
  RestaurantPaymentMode mode;
  Double taxAmount;
  Double total;

  public Payment(Double amount, RestaurantPaymentMode mode, TaxSlab slab) {
    this.txnNo = UUID.randomUUID().toString();
    this.orderAmount = amount;
    this.taxAmount = slab.getPercentage() * amount;
    this.mode = mode;
    this.status = RestaurantPaymentStatus.PENDING;
    this.total = orderAmount + taxAmount;
  }

  // public Payment createPayment(Double amount, PaymentMode mode, TaxSlab slab) {
  //     return new Payment(amount, mode, slab);
  // }

  public void completePayment(Double paidAmount) {
    if (!paidAmount.equals(this.total) || !RestaurantPaymentStatus.PENDING.equals(this.status)) {
      throw new RuntimeException("Something went wrong");
    }

    this.status = RestaurantPaymentStatus.COMPLETED;
  }
}

enum OrderStatus {
  IN_PROGRESS,
  IN_TRANSIT,
  DELIVERED,
}

class Order {
  String id;
  User user;
  Double eta;
  Payment paymentDetails;
  OrderStatus status;
  Address orderAddress;
  Restaurant restaurant;
  List<RestaurantDish> orderedDishes;

  Order(
      User user,
      Double eta,
      Payment payment,
      List<RestaurantDish> restaurantDishs,
      Address orderAddress,
      Restaurant restaurant) {
    this.user = user;
    this.eta = eta;
    this.paymentDetails = payment;
    this.status = OrderStatus.IN_PROGRESS;
    this.orderedDishes = restaurantDishs;
    this.orderAddress = orderAddress;
    this.restaurant = restaurant;
  }
}

class OrderService {
  private Double getETA(Address orderAddress, Restaurant restaurant, List<RestaurantDish> dish) {
    return 10.0;
  }

  public Order placeOrder(
      User user,
      List<RestaurantDish> restaurantDishs,
      Address orderAddress,
      Restaurant restaurant) {
    Double eta = getETA(orderAddress, restaurant, restaurantDishs);
    Double amount = 199.0;
    Payment payment = new Payment(amount, RestaurantPaymentMode.CASH, TaxSlab.FOOD);

    return new Order(user, eta, payment, restaurantDishs, orderAddress, restaurant);
  }

  OrderService() {}
}
