# Spring Social

An API-based social media where users can post, comment, send friend requests and, receive notifications written using
the Spring Boot framework.

## Architecture

![architecture](./docs/assets/images/infrastructure.png)

## Installation

1. Install [IntelliJ IDEA](https://www.jetbrains.com/idea/download) (*optional*)
2. Install Docker Desktop
   ([Windows](https://docs.docker.com/desktop/install/windows-install/),
   [Mac](https://docs.docker.com/desktop/install/mac-install/),
   [Linux](https://docs.docker.com/desktop/install/linux-install/))
3. Install [Postman](https://www.postman.com/downloads/)
4. Clone the repository
   ```shell
   git clone https://github.com/siomao/spring-social.git
   ```

## Running

1. Navigate to the folder repository
   ```shell
   cd spring-social
   ```
2. Build docker images and run them as docker containers
   ```shell
   docker-compose -p spring-social -f docker-compose.yml up -d
   ```

## Testing

1. Open Postman
2. Locate Postman collection using relative path `./postman/spring-social.postman_collection.json`
3. Drag and drop Postman collection to the Postman Desktop
4. Retrieve credentials to authorize requests via API gateway with OAuth2.0 using `Keycloak`
    1. Navigate to `Keycloak` at <http://localhost:8080/auth>
    2. Retrieve `token_endpoint`
       from [OpenID Endpoint Configuration](http://localhost:8080/auth/realms/spring-social-realm/.well-known/openid-configuration)
    3. Under the [Clients](http://localhost:8080/auth/admin/master/console/#/realms/spring-social-realm/clients)
       navigation item on the left-hand side, click on `spring-social-client` to retrieve
       the `Client ID`
    4. Under Credentials tab, retrieve `Client Secret`
5. Authenticate requests using Postman Authorization Tab...

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

| Api endpoint           | Method | Description                          |
|------------------------|:------:|--------------------------------------|
| `?userId={{userId}}`   |  GET   | Retrieves posts                      |
| `N/A`                  |  POST  | Creates a post                       |
| `/{{postId}}`          |  PUT   | Updates a post                       |
| `/{{postId}}`          | DELETE | Removes a post                       |
| `/{{postId}}`          |  GET   | Retrieves a post                     |
| `/{{postId}}/comments` |  GET   | Retrieves a post with their comments |

### Comment Service `/api/comments`

| Api endpoint                           | Method | Description         |
|----------------------------------------|:------:|---------------------|
| `?userId={{userId}}&postId={{postId}}` |  GET   | Retrieves comments  |
| `N/A`                                  |  POST  | Creates a comment   |
| `/{{commentId}}`                       |  PUT   | Updates a comment   |
| `/{{commentId}}`                       | DELETE | Removes a comment   |
| `/{{commentId}}`                       |  GET   | Retrieves a comment |

### Friendship Service `/api/friendships`

| Api endpoint                     | Method | Description                             |
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

## Authors

* Jerome Delos Reyes
* Jayden Nguyen