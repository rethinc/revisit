const encryption = require("./encryption.js");
const uuid4 = require('uuid4');

module.exports.create = function(
  firestore,
  name,
  zip,
  phone,
  table,
  waiter,
  time,
  userId,
  secretKey
) {
  let visit = {
    id: uuid4(),
    name: encryption.encrypt(name, secretKey),
    zip: encryption.encrypt(zip, secretKey),
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
    );
}

module.exports.delete = function(firestore, visitId, userId) {
  return firestore
    .collection('places')
    .doc(userId)
    .collection('visits')
    .doc(visitId)
    .delete()
    .catch(error =>
      console.error(error.message)
    );
}

module.exports.subscribeToAllVisits = function(firestore, userId, secretKey, handler) {
    return firestore
      .collection('places')
      .doc(userId)
      .collection('visits')
      .onSnapshot(querySnapshot => {
        var visits = [];
        querySnapshot.forEach(doc => {
          visits.push(mapVisit(doc, secretKey));
        });
        handler(visits);
      }, error => {
        console.error(error.message)
      });
}

function mapVisit(doc, secretKey) {
  let docData = doc.data();
  return {
    id: doc.id,
    name: docData.name ? encryption.decrypt(docData.name, secretKey) : '',
    zip: docData.zip ? encryption.decrypt(docData.zip, secretKey) : '',
    phone: docData.phone ? encryption.decrypt(docData.phone, secretKey) : '',
    table: docData.table ? encryption.decrypt(docData.table, secretKey) : '',
    waiter: docData.waiter ? encryption.decrypt(docData.waiter, secretKey) : '',
    visitedAt: docData.visitedAt ? docData.visitedAt : ''
  };
}

