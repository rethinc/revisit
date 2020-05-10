function decrypt(input, key) {
  AES_Init()

  AES_Decrypt(input, key)

  AES_Done()
}

function getKey() {
  const salt = btoa("vEhJh30gK0GQBGGXzRj8IKqkw9mZaVAcPebuDzY4qJn1EAwywn12NjieScoGd28uCKDNtAybBZ3i5O3NrW+z4mECaNJtalPi2OGkvD0ynjOr0UcLnAZftbW5dR3TzYHVr5A0+YTUy2A0c1bZjd6Z27mmOVq/SO1jGJpYzKqAg6w=")
  return pbkdf2("test", salt, 12000, 32, 'sha256')
}

function pbkdf2(password, salt, iterations, len, hashType) {
  hashType = hashType || 'sha1';
  if (!Buffer.isBuffer(password)) {
    password = new Buffer(password);
  }
  if (!Buffer.isBuffer(salt)) {
    salt = new Buffer(salt);
  }
  var out = new Buffer('');
  var md, prev, i, j;
  var num = 0;
  var block = Buffer.concat([salt, new Buffer(4)]);
  while (out.length < len) {
    num++;
    block.writeUInt32BE(num, salt.length);
    prev = crypto.createHmac(hashType, password)
      .update(block)
      .digest();
    md = prev;
    i = 0;
    while (++i < iterations) {
      prev = crypto.createHmac(hashType, password)
        .update(prev)
        .digest();
      j = -1;
      while (++j < prev.length) {
        md[j] ^= prev[j]
      }
    }
    out = Buffer.concat([out, md]);
  }
  return out.slice(0, len);
}

// https://gist.github.com/lihnux/2aa4a6f5a9170974f6aa
function toUTF8Array(str) {
  let utf8 = [];
  for (let i = 0; i < str.length; i++) {
    let charcode = str.charCodeAt(i);
    if (charcode < 0x80) utf8.push(charcode);
    else if (charcode < 0x800) {
      utf8.push(0xc0 | (charcode >> 6),
        0x80 | (charcode & 0x3f));
    }
    else if (charcode < 0xd800 || charcode >= 0xe000) {
      utf8.push(0xe0 | (charcode >> 12),
        0x80 | ((charcode>>6) & 0x3f),
        0x80 | (charcode & 0x3f));
    }
    // surrogate pair
    else {
      i++;
      // UTF-16 encodes 0x10000-0x10FFFF by
      // subtracting 0x10000 and splitting the
      // 20 bits of 0x0-0xFFFFF into two halves
      charcode = 0x10000 + (((charcode & 0x3ff)<<10)
        | (str.charCodeAt(i) & 0x3ff));
      utf8.push(0xf0 | (charcode >>18),
        0x80 | ((charcode>>12) & 0x3f),
        0x80 | ((charcode>>6) & 0x3f),
        0x80 | (charcode & 0x3f));
    }
  }
  return utf8;
}
