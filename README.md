<div align="center">
  <img src="docs/reel_trouble.png" alt="Alt text" width="200">
</div>

# ReelTrouble

An API that suggests movies based on user preferences.
A user can like movies, and therefore make sure the movies that are suggested, aren't movies they have watched before.
A user can choose to get a movie suggestion within a specific genre or randomly.
Anyone can choose to get a movie suggestion within a specific genre, but here their like list is not included.

# Endpoint Table

| Endpoints                                | Method | Secured | Description                                 |
|:-----------------------------------------|:-------|:-------:|:--------------------------------------------|
| api/auth/register                        | POST   |    ❌    | Create a new user                           |
| api/auth/login                           | POST   |    ❌    | Auth a user, return JWT token               |
| api/movies                               | GET    |    ❌    | Shows all movies                            |
| api/movies/movie/{id}                    | GET    |    ❌    | Shows a movie with a given ID               |
| api/movies/genre/{genre}                 | GET    |    ❌    | Shows movies with a given genre name        |
| api//movies/recommend/{genre}/{username} | GET    |    ✅    | Shows recommendations based on user likes   |
| api/movies/random-movie/{genre}          | GET    |    ❌    | Shows random movie based on genre           |
| api/movies/like/{username}/{movieId}     | POST   |    ✅    | User likes a movie and preference is stored |
| api/movies/history/{username}            | GET    |    ✅    | Shows a user’s liked movies                 |
| api/admin/movies/add                     | POST   |   🔒    | Admins can add new movies to the DB         |
| api/movies/random/{id}                   | GET    |    ✅    | Shows a random movie not liked by user      |

❌ = Not secured

✅ = User secured

🔒 = Admin secured

Deployed version: https://reeltrouble.dataduck.dk/api/routes
