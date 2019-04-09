package org.wikipedia.page.chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
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
    private int userCount;
    private boolean lock;
    private List<Message> sendMessageQueue;
    private DatabaseReference articlesRef;
    private DatabaseReference messageRef;
    private ChildEventListener messageListener;
    private ChildEventListener userCountListener;

    private final String userPrefix = "anon";
    private final String messagesPath = "messages";
    private final String articlesPath = "articles";
    private final String idCountPath = "idCount";
    private final String usersCountPath = "userCount";

    private UserCountCallback userCountCallback;

    public interface UserCountCallback {
        void run(Integer count);
    }

    public interface MessageCallback {
        void messageReceived(Message message);
    }

    public ChatClient(int articleId, UserCountCallback userCountCallback) {
        this(articleId, FirebaseDatabase.getInstance(), userCountCallback);
    }

    public ChatClient(int articleId, FirebaseDatabase firebaseDatabase, UserCountCallback userCountCallback) {
        idCount = 0;
        userCount = 0;
        closeLock();
        sendMessageQueue = new ArrayList<>();
        articlesRef = firebaseDatabase.getReference(articlesPath + "/" + articleId);
        messageRef = articlesRef.child(messagesPath);
        this.userCountCallback = userCountCallback;
        connect();
    }

    private void connect() {
        // Read all data from the article node. Set the instance variables here.
        articlesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(idCountPath)) {
                    idCount = dataSnapshot.child(idCountPath).getValue(Integer.class) != null
                            ? dataSnapshot.child(idCountPath).getValue(Integer.class) : 0;
                }

                if (dataSnapshot.hasChild(usersCountPath)) {
                    userCount = dataSnapshot.child(usersCountPath).getValue(Integer.class) != null
                            ? dataSnapshot.child(usersCountPath).getValue(Integer.class) : 0;
                }

                enterChatRoom();
                openLock();
                attachUserCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                L.e(databaseError.toException());
            }
        });
    }

    public void subscribe(MessageCallback callback) {
        messageListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                callback.messageReceived(dataSnapshot.getValue(Message.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Never gonna give you up
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Never gonna let you down
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Never gonna run around and desert you
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                L.e(databaseError.toException());
            }
        };
        messageRef.addChildEventListener(messageListener);
    }

    private void attachUserCount() {
        // Read data from the user count node. Set the instance variables here.
        articlesRef.child(usersCountPath).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userCount = dataSnapshot.getValue(Integer.class) != null
                        ? dataSnapshot.getValue(Integer.class) : 0;
                userCountCallback.run(userCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                L.e(databaseError.toException());
            }
        });
    }

    public void unsubscribe() {
        messageRef.removeEventListener(messageListener);
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

    public int getUsersCount() {
        return this.userCount;
    }

    public String getUser() {
        return userPrefix + String.valueOf(getIdCount());
    }

    public void enterChatRoom() {
        // Update idCount and userCount count
        articlesRef.child(idCountPath).setValue(++this.idCount);
        articlesRef.child(usersCountPath).setValue(++this.userCount);
        userCountCallback.run(this.userCount);
    }

    public void leaveChatRoom() {
        articlesRef.child(usersCountPath).setValue(--this.userCount);
        userCountCallback.run(this.userCount);
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
