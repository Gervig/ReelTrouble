package app.populator;

import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import app.populator.PopulatedData;

import java.time.LocalDate;
import java.util.*;

public class GlobalPopulator {

    public static PopulatedData populate() {
        // Create Directors
        Director d1 = Director.builder().name("Director 1").build();
        Director d2 = Director.builder().name("Director 2").build();

        // Create Genres
        Genre g1 = Genre.builder().name("Action").build();
        Genre g2 = Genre.builder().name("Comedy").build();
        Genre g3 = Genre.builder().name("Drama").build();

        // Create Movies and Assign Directors & Genres
        Movie m1 = Movie.builder()
                .title("Movie1")
                .movieApiId(678L)
                .releaseDate(LocalDate.now())
                .directors(new HashSet<>(Set.of(d1)))
                .genres(new HashSet<>(Set.of(g1, g2)))
                .build();

        Movie m2 = Movie.builder()
                .title("Movie2")
                .movieApiId(456L)
                .releaseDate(LocalDate.now().minusYears(3))
                .directors(new HashSet<>(Set.of(d1, d2)))
                .genres(new HashSet<>(Set.of(g2, g3)))
                .build();

        Movie m3 = Movie.builder()
                .title("Movie3")
                .movieApiId(789L)
                .releaseDate(LocalDate.now().minusYears(1))
                .directors(new HashSet<>(Set.of(d2)))
                .genres(new HashSet<>(Set.of(g1, g3)))
                .build();

        Movie[] movies = new Movie[]{m1, m2, m3};

        // Create Actors and Assign Movies
        Actor a1 = Actor.builder().name("Actor 1").build();
        Actor a2 = Actor.builder().name("Actor 2").build();
        Actor a3 = Actor.builder().name("Actor 3").build();

        a1.setMovies(new HashSet<>(Set.of(m1, m2)));
        a2.setMovies(new HashSet<>(Set.of(m2, m3)));
        a3.setMovies(new HashSet<>(Set.of(m3)));

        m1.setActors(new HashSet<>(Set.of(a1)));
        m2.setActors(new HashSet<>(Set.of(a1, a2)));
        m3.setActors(new HashSet<>(Set.of(a2, a3)));

        // Assign movies to directors
        d1.setMovies(new HashSet<>(Set.of(m1, m2)));
        d2.setMovies(new HashSet<>(Set.of(m3)));

        // Assign movies to genres
        g1.setMovies(new HashSet<>(Set.of(m1, m3)));
        g2.setMovies(new HashSet<>(Set.of(m1, m2)));
        g3.setMovies(new HashSet<>(Set.of(m2, m3)));

        // Wrap all data into a single object
        return new PopulatedData(movies, new Actor[]{a1, a2, a3}, new Director[]{d1, d2}, new Genre[]{g1, g2, g3});
    }

}