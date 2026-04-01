# Glaze
A lightweight Fabric mod for DonutSMP that adds real-time average Auction House prices to item tooltips.

## Features
Live Price Fetching: Automatically retrieves the lowest active prices from the DonutSMP API.

Smart Averaging: Uses the last 5 valid listings to give you a stable market average.

Rate-Limit Protection: Built-in shielding to prevent API bans (250 request cap with cooldown).

In-Game Configuration: Manage your API key directly through chat commands.

## Commands
/glaze api <key> - Sets and saves your DonutSMP API key.

/glaze api delete - Removes your API key from the local config for privacy.

## Installation
Download the latest .jar from the Releases page.

Drop it into your mods folder.

Ensure you have Fabric API installed for version 26.1.

Run /api key on DonutSMP to get your token, then link it with /glaze api.

## Development Status:
This mod is mostly vibe coded and built while learning the Fabric 26.1 API. Expect some chaos, but the prices (usually) work! :)