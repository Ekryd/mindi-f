# Minimal Dependency Injection Framework #
Allows a developer to use dependency injection for small scale projects, such as tools or plugins.

## Usage ##
Injects dependencies into a Java object. Injected instances will be reused as
long as they belong to the same Context.
Example:
```
public class BaseComponentImpl {
  @Dependency
  private AnotherComponentImpl anotherComponent;

  . . .
}
```
Calling code:
```
// Create base component
final BaseComponentImpl component = new BaseComponentImpl();
// Inject dependencies recursively
new Context().inject(component);
```
### Alternative usage ###
The dependency can also be an interface with a specified implementing class
```
  @Dependency(AnotherComponentImpl.class)
  private AnotherComponent anotherComponent;
```
If there is only one implementing class then the framework can find it (this is a bit slower)
```
  @Dependency
  private AnotherComponent anotherComponent;
```


For up-to-date documentation, please see the javadoc for the main class, [Context.java](https://github.com/GreenThingSalad/mindi-f/blob/master/src/main/java/se/mine/mindif/Context.java)

## Requirements ##
MinDI F requires Java 1.5. 

[Slf4j](http://www.slf4j.org/index.html) can be used, if you need logging.

## Download ##

Maven users can add this project as a dependency with the following additions to a POM.xml file:

```
<repositories>
  <repository>
    <id>googlecode.mindi-f</id>
    <url>http://mindi-f.googlecode.com/svn/repo</url>       
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.google.code.mindi-f</groupId>
    <artifactId>mindi-f</artifactId>
    <version>0.1.2</version>
  </dependency>
</dependencies>
```

## Disclaimer ##

This framework is not designed for large scale enterprise systems, there are other frameworks for that (such as Spring or EJB3).
This framework focus on simplicity and a small codebase.
