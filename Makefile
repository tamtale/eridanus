PORT?=42069
IP?=localhost

build:
	./gradlew build

run-host:
	./gradlew run --args="host $(PORT)"

run-host-start:
	./gradlew run --args="host $(PORT) start"

run-client:
	./gradlew run --args="client $(IP) $(PORT)"

run-client-start:
	./gradlew run --args="client $(IP) $(PORT) start"
