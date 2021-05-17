#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset

dir=$(cd -P -- "$(dirname -- "$0")" && pwd -P)

main() {
  npm=$(which npm || true)
  if [ -z "${npm:-}" ]; then
    echo  "You need to install Node"
    exit 1
  fi

  browserify=$(which browserify || true)
  if [ -z "${browserify:-}" ]; then
    echo "You need to install browserify run `npm install -g browserify`"
    exit 1
  fi


  echo "firebase"

	firebase=$(which firebase || true)
	if [ -z "${firebase:-}" ]; then
	  echo "You need to install firebase run `npm install -g firebase-tools`"
	  exit 1
  fi

  pushd "${dir}/src/restaurant"
    test -f "firebase.config.js" || {
     echo "Please add ${dir}/src/restaurant/firebase.config.js from rethinc-signing repo."
     exit 1
    }

    ${npm} install
    ${browserify} restaurant.js -o ../../public/js/restaurant.js -t \
    [ babelify --presets [ @babel/preset-env ] --global ]

  popd

  pushd "${dir}/functions"
    ${npm} install
  popd

  ${firebase} login
  ${firebase} deploy
}


main "$@"
