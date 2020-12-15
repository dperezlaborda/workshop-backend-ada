FROM openjdk:8-jre-alpine
ADD /@project.build.finalName@.jar //
ADD / /
ADD /newrelic-agent-*.jar /newrelic-agent.jar
ADD /dd-java-agent-*.jar /dd-java-agent.jar
ADD /elastic-apm-agent-*.jar  /elastic-apm-agent.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/@project.build.finalName@.jar", "--server.port=8080"]