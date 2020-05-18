#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

dir=$(cd -P -- "$(dirname -- "$0")" && pwd -P)

main() {

  npm=$(which npm)
  if [ -z "${npm:-}" ]; then
    echo  "You need to install Node"
    exit 1
  fi

	browserify=$(which browserify)
	if [ -z "${browserify:-}" ]; then
	  echo "You need to install browserify run `npm install -g browserify`"
	  exit 1
  fi

	firebase=$(which firebase)
	if [ -z "${firebase:-}" ]; then
	  echo "You need to install firebase run `npm install -g firebase-tools`"
	  exit 1
  fi

  pushd "${dir}/src/restaurant"
    ${npm} install
    ${browserify} restaurant.js -o ../../public/js/restaurant.js
  popd

  pushd "${dir}/functions"
    ${npm} install
  popd

  ${firebase} login
  ${firebase} deploy
}

main "$@"
