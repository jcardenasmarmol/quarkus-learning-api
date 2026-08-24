package com.jcard.quarkus;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import java.util.List;

@Path("/tasks")
public class TaskResource {

    @Inject
    TaskService taskService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get all tasks",
            description = "Returns all tasks"
    )
    @APIResponse(
            responseCode = "200",
            description = "Tasks retrieved successfully"
    )
    public List<TaskResponse> getTasks() {
        return taskService.getTasks()
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get task by ID",
            description = "Returns a task by its ID"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Task retrieved successfully"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Task not found"
            )
    })
    public Response getTaskById(
            @Parameter(
                    description = "Task ID",
                    required = true
            )
            @PathParam("id") Long id
    ) {
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
    @Operation(
            summary = "Create task",
            description = "Creates a new task"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Task created successfully"
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid request"
            )
    })
    public Response createTask(
            @RequestBody(
                    description = "Task to create",
                    required = true
            )
            @Valid TaskCreateRequest request
    ) {

        Task createdTask = taskService.addTask(new Task(request.getTitle(), request.isCompleted()));

        return Response.status(Response.Status.CREATED)
                .entity(new TaskResponse(createdTask))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Update task",
            description = "Updates an existing task"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Task updated successfully"
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid request"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Task not found"
            )
    })
    public Response updateTask(
            @Parameter(
                    description = "Task ID",
                    required = true
            )
            @PathParam("id") Long id,
            @RequestBody(
                    description = "Updated task data",
                    required = true
            )
            @Valid TaskUpdateRequest request
    ) {
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
    @Operation(
            summary = "Delete task",
            description = "Deletes an existing task"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Task deleted successfully"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Task not found"
            )
    })
    public Response deleteTask(
            @Parameter(
                    description = "Task ID",
                    required = true
            )
            @PathParam("id") Long id
    ) {
        boolean isDeleted = taskService.deleteTask(id);

        if (isDeleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
