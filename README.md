# ZOMB

ZOMB provides an API-driven HTTP back-end for an IRC bot style command
processor, where functionality is implemented in remotely-hosted HTTP based
plugin services.

# Client API

Interaction with ZOMB is achieved by making HTTP POST requests to the root
path.

Client applications are each provided a unique "API key", which provides both
access control and organisation (one ZOMB instance may serve several clients,
each with different configurations).

## Request

Queries are in JSON format, and are structured as follows (the body of the
POST request):

```json
{
  "key": "client-app-api-key",
  "user": "jane",
  "query": "random query here"
}
```

- `key`
  the client application API key.
- `user`
  name of the user who issued the query - it will be passed on to plugin
  services and may be used for statistics or response personalisation.
- `query`
  the query as requested by the user.

## Response

Responses take the following format:

```json
{
  "plugin": "plugin-name",
  "user": "jane",
  "query": "random query here",
  "response": [
    "line 1",
    "multi-line response line 2"
  ],
  "image": "path/to/image"
}
```

- `plugin`
  the name of the plugin which produced the response.
- `user`
  user who originally issued the query.
- `query`
  the original query string.
- `response`
  array of strings to display to the user. may be markdown formatted - clients
  are expected to implement markdown processing appropriate to their interface.
- `image`
  optionally, an image may be returned which some clients may be able to make
  use of.


# Plugin API

The plugin API is largely the same as the client API, but somewhat simplified
for ease of plugin implementation.

Plugins are referenced via a unique URL. A GET request to the URL should
provide plugin information in the following format:

```json
{
  "plugin": "plugin-name",
  "help": "plugin help string",
  "contact": "you <you@mail>"
  "commands": [
    {
      "command": "command-name",
      "help": "command help string",
      "args": 1,
      "pattern": "argument regex"
    }
  ]
```

- plugin
  unique plugin name.
- help
  a brief help string describing what the plugin is and does, optionally
  markdown formatted.
- contact
  plugin author contact information.
- commands
  a list of commands exposed by this plugin.
 - command
   the command name.
 - help
   short help description for this specific command, optionally markdown
   formatted.
 - args
   number of arguments this command expects - allows ZOMB to pre-validate query
   input prior to submitting it to the plugin for execution. a value of 0 will
   not perform validation (allows any number of arguments, including none)
 - pattern
   optional regular expression which can be applied to a query to validate it,
   prior to submitting to the plugin for execution.


# Usage

## Plugin Management

In order for ZOMB to be useful, plugins must be added. Plugins implement
the actual functionality ZOMB provides.

Plugin management is achieved using the same language and request style as
normal usage.

Add a plugin:

```
  plugin add http://url.to/plugin/script
```

Remove a plugin:

```
  plugin remove plugin-name
```

## Help And Information


List available plugins:

```
  plugin list
```

Plugins are expected to provide a brief help blurb, which can be accessed as
follows:

```
  help plugin-name
```

See the available commands a plugin provides:

```
  help plugin-name commands
```

To access help for a specific command:

```
  help plugin-name command-name
```