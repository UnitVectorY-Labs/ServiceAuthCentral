# common

This provides a set of common useful services.

## EntropyService

The entropy services provides a source of randomness for various applications.

- `SystemEntropyService`: Implementation that provides randomness
- `StaticEntropyService`: Implementation that provides static data instead of random data for testing

## TimeService

The time service provides a source for the current time.

- `SystemTimeService`: Implementation that provides the current time
- `StaticTimeService`: Implementation that provides static time instead of current time for testing
