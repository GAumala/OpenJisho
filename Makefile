all: app/src/main/res/raw/jmdict_e.gz app/src/main/res/raw/radkfile.gz app/src/main/res/raw/kanjidic.gz

app/src/main/res/raw/jmdict_e.gz:
	mkdir -p app/src/main/res/raw
	wget -O "app/src/main/res/raw/jmdict_e.gz" "http://ftp.usf.edu/pub/ftp.monash.edu.au/pub/nihongo/JMdict_e.gz"

app/src/main/res/raw/radkfile.gz:
	mkdir -p app/src/main/res/raw
	wget -O "app/src/main/res/raw/radkfile.gz" "http://ftp.usf.edu/pub/ftp.monash.edu.au/pub/nihongo/radkfile.gz"

app/src/main/res/raw/kanjidic.gz:
	mkdir -p app/src/main/res/raw
	wget -O "app/src/main/res/raw/kanjidic.gz" "http://ftp.usf.edu/pub/ftp.monash.edu.au/pub/nihongo/kanjidic2.xml.gz"


clean:
	rm -f app/src/main/res/raw/jmdict_e.gz app/src/main/res/raw/radkfile.gz app/src/main/res/raw/kanjidic.gz
