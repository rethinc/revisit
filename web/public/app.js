(function () {

  const auth = firebase.auth()
  const db = firebase.firestore()

  const txtEmail = document.getElementById('txtEmail')
  const txtPassword = document.getElementById('txtPassword')
  const btnLogin = document.getElementById('btnLogin')
  const btnSignUp = document.getElementById('btnSignUp')
  const btnLogout = document.getElementById('btnLogout')

  btnLogin.addEventListener('click', event => {
    const email = txtEmail.value
    const password = txtPassword.value

    const signInPromise = auth.signInWithEmailAndPassword(email, password)
    signInPromise.catch(e => {
      console.log(e.message)
    })
  })

  btnSignUp.addEventListener('click', event => {
    const email = txtEmail.value
    const password = txtPassword.value

    const signUpPromise = auth.createUserWithEmailAndPassword(email, password)
    signUpPromise.catch(error => {
      console.log(error.message)
    })
  })

  btnLogout.addEventListener('click', event => {
    auth.signOut()
  })

  auth.onAuthStateChanged(firebaseUser => {
    if (firebaseUser) {
      console.log(firebaseUser)
      loadVisits(db, firebaseUser.uid)
      btnLogout.classList.remove('hide')
    } else {
      console.log('not logged in')
      btnLogout.classList.add('hide')
    }
  })

}())

function loadVisits(db, userId) {
  db
    .collection('places')
    .doc(userId)
    .collection('visits')
    .onSnapshot(querySnapshot => {
      let visitsDiv = document.getElementById('visits')
      visitsDiv.innerHTML = ''
      querySnapshot.forEach(doc => {
        let docData = doc.data()
        console.log(doc.id, docData)
        let entry = docData['name'] + ', ' + docData['phone'] + ', ' + docData['visitedAt'] + '<br/>'
        document.getElementById('visits').innerHTML += entry
      })
    }, error => {
      console.log(error.message)
    })
}
