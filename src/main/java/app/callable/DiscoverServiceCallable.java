package app.callable;

import app.services.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

// API reference
// https://developer.themoviedb.org/reference/discover-movie

public class DiscoverServiceCallable implements Callable<List<String>>
{
    String pageNum;

    public DiscoverServiceCallable(String pageNum)
    {
        this.pageNum = pageNum;
    }

    @Override
    public List<String> call() throws Exception
    {
        List<String> movie_api_ids = Service.fetchMoviesFromPage(pageNum);
        return movie_api_ids;
    }

    public static List<String> getMovieApiIds(int totalPages)
    {
        List<Future<List<String>>> futureList = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(10); // increase for faster performance, beware of overload

        for (int i = 1; i <= totalPages; i++)
        {
            Callable<List<String>> task = new DiscoverServiceCallable(String.valueOf(i));
            Future<List<String>> future = executorService.submit(task);
            futureList.add(future);
        }

        List<String> movieApiIDs = new ArrayList<>();

        for (Future<List<String>> movieApiIdsFuture : futureList)
        {
            try
            {
                List<String> finishedTask = movieApiIdsFuture.get();
                movieApiIDs.addAll(finishedTask);
            } catch (InterruptedException | ExecutionException e)
            {
                System.err.println("Error retrieving API IDs for movies: " + e.getMessage());
                e.printStackTrace();
            }
        }

        executorService.shutdown();
        return movieApiIDs;
    }
}
