package org.wikipedia.saved.notes.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.page.PageTitle;

import java.util.Comparator;
import java.util.Date;

public class Note {
    public static final NoteTable DATABASE_TABLE = new NoteTable();

    private long id;

    @NonNull private final String content;
    // Date time when note was created
    @NonNull private final Date createdAt;

    // For creating a page title
    @NonNull private final WikiSite wiki;
    @NonNull private final String title;
    @Nullable private String description;
    @Nullable private String thumbUrl;

    public Note(@NonNull String content, @NonNull WikiSite wiki, @NonNull String title, @NonNull Date createdAt) {
        this.content = content;
        this.wiki = wiki;
        this.title = title;
        this.createdAt = createdAt;
    }

    public Note(@NonNull String content, @NonNull PageTitle pageTitle, @NonNull Date createdAt) {
        this.content = content;
        this.title = pageTitle.getDisplayText();
        this.wiki = pageTitle.getWikiSite();
        this.thumbUrl = pageTitle.getThumbUrl();
        this.description = pageTitle.getDescription();
        this.createdAt = createdAt;
    }

    public long id() {
        return id;
    }

    public void id(long id) {
        this.id = id;
    }

    public String content() {
        return content;
    }

    public Date createdAt() { return createdAt; }

    @NonNull public WikiSite wiki() {
        return wiki;
    }

    @NonNull public String title() {
        return title;
    }

    @Nullable public String description() {
        return description;
    }

    public void description(@Nullable String description) {
        this.description = description;
    }

    @Nullable public String thumbUrl() {
        return thumbUrl;
    }

    public void thumbUrl(@Nullable String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public PageTitle getPageTitle() {
        return new PageTitle(this.title, this.wiki, this.thumbUrl, this.description);
    }

    /**
     * Use this comparator to sort notes based on the article they belong to
     * To sort a list of notes, pass in this comparator to Collections.sort()
     */
    public static Comparator<Note> articleTitleComparator = (o1, o2) -> {
        String title1 = o1.title().toUpperCase();
        String title2 = o2.title().toUpperCase();
        return title1.compareTo(title2);
    };

    /**
     * Use this comparator to sort notes based on their creation dates
     * To sort a list of notes, pass in this comparator to Collections.sort()
     */
    public static Comparator<Note> createdAtComparator = (o1, o2) -> o1.createdAt().compareTo(o2.createdAt());
}
