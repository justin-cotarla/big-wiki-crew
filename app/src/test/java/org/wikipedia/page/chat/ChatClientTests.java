package org.wikipedia.page.chat;

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
    private DatabaseReference childReferenceMock;

    @Before
    public void setUp() {
        firebaseDatabaseMock = mock(FirebaseDatabase.class);
        articlesReferenceMock = mock(DatabaseReference.class);
        childReferenceMock = mock(DatabaseReference.class);
        when(firebaseDatabaseMock.getReference(articlesPath + '/' + articleId)).thenReturn(articlesReferenceMock);
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

        chatClient = new ChatClient(articleId, firebaseDatabaseMock);

        assertThat(chatClient.getIdCount(), is(1));
        verify(refMockIdCount).setValue(1);
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
