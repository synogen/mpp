FROM eclipse-temurin:21.0.2_13-jre-alpine

COPY ./target/mpp-1.0-SNAPSHOT-jar-with-dependencies.jar /opt/mpp/mpp.jar

WORKDIR /opt/mpp
CMD java -jar mpp.jar
# for remote debugging
#CMD java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar mpp.jar