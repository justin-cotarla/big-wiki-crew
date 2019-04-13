package org.wikipedia.saved.notes.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.page.PageTitle;

public class Note {
    public static final NoteTable DATABASE_TABLE = new NoteTable();

    private long id;

    @NonNull private final String content;

    // For creating a page title
    @NonNull private final WikiSite wiki;
    @NonNull private final String title;
    @Nullable private String description;
    @Nullable private String thumbUrl;

    public Note(@NonNull String content, @NonNull WikiSite wiki, @NonNull String title) {
        this.content = content;
        this.wiki = wiki;
        this.title = title;
    }

    public Note(@NonNull String content, @NonNull PageTitle pageTitle) {
        this.content = content;
        this.title = pageTitle.getDisplayText();
        this.wiki = pageTitle.getWikiSite();
        this.thumbUrl = pageTitle.getThumbUrl();
        this.description = pageTitle.getDescription();
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
}
