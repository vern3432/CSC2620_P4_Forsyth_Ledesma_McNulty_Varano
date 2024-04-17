package com.gutenberg;

public class Book {
    private int pageNumber;
    private String title;
    
    /** 
     * @return int
     */
    public int getPageNumber() {
        return pageNumber;
    }

    
    /** 
     * @param pageNumber
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    private String author;
    private String releaseDate;
    private String language;

    // Constructor
    public Book(String title, String author, String releaseDate, String language) {
        this.title = title;
        this.author = author;
        this.releaseDate = releaseDate;
        this.language = language;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getLanguage() {
        return language;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    // Optional: Override toString() for easy printing of the book details
    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
