package org.wikipedia.page.chat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("checkstyle:magicnumber")
public class ChatClientTests {
    private ChatClient chatClient;
    private int articleId = 2;
    private String articlesPath = "articles";
    private String messagesPath = "messages";
    private String idCountPath = "idCount";
    private String usersCountPath = "userCount";

    private FirebaseDatabase firebaseDatabaseMock;
    private DatabaseReference articlesReferenceMock;
    private DatabaseReference messageReferenceMock;
    private ChatClient.UserCountCallback userCountCallbackMock;


    @Before
    public void setUp() {
        firebaseDatabaseMock = mock(FirebaseDatabase.class);
        articlesReferenceMock = mock(DatabaseReference.class);
        messageReferenceMock = mock(DatabaseReference.class);
        userCountCallbackMock = mock(ChatClient.UserCountCallback.class);
        when(firebaseDatabaseMock.getReference(articlesPath + '/' + articleId)).thenReturn(articlesReferenceMock);
        when(articlesReferenceMock.child(messagesPath)).thenReturn(messageReferenceMock);
    }

    @Test
    public void testConnect() {
        DatabaseReference refMockIdCount = mock(DatabaseReference.class);
        DatabaseReference refMockUserCount = mock(DatabaseReference.class);
        when(articlesReferenceMock.child(idCountPath)).thenReturn(refMockIdCount);
        when(articlesReferenceMock.child(usersCountPath)).thenReturn(refMockUserCount);

        doAnswer((Answer<Void>) invocation -> {
            ValueEventListener valueEventListener = (ValueEventListener) invocation.getArguments()[0];
            DataSnapshot mockedDataSnapshot = mock(DataSnapshot.class);
            valueEventListener.onDataChange(mockedDataSnapshot);
            return null;
        }).when(articlesReferenceMock).addListenerForSingleValueEvent(any(ValueEventListener.class));

        chatClient = new ChatClient(articleId, firebaseDatabaseMock, userCountCallbackMock);

        assertThat(chatClient.getIdCount(), is(1));
        assertThat(chatClient.getUsersCount(), is(1));
        verify(refMockIdCount).setValue(1);
        verify(refMockUserCount).setValue(1);
    }

    @Test
    public void testLeaveChatRoom() {
        DatabaseReference refMockIdCount = mock(DatabaseReference.class);
        DatabaseReference refMockUserCount = mock(DatabaseReference.class);
        when(articlesReferenceMock.child(idCountPath)).thenReturn(refMockIdCount);
        when(articlesReferenceMock.child(usersCountPath)).thenReturn(refMockUserCount);

        doAnswer((Answer<Void>) invocation -> {
            ValueEventListener valueEventListener = (ValueEventListener) invocation.getArguments()[0];
            DataSnapshot mockedDataSnapshot = mock(DataSnapshot.class);
            valueEventListener.onDataChange(mockedDataSnapshot);
            return null;
        }).when(articlesReferenceMock).addListenerForSingleValueEvent(any(ValueEventListener.class));

        chatClient = new ChatClient(articleId, firebaseDatabaseMock, userCountCallbackMock);
        chatClient.leaveChatRoom();

        assertThat(chatClient.getUsersCount(), is(0));
        verify(refMockUserCount).setValue(0);
    }

    @Test
    public void testWriteMessages() {
        chatClient = new ChatClient(articleId, firebaseDatabaseMock, userCountCallbackMock);

        DatabaseReference refMock = mock(DatabaseReference.class);
        DatabaseReference pushMock = mock(DatabaseReference.class);

        when(articlesReferenceMock.child(messagesPath)).thenReturn(refMock);
        when(refMock.push()).thenReturn(pushMock);

        chatClient.openLock();
        chatClient.writeMessage("hello");

        verify(pushMock).setValue(any(Message.class));
    }

    @Test
    public void testWriteMessagesLocked() {
        chatClient = new ChatClient(articleId, firebaseDatabaseMock, userCountCallbackMock);

        DatabaseReference refMock = mock(DatabaseReference.class);
        DatabaseReference pushMock = mock(DatabaseReference.class);

        when(articlesReferenceMock.child(messagesPath)).thenReturn(refMock);
        when(refMock.push()).thenReturn(pushMock);

        chatClient.writeMessage("hello");
        verifyZeroInteractions(pushMock);

        chatClient.openLock();
        verify(pushMock).setValue(any(Message.class));
    }

    @Test
    public void testWriteMessagesQueued() {
        chatClient = new ChatClient(articleId, firebaseDatabaseMock, userCountCallbackMock);

        DatabaseReference refMock = mock(DatabaseReference.class);
        DatabaseReference pushMock = mock(DatabaseReference.class);

        when(articlesReferenceMock.child(messagesPath)).thenReturn(refMock);
        when(refMock.push()).thenReturn(pushMock);

        chatClient.writeMessage("hello");
        chatClient.writeMessage("goodBye");
        chatClient.writeMessage("Great Article!");
        verifyZeroInteractions(pushMock);

        chatClient.openLock();
        verify(pushMock, times(3)).setValue(any(Message.class));
    }

    @Test
    public void testSubscribe() {
        Message message = new Message("1", "2", null);

        doAnswer((Answer<Void>) invocation -> {
            ChildEventListener childEventListener = (ChildEventListener) invocation.getArguments()[0];
            DataSnapshot mockedDataSnapshot = mock(DataSnapshot.class);
            when(mockedDataSnapshot.getValue(Message.class)).thenReturn(message);
            childEventListener.onChildAdded(mockedDataSnapshot, null);
            return null;
        }).when(messageReferenceMock).addChildEventListener(any(ChildEventListener.class));

        chatClient = new ChatClient(articleId, firebaseDatabaseMock, userCountCallbackMock);
        ChatClient.MessageCallback messageCallbackMock = mock(ChatClient.MessageCallback.class);

        chatClient.subscribe(messageCallbackMock);

        verify(messageCallbackMock).messageReceived(message);
    }

    @Test
    public void testUnsubscribe() {
        chatClient = new ChatClient(articleId, firebaseDatabaseMock, userCountCallbackMock);
        ChatClient.MessageCallback messageCallbackMock = mock(ChatClient.MessageCallback.class);

        chatClient.subscribe(messageCallbackMock);
        chatClient.unsubscribe();

        verify(messageReferenceMock).removeEventListener(any(ChildEventListener.class));
    }
}
