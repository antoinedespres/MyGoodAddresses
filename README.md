# MyGoodAddresses

Keep a list of restaurants you like, with a photo and an address, and see each
one on a map.

Android, Kotlin, Jetpack Compose, Room.

## Setup

The app needs your own **Google Maps SDK for Android** key. Keys are not
committed — they are read from `local.properties`, which is git-ignored.

1. Create a key in the
   [Google Cloud console](https://console.cloud.google.com/apis/credentials),
   with the **Maps SDK for Android** enabled.
2. Restrict it. An unrestricted key is billable by anyone who finds it. For
   Android, restrict by application: the package name
   `com.despreschen.mygoodaddresses` plus your signing certificate's SHA-1
   fingerprint, and restrict the API list to Maps SDK for Android only.
3. Copy `local.properties.example` to `local.properties` — Android Studio
   usually creates the file already, containing just `sdk.dir`.
4. Add your key:

   ```properties
   MAPS_API_KEY=your_api_key_here
   ```

5. Build and run.

The key is injected into the manifest at build time as a placeholder. Without
one the project still builds and every screen works — only the map renders
blank, so you can contribute without needing a key at all.

CI can supply it through a `MAPS_API_KEY` environment variable instead.

> **Client-side keys are extractable.** Injecting the key at build time keeps it
> out of version control, not out of the APK. Anyone with the binary can read
> it, which is why the restrictions in step 2 matter — they are what actually
> limits the damage, not secrecy.

## Data

Everything is stored on the device with Room. There is no server and no
account, so the list does not sync between devices, and uninstalling the app
removes it.

Photos are kept in the app's own storage, which is why no storage permission is
requested.

## Permissions

| Permission | Why |
| --- | --- |
| `INTERNET`, `ACCESS_NETWORK_STATE` | map tiles and geocoding |
| `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION` | the "use my location" button on the add form |

The camera is used through the system camera app, so no camera permission is
needed. Location is optional — addresses can be typed by hand.

## Architecture

Single Activity, Compose, roughly MVVM:

```
com.despreschen.mygoodaddresses
├── data
│   ├── local       Room entity, DAO and database
│   ├── PhotoStorage.kt        photos in app storage
│   └── RestaurantRepository.kt
├── location        device location, and geocoding both directions
├── ui              Compose screens + ViewModels, theme
└── AppContainer.kt hand-written dependency container
```

`AppContainer` is a plain container rather than a DI framework: the app is small
enough to read in one sitting, and it keeps another annotation processor out of
the build.

## Development

```bash
./gradlew assembleDebug     # build
./gradlew testDebugUnitTest # unit tests
./gradlew assembleRelease   # minified release build
```

Requires JDK 21. Kotlin comes from AGP's built-in support, so there is no
`org.jetbrains.kotlin.android` plugin in the build files.
