const encryption = require("./encryption.js");
const firebase = require("./revisit.firebase.js")

module.exports.create = function(
  name,
  phone,
  table,
  waiter,
  time,
  userId,
  secretKey
) {
  let visit = {
    id: uuidv4(),
    name: encryption.encrypt(name, secretKey),
    phone: encryption.encrypt(phone, secretKey),
    table: encryption.encrypt(table, secretKey),
    waiter: encryption.encrypt(waiter, secretKey),
    visitedAt: time
  }
  return firebase.firestore
    .collection('places')
    .doc(userId)
    .collection('visits')
    .doc(visit.id)
    .set(visit)
    .catch(error =>
      console.error(error.message)
    );
}

module.exports.delete = function(visitId, userId) {
  firebase.firestore
    .collection('places')
    .doc(userId)
    .collection('visits')
    .doc(visitId)
    .delete()
    .catch(error =>
      console.error(error.message)
    );
}
