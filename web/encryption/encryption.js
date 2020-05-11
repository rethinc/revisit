(function() {
  var CryptoJS = require('crypto-js')
  var ivText = 'ZEYcDTT53t55V3e4'
  window.decrypt = function (ciphertext, password, saltBase64) {
    var key = deriveKey(password, saltBase64)

    var iv = CryptoJS.enc.Utf8.parse(ivText)

    var bytes = CryptoJS.AES.decrypt(ciphertext, key, {
      mode: CryptoJS.mode.CBC,
      iv: iv,
      padding: CryptoJS.pad.Pkcs7
    })

    return bytes.toString(CryptoJS.enc.Utf8)
  }

  window.encrypt = function (plaintext, password, saltBase64) {
    var key = deriveKey(password, saltBase64)

    var iv = CryptoJS.enc.Utf8.parse(ivText)

    var encrypted = CryptoJS.AES.encrypt(plaintext, key, {
      mode: CryptoJS.mode.CBC,
      iv: iv,
      padding: CryptoJS.pad.Pkcs7
    });

    return CryptoJS.enc.Base64.stringify(encrypted.ciphertext)
  }

  function deriveKey(password, saltBase64) {
    let salt = CryptoJS.enc.Base64.parse(saltBase64)

    return CryptoJS.PBKDF2(password, salt, {
      keySize: 256 / 32,
      iterations: 12000,
      hasher: CryptoJS.algo.SHA256
    });
  }

  window.createSaltBase64 = function () {
    var random = CryptoJS.lib.WordArray.random(128)
    return CryptoJS.enc.Base64.stringify(random)
  }
}())


