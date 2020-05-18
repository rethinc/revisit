  const firebase = require('firebase/app')
  const firebaseConfig = require('./firebase.config.js').firebaseConfig
  firebase.initializeApp(firebaseConfig)
  require('firebase/firestore') // Needed for side effects
  const firebaseui = require('firebaseui')


module.exports.firestore = firebase.firestore()

module.exports.auth = firebase.auth()

module.exports.authUi = new window.firebaseui.auth.AuthUI(firebase.auth())

module.exports.authContstants = firebase.auth
module.exports.authUiConstants = firebaseui.auth

