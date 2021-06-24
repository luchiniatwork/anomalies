# anomalies

## Table of Contents

* [Getting Started](#getting-started)
* [Rationale](#rationale)
* [The Categories](#the-categories)
* [Inspiration](#inspiration)
* [Bugs](#bugs)
* [Help!](#help)


## Getting Started

Add `anomalies` as a dependency in your `deps.edn` file:

``` clojure
{:deps
 {net.clojars.luchiniatwork/anomalies {:mvn/version "0.0.2"}}}
```

Require `anomalies` where needed:

``` clojure
(require '[anomalies.core :as anom])
```

Throw with `anom/throw` instead:

``` clojure
(anom/throw "Oops")
```

Explore thrown expcetion with `ex-data`:

``` clojure
(ex-data e) ;;=> #:anomalies.core{:message "Oops,
                                  :category :anomalies.core/unavailable,
                                  :retry-possible? true,
                                  :fix :make-sure-callee-healthy}
```

`anom/throw-anom` supports defaults to `::anom/unavailable`. You can
specify a map with your intended category as a second param (the first
being a message). The second param map can also have a fully qualified
keyword as an `::anom/id` for this anomaly.

## Rationale

Anomalies capture errors as information that is simple, actionable,
generic, and extensible.

* Simple: Anomalies contain only information about the error, not
  e.g. flow control or causality.
* Actionable: the `::anom/category` aims to be a top-level
  partitioning of all kinds of errors, allowing many programs that need to
  branch during error-handling to branch *only* on this keyword.
* Generic: Anomalies are represented as ordinary maps, and can be created
  and consumed without any special API.
* Extensible: As maps are open, applications can add their own context
  via namespaced keywords. That said, try to do as much as possible
  by dispatching only via `::anom/category`.

Anomalies overlap in purpose with e.g. Java exceptions and HTTP
responses. The differences are instructive:

* Java exceptions are not simple, as they combine error information
  with a flow control mechanism. This is a problem in e.g. async
  applications and transductions. Both of these contexts need to talk
  about errors, but do not want to utilize exceptions' flow control by
  stack unwinding.

* HTTP responses provide useful partitioning via status codes. But
  since status codes appear inside a server *response* they cannot
  possibly cover e.g. failure to get a response at all.

## The Categories

The category column contains the name of an anomaly category within
the `cognitect.anomalies` namespace.

The retry column is "yes" if a replay of the same activity might
reasonably lead to a different outcome. When a program encounters a
retryable anomaly, it may be reasonable to back off and try again.

The "fix" column provides an example of how a programmer or operator
might fix problems in this category.

The "song" column contains a Hall and Oates song. The idea that Hall
and Oates are software gurus is controversial in some circles, so you
can treat this as flavortext.

| category     | retry-possible? | fix                      | song                      |
| ----         | ----            | ---                      | ---                       |
| :unavailable | true            | make sure callee healthy | Out of Touch              |
| :interrupted | true            | stop interrupting        | It Doesn't Matter Anymore |
| :incorrect   | false           | fix caller bug           | You'll Never Learn        |
| :forbidden   | false           | fix caller creds         | I Can't Go For That       |
| :unsupported | false           | fix caller verb          | Your Imagination          |
| :not-found   | false           | fix caller noun          | She's Gone                |
| :conflict    | false           | coordinate with callee   | Give It Up                |
| :fault       | false           | fix callee bug           | Falling                   |
| :busy        | true            | backoff and retry        | Wait For Me               |

## Inspiration

Or maybe just "copy-and-paste plus some utility functions." This
project draws largely from
[cognitect.anomalies](https://github.com/cognitect-labs/anomalies). `cognitect.anomalies`
is a great idea and I noticed I used the pattern over and over without
ever bringing it as a dependency because it's so light as to provide
just a spec.

In practice I caught myself writing the same simple utility functions
for several projects so decided to wrap them around my own flavor of
anomalies.

## Bugs

If you find a bug, submit a [GitHub
issue](https://github.com/luchiniatwork/anomalies/issues).

## Help

This project is looking for team members who can help this project
succeed!  If you are interested in becoming a team member please open
an issue.

## License

Copyright © 2021 Tiago Luchini

Distributed under the MIT License.
