package com.jcard.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class TaskResourceTest {

    private List<Task> testTasks;

    private Task createTask(String title, boolean completed) {
        return given()
                .contentType(ContentType.JSON)
                .body(("""
        {
            "title": "%s",
            "completed": %s
        }
        """).formatted(title, completed))
                .when()
                .post("/tasks")
                .then()
                .statusCode(201)
                .extract()
                .as(Task.class);
    }

    @BeforeEach
    void setUp() {
        int tasksSize = given()
                .when()
                .get("/tasks")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getInt("size()");


        for (int i = tasksSize; i < 4; i++) {
            createTask("Test task", false);
        }

        testTasks = given()
                .when()
                .get("/tasks")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList(".", Task.class);
    }

    @Test
    void shouldGetTasks() {
        given()
                .when()
                .get("/tasks")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0));
    }

    @Test
    void shouldGetCompletedTasks() {

        createTask("Test task", true);

        given()
                .when()
                .get("/tasks?completed=true")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0))
                .body("completed", everyItem(equalTo(true)));
    }

    @Test
    void shouldGetIncompleteTasks() {
        createTask("Test task", false);

        given()
                .when()
                .get("/tasks?completed=false")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0))
                .body("completed", everyItem(equalTo(false)));
    }


    @Test
    void shouldGetTasksById() {
        Task testTask = testTasks.getFirst();

        given()
                .when()
                .get("/tasks/{id}", testTask.getId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(testTask.getId().intValue()))
                .body("title", equalTo(testTask.getTitle()))
                .body("completed", equalTo(testTask.isCompleted()));
    }

    @Test
    void shouldReturnNotFoundWhenTaskDoesNotExist() {
        Long nonExistingId = testTasks.getLast().getId() + 9999L;

        given()
                .when()
                .get("/tasks/{id}", nonExistingId)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldCreateTask() {
        String title = "Created test task";
        boolean completed = false;

        Task createdTask = createTask(title,completed);

        given()
                .when()
                .get("/tasks/{id}", createdTask.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(createdTask.getId().intValue()))
                .body("title", equalTo(createdTask.getTitle()))
                .body("completed", equalTo(createdTask.isCompleted()));
    }

    @Test
    void shouldUpdateTask() {
        Task testTask = testTasks.getFirst();
        String title = "Updated test task";
        boolean completed = true;
        String requestBody = ("""
        {
            "title": "%s",
            "completed": %s
        }
        """).formatted(title, completed);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/tasks/{id}", testTask.getId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(testTask.getId().intValue()))
                .body("title", equalTo(title))
                .body("completed", equalTo(completed));

        given()
                .when()
                .get("/tasks/{id}", testTask.getId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(testTask.getId().intValue()))
                .body("title", equalTo(title))
                .body("completed", equalTo(completed));
    }

    @Test
    void shouldDeleteTask() {
        Task testTask = testTasks.getFirst();

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
        Long nonExistingId = testTasks.getLast().getId() + 9999L;
        String title = "Updated test task";
        boolean completed = true;
        String requestBody = ("""
        {
            "title": "%s",
            "completed": %s
        }
        """).formatted(title, completed);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/tasks/{id}", nonExistingId)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingTask() {
        Long nonExistingId = testTasks.getLast().getId() + 9999L;

        given()
                .when()
                .delete("/tasks/{id}", nonExistingId)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldRejectTaskWithProvidedId() {
        String title = "Updated test task";
        boolean completed = false;
        String requestBody = ("""
        {
            "id": 2,
            "title": "%s",
            "completed": %s
        }
        """).formatted(title, completed);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/tasks")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldRejectTaskWithoutTitle() {
        String title = "";
        boolean completed = false;
        String requestBody = ("""
        {
            "title": "%s",
            "completed": %s
        }
        """).formatted(title, completed);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/tasks")
                .then()
                .statusCode(400);
    }
    @Test
    void shouldRejectTaskWithBlankTitle() {
        String title = "     ";
        boolean completed = false;
        String requestBody = ("""
        {
            "title": "%s",
            "completed": %s
        }
        """).formatted(title, completed);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/tasks")
                .then()
                .statusCode(400)
                .body("violations[0].message", equalTo("Title is required"));
    }

    @Test
    void shouldRejectUpdateWithoutTitle() {
        Task testTask = testTasks.getFirst();
        String title = "";
        boolean completed = true;
        String requestBody = ("""
        {
            "title": "%s",
            "completed": %s
        }
        """).formatted(title, completed);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/tasks/{id}", testTask.getId())
                .then()
                .statusCode(400)
                .body("violations[0].message", equalTo("Title is required"));
    }

}
