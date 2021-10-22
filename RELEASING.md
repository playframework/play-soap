# Releasing

This is released from the `main` branch. Unless and older version needs patching, when it is released from a `1.1.x` branch. If there is no branch for the release that needs patching, create it from the tag.

## Cutting the release

### Requires contributor access

- Check the [draft release notes](https://github.com/playframework/play-soap/releases) to see if everything is there
- Wait until [main build finished](https://github.com/playframework/play-soap/actions) after merging the last PR
- Update the [draft release](https://github.com/playframework/play-soap/releases) with the next tag version (eg. `1.2.0`), title and release description
- Check that GitHub Actions release build has executed successfully (GitHub will start a [CI build](https://github.com/playframework/play-soap/actions) for the new tag and publish artifacts to Sonatype)

### Requires Sonatype access

- Go to [Staging repository](https://oss.sonatype.org/#stagingRepositories) and release the concrete staging repository

### Check Maven Central

- [Play SOAP @ Maven Central](https://repo1.maven.org/maven2/com/typesafe/play/play-soap-client_2.12/)
