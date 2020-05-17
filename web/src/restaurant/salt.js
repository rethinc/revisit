const encryption = require('./encryption.js')

module.exports.getOrCreate = function (firestore, userId) {
  return getSalt(firestore, userId)
    .then((salt) => {
      if (salt) {
        return salt
      } else {
        var newSalt = encryption.createSaltBase64()
        return storeSalt(firestore, userId, newSalt)
          .then(i => {
            return newSalt
          })
      }
    }).catch(error => {
      console.error(error.message)
    })
}

function getSalt(firestore, userId) {
  return firestore
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


function storeSalt(firestore, userId, salt) {
  return firestore
    .collection('places')
    .doc(userId)
    .set({salt: salt}, {merge: true})
    .catch(error => {
      console.error(error.message)
    })
}
