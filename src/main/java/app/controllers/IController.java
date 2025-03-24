package app.controllers;

import app.exceptions.ApiException;
import io.javalin.http.Context;

import java.util.List;

public interface IController<T, I>
{
    List<T> getAll();
    T getById(I id);
}
