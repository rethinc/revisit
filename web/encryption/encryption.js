(function () {
  var CryptoJS = require('crypto-js')

  window.decrypt = function (ciphertext, keyBase64) {
    var key = CryptoJS.enc.Base64.parse(keyBase64)

    var tokens = ciphertext.split('#')
    var iv = CryptoJS.enc.Base64.parse(tokens[0])
    var ciphertextData = tokens[1]


    var bytes = CryptoJS.AES.decrypt(ciphertextData, key, {
      mode: CryptoJS.mode.CBC,
      iv: iv,
      padding: CryptoJS.pad.Pkcs7
    })

    return bytes.toString(CryptoJS.enc.Utf8)
  }

  window.encrypt = function (plaintext, keyBase64) {
    var key = CryptoJS.enc.Base64.parse(keyBase64)
    var iv = createInitialVectorBase64()

    var encrypted = CryptoJS.AES.encrypt(plaintext, key, {
      mode: CryptoJS.mode.CBC,
      iv: iv,
      padding: CryptoJS.pad.Pkcs7
    })

    var ivEncoded = CryptoJS.enc.Base64.stringify(iv)
    var ciphertextEncoded = CryptoJS.enc.Base64.stringify(encrypted.ciphertext)
    return ivEncoded + '#' + ciphertextEncoded
  }

  window.deriveKey = function (password, saltBase64) {
    let salt = CryptoJS.enc.Base64.parse(saltBase64)

    let key = CryptoJS.PBKDF2(password, salt, {
      keySize: 256 / 32,
      iterations: 12000,
      hasher: CryptoJS.algo.SHA256
    })
    return CryptoJS.enc.Base64.stringify(key)
  }

  window.createSaltBase64 = function () {
    var random = CryptoJS.lib.WordArray.random(128)
    return CryptoJS.enc.Base64.stringify(random)
  }

  function createInitialVectorBase64() {
    return CryptoJS.lib.WordArray.random(16)
  }
}())


