var firestore = require("firebase/firestore");

module.exports.create = function(name, phone, table, waiter, userId, secretKey) {
  let visit = {
    id: uuidv4(),
    name: encrypt(name, secretKey),
    phone: encrypt(phone, secretKey),
    table: encrypt(table, secretKey),
    waiter: encrypt(waiter, secretKey),
    visitedAt: timestamp.selectedDates[0].getTime()
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
