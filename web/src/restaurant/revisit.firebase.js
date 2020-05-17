  const firebase = require('firebase/app')
  const firebaseConfig = {
    apiKey: 'AIzaSyAFPTOBgRVaunHBY5tP1wiZbjnwQLC9y_A',
    authDomain: 'gastro-checkin.firebaseapp.com',
    databaseURL: 'https://gastro-checkin.firebaseio.com',
    projectId: 'gastro-checkin',
    storageBucket: 'gastro-checkin.appspot.com',
    messagingSenderId: '1077652043096',
    appId: '1:1077652043096:web:d88109016e93a447e0612d'
  }
  firebase.initializeApp(firebaseConfig)
  require('firebase/firestore') // Needed for side effects
  const firebaseui = require('firebaseui')


module.exports.firestore = firebase.firestore()

module.exports.auth = firebase.auth()

module.exports.authUi = new window.firebaseui.auth.AuthUI(firebase.auth())

module.exports.authContstants = firebase.auth
module.exports.authUiConstants = firebaseui.auth

