To run application: **mvn spring-boot:run**

By default it's run in **localhost:8080** with **local** profile

`It's required Java 1.8 version.`

To access to the database **localhost:8080/h2-console/login.do**
`Password: password`




All services response errors with this structure:
```json
{
    "timestamp": "2020-10-21T00:18:22.110546",
    "status": "[HTTP STATUS CODE]",
    "code": "[ERROR CODE]",
    "message": "[ERROR MESSAGE]",
    "debugMessage": "[DEBUG MESSAGE]"
}
```
