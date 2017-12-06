# Latest Release
[2017-12-06](https://github.com/cymigodz/Portfolio-Tetris/releases)

# Project
This is the first game of all the others you see in my repository.

I have done Tetris years ago , however i lost the codes and it wasn't fully complete as a game anyway.

The game is pretty simple and i am not going to add any crazy flavor to it, rather treat it like a warm up after serving two years of national service.

# Details
* Platforms: Windows, Android
* Writen in: Java (LibGdx)

# Structure
* Core game logics are under [here](https://github.com/cymigodz/Portfolio-Tetris/tree/master/core/src/com/chunyi/tetris)
* Platform specific launchers at [desktop](https://github.com/cymigodz/Portfolio-Tetris/tree/master/desktop/src/com/chunyi/tetris/desktop) and [android](https://github.com/cymigodz/Portfolio-Tetris/tree/master/android/src/com/chunyi/tetris)
* There is barely any code of mine anywhere else

# Rules
* Following [Super Rotation System](http://tetris.wikia.com/wiki/SRS)

# Logics
* Tetrominoes rotations are currently hard coded in arrays. Reason: I think it might take me more time to think of a "smarter" way than just "stupidly" typing manually.
* Ninepatches for the buttons

# Timeline (when i remember to update)
### 2017-08-30 23:30
* Finished initializing the project. Basically just created the project with libGdx and ensured the build environment is properly configured.
### 2017-08-31 16:40
* Writing the codes for the settings, such as desktop window sizes, saving and retrieving of user settings etc.
### 2017-09-01 05:22
* Finished basic functional main menu
* Already started working on core game.
* Element scaling and positioning was inconsistent for different platforms, tried to improve it, looks okay now, not fully tested yet.
* Need to do a full disposal() check in case for memory leak
* Load time on start up has drastically increased, maybe because of all the excess textures in the assets folder
### 2017-09-02 05:43
* Focusing on tetris core game now, game is still controlled by temp on screen control instead of key input.
### 2017-09-02 07:40
* Bug fixes and more core game functions

# Credits:
* Various Free UI Packs and Fonts by [Kenney.nl](www.kenney.nl)
* [8 Bit Wonder Font by Joiro Hatagaya](http://www.dafont.com/8bit-wonder.font)
* [libGDX](https://libgdx.badlogicgames.com/) by badlogicgames
