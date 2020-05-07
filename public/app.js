(function () {

  const auth = firebase.auth()

  const txtEmail = document.getElementById('txtEmail');
  const txtPassword = document.getElementById('txtPassword');
  const btnLogin = document.getElementById('btnLogin');
  const btnSignUp = document.getElementById('btnSignUp');
  const btnLogout = document.getElementById('btnLogout');

  btnLogin.addEventListener('click', event => {
    const email = txtEmail.value;
    const password = txtPassword.value;

    const signInPromise = auth.signInWithEmailAndPassword(email, password)
    signInPromise.catch(e => {
      console.log(e.message);
    })
  })

  btnSignUp.addEventListener('click', event => {
    const email = txtEmail.value;
    const password = txtPassword.value;

    const signUpPromise = auth.createUserWithEmailAndPassword(email, password)
    signUpPromise.catch(e => {
      console.log(e.message)
    });
  });

  btnLogout.addEventListener('click', event => {
      auth.signOut();
  });

  auth.onAuthStateChanged(firebaseUser => {
    if (firebaseUser) {
      console.log(firebaseUser);
      btnLogout.classList.remove('hide');
    } else {
      console.log('not logged in');
      btnLogout.classList.add('hide');
    }
  });

}());
