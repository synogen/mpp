## General
Reads data from a Voltronic/MPPSolar inverter and publishes it to MQTT. Supports Home Assistant MQTT auto discovery.
Right now this is still incomplete but the following works:
- publishing QPIGS, QPIRI, QDOP and QMOD values
- raw command receiver
- setting battery trigger capacities via PBCC, PBDC and PSDC
- setting output and charging source priorities
- setting maximum charging current

See https://github.com/jblance/mpp-solar/tree/master/docs/protocols for different Voltronic protocols and commands

Tested with a MPPSolar PIP5048GEW, an OrangePi Zero 2W and a CH340 USB to serial adapter from Aliexpress.

## Build
1. `mvn clean`
2. `mvn package`

## Docker
Run build first. Then for running on a PI/ARM device:
1. `docker build -t mpp-test-arm --platform linux/arm64 .` 
2. `docker save mpp-test-arm -o mpp-test-arm.tar`
3. Copy `mpp-test-arm.tar` file to device
4. Run `docker load -i mpp-test-arm.tar` on device
5. Copy `mqtt.properties` to device and adapt configuration to your MQTT broker
6. Copy `docker-compose.yml` to device and adapt devices section, first port should match your host port
7. `docker-compose up` on device in the directory where docker-compose.yml is
8. You should see the log with something like "Published MQTT discovery configurations for Home Assistant"
9. If everything is alright quit the application with CTRL+C and run in daemon mode with `docker-compose -d` to keep it running

## Home Assistant
Requirements:
- Home Assistant MQTT integration installed and connected to a MQTT broker
- `mqtt.properties` configured to connect to that same MQTT broker
- `topicPrefix` in `mqtt.properties` set to `homeassistant`

HA should then automatically pick up the different published entities through its MQTT integration.

## TODO
- implement more commands from inverter protocol manual