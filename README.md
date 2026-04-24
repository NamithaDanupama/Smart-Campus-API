# University API

The Smart Campus API is a RESTful web service built using JAX-RS (Java API for RESTful Web Services) with the Jersey framework, deployed on an Apache Tomcat server. 
It is designed to manage campus rooms and the IoT sensors installed within them, providing administrators and automated building systems with a structured and reliable interface to interact with campus infrastructure data.

## What it does
- **Rooms:** You can list, fetch, create, and delete campus rooms.
- **Sensors:** You can register new hardware, filter them by type, and view them.
- **Readings:** A sub-resource system handles posting new sensor data and tracking the history.

Everything speaks JSON. I made sure it catches bad requests cleanly without dumping messy Java stack traces to the client.

---

## Build & Run Instruction

**Prerequisites**
- Java JDK 11 or higher installed
- Apache Maven 3.6+ installed and added to PATH
- Apache Tomcat 9.0 extracted (e.g. to C:\Tomcat\apache-tomcat-9.0.100)

**Step 1: Clone the Repository**
```bash
git clone https://github.com/NamithaDanupama/Smart-Campus-API.git
```

**Step 2 — Build the Project**
Open the project in NetBeans, then:
- Right click the project → Clean and Build
- Wait for BUILD SUCCESS in the output window

**Step 3 — Copy the WAR file to Tomcat**
Go to your project folder → open target folder → copy `smart-campus-w2121365-1.0-SNAPSHOT.war` → paste it into `C:\apache-tomcat-9.0.100\webapps\`

**Step 4 — Start Tomcat**
In NetBeans:
- Right click the project → Run
- NetBeans will start Tomcat and deploy automatically

---

## Testing the Endpoints (cURL)

**1. API Discovery Info**
```bash
curl -X GET http://localhost:8080/smart-campus-api-1.0-SNAPSHOT/api/v1/
```

**2. See all rooms**
```bash
curl -X GET http://localhost:8080/smart-campus-api-1.0-SNAPSHOT/api/v1/rooms
```

**3. Add a new room**
```bash
curl -X POST http://localhost:8080/smart-campus-api-1.0-SNAPSHOT/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "AUD-001", "name": "Auditorium", "capacity": 300}'
```

**4. Register a sensor (assigning to our default room)**
```bash
curl -X POST http://localhost:8080/smart-campus-api-1.0-SNAPSHOT/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "TEMP-999", "type": "Temperature", "status": "ACTIVE", "currentValue": 22.0, "roomId": "LEC-101"}'
```

**5. Get sensors by their type**
```bash
curl -X GET "http://localhost:8080/smart-campus-api-1.0-SNAPSHOT/api/v1/sensors?type=Carbon%20Dioxide"
```

**6. Push a new reading to a sensor**
```bash
curl -X POST http://localhost:8080/smart-campus-api-1.0-SNAPSHOT/api/v1/sensors/CO2-999/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 412.5}'
```

**7. View the sensor's reading history**
```bash
curl -X GET http://localhost:8080/smart-campus-api-1.0-SNAPSHOT/api/v1/sensors/CO2-999/readings
```

---

## Coursework Questions & Answers

### Part 1: Service Architecture & Setup

**Q: In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.**
As the default case, the JAX-RS creates a new instance for each class for each HTTP request, which is known as the per-request cycle. 
This means data can't be stored, and with each request they wiped out. As the solution for this, all the data is stored in a separate static class called DataStore. 
Static fields belong to the class rather than the instance, so all requests share the same data for the lifetime of the server, as if it were a database.

**Q: Why is the provision of ”Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?**
HATEOAS means the API includes hyperlinks inside responses to tell clients what actions or resources are available for the next step, rather than expecting developers to memorise URLs from documentation. 
This can make the API look like it self-navigates, similar to following links on a webpage. 
If a URL change occurs on the server side, the client side will catch up without crashing. Here, the discover endpoint at GET /api/v1/ returns links to all primary collections, giving the client a complete map from a single starting point.

### Part 2: Room Management

**Q: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.**
Returning only the IDs produces a lightweight response, but it forces the client to make a request for every ID to retrieve details, which can create a network overload. 
It returns a full object with everything in one request, which is more practical. For this implementation, full objects are the better choice.

**Q: Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.**
Yes, the first DELETE on a valid room returns 200 OK, and once the resource is deleted, if the DELETE runs again, it will return 404 Not Found because the resource is not there. 
Although the response code differs, the server state is identical as the room is gone when the delete is executed. REST defines idempotency based on the server state, not on the response code. 
This implementation is fully idempotent as per the HTTP specification.

### Part 3: Sensor Operations & Linking

**Q: We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?**
The @Consumes(MediaType.APPLICATION_JSON) annotation tells the JAX-RS runtime to only accept requests where the Content-Type header is application/json. 
If a client sends something not related, like text/plain or application /xml, the framework will check the header before the resource methods get executed, so that it will return an HTTP 415 Unsupported Media Type response. 
The annotation will work as a gatekeeper without any validation code.

**Q: You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/vl/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?**
The path parameters are designed to identify a specific resource, so the embedding filters in the path, like /sensors/type/CO2, are semantically misleading because the CO2 is a filter, not a resource. 
The query parameters are designed specially for filtering collections, which are accurate and readable. Using a query can also scale cleanly when multiple filters are needed, for example,/sensors?type=CO2&status=ACTIVE, where the path-based filtering becomes increasingly awkward with each additional filter.

### Part 4: Deep Nesting

**Q: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?**
The Sub-Resource Locator pattern allows a resource class to delegate nested path handling to a separate dedicated class. 
In this implementation, SensorResource manages /sensors but they dands off /sensors/{sensorId}/readings to SensorReadingResource. 
This shows the separation of concerns, as each class has one clear responsibility. 
Without this type of pattern, a single controller would have to handle a wide path, making it extremely hard to read, test, and maintain. 
Using this method will distribute the same logic across the focus classes, allowing each to be modified independently without affecting the rest.

### Part 5: Error Handling & Logging

**Q: Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?**
A 404 Not Found error tells the client that the requested URL does not exist. But the 422 Unprocessable Entity is showing that the request was understood, but its fail to execute due to invalid content inside the payload. 
However, if the URL is a valid one and the JSON is correctly formatted, but contains a roomId that does not exist in the system, then the problem lies within the payload's business logic, not the URL. 
Both these things help to give a clear message to the client about what's happening.

**Q: From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?**
Exposing the stack traces is a serious security risk. That will reveal data such as the used framework, its exact version number, internal paths, and class names. 
A third party can use these data to look up known vulnerabilities for a specific version and exploit them directly, without probing the system blindly.

**Q: Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?**
Without using filters, logging statements would need to be manually added inside every single resource method, which can create cluttering business logic and inconsistent coverage. 
A JAX-RX filter can wrap the entire application from one central point where it can automatically capturing every incoming and outgoing response. 
This keeps resource classes clean and ensures no endpoint is ever accidentally left unlogged.
