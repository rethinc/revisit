const encryption = require('./encryption.js')
const firebase = require('./revisit.firebase.js')

module.exports.getOrCreate = function (userId) {
  console.log(userId)
  return getSalt(userId)
    .then((salt) => {
      if (salt) {
        return salt
      } else {
        var newSalt = encryption.createSaltBase64()
        return storeSalt(userId, newSalt)
          .then(i => {
            return newSalt
          })
      }
    }).catch(error => {
      console.error(error.message)
    })
}

function getSalt(userId) {
  return firebase.firestore
    .collection('places')
    .doc(userId)
    .get()
    .then((doc) => {
      if (doc.exists) {
        return doc.data()['salt']
      } else {
        return null
      }
    })
    .catch(error => {
      console.error(error.message)
    })
}


function storeSalt(userId, salt) {
  return firebase.firestore
    .collection('places')
    .doc(userId)
    .set({salt: salt}, {merge: true})
    .catch(error => {
      console.error(error.message)
    })
}
