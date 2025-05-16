# Reactive SOAP

[![Twitter Follow](https://img.shields.io/twitter/follow/playframework?label=follow&style=flat&logo=twitter&color=brightgreen)](https://twitter.com/playframework)
[![Discord](https://img.shields.io/discord/931647755942776882?logo=discord&logoColor=white)](https://discord.gg/g5s2vtZ4Fa)
[![GitHub Discussions](https://img.shields.io/github/discussions/playframework/playframework?&logo=github&color=brightgreen)](https://github.com/playframework/playframework/discussions)
[![StackOverflow](https://img.shields.io/static/v1?label=stackoverflow&logo=stackoverflow&logoColor=fe7a16&color=brightgreen&message=playframework)](https://stackoverflow.com/tags/playframework)
[![YouTube](https://img.shields.io/youtube/channel/views/UCRp6QDm5SDjbIuisUpxV9cg?label=watch&logo=youtube&style=flat&color=brightgreen&logoColor=ff0000)](https://www.youtube.com/channel/UCRp6QDm5SDjbIuisUpxV9cg)
[![Twitch Status](https://img.shields.io/twitch/status/playframework?logo=twitch&logoColor=white&color=brightgreen&label=live%20stream)](https://www.twitch.tv/playframework)
[![OpenCollective](https://img.shields.io/opencollective/all/playframework?label=financial%20contributors&logo=open-collective)](https://opencollective.com/playframework)

[![Build Status](https://github.com/playframework/play-soap/actions/workflows/build-test.yml/badge.svg)](https://github.com/playframework/play-soap/actions/workflows/build-test.yml)
[![Maven](https://img.shields.io/maven-central/v/org.playframework/play-soap-client_2.13.svg?logo=apache-maven)](https://mvnrepository.com/artifact/org.playframework/play-soap-client_2.13)
[![Repository size](https://img.shields.io/github/repo-size/playframework/play-soap.svg?logo=git)](https://github.com/playframework/play-soap)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/playframework/play-soap&style=flat)](https://mergify.com)

Play SOAP allows an application to make calls on a remote web service using SOAP. It provides a reactive interface to doing so, making HTTP requests asynchronously and returning promises/futures of the result.

The detail description of how to use the library is described in the [Docs](https://playframework.github.io/play-soap/2.x/) point.

## Docs

For testing documentation locally use a few next commands (for more details see [Antora workflow](https://github.com/playframework/.github/blob/main/.github/workflows/antora.yml)):

```bash
cd docs
npm i -D -E @antora/cli @antora/site-generator @antora/lunr-extension
npx antora local-antora-playbook.yml
```

Then open in browser generated documentation from `<repo_path>/docs/build/site`.

## Releasing a new version

See https://github.com/playframework/.github/blob/main/RELEASING.md
