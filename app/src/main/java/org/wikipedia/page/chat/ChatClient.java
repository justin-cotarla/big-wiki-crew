package org.wikipedia.page.chat;

import android.content.Context;
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
    private int idCount;
    private int users;
    private boolean lock;
    private List<Message> sendMessageQueue;
    private List<Message> messageList;
    private DatabaseReference articlesRef;

    private final String userPrefix = "anon";
    private final String messagesPath = "messages";
    private final String articlesPath = "articles";
    private final String idCountPath = "idCount";
    private final String usersCountPath = "users";

    public ChatClient(int articleId) {
        Log.i("ChatClient",  "articleId: " + articleId);
        this.idCount = 0;
        this.users = 0;
        closeLock();
        this.sendMessageQueue = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.articlesRef = database.getReference(articlesPath + "/" + articleId);

        // Read all data from the article node. Set the instance variables here.
        this.articlesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("ChatClient",  "onDataChange");
                if (dataSnapshot.hasChild(idCountPath)) {
                    idCount = dataSnapshot.child(idCountPath).getValue(Integer.class) != null
                            ? dataSnapshot.child(idCountPath).getValue(Integer.class) : 0;
                }

                if (dataSnapshot.hasChild(usersCountPath)) {
                    users = dataSnapshot.child(usersCountPath).getValue(Integer.class) != null
                            ? dataSnapshot.child(usersCountPath).getValue(Integer.class) : 0;
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

    private void consumeMessageQueue() {
        for (Message message : this.sendMessageQueue) {
            message.setUser(getUser());
            writeMessage(message);
        }
        this.sendMessageQueue.clear();
    }

    public int getIdCount() {
        return this.idCount;
    }

    public int getUsersCount() {
        return this.users;
    }

    public String getUser() {
        return userPrefix + String.valueOf(getIdCount());
    }

    private void enterChatRoom() {
        // Update idCount and users count
        this.idCount++;
        this.users++;
        this.articlesRef.child("idCount").setValue(this.idCount);
        this.articlesRef.child("users").setValue(this.users);
    }

    public void leaveChatRoom() {
        this.users--;
        this.articlesRef.child("users").setValue(this.users);
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
