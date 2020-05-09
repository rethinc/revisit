(function () {

  const auth = firebase.auth()
  const db = firebase.firestore()
  const authUi = new firebaseui.auth.AuthUI(auth)

  const containerSignIn = document.getElementById('containerSignIn')
  const containerVisits = document.getElementById('containerVisits')
  const buttonSignOut = document.getElementById('buttonSignOut')

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
    loadVisits(db, firebaseUser.uid)
  }

  function showSignInView() {
    containerSignIn.classList.remove('hide')
    containerVisits.classList.add('hide')
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
    db
      .collection('places')
      .doc(userId)
      .collection('visits')
      .onSnapshot(querySnapshot => {
        containerVisits.innerHTML = ''
        querySnapshot.forEach(doc => {
          let docData = doc.data()
          containerVisits.innerHTML += docData['name'] + ', ' + docData['phone'] + ', ' + docData['visitedAt'] + '<br/>'
        })
      }, error => {
        console.log(error.message)
      })
  }

}())
