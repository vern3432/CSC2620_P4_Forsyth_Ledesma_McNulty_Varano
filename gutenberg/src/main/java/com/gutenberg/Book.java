package com.gutenberg;

/**
 * 
 */
public class Book {
    private int pageNumber;
    private String title;
    
    /** 
     * gets page number
     * 
     * @return page number
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

    /**
     *  constructs a new book object with the specified attributes
     * 
     * @param title        title of book
     * @param author       author of book
     * @param releaseDate  release date of book
     * @param language     language of book
     */
    public Book(String title, String author, String releaseDate, String language) {
        this.title = title;
        this.author = author;
        this.releaseDate = releaseDate;
        this.language = language;
    }

    
    /** 
     * Gets title of book
     * 
     * @return title of book
     */
    public String getTitle() {
        return title;
    }

    
    /** 
     * Gets author of given book
     * 
     * @return Author of book
     */
    public String getAuthor() {
        return author;
    }

    
    /** 
     * Gets release date of book
     * 
     * @return release date of book
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * Gets language of given book
     * 
     * @return language written in book
     */
    
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

    @Override
    /**
     * Turns book and its info into stiring form
     * 
     * @return string describing books attributes
     */
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
