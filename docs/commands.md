```sh
bundle exec jekyll clean
bundle exec jekyll build
bundle exec jekyll serve



nl -ba "01-Leetcode-150/07-Stack/0020-valid-parentheses.md" | sed -n '138,150p'


```


```sh

# Find deprecated Sass functions
grep -RnE 'darken\(|lighten\(|map-get\(' \
  assets/css _sass 2>/dev/null

# Find deprecated Sass imports
grep -Rn '@import' \
  assets/css _sass 2>/dev/null


bundle show just-the-docs

THEME_PATH="$(bundle show just-the-docs)"

grep -RnE 'darken\(|lighten\(|map-get\(' \
  "$THEME_PATH/_sass"

bundle list | grep -E 'jekyll|sass|just-the-docs'

grep -n -A10 -B2 '^sass:' _config.yml

bundle update jekyll-sass-converter

bundle list | grep jekyll-sass-converter

```


```sh

rm -f Gemfile.lock

bundle install

bundle exec jekyll build

git add Gemfile Gemfile.lock .github/workflows/

git commit -m "Fix Jekyll dependencies and GitHub Pages deployment"

git push origin main

ls -la .github/workflows/

cat .github/workflows/jekyll.yml


find . -iname "dynamic-arrays.png" \
  -not -path "./.git/*" \
  -not -path "./_site/*"

ls -l "00-Data-Structure/Dynamic-Arrays/dynamic-arrays.png"

find 00-Data-Structure -type f

```

#### PNG issue

```sh
find 00-Data-Structure -type f -iname "*.png" | sort


find . -type f -iname "*.png" \
  -not -path "./.git/*" \
  -not -path "./_site/*" \
  -not -path "./vendor/*" | sort



find 00-Data-Structure -type f -iname "*.png" | sort | while read -r file; do
  echo "![${file##*/}]({{ '/${file}' | relative_url }})"
done
```