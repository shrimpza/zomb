# ZOMB 
[![Build Status](https://drone.io/github.com/shrimpza/zomb/status.png)](https://drone.io/github.com/shrimpza/zomb/latest)

ZOMB provides an API-driven HTTP back-end for an IRC bot style command
processor, where functionality is implemented in remotely-hosted HTTP based
plugin services.

## API documentation

- [Client API](https://github.com/shrimpza/zomb/wiki/Client-API)
- [Plugin API](https://github.com/shrimpza/zomb/wiki/Plugin-API)
- [Client Application Management API](https://github.com/shrimpza/zomb/wiki/Application-API)


## Usage

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
