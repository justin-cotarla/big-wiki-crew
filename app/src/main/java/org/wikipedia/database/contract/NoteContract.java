package org.wikipedia.database.contract;

import android.net.Uri;

import org.wikipedia.database.DbUtil;
import org.wikipedia.database.column.DateColumn;
import org.wikipedia.database.column.IdColumn;
import org.wikipedia.database.column.StrColumn;

@SuppressWarnings("checkstyle:interfaceistype")
public interface NoteContract {
    String TABLE = "savednote";
    Uri URI = Uri.withAppendedPath(AppContentProviderContract.AUTHORITY_BASE, "/savednote");

    interface Col {
        IdColumn ID = new IdColumn(TABLE);
        StrColumn CONTENT = new StrColumn(TABLE, "content", "text not null");
        StrColumn SITE = new StrColumn(TABLE, "site", "text not null");
        StrColumn TITLE = new StrColumn(TABLE, "title", "text not null");
        StrColumn THUMBNAIL_URL = new StrColumn(TABLE, "thumbnailUrl", "text");
        StrColumn DESCRIPTION = new StrColumn(TABLE, "description", "text");
        StrColumn LANG = new StrColumn(TABLE, "lang", "text");
        DateColumn CREATED_AT = new DateColumn(TABLE, "createdAt", "integer");

        String[] SELECTION = DbUtil.qualifiedNames(TITLE);
        String[] ALL = DbUtil.qualifiedNames(ID, CONTENT, SITE, TITLE, THUMBNAIL_URL, DESCRIPTION, LANG, CREATED_AT);
    }
}
