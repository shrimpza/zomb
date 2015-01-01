# ZOMB 
[![Build Status](https://drone.io/github.com/shrimpza/zomb/status.png)](https://drone.io/github.com/shrimpza/zomb/latest)

ZOMB provides an API-driven HTTP back-end for an IRC bot style command
processor, where functionality is implemented in remotely-hosted HTTP based
plugin services.

## Clients

To actually "use" ZOMB, a client application is required:

- [zomb-web](https://github.com/shrimpza/zomb-web/) - a browser-based client
- [zomb-cli](https://github.com/shrimpza/zomb-cli/) - a command-line client suitable for use from a terminal

## API documentation

- [Client API](https://github.com/shrimpza/zomb/wiki/Client-API)
- [Plugin API](https://github.com/shrimpza/zomb/wiki/Plugin-API)
- [Client Application Management API](https://github.com/shrimpza/zomb/wiki/Application-API)


## Client Usage

The following are commands issued by a user via a ZOMB client (see
[zomb-web](https://github.com/shrimpza/zomb-web/) as an example). The same
interactions may be achieved programmatically via the Client API.

### General Usage

Instructions are given in the following format:

`<plugin-name> <command> [arguments]`

- `<plugin-name>`<br/>
  name of an installed plugin
- `<command>`<br/>
  a command which exists within the plugin specified
- `[arguments]`<br/>
  many commands require additional arguments, which may be passed to the
  command as a space-separated list. multi-word arguments may be surrounded
  by double or single quotes

### Plugin Management

In order for ZOMB to be useful, plugins must be added. Plugins implement
the actual functionality ZOMB provides.

Plugin management is achieved using the same language and request style as
normal usage.

**Add a plugin:**

`plugin add http://url.to/plugin/script`

**Remove a plugin:**

`plugin remove plugin-name`

### Help And Information

**List available (installed) plugins:**

`plugin list`

**Plugin information:**

`help show plugin-name`

**List plugin commands:**

`help list plugin-name`

**Plugin command help:**

`help show plugin-name command-name`
