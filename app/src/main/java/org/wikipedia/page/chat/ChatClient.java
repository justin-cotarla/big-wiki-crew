package org.wikipedia.page.chat;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.wikipedia.util.log.L;

/**
 * Each article will have its own instance of ChatClient.
 */
public class ChatClient {
    private int idCount;

    private DatabaseReference articlesRef;

    public ChatClient(int articleId) {
        this.idCount = 0;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.articlesRef = database.getReference("articles/" + articleId);

        // Read all data from the article node
        this.articlesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("idCount")) {
                    idCount = dataSnapshot.child("idCount").getValue(Integer.class) != null ?
                            dataSnapshot.child("idCount").getValue(Integer.class) : 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                L.e(databaseError.toException());
            }
        });
    }

    public int getIdCount() {
        return this.idCount;
    }

    public void incrementIdCount() {
        this.idCount++;
        this.articlesRef.child("idCount").setValue(this.idCount);
    }
}
