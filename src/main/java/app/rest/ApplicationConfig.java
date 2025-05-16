package app.rest;

import app.controllers.ISecurityController;
import app.controllers.impl.SecurityController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

import static io.javalin.apibuilder.ApiBuilder.path;

public class ApplicationConfig
{
    private static ApplicationConfig applicationConfig;
    private ISecurityController securityController = new SecurityController();
    private static Javalin app;
    private static JavalinConfig javalinConfig;
    private ObjectMapper objectMapper = new ObjectMapper();

    // singleton, private constructor
    private ApplicationConfig()
    {
    }

    public static ApplicationConfig getInstance()
    {
        if (applicationConfig == null)
        {
            applicationConfig = new ApplicationConfig();
        }
        return applicationConfig;
    }

    public ApplicationConfig initiateServer()
    {
        app = Javalin.create(config ->
        {
            // saves the config to a variable so we can add to it later
            javalinConfig = config;
            // sets the default content to json
            config.http.defaultContentType = "application/json";
            // "/api" will be prefixed to all routes
            config.router.contextPath = "/api";
            config.bundledPlugins.enableRouteOverview("/routes");
            config.bundledPlugins.enableDevLogging();
        });
        return applicationConfig;
    }

    public ApplicationConfig securityCheck()
    {
        app.beforeMatched(securityController.authenticate());
        app.beforeMatched(securityController.authorize());
        return applicationConfig;
    }

    public ApplicationConfig setRoute(EndpointGroup route)
    {
        javalinConfig.router.apiBuilder(route); // No need for path("")
        return applicationConfig;
    }

    public ApplicationConfig startServer(int port)
    {
        app.before(ApplicationConfig::corsHeaders);
        app.options("/*", ApplicationConfig::corsHeadersOptions);
        app.start(port);
        return applicationConfig;
    }

    public ApplicationConfig handleException()
    {
        app.exception(Exception.class, (e, ctx) ->
        {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("msg", e.getMessage());
            ctx.status(500);
            ctx.json(node);
        });
        return applicationConfig;
    }

    public static void stopServer()
    {
        app.stop();
        app = null;
    }

    private static void corsHeaders(Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ctx.header("Access-Control-Allow-Credentials", "true");
    }

    private static void corsHeadersOptions(Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ctx.header("Access-Control-Allow-Credentials", "true");
        ctx.status(204);
    }


}
