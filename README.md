# securestring [![Build Status](https://travis-ci.org/choffmeister/securestring.png?branch=master)](https://travis-ci.org/choffmeister/securestring)

An implementation to securely hold a private string in memory implemented in Scala 2.10.3.

# Example

```scala
val password = Array[Char]('p', 'a', 's', 's')
val securestring = SecureString(password)

// original array is wiped out
println(password.toSeq) // prints 4 times the char '\0'

// access the secret string
// encrypted string is decrypted and passed to the inner funciton
securestring.read { plain =>
  println(plain.mkString) // prints "pass"
}

// after the inner function the decrypted char array is automatically wiped out
```

# Use with SBT

Add the following configuration to your `build.sbt`:

```scala
resolvers += "repo.choffmeister.de" at "http://repo.choffmeister.de/maven2"

libraryDependencies += "de.choffmeister" %% "securestring" % "0.0.1"
```
