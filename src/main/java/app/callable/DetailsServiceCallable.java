package app.callable;

import app.dtos.MovieDTO;
import app.services.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

// API reference
// https://developer.themoviedb.org/reference/movie-details

public class DetailsServiceCallable implements Callable<MovieDTO>
{
    String movieId;

    public DetailsServiceCallable(String movieId)
    {
        this.movieId = movieId;
    }

    @Override
    public MovieDTO call() throws Exception
    {
        MovieDTO movieDTO = Service.getDataFromApiId(movieId);
        return movieDTO;
    }

    public static List<MovieDTO> getMovieDTOs(List<String> movieApiIds)
    {
        List<Future<MovieDTO>> futureList = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(10); // increase for faster performance, beware of overload

        for (String movieApiId : movieApiIds)
        {
            Callable<MovieDTO> task = new DetailsServiceCallable(movieApiId);
            Future<MovieDTO> future = executorService.submit(task);
            futureList.add(future);
        }
        List<MovieDTO> movieDTOS = new ArrayList<>();

        for (Future<MovieDTO> movieDTOFuture : futureList)
        {
            try
            {
                MovieDTO finishedTask = movieDTOFuture.get();
                if (finishedTask != null)
                {
                    movieDTOS.add(finishedTask);
                }
            } catch (InterruptedException | ExecutionException e)
            {
                System.err.println("Error retrieving data from movie: " + e.getMessage());
                e.printStackTrace();
            }
        }

        executorService.shutdown();
        return movieDTOS;
    }
}
