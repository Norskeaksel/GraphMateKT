#!/usr/bin/env bash
# Updates every version property in pom.xml to the latest stable release on Maven Central.
# Properties and their artifacts are discovered automatically from <version>${property}</version> usages.
#
# Usage:
#   ./scripts/update-dependencies.sh            # update pom.xml
#   ./scripts/update-dependencies.sh --dry-run  # only show what would change
#
# JavaFX N requires JDK N-2 or newer, so JavaFX is capped at major version <jvmTarget> + 2.
# Override with JAVAFX_MAX_MAJOR=<n>, or JAVAFX_MAX_MAJOR= for no cap.

set -euo pipefail
cd "$(dirname "$0")/.."

POM=pom.xml
DRY_RUN=false
[[ "${1:-}" == "--dry-run" ]] && DRY_RUN=true

JVM_TARGET=$(grep -oP '(?<=<jvmTarget>)[^<]+' "$POM" | head -n1 || true)
# Highest allowed major version per property (empty = no limit)
declare -A MAX_MAJOR=([javafx.version]="${JAVAFX_MAX_MAJOR-${JVM_TARGET:+$((JVM_TARGET + 2))}}")

# Prints "property groupId artifactId" for each property used as <version>${property}</version>,
# taking the coordinates of the first dependency/plugin that uses it. XML comments are ignored.
find_version_properties() {
  awk '
    /<!--/ && !/-->/ { incomment = 1; next }
    incomment        { if (/-->/) incomment = 0; next }
    /<dependency>/   { g = ""; a = "" }
    /<plugin>/       { g = "org.apache.maven.plugins"; a = "" }
    match($0, /<groupId>[^<]+<\/groupId>/)       { g = substr($0, RSTART + 9,  RLENGTH - 19) }
    match($0, /<artifactId>[^<]+<\/artifactId>/) { a = substr($0, RSTART + 12, RLENGTH - 25) }
    match($0, /<version>\$\{[^}]+\}<\/version>/) {
      p = substr($0, RSTART + 11, RLENGTH - 22)
      if (g != "" && a != "" && !seen[p]++) print p, g, a
    }
  ' "$POM"
}

# Prints the newest stable version of group:artifact on Maven Central, optionally capped at a major version.
latest_stable() {
  local group="$1" artifact="$2" max_major="$3"
  curl -fsSL "https://repo1.maven.org/maven2/${group//.//}/$artifact/maven-metadata.xml" \
    | grep -oP '(?<=<version>)[^<]+' \
    | grep -viE 'alpha|beta|rc|-m[0-9]|-ea|dev|snapshot|preview' \
    | awk -F. -v max="$max_major" 'max == "" || $1 + 0 <= max + 0' \
    | sort -V | tail -n1
}

mapfile -t entries < <(find_version_properties)
$DRY_RUN || cp "$POM" "$POM.bak"

changes=0
for entry in "${entries[@]}"; do
  read -r prop group artifact <<< "$entry"
  coord="$group:$artifact"

  current=$(grep -oP "(?<=<${prop//./\\.}>)[^<]+" "$POM" | head -n1 || true)
  [[ -z "$current" ]] && continue # not defined in <properties>, e.g. project.version

  latest=$(latest_stable "$group" "$artifact" "${MAX_MAJOR[$prop]:-}" || true)
  if [[ -z "$latest" ]]; then
    printf '%-38s %-55s could not fetch latest version\n' "$prop" "$coord"
  elif [[ "$current" == "$latest" ]]; then
    printf '%-38s %-55s %s (up to date)\n' "$prop" "$coord" "$current"
  else
    printf '%-38s %-55s %s -> %s\n' "$prop" "$coord" "$current" "$latest"
    $DRY_RUN || sed -i "s|<${prop}>[^<]*</${prop}>|<${prop}>${latest}</${prop}>|" "$POM"
    changes=$((changes + 1))
  fi
done

echo
if $DRY_RUN; then
  echo "Dry run: $changes update(s) available. pom.xml was not modified."
elif (( changes > 0 )); then
  echo "Updated $changes version(s) in $POM."
  echo "Run 'mvn clean test' to verify the build."
else
  echo "Everything is already up to date."
fi
