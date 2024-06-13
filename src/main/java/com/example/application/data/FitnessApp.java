package com.example.application.data;

import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
public class FitnessApp extends AbstractEntity {

    private LocalDate date;
    private Integer moves;
    private Integer exercise_time;
    private Integer stand;
    private Integer steps;
    private Integer calories;

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public Integer getMoves() {
        return moves;
    }
    public void setMoves(Integer moves) {
        this.moves = moves;
    }
    public Integer getExercise_time() {
        return exercise_time;
    }
    public void setExercise_time(Integer exercise_time) {
        this.exercise_time = exercise_time;
    }
    public Integer getStand() {
        return stand;
    }
    public void setStand(Integer stand) {
        this.stand = stand;
    }
    public Integer getSteps() {
        return steps;
    }
    public void setSteps(Integer steps) {
        this.steps = steps;
    }
    public Integer getCalories() {
        return calories;
    }
    public void setCalories(Integer calories) {
        this.calories = calories;
    }

}
