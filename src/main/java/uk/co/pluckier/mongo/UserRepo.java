package uk.co.pluckier.mongo;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;

import uk.co.pluckier.model.Forgot;
import uk.co.pluckier.model.Login;
import uk.co.pluckier.model.User;

public class UserRepo implements Repo {

	// Property Keys
	private static final String DB_DATABASE = "db.database";
	private static final String DB_COLLECTION_USERS = "db.collection";
	private static final String DB_COLLECTION_FORGOT = "db.collection1";
	private static final String DB_COLLECTION_LOGINS = "db.collection2";
	private static final String DB_CONNECTION_STRING = "db.connectionString";

	// Field Names
	private static final String FIELD_USERNAME = "username";
	private static final String FIELD_EMAIL = "email";
	private static final String FIELD_PASSWORD = "password";
	private static final String FIELD_FREQ = "freq";
	private static final String FIELD_PURCHASED = "purchased";
	private static final String FIELD_UUID = "uuid";
	private static final String FIELD_CREATED = "created";
	private static final String FIELD_IP = "ip";
	private static final String FIELD_AT = "at";

	// Frequency Codes
	private static final String FREQ_MONTHLY = "m";
	private static final String FREQ_DAILY = "d";
	private static final String FREQ_VERIFIED = "x";
	private final MongoClient mongoClient;
	private final MongoCollection<User> repo;
	private final MongoCollection<Forgot> frepo;
	private final MongoCollection<Login> lrepo;

	public static Repo getDefaultInstance() {
		Properties p = new Properties();
		try (InputStream input = UserRepo.class.getClassLoader().getResourceAsStream("db.properties")) {
			if (input == null) {
				// A more robust application might have better error handling or fallbacks
				throw new RuntimeException("Unable to find db.properties in classpath");
			}
			p.load(input);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load db.properties", e);
		}
		return new UserRepo(p);
	}
	
	public static Repo getInstance(Properties prop) {
		return new UserRepo(prop);
	}
	
	private UserRepo(Properties p) {
		ConnectionString connectionString = new ConnectionString(p.getProperty(DB_CONNECTION_STRING));
		MongoClientSettings settings = MongoClientSettings.builder()
				.applyConnectionString(connectionString)
				.codecRegistry(fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true)
						.build())))
				.build();
		mongoClient = MongoClients.create(settings);
		repo = mongoClient.getDatabase(p.getProperty(DB_DATABASE)).getCollection(p.getProperty(DB_COLLECTION_USERS), User.class);
		frepo = mongoClient.getDatabase(p.getProperty(DB_DATABASE)).getCollection(p.getProperty(DB_COLLECTION_FORGOT), Forgot.class);
		lrepo = mongoClient.getDatabase(p.getProperty(DB_DATABASE)).getCollection(p.getProperty(DB_COLLECTION_LOGINS), Login.class);
	}

	/**
	 * A constructor designed for testing. It allows for injecting mock or fake
	 * collections, which makes unit testing possible without a real database.
	 *
	 * @param repo The collection for User objects.
	 * @param frepo The collection for Forgot objects.
	* @param lrepo The collection for Login objects.
	 */
	public UserRepo(MongoCollection<User> repo, MongoCollection<Forgot> frepo, MongoCollection<Login> lrepo) {
		this.mongoClient = null; // Not used in mocked tests, so it can be null.
		this.repo = repo;
		this.frepo = frepo;
		this.lrepo = lrepo;
	}

	public List<User> getAll() { 
		return StreamSupport
		  .stream(repo.find().spliterator(), true)
		  .collect(Collectors.toList());
	}
	
	public List<User> getAllValid() { 
		// This is more efficient as it filters in the database.
		// Note: This query is complex and may need adjustment based on exact `purchased` date formats.
		// For simplicity, this example just fetches non-expired users. A more robust solution
		// would use proper date objects in MongoDB.
		return StreamSupport.stream(repo.find(
				or(
					eq(FIELD_PURCHASED, "A"),
					eq(FIELD_PURCHASED, "0")
					// A more complex filter for date strings would go here, e.g., using $gte
				)
			).spliterator(), true)
			.collect(Collectors.toList());
	}
	
	public User get(String userId) {
		return getWithQuery(new Document(FIELD_USERNAME, userId));
	}
	
	public User getByEmail(String email) {
		return getWithQuery(new Document(FIELD_EMAIL, email));
	}
	
	public User getByFrequency(String freq) {
		return getWithQuery(new Document(FIELD_FREQ, freq));
	}
	
	public boolean add(User user) {
		return repo.insertOne(user).wasAcknowledged();
	}
	
	public boolean remove(String username) {
		return repo.deleteOne(eq(FIELD_USERNAME, username)).wasAcknowledged();
	}
	
	public boolean updatePasswordViaEmail(String email, String password) {
		return repo.updateOne(eq(FIELD_EMAIL, email), set(FIELD_PASSWORD, password)).wasAcknowledged();
	}
	
	public boolean updatePassword(String username, String password) {
		return repo.updateOne(eq(FIELD_USERNAME, username), set(FIELD_PASSWORD, password)).wasAcknowledged();
	}

	public boolean updatePurchased(String username, String purchased) {
		return repo.updateOne(eq(FIELD_USERNAME, username), combine(set(FIELD_FREQ, FREQ_MONTHLY), set(FIELD_PURCHASED, purchased))).wasAcknowledged();
	}
	
	public boolean updateDayPurchased(String username, String purchased) {
		return repo.updateOne(eq(FIELD_USERNAME, username), combine(set(FIELD_FREQ, FREQ_DAILY), set(FIELD_PURCHASED, purchased))).wasAcknowledged();
	}
	
	public boolean verify(String username) {
		return repo.updateOne(eq(FIELD_USERNAME, username), combine(set(FIELD_FREQ, FREQ_VERIFIED), set(FIELD_PURCHASED, "0"))).wasAcknowledged();
	}
	
	@Override
	public boolean isValid(User user) {
		if (user == null || user.getPurchased() == null) {
			return false;
		}
		String purchased = user.getPurchased();
		// "A" for active free, "0" for registered but not paid
		if (purchased.equals("A") || purchased.equals("0")) {
			return true;
		}
		try {
			// The date is stored as "dd/MM/yyyy HH:mm"
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			LocalDateTime purchaseDateTime = LocalDateTime.parse(purchased, dateTimeFormatter);
			// Valid for 32 days
			return purchaseDateTime.isAfter(LocalDateTime.now().minusDays(32));
		} catch (Exception e) {
			// If date is malformed or not present, consider it invalid.
			return false;
		}
	}

	public Forgot getForgot(String userId) {
		return getForgotWithQuery(new Document("uuid", userId));
	}
	
	public Forgot getForgotByEmail(String email) { 
		return getForgotWithQuery(new Document("email", email));
	}
	
	public boolean addForgot(Forgot user) {
		return frepo.insertOne(user).wasAcknowledged();
	}
	
	public boolean removeForgot(String email) {
		return frepo.deleteOne(eq(FIELD_EMAIL, email)).wasAcknowledged();
	}
	
	public boolean updateForgotCreated(String uuid, String created) {
		return frepo.updateOne(eq(FIELD_UUID, uuid), combine(set(FIELD_CREATED, created))).wasAcknowledged();
	}
	
	private Forgot getForgotWithQuery(Document query) {
		return frepo.find(query).first();
	}
	
	public boolean addLogin(Login login) {
		return lrepo.insertOne(login).wasAcknowledged();
	}

	@Override
	public Login getLogin(String userId) {
		return getLoginWithQuery(new Document(FIELD_USERNAME, userId));
	}
	
	private Login getLoginWithQuery(Document query) {
		return lrepo.find(query).first();
	}
	
	@Override
	public boolean updateLogin(String username, String ip, String at) {
		return lrepo.updateOne(eq(FIELD_USERNAME, username), combine(set(FIELD_IP, ip), set(FIELD_AT, at))).wasAcknowledged();
	}
	
	@Override
	public boolean deletAllLogins() {
		return lrepo.deleteMany(new Document()).wasAcknowledged();
	}
	
	public void close() { 
		mongoClient.close(); 
	}
	
	private User getWithQuery(Document query) {
		return repo.find(query).first();
	}

}
