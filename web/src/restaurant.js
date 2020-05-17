(function () {
  let encryption = require('./encryption.js')
  let visits = require('./visits.js')

  if (typeof (Storage) == 'undefined') {
    alert('Sorry, dein Browser wird nicht unterstützt.')
    return
  }
  const localstorage = window.localStorage
  const dateTimeFormat = 'd.m.Y H:i'

  const auth = firebase.auth()
  const db = firebase.firestore()
  const authUi = new firebaseui.auth.AuthUI(auth)

  const containerSignIn = document.getElementById('containerSignIn')
  const containerSignedIn = document.getElementById('containerSignedIn')
  const containerSecretKey = document.getElementById('containerSecretKey')
  const buttonSignOut = document.getElementById('buttonSignOut')
  const textName = document.getElementById('textName')
  const textPhone = document.getElementById('textPhone')
  const textTable = document.getElementById('textTable')
  const textWaiter = document.getElementById('textWaiter')
  var timestamp = flatpickr('#textTimestamp', {
    dateFormat: dateTimeFormat,
    enableTime: true,
    time_24hr: true,
    defaultDate: new Date(Date.now())
  })
  const buttonCreate = document.getElementById('buttonCreate')
  const buttonSecretKey = document.getElementById('buttonSecretKey')
  const textSecretKey = document.getElementById('textSecretKey')
  const buttonSignOutSecret = document.getElementById('buttonSignOutSecret')
  const messageSecretError = document.getElementById('messageSecretError')

  var currentUser = null

  const deleteIcon = (cell, formatterParams) => {
    return '<svg class="bi bi-trash" width="1em" height="1em" viewBox="0 0 16 16" fill="currentColor" xmlns="http://www.w3.org/2000/svg">\n' +
      '  <path d="M5.5 5.5A.5.5 0 016 6v6a.5.5 0 01-1 0V6a.5.5 0 01.5-.5zm2.5 0a.5.5 0 01.5.5v6a.5.5 0 01-1 0V6a.5.5 0 01.5-.5zm3 .5a.5.5 0 00-1 0v6a.5.5 0 001 0V6z"/>\n' +
      '  <path fill-rule="evenodd" d="M14.5 3a1 1 0 01-1 1H13v9a2 2 0 01-2 2H5a2 2 0 01-2-2V4h-.5a1 1 0 01-1-1V2a1 1 0 011-1H6a1 1 0 011-1h2a1 1 0 011 1h3.5a1 1 0 011 1v1zM4.118 4L4 4.059V13a1 1 0 001 1h6a1 1 0 001-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z" clip-rule="evenodd"/>\n' +
      '</svg>'
  }

  var visitsTable = new Tabulator('#containerVisits', {
    layout: 'fitColumns',
    index: 'id',
    columns: [
      {
        title: 'Name',
        field: 'name',
        headerFilter: 'input',
        headerFilterPlaceholder: 'Nach Namen filtern'
      },
      {
        title: 'Telefonnummer',
        field: 'phone'
      },
      {
        title: 'Datum',
        field: 'visitedAt',
        formatter: (cell, formatterParams) => {
          return new Date(cell.getValue()).format(dateTimeFormat)
        },
        headerFilter: 'input',
        headerFilterPlaceholder: 'Nach Datum filtern'
      },
      {title: 'Tisch', field: 'table'},
      {title: 'Servicekraft', field: 'waiter'},
      {
        formatter: deleteIcon,
        width: 44,
        hozAlign: 'center',
        cellClick: (e, cell) => {
          if (currentUser) {
            visits.delete(cell.getRow().getData().id, currentUser.uid)
          }
        },
        headerSort: false
      },
    ],
    initialSort:[
      {
        column: 'visitedAt',
        dir: 'desc'
      }
    ]
  })

  var loadVisitsUnsubscribe = null

  auth.onAuthStateChanged(firebaseUser => {
    if (firebaseUser) {
      currentUser = firebaseUser
      let secretKey = getSecretKey()
      if (secretKey) {
        showSignedInView(firebaseUser)
      } else {
        showPasswordView(firebaseUser)
      }
    } else {
      currentUser = null
      showSignInView()
    }
  })

  function showPasswordView(firebaseUser) {
    containerSecretKey.classList.remove('hide')
    containerSignIn.classList.add('hide')
    containerSignedIn.classList.add('hide')

    buttonSignOutSecret.addEventListener('click', signOut)

    buttonSecretKey.addEventListener('click', e => {
      messageSecretError.classList.add('hide')
      getOrCreateSalt(db, firebaseUser.uid)
        .then(salt => {
          let password = textSecretKey.value
          if (!password || password === '') {
            messageSecretError.classList.remove('hide')
          }
          let secretKey = encryption.deriveKey(password, salt)
          isSecretKeyValid(db, firebaseUser.uid, secretKey)
            .then(isValid => {
              if (!isValid) {
                messageSecretError.classList.remove('hide')
              } else {
                saveSecretKey(secretKey)
                showSignedInView(firebaseUser)
              }
            })
        })
        .catch(error => {
          console.error(error)
          messageSecretError.classList.remove('hide')
        })
    })
  }

  function showSignedInView(firebaseUser) {
    containerSignedIn.classList.remove('hide')
    containerSignIn.classList.add('hide')
    containerSecretKey.classList.add('hide')
    buttonCreate.addEventListener('click', createVisitFromForm)
    buttonSignOut.addEventListener('click', signOut)
    loadVisitsUnsubscribe = visits
      .subscribeToAllVisits(firebaseUser.uid, getSecretKey(), visits => {
        visitsTable.setData(visits)
      });
  }

  function showSignInView() {
    containerSignIn.classList.remove('hide')
    containerSignedIn.classList.add('hide')
    containerSecretKey.classList.add('hide')
    buttonCreate.removeEventListener('click', createVisitFromForm)
    if (loadVisitsUnsubscribe) {
      loadVisitsUnsubscribe()
    }
    buttonSignOut.removeEventListener('click', signOut)
    buttonCreate.removeEventListener('click', signOut)
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

  function createVisitFromForm() {
    if (currentUser && textName.value && textPhone.value) {
      visits.create(
        textName.value,
        textPhone.value,
        textTable.value,
        textWaiter.value,
        timestamp.selectedDates[0].getTime(),
        currentUser.uid,
        getSecretKey()
      )
        .then(_ => {
          textPhone.value = ''
          textName.value = ''
          textTable.value = ''
          textWaiter.value = ''
        })
    }
  }

  function signOut() {
    removeSecretKey()
    auth.signOut()
  }

  function getOrCreateSalt(db, userId) {
    return getSalt(db, userId)
      .then((salt) => {
        if (salt) {
          return salt
        } else {
          var newSalt = encryption.createSaltBase64()
          return storeSalt(db, userId, newSalt)
            .then(i => {
              return newSalt
            })
        }
      }).catch(error => {
          console.error('Could not get or create salt', error)
        }
      )
  }

  function getSalt(db, userId) {
    return db
      .collection('places')
      .doc(userId)
      .get()
      .then((doc) => {
        if (doc.exists) {
          return doc.data()['salt']
        } else {
          return null
        }
      })
      .catch(error => {
        console.error('Could not get salt', error)
      })
  }

  function storeSalt(db, userId, salt) {
    return db
      .collection('places')
      .doc(userId)
      .set({salt: salt}, {merge: true})
      .catch(error => {
        console.error('Could not store salt', error)
      })
  }

  function isSecretKeyValid(db, userId, password, salt) {
    let challenge = 'This is the challenge!'
    return db
      .collection('places')
      .doc(userId)
      .get()
      .then(doc => {
        let storedChallenge = doc.data()['challenge']
        if (storedChallenge) {
          let decrypted = decrypt(storedChallenge, password, salt)
          return decrypted === challenge
        } else {
          return saveEncryptedChallenge(db, userId, password, salt, challenge)
            .then(e => {
              return true
            })
            .catch(error => {
              console.error('Could not store encrypted Challenge', error)
            })
        }
      }).catch(error => {
        console.error('Could not get challenge', error)
      })
  }

  function saveEncryptedChallenge(db, userId, password, salt, challenge) {
    let encryptedChallenge = encryption.encrypt(challenge, password, salt)
    return db
      .collection('places')
      .doc(userId)
      .set({challenge: encryptedChallenge}, {merge: true})
  }

  function saveSecretKey(secretKey) {
    localstorage.setItem('secretKey', secretKey)
  }

  function getSecretKey() {
    return localstorage.getItem('secretKey')
  }

  function removeSecretKey() {
    localstorage.removeItem('secretKey')
  }
}())
