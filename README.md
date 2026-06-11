# WebRTC Spring Boot Starter

## Project Overview

This project provides a lightweight Spring Boot starter for building real-time communication applications using WebRTC and WebSockets.

It abstracts away the low-level signaling setup typically required for WebRTC, allowing developers to focus on building features rather than wiring infrastructure.

### Who this is for

* Backend developers who want to add real-time video/audio to existing Spring Boot applications
* Full-stack developers working with Angular, React, or similar frontends
* Engineers who want a simple, extensible signaling layer without committing to heavy media servers (e.g., Janus, Kurento)
* Anyone experimenting with peer-to-peer communication and WebRTC concepts

This project is intentionally minimal and designed as a foundation—not a fully managed RTC platform.

---

## Tech Stack Overview

### Backend

* Spring Boot
* WebSocket (for signaling)
* Java (core signaling logic)

### Frontend (example implementation)

* Angular
* WebRTC (RTCPeerConnection, MediaStream APIs)

### Protocols / Standards

* WebRTC (peer-to-peer media)
* STUN (Google public STUN server)
* WebSocket signaling layer

---

## Project Structure

```
webrtc-spring-boot-starter/
├── src/main/java/com/albers/webrtc
│   ├── autoconfigure/   # WebSocket + signaling configuration + bean managment
│   ├── events/          # broadcasting events of join and leave
│   ├── service/         # Signaling + room management logic
│   └── model/           # DTOs for signaling messages
    └── websocket/       # Handling sessions after connection + chat messages
│
├── example-ui/ (separate repo)
│   └── Angular WebRTC demo client
│
└── pom.xml
```

The backend is responsible only for signaling (offer/answer/ICE exchange).
Media flows directly peer-to-peer between clients.

---

## How to Use

### 1. Add Repository + Dependency To Your pom.xml

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/AlbersSoftware/webrtc-spring-boot-starter</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.albers.webrtc</groupId>
        <artifactId>webrtc-spring-boot-starter</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```

---

### 2. Configure GitHub Package Access

Create or update:

```
C:\Users\YOURNAME\.m2\settings.xml
```

```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>YOUR_GITHUB_USERNAME</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

> Your token must have `read:packages` scope.

---

### 3. Run the Application

```bash
mvn clean install
mvn spring-boot:run
```

---

### 4. Connect a Client

Use the example UI:

https://github.com/AlbersSoftware/web-rtc-with-chat-simple-ui-example

Or connect your own frontend using:

* WebSocket for signaling
* WebRTC APIs for media

---

## Security

Security is minimal at this stage.

Current limitations:

* No authentication or authorization
* No encrypted signaling beyond default WebSocket transport
* No TURN server support (NAT traversal may fail in restrictive networks)
* No rate limiting or abuse protection

This project is currently intended for:

* Local development
* Internal tools
* Prototyping

---

## Where the Project is Heading

Planned improvements include:

* TURN server support for reliable NAT traversal
* Pluggable signaling strategies
* Optional integration with media servers:

  * Kurento
  * Janus
  * mediasoup
* Better room lifecycle management
* Authentication hooks
* Metrics / observability
* Publishing to Maven Central

Long-term, this could evolve into a flexible WebRTC backend layer that can scale from simple peer-to-peer apps to more advanced media architectures.

---

## Contact

Maintained by: Christopher David Albers
Email: [Chrisalberssoftware@gmail.com](mailto:Chrisalberssoftware@gmail.com)

If you're using this project or extending it, feel free to reach out or open an issue.
