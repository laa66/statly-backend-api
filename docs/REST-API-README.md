# Spring REST API documentation

## Overview

This REST API provides endpoints to access Spotify user data. 
The API allows users to retrieve their top tracks, top artists, top genres, recently played tracks, and calculate their mainstream score. 
The API also provides functionality to create a playlist based on the user's top tracks.

API is designed using REST principles, using HTTP methods (GET, POST, DELETE) 
to perform operations on resources identified by URLs. 
The endpoints in Spring Boot application handle these HTTP methods, 
perform operations on resources shared by Spotify API and return JSON responses to React frontend.

You can also check [React app documentation](./React-app-README.md) if 
you want to look how frontend exchange data with backend.

## User Authentication

Statly-app implements the OAuth 2.0 Authorization Code flow with Spring Boot as a client and Spotify external API as a resource server.

**Configuration for OAuth 2.0 Client is available inside [Security Config class](../src/main/java/com/laa66/statlyapp/config/SecurityConfig.java)**

Let’s look how OAuth2 Authorization Code flow is implemented:

1. User is calling `/api/auth` endpoint, because it’s secured with OAuth 2.0 the authorization process will start


2. Client app will send GET request to `https://accounts.spotify.com/authorize?show_dialog=true` endpoint in resource server with special parameters defined in `ClientRegistration` bean


3. After user authorize access in Spotify account service, they will be redirected to callback URL defined in `ClientRegistration` bean in our case - `{baseUrl}/login/oauth2/code/{registrationId}` which includes two query params - ‘code’ and ‘state’


4. When callback request arrives our Client will retrieve ‘state’ parameter and check if it’s the same as ‘state’ parameter generated and added to the authorization request sent at the very beginning from our App.


5. Now Client will exchange ‘code’ for an Access Token by making POST request to `https://accounts.spotify.com/api/token` endpoint


6. After receiving Access and Refresh Token in response our App will make GET request to `https://api.spotify.com/v1/me` obtain user info and insert user details into the Spring Security context making current user authenticated.


In this configuration class there are defined few beans such as:

- `securityFilterChain`- defined to configure the CSRF token handling, authorization rules and logout settings.


- `customCorsFilter` - defined to configure CORS filter which allows to make cross-origin HTTP requests to web application.


- `clientRegistrationRepository` - defined to provide Client registration details for the OAuth 2.0 Client. It calls private method which creates special `spotifyClientRegistration` object which holds important properties used in OAuth 2.0 authorization.

## Rest Template & Interceptor

**Beans which configure this functionality are available inside [Interceptor](../src/main/java/com/laa66/statlyapp/interceptor/HeaderModifierTokenRefresherInterceptor.java) and [Rest Template Config](../src/main/java/com/laa66/statlyapp/config/OAuth2RestTemplateConfig.java) classes.**

Because `OAuth2RestTemplate` bean is deprecated in Spring Security 6, I need to implement different logic for adding current user’s access token to every request to Spotify API.

In this case I implemented special `restTemplateInterceptor` bean with `HeaderModifierTokenRefresherInterceptor` that retrieves authenticated user credentials and adds required headers by Spotify API to every request from my application.

This Interceptor also checks incoming response from API and verifies whether response code is equal to UNAUTHORIZED or any other error status code.

If response contains UNAUTHORIZED code Interceptor will make another call to the Spotify API with help of `SpotifyTokenService` to exchange current user’s refresh token for new access token, re-authenticate user with new access token and make another call to the Spotify API.

## Spotify API endpoints & data

**These URLs and also query parameters are defined inside [Spotify API constants class](../src/main/java/com/laa66/statlyapp/constants/SpotifyAPI.java)**

In this section I describe endpoints used for exchanging data between my application and Spotify API.

API responds with JSON formatted data, so I used `RestTemplate` bean configured to calls API and return `ResponseEntity<>` containing mapped object with help of built-in inside Spring Boot JSON serializer. Data is deserialized into [DTO](../src/main/java/com/laa66/statlyapp/DTO) objects with help of Java objects defined in [model](../src/main/java/com/laa66/statlyapp/model) package which are designed like JSON data from API response.

Objects created from API responses are later modified inside `SpotifyAPIService` and returned to the `AppController` which serves JSON formatted responses from REST API to React-app.

Spotify API endpoints used to exchange data and corresponding JSON response:

- returns refreshed access token - GET `https://accounts.spotify.com/api/token`
    - response JSON - `https://developer.spotify.com/documentation/web-api/tutorials/code-flow`


- returns Current User’s profile - GET `https://api.spotify.com/v1/me`
    - response JSON - `https://developer.spotify.com/documentation/web-api/reference/get-current-users-profile`


- returns Recently played tracks - GET `https://api.spotify.com/v1/me/player/recently-played?limit=50`
    - response JSON - `https://developer.spotify.com/documentation/web-api/reference/get-recently-played`


- returns Top 50 User most listened artists in three different time ranges with query parameter - GET `https://api.spotify.com/v1/me/top/artists?limit=50&time_range=`
    - response JSON - `https://developer.spotify.com/documentation/web-api/reference/get-users-top-artists-and-tracks`


- returns Top 50 User most listened tracks in three different time ranges with query parameter - GET `https://api.spotify.com/v1/me/top/tracks?limit=50&time_range=`
    - response JSON - `https://developer.spotify.com/documentation/web-api/reference/get-users-top-artists-and-tracks`


- used to create new public playlist - POST`https://api.spotify.com/v1/users/user_id/playlists`
    - response JSON - `https://developer.spotify.com/documentation/web-api/reference/add-tracks-to-playlist`


- used to add tracks to a previously created playlist - POST `https://api.spotify.com/v1/playlists/playlist_id/tracks`
    - response JSON - `https://developer.spotify.com/documentation/web-api/reference/create-playlist`


## Service Layer

### Spotify API Service

The `Spotify API Service` is a service layer responsible for calling the Spotify API and mapping the JSON response to DTO which is transferred to the Controller. The service layer implements the `SpotifyAPIService` interface.

In few methods there is also implemented logic for creating special objects (`TopGenresDTO` or `MainstreamScoreDTO`) from previously fetched objects such as `TopTracksDTO` or `TopArtistsDTO`.

This class has also injected `RestTemplate` bean that adds authenticated user’s access token to every API request and check if user’s access token is not expired.

Furthermore, there is also `@Cacheable` annotation on top of few methods which directly call the API. This design pattern approach was necessary because Spotify API has some limitations and I don’t want to make a call every time React client requests the REST API endpoint. Doing it could slow down the application and reach the limit of possible requests to Spotify API. Instead of calling every time external API responses are saved into cache with a key containing method name, authenticated user `userId` and `range` parameter.

This service is available inside [Spotify API Service class](/src/main/java/com/laa66/statlyapp/service/SpotifyAPIServiceImpl.java).

Methods description:

- `getCurrentUser()`: This method is used to get the current user’s data. It sends a GET request to the Spotify API endpoint `/v1/me`. The method returns the `UserIdDTO` object which contains the user's ID, username and his profile image.


- `getTopTracks(long userId, String range)`: This method is used to get user’s top tracks. It sends a GET request to the Spotify API endpoint `/v1/me/top/tracks?limit=50&time_range=` which is provided as a parameter. The method returns the `TopTracksDTO` object which contains list of 50 user’s most listened tracks.


- `getTopArtists(long userId, String range)`: This method is used to get user’s top artists. It sends a GET request to the Spotify API endpoint `/v1/me/top/artists?limit=50&time_range=`which is provided as a parameter. The method returns the `TopArtistsDTO` object which contains list of 50 user’s most listened artists.


- `getTopGenres(long userId, String range)`: This method is used to get the top genres of the user. It calls the `getTopArtists` method internally to get the top artists of the user. The method extracts the genres of each artist and creates a map of genre and its count. The map is sorted in descending order based on the count and the top 8 genres are selected. The method returns the `TopGenresDTO` object which contains list of 8 user’s most listened genres.


- `getMainstreamScore(long userId, String range)`: This method is used to get the mainstream score of the user. It calls the `getTopTracks` method internally to get the top tracks of the user. The method calculates the average popularity of the top tracks and returns the `MainstreamScoreDTO` object which contains the mainstream score of the user.


- `getRecentlyPlayed()`: This method is used to get the recently played tracks of the user. It sends a GET request to the Spotify API endpoint `/v1/me/player/recently-played?limit=50`. The method returns the `RecentlyPlayedDTO` object which contains list of 50 recently played user tracks.


- `postTopTracksPlaylist(long userId, String range)`: This method is used to create a playlist of the user's top tracks. It calls the `getCurrentUser` method internally to get the current user's ID. The method extracts the time range from the **`url`** parameter and posts an empty playlist with the time range description using the `postEmptyPlaylist` method. The method then gets the top tracks of the user using the `getTopTracks` method, extracts the URIs of the tracks, and posts the tracks to the playlist using the `postTracksToPlaylist` method. The method returns the `PlaylistDTO` object which contains the playlist ID and Spotify URL of created playlist.


- `postEmptyPlaylist(UserDTO user, String range)`: This method is used to post an empty playlist to the Spotify API. It takes the user's ID and the time range as parameters. The method reads the JSON body from the `post-playlist.json` file, replaces the placeholders with the user's ID and the time range, and posts the playlist to the Spotify API. The method returns the `PlaylistDTO` object which contains the playlist ID.


### User Service

The `UserServiceImpl` is a service [class](/src/main/java/com/laa66/statlyapp/service/UserServiceImpl.java) that implements `UserService` interface. It provides implementation for operations such as finding a user by email, saving user, deleting a user and saving user statistics. It also provides methods for comparing user current stats with last visit stats. The Class is annotated with `@Transactional` to group multiple database operations together as a single transaction.

`UserServiceImpl` also has injected 5 dependencies: `UserRepository`, `UserTrackRepository`, `UserAritstRepository`, `UserGenreRepository` , `UserMainstreamRepository` which are responsible for performing CRUD operations.

Methods description:

- `findUserByEmail(String email)` takes an email as a parameter and returns Optional `User` object from database.
- `saveUser(User user)` takes a `User` object as a parameter and saves it in the database.
- `deleteUser(long id)` takes user ID as a parameter, finds the user with given ID and delete it from database. If user is not found method will throw `UserNotFoundException` .
- `saveUserTracks`, `saveUserArtists`, `saveUserGenres`, `saveUserMainstream` these methods are called only from `CacheTask` component once a day. They map data from special DTO objects (cached Spotify API responses) to the entities which are saved in the database.
- `compareTracks`, `compareArtists`, `compareGenres`, `compareMainstream` these methods are called only from `AppController` and are used to read user’s last visit stats and compare them with current visit statistics.

### Custom OAuth 2.0 User Service

This [class](/path) is a custom implementation of `OAuth2UserService` interface which is used for obtaining the user attributes from the Resource Server User Info endpoint.

This customized bean gets user attributes and then checks if user was previously logged in to Statly app. If a user account with this email was created in Statly it simply returns `OAuth2User` instance with an additional attribute - `userId` that will be associated with current user authentication inside Security Context. If it’s user first time logging in, a new account connected with `email` attribute will be created and saved in database. The `userId` attribute is necessary for making calls to the database.

## Cache

Statly REST API is using `Caffeine` Cache implementation to cache Spotify API responses. This custom Cache is configured in [Cache Config class](/path).

When a user logs in for the first time during the day their statistics obtained from the Spotify API will be cached until 11:59 PM on the same day. At this time API responses will be saved to the database with [scheduled task](/path) implemented inside `CacheTask` component using `UserService` implementation, and the Cache will be cleared.

With this approach current user’s statistics can be compared with data from previous days.

## REST Controllers

This [REST Controller](../src/main/java/com/laa66/statlyapp/controller/AppController.java) is responsible for handling API requests from a React application and returning JSON data.

It uses the **`SpotifyAPIService`** class to interact with the Spotify Web API to retrieve data. It also uses the `Image` and `UserIdDTO` classes to handle user image data and user information data respectively. The `@Value` annotation is used to inject the `REACT_URL` property which points to the React-app from the application properties file into the controller.

`@RestController` annotation on the controller class indicates that all methods in the class return JSON data. The `@RequestMapping` annotation on the class specifies the base URL `/api` for all endpoints.

- **`/api/auth`**: This GET endpoint is starting point of application. At first it starts user authentication flow with help of Spring Security filters configured with OAuth 2.0. It redirects the user to the Spotify authentication page and then redirects them back to the React application with user information and profile image URL in the query string.


- **`/api/join`**: This POST endpoint is used for beta user sign-up. It logs information about the user who joined the beta test.
    - Header of this request should contain valid CSRF token which is generated inside REST API.
    - Incoming POST request body should look like this:

        ```json
        {
            "username": "test-user",
            "email": "testuser@domain.com"
        }
        ```

- **`/api/delete`**: This DELETE endpoint is used to delete all data from database associated with user Statly account.
  - Header of this request should contain valid CSRF token.


- **`/api/top/tracks`**: This GET endpoint returns user's 50 most listened tracks on Spotify based on a time range specified in the request parameter.
  - Response JSON look like this:

    ```json
    {
        "items": [
            {
                "album": {
                    "images": [
                        {
                            "url": "URL",
                            "height": 100,
                            "width": 100
                        }
                    ],
                    "name": "track",
                    "genres": null
                },
                "artists": [
                    {
                        "name": "artist 1"
                    },
                    {
                        "name": "artist 2"
                    }
                ],
                "name": "track",
                "popularity": 50,
                "uri": "spotify:track:uri",
                "external_urls": {
                    "spotify": "Spotify track URL"
                },
                "difference": null
            }
        ],
        "range": "50",
        "total": "50"
      }
    ```


- **`/api/top/artists`**: This GET endpoint returns user's 50 most listened artists on Spotify based on a time range specified in the request parameter.
  - Response JSON look like this:

    ```json
    {
        "total": "50",
        "range": "short",
        "items": [
            {
                "genres": [
                    "genre 1",
                    "genre 2"
                ],
                "images": [
                    {
                        "url": "URL",
                        "height": 100,
                        "width": 100
                    }
                ],
                "name": "artist name",
                "external_urls": {
                    "spotify": "Spotify artist URL"
                },
                "uri": "spotify:artist:uri",
                "difference": null
            }
        ]
    }
    ```


- **`/api/top/genres`**: This GET endpoint returns user's top genres on Spotify based on a time range specified in the request parameter.
  - Response JSON look like this:

    ```json
    {
        "genres": [
            {
                "genre": "genre 1",
                "score": 25,
                "difference": null
            },
            {
                "genre": "genre 2",
                "score": 15,
                "difference": null
            },
            {
                "genre": "genre 3",
                "score": 10,
                "difference": null
            },
            {
                "genre": "genre 4",
                "score": 10,
                "difference": null
            },
            {
                "genre": "genre 5",
                "score": 10,
                "difference": null
            },
            {
                "genre": "genre 6",
                "score": 10,
                "difference": null
            },
            {
                "genre": "genre 7",
                "score": 10,
                "difference": null
            },
            {
                "genre": "genre 8",
                "score": 10,
                "difference": null
            }
        ],
        "range": "short"
    }
    ```


- **`/api/recently`**: This GET endpoint returns user's recently played tracks on Spotify.
  - Response JSON look like this:

    ```json
    {
        "total": 50,
        "items": [
            {
                "track": {
                    "album": {
                        "images": [
                            {
                                "url": "URL",
                                "height": 100,
                                "width": 100
                            }
                        ],
                        "name": "track",
                        "genres": null
                    },
                    "artists": [
                        {
                            "name": "artist"
                        }
                    ],
                    "name": "track",
                    "external_urls": {
                        "spotify": "Spotify track URL"
                    }
                },
                "played_at": "2020-01-01T01:00:00.000Z"
            }
        ]
    }
    ```

- **`/api/score`**: This GET endpoint returns user's mainstream score on Spotify based on a time range specified in the request parameter.
  - Response JSON look like this:

    ```json
    {
        "score": 78.8,
        "range": "short",
        "difference": 0
    }
    ```


- **`/api/playlist/create`**: This POST endpoint creates a new playlist on the user's Spotify account containing the user's top tracks based on a time range specified in the request parameter.
  - Header of this request should contain valid CSRF token which is generated inside REST API.
  - Response JSON look like this:

    ```json
    {
        "id": "playlist ID",
        "external_urls": {
            "spotify": "Spotify playlist URL"
        }
    }
    ```

## Exception handling

The [ResponseExceptionHandler](../src/main/java/com/laa66/statlyapp/aspect/ResponseExceptionHandler.java) class is used for handling exceptions in the REST API.

It contains methods for handling various exceptions that may occur during the execution of the API, such as `UserAuthenticationException`, `ClientAuthorizationException`, `HttpClientErrorException`, `HttpServerErrorException`, `UserNotFoundException` and `SpotifyAPIException`.

The methods are annotated with `@ExceptionHandler` and `@ResponseStatus` to handle the specific exceptions and return the appropriate HTTP status code and response entity with an `ExceptionDTO` object containing the error message, status code, and timestamp which look like this:

```json
{
  "status": 400,
  "message": "Spotify API error",
  "timestamp": 1681477674363
}
```

This class extends the `ResponseEntityExceptionHandler` class from Spring's MVC framework, which provides a default implementation for handling common exceptions. By extending this class, the `ResponseExceptionHandler` can handle additional exceptions specific to the application.