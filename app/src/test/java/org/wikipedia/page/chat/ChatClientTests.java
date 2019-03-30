package org.wikipedia.page.chat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class ChatClientTests {
    private ChatClient chatClient;
    private int articleId = 2;
    private String articlesPath = "articles";
    private String messagesPath = "messages";

    private FirebaseDatabase firebaseDatabaseMock;
    private DatabaseReference articlesReferenceMock;
    private DatabaseReference childReferenceMock;

    @Before
    public void setUp() {
        firebaseDatabaseMock = mock(FirebaseDatabase.class);
        articlesReferenceMock = mock(DatabaseReference.class);
        childReferenceMock = mock(DatabaseReference.class);
        when(firebaseDatabaseMock.getReference(articlesPath + '/' + articleId)).thenReturn(articlesReferenceMock);
    }

    @Test
    public void testWriteMessages() {
        chatClient = new ChatClient(articleId, firebaseDatabaseMock);

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
        chatClient = new ChatClient(articleId, firebaseDatabaseMock);

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
        chatClient = new ChatClient(articleId, firebaseDatabaseMock);

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
}
