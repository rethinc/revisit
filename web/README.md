# re:visit

## Setup Firebase CLI And Browserify
```
npm install -g firebase-tools
firebase login
npm install -g browserify
```

## Deploy
```
cd src/restaurant; npm install; browserify restaurant.js -o ../../public/js/restaurant.js
firebase deploy
```
