package uk.co.pluckier.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.co.pluckier.model.User;
import uk.co.pluckier.model.Forgot;
import uk.co.pluckier.model.Login;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * This test class uses the Mockito framework to simulate the database.
 * This makes the tests very fast and self-contained, with no need for a real database.
 */
@ExtendWith(MockitoExtension.class)
class UserRepoTest {

    // These are our fake database objects
    @Mock private MongoCollection<User> urepo;
    @Mock private MongoCollection<Forgot> frepo;
    @Mock private MongoCollection<Login> lrepo;
    @Mock private FindIterable<User> findIterable;

    // This is the real object we are testing
    private Repo userRepo;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        // Create the real UserRepo, but inject our fake collection objects
        userRepo = new UserRepo(urepo, frepo, lrepo);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
    }

    @AfterEach
    void tearDown() throws Exception {
        // No cleanup needed for mocks
    }

    @Test
    void testAddUser() {
        InsertOneResult mockResult = mock(InsertOneResult.class);
        when(mockResult.wasAcknowledged()).thenReturn(true);
        when(urepo.insertOne(any(User.class))).thenReturn(mockResult);

        // ACT
        boolean added = userRepo.add(testUser);

        // ASSERT
        assertTrue(added, "The add operation should return true on success.");

        // VERIFY: Check that insertOne was called on the mock collection with our test user.
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(urepo).insertOne(userCaptor.capture());
        assertEquals("testuser", userCaptor.getValue().getUsername());
    }

    @Test
    void testRemoveUser() {
        // ARRANGE: Simulate a successful deletion
        DeleteResult mockResult = mock(DeleteResult.class);
        when(mockResult.wasAcknowledged()).thenReturn(true);
        when(urepo.deleteOne(any(Bson.class))).thenReturn(mockResult);

        // ACT
        boolean removed = userRepo.remove(testUser.getUsername());

        // ASSERT
        assertTrue(removed);

        // VERIFY
        verify(urepo).deleteOne(any(Bson.class));
    }

}
