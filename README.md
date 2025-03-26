# ReelTrouble
A REST API that suggests movies based on user preferences.


# Endpoint Table

| Endpoints            | Method   | Secured      | Description                                 |
|:---------------------|:---------|:------------:|:--------------------------------------------|
| api/auth/register    | POST     | ❌          | Create a new user                           |
| api/auth/login       | POST     | ❌          | Auth a user, return JWT token               |
| api/movies           | GET      | ✅          | Shows all movies                            |
| api/movies/{id}      | GET      | ✅          | Shows a movie with a given ID               |
| api/movies/recommend | GET      | ✅          | Shows recommendations based on user likes   |
| api/movies/like/{id} | POST     | ✅          | User likes a movie and preference is stored |
| api/movies/history   | GET      | ✅          | Shows a user’s liked movies                 |
| api/admin/movies/add | POST     | 🔒          | Admins can add new movies to the DB         |
| api/movies/random    | GET      | ✅          | Shows a random movie not liked by user      |

❌ = Not secured

✅ = User secured

🔒 = Admin secured
