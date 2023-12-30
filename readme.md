# Spring Social

An API-based social media where users can post, comment, send friend requests and, receive notifications regarding
friend requests written using the Spring Boot framework.

## Architecture

![architecture](./docs/assets/images/infrastructure.png)

## Installation

1. Download and Install [IntelliJ IDEA](https://www.jetbrains.com/idea/download) (*optional*).
2. Download and Install Docker Desktop on
   [Windows](https://docs.docker.com/desktop/install/windows-install/),
   [Mac](https://docs.docker.com/desktop/install/mac-install/), or
   [Linux](https://docs.docker.com/desktop/install/linux-install/).
3. Download and Install [Postman](https://www.postman.com/downloads/).
4. Clone the repository.
   ```shell
   git clone https://github.com/jdelreyes/spring-social.git
   ```

## Running

1. Navigate to the folder repository.
   ```shell
   cd spring-social
   ```
2. Build docker images and run them as docker containers.
   ```shell
   docker-compose -p spring-social -f docker-compose.yml up -d
   ```
3. Add keycloak hostname with a port value of 127.0.0.1 to your machine's host file so that authorization requests are
   mapped to dockerized keycloak. Restart your machine if necessary.

## Testing

1. Open Postman
2. Locate Postman collection at `./postman/spring-social.postman_collection.json`
3. Drag and drop the Postman collection to the Postman Desktop
4. Retrieve credentials to authorize requests via API gateway with OAuth2.0
    1. Navigate to Keycloak webpage at <http://localhost:8080/auth>
    2. Login with `admin` username and `password` password credentials
    3. Retrieve `token_endpoint`
       from [OpenID Endpoint Configuration](http://localhost:8080/auth/realms/spring-social-realm/.well-known/openid-configuration)
    4. Under the [Clients](http://localhost:8080/auth/admin/master/console/#/realms/spring-social-realm/clients)
       navigation item on the left-hand side, click on `spring-social-client` to retrieve
       the `Client ID`
    5. Under Credentials tab, retrieve `Client Secret`
5. Authenticate requests using Postman Authorization Tab
    1. Under Authentication tab, choose **OAuth 2.0** type from the dropdown
    2. Configure a new token with the following configuration options
        * **Token Name**: spring-social-client
        * **Grant Type**: Client Credentials
        * **Access Token URL**: <http://keycloak:8080/auth/realms/spring-social-realm/protocol/openid-connect/token>
        * **Client ID**: spring-social-client
        * **Client Secret**: *the `Client Secret` retrieved from 4.iv step*
    3. Click on **Get New Access Token** button
6. Have fun :)

## API Endpoints

Request to these endpoints are passed through an API gateway which is authorized by `Keycloak`

### User Service `/api/users`

| Endpoint               | Method | Description                          |
|------------------------|:------:|--------------------------------------|
| `N/A`                  |  GET   | Retrieves users                      |
| `N/A`                  |  POST  | Creates a user                       |
| `/{{userId}}`          |  PUT   | Updates a user                       |
| `/{{userId}}`          | DELETE | Removes a user                       |
| `/{{userId}}`          |  GET   | Retrieves a user                     |
| `/{{userId}}/posts`    |  GET   | Retrieves a user with their posts    |
| `/{{userId}}/comments` |  GET   | Retrieves a user with their comments |

### Post Service `/api/posts`

| Endpoint               | Method | Description                          |
|------------------------|:------:|--------------------------------------|
| `?userId={{userId}}`   |  GET   | Retrieves posts                      |
| `N/A`                  |  POST  | Creates a post                       |
| `/{{postId}}`          |  PUT   | Updates a post                       |
| `/{{postId}}`          | DELETE | Removes a post                       |
| `/{{postId}}`          |  GET   | Retrieves a post                     |
| `/{{postId}}/comments` |  GET   | Retrieves a post with their comments |

### Comment Service `/api/comments`

| Endpoint                               | Method | Description         |
|----------------------------------------|:------:|---------------------|
| `?userId={{userId}}&postId={{postId}}` |  GET   | Retrieves comments  |
| `N/A`                                  |  POST  | Creates a comment   |
| `/{{commentId}}`                       |  PUT   | Updates a comment   |
| `/{{commentId}}`                       | DELETE | Removes a comment   |
| `/{{commentId}}`                       |  GET   | Retrieves a comment |

### Friendship Service `/api/friendships`

| Endpoint                         | Method | Description                             |
|----------------------------------|:------:|-----------------------------------------|
| `N/A`                            |  GET   | Retrieves friendships                   |
| `/send`                          |  POST  | Sends a friend request                  |
| `/accept`                        |  PUT   | Accepts a friend request                |
| `/reject`                        |  PUT   | Rejects a friend request                |
| `/{{friendshipId}}`              |  GET   | Retrieves a friendship                  |
| `/user/{{userId}}/accepted-list` |  GET   | Retrieves a user's friend list          |
| `/user/{{userId}}/rejected-list` |  GET   | Retrieves a user's rejected friend list |
| `/user/{{userId}}/pending-list`  |  GET   | Retrieves a user's pending friend list  |

### Notification Service `/api/notifications`

| Endpoint             | Method | Description                                                      |
|----------------------|:------:|------------------------------------------------------------------|
| `?userId={{userId}}` |  GET   | Retrieves friendship, post, and comment notifications for a user |

## Authors

* Jerome Delos Reyes
* Jayden Nguyen
