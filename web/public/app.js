(function () {

  const auth = firebase.auth()
  const db = firebase.firestore()
  const authUi = new firebaseui.auth.AuthUI(auth)

  const containerSignIn = document.getElementById('containerSignIn')
  const containerVisits = document.getElementById('containerVisits')
  const buttonSignOut = document.getElementById('buttonSignOut')

  var currentUser = null

  var currentData = new Set([])

  var deleteIcon = (cell, formatterParams) => {
    return '<svg class="bi bi-trash" width="1em" height="1em" viewBox="0 0 16 16" fill="currentColor" xmlns="http://www.w3.org/2000/svg">\n' +
      '  <path d="M5.5 5.5A.5.5 0 016 6v6a.5.5 0 01-1 0V6a.5.5 0 01.5-.5zm2.5 0a.5.5 0 01.5.5v6a.5.5 0 01-1 0V6a.5.5 0 01.5-.5zm3 .5a.5.5 0 00-1 0v6a.5.5 0 001 0V6z"/>\n' +
      '  <path fill-rule="evenodd" d="M14.5 3a1 1 0 01-1 1H13v9a2 2 0 01-2 2H5a2 2 0 01-2-2V4h-.5a1 1 0 01-1-1V2a1 1 0 011-1H6a1 1 0 011-1h2a1 1 0 011 1h3.5a1 1 0 011 1v1zM4.118 4L4 4.059V13a1 1 0 001 1h6a1 1 0 001-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z" clip-rule="evenodd"/>\n' +
      '</svg>'
  }


  var visitsTable = new Tabulator('#containerVisits', {
    layout: 'fitColumns',
    index: 'id',
    columns: [
      {title: 'Name', field: 'name', editor: 'input'},
      {title: 'Telefonnummer', field: 'phone'},
      {title: 'Datum', field: 'visitedAt'},
      {
        formatter: deleteIcon, width: 44, hozAlign: 'center', cellClick: (e, cell) => {
          if (currentUser) {
            deleteVisit(cell.getRow().getData().id, currentUser.uid)
          }
        }
      }
    ]
  })

  var loadVisitsUnsubscribe = null

  auth.onAuthStateChanged(firebaseUser => {
    if (firebaseUser) {
      console.log(firebaseUser)
      currentUser = firebaseUser
      showSignedInView(firebaseUser)
    } else {
      currentUser = null
      console.log('not logged in')
      showSignInView()
    }
  })

  function showSignedInView(firebaseUser) {
    containerSignIn.classList.add('hide')
    containerSignIn.classList.add('hide')
    containerVisits.classList.remove('hide')
    buttonSignOut.classList.remove('hide')
    buttonSignOut.addEventListener('click', signOut)
    loadVisitsUnsubscribe = loadVisits(db, firebaseUser.uid)
  }

  function showSignInView() {
    containerSignIn.classList.remove('hide')
    containerVisits.classList.add('hide')
    if (loadVisitsUnsubscribe) {
      loadVisitsUnsubscribe()
    }
    buttonSignOut.classList.add('hide')
    buttonSignOut.removeEventListener('click', signOut)
    authUi.start('#containerSignIn', {
      signInOptions: [
        {
          provider: firebase.auth.EmailAuthProvider.PROVIDER_ID,
          requireDisplayName: false
        }
      ],
      credentialHelper: firebaseui.auth.CredentialHelper.NONE,
      callbacks: {
        signInSuccessWithAuthResult: (authResult, redirectUrl) => {
          return false
        }
      }
    })
  }

  function signOut() {
    auth.signOut()
  }

  function deleteVisit(visitId, userId) {

    return db
      .collection('places')
      .doc(userId)
      .collection('visits')
      .doc(visitId)
      .delete()
      .catch(error =>
        console.error(error.message)
      )
  }

  function loadVisits(db, userId) {
    return db
      .collection('places')
      .doc(userId)
      .collection('visits')
      .onSnapshot(querySnapshot => {
        var visits = []
        querySnapshot.forEach(doc => {
          const docData = doc.data()
          visits.push({
            id: doc.id,
            name: docData.name,
            phone: docData.phone,
            visitedAt: docData.visitedAt
          })
        })
        currentData = new Set(visits)
        visitsTable.setData(visits)
      }, error => {
        console.log(error.message)
      })
  }

}())
