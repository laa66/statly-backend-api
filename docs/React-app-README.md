# React App documentation

## Overview

Architecture of application follows client-server model.

Frontend of application is implemented using React and RESTful Backend app using Spring Boot. React app communicates with Spring Boot app using `fetch API` for making HTTP requests from client to server.

Communication between apps is done over HTTP protocol, each request is independent and doesn’t rely on previous requests. Backend handles HTTP requests from frontend, processes the requests and return `JSON` responses.

The API is designed using REST principles, using HTTP methods `GET, POST, DELETE` to perform operations on resources identified by `URLs`. The endpoints in Spring Boot application handle these HTTP methods, perform operations on resources and return `JSON` responses to React frontend.

You can also check [Spring REST API documentation](REST-API-README.md) for more information about backend architecture.

## Deployment

Application is deployed on [Render.com](http://Render.com) Cloud provider with combination of static site deployment for the frontend React app and Dockerfile-based deployment for the Spring Boot backend app.

Frontend application involves integrating GitHub repository with Render Cloud, which allows for automated deployments whenever changes are pushed to the repository. React-app is served from content delivery network (CDN) that caches and delivers content to users from a server that is closest to them.

Backend application is deployed as a web service on cloud with help of Dockerfile script which contains instructions for building a Docker image. This Docker image is then deployed as web service which runs the backend app in a containerized environment with all its dependencies and configurations.

You can also check [instructions ](../README.md) on how to set up and run the application locally.

## React Components

### index.js

This React component serves as the entry point for the frontend application, setting up the necessary environment and rendering the `root` component of the application using `ReactDOM.createRoot()`and `root.render()`. It also includes necessary imports for CSS, JS, routing dependencies, and wraps the `App`component in a `React**.**StrictMode`component for enhanced runtime checks.

### App.js

`App` component serves as the main container for rendering `body` and `footer` components in React app. It defines layout of the application, applies CSS classes and return JSX code.

### config.js

This file provides dynamic configuration of API endpoint URL based on current environment.

### footer.js

`Footer` component renders a footer section with links, social media icons and copyrights information. It’s used in every part of the application to display footer section.

### body.js

This component serves as the container for rendering different sections of app based on URL path like:

- Dashboard
- Tracks section
- Artists section
- Genres section
- Mainstream score section
- Recently played section
- Account settings

It includes a `header` component that is rendered on top of each section and `PrivateRoute` component to protect routes.

### PrivateRoute.js

This component protect routes that should be only accessible to authenticated users. It takes children prop which represents content that should be rendered inside the protected route. The component uses `localStorage` to check if user is logged in. If `userLogged` item in storage is null or false it redirects user to the root path `/` using `Navigate` component. If user is logged in, the protected content from prop is rendered.

### header.js

Header component is divided into two parts: `Header` and `HeaderLogged` which are conditionally rendered based on whether user is logged in or not.

The `Header` component renders a header for home page wrapped in `Link` component from React Router which navigates to home page.

The `HeaderLogged` component renders a header with username, user profile both retrieved from the `localStorage` object which is used to store data in browser. This component also contains a navigation menu with links to different sections of the web application and dropdown menu with `account` section. It is only rendered after successful user authentication inside Spring Boot app.

### home.js

This React component for a home page of Statly app interacts with REST API. It contains description of app features, login button that redirects to the backend authentication endpoint.

The `Home` component also contains join beta section which makes it possible to register for app testing. After filling form this component is sending user data to the backend endpoint.

### callback.js

This component handles `callback` after a successful user authentication process inside Spring Boot app and can be accessed with `/callback` link. It uses `useEffect` hook from React to check if values are present in the `localStorage` and sets them using values obtained from the URL parameters. Finally it renders a `Navigate` component to navigate to the `/dashboard` React route.

### dashboard.js

The `Dashboard` component renders plain dashboard view for app and container which describes application functionality.

### account.js

The `Account` component renders page with UI for managing Spotify account settings and deleting current user account in Statly app.

After clicking on “Continue to Spotify” section user is redirected to the external account settings page connected with their Spotify account.

If user chooses to delete their account by clicking on “I want to delete my Statly account” the `handleDelete` function will take care of deleting user account in the backend by calling `deleteAccount` function defined in `deleteAccount.js` file and logging out user by calling `logOut` function from `logOut.js` component.

### track.js

This component renders a panel containing a list of tracks with ability to switch between different time ranges such as 4 weeks, 6 months and all the time), and option to create and export favourite tracks playlist to Spotify account. Active time range is controlled by the `active` state that uses `useState` hook.

It uses `Image` and `List.TrackList` components for rendering the track list and all track data. Can be accessed with `/track/top` route. Fetching data from API is done with help of React `useEffect` hook.

### artist.js

The `Artist` component renders a panel containing list of favourite artists with ability to switch between different time ranges like in the `track` component and is accessed with `/artist/top` route.

It also uses `List.ArtistList` component to render appropriate type of component for showing user artists.

### genre.js

Very similar component to the `artist` and `track` . It fetches and renders data in different time ranges related to user most listened genres with help of `List.GenreList` component. Can be accessed with `/genre/top` route.

### mainstream.js

Another component similar to previous, it fetches and renders data in different time ranges related to user mainstream score using `List.Mainstream` and `Image`component. Can be accessed with `/user/score` route.

### history.js

This component fetches and displays user recently played tracks in Spotify player with use of `useState` ,`useEffect` hooks and renders `List.HistoryList` component to display interactive list of 50 recently listened tracks. Can be accessed with `/user/history` route.

### list.js

List.js file defines multiple functional components for displaying different types of lists. All components takes prop which contains data that should be displayed to the user.

- `TrackList` renders a list of tracks as a table.
- `ArtistList` renders a list of artists as a grid of cards.
- `GenreList` renders a bar chart for displaying user favourite genres percentage.
- `Mainstream` displays a score in score container.
- `HistoryList` renders a list of tracks played history as a table.

## Data exchange between React App and Spring Boot REST API

React app is using Fetch API for exchanging data with REST API. It also includes appropriate cookies in requests to the API.

The backend processes data - calling Spotify API, MySQL database and doing all required operations before returning result. After successful mapping backend endpoints return JSON data containing requested information to the React application.

GET requests using Fetch API are very similar to this:

```jsx
export const fetchArtistShort = () =>  fetch(url + '/api/top/artists?range=short', {
    method: 'GET',              //define HTTP method
    credentials: 'include'      //include credentials inside cookies
}).then((response) => response.json());
```
<br>

POST, DELETE requests also include a CSRF token in header:

```jsx
export const postTrackShort = () => fetch(url + '/api/playlist/create?range=short', {
    method: 'POST',             //define HTTP method
    credentials: 'include',     //include credentials inside cookies
    headers: {
        'X-XSRF-TOKEN': csrfToken //include CSRF token for protection
    }
}).then((response) => response.json());
```

All sample REST API responses are listed in the backend [documentation](./REST-API-README.md).

## Security

Authentication with Spring REST API is using a combination of Basic Authentication with JSESSIONID cookie and Cross-Site Request Forgery (CSRF) protection with CSRF token for `POST` and `DELETE` requests.

REST API authenticates user with Spotify services after user clicks “Login with Spotify” button on home page, and then sends back JSESSIONID cookie and CSRF token to the user’s browser.

JSESSIONID session Cookie is send in every request to the backend to authenticate client. CSRF token is send in every request which may change the state of the server like `POST`, `PUT` or `DELETE`.

Also Cross-Origin Resource Sharing (CORS) is configured in Spring REST API to only allow requests from React App to access the backend.

Click [here](../README.md) to go to the README file.