package org.example.lld.bookmyshow;

import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

// Main class should be named 'Solution' and should not be public.
class Solution {
  static BookMyShow bookMyShow = new BookMyShow();

  public static void main(String[] args) {
    System.out.println("Hello, World");
    bookMyShow.main(args);
  }
}

// bookmyshow
// r1 = user should be able to search for theatres and movies in the city
// r2 = user should be able to select a show and seats
// r3 = user can book and pay for the seat

// core entitites: User, Address, Movie, MovieShow, Theatre, Screen, Seat,
// Payment, Ticket

class User {
  String id;
  String name;
  String email;
  String contact;
  City city;

  public User(String name, String email, String contact, City city) {
    this.name = name;
    this.email = email;
    this.contact = contact;
    this.city = city;
  }
}

class Address {
  City city;
  String street;

  public Address(City city, String street) {
    this.city = city;
    this.street = street;
  }
}

enum Genre {
  HORROR,
  DRAMA,
  ROMANCE,
}

enum City {
  MUMBAI,
  DELHI,
  CHENNAI,
}

class Movie {
  String id;
  String name;
  Date releaseDate;
  Genre genre;
  List<City> cities;

  public Movie(String name, Date date, Genre genre, List<City> cities) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.releaseDate = date;
    this.cities = cities;
  }

  public Movie getByCity(City city) {
    if (this.cities.contains(city)) return this;
    else throw new RuntimeException("No movie(s) in this city: {}");
  }
}

class Theatre {
  String id;
  String name;
  Address address;
  List<Movie> movies;
  Map<String, Screen> screenIdMap;

  Theatre(String name, Address address, Map<String, Screen> screens, List<Movie> movies) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.address = address;
    this.movies = movies;
    this.screenIdMap = screens;
  }
}

class Screen {
  String id;
  String code;
  List<Seat> seats;

  Screen(String code, List<Seat> seats) {
    this.id = UUID.randomUUID().toString();
    this.code = code;
    this.seats = seats;
  }
}

enum SeatType {
  ECONOMY,
  PREMIUM,
  RECLINER,
}

enum SeatStatus {
  OCCUPIED,
  AVAILABLE,
  LOCKED,
}

class Seat {
  String number;
  SeatType type;

  Seat(String number, SeatType type) {
    this.number = number;
    this.type = type;
  }
}

class ShowSeat extends Seat {
  Double price;
  SeatStatus status;

  ShowSeat(String number, SeatType type, Double price) {
    super(number, type);
    this.price = price;
    this.status = SeatStatus.AVAILABLE;
  }
}

class MovieShow {
  String id;
  Movie movie;
  Theatre theatre;
  Screen screen;
  Date startTime;
  Date endTime;
  List<ShowSeat> showSeats;

  MovieShow(
      Movie movie, Theatre theatre, String screenId, Date start, Date end, List<ShowSeat> seats) {
    this.id = UUID.randomUUID().toString();
    this.movie = movie;
    this.theatre = theatre;
    this.screen = theatre.screenIdMap.get(screenId);
    this.startTime = start;
    this.endTime = end;
    this.showSeats = seats;
  }
}

enum PaymentStatus {
  PENDING,
  COMPLETED,
}

enum PaymentMode {
  UPI,
  CREDIT,
}

class PaymentDetails {
  String txnId = UUID.randomUUID().toString();
  PaymentStatus status;
  PaymentMode mode;
  Double amount;

  PaymentDetails(PaymentStatus status, PaymentMode mode, Double amount) {
    this.status = status;
    this.mode = mode;
    this.amount = amount;
  }
}

class Booking {
  User user;
  MovieShow show;
  List<ShowSeat> bookedSeats;
  PaymentDetails paymentDetails;

  Booking(User user, MovieShow show, List<ShowSeat> bookedSeats, Double amount) {
    this.user = user;
    this.show = show;
    this.bookedSeats = bookedSeats;
    this.paymentDetails = new PaymentDetails(PaymentStatus.PENDING, null, amount);
  }

  Booking() {}
}

class BookingService {
  void validateAndLockSeats(MovieShow show, List<ShowSeat> seats) {

    List<ShowSeat> filteredShowSeats =
        show.showSeats
            .stream()
            .filter(seat -> SeatStatus.AVAILABLE.equals(seat.status) && seats.contains(seat))
            .collect(Collectors.toList());

    if (filteredShowSeats.isEmpty()) {
      throw new RuntimeException("Seats not available");
    }

    synchronized (seats) {
      for (ShowSeat seat : show.showSeats) {
        if (!seats.contains(seat)) {
          continue;
        }
        seat.status = SeatStatus.LOCKED;
      }
    }
  }

  void markSeatsOccupied(MovieShow show, List<ShowSeat> seats) {
    for (ShowSeat showSeat : show.showSeats) {
      if (!seats.contains(showSeat)) {
        continue;
      }
      showSeat.status = SeatStatus.OCCUPIED;
    }
  }

  Booking bookMyShow(User user, MovieShow show, List<ShowSeat> bookedSeats) {

    validateAndLockSeats(show, bookedSeats);
    Double amount = 0.0;

    for (ShowSeat seat : bookedSeats) {
      amount += seat.price;
    }

    // calculate tax and discount here if needed

    Booking booking = new Booking(user, show, bookedSeats, amount);
    markSeatsOccupied(show, bookedSeats);

    return booking;
  }
}

enum SearchCriterion {
  MOVIE,
  THEATRE,
}

class SearchService {
  Map<City, List<Movie>> cityToMovieMap = new HashMap<>();
  Map<String, Movie> nameToMovieMap = new HashMap<>();
  Map<Theatre, List<Movie>> theatreToMovieMap = new HashMap<>();

  SearchService(List<Movie> movies) {
    // manually adding to city map here else can use fillCityToMovieMap()
    this.cityToMovieMap.put(City.MUMBAI, movies);
    this.cityToMovieMap.put(City.DELHI, movies);
    this.nameToMovieMap =
        movies.stream().collect(Collectors.toMap(m -> m.name, Function.identity()));
  }

  void SearchService(List<Theatre> theatres) {
    this.theatreToMovieMap =
        theatres.stream().collect(Collectors.toMap(Function.identity(), t -> t.movies));
  }

  void fillCityToMovieMap(Movie movie) {
    for (City city : movie.cities) {
      List<Movie> cityMovies = this.cityToMovieMap.getOrDefault(city, new ArrayList<>());
      cityMovies.add(movie);
      this.cityToMovieMap.put(city, cityMovies);
    }
  }

  SearchService() {}

  // Movie getMovies(String name, City city) {

  // return nameToMovieMap.get(name).getByCity(city);
  // }

  Movie getMovies(String name, City city) {
    Movie movie = nameToMovieMap.get(name); // Fetch movie by name
    if (movie == null) {
      throw new RuntimeException("Movie not found: " + name); // Movie does not exist
    }
    return movie.getByCity(city); // Fetch movie by city
  }

  List<Movie> getMoviesByCity(String cityName, City city) {
    return cityToMovieMap.getOrDefault(City.valueOf(cityName), Collections.emptyList());
  }
}

class SearchFacade {
  SearchService searchService;

  SearchFacade(List<Movie> movies) {
    this.searchService = new SearchService(movies);
  }

  List<Movie> search(SearchCriterion criterion, City city, String query) {
    switch (criterion) {
      case MOVIE:
        return Arrays.asList(searchService.getMovies(query, city));
      case THEATRE:
        return searchService.getMoviesByCity(query, city);
      default:
        throw new RuntimeException("Search criteria not valid");
    }
  }
}

class BookMyShow {
  public static void main(String[] args) {
    // 1. Create Movies and Theatres
    Movie movie1 =
        new Movie("Movie 1", new Date(), Genre.DRAMA, Arrays.asList(City.MUMBAI, City.DELHI));
    Movie movie2 =
        new Movie("Movie 2", new Date(), Genre.HORROR, Arrays.asList(City.MUMBAI, City.DELHI));
    Movie movie3 = new Movie("Movie 3", new Date(), Genre.ROMANCE, Arrays.asList(City.DELHI));

    // 2. Create Screens and Seats
    List<ShowSeat> screen1Seats =
        Arrays.asList(
            new ShowSeat("A1", SeatType.ECONOMY, 200.0),
            new ShowSeat("A2", SeatType.ECONOMY, 200.0),
            new ShowSeat("A3", SeatType.RECLINER, 300.0));

    List<Seat> screen1Seats1 =
        Arrays.asList(
            new Seat("A1", SeatType.ECONOMY),
            new Seat("A2", SeatType.ECONOMY),
            new Seat("A3", SeatType.RECLINER));

    List<ShowSeat> screen2Seats =
        Arrays.asList(
            new ShowSeat("B1", SeatType.ECONOMY, 150.0),
            new ShowSeat("B2", SeatType.PREMIUM, 250.0));

    List<Seat> screen2Seats1 =
        Arrays.asList(new Seat("B1", SeatType.ECONOMY), new Seat("B2", SeatType.PREMIUM));

    Screen screen1 = new Screen("Screen 1", screen1Seats1);
    Screen screen2 = new Screen("Screen 2", screen2Seats1);

    Map<String, Screen> screenMap1 = new HashMap<>();
    screenMap1.put(screen1.id, screen1);
    screenMap1.put(screen2.id, screen2);

    // 3. Create Theatres
    Theatre theatre1 =
        new Theatre(
            "Theatre 1",
            new Address(City.MUMBAI, "Street 1"),
            screenMap1,
            Arrays.asList(movie1, movie2));
    Theatre theatre2 =
        new Theatre(
            "Theatre 2",
            new Address(City.DELHI, "Street 2"),
            screenMap1,
            Arrays.asList(movie1, movie3));

    // 4. Create User
    User user = new User("User1", "user1@gmail.com", "99999999", City.MUMBAI);

    // 5. Create SearchFacade with correct movies
    List<Movie> availableMovies = Arrays.asList(movie1, movie2, movie3);
    SearchFacade searchFacade = new SearchFacade(availableMovies);

    // 6. Searching for a movie in the city
    List<Movie> moviesInMumbai = searchFacade.search(SearchCriterion.MOVIE, City.MUMBAI, "Movie 1");

    System.out.println("Movies in Mumbai:");
    for (Movie movie : moviesInMumbai) {
      System.out.println(movie.name);
    }

    // 7. Creating a movie show
    MovieShow movieShow =
        new MovieShow(movie1, theatre1, screen1.id, new Date(), new Date(), screen1Seats);

    // 8. Booking Service
    BookingService bookingService = new BookingService();

    // 9. User selects seats and books
    try {
      List<ShowSeat> selectedSeats =
          Arrays.asList(screen1Seats.get(0), screen1Seats.get(1)); // A1 and A2
      Booking booking = bookingService.bookMyShow(user, movieShow, selectedSeats);

      // Booking successful, print details
      System.out.println("Booking successful for " + booking.user.name);
      System.out.println("Total amount: " + booking.paymentDetails.amount);
      System.out.println("Seats booked:");
      for (ShowSeat seat : booking.bookedSeats) {
        System.out.println("Seat: " + seat.number + ", Price: " + seat.price);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  BookMyShow() {}
}
