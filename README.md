<br />
<div align="center">
  
<img src="logo3.png" alt="Logo" width="250" height="250">

<h3 align="center">Nasa Gallery</h3>
</div>
<br />

## About The Project

Nasa Gallery is a sample android app to prove new concepts a try 3rd party libraries.
The app consumes [nasa apod api](https://github.com/nasa/apod-api)

### Tech stack

* Entirely written in [Kotlin](https://kotlinlang.org/).
* UI completely written in [Jetpack Compose](https://developer.android.com/jetpack/compose).
* Uses [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html)
  throughout.
* [Retrofit2 & OkHttp3](https://github.com/square/retrofit) to construct the REST APIs and paging
  network data.
* Uses [Koin](https://insert-koin.io/) for dependency injection
* [Coil](https://insert-koin.io/) for loading images from network.
* Navigation is handled by [Voyager](https://github.com/adrielcafe/voyager)

### Screens

The app has two screens. Home screen displays list of image (or video) thumbnails.
When user clicks on any of them the Detail screen is shown, which contains extensive
description of the image.

<p align="middle">
  <img src="/screenshots/home_screen.png" width="370" />
  <img src="/screenshots/detail_screen.png" width="370" />
</p>
