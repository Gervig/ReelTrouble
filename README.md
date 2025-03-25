# ReelTrouble
 A REST API that suggests movies based on user preferences.


# Endpoint Table

| Endpoints            | Method   | Secured      | Description                               |
|:---------------------|:---------|:------------:|:------------------------------------------|
| auth/register       | POST     | ❌          | Create a new user                         |
| auth/login         | POST     | ❌          | Auth a user, return JWT token             |
| movies/recommend   | GET      | ✅          | Shows recommendations based on user likes |
| movies/like/{id}   | POST     | ✅          | User likes a movie and preference is stored |
| movies/history     | GET      | ✅          | Shows a user’s liked movies               |
| admin/movies/add   | POST     | ✅ (admin)  | Admins can add new movies to the DB       |
| movies/random      | GET      | ✅          | Shows a random movie not liked by user    |
