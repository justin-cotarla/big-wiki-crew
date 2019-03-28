package org.wikipedia.page.chat;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.wikipedia.util.log.L;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Each article will have its own instance of ChatClient.
 */
public class ChatClient {
    final private String userPrefix = "anon";
    final private String messagesPath = "messages";
    final private String articlesPath = "articles";
    final private String idCountPath = "idCount";

    private int idCount;
    private boolean lock;
    private List<Message> messageQueue;
    private DatabaseReference articlesRef;

    public ChatClient(int articleId) {
        this.idCount = 0;
        closeLock();
        this.messageQueue = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.articlesRef = database.getReference(articlesPath + "/" + articleId);

        // Read all data from the article node. Set the instance variables here.
        this.articlesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(idCountPath)) {
                    idCount = dataSnapshot.child(idCountPath).getValue(Integer.class) != null
                            ? dataSnapshot.child(idCountPath).getValue(Integer.class) : 0;
                }
                enterChatRoom();
                openLock();
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

        writeMessage(newMessage);
    }

    private void writeMessage(Message message) {
        if (isLocked()) {
            messageQueue.add(message);
        } else {
            DatabaseReference messagesRef = articlesRef.child(messagesPath);
            messagesRef.push().setValue(message);
        }
    }

    private void consumeMessageQueue() {
        for (Message message : this.messageQueue) {
            message.setUser(getUser());
            writeMessage(message);
        }
        this.messageQueue.clear();
    }

    public int getIdCount() {
        return this.idCount;
    }

    public String getUser() {
        return userPrefix + String.valueOf(getIdCount());
    }

    private void enterChatRoom() {
        // Update idCount
        this.idCount++;
        this.articlesRef.child("idCount").setValue(this.idCount);
    }

    private void closeLock() {
        this.lock = true;
    }

    private void openLock() {
        this.lock = false;
        consumeMessageQueue();
    }

    private boolean isLocked() {
        return this.lock;
    }
}
