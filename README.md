
## 1. Protocol Compiler Installation
Source: [https://github.com/protocolbuffers/protobuf/blob/master/README.md]

Protocol Buffers( download protobuf-java-3.13.0.tar.gz ) [https://github.com/protocolbuffers/protobuf/releases]
![alt text](https://imgur.com/EwJgDll.png)

## 2. Install Maven
[https://www.javahelps.com/2017/10/install-apache-maven-on-linux.html]

Check if it works: ``` mvn --version ```

## 3. Compile the Protocol
Source: [https://github.com/protocolbuffers/protobuf/tree/master/java]

Run: ``` mvn test ```

![alt text](https://imgur.com/E5ynFKM.png)

If some tests fail, this library may not work correctly.

Navigate to your protobuf directory/java ```/Downloads/protobuf-3.13.0/java```

Run: ``` mvn install ```

![alt text](https://imgur.com/KyxYcyk.png)

If this compiles succesefully it means everything works and the project can be compiled.

## 4. Setup Eclipse

Pull the project, exclude target folder, because that you need to generate for yourself or follow this tutorial:
```https://www.youtube.com/watch?v=JFzAe9SvNaU&list=PLI5t0u6ye3FGXJMh5kU2RvN0xrul67p7R```

and use the same pom.xml and the same proto files to generate Grpc files.
