package app.services;

import app.callable.DiscoverServiceCallable;
import app.daos.impl.MovieDAO;
import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Service
{
    //API_reader
    private final MovieDAO movieDAO;
    static String api_key = System.getenv("api_key");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    //MovieService
    private static final HttpClient client = HttpClient.newHttpClient();

    public Service(MovieDAO movieDAO)
    {
        this.movieDAO = movieDAO;
    }

    public static List<String> getMovieApiIds()
    {
        List<String> movieApiIds = new ArrayList<>();
        String currentPage = "1";

        try
        {
            // Make the first request to get the total pages value
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(buildDiscoverUri(currentPage)))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200)
            {
                JsonNode rootNode = objectMapper.readTree(response.body());
                int totalPages = rootNode.path("total_pages").asInt();  // Get the total pages value

                movieApiIds = DiscoverServiceCallable.getMovieApiIds(totalPages);

            } else
            {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return movieApiIds;
    }

    public static List<String> fetchMoviesFromPage(String page)
    {
        List<String> movieIds = new ArrayList<>();
        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(buildDiscoverUri(page)))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200)
            {
                JsonNode rootNode = objectMapper.readTree(response.body());

                // Extract movie IDs from the current page's "results" array
                for (JsonNode movieNode : rootNode.path("results"))
                {
                    movieIds.add(movieNode.path("id").asText());
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return movieIds;
    }

    // Build the URI for a specific page
    public static String buildDiscoverUri(String page)
    {
        String lowerEndDate = String.valueOf(LocalDate.now().minusYears(5));
        String upperEndDate = String.valueOf(LocalDate.now());
        String uri = "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=da&primary_release_date.gte=%%ld&primary_release_date.lte=%%ud&sort_by=popularity.desc&with_original_language=da&api_key=" + api_key + "&page=" + page;
        uri = uri.replace("%%ld", lowerEndDate);
        uri = uri.replace("%%ud", upperEndDate);
        return uri;
    }

    public static MovieDTO getDataFromApiId(String movieApiId)
    {
        String url = "https://api.themoviedb.org/3/movie/%%?append_to_response=credits%2C%20overview&language=da&api_key=" + api_key;
        String movieURL = url.replace("%%", movieApiId);

        try
        {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(new URI(movieURL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Handle rate limit status (429)
            if (response.statusCode() == 429)
            {
                String retryAfterHeader = response.headers().firstValue("Retry-After").orElse("4");
                long retryAfterSeconds = Long.parseLong(retryAfterHeader);

                System.out.println("Rate limit exceeded. Retrying after " + retryAfterSeconds + " seconds.");

                Thread.sleep(retryAfterSeconds * 1000); // Convert seconds to milliseconds

                return getDataFromApiId(movieApiId);  // Retry after waiting
            }


            // If status code is not 429, proceed as usual
            if (response.statusCode() == 200)
            {
                String responseBody = response.body();

                // Parse movie data, including genres, actors, and directors
                MovieDTO movieDTO = parseMovieData(responseBody);
                if (movieDTO != null)
                {
                    // Extract the release_date
                    String releaseDateString = movieDTO.getReleaseDate().toString();  // Ensures we get a valid release date
                    if (releaseDateString == null || releaseDateString.isEmpty())
                    {
                        System.out.println("Skipping movie with ID " + movieApiId + " due to missing release_date.");
                        return null; // Skip the movie if there's no release_date
                    }

                    // Return the populated MovieDTO
                    return movieDTO;
                }
            } else
            {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null; // Return null if there was an issue or invalid data
    }

    private static MovieDTO parseMovieData(String responseBody)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // Extract actors from "credits" -> "cast" where "known_for_department" == "Acting"
            List<ActorDTO> actors = new ArrayList<>();
            JsonNode castNode = rootNode.path("credits").path("cast");
            if (castNode.isArray())
            {
                for (JsonNode actorNode : castNode)
                {
                    if ("Acting".equals(actorNode.path("known_for_department").asText()))
                    {
                        actors.add(new ActorDTO(
                                actorNode.path("id").asLong(),
                                actorNode.path("name").asText()
                        ));
                    }
                }
            }

            // Extract directors from "credits" -> "crew" where "job" == "Director"
            DirectorDTO director = null;
            JsonNode crewNode = rootNode.path("credits").path("crew");
            if (crewNode.isArray())
            {
                for (JsonNode crewMember : crewNode)
                {
                    if ("Director".equals(crewMember.path("job").asText()))
                    {
                        director = new DirectorDTO(
                                crewMember.path("id").asInt(),
                                crewMember.path("name").asText()
                        );
                        break; // Assume one director per movie
                    }
                }
            }

            // Extract genres from "genres"
            List<GenreDTO> genres = new ArrayList<>();
            JsonNode genresArray = rootNode.path("genres");
            for (JsonNode genreNode : genresArray)
            {
                int id = genreNode.path("id").asInt();
                String name = genreNode.path("name").asText();
                genres.add(new GenreDTO(id, name));
            }

            // Extract release_date
            String releaseDateString = rootNode.path("release_date").asText();
            if (releaseDateString == null || releaseDateString.isEmpty())
            {
                System.out.println("Skipping movie due to missing release_date.");
                return null; // Skip the movie if release_date is missing or empty
            }

            // Attempt to parse the release_date
            LocalDate releaseDate = null;
            try
            {
                releaseDate = LocalDate.parse(releaseDateString);
            } catch (DateTimeParseException e)
            {
                System.out.println("Invalid date format: " + releaseDateString);
                return null; // Skip the movie if the date format is invalid
            }

            // Build and return the MovieDTO
            return MovieDTO.builder()
                    .title(rootNode.path("original_title").asText())
                    .description(rootNode.path("overview").asText())
                    .rating(BigDecimal.valueOf(rootNode.path("vote_average").asDouble()))
                    .releaseDate(releaseDate) // Use the safely parsed release date
                    .movieApiID(rootNode.path("id").asInt())
                    .genreDTOs(genres)
                    .actorDTOS(actors)
                    .directorDTO(director)
                    .build();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null; // Return null if parsing fails
    }

}
