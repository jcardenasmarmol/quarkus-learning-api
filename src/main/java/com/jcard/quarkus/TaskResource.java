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
    public List<TaskResponse> getTasks() {
        return taskService.getTasks()
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTaskById(@PathParam("id") Long id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(new TaskResponse(task)).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTask(@Valid TaskCreateRequest request) {

        Task createdTask = taskService.addTask(new Task(request.getTitle(), request.isCompleted()));

        return Response.status(Response.Status.CREATED)
                .entity(new TaskResponse(createdTask))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTask(@PathParam("id") Long id, @Valid TaskUpdateRequest  request) {
        Task updatedTask = taskService.updateTask(id,
                new Task(request.getTitle(), request.isCompleted())
        );

        if (updatedTask == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(new TaskResponse(updatedTask)).build();
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
