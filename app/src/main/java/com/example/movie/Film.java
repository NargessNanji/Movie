package com.example.movie;

class Film {
    public long id;
    public String title;
    public String poster_path;

    public Film(long id, String title, String poster_path) {
        this.id = id;
        this.title = title;
        this.poster_path = poster_path;
    }
}
