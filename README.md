# Fashigoon

##### Fashigoon is a character customization mod for Severed Chains 3, it supports:
- 5 default attachment slots (weapon, outfit, attachment 1, attachment 2, attachment 3)
- Ability for artists to easily create their own collections and share them
- Support for transparency (limited)

###### Dragoon customizations are not supported at this time!

### Installing Mod:

- Grab the latest release from releases page
- Unpack the release .zip into your Severed Chains mods folder

### Installing Collections:

- Navigate to Severed Chains mods folder
- Find fashigoon/collections folder
- Drop your collection folder in there!

You can find some collections made by me at [here!](https://github.com/avionanx/Icarus-Collections)

### Creating Collections:

Before you read text below, fashigoon has a default **standard** collection which you can inspect on how a collection is configured
in **mods/fashigoon/collections/standard**

A fashigoon collection consists of:
- Translation file **[language].lang** 
- Configuration file **config.csv**
- Numerous 3D assets **[asset name].glb**

In order to position your new asset, you'll need character models.
You can get them easily with [TLoD-Assets-Manager](https://github.com/Legend-of-Dragoon-Modding/TLoD-Assets-Manager)

Fashigoon works with GLB (GLTF Binary) files. This format has support for custom properties, which is used to attach
assets to character models. Supported properties are listed below:
- "attachment" (**REQUIRED**, **integer**) - Specifies which character model part will asset attach to
- "replacement" (Optional, **boolean**) - Whether original model index will be hidden

Make sure to export your GLB file with Custom Properties enabled.

After you export your assets to an empty folder, create **[language].lang** and **config.csv** files in same folder

#### [language].lang format:

[collection name (folder name)].[asset name].name = Asset
[collection name (folder name)].[asset name].description = My new asset 

###### Examples:

- tides.oversized_key.name=Oversized Key
- tides.oversized_key.description=One day, Kongol set this right.
- tides.his_hands.name=His Hands
- tides.his_hands.description=KONGOL NEEDS NO WEAPON

#### config.csv format:

[asset name],[slot name],[character type]

###### Examples:

- cloud,outfit,lod:dart
- pretty_hammer,weapon,lod:meru
- angel_halo,attachment,lod:shana,lod:miranda

After you finish your collection, you can drop it in fashigoon's collections folder!