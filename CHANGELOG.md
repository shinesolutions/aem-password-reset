# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2026-10-27
### Added
- Added Makefile targets for ci, clean, deps, build, install, release-test, publish, release-status, release-drop and release-major/minor/patch
- Added Maven Central credentials to `.mvn/settings.xml`, read from environment variables
- Added Release Major/Minor/Patch GitHub workflows, and a Publish to Maven Central workflow that runs `make publish` on a pushed release tag with the Maven Central and GPG secrets

### Changed
- Builds, publishes and releases in aem-platform-buildenv 6.4.1, with its Java 11
- Publish to Maven Central through the Central Portal (central-publishing-maven-plugin) instead of the retired OSSRH
- Maven Central deployments wait for a manual Publish or Drop in the Central Portal, use `-DautoPublish=true` to publish automatically
- Artifact signing now runs in the `release` Maven profile, replacing `sign-artifacts`, with maven-gpg-plugin 3.2.8
- Releases are now made with RTK, by `make release-major|minor|patch` or the Release Major/Minor/Patch GitHub workflows, replacing maven-release-plugin, and tagged `aem-password-reset-<version>`
- Development versions are now pre-release versions such as `1.1.2-pre.0` instead of `-SNAPSHOT`
- CI now runs `make ci` in aem-platform-buildenv 6.4.1 with Java 11, through cliffano/command-docker-action
- Simplify CI workflow to not use action files anymore
- Restructured README into Installation, Usage, Result Model, Error Handling, Testing and Versions History
- Converted CHANGELOG.md to the Keep a Changelog format, which RTK updates

## [1.1.1] - 2019-02-27
### Changed
- Increased the bundle start level to 21 to ensure supporting services start beforehand

## [1.1.0] - 2018-06-29
### Added
- Added bundle whitelist config (required to use the admin session from AEM 6.4)

## [1.0.2] - 2017-04-21
### Changed
- Specified vendor property for bundle and services

### Fixed
- Fixed bundle project name

## [1.0.1] - 2017-04-04
### Added
- Added the ability to configure the authorizable IDs that need to be reset via an OSGi config file

## [0.0.1] - 2017-03-21
### Added
- Initial version

[2.0.0]: https://github.com/shinesolutions/aem-password-reset/compare/aem-password-reset-1.1.1...aem-password-reset-2.0.0
[1.1.1]: https://github.com/shinesolutions/aem-password-reset/compare/aem-password-reset-1.1.0...aem-password-reset-1.1.1
[1.1.0]: https://github.com/shinesolutions/aem-password-reset/compare/aem-password-reset-1.0.2...aem-password-reset-1.1.0
[1.0.2]: https://github.com/shinesolutions/aem-password-reset/compare/aem-password-reset-1.0.1...aem-password-reset-1.0.2
[1.0.1]: https://github.com/shinesolutions/aem-password-reset/compare/aem-password-reset-0.0.1...aem-password-reset-1.0.1
[0.0.1]: https://github.com/shinesolutions/aem-password-reset/releases/tag/aem-password-reset-0.0.1
