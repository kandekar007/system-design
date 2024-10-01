package org.example.lld;

// Restaurant System
import java.util.*;
import java.util.stream.Collectors;

class RestaurantSystem {

  public static void printReq() {
    String r1 = "user should be able to search restaurant/dish";
    String r2 = "user should be able to order selected dish from restaurant";
    String r3 = "location based searches";
    String r4 = "user should be able to rate and provide feedback for restaurant";
    String r5 = "user should be able to view top rated 'k' restaurants";
    String r6 = "handle payments";
  }

  public static void main(String[] args) {
    System.out.println("Restaurant Main Running");

    printReq();
  }
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
  Address address = new Address("Delhi");
  Map<DishCategory, List<RestaurantDish>> dishMap; // works as menu
  Double rating;

  public Restaurant(String name, String cityName) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.address.city = cityName;
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
    return dishToRestaurantMap
        .get(DishCategory.valueOf(name))
        .stream()
        .filter(r -> r.address.city.equals(cityName))
        .collect(Collectors.toList());
  }

  public List<Restaurant> getRestaurantsByName(String name, String cityName) {
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

class Order {
  String id;
  User user;
}
