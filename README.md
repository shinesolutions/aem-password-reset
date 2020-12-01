[![Build Status](https://github.com/shinesolutions/aem-password-reset/workflows/CI/badge.svg)](https://github.com/shinesolutions/aem-password-reset/actions?query=workflow%3ACI)
[![Known Vulnerabilities](https://snyk.io/test/github/shinesolutions/aem-password-reset/badge.svg)](https://snyk.io/test/github/shinesolutions/aem-password-reset)

# Reset Password for Admin User AEM 6.x

This application resets the admin password to the default value without the existing password.

## Supported AEM versions

| AEM | Password Reset |
|--|--|
| 6.4 | 1.1.x |
| 6.3 | 1.0.x |
| 6.2 | 1.0.x |

## Usage

Drop `aem-password-reset-content-<version>.zip` into `<aem-home>/crx-quickstart/install` (create the `install` folder if it isn't there).

The bundle contains an activator that will execute as soon as the bundle is installed (see `Activator.java`).

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

## Resetting additional passwords

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

## Building

This project uses Maven for building. Common commands:

From the root directory, run ``mvn clean install`` to build the bundle and content package.

The artifact will be located at `aem-password-reset/content/target/aem-password-reset-content-<version>.zip`.

## Release

Create a release branch off the `master` branch
```
git branch release/X.X.X
```

Prepare the release (use vX.X for the tag)
```
mvn release:prepare
```

Push the branch to the repository
```
git push origin
```

Checkout the newly created tag and build the project
```
git checkout tags/vX.X.X
```

```
mvn clean package
```

Attach the CRX package to the release on Github and merge back to `master`.


