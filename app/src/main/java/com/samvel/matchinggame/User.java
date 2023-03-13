package com.samvel.matchinggame;

public class User {
    public String email;
    public String bestScore;
    public String score;
    public String size;
    public String step;
    public String time;
    public int games;

    public User(String email, String bestScore, String score, String size, String step, String time, int games) {
        this.email = email;
        this.bestScore = bestScore;
        this.score = score;
        this.size = size;
        this.step = step;
        this.time = time;
        this.games = games;
    }
}
