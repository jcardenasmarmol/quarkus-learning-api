package com.jcard.quarkus;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Path("/tasks")
public class TaskResource {

    @Inject
    TaskService taskService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getTasks() {
        return taskService.getTasks();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTaskById(@PathParam("id") Long id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(task).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTask(@Valid Task task) {

        if (task.getId() != null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Task createdTask = taskService.addTask(task);

        return Response.status(Response.Status.CREATED)
                .entity(createdTask)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTask(@PathParam("id") Long id, @Valid Task updatedTask) {
        Task task = taskService.updateTask(id, updatedTask);

        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(task).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTask(@PathParam("id") Long id) {
        boolean isDeleted = taskService.deleteTask(id);

        if (isDeleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
