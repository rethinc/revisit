(function decrypt() {
  var pbkdf2 = require('pbkdf2')
  var Base64 = require('js-base64').Base64;

  var salt = Base64.decode('vEhJh30gK0GQBGGXzRj8IKqkw9mZaVAcPebuDzY4qJn1EAwywn12NjieScoGd28uCKDNtAybBZ3i5O3NrW+z4mECaNJtalPi2OGkvD0ynjOr0UcLnAZftbW5dR3TzYHVr5A0+YTUy2A0c1bZjd6Z27mmOVq/SO1jGJpYzKqAg6w=');

  var iterations = 12000

  var keyLength = 32

  var hashtype = 'sha256'

  var key = pbkdf2.pbkdf2Sync('test', salt, iterations, keyLength, hashtype)

  console.log(key)

  var aesjs = require('aes-js');

  var iv = aesjs.utils.utf8.toBytes("ZEYcDTT53t55V3e4");
  console.log(iv)

  var aesCbc = new aesjs.ModeOfOperation.cbc(key, iv);

  var encrypted = Base64.decode("iw6WgfTJGC81wIYsQp26FOV/04miNYRo5PSqMFlNOKU=")
  var encryptedArray = aesjs.utils.utf8.toBytes(encrypted)
  var encryptedArray = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]
  console.log(encryptedArray)
  aesCbc.decrypt(encryptedArray)
  //
  // var cleartext = aesjs.utils.utf8.fromBytes(decryptedBytes);
  //
  // console.log(cleartext)
}())
