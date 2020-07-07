package com.example.recycleview;
/*
Talking Pages
Free Audiobook App
Created By: Soumya Chowdhury
            Makineedi Sai Harsh
            Nagani Vrudant Gopalbhai
            Mukul Bhambari
 */
public class Song {
    int Id;
    String title;
    String author;
    String genre;
    String series;

    public Song(String Id, String title,String author,String series,String genre) {
        try {
            this.Id = Integer.parseInt(Id);
        }catch (Exception e) {
            this.Id = 0;
        }
        this.title = title;
        this.author = author;
        this.series = series;
        this.genre = genre;
    }

    public int getId() {
        return Id;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthor() { return author;}
    public String getGenre() { return genre;}
    public String getSeries() {return series;}
}
