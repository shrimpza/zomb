# ZOMB 
[![Build Status](https://drone.io/github.com/shrimpza/zomb/status.png)](https://drone.io/github.com/shrimpza/zomb/latest)

ZOMB provides an API-driven HTTP back-end for an IRC bot style command
processor, where functionality is implemented in remotely-hosted HTTP based
plugin services.


## Client API

Interaction with ZOMB is achieved by making HTTP POST requests to the root
path.

Client applications are each provided a unique "API key", which provides both
access control and organisation (one ZOMB instance may serve several clients,
each with different configurations).


### Request

Queries are in JSON format, and are structured as follows (the body of the
POST request):

```json
{
  "key": "client-app-api-key",
  "user": "jane",
  "query": "random query here"
}
```

- `key`<br/>
  the client application API key.
- `user`<br/>
  name of the user who issued the query - it will be passed on to plugin
  services and may be used for statistics or response personalisation.
- `query`<br/>
  the query as requested by the user.


### Response

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

- `plugin`<br/>
  the name of the plugin which produced the response.
- `user`<br/>
  user who originally issued the query.
- `query`<br/>
  the original query string.
- `response`<br/>
  array of strings to display to the user. may be markdown formatted - clients
  are expected to implement markdown processing appropriate to their interface.
- `image`<br/>
  optionally, an image may be returned which some clients may be able to make
  use of.


## Plugin API

The plugin API is largely the same as the client API, but somewhat simplified
for ease of plugin implementation. Each plugins is referenced via a unique URL.


### Plugin Definition

A GET request to the plugin URL should provide plugin information in the
following format:

```json
{
  "plugin": "plugin-name",
  "help": "plugin help string",
  "contact": "you <you@mail>",
  "commands": [
    {
      "command": "command-name",
      "help": "command help string",
      "args": 1,
      "pattern": "argument regex"
    }
  ]
}
```

- `plugin`<br/>
  unique plugin name.
- `help`<br/>
  a brief help string describing what the plugin is and does, optionally
  markdown formatted.
- `contact`<br/>
  plugin author contact information.
- `commands`<br/>
  a list of commands exposed by this plugin.
 - `command`<br/>
   the command name.
 - `help`<br/>
   short help description for this specific command, optionally markdown
   formatted.
 - `args`<br/>
   number of arguments this command expects - allows ZOMB to pre-validate query
   input prior to submitting it to the plugin for execution. a value of 0 will
   not perform validation (allows any number of arguments, including none)
 - `pattern`<br/>
   optional regular expression which can be applied to a query to validate it,
   prior to submitting to the plugin for execution. if set, the args property
   will be ignored and the pattern applied to the entire query.


### Plugin Execution

When ZOMB has determined that a specific plugin is responsible for executing
a query issued by a user, a POST request will be made to the plugin URL, with
the following body:

```json
{
  "application": "unique-identifier",
  "user": "jane",
  "command": "command-name",
  "args": ["arg1", "arg2"],
  "query": "command-name arg1 arg2"
}
```

- `application`<br/>
  a unique identifier for an application. allows a plugin to identify requests
  from the same application, without exposing any application information.
- `user`<br/>
  name of the user who issued the request.
- `command`<br/>
  name of the command requested, as parsed by ZOMB.
- `args`<br/>
  list of parsed argument strings, in order.
- `query`<br/>
  the original query, as made by the user, excluding the plugin name.

The expected response is a simplified version of the Client API response:

```json
{
  "response": [
    "line 1",
    "multi-line response line 2"
  ],
  "image": "path/to/image"
}
```

- `response`<br/>
  array of strings to display to the user. may be markdown formatted - clients
  are expected to implement markdown processing appropriate to their interface.
  plugins are encouraged to keep responses as simple as possible, and clients
  should be assumed to have very simple output capabilities - for example CLI
  or IRC.
- `image`<br/>
  optionally, an image may be returned which some clients may be able to make
  use of; an image should never be the only expected output.


### Reserved Names

As the query language for plugins is also shared by ZOMB's internal functions,
there are a few names which may not be used for either plugins or commands:

A plugin may not be named any of the following:

- plugin
- help


## Usage

### Plugin Management

In order for ZOMB to be useful, plugins must be added. Plugins implement
the actual functionality ZOMB provides.

Plugin management is achieved using the same language and request style as
normal usage.

Add a plugin:

```plugin add http://url.to/plugin/script```

Remove a plugin:

```plugin remove plugin-name```

### Help And Information

List available plugins:

```plugin list```

Plugins are expected to provide a brief help blurb, which can be accessed as
follows:

```help show plugin-name```

See the available commands a plugin provides:

```help list plugin-name```

To access help for a specific command:

```help show plugin-name command-name```
