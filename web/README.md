# re:visit

## Setup Firebase CLI And Browserify
```
npm install -g firebase-tools
firebase login
npm install -g browserify
```

## Deploy
```
cd encryption; browserify encryption.js -o ../public/js/encryption.js
firebase deploy
```
