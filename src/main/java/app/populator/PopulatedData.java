package app.populator;

import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;

import java.util.List;

// Custom wrapper class to return all entities together
public class PopulatedData {
    public final Movie[] movies;
    public final Actor[] actors;
    public final Director[] directors;
    public final Genre[] genres;

    public PopulatedData(Movie[] movies, Actor[] actors, Director[] directors, Genre[] genres) {
        this.movies = movies;
        this.actors = actors;
        this.directors = directors;
        this.genres = genres;
    }
}
