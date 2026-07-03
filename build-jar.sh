#!/bin/sh
set -e

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

mkdir -p build/classes build/jar
find src -name '*.java' -print0 | xargs -0 javac -encoding UTF-8 -d build/classes -cp 'libs/*'
mkdir -p build/classes/media
rsync -a src/media/ build/classes/media/
cp src/*.properties build/classes/

rm -rf build/jar/staging
mkdir -p build/jar/staging

for jar in libs/*.jar; do
  (cd build/jar/staging && jar xf "$ROOT/$jar")
done

cp -R build/classes/. build/jar/staging/
rm -rf build/jar/staging/META-INF/*.SF build/jar/staging/META-INF/*.DSA build/jar/staging/META-INF/*.RSA

cat > build/jar/MANIFEST.MF <<'EOF'
Manifest-Version: 1.0
Main-Class: net.nosleep.superanalyzer.Main

EOF

jar cfm build/super-analyzer.jar build/jar/MANIFEST.MF -C build/jar/staging .

echo "Built build/super-analyzer.jar"
echo "Run with: java -jar build/super-analyzer.jar"
