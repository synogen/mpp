## General
Reads data from a Voltronic/MPPSolar inverter and publishes it to MQTT with Home Assistant discovery.
Right now this is just an incomplete testbed but publishing QPIGS values works.

Tested with a MPPSolar PIP5048GEW and a CH340 USB to serial adapter from Aliexpress.

TODO more logging
TODO timestamp logs

## Build
1. `mvn clean`
2. `mvn package`

## Docker
Run build first. Then for running on a PI/ARM device:
1. `docker build -t mpp-test-arm --platform linux/arm64 .` 
2. `docker save mpp-test-arm -o mpp-test-arm.tar`
3. Copy `mpp-test-arm.tar` file to device
4. `docker load -i mpp-test-arm`
5. Copy `mqtt.properties` to device and adapt configuration to your MQTT broker
6. Copy `docker-compose.yml` to device and adapt devices section, first port should match your host port
7. `docker-compose up` on device in the directory where docker-compose.yml is
8. You should see the log with something like "Published MQTT discovery configurations for Home Assistant"
9. If everything is alright quit the application with CTRL+C and run in daemon mode with `docker-compose -d` to keep it running