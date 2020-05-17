let firebase = require('firebase');
require("firebase/firestore"); // Needed for side effects

var firebaseConfig = {
  apiKey: 'AIzaSyAFPTOBgRVaunHBY5tP1wiZbjnwQLC9y_A',
  authDomain: 'gastro-checkin.firebaseapp.com',
  databaseURL: 'https://gastro-checkin.firebaseio.com',
  projectId: 'gastro-checkin',
  storageBucket: 'gastro-checkin.appspot.com',
  messagingSenderId: '1077652043096',
  appId: '1:1077652043096:web:d88109016e93a447e0612d'
}

firebase.initializeApp(firebaseConfig)

module.exports.firestore = firebase.firestore();
