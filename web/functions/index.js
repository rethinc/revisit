const functions = require('firebase-functions')
const Firestore = require('@google-cloud/firestore')
const PROJECTID = 'gastro-checkin'
const firestore = new Firestore({
  projectId: PROJECTID,
  timestampsInSnapshots: true
})

exports.removeOldVisits  = functions.pubsub
  .schedule('0 0 * * *')
  .timeZone('Europe/Zurich')
  .onRun((context) => {
    return removeVisitsOlderThen14Days();
});

exports.testingRemovals = functions.https.onRequest(async (req, res) => {
  return removeVisitsOlderThen14Days()
    .then(x => {
      res.status(200).send('ok')
    })
    .catch(error => {
      console.error('Could not get visits', error)
      return res.status(500).send({error: 'Could not get snapshot'})
    })

})

function removeVisitsOlderThen14Days() {
  return getVisitsOlderThen14Days()
    .then(visits => {
      var promises = visits.map(visit => {
        return firestore.doc(visit).delete()
      })

      return Promise.all(promises)
        .then(x => { console.log(`Removed ${visits.length} visits`)})
        .catch(error => { console.error('Could not remove visits', error.message)});
    })
}

function getVisitsOlderThen14Days() {
  let now = Date.now()
  let fourteenDays = 12096e5
  let fourteenDaysAgo = now - fourteenDays
  return firestore
    .collectionGroup('visits')
    .where('visitedAt', '<', fourteenDaysAgo)
    .get()
    .then(querySnapshot => {
      var visits = []
      querySnapshot.forEach(function (doc) {
        visits.push(doc.ref.path)
      })
      return visits
    })
}
