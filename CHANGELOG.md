# Quarkus Run Configs

## [1.2.0]
### Changed
- Rebuilt plugin to be ready for IDEA 2023.1

## [1.1.2]
### Changed
- Renamed Plugin to "Quarkus Run Config"
- Changed plugin coordinates and icon
- Build with JDK11

## [1.1.1]
### Fixed
- Gradle integration should run 'assemble' instead of 'build', because triggering unit tests is not necessary for quarkusDev

## [1.1.0]
### Added
- Added (preview) support for gradle

### Fixed
- Fixed message order, if messages arrive too early
- Fixed broken console logs on slow machines

## [1.0.3]
### Fixed
- Removed unnecessary "ARTIFACT_RESOLVING" and "ARTIFACT_RESOLVED" logs from maven output
