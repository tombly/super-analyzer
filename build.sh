mkdir -p build/classes
find src -name '*.java' -print0 | xargs -0 javac -encoding UTF-8 -d build/classes -cp 'libs/*'
mkdir -p build/classes/media
rsync -a src/media/ build/classes/media/
cp src/*.properties build/classes/
