# ReelTrouble
A REST API that suggests movies based on user preferences.


# Endpoint Table

| Endpoints                         | Method   | Secured      | Description                                 |
|:----------------------------------|:---------|:------------:|:--------------------------------------------|
| api/auth/register                 | POST     | ❌          | Create a new user                           |
| api/auth/login                    | POST     | ❌          | Auth a user, return JWT token               |
| api/movies                        | GET      | ❌          | Shows all movies                            |
| api/movies/movie/{id}             | GET      | ❌          | Shows a movie with a given ID               |
| api/movies/genre/{genre}          | GET      | ❌          | Shows movies with a given genre name        |
| api/movies/recommend/{genre}/{id} | GET      | ✅          | Shows recommendations based on user likes   |
| api/movies/like/{id}/{movieId}    | POST     | ✅          | User likes a movie and preference is stored |
| api/movies/history/{id}           | GET      | ✅          | Shows a user’s liked movies                 |
| api/admin/movies/add              | POST     | 🔒          | Admins can add new movies to the DB         |
| api/movies/random/{id}            | GET      | ✅          | Shows a random movie not liked by user      |

❌ = Not secured

✅ = User secured

🔒 = Admin secured
