package org.wikipedia.page.chat;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.wikipedia.util.log.L;

import java.util.Calendar;
import java.util.Date;

/**
 * Each article will have its own instance of ChatClient.
 */
public class ChatClient {
    final private String userPrefix = "anon";
    final private String messagesPath = "messages";
    final private String articlesPath = "articles";

    private int idCount;

    private DatabaseReference articlesRef;

    public ChatClient(int articleId) {
        this.idCount = 0;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.articlesRef = database.getReference(articlesPath + "/" + articleId);

        // Read all data from the article node. Set the instance variables here.
        this.articlesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("idCount")) {
                    idCount = dataSnapshot.child("idCount").getValue(Integer.class) != null
                            ? dataSnapshot.child("idCount").getValue(Integer.class) : 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                L.e(databaseError.toException());
            }
        });
    }

    public void writeMessage(String message) {
        String user = getUser();
        Date timeStamp = Calendar.getInstance().getTime();

        Message newMessage = new Message(user, message, timeStamp);

        DatabaseReference messagesRef = articlesRef.child(messagesPath);
        messagesRef.push().setValue(newMessage);
    }

    public int getIdCount() {
        return this.idCount;
    }

    public String getUser() {
        return userPrefix + String.valueOf(getIdCount());
    }

    public void enterChatRoom() {
        // Update idCount
        this.idCount++;
        this.articlesRef.child("idCount").setValue(this.idCount);
    }
}
