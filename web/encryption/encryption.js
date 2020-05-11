window.decrypt = function(ciphertext, password, saltBase64) {
  var CryptoJS = require("crypto-js");

  let salt = CryptoJS.enc.Base64.parse(saltBase64)

  var key = CryptoJS.PBKDF2(password, salt, {
    keySize: 256 / 32,
    iterations: 12000,
    hasher: CryptoJS.algo.SHA256
  });

  var iv = CryptoJS.enc.Utf8.parse("ZEYcDTT53t55V3e4")

  var bytes  = CryptoJS.AES.decrypt(ciphertext, key, {
    mode: CryptoJS.mode.CBC,
    iv: iv,
    padding: CryptoJS.pad.Pkcs7
  });
  var plaintext = bytes.toString(CryptoJS.enc.Utf8);

  return plaintext
}
