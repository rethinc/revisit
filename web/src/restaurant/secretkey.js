module.exports.canBeStored = function () {
  return typeof (Storage) != 'undefined'
}

const localStorage = window.localStorage

module.exports.store = function(secretKey) {
  localStorage.setItem('secretKey', secretKey)
}

module.exports.load = function() {
  return localStorage.getItem('secretKey')
}

module.exports.remove = function() {
  localStorage.removeItem('secretKey')
}
