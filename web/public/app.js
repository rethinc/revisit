(function () {

  const auth = firebase.auth()
  const db = firebase.firestore()
  const authUi = new firebaseui.auth.AuthUI(auth)

  const containerSignIn = document.getElementById('containerSignIn')
  const containerVisits = document.getElementById('containerVisits')
  const buttonSignOut = document.getElementById('buttonSignOut')

  var visitsTable = new Tabulator("#containerVisits", {
    layout:"fitColumns",
    index: "id",
    columns: [
      {title: "Name", field: "name"},
      {title: "Telefonnummer", field: "phone"},
      {title: "Datum", field: "visitedAt"}
    ]
  });

  var loadVisitsUnsubscribe = null

  auth.onAuthStateChanged(firebaseUser => {
    if (firebaseUser) {
      console.log(firebaseUser)
      showSignedInView(firebaseUser)
    } else {
      console.log('not logged in')
      showSignInView()
    }
  });

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
          return false;
        }
      }
    });
  }

  function signOut() {
      auth.signOut()
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
        });
        visitsTable.setData(visits);
      }, error => {
        console.log(error.message)
      });
  }

}())
