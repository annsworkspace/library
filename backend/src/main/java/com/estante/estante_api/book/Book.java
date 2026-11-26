package com.estante.estante_api.book;

import com.estante.estante_api.account.AppUser;
import com.estante.estante_api.author.Author;
import com.estante.estante_api.category.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 2000)
    private String notes;

    @Column(name = "cover_path", length = 500)
    private String coverPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "reading_status", nullable = false, length = 20)
    private ReadingStatus readingStatus = ReadingStatus.WANT_TO_READ;

    @Column(name = "loaned_to", length = 160)
    private String loanedTo;

    @Column(name = "loan_date")
    private LocalDate loanDate;

    @Column(name = "returned_date")
    private LocalDate returnedDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    protected Book() {
    }

    public Book(
            AppUser owner,
            String title,
            Author author,
            Category category,
            String notes,
            String coverPath,
            String loanedTo,
            LocalDate loanDate,
            LocalDate returnedDate,
            ReadingStatus readingStatus) {
        this.owner = owner;
        this.title = title;
        this.author = author;
        this.category = category;
        this.notes = notes;
        this.coverPath = coverPath;
        this.loanedTo = loanedTo;
        this.loanDate = loanDate;
        this.returnedDate = returnedDate;
        this.readingStatus = readingStatus == null
                ? ReadingStatus.WANT_TO_READ
                : readingStatus;
    }

    public void update(
            String title,
            Author author,
            Category category,
            String notes,
            String coverPath,
            String loanedTo,
            LocalDate loanDate,
            LocalDate returnedDate,
            ReadingStatus readingStatus) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.notes = notes;
        this.coverPath = coverPath;
        this.loanedTo = loanedTo;
        this.loanDate = loanDate;
        this.returnedDate = returnedDate;
        this.readingStatus = readingStatus == null
                ? ReadingStatus.WANT_TO_READ
                : readingStatus;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Author getAuthor() {
        return author;
    }

    public Category getCategory() {
        return category;
    }

    public String getNotes() {
        return notes;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public ReadingStatus getReadingStatus() {
        return readingStatus;
    }

    public String getLoanedTo() {
        return loanedTo;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }
}