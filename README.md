# OpenJisho

Japanese dictionary Android app that uses 
[JMdict](http://www.edrdg.org/wiki/index.php/JMdict-EDICT_Dictionary_Project), 
[RADKFILE](http://www.edrdg.org/krad/kradinf.html), 
[KANJIDIC](http://www.edrdg.org/wiki/index.php/KANJIDIC_Project) and 
[Tatoeba](https://tatoeba.org/eng/downloads) to search word defintions and 
example sentences. All queries are done offline. Dictionary files are 
downloaded during first time setup.

<p align="center">
  <img src="https://user-images.githubusercontent.com/5729175/121984920-7265f500-cd59-11eb-92ea-9418487ade3e.png" />
</p>

## Development

Use a recent version of Android Studio. I'm currently using Arctic Fox, but
any 4.0+ version may work.

Before building the app you need to get some dictionary files that are not 
included in this repository. Just run `make` at the project root and the 
files should download to the resources directory.

## LICENSE

OpenJisho by Gabriel Aumala is licensed under GPL-3.

```
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

This project also includes a copy of the library 
[FuriganaView](https://github.com/sh0/furigana-view) distributed under 
CC BY-SA 3.0.

Dictionary files are used in accordance with each copyright holder's license.

Illustrations provided by Ambar Troya, all rights reserved. The illustrations 
cannot be reproduced in any form, without permission. 
[LEARN MORE](https://www.artstation.com/ambartroya)
