package org.example.lld.twitter;

import java.util.*;
import java.util.stream.Collectors;

/*
 *  r1 = user can post tweet
 *  r2 = user can like and comment on tweets
 *  r3 = users can follow each other
 *  r4 = users can reply to comments
 *  core entities: user, tweet, comments
 */
class Twitter {
  public static void main(String[] args) {

    // create users
    User user1 = new User("abc", "abc@gmail.com");
    User user2 = new User("xyz", "xyz@gmail.com");

    // post tweets
    user1.createTweet("tweet unda leda");
    user2.createTweet("tweet ante pante");

    System.out.println("User 1 tweet: " + user1.tweets.get(0).text);
    System.out.println("User 2 tweet: " + user2.tweets.get(0).text);

    // add comment
    user1.tweets.get(0).addComment("comment on 1", user2.id);
    System.out.println("comment on tweet 1: " + user1.tweets.get(0).comments.get(0).text);

    // follow
    user1.follow(user2);
    System.out.println("following user: " + user1.following.get(0).name);

    // get tweets
    user1.getTweets();
    System.out.println("getTweets: " + user1.getTweets().get(0).text);
  }
}

class User {
  static final Random random = new Random();
  Long id;
  String name;
  String email;
  List<User> following = new ArrayList<>();
  List<User> followers = new ArrayList<>();
  List<Tweet> tweets = new ArrayList<>();

  public User(String name, String email) {
    this.id = (Long) random.nextLong();
    this.name = name;
    this.email = email;
  }

  public void createTweet(String tweetText) {
    Tweet tweet = new Tweet(tweetText, 0L);
    this.tweets.add(tweet);
  }

  public void follow(User user) {
    this.following.add(user);
    user.followers.add(this);
  }

  public List<Tweet> getTweets() {
    return this.following
        .stream()
        .map(user -> user.tweets)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }
}

class Tweet {
  static final Random random = new Random();
  Long id;
  String text;
  Long likeCount;
  List<Comment> comments = new ArrayList<>();

  public Tweet(String text, Long likeCount) {
    this.id = (Long) random.nextLong();
    this.text = text;
    this.likeCount = likeCount;
  }

  public void addComment(String commentText, Long userId) {
    Comment comment = new Comment(commentText, userId, null);
    this.comments.add(comment);
  }

  public void addLike() {
    this.likeCount++;
  }
}

class Reply extends Comment {
  Long parentCommentId;

  Reply(String text, Long userId, List<Reply> replies, Long parentCommentId) {
    super(text, userId, replies);
    this.parentCommentId = parentCommentId;
  }
}

class Comment {
  static final Random random = new Random();
  Long id;
  String text;
  Long userId;
  Long likeCount;
  List<Reply> replies = new ArrayList<>();

  public Comment(String text, Long userId, List<Reply> replies) {
    this.id = (Long) random.nextLong();
    this.text = text;
    this.userId = userId;
    this.replies = replies;
  }

  public void addLike() {
    this.likeCount++;
  }

  public void addReply(String text, Long userId) {
    Reply reply = new Reply(text, userId, null, this.id);
    this.replies.add(reply);
  }
}
