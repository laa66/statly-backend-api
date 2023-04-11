# Statly - Your personal Spotify stats 🎵

Statly is a web application powered by Spotify API that lets you view your music
statistics, account mainstream score, and recently played tracks.
You can also export a playlist to your Spotify account from your favourite tracks in three different
ranges.

Check out the [Statly demo](https://statly-app.onrender.com) to see the application in action.

<hr>

**Important Note!**

    To access the app, please click on "Join Beta" and register with your full name and 
    Spotify email address associated with your Spotify account.

    This is necessary because I use the basic version of the Spotify API and 
    need to add testers to Spotify developer panel to grant them access.

## How to run ⚡

#### It's simple! Go and check [Statly demo](https://statly-app.onrender.com) or...
#### Try to run this application on your local machine. You will need to do the following:

1. Obtain a **Client ID** and **Client secret** from [Spotify Developer](https://developer.spotify.com/) panel
2. Set the ``SPOTIFY_CLIENT_ID``, ``SPOTIFY_CLIENT_SECRET`` environment variables
   with the values from your Spotify Developer panel and set ``STATLY_APP_URL``
   variable to ``http://localhost:3000``
 <!-- end -->

    # Clone this repository to your local machine
    $ git clone https://github.com/laa66/statly-app.git

    # Run the backend Spring Boot application in the root project directory:
    $ ./mvnw spring-boot:run

    # Run the frontend React application in the directory statly-app/react-app:
    $ npm start

#### ...and Access the application in your web browser at ``http://localhost:3000``

## How to use 🗺️

1. At the very beginning, try to register as beta-tester on the Home page.
   Your access should be granted after 15 minutes.
2. Click on ``Login with Spotify`` button on the Home page. You will be redirected to Spotify account service
3. Login with your Spotify username and password
4. **Congratulations!** Now you can use all features of Statly

## Features 📌
#### Here, you can check app features:

* Allows users to see their ``top tracks`` based on their listening habits over three different time ranges

* Enables users to ``export playlists`` containing their favorite tracks based on their listening habits over three different time ranges

* Enables users to view their ``top artists`` over three different time ranges

* Shows users their ``top genres`` over three different time ranges

* Displays a user's ``mainstream score`` based on the popularity of the tracks they listen to

* Allows users to view their ``recently played`` tracks, giving them easy access to music they have enjoyed

* Redirect user to ``Spotify web player`` immediately after clicking on some resources e.g. on special track, artist or created playlist


## Built with 🔨

#### Technologies & tools used:

- JDK 19
- Spring Boot 3 (Spring MVC, Spring Security, Spring AOP)
- Maven
- OAuth 2.0
- React.js
- Node.js
- HTML/CSS
- Bootstrap v5.3
- IntelliJ IDEA Community Edition
- Visual Studio Code

#### Tested with:

- Spring Boot Test
- JUnit 5 & AssertJ
- Mockito
- Hamcrest


## To-do 💡

- Add MySQL database to save user stats and compare their listening habits
- Make app stateless with JWT authentication
- Rebuild the Mainstream Score section in the React-app
- Add custom playlist image to exported playlists
- Create section with user track analysis that describes the track structure and musical content, rhythm etc.
- Add small web player to the React-app which will play user's top songs inside the browser
- Create functionality that will allow users to add friends and check their statistics
- Move Spotify-powered app from development mode to extended modem, allowing anyone to use Statly without registering for the beta