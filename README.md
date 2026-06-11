To use this library you need to include the following in your pom.xml:
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

you will also need a github token with read:packages scope

add the following block to your settings.xml in the .m2 directory e.g. 'C:\Users\YOURNAME\\.m2\settings.xml' ( add a settings.xml file in your .m2 directory if it doesnt already exist )
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
run mvn clean install and then mvn-spring-boot:run and it should be good to go. check out my ui example if you want something that works out of the box with it. 
https://github.com/AlbersSoftware/web-rtc-with-chat-simple-ui-example

I will look into publishing to Maven Central when the library is more defined and potientially adds support for Kurento, Janus, mediaSoup APIS etc.

