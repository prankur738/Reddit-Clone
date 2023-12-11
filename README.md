# Reddit Clone App

Welcome to our Reddit Clone App! This project is a simple Reddit clone implemented using Spring Boot, Thymeleaf and PostgreSQL. It allows users to browse various communities, create and manage posts, comment on posts, upvote and downvote, and much more.

## Key Features

- **Subreddit Exploration:**<br>
    - Browse various subreddits and read posts as a non-logged-in user.
    - Subscribe to and unsubscribe from different subreddits.

- **Post Management:**<br>
    - Create, update, and delete personal posts in specific subreddits when logged in.
    - Comment on any post when logged in.

- **Voting System:**<br>
    - Upvote and downvote posts and comments when logged in.
    - Comments are sorted by the number of upvotes.

- **Homepage Customization:**<br>
    - Personalized homepage with posts from subscribed subreddits.
    - Popular page showcasing top posts across all subreddits.

- **Search and Sort:**<br>
    - Search for specific posts with filters and sorting options.

- **User Profile:**<br>
    - Profile page displaying user's posts, comments, and saved posts.
    - Karma system based on upvotes on posts and comments.

- **Subreddit Management:**<br>
    - Create a personal subreddit and add other users as moderators.
    - Delete posts and comments as a subreddit moderator.
    - Restrict certain users' access to a subreddit as a moderator.

## Technologies Used

- Spring Boot
- Spring Security
- Spring Hibernate JPA
- Thymeleaf
- PostgreSQL
- Firebase

## How to Run
You can find the live deployment of the project here -> [Live project link](https://reddit-clone-production-2937.up.railway.app/)

### To run locally
- Clone the repository.
- Set up your PostgreSQL database and update the application.properties file with the database configuration.
- Set up Firebase and replace the serviceAccountKey.json file with your own Firebase service account key.
- Build and run the application using Maven or your preferred IDE.
    ```bash
    mvn clean install
    java -jar target/redditclone-0.0.1-SNAPSHOT.jar
    ```
- Access the application at the specified port.

## Contact
If you have questions or need assistance with this project, you can reach out to the project maintainers:
- Renuka [renuka.thakre.26.1@mountblue.tech]
- Prankur [prankur.garg.26.1@mountblue.techy]
- Aravind [aravind.parthasarathy.26.1@mountblue.tech]
- Jitendra [jitendra.patel.26.1@mountblue.tech]


_**Feel free to explore, contribute, and provide feedback!**_
