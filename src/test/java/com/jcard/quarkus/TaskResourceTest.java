package com.jcard.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class TaskResourceTest {

    @Inject
    TaskRepository taskRepository;

    private Task testTask;

    @BeforeEach
    @Transactional
    void setUp() {
        taskRepository.deleteAll();
        testTask = new Task("Test task", false);
        taskRepository.persist(testTask);
    }

    @Test
    void shouldGetTasks() {
        given()
          .when()
                .get("/tasks")
          .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[0].title", equalTo("Test task"))
                .body("[0].completed", equalTo(false));
    }


    @Test
    void shouldGetTasksById() {
        given()
                .when()
                .get("/tasks/{id}", testTask.getId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(testTask.getId().intValue()))
                .body("title", equalTo("Test task"))
                .body("completed", equalTo(false));
    }

    @Test
    void shouldReturnNotFoundWhenTaskDoesNotExist() {
        given()
                .when()
                .get("/tasks/999")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldCreateTask() {
        String requestBody = """
        {
            "title": "New task",
            "completed": false
        }
        """;

        Integer createdId = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/tasks")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("title", equalTo("New task"))
                .body("completed", equalTo(false))
                        .extract()
                                .path("id");

        given()
                .when()
                .get("/tasks/{id}", createdId)
                .then()
                .statusCode(200)
                .body("id", equalTo(createdId))
                .body("title", equalTo("New task"))
                .body("completed", equalTo(false));
    }

    @Test
    void shouldUpdateTask() {
        String requestBody = """
        {
            "title": "Updated task",
            "completed": true
        }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/tasks/{id}", testTask.getId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(testTask.getId().intValue()))
                .body("title", equalTo("Updated task"))
                .body("completed", equalTo(true));

        given()
                .when()
                .get("/tasks/{id}", testTask.getId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(testTask.getId().intValue()))
                .body("title", equalTo("Updated task"))
                .body("completed", equalTo(true));
    }

    @Test
    void shouldDeleteTask() {
        given()
                .when()
                .delete("/tasks/{id}", testTask.getId())
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/tasks/{id}", testTask.getId())
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingTask() {
        String requestBody = """
        {
            "title": "Updated task",
            "completed": true
        }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/tasks/{id}", 999L)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingTask() {
        given()
                .when()
                .delete("/tasks/{id}", 999L)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldRejectTaskWithProvidedId() {
        String requestBody = """
        {
            "id": 9999,
            "title": "Generated ID task",
            "completed": false
        }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/tasks")
                .then()
                .statusCode(400);
    }

}
