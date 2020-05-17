
if (!window.firebase) {
  window.firebase = require('firebase/app')
  var firebaseConfig = {
    apiKey: 'AIzaSyAFPTOBgRVaunHBY5tP1wiZbjnwQLC9y_A',
    authDomain: 'gastro-checkin.firebaseapp.com',
    databaseURL: 'https://gastro-checkin.firebaseio.com',
    projectId: 'gastro-checkin',
    storageBucket: 'gastro-checkin.appspot.com',
    messagingSenderId: '1077652043096',
    appId: '1:1077652043096:web:d88109016e93a447e0612d'
  }
  window.firebase.initializeApp(firebaseConfig)
  require('firebase/firestore') // Needed for side effects
  window.firebaseui = require('firebaseui');
}


let auth = window.firebase.auth()

module.exports.firestore = window.firebase.firestore()

module.exports.auth = auth

module.exports.authUi = new window.firebaseui.auth.AuthUI(auth)

module.exports.authContstants = window.firebase.auth
module.exports.authUiConstants = window.firebaseui.auth

