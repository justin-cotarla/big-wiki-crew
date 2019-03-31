package org.wikipedia.page.chat;

import android.content.Context;
import android.support.annotation.NonNull;

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
    private int idCount;
    private boolean lock;
    private List<Message> sendMessageQueue;
    private List<Message> messageList;
    private DatabaseReference articlesRef;

    private final String userPrefix = "anon";
    private final String messagesPath = "messages";
    private final String articlesPath = "articles";
    private final String idCountPath = "idCount";

    public ChatClient(int articleId) {
        this(articleId, FirebaseDatabase.getInstance());
    }

    public ChatClient(int articleId, FirebaseDatabase firebaseDatabase) {
        this.idCount = 0;
        closeLock();
        this.sendMessageQueue = new ArrayList<>();
        this.articlesRef = firebaseDatabase.getReference(articlesPath + "/" + articleId);
    }

    public void connect() {
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

    public void readMessages(Context context) {
        DatabaseReference messageRef = this.articlesRef.child(messagesPath);
        messageList = new ArrayList<>();
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Message newMessage = child.getValue(Message.class);
                        if (!messageList.contains(newMessage)) {
                            messageList.add(newMessage);

                            // THIS IS WHERE YOU WOULD UPDATE THE UI
                            // PASS A REFERENCE THAT YOU CAN CALL A FUNCTION ON HERE
                            // IN ORDER TO ADD THE NEW MESSAGE TO THE LIST ON THE UI
                            // "context.addMessageToChatList(newMessage)"
                        }
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

        writeMessage(newMessage);
    }

    private void writeMessage(Message message) {
        if (isLocked()) {
            sendMessageQueue.add(message);
        } else {
            DatabaseReference messagesRef = articlesRef.child(messagesPath);
            messagesRef.push().setValue(message);
        }
    }

    public void consumeMessageQueue() {
        for (Message message : this.sendMessageQueue) {
            message.setUser(getUser());
            writeMessage(message);
        }
        this.sendMessageQueue.clear();
    }

    public int getIdCount() {
        return this.idCount;
    }

    public String getUser() {
        return userPrefix + String.valueOf(getIdCount());
    }

    protected void enterChatRoom() {
        // Update idCount
        this.idCount++;
        this.articlesRef.child("idCount").setValue(this.idCount);
    }

    public void closeLock() {
        this.lock = true;
    }

    public void openLock() {
        this.lock = false;
        consumeMessageQueue();
    }

    private boolean isLocked() {
        return this.lock;
    }
}
