[![Build Status](https://github.com/shinesolutions/aem-password-reset/workflows/CI/badge.svg)](https://github.com/shinesolutions/aem-password-reset/actions?query=workflow%3ACI)
[![Known Vulnerabilities](https://snyk.io/test/github/shinesolutions/aem-password-reset/badge.svg)](https://snyk.io/test/github/shinesolutions/aem-password-reset)

# Reset Password for Admin User AEM 6.x

This application resets the admin password to the default value without the existing password.

Learn more about AEM Password Reset:

* [Installation](#installation)
* [Usage](#usage)
* [Result Model](#result-model)
* [Error Handling](#error-handling)
* [Testing](#testing)
* [Versions History](#versions-history)

## Installation

### Supported AEM versions

| AEM | Password Reset |
|--|--|
| 6.5 | 1.1.x |
| 6.4 | 1.1.x |
| 6.3 | 1.0.x |
| 6.2 | 1.0.x |

### Requirements

This project requires Java 11 and Apache Maven. Run the `make` targets inside the
`shinesolutions/aem-platform-buildenv:6.4.1` Docker image, which has Java 11, Maven and RTK, started
from the directory above this repository. The image's default Java is 21, so this sets `JAVA_HOME`
to its Java 11 in `/usr/lib/jvm/java-11`. It also passes the release variables through by name and
your git identity for the release commits, and marks the mounted repository as safe for git (it is
owned by your user, not root):

```
$ docker run --rm -it -v "$(pwd)":/workspace -w /workspace/aem-password-reset \
    -v aem-password-reset-m2:/root/.m2 -v ~/.gitconfig:/root/.gitconfig:ro \
    -e GIT_CONFIG_COUNT=1 -e GIT_CONFIG_KEY_0=safe.directory \
    -e GIT_CONFIG_VALUE_0=/workspace/aem-password-reset \
    -e JAVA_HOME=/usr/lib/jvm/java-11 \
    -e MAVEN_GPG_KEY -e MAVEN_GPG_PASSPHRASE -e MAVEN_CENTRAL_USERNAME -e MAVEN_CENTRAL_PASSWORD \
    shinesolutions/aem-platform-buildenv:6.4.1 bash
```

### Download

Releases are published to [Maven Central](https://central.sonatype.com/namespace/com.shinesolutions)
under `com.shinesolutions:aem-password-reset-content` (the content package) and
`com.shinesolutions:aem-password-reset-bundle` (the OSGi bundle).

### Build from source

| Target | Description |
|---|---|
| `make deps` | Download all dependencies |
| `make build` | Build the bundle and the content package, and run the unit tests |
| `make install` | Build and install the artifacts into the local Maven repository |

```
$ make build
```

This creates the bundle `bundle/target/aem-password-reset-bundle-<version>.jar` and the content package
`content/target/aem-password-reset-content-<version>.zip`, which embeds the bundle.

## Usage

Drop `aem-password-reset-content-<version>.zip` into `<aem-home>/crx-quickstart/install` (create the `install` folder if it isn't there).

The bundle contains an activator that will execute as soon as the bundle is installed (see `Activator.java`).

### Resetting additional passwords

This bundle activator may take a pre-configured list of authorizable IDs via an OSGi configuration file.

/apps/system/config/com.shinesolutions.aem.passwordreset.Activator:
```
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="sling:OsgiConfig"
    pwdreset.authorizables="[admin,deployer,importer]"/>
```
With this configuration, the password for `deployer` will be reset to `deployer`, the password for `importer` will be reset to `importer`, etc.

Note: a change to the OSGI configuration *will* trigger a restart of the bundle which will reset the passwords.

## Result Model

Each configured authorizable's password, `admin` by default, is reset to its user ID.

The contents of `logs/error.log` should look something like this:

```
20.03.2017 16:25:40.485 *INFO* [OsgiInstallerImpl] com.shinesolutions.aem.passwordreset BundleEvent INSTALLED
20.03.2017 16:25:40.486 *INFO* [OsgiInstallerImpl] org.apache.sling.audit.osgi.installer Installed bundle com.shinesolutions.aem.passwordreset [471] from resource TaskResource(url=jcrinstall:/apps/aem-password-reset/install/aem-password-reset-bundle-0.0.1-SNAPSHOT.jar, entity=bundle:com.shinesolutions.aem.passwordreset, state=INSTALL, attributes=[org.apache.sling.installer.api.tasks.ResourceTransformer=:24:21:, Bundle-SymbolicName=com.shinesolutions.aem.passwordreset, Bundle-Version=0.0.1.SNAPSHOT], digest=1489987539235)
20.03.2017 16:25:40.521 *INFO* [OsgiInstallerImpl] com.shinesolutions.aem.passwordreset BundleEvent RESOLVED
20.03.2017 16:25:40.522 *INFO* [OsgiInstallerImpl] com.shinesolutions.aem.passwordreset BundleEvent STARTING
20.03.2017 16:25:40.526 *INFO* [OsgiInstallerImpl] com.shinesolutions.aem.passwordreset.Activator Changed the admin password
20.03.2017 16:25:40.527 *INFO* [OsgiInstallerImpl] com.shinesolutions.aem.passwordreset BundleEvent STARTED
20.03.2017 16:25:40.527 *INFO* [OsgiInstallerImpl] org.apache.sling.audit.osgi.installer Started bundle com.shinesolutions.aem.passwordreset [471]
```

Once the admin password has been reset, you may remove the package from the `install` directory to uninstall it.
The contents of `logs/error.log` should look something like this:

```
20.03.2017 16:32:40.365 *INFO* [OsgiInstallerImpl] com.shinesolutions.aem.passwordreset BundleEvent STOPPING
20.03.2017 16:32:40.365 *INFO* [OsgiInstallerImpl] com.shinesolutions.aem.passwordreset BundleEvent STOPPED
20.03.2017 16:32:40.365 *INFO* [OsgiInstallerImpl] com.shinesolutions.aem.passwordreset BundleEvent UNRESOLVED
20.03.2017 16:32:40.366 *INFO* [OsgiInstallerImpl] com.shinesolutions.aem.passwordreset BundleEvent UNINSTALLED
20.03.2017 16:32:40.367 *INFO* [OsgiInstallerImpl] org.apache.sling.audit.osgi.installer Uninstalled bundle com.shinesolutions.aem.passwordreset [471] from resource TaskResource(url=jcrinstall:/apps/aem-password-reset/install/aem-password-reset-bundle-0.0.1-SNAPSHOT.jar, entity=bundle:com.shinesolutions.aem.passwordreset, state=UNINSTALL, attributes=[org.apache.sling.installer.api.tasks.ResourceTransformer=:24:21:, Bundle-SymbolicName=com.shinesolutions.aem.passwordreset, Bundle-Version=0.0.1.SNAPSHOT], digest=1489987539235)
```

## Error Handling

The activator logs an error and leaves the password unchanged when it can't log in to the repository
(`Could not login to the repository`), an authorizable doesn't exist (`Could not find authorizable <id>`) or
the password change fails (`Could not change password for <id>`).
From AEM 6.4, logging in needs the bundle to be in the login admin whitelist, which the content package
configures in `/apps/aem-password-reset/config`.

## Testing

The bundle has JUnit unit tests of the activator, with the repository mocked by Mockito. `make ci`
cleans, downloads dependencies, then builds, runs the unit tests and installs the bundle and content
package into the local Maven repository:

```
$ make ci
```

To test the package itself, drop it into the `install` folder of a local AEM instance (see [Usage](#usage))
and log in as `admin` with the password `admin`.

## Versions History

Changes in each version are described in [CHANGELOG.md](CHANGELOG.md).

Releases are published to [Maven Central](https://central.sonatype.com/namespace/com.shinesolutions)
under `com.shinesolutions:aem-password-reset`, `aem-password-reset-bundle` and `aem-password-reset-content`, and
tagged `aem-password-reset-<version>` in git. Versions follow [Semantic Versioning](https://semver.org/):
use a major release for breaking changes, minor for new features and patch for fixes.
Between releases, the `pom.xml` files hold the next pre-release version, e.g. `1.1.2-pre.0`.

Versions are managed with [RTK](https://github.com/cliffano/rtk), configured in `.rtk.json`: it sets
the release version in the `pom.xml` files and CHANGELOG.md, commits and tags it
`aem-password-reset-<version>`, then commits the next pre-release version.

### Release targets

| Target | Description |
|---|---|
| `make release-test` | Sign `HEAD` and upload it to the Maven Central Portal as a pre-release test version without publishing it, then print the deployment ID for `release-status` and `release-drop`. Uploads the current release version with `-rc.0`, such as `1.1.2-rc.0` for `1.1.2-pre.0` |
| `make release-status DEPLOYMENT_ID=<id>` | Show a Maven Central Portal deployment's state (e.g. `VALIDATED`, `PUBLISHED`, `FAILED`) and errors, using `MAVEN_CENTRAL_USERNAME` and `MAVEN_CENTRAL_PASSWORD` |
| `make release-drop DEPLOYMENT_ID=<id>` | Drop (delete) a Maven Central Portal deployment that has not been published |
| `make publish` | Sign the checked-out version and upload it to the Maven Central Portal, then wait until it is validated. Run by the **Publish to Maven Central** workflow on a pushed `aem-password-reset-<version>` tag |
| `make release-major`, `release-minor`, `release-patch` | Release the next major, minor or patch version with RTK. Dates the `## [Unreleased]` CHANGELOG.md entries as the release, then commits and tags `aem-password-reset-<version>` locally; push with `git push --follow-tags` |

### Release requirements
- A GPG key for signing, with its public key on a keyserver such as `keyserver.ubuntu.com`
  (Maven Central verifies signatures against it)
- A Maven Central Portal user token for the `com.shinesolutions` namespace
- Both are read from environment variables only, never from files. Enter them in your shell, so
  the values stay hidden and out of your shell history:

  ```
  # GPG private key: run this, paste the whole key including the
  # -----BEGIN/END PGP PRIVATE KEY BLOCK----- lines, press Enter, then Ctrl-D
  $ stty -echo; MAVEN_GPG_KEY="$(cat)"; stty echo; export MAVEN_GPG_KEY

  # GPG passphrase and Central Portal user token: type or paste each value, then press Enter
  $ read -rs MAVEN_GPG_PASSPHRASE; export MAVEN_GPG_PASSPHRASE
  $ read -rs MAVEN_CENTRAL_USERNAME; export MAVEN_CENTRAL_USERNAME
  $ read -rs MAVEN_CENTRAL_PASSWORD; export MAVEN_CENTRAL_PASSWORD
  ```

  Check the key arrived complete without printing it: `printf '%s\n' "$MAVEN_GPG_KEY" | head -1` and
  `| tail -1` should show the BEGIN and END lines.

Run the release targets in the aem-platform-buildenv container from [Requirements](#requirements),
started after you set the variables so they are passed through.

### Release steps
1. Start from an up-to-date `master` with a green CI build and a clean working tree. Check that the
   `## [Unreleased]` entries in CHANGELOG.md describe everything in this release.
2. Check signing and Maven Central validation with a test upload of `HEAD`, in aem-platform-buildenv:
   ```
   $ make release-test
   ```
   Then drop the test deployment with `make release-drop DEPLOYMENT_ID=<id>`, or **Drop** in the
   Central Portal.
3. Create the release, either:
   - In GitHub Actions, run the **Release Major**, **Release Minor** or **Release Patch** workflow.
     It runs RTK and pushes the commits and the tag. Then `git pull --tags` to fetch them.
   - Or locally in aem-platform-buildenv, choosing `release-major`, `release-minor` or `release-patch`:
     ```
     $ make release-patch
     ```
     This dates the `## [Unreleased]` entries as the new version in CHANGELOG.md, commits the release
     version, tags it `aem-password-reset-<version>` and commits the next pre-release version. Nothing
     is pushed. Review the result with `git log -3` and `git show aem-password-reset-<version>:CHANGELOG.md`.
4. If you released locally, push the commits and the tag from your machine, outside the container,
   where your GitHub credentials are:
   ```
   $ git push --follow-tags
   ```
5. Upload the tagged version to Maven Central. The pushed tag triggers the **Publish to Maven
   Central** workflow, which runs `make publish` in aem-platform-buildenv with the
   `SHINEOPENSOURCE_MAVEN_CENTRAL_USERNAME`, `SHINEOPENSOURCE_MAVEN_CENTRAL_PASSWORD`,
   `SHINEOPENSOURCE_MAVEN_GPG_KEY` and `SHINEOPENSOURCE_MAVEN_GPG_PASSPHRASE` GitHub secrets. To
   upload it yourself instead, in aem-platform-buildenv:
   ```
   $ git checkout aem-password-reset-<version>
   $ make publish
   $ git checkout master
   ```
   Maven Central validates the upload (signatures, checksums and POM) and the build waits until it
   is validated. The deployment then waits at [central.sonatype.com](https://central.sonatype.com)
   → Publishing → Deployments: click **Publish** to release it, or **Drop** to discard it without
   publishing. A published version can't be changed or removed. To publish automatically once
   validated, run `make publish MVN="mvn -B -ntp -DautoPublish=true"`.

   Without a Central Portal login, use the user token and the deployment ID printed by `make publish`,
   in aem-platform-buildenv:
   ```
   $ make release-status DEPLOYMENT_ID=<id>
   $ make release-drop DEPLOYMENT_ID=<id>
   ```
6. Unset the secrets when you are done:
   `unset MAVEN_GPG_KEY MAVEN_GPG_PASSPHRASE MAVEN_CENTRAL_USERNAME MAVEN_CENTRAL_PASSWORD`
