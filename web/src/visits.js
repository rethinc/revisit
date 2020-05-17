let firestore = require("firebase/firestore");
let encryption = require("./encryption.js")

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
  return firestore
    .collection('places')
    .doc(userId)
    .collection('visits')
    .doc(visit.id)
    .set(visit)
    .catch(error =>
      console.error(error.message)
    )
}
