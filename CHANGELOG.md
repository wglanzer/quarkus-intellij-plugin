# Quarkus Run Configs

## [Unreleased]

## [1.3.1] - 2023-08-04

### Fixed
- Configuration properties "Tasks", "Profiles" and "Goals" reset after IntelliJ restart (#9)
- Configuration properties "Tasks", "Profiles" and "Goals" could not be stored in a project directory
- Fixed exceptions on IntelliJ startup, caused by parsing the JDK in a run config editor

## [1.3.0] - 2023-08-01

### Added
- Added support for custom quarkus goals (maven), profiles (maven) and tasks (gradle) in run configurations
- Removed "compileBeforeLaunch" in run configurations, because it is more transparent to use the newly added custom settings

## [1.2.0] - 2023-03-05

### Added
- Added support for IntelliJ 2023.1 (231.7864.76+)

## [1.1.2] - 2020-11-24

### Changed
- Renamed Plugin to "Quarkus Run Config"
- Changed plugin coordinates and icon
- Build with JDK11

## [1.1.1] - 2020-05-28

### Fixed
- Gradle integration should run 'assemble' instead of 'build', because triggering unit tests is not necessary for quarkusDev

## [1.1.0] - 2020-05-24

### Added
- Added (preview) support for gradle

### Fixed
- Fixed message order, if messages arrive too early
- Fixed broken console logs on slow machines

## [1.0.3] - 2020-05-10

### Fixed
- Removed unnecessary "ARTIFACT_RESOLVING" and "ARTIFACT_RESOLVED" logs from maven output

[Unreleased]: https://github.com/conceptivesolutions/quarkus-intellij-plugin/compare/v1.3.1...HEAD

[1.3.1]: https://github.com/conceptivesolutions/quarkus-intellij-plugin/compare/v1.3.0...v1.3.1
[1.3.0]: https://github.com/conceptivesolutions/quarkus-intellij-plugin/compare/v1.2.0...v1.3.0
[1.2.0]: https://github.com/conceptivesolutions/quarkus-intellij-plugin/compare/v1.1.2...v1.2.0
[1.1.2]: https://github.com/conceptivesolutions/quarkus-intellij-plugin/compare/v1.1.1...v1.1.2
[1.1.1]: https://github.com/conceptivesolutions/quarkus-intellij-plugin/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/conceptivesolutions/quarkus-intellij-plugin/compare/v1.0.3...v1.1.0
[1.0.3]: https://github.com/conceptivesolutions/quarkus-intellij-plugin/commits/v1.0.3
