const encryption = require('./encryption.js')

module.exports.challengeSecretKey = function(firestore, userId, secretKey) {
  let challenge = 'This is the challenge!'
  return firestore
    .collection('places')
    .doc(userId)
    .get()
    .then(doc => {
      let storedChallenge = doc.data()['challenge']
      if (storedChallenge) {
        let decrypted = encryption.decrypt(storedChallenge, secretKey)
        return decrypted === challenge
      } else {
        return saveEncryptedChallenge(firestore, userId, challenge, secretKey)
          .then(_ => {
            return true
          })
      }
    }).catch(error => {
      console.error(error.message)
    })
}

function saveEncryptedChallenge(firestore, userId, challenge, secretKey) {
  let encryptedChallenge = encryption.encrypt(challenge, secretKey)
  return firestore
    .collection('places')
    .doc(userId)
    .set({challenge: encryptedChallenge}, {merge: true})
}
