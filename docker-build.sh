mvn clean
mvn package
docker build -t mpp-test-arm --platform linux/arm64 .
docker save mpp-test-arm | gzip > mpp-test-arm.tar.gz