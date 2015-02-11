/*
 * Copyright © 2015 Typesafe, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form or
 * by any means without the express written permission of Typesafe, Inc.
 */

// Update this when a new patch of Reactive Platform is available
val rpVersion = "15v01p01"

// Update this when a major version of Reactive Platform is available
val rpUrl = "https://private-repo.typesafe.com/typesafe/for-subscribers-only/DFDB5DD187A28462DDAF7AB39A95A6AE65983B23"

addSbtPlugin("com.typesafe.rp" % "sbt-typesafe-rp" % rpVersion)

// The resolver name must start with typesafe-rp
resolvers += "typesafe-rp-mvn" at rpUrl

// The resolver name must start with typesafe-rp
resolvers += Resolver.url("typesafe-rp-ivy",
  url(rpUrl))(Resolver.ivyStylePatterns)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
